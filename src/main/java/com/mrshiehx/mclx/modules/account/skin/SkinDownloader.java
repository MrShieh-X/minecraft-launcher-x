package com.mrshiehx.mclx.modules.account.skin;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.settings.Settings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;

public class SkinDownloader {
    public static void start(Component frame){
        File file = Settings.selectSkin(frame,getString("DIALOG_DOWNLOAD_SKIN_FILE_TITLE"));
        if (file != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(configContent);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            String uuid = jsonObject.optString("uu", "0");
            if (!isEmpty(uuid)) {
                try {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                    JSONObject result = new JSONObject(Settings.httpURLConnection2String((HttpURLConnection) url.openConnection()));
                    JSONArray properties = result.getJSONArray("properties");
                    for (int i = 0; i < properties.length(); i++) {
                        JSONObject jsonObject1 = properties.optJSONObject(i);
                        if (jsonObject1 != null) {
                            if (jsonObject1.optString("name").equals("textures")) {
                                JSONObject jsonObject2 = new JSONObject(new String(Base64.getDecoder().decode(jsonObject1.optString("value"))));
                                JSONObject var = jsonObject2.optJSONObject("textures");
                                if (var.has("SKIN")) {
                                    MinecraftLauncherX.downloadFile(var.optJSONObject("SKIN").optString("url"), file);
                                    JOptionPane.showMessageDialog(frame, getString("DIALOG_DOWNLOADED_SKIN_FILE_TEXT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(frame, getString("DIALOG_DOWNLOAD_SKIN_FILE_NOT_SET_TEXT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.WARNING_MESSAGE);
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    errorDialog(frame, exception.toString(), null);
                }
            } else {
                errorDialog(frame, getString("MESSAGE_UUID_ACCESSTOKEN_EMPTY"), null);
            }
        }
    }
}
