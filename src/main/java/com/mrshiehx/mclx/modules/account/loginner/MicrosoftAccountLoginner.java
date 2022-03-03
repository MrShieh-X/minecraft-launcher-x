package com.mrshiehx.mclx.modules.account.loginner;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.microsoft.MicrosoftAuthenticationServer;
import com.mrshiehx.mclx.settings.Settings;
import com.mrshiehx.mclx.utils.Utils;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static com.mrshiehx.mclx.MinecraftLauncherX.CLIENT_ID;
import static com.mrshiehx.mclx.MinecraftLauncherX.getString;
import static com.mrshiehx.mclx.utils.Utils.post;
import static com.mrshiehx.mclx.settings.Settings.*;

public class MicrosoftAccountLoginner {
    public static void loginMicrosoftAccount(Settings frame, JSONObject jsonObject, String successText) {
        JOptionPane op = new JOptionPane(getString("LOGIN_MICROSOFT_WAIT_FOR_RESPONSE"), JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION);
        final JDialog dialog = op.createDialog(frame, getString("DIALOG_TITLE_NOTICE"));


        //new Thread(()->{
        MicrosoftAuthenticationServer server = null;
        MicrosoftAuthenticationServer.OnGotCode o = new MicrosoftAuthenticationServer.OnGotCode() {
            @Override
            public void onGotCode(String code, String rUrl) {
                if (dialog.isVisible()) {
                    //stem.ut.println(code);
                    try {
                        String secret = System.currentTimeMillis() + UUID.randomUUID().toString();
                        String ACCESS_TOKEN_URL = "https://login.live.com/oauth20_token.srf" +
                                "?grant_type=authorization_code" +
                                "&scope=XboxLive.signin+offline_access" +
                                "&client_secret=" + secret +
                                "&client_id=" + CLIENT_ID +
                                "&redirect_uri=" + rUrl + "" +
                                "&code=" + code;
                        String secondString = ("client_id=" + CLIENT_ID +
                                "&grant_type=authorization_code" +
                                "&scope=XboxLive.signin+offline_access" +
                                "&client_id=" + CLIENT_ID +
                                "&redirect_uri=" + rUrl + "" +
                                "&code=" + code);
                        String first = post(ACCESS_TOKEN_URL, secondString, "application/x-www-form-urlencoded", null);
                        JSONObject result = Utils.parseJSONObject(first);
                        if (result != null) {
                            if (result.has("error") || result.has("error_description")) {
                                String var = result.optString("error_description");
                                JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result.optString("error"), var), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                String tokenType = result.optString("token_type");
                                //String scope=result.optString("scope");
                                //String access_token=result.optString("access_token");
                                String refresh_token = result.optString("refresh_token");
                                //String user_id=result.optString("user_id");
                                //long expires_in=result.optLong("expires_in");

                                String secondFirst = "https://login.live.com/oauth20_token.srf" +
                                        "?client_id=" + CLIENT_ID +
                                        "&client_secret=" + secret +
                                        "&refresh_token=" + refresh_token +
                                        "&grant_type=refresh_token" +
                                        "&redirect_uri=" + rUrl;
                                String secondSecond =
                                        "client_id=" + CLIENT_ID +
                                                "&refresh_token=" + refresh_token +
                                                "&grant_type=refresh_token" +
                                                "&redirect_uri=" + rUrl;
                                String second = post(secondFirst, secondSecond, "application/x-www-form-urlencoded", null);
                                JSONObject result2 = Utils.parseJSONObject(second);


                                if (result2 != null) {
                                    if (result2.has("error") || result2.has("error_description")) {
                                        String var = result2.optString("error_description");
                                        JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result2.optString("error"), var), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        String xboxLive = post("https://user.auth.xboxlive.com/user/authenticate", "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=" + result2.optString("access_token") + "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}", "application/json", "application/json");

                                        JSONObject xboxLiveFirst = Utils.parseJSONObject(xboxLive);
                                        if (xboxLiveFirst != null && !xboxLiveFirst.has("error")) {
                                            String Token = xboxLiveFirst.optString("Token");
                                            String uhs = "";
                                            JSONObject DisplayClaims = xboxLiveFirst.optJSONObject("DisplayClaims");
                                            if (DisplayClaims != null) {
                                                JSONArray xui = DisplayClaims.optJSONArray("xui");
                                                if (xui != null && xui.length() > 0) {
                                                    JSONObject firsta = xui.optJSONObject(0);
                                                    if (firsta != null) uhs = firsta.optString("uhs");
                                                }
                                            }

                                            JSONObject xstsResult = Utils.parseJSONObject(post("https://xsts.auth.xboxlive.com/xsts/authorize", "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" + Token + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}", "application/json", "application/json"));

                                            if (xstsResult != null && !xstsResult.has("error")) {
                                                String xstsToken = xstsResult.optString("Token");
                                                JSONObject mcFirst = Utils.parseJSONObject(post("https://api.minecraftservices.com/authentication/login_with_xbox", "{\"identityToken\":\"XBL3.0 x=" + uhs + ";" + xstsToken + "\"}", "application/json", "application/json"));

                                                if (mcFirst != null && !mcFirst.has("error")) {
                                                    String access_token = mcFirst.optString("access_token");
                                                    JSONObject mcSecond = Utils.parseJSONObject(Utils.get("https://api.minecraftservices.com/minecraft/profile", tokenType, access_token));

                                                    if (mcSecond != null) {

                                                        //stem.ut.println("microsoft_first : " + first);
                                                        //stem.ut.println("microsoft_second: " + second);
                                                        //stem.ut.println("xboxLive first  : " + xboxLive);
                                                        //stem.ut.println("xstsResult first: " + xstsResult);
                                                        //stem.ut.println("mc         first: " + mcFirst);
                                                        //stem.ut.println("mc        second: " + mcSecond);
                                                        if (mcSecond.has("error") || mcSecond.has("errorMessage")) {
                                                            String var = mcSecond.optString("errorMessage");
                                                            JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), mcSecond.optString("error"), var), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                                                        } else {
                                                            jsonObject.put("loginMethod", 2);
                                                            jsonObject.put("accessToken", access_token);
                                                            jsonObject.put("tokenType", tokenType);
                                                            jsonObject.put("uuid", mcSecond.optString("id"));
                                                            jsonObject.put("playerName", mcSecond.optString("name"));
                                                            MinecraftLauncherX.configContent = jsonObject.toString();
                                                            try {
                                                                FileWriter writer = new FileWriter(configFile, false);
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
                                                            dialog.setVisible(false);
                                                            dialog.dispose();
                                                            JOptionPane.showMessageDialog(frame, successText, getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);

                                                            return;
                                                        }
                                                    } else {
                                                        //stem.ut.println(849);
                                                        JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                                    }


                                                } else {
                                                    //stem.ut.println(855);
                                                    JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_MESSAGE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                                }
                                            } else {
                                                //stem.ut.println(859);
                                                JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                            }

                                        } else {
                                            //stem.ut.println(864);
                                            JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                } else {
                                    //stem.ut.println(869);
                                    JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            //stem.ut.println(874);
                            JOptionPane.showMessageDialog(frame, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        //stem.ut.println(878);
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(frame, e, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                    }
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        };
        try {
            server = getServer(o);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        if (server != null) {
            String url = "";
            try {
                url = "https://login.live.com/oauth20_authorize.srf" +
                        "?client_id=" + CLIENT_ID +
                        "&response_type=code" +
                        "&scope=XboxLive.signin+offline_access" +
                        "&prompt=select_account" +
                        "&redirect_uri=" + (server.getRedirectURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.openLink(url);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            //dialog.setAlwaysOnTop(true);
            //dialog.setModal(false);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(frame, getString("DIALOG_UNABLE_TO_LOGIN_MICROSOFT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
        }
        //}).start();

    }

    private static MicrosoftAuthenticationServer getServer(MicrosoftAuthenticationServer.OnGotCode o) throws Exception {
        MicrosoftAuthenticationServer server = null;
        Exception exception = null;
        for (int port : new int[]{29116, 29117, 29118, 29119, 29120, 29121, 29122, 29123, 29124, 29125, 29126}) {
            ////stem.ut.println(port);
            try {
                server = new MicrosoftAuthenticationServer(port, o);
                server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
                return server;
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }
        }
        throw exception;
    }
}
