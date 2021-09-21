package com.mrshiehx.mclx.modules.version;

import com.mrshiehx.mclx.dialog.UnExitableDialog;
import com.mrshiehx.mclx.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;

public class VersionInstaller {
    public static void showDialog(Frame frame){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkNetwork()) {
                    File mclx = new File("mclx");
                    File versionsFile = new File(mclx, "versions.json");
                    mclx.mkdirs();
                    try {
                        if (versionsFile.exists()) versionsFile.delete();
                        versionsFile.createNewFile();
                        downloadFile("https://launchermeta.mojang.com/mc/game/version_manifest.json", versionsFile);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(frame, getString("MESSAGE_FAILED_TO_DOWNLOAD_VERSIONS_FILE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        JSONObject headVersions = new JSONObject(readFileContent(versionsFile));
                        JSONArray versions = headVersions.optJSONArray("versions");
                        if (versions != null) {
                            List<String> urls = new ArrayList<>();
                            List<String> ids = new ArrayList<>();
                            for (int i = 0; i < versions.length(); i++) {
                                JSONObject jsonObject = versions.optJSONObject(i);
                                if (jsonObject != null) {
                                    urls.add(jsonObject.optString("url", "0"));
                                    ids.add(jsonObject.optString("id", "0"));
                                }
                            }

                            Object[] selectionValues = ids.toArray();

                            Object inputContent = JOptionPane.showInputDialog(
                                    frame,
                                    getString("MESSAGE_CHOOSE_A_VERSION"),
                                    getString("MENU_INSTALL_NEW_VERSION"),
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    selectionValues,
                                    selectionValues[0]
                            );
                            if (inputContent != null) {
                                //System.out.println(urls.get(ids.indexOf(inputContent)));
                                String jsonFileURL = urls.get(ids.indexOf(inputContent));

                                String name = showInputNameDialog(frame, (String) inputContent, getString("MESSAGE_INSTALL_INPUT_NAME"));

                                if (!isEmpty(name)) {
                                    File versionDir = new File(versionsDir, name);
                                    versionDir.mkdirs();
                                    File jsonFile = new File(versionDir, name + ".json");
                                    File jarFile = new File(versionDir, name + ".jar");
                                    if (jsonFile.exists()) jsonFile.delete();
                                    try {
                                        jsonFile.createNewFile();
                                        jarFile.createNewFile();
                                        downloadFile(jsonFileURL, jsonFile);
                                        JSONObject headVersionFile = new JSONObject(readFileContent(jsonFile));
                                        JSONObject downloadsJo = headVersionFile.optJSONObject("downloads");
                                        JSONObject clientJo = downloadsJo != null ? downloadsJo.optJSONObject("client") : null;
                                        if (downloadsJo != null) {
                                            if (clientJo != null) {
                                                String url = clientJo.optString("url");
                                                if (!isEmpty(url)) {
                                                    UnExitableDialog dialog = new UnExitableDialog(frame, getString("MENU_INSTALL_NEW_VERSION"), true);
                                                    dialog.setSize(250, 160);
                                                    dialog.setResizable(false);
                                                    dialog.setLayout(null);
                                                    dialog.setLocationRelativeTo(frame);
                                                    JProgressBar progressBar = new JProgressBar();
                                                    progressBar.setBounds(10, 10, 215, 20);

                                                    progressBar.setOrientation(JProgressBar.HORIZONTAL);
                                                    //progressBar.setMinimum(0);

                                                    //progressBar.setMaximum(30);
                                                    //progressBar.setValue(29);
                                                    progressBar.setStringPainted(true);
                                                    //progressBar.addChangeListener(this);
                                                    //progressBar.setPreferredSize(new Dimension(300,20));
                                                    progressBar.setBorderPainted(true);
                                                    //progressBar.setBackground(Color.pink);


                                                    JTextArea textArea = new JTextArea();
                                                    textArea.setBounds(10, 40, 215, 70);
                                                    textArea.setEditable(false);
                                                    textArea.setFont(new Font(null, Font.PLAIN, 12));
                                                    textArea.setLineWrap(true);
                                                    textArea.setWrapStyleWord(true);
                                                    JScrollPane jsp = new JScrollPane(textArea);
                                                    jsp.setBounds(10, 40, 215, 70);
                                                    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                                                    dialog.add(progressBar);
                                                    dialog.add(jsp);

                                                    dialog.addComponentListener(new ComponentAdapter() {
                                                        @Override
                                                        public void componentShown(ComponentEvent e) {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADING_JAR_FILE"));
                                                                        downloadFile(url, jarFile, progressBar);
                                                                        addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADED_JAR_FILE"));
                                                                        progressBar.setValue(0);
                                                                        addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADING_ASSETS"));
                                                                        //downloadFile(url, jarFile, progressBar);
                                                                        File librariesDir = new File(gameDir, "libraries");
                                                                        File assetsDir = new File(gameDir, "assets");
                                                                        File indexesDir = new File(assetsDir, "indexes");
                                                                        File objectsDir = new File(assetsDir, "objects");
                                                                        File nativesDir = new File(versionDir, "natives");
                                                                        File tempNatives = new File(mclx, "temp_natives");
                                                                        tempNatives.mkdirs();
                                                                        librariesDir.mkdirs();
                                                                        assetsDir.mkdirs();
                                                                        indexesDir.mkdirs();
                                                                        objectsDir.mkdirs();
                                                                        nativesDir.mkdirs();
                                                                        String assetsIndex = headVersionFile.optString("assets");
                                                                        if (!isEmpty(assetsIndex)) {
                                                                            File assetsIndexFile = new File(indexesDir, assetsIndex + ".json");
                                                                            JSONObject assetIndexObject = headVersionFile.optJSONObject("assetIndex");
                                                                            String assetIndexUrl = assetIndexObject != null ? assetIndexObject.optString("url") : null;

                                                                            if (!isEmpty(assetIndexUrl)) {
                                                                                try {
                                                                                    downloadFile(assetIndexUrl, assetsIndexFile, progressBar);
                                                                                    progressBar.setValue(0);
                                                                                    JSONObject assetsJo = new JSONObject(readFileContent(assetsIndexFile));
                                                                                    JSONObject objectsJo = assetsJo.optJSONObject("objects");

                                                                                    Map<String, Object> map = objectsJo.toMap();
                                                                                    List<String> nameList = new ArrayList<>(map.keySet());
                                                                                    JSONArray names = new JSONArray(nameList);
                                                                                    JSONArray objectsJa = objectsJo.toJSONArray(names);
                                                                                    for (int i = 0; i < objectsJa.length(); i++) {
                                                                                        JSONObject object = objectsJa.optJSONObject(i);
                                                                                        if (object != null) {
                                                                                            String hash = object.optString("hash");
                                                                                            try {
                                                                                                if (!isEmpty(hash)) {
                                                                                                    File file;
                                                                                                    if (!assetsIndex.equals("legacy")) {
                                                                                                        File dir = new File(objectsDir, hash.substring(0, 2));
                                                                                                        dir.mkdirs();
                                                                                                        file = new File(dir, hash);
                                                                                                    } else {
                                                                                                        file = new File(assetsDir, "virtual" + getFileSeparator(assetsDir.getAbsolutePath()) + "legacy" + getFileSeparator(assetsDir.getAbsolutePath()) + nameList.get(i));
                                                                                                        file.getParentFile().mkdirs();
                                                                                                    }
                                                                                                            /*File dir = new File(!assetsIndex.equals("legacy")?objectsDir:new File(assetsDir,"virtual"+getFileSeparator(assetsDir.getAbsolutePath())+"legacy"), !assetsIndex.equals("legacy")?hash.substring(0, 2):nameList.get(i));

                                                                                                            File file = !assetsIndex.equals("legacy")?new File(dir, hash):new File();
                                                                                                            file.getParentFile().mkdirs();*/


                                                                                                    if (!file.exists()) {
                                                                                                        file.createNewFile();
                                                                                                        downloadFile("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash, file, progressBar);
                                                                                                    }
                                                                                                }
                                                                                            } catch (Exception e) {
                                                                                                e.printStackTrace();
                                                                                                addLog(textArea, String.format(getString("MESSAGE_FAILED_DOWNLOAD_FILE"), hash));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADED_ASSETS"));
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                    addLog(textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"), e));
                                                                                }
                                                                            } else {
                                                                                addLog(textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"), getString("MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL")));
                                                                                //textArea.setText(textArea.getText()+String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"),getString("MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL"))+"\n");
                                                                            }
                                                                        } else {
                                                                            //textArea.setText(textArea.getText()+getString("MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX""))+"\n");
                                                                            addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX"));
                                                                        }


                                                                        addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"));
                                                                        try {
                                                                            JSONArray librariesJa = headVersionFile.optJSONArray("libraries");
                                                                            if (librariesJa != null) {
                                                                                List<String>nativesNames=new ArrayList<>();
                                                                                for (int i = 0; i < librariesJa.length(); i++) {
                                                                                    JSONObject jsonObject = librariesJa.optJSONObject(i);
                                                                                    if (jsonObject != null) {
                                                                                        JSONObject downloadsJo = jsonObject.optJSONObject("downloads");
                                                                                        if (downloadsJo != null) {
                                                                                            JSONObject artifactJo = downloadsJo.optJSONObject("artifact");
                                                                                            if (artifactJo != null) {
                                                                                                String path = artifactJo.optString("path");
                                                                                                String url = artifactJo.optString("url");
                                                                                                if (!isEmpty(path) && !isEmpty(url)) {
                                                                                                    try {
                                                                                                        File file = new File(librariesDir, path);
                                                                                                        file.getParentFile().mkdirs();
                                                                                                        if (!file.exists()) {
                                                                                                            file.createNewFile();
                                                                                                        }
                                                                                                        if (file.length() == 0) {
                                                                                                            downloadFile(url, file, progressBar);
                                                                                                        }
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                        addLog(textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARY"), url, e));
                                                                                                    }
                                                                                                }
                                                                                            }

                                                                                            JSONObject classifiersJo = downloadsJo.optJSONObject("classifiers");
                                                                                            if (classifiersJo != null) {
                                                                                                String osName = System.getProperty("os.name");
                                                                                                JSONObject nativesJo = null;
                                                                                                if (osName.toLowerCase().contains("windows")) {
                                                                                                    nativesJo = classifiersJo.optJSONObject("natives-windows");
                                                                                                }else if (osName.toLowerCase().contains("mac")) {
                                                                                                    nativesJo = classifiersJo.optJSONObject("natives-macos");
                                                                                                } else {
                                                                                                    nativesJo = classifiersJo.optJSONObject("natives-linux");
                                                                                                }


                                                                                                if (nativesJo != null) {
                                                                                                    String name = Utils.getNativeLibraryName(nativesJo.optString("path"));
                                                                                                    //System.out.println(Arrays.toString(nativesNames.toArray())+","+name );
                                                                                                    if (!nativesNames.contains(name)) {
                                                                                                        String url = nativesJo.optString("url");
                                                                                                        try {
                                                                                                            if (!isEmpty(url)) {
                                                                                                                File nativeFile = new File(tempNatives, url.substring(url.lastIndexOf("/") + 1));
                                                                                                                //if(!nativeFile.exists()) {
                                                                                                                nativeFile.createNewFile();
                                                                                                                downloadFile(url, nativeFile, progressBar);
                                                                                                                //}
                                                                                                            }
                                                                                                        } catch (Exception e) {
                                                                                                            e.printStackTrace();
                                                                                                            addLog(textArea, String.format(getString("MESSAGE_FAILED_DOWNLOAD_FILE"), url));
                                                                                                        }
                                                                                                        nativesNames.add(name);
                                                                                                    }
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                }
                                                                                addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"));
                                                                            } else {
                                                                                addLog(textArea, getString("MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"));
                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                            addLog(textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES"), e));
                                                                        }


                                                                        File[] natives = tempNatives.listFiles((dir, name1) -> name1.endsWith(".jar"));
                                                                        if (natives != null && natives.length != 0) {
                                                                            addLog(textArea, getString("MESSAGE_INSTALL_DECOMPRESSING_LIBRARIES"));
                                                                            for (File file : natives) {
                                                                                try {
                                                                                    File dir = new File(tempNatives, file.getName().substring(0, file.getName().lastIndexOf(".")));
                                                                                    dir.mkdirs();
                                                                                    unZip(file, dir, progressBar);
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                    addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE"), file.getAbsolutePath(), e));
                                                                                }
                                                                            }
                                                                        }

                                                                                /*File[]lwjglDirs=tempNatives.listFiles(new FilenameFilter() {
                                                                                    @Override
                                                                                    public boolean accept(File dir, String name) {
                                                                                        return dir.isDirectory()&&(
                                                                                                name.startsWith("lwjgl-0")||
                                                                                                name.startsWith("lwjgl-1")||
                                                                                                name.startsWith("lwjgl-2")||
                                                                                                name.startsWith("lwjgl-3")||
                                                                                                name.startsWith("lwjgl-4")||
                                                                                                name.startsWith("lwjgl-5")||
                                                                                                name.startsWith("lwjgl-6")||
                                                                                                name.startsWith("lwjgl-7")||
                                                                                                name.startsWith("lwjgl-8")||
                                                                                                name.startsWith("lwjgl-9"));
                                                                                    }
                                                                                });

                                                                                List<File>var=new ArrayList<>();
                                                                                for(File file:lwjglDirs){
                                                                                    if(file!=null&&file.isDirectory()){
                                                                                        var.add(file);
                                                                                    }
                                                                                }
                                                                                File var1=var.get(var.size()-1);
                                                                                String var2="META-INF";
                                                                                File var3=new File(var1,var2);
                                                                                if(var3.exists()){
                                                                                    try{
                                                                                        copyDirectory(var3,nativesDir.getAbsolutePath(),var2);
                                                                                    }catch (IOException e){
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }*/

                                                                        List<File> libFiles = new ArrayList<>();
                                                                        String houzhui = ".so";

                                                                        String osName = System.getProperty("os.name");

                                                                        if (osName.toLowerCase().contains("windows")) {
                                                                            houzhui = ".dll";
                                                                        } else if (osName.toLowerCase().contains("mac")) {
                                                                            houzhui = ".dylib";
                                                                        }

                                                                        File[] var4 = tempNatives.listFiles(new FilenameFilter() {
                                                                            @Override
                                                                            public boolean accept(File dir, String name) {
                                                                                return dir.exists() && dir.isDirectory();
                                                                            }
                                                                        });

                                                                        for (File file : var4) {
                                                                            if (file != null && file.isDirectory()) {
                                                                                String finalHouzhui = houzhui;
                                                                                File[] files = file.listFiles(new FilenameFilter() {
                                                                                    @Override
                                                                                    public boolean accept(File dir, String name) {
                                                                                        return name.toLowerCase().endsWith(finalHouzhui);
                                                                                    }
                                                                                });
                                                                                libFiles.addAll(Arrays.asList(files));
                                                                            }
                                                                        }

                                                                        for (File file : libFiles) {
                                                                            File to = new File(nativesDir, file.getName());
                                                                            try {
                                                                                Utils.copyFile(file, to);
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                                addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_COPY_FILE"), file.getAbsolutePath(), to.getAbsolutePath(), e));
                                                                            }
                                                                        }

                                                                        Utils.deleteDirectory(tempNatives);
                                                                        addLog(textArea, getString("MESSAGE_INSTALL_DECOMPRESSED_LIBRARIES"));
                                                                        addLog(textArea, getString("MESSAGE_INSTALLED_NEW_VERSION"));
                                                                    } catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                        addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_INSTALL_NEW_VERSION, e")));
                                                                        JOptionPane.showMessageDialog(frame, String.format(getString("MESSAGE_FAILED_TO_INSTALL_NEW_VERSION"), e), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                                                    }
                                                                    progressBar.setValue(progressBar.getMaximum());
                                                                    dialog.setExitable(true);
                                                                    //dialog.dispose();
                                                                }
                                                            }).start();
                                                        }
                                                    });

                                                    dialog.setVisible(true);


                                                } else {
                                                    JOptionPane.showMessageDialog(frame, (getString("MESSAGE_INSTALL_JAR_FILE_DOWNLOAD_URL_EMPTY")), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(frame, (getString("MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO")), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(frame, (getString("MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO")), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        JOptionPane.showMessageDialog(frame, String.format(getString("MESSAGE_FAILED_TO_CONTROL_VERSION_JSON_FILE"), e), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                    }
                                    updateVersions();
                                }


                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, (getString("MESSAGE_VERSIONS_LIST_IS_EMPTY")), getString("DIALOG_TITLE_NOTICE"), JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(frame, String.format(getString("MESSAGE_FAILED_TO_PARSE_VERSIONS_FILE"), e), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, getString("MESSAGE_FAILED_TO_CONNECT_TO_LAUNCHERMETA"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

    private static boolean checkNetwork() {
        try {
            URL url = new URL("https://launchermeta.mojang.com");
            try {
                URLConnection co = url.openConnection();
                co.setConnectTimeout(12000);
                co.connect();
                return true;
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        } catch (MalformedURLException ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

}
