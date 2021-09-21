package com.mrshiehx.mclx.modules.account.skin;

import com.mrshiehx.mclx.settings.Settings;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;
import static com.mrshiehx.mclx.settings.Settings.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SkinChanger {
    public static void start(Component frame) {
            File file = selectSkin(frame,getString("DIALOG_CHANGE_SKIN_FILE_TITLE"));
            if (file != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(configContent);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if(jsonObject.optInt("lm")!=1)return;
                String uuid = jsonObject.optString("uu");
                String accessToken = jsonObject.optString("at");
                if (!isEmpty(uuid) && !isEmpty(accessToken)) {
                    try {
                        URL url = new URL("https://api.mojang.com/user/profile/" + uuid + "/skin");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setUseCaches(false);
                        connection.setConnectTimeout(15000);
                        connection.setReadTimeout(15000);
                        connection.setRequestMethod("PUT");
                        connection.setRequestProperty("Authorization", jsonObject.optString("tt")+" " + accessToken);
                        connection.setRequestProperty("Accept", "*/*");
                        String boundary = "~~~~~~~~~~~~~~~~~~~~~~~~~";
                        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                        connection.setDoOutput(true);

                        String var1 = "";
                        int var2 = file.getName().lastIndexOf("\\.");
                        if (var2 != -1) {
                            var1 = "/" + file.getName().substring(var2 + 1);
                        }

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        byte[] sl = "\r\n".getBytes(UTF_8);
                        byteArrayOutputStream.write(("--" + boundary).getBytes(UTF_8));
                        byteArrayOutputStream.write(sl);

                        byteArrayOutputStream.write("Content-Disposition: form-data; name=\"model\"".getBytes(UTF_8));
                        byteArrayOutputStream.write(sl);
                        byteArrayOutputStream.write(sl);
                        String[] models = new String[]{"Steve", "Alex (slim)"};

                        String modelx = (String) JOptionPane.showInputDialog(
                                frame,
                                getString("DIALOG_CHANGE_SKIN_SELECT_MODEL_TITLE"),
                                getString("DIALOG_TITLE_NOTICE"),
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                models,
                                models[0]
                        );

                        String model = "";
                        if (!isEmpty(modelx) && modelx.equals(models[1])) model = "slim";

                        byteArrayOutputStream.write(model.getBytes(UTF_8));
                        byteArrayOutputStream.write(sl);

                        byteArrayOutputStream.write(("--" + boundary).getBytes(UTF_8));
                        byteArrayOutputStream.write(sl);
                        byteArrayOutputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").getBytes(UTF_8));
                        byteArrayOutputStream.write(sl);
                        byteArrayOutputStream.write(("Content-Type: image" + var1).getBytes(UTF_8));
                        byteArrayOutputStream.write(sl);
                        byteArrayOutputStream.write(sl);
                        if (!file.exists()) file.createNewFile();
                        FileInputStream f=new FileInputStream(file);
                        copyTo(f, byteArrayOutputStream, new byte[8192]);
                        f.close();
                        byteArrayOutputStream.write(sl);
                        byteArrayOutputStream.write(("--" + boundary + "--").getBytes(UTF_8));
                        connection.setRequestProperty("Content-Length", String.valueOf(byteArrayOutputStream.size()));
                        OutputStream outputStream = connection.getOutputStream();

                        outputStream.write(byteArrayOutputStream.toByteArray());
                        byteArrayOutputStream.close();

                        ////stem.ut.println(byteArrayOutputStream.toString());

                        String var = httpURLConnection2String(connection);

                        if (!isEmpty(var) && var.startsWith("{")) {
                            JSONObject result = new JSONObject(var);
                            JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result.optString("error"), result.optString("errorMessage")), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, getString("DIALOG_CHANGED_SKIN_FILE_TEXT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);
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
