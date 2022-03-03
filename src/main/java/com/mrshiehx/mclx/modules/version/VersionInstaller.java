package com.mrshiehx.mclx.modules.version;

import com.mrshiehx.mclx.dialog.UnExitableDialog;
import com.mrshiehx.mclx.modules.MinecraftLauncher;
import com.mrshiehx.mclx.utils.OperatingSystem;
import com.mrshiehx.mclx.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;
import static com.mrshiehx.mclx.utils.DownloadDialog.*;

public class VersionInstaller {
    public static void showDialog(Frame frame) {
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
                        JSONObject headVersions = new JSONObject(Utils.readFileContent(versionsFile));
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
                                        JSONObject headVersionFile = new JSONObject(Utils.readFileContent(jsonFile));
                                        JSONObject downloadsJo = headVersionFile.optJSONObject("downloads");
                                        JSONObject clientJo = downloadsJo != null ? downloadsJo.optJSONObject("client") : null;
                                        if (downloadsJo != null) {
                                            if (clientJo != null) {
                                                String url = clientJo.optString("url");
                                                if (!isEmpty(url)) {
                                                    JProgressBar progressBar = createProgressBar();
                                                    JTextArea textArea = createTextArea();
                                                    UnExitableDialog dialog = createDownloadDialog(frame, progressBar, textArea);

                                                    dialog.addComponentListener(new ComponentAdapter() {
                                                        @Override
                                                        public void componentShown(ComponentEvent e) {
                                                            new Thread(() -> {
                                                                try {
                                                                    String text = getString("MESSAGE_INSTALL_DOWNLOADING_JAR_FILE");

                                                                    addLog(textArea, text);
                                                                    downloadFile(url, jarFile, progressBar);
                                                                    //print(VersionInstaller.class,getString("MESSAGE_INSTALL_DOWNLOADED_JAR_FILE"));
                                                                    addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADED_JAR_FILE"));
                                                                    progressBar.setValue(0);
                                                                    //print(VersionInstaller.class,getString("MESSAGE_INSTALL_DOWNLOADING_ASSETS"));
                                                                    addLog(textArea, getString("MESSAGE_INSTALL_DOWNLOADING_ASSETS"));
                                                                    //downloadFile(url, jarFile, progressBar);
                                                                    File librariesDir = new File(gameDir, "libraries");
                                                                    File assetsDir = /*new File(gameDir, "assets")*/Utils.getAssets(Utils.getConfig().optString("assetsPath"), gameDir);
                                                                    //System.out.println(assetsDir.getAbsolutePath());
                                                                    //System.out.println(assetsDir.getAbsolutePath());
                                                                    File indexesDir = new File(assetsDir, "indexes");
                                                                    File objectsDir = new File(assetsDir, "objects");
                                                                    File nativesDir = new File(versionDir, Utils.getNativesDirName());
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
                                                                                JSONObject assetsJo = new JSONObject(Utils.readFileContent(assetsIndexFile));
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
                                                                                                    file = new File(assetsDir, "virtual/legacy/" + nameList.get(i));
                                                                                                    file.getParentFile().mkdirs();
                                                                                                }
                                                                                                        /*File dir = new File(!assetsIndex.equals("legacy")?objectsDir:new File(assetsDir,"virtual"+getFileSeparator(assetsDir.getAbsolutePath())+"legacy"), !assetsIndex.equals("legacy")?hash.substring(0, 2):nameList.get(i));

                                                                                                        File file = !assetsIndex.equals("legacy")?new File(dir, hash):new File();
                                                                                                        file.getParentFile().mkdirs();*/


                                                                                                if (!file.exists()) {
                                                                                                    file.createNewFile();
                                                                                                    //String text2=String.format(getString("MESSAGE_DOWNLOADING_FILE"),hash);
                                                                                                    //addLog(textArea, text2);
                                                                                                    downloadFile("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash, file, progressBar);
                                                                                                    System.out.println(i + "/" + objectsJa.length());
                                                                                                }
                                                                                            }
                                                                                        } catch (Exception e1) {
                                                                                            e1.printStackTrace();
                                                                                            String textx = String.format(getString("MESSAGE_FAILED_DOWNLOAD_FILE"), hash);
                                                                                            //print(VersionInstaller.class,textx);
                                                                                            addLog(textArea, textx);
                                                                                        }
                                                                                    }
                                                                                }
                                                                                String textx = getString("MESSAGE_INSTALL_DOWNLOADED_ASSETS");
                                                                                //print(VersionInstaller.class,textx);
                                                                                addLog(textArea, textx);
                                                                            } catch (Exception e1) {
                                                                                e1.printStackTrace();
                                                                                String textx = String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"), e1);
                                                                                //print(VersionInstaller.class,textx);
                                                                                addLog(textArea, textx);
                                                                            }
                                                                        } else {
                                                                            String textx = String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"), getString("MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL"));
                                                                            //print(VersionInstaller.class,textx);
                                                                            addLog(textArea, textx);
                                                                            //textArea.setText(textArea.getText()+String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"),getString("MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL"))+"\n");
                                                                        }
                                                                    } else {
                                                                        String textx = getString("MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX");
                                                                        //print(VersionInstaller.class,textx);
                                                                        //textArea.setText(textArea.getText()+getString("MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX""))+"\n");
                                                                        addLog(textArea, textx);
                                                                    }


                                                                    String textx = getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES");
                                                                    //print(VersionInstaller.class,textx);
                                                                    addLog(textArea, textx);
                                                                    try {
                                                                        JSONArray librariesJa = headVersionFile.optJSONArray("libraries");
                                                                        if (librariesJa != null) {
                                                                            List<String> nativesNames = new ArrayList<>();
                                                                            for (int i = 0; i < librariesJa.length(); i++) {
                                                                                JSONObject jsonObject = librariesJa.optJSONObject(i);
                                                                                if (jsonObject != null) {
                                                                                    boolean meet = true;
                                                                                    JSONArray rules = jsonObject.optJSONArray("rules");
                                                                                    if (rules != null) {
                                                                                        meet = MinecraftLauncher.isMeetConditions(rules, false, false);
                                                                                    }
                                                                                    //System.out.println(meet);

                                                                                    JSONObject downloadsJo1 = jsonObject.optJSONObject("downloads");
                                                                                    if (meet && downloadsJo1 != null) {
                                                                                        JSONObject artifactJo = downloadsJo1.optJSONObject("artifact");
                                                                                        if (artifactJo != null) {
                                                                                            String path = artifactJo.optString("path");
                                                                                            String url1 = artifactJo.optString("url");
                                                                                            if (!isEmpty(path) && !isEmpty(url1)) {
                                                                                                try {
                                                                                                    File file = new File(librariesDir, path);
                                                                                                    file.getParentFile().mkdirs();
                                                                                                    if (!file.exists()) {
                                                                                                        file.createNewFile();
                                                                                                    }
                                                                                                    if (file.length() == 0) {
                                                                                                        String textxx = String.format(getString("MESSAGE_DOWNLOADING_FILE"), url1.substring(url1.lastIndexOf("/") + 1));
                                                                                                        //print(VersionInstaller.class,textxx);
                                                                                                        addLog(textArea, textxx);
                                                                                                        downloadFile(url1, file, progressBar);
                                                                                                    }
                                                                                                } catch (Exception e1) {
                                                                                                    e1.printStackTrace();
                                                                                                    String textxx = String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARY"), url1, e1);
                                                                                                    //print(VersionInstaller.class,textxx);
                                                                                                    addLog(textArea, textxx);
                                                                                                }
                                                                                            }
                                                                                        }


                                                                                        JSONObject classifiersJo = downloadsJo1.optJSONObject("classifiers");
                                                                                        if (classifiersJo != null) {
                                                                                            JSONObject nativesNamesJO = jsonObject.optJSONObject("natives");

                                                                                            if (nativesNamesJO != null) {

                                                                                                //String osName = System.getProperty("os.name");
                                                                                                JSONObject nativesJo = classifiersJo.optJSONObject(nativesNamesJO.optString(OperatingSystem.CURRENT_OS.getCheckedName()));
                                                                                                ;
                                                                                                /*if (*//*osName.toLowerCase().contains("windows")*//*OperatingSystem.CURRENT_OS == OperatingSystem.WINDOWS) {
                                                                                                    nativesJo = classifiersJo.optJSONObject("natives-windows");
                                                                                                } else if (*//*osName.toLowerCase().contains("mac"*//*OperatingSystem.CURRENT_OS == OperatingSystem.OSX) {
                                                                                                    nativesJo = classifiersJo.optJSONObject("natives-macos");
                                                                                                } else {
                                                                                                    nativesJo = classifiersJo.optJSONObject("natives-linux");
                                                                                                }
*/

                                                                                                if (nativesJo != null) {
                                                                                                    String name12 = Utils.getNativeLibraryName(nativesJo.optString("path"));
                                                                                                    //System.out.println(Arrays.toString(nativesNames.toArray())+","+name );
                                                                                                    if (!nativesNames.contains(name12)) {
                                                                                                        String url1 = nativesJo.optString("url");
                                                                                                        try {
                                                                                                            if (!isEmpty(url1)) {
                                                                                                                File nativeFile = new File(tempNatives, url1.substring(url1.lastIndexOf("/") + 1));
                                                                                                                //if(!nativeFile.exists()) {
                                                                                                                nativeFile.createNewFile();
                                                                                                                String textxx = String.format(getString("MESSAGE_DOWNLOADING_FILE"), url1.substring(url1.lastIndexOf("/") + 1));
                                                                                                                //print(VersionInstaller.class,textxx);
                                                                                                                addLog(textArea, textxx);
                                                                                                                downloadFile(url1, nativeFile, progressBar);
                                                                                                                nativesNames.add(name12);
                                                                                                                //}
                                                                                                            }
                                                                                                        } catch (Exception e1) {
                                                                                                            e1.printStackTrace();
                                                                                                            String textxx = String.format(getString("MESSAGE_FAILED_DOWNLOAD_FILE"), url1);
                                                                                                            //print(VersionInstaller.class,textxx);
                                                                                                            addLog(textArea, textxx);
                                                                                                        }
                                                                                                    }
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
                                                                    } catch (Exception e1) {
                                                                        e1.printStackTrace();
                                                                        addLog(textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES"), e1));
                                                                    }


                                                                    File[] natives = tempNatives.listFiles((dir, name1) -> name1.endsWith(".jar"));
                                                                    if (natives != null && natives.length != 0) {
                                                                        addLog(textArea, getString("MESSAGE_INSTALL_DECOMPRESSING_NATIVE_LIBRARIES"));
                                                                        for (File file : natives) {
                                                                            try {
                                                                                File dir = new File(tempNatives, file.getName().substring(0, file.getName().lastIndexOf(".")));
                                                                                dir.mkdirs();
                                                                                String textxxx = String.format(getString("MESSAGE_UNZIPPING_FILE"), file.getName());
                                                                                //print(VersionInstaller.class,text);
                                                                                addLog(textArea, textxxx);
                                                                                unZip(file, dir, progressBar);
                                                                            } catch (Exception e1) {
                                                                                e1.printStackTrace();
                                                                                addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE"), file.getAbsolutePath(), e1));
                                                                            }
                                                                        }
                                                                    }
                                                                    addLog(textArea, getString("MESSAGE_INSTALL_DECOMPRESSED_NATIVE_LIBRARIES"));

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
                                                                        public boolean accept(File dir, String name12) {
                                                                            return dir.exists() && dir.isDirectory();
                                                                        }
                                                                    });

                                                                    for (File file : var4) {
                                                                        if (file != null && file.isDirectory()) {
                                                                            String finalHouzhui = houzhui;
                                                                            File[] files = file.listFiles(new FilenameFilter() {
                                                                                @Override
                                                                                public boolean accept(File dir, String name12) {
                                                                                    return name12.toLowerCase().endsWith(finalHouzhui) || name12.toLowerCase().endsWith(".jnilib");
                                                                                }
                                                                            });
                                                                            libFiles.addAll(Arrays.asList(files));
                                                                        }
                                                                    }

                                                                    for (File file : libFiles) {
                                                                        File to = new File(nativesDir, file.getName());
                                                                        try {
                                                                            String textxxxxxx = String.format(getString("MESSAGE_COPYING_FILE"), file.getName(), to.getPath());
                                                                            //print(VersionInstaller.class,text);
                                                                            addLog(textArea, textxxxxxx);
                                                                            Utils.copyFile(file, to);
                                                                        } catch (IOException e1) {
                                                                            e1.printStackTrace();
                                                                            addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_COPY_FILE"), file.getAbsolutePath(), to.getAbsolutePath(), e1));
                                                                        }
                                                                    }

                                                                    Utils.deleteDirectory(tempNatives);
                                                                    addLog(textArea, getString("MESSAGE_INSTALL_COPIED_NATIVE_LIBRARIES"));
                                                                    addLog(textArea, getString("MESSAGE_INSTALLED_NEW_VERSION"));
                                                                } catch (Exception ex) {
                                                                    ex.printStackTrace();
                                                                    addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_INSTALL_NEW_VERSION"), e));
                                                                    JOptionPane.showMessageDialog(frame, String.format(getString("MESSAGE_FAILED_TO_INSTALL_NEW_VERSION"), e), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                                                                }
                                                                progressBar.setValue(progressBar.getMaximum());
                                                                dialog.setExitable(true);
                                                                //dialog.dispose();
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
                                    updateVersions(null);
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

    public static void addLog(JTextArea textArea, String message) {
        print(VersionInstaller.class, message);
        if (textArea != null) {

            textArea.setText(textArea.getText() + "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" + message + "\n");
            Document doc = textArea.getDocument();
            textArea.setCaretPosition(doc.getLength());
        }
    }

}
