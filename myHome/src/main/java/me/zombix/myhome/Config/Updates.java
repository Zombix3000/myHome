package me.zombix.myhome.Config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updates {

    private final String pluginName;
    private final String currentVersion;
    private final String repoOwner;
    private final String repoName;

    public Updates(String pluginName, String currentVersion, String repoOwner, String repoName) {
        this.pluginName = pluginName;
        this.currentVersion = currentVersion;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    public boolean checkForUpdates() {
        try {
            String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", repoOwner, repoName);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                JsonObject jsonObject = JsonParser.parseReader(new java.io.InputStreamReader(inputStream)).getAsJsonObject();
                String latestVersion = jsonObject.get("tag_name").getAsString();

                return !latestVersion.equals(currentVersion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getLatestVersion() {
        try {
            String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", repoOwner, repoName);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                JsonObject jsonObject = JsonParser.parseReader(new java.io.InputStreamReader(inputStream)).getAsJsonObject();
                return jsonObject.get("tag_name").getAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePlugin() {
        try {
            String latestVersion = getLatestVersion();
            if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                String downloadUrl = String.format("https://github.com/%s/%s/releases/latest/download/%s.jar", repoOwner, repoName, pluginName + "-" + latestVersion.replace("v", ""));
                URL url = new URL(downloadUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream("plugins/" + pluginName + ".jar")) {

                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
