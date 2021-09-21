package com.mrshiehx.mclx.modules.account.skin;

import com.mrshiehx.mclx.settings.Settings;
import com.mrshiehx.mclx.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

import java.awt.*;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;
import static com.mrshiehx.mclx.MinecraftLauncherX.getString;

public class SkinResetter {
    public static void showDialog(Component frame){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(configContent);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        if (jsonObject.optInt("lm") == 1) {
            String uuid = jsonObject.optString("uu");
            if (!isEmpty(uuid)) {
                String url = "https://api.mojang.com/user/profile/" + uuid + "/skin";
                try {
                    JSONObject result = Utils.parseJSONObject(Utils.delete(url, jsonObject.optString("tt"), jsonObject.optString("at")));
                    if (result != null && result.has("error")) {
                        JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result.optString("error"), result.optString("errorMessage")), getString("DIALOG_TITLE_FAILED_RESET_SKIN"), JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, getString("DIALOG_TEXT_RESET_SKIN"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ioException, getString("DIALOG_TITLE_FAILED_RESET_SKIN"), JOptionPane.ERROR_MESSAGE);

                }
            } else {
                errorDialog(frame, getString("MESSAGE_UUID_ACCESSTOKEN_EMPTY"), null);
            }
        }
    }
}
