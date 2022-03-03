package com.mrshiehx.mclx.modules.account.loginner;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.settings.Settings;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mrshiehx.mclx.MinecraftLauncherX.getString;
import static com.mrshiehx.mclx.MinecraftLauncherX.isEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;

public class MojangAccountLoginner {
    public static void loginMojangAccount(Settings frame, String account, boolean haveADialogToGetAccount, JSONObject jsonObject, String successText) {
        if (haveADialogToGetAccount || account == null) account = JOptionPane.showInputDialog(
                frame,
                getString("DIALOG_OFFICIAL_LOGIN_TIP_ENTER_ACCOUNT_TEXT"),
                getString("SETTINGS_BUTTON_OFFICIAL_LOGIN_TEXT"),
                JOptionPane.QUESTION_MESSAGE
        );
        if (!isEmpty(account)) {
            String password = JOptionPane.showInputDialog(
                    frame,
                    getString("DIALOG_OFFICIAL_LOGIN_TIP_ENTER_PASSWORD_TEXT"),
                    getString("SETTINGS_BUTTON_OFFICIAL_LOGIN_TEXT"),
                    JOptionPane.QUESTION_MESSAGE
            );
            if (!isEmpty(password)) {
                try {
                    URL url = new URL("https://authserver.mojang.com/authenticate");

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("requestUser", true);
                    jsonObject1.put("username", account);
                    jsonObject1.put("password", password);
                    JSONObject agentJo = new JSONObject();
                    agentJo.put("name", "Minecraft");
                    agentJo.put("version", "1");
                    jsonObject1.put("agent", agentJo);

                    String post = jsonObject1.toString();

                    byte[] bytes = post.getBytes(UTF_8);


                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setUseCaches(false);
                    con.setConnectTimeout(15000);
                    con.setReadTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                    try (OutputStream os = con.getOutputStream()) {
                        os.write(bytes);
                    }

                    JSONObject result = null;
                    try {
                        result = new JSONObject(Settings.httpURLConnection2String(con));
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                    if (result != null) {
                        if (result.has("error") || result.has("errorMessage")) {
                            String var = result.optString("errorMessage");
                            if (var.equals("Invalid credentials. Invalid username or password.")) {
                                JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_ERROR_INVALID_AOP_TEXT"), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result.optString("error"), var), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JSONObject selectedProfileJo = result.optJSONObject("selectedProfile");
                            JSONObject userJo = result.optJSONObject("user");
                            if (userJo != null && !isEmpty(userJo.optString("username"))) {
                                account = userJo.optString("username");
                            }
                            jsonObject.put("loginMethod", 1);
                            jsonObject.put("ea", account);
                            jsonObject.put("accessToken", result.optString("accessToken"));
                            jsonObject.put("uuid", selectedProfileJo.optString("id"));
                            jsonObject.put("playerName", selectedProfileJo.optString("name"));
                            MinecraftLauncherX.configContent = jsonObject.toString();
                            try {
                                FileWriter writer = new FileWriter(Settings.configFile, false);
                                writer.write(jsonObject.toString(Settings.INDENT_FACTOR));
                                writer.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            //((JLabel) playerName).setText(selectedProfileJo.optString("name"));
                            frame.loadAccount(jsonObject, false);

                            frame.remove(frame.playerName);
                            frame.playerName = new JLabel();
                            frame.playerName.setBounds(140, 15, 135, 25);
                            frame.add(frame.playerName);
                            ((JLabel) frame.playerName).setText(jsonObject.optString("playerName"));
                            JOptionPane.showMessageDialog(frame, successText, getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);

                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_NORESPONSE_TEXT"), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);

                }
            }
        }
    }
}
