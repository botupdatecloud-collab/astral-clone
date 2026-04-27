package com.astralcore.multispace;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ════════════════════════════════════════════════
 *   ASTRAL CLONE — GitHub Auto Update Checker
 *   by Team Astral Core
 * ════════════════════════════════════════════════
 *
 *  GitHub Release ekak hadanakota:
 *  1. github.com/nima-axis/astral-clone → Releases → New Release
 *  2. Tag: v1.0.1  (versionCode 1 wata vada lokui wenna  ↑)
 *  3. APK file attach karanna (AstralClone-v1.0.1.apk)
 *  4. Publish Release
 *
 *  App open wena hadama meka automatically check karanawa!
 */
public class UpdateChecker {

    // ⚠️ oya GitHub username + repo name denna
    private static final String GITHUB_OWNER = "nima-axis";
    private static final String GITHUB_REPO  = "astral-clone";
    private static final String API_URL =
            "https://api.github.com/repos/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";

    private final Activity activity;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateChecker(Activity activity) {
        this.activity = activity;
    }

    /** App start wena hadama call karanna */
    public void checkForUpdate() {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Accept", "application/vnd.github.v3+json")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful() || response.body() == null) return;

                String json = response.body().string();
                JSONObject release = new JSONObject(json);

                // GitHub release tag: "v1.0.2" → parse → 102
                String tagName     = release.getString("tag_name");          // e.g. "v1.0.2"
                String releaseNote = release.getString("body");              // changelog
                String releaseName = release.getString("name");              // release title
                boolean isPreRelease = release.getBoolean("prerelease");

                if (isPreRelease) return; // beta skip

                // versionCode calculate karanna tag ekata
                int latestVersionCode = parseVersionCode(tagName);
                int currentVersionCode = BuildConfig.VERSION_CODE;

                if (latestVersionCode > currentVersionCode) {
                    // APK download URL find karanna assets eken
                    JSONArray assets = release.getJSONArray("assets");
                    String apkUrl = null;
                    for (int i = 0; i < assets.length(); i++) {
                        JSONObject asset = assets.getJSONObject(i);
                        String name = asset.getString("name");
                        if (name.endsWith(".apk")) {
                            apkUrl = asset.getString("browser_download_url");
                            break;
                        }
                    }

                    final String finalApkUrl = apkUrl;
                    final String finalTag    = tagName;
                    final String finalNotes  = releaseNote;

                    mainHandler.post(() ->
                            showUpdateDialog(releaseName, finalTag, finalNotes, finalApkUrl));
                }

            } catch (Exception e) {
                // Network error — silently ignore
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * "v1.0.2" → 102,  "v2.1.0" → 210
     * versionCode ekak generate karanne mehethin
     */
    private int parseVersionCode(String tag) {
        try {
            String clean = tag.replace("v", "").replace("V", "").trim();
            String[] parts = clean.split("\\.");
            int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            return major * 10000 + minor * 100 + patch;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Update dialog show karanna */
    private void showUpdateDialog(String name, String tag, String notes, String apkUrl) {
        if (activity.isFinishing() || activity.isDestroyed()) return;

        String message = "✨ " + name + "\n\n" +
                "📦 Version: " + tag + "\n\n" +
                "📝 What's new:\n" + (notes.isEmpty() ? "Bug fixes & improvements" : notes);

        new AlertDialog.Builder(activity, R.style.UpdateDialogStyle)
                .setTitle("🚀 Update Available!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Update Now", (d, w) -> {
                    if (apkUrl != null) downloadApk(apkUrl, tag);
                })
                .setNegativeButton("Later", null)
                .show();
    }

    /** DownloadManager use karala APK download + install */
    private void downloadApk(String url, String version) {
        String fileName = "AstralClone-" + version + ".apk";

        DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle("Astral Clone " + version)
                .setDescription("Downloading update...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(activity,
                        Environment.DIRECTORY_DOWNLOADS, fileName)
                .setMimeType("application/vnd.android.package-archive");

        long downloadId = dm.enqueue(request);

        // Download finish wena hadama install karanna
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    installApk(fileName);
                    activity.unregisterReceiver(this);
                }
            }
        };

        activity.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /** Download wuna APK install karanna */
    private void installApk(String fileName) {
        File apkFile = new File(
                activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        if (!apkFile.exists()) return;

        Uri apkUri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                apkFile);

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(install);
    }
}
