package com.mrshiehx.mclx.modules.version;

import com.mrshiehx.mclx.bean.Library;
import com.mrshiehx.mclx.dialog.UnExitableDialog;
import com.mrshiehx.mclx.modules.MinecraftLauncher;
import com.mrshiehx.mclx.utils.OperatingSystem;
import com.mrshiehx.mclx.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mrshiehx.mclx.utils.DownloadDialog.*;
import static com.mrshiehx.mclx.MinecraftLauncherX.*;

public class NativesReDownloader {
    public static void reDownload(JFrame frame, File versionDir, JSONArray librariesJa){
        JProgressBar progressBar = createProgressBar();
        JTextArea textArea = createTextArea();
        UnExitableDialog dialog = createDownloadDialog(frame, progressBar, textArea,getString("DIALOG_DOWNLOAD_LIBRARIES"));
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File tempNatives = new File("mclx", "temp_natives");
                        tempNatives.mkdirs();

                        String textx=getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES");
                        //print(VersionInstaller.class,textx);
                        addLog(textArea, textx);
                        try {
                            if (librariesJa != null) {
                                List<String> nativesNames=new ArrayList<>();
                                for (int i = 0; i < librariesJa.length(); i++) {
                                    JSONObject jsonObject = librariesJa.optJSONObject(i);
                                    if (jsonObject != null) {
                                        boolean meet=true;
                                        JSONArray rules=jsonObject.optJSONArray("rules");
                                        if(rules!=null){
                                            meet=MinecraftLauncher.isMeetConditions(rules,false,false);
                                        }
                                        //System.out.println(meet);

                                        JSONObject downloadsJo1 = jsonObject.optJSONObject("downloads");
                                        if (meet&& downloadsJo1 != null) {

                                            JSONObject classifiersJo = downloadsJo1.optJSONObject("classifiers");
                                            if (classifiersJo != null) {
                                                JSONObject nativesNamesJO=jsonObject.optJSONObject("natives");

                                                if(nativesNamesJO!=null) {

                                                    //String osName = System.getProperty("os.name");
                                                    JSONObject nativesJo = classifiersJo.optJSONObject(nativesNamesJO.optString(OperatingSystem.CURRENT_OS.getCheckedName()));;



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
                                                                    String textxx=String.format(getString("MESSAGE_DOWNLOADING_FILE"),url1.substring(url1.lastIndexOf("/")+1));
                                                                    //print(VersionInstaller.class,textxx);
                                                                    addLog(textArea, textxx);
                                                                    downloadFile(url1, nativeFile, progressBar);
                                                                    nativesNames.add(name12);
                                                                    //}
                                                                }
                                                            } catch (Exception e1) {
                                                                e1.printStackTrace();
                                                                String textxx=String.format(getString("MESSAGE_FAILED_DOWNLOAD_FILE"), url1);
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
                                progressBar.setValue(progressBar.getMaximum());
                                dialog.setExitable(true);
                                return;
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
                                    String textxxx=String.format(getString("MESSAGE_UNZIPPING_FILE"),file.getName());
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
                                        return name12.toLowerCase().endsWith(finalHouzhui)||name12.toLowerCase().endsWith(".jnilib");
                                    }
                                });
                                libFiles.addAll(Arrays.asList(files));
                            }
                        }

                        File nativesDir=new File(versionDir,Utils.getNativesDirName());
                        nativesDir.mkdirs();
                        for (File file : libFiles) {
                            File to = new File(nativesDir, file.getName());
                            try {
                                String textxxxxxx=String.format(getString("MESSAGE_COPYING_FILE"),file.getName(),to.getPath());
                                //print(VersionInstaller.class,text);
                                addLog(textArea, textxxxxxx);
                                if(to.exists())to.delete();
                                Utils.copyFile(file, to);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                addLog(textArea, String.format(getString("MESSAGE_FAILED_TO_COPY_FILE"), file.getAbsolutePath(), to.getAbsolutePath(), e1));
                            }
                        }

                        Utils.deleteDirectory(tempNatives);
                        addLog(textArea, getString("MESSAGE_INSTALL_COPIED_NATIVE_LIBRARIES"));
                        addLog(textArea, getString("MESSAGE_REDOWNLOADED_NATIVES"));
                        progressBar.setValue(progressBar.getMaximum());
                        dialog.setExitable(true);
                    }
                }).start();
            }
        });
        dialog.setVisible(true);
    }

    public static void addLog(JTextArea textArea, String message) {
        print(NativesReDownloader.class,message);
        if (textArea != null) {

            textArea.setText(textArea.getText() + "["+ new SimpleDateFormat("HH:mm:ss").format(new Date())+"]"+message + "\n");
            Document doc = textArea.getDocument();
            textArea.setCaretPosition(doc.getLength());
        }
    }
}
