package com.mrshiehx.mclx;

import com.sun.management.OperatingSystemMXBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MCLX {
    public static File gameDir;
    public static File assetsDir;
    public static File respackDir;
    public static File datapackDir;
    public static File smDir;
    public static File versionsDir;
    static JMenuBar menuBar;
    static JButton startGame;
    static JComboBox versionChooser;
    static JTextArea log;

    static File configFile = new File("mclx.json");
    static Process runningMc;

    public static String configContent = "";
    public static String javaPath = "";

    public static String MCLX_VERSION = "1.1";

    public static ImageIcon icon = new ImageIcon(MCLX.class.getResource("/icon.png"));

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }
        JFrame frame = new JFrame(Strings.APPLICATION_NAME);
        frame.setIconImage(icon.getImage());
        frame.setBounds(500, 250, 420, 350);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        int height = frame.getHeight();
        int width = frame.getWidth();
        frame.setLocation(screenWidth - width / 2, screenHeight - height / 2);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        startGame = new JButton(Strings.BUTTON_START_NAME);
        versionChooser = new JComboBox();
        log = new JTextArea();

        JMenu menu = new JMenu(Strings.MENU_NAME);

        JMenuItem settingsMenu = new JMenuItem(Strings.MENU_SETTINGS_NAME);
        JMenuItem installNewVersionMenu = new JMenuItem(Strings.MENU_INSTALL_NEW_VERSION);
        JMenuItem aboutMenu = new JMenuItem(Strings.MENU_ABOUT_NAME);
        JMenuItem exitMenu = new JMenuItem(Strings.DIALOG_BUTTON_EXIT_TEXT);
        JMenuItem killMc = new JMenuItem(Strings.MENU_KILL_MINECRAFT);

        settingsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings settings = new Settings(frame, true);
                settings.setVisible(true);
            }
        });
        aboutMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, Strings.DIALOG_ABOUT_DESCRIPTION, Strings.MENU_ABOUT_NAME, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
            }
        });
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        killMc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runningMc != null) {
                    if (runningMc.isAlive()) {
                        runningMc.destroy();
                    }
                }
                runningMc = null;
            }
        });

        installNewVersionMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                                JOptionPane.showMessageDialog(frame, Strings.MESSAGE_FAILED_TO_DOWNLOAD_VERSIONS_FILE, Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
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
                                            Strings.MESSAGE_CHOOSE_A_VERSION,
                                            Strings.MENU_INSTALL_NEW_VERSION,
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            selectionValues,
                                            selectionValues[0]
                                    );
                                    if (inputContent != null) {
                                        //System.out.println(urls.get(ids.indexOf(inputContent)));
                                        String jsonFileURL = urls.get(ids.indexOf(inputContent));

                                        String name = showInputNameDialog(frame, (String) inputContent, Strings.MESSAGE_INSTALL_INPUT_NAME);

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
                                                            UnExitableDialog dialog = new UnExitableDialog(frame, Strings.MENU_INSTALL_NEW_VERSION, true);
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
                                                                                addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOADING_JAR_FILE);
                                                                                downloadFile(url, jarFile, progressBar);
                                                                                addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOADED_JAR_FILE);
                                                                                progressBar.setValue(0);
                                                                                addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOADING_ASSETS);
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
                                                                                                            File dir = new File(objectsDir, hash.substring(0, 2));
                                                                                                            dir.mkdirs();
                                                                                                            File file = new File(dir, hash);
                                                                                                            if (!file.exists()) {
                                                                                                                file.createNewFile();
                                                                                                                downloadFile("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash, file, progressBar);
                                                                                                            }
                                                                                                        }
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                        addLog(textArea, String.format(Strings.MESSAGE_FAILED_DOWNLOAD_FILE, hash));
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOADED_ASSETS);
                                                                                        } catch (Exception e) {
                                                                                            e.printStackTrace();
                                                                                            addLog(textArea, String.format(Strings.MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS, e));
                                                                                        }
                                                                                    } else {
                                                                                        addLog(textArea, String.format(Strings.MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS, Strings.MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL));
                                                                                        //textArea.setText(textArea.getText()+String.format(Strings.MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS,Strings.MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL)+"\n");
                                                                                    }
                                                                                } else {
                                                                                    //textArea.setText(textArea.getText()+Strings.MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX+"\n");
                                                                                    addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX);
                                                                                }


                                                                                addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOADING_LIBRARIES);
                                                                                try {
                                                                                    JSONArray librariesJa = headVersionFile.optJSONArray("libraries");
                                                                                    if (librariesJa != null) {
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
                                                                                                                if(!file.exists()) {
                                                                                                                    file.getParentFile().mkdirs();
                                                                                                                    file.createNewFile();
                                                                                                                    downloadFile(url, file, progressBar);
                                                                                                                }
                                                                                                            } catch (Exception e) {
                                                                                                                e.printStackTrace();
                                                                                                                addLog(textArea, String.format(Strings.MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARY, url, e));
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                                    JSONObject classifiersJo = downloadsJo.optJSONObject("classifiers");
                                                                                                    if (classifiersJo != null) {
                                                                                                        String osName = System.getProperty("os.name");
                                                                                                        JSONObject nativesJo = null;
                                                                                                        if (osName.toLowerCase().startsWith("windows")) {
                                                                                                            nativesJo = classifiersJo.optJSONObject("natives-windows");
                                                                                                        } else if (osName.toLowerCase().startsWith("linux")) {
                                                                                                            nativesJo = classifiersJo.optJSONObject("natives-linux");
                                                                                                        } else if (osName.toLowerCase().startsWith("mac")) {
                                                                                                            nativesJo = classifiersJo.optJSONObject("natives-macos");
                                                                                                        }
                                                                                                        if (nativesJo != null) {
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
                                                                                                                addLog(textArea, String.format(Strings.MESSAGE_FAILED_DOWNLOAD_FILE, url));
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        addLog(textArea, Strings.MESSAGE_INSTALL_DOWNLOADED_LIBRARIES);
                                                                                    } else {
                                                                                        addLog(textArea, Strings.MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY);
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                    addLog(textArea, String.format(Strings.MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES, e));
                                                                                }


                                                                                File[] natives = tempNatives.listFiles((dir, name1) -> name1.endsWith(".jar"));
                                                                                if(natives!=null&&natives.length!=0) {
                                                                                    addLog(textArea, Strings.MESSAGE_INSTALL_DECOMPRESSING_LIBRARIES);
                                                                                    for (File file : natives) {
                                                                                        try {
                                                                                            File dir = new File(tempNatives, file.getName().substring(0, file.getName().lastIndexOf(".")));
                                                                                            dir.mkdirs();
                                                                                            unZip(file, dir, progressBar);
                                                                                        } catch (Exception e) {
                                                                                            e.printStackTrace();
                                                                                            addLog(textArea, String.format(Strings.MESSAGE_FAILED_TO_DECOMPRESS_FILE, file.getAbsolutePath(), e));
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

                                                                                List<File>libFiles=new ArrayList<>();
                                                                                String houzhui=".so";

                                                                                String osName = System.getProperty("os.name");

                                                                                if (osName.toLowerCase().startsWith("windows")) {
                                                                                    houzhui=".dll";
                                                                                } else if (osName.toLowerCase().startsWith("mac")) {
                                                                                    houzhui=".dylib";
                                                                                }

                                                                                File[] var4 = tempNatives.listFiles(new FilenameFilter() {
                                                                                    @Override
                                                                                    public boolean accept(File dir, String name) {
                                                                                        return dir.exists()&&dir.isDirectory();
                                                                                    }
                                                                                });

                                                                                for (File file : var4) {
                                                                                    if (file != null && file.isDirectory()) {
                                                                                        String finalHouzhui = houzhui;
                                                                                        File[] files = file.listFiles(new FilenameFilter() {
                                                                                            @Override
                                                                                            public boolean accept(File dir, String name) {
                                                                                                return name.endsWith(finalHouzhui);
                                                                                            }
                                                                                        });
                                                                                        libFiles.addAll(Arrays.asList(files));
                                                                                    }
                                                                                }

                                                                                for(File file:libFiles){
                                                                                    File to=new File(nativesDir,file.getName());
                                                                                    try{
                                                                                        copyFile(file,to);
                                                                                    }catch (IOException e){
                                                                                        e.printStackTrace();
                                                                                        addLog(textArea,String.format(Strings.MESSAGE_FAILED_TO_COPY_FILE,file.getAbsolutePath(),to.getAbsolutePath(),e));
                                                                                    }
                                                                                }

                                                                                deleteDirectory(tempNatives);
                                                                                addLog(textArea, Strings.MESSAGE_INSTALL_DECOMPRESSED_LIBRARIES);
                                                                                addLog(textArea, Strings.MESSAGE_INSTALLED_NEW_VERSION);
                                                                            } catch (Exception ex) {
                                                                                ex.printStackTrace();
                                                                                addLog(textArea,String.format(Strings.MESSAGE_FAILED_TO_INSTALL_NEW_VERSION, e));
                                                                                JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_FAILED_TO_INSTALL_NEW_VERSION, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
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
                                                            JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_INSTALL_JAR_FILE_DOWNLOAD_URL_EMPTY, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                                                        }
                                                    } else {
                                                        JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                                                }

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_FAILED_TO_CONTROL_VERSION_JSON_FILE, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                                            }
                                            updateVersions();
                                        }


                                    }
                                } else {
                                    JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_VERSIONS_LIST_IS_EMPTY, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.WARNING_MESSAGE);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(frame, String.format(Strings.MESSAGE_FAILED_TO_PARSE_VERSIONS_FILE, e), Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, Strings.MESSAGE_FAILED_TO_CONNECT_TO_LAUNCHERMETA, Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });

        menu.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (runningMc != null) {
                    killMc.setEnabled(runningMc.isAlive());
                } else {
                    killMc.setEnabled(false);
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        menu.add(killMc);
        menu.add(installNewVersionMenu);
        menu.add(settingsMenu);
        menu.add(aboutMenu);
        menu.add(exitMenu);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        frame.setResizable(false);

        versionChooser.setBounds(10, 10, 200, 30);
        startGame.setBounds(220, 10, 175, 30);
        startGame.setFont(new Font(null, Font.BOLD, 17));
        versionChooser.setFont(new Font(null, Font.PLAIN, 15));
        log.setBounds(10, 50, 385, 230);
        log.setEditable(false);
        log.setFont(new Font(null, Font.PLAIN, 12));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);

        JScrollPane jsp = new JScrollPane(log);
        jsp.setBounds(10, 50, 385, 230);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        frame.add(startGame);
        frame.add(versionChooser);
        frame.add(jsp);

        if (configFile.exists()) {
            try {
                configContent = readFileContent(configFile);
                JSONObject jsonObject = new JSONObject(configContent);
                javaPath = jsonObject.optString("jp");
                if (jsonObject.optBoolean("cw")) {

                    gameDir = new File(!isEmpty(jsonObject.optString("gd")) ? jsonObject.optString("gd") : ".minecraft");
                    assetsDir = new File(!isEmpty(jsonObject.optString("ad")) ? jsonObject.optString("ad") : ".minecraft/assets");
                    respackDir = new File(!isEmpty(jsonObject.optString("rd")) ? jsonObject.optString("rd") : ".minecraft/resourcepacks");
                    datapackDir = new File(!isEmpty(jsonObject.optString("dd")) ? jsonObject.optString("dd") : ".minecraft/datapacks");
                    smDir = new File(!isEmpty(jsonObject.optString("sd")) ? jsonObject.optString("sd") : ".minecraft/simplemods");
                    /*File[] filesFromFile = new File[]{gameDirFromFile, assetDirFromFile, resPackDirFromFile, dataPackDirFromFile, smDirFromFile};
                    for (int i = 0; i < filesFromFile.length; i++) {
                        if (!filesFromFile[i].exists()) {

                            Object[] options = new Object[]{Strings.MENU_SETTINGS_NAME, Strings.DIALOG_BUTTON_CANCEL_TEXT};

                            int optionSelected = JOptionPane.showOptionDialog(
                                    frame,
                                    String.format(Strings.DIALOG_TARGET_FILE_NOT_EXISTS_TEXT, filesFromFile[i].getAbsolutePath()),
                                    Strings.DIALOG_TITLE_NOTICE,
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[0]
                            );

                            if (optionSelected >= 0) {
                                if (optionSelected == 0) {
                                    Settings settings = new Settings(null, true);
                                    settings.setVisible(true);
                                }
                            }

                        } else {
                            if (i == 0) {
                                gameDir = filesFromFile[i];
                            } else if (i == 1) {
                                assetsDir = filesFromFile[i];
                            } else if (i == 2) {
                                respackDir = filesFromFile[i];
                            } else if (i == 3) {
                                datapackDir = filesFromFile[i];
                            } else if (i == 4) {
                                smDir = filesFromFile[i];
                            }

                        }
                    }*/
                } else {
                    initDefaultDirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            initDefaultDirs();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pn", "XPlayer");
            jsonObject.put("mm", 1024);
            jsonObject.put("ww", 854);
            jsonObject.put("wh", 480);
            configContent = jsonObject.toString();
            try {
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile, false);
                writer.write(jsonObject.toString());
                writer.close();
            } catch (IOException E) {
                E.printStackTrace();
            }
        }


        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(versionChooser.getModel().getSelectedItem()!=null) {
                    try {
                        configContent = readFileContent(configFile);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(frame, exception, Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                    }
                    JSONObject jsonObject = new JSONObject(configContent);
                    if (!configFile.exists() || configContent == null || javaPath == null || (javaPath == null ? true : !new File(javaPath).exists())) {
                        Object[] options = new Object[]{Strings.MENU_SETTINGS_NAME, Strings.DIALOG_BUTTON_CANCEL_TEXT};
                        int optionSelected = JOptionPane.showOptionDialog(
                                frame,
                                Strings.MESSAGE_NOT_FOUND_JAVA,
                                Strings.DIALOG_TITLE_NOTICE,
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE,
                                null,
                                options,
                                options[0]
                        );

                        if (optionSelected >= 0) {
                            if (optionSelected == 0) {
                                Settings settings = new Settings(frame, true);
                                settings.setVisible(true);
                            }
                        }
                    } else {
                        String selected = (String) versionChooser.getSelectedItem();
                        File versionsFolder = addTo(gameDir, "versions");
                        File versionFolder = addTo(versionsFolder, selected);
                        File versionJarFile = addTo(versionFolder, selected + ".jar");
                        File versionJsonFile = addTo(versionFolder, selected + ".json");
                        try {
                            runningMc = launchMinecraft(versionJarFile, versionJsonFile, gameDir, assetsDir, respackDir, datapackDir, jsonObject.optBoolean("ls"), smDir, jsonObject.optString("pn", "XPlayer"), jsonObject.optString("jp"), jsonObject.optInt("mm", 1024), 128, jsonObject.optInt("ww", 854), jsonObject.optInt("wh", 480), jsonObject.optBoolean("fs"), "0");

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DataInputStream dis = new DataInputStream(runningMc.getInputStream());
                                    String line;
                                    try {
                                        while ((line = dis.readLine()) != null) {
                                            //Log.d("result", line);
                                            //result += line;
                                            log.setText(log.getText() + line + "\n");
                                            Document doc = log.getDocument();
                                            log.setCaretPosition(doc.getLength());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        runningMc.waitFor();
                                        log.setText(log.getText() + Strings.MESSAGE_FINISHED_GAME);
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, ex, Strings.DIALOG_TITLE_NOTICE, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        /*if (!gameDir.exists()) {
            Object[] options = new Object[]{Strings.MENU_SETTINGS_NAME, Strings.DIALOG_BUTTON_EXIT_TEXT};

            int optionSelected = JOptionPane.showOptionDialog(
                    frame,
                    Strings.DIALOG_NO_MINECRAFT_DIR_TEXT,
                    Strings.DIALOG_TITLE_NOTICE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (optionSelected >= 0) {
                if (optionSelected == 0) {
                    Settings settings = new Settings(null, true);
                    settings.setVisible(true);
                } else if (optionSelected == 1) {
                    System.exit(0);
                }
            }
        }*/
        if (gameDir.getAbsolutePath().endsWith("/") || gameDir.getAbsolutePath().endsWith("\\")) {
            versionsDir = new File(gameDir.getAbsolutePath() + "versions");
        } else {
            //versionsDir=new File(gameDir.getAbsolutePath()+File.separator+"versions");
            if (gameDir.getAbsolutePath().lastIndexOf("\\") != -1) {
                versionsDir = new File(gameDir.getAbsolutePath() + "\\" + "versions");
            } else {
                versionsDir = new File(gameDir.getAbsolutePath() + "/" + "versions");
            }
        }

        updateVersions();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void updateVersions() {
        List<String> versionsStrings = new ArrayList();
        File[] files = versionsDir.listFiles(File::isDirectory);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                versionsStrings.add(getVersion(files[i].getAbsolutePath()));
            }
            String[] strArray = new String[versionsStrings.size()];
            versionsStrings.toArray(strArray);
            ComboBoxModel<String> spinnerListModel = new DefaultComboBoxModel<String>(strArray);
            versionChooser.setModel(spinnerListModel);
        }
    }

    public static String getVersion(String path) {
        String split = "/";
        if (path.contains("\\")) {
            split = "\\";
        }
        String noDriver = path.substring(path.indexOf(split));
        int indexOf = noDriver.lastIndexOf(split);
        return noDriver.substring(indexOf + 1);
    }

    public static void initDefaultDirs() {
        gameDir = new File(".minecraft");
        assetsDir = new File(gameDir, "assets");
        respackDir = new File(gameDir, "resourcepacks");
        datapackDir = new File(gameDir, "datapacks");
        smDir = new File(gameDir, "simplemods");
        versionsDir = new File(gameDir, "versions");
    }

    public static boolean isWindows() {
        return /*File.separator.equals("\\")||File.separatorChar=='\\'||*//*AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.WINDOWS*/System.getProperty("os.name").startsWith("Windows") || System.getProperty("os.name").startsWith("windows");
    }

    public static Process launchMinecraft(
            File minecraftJarFile,
            File minecraftVersionJsonFile,
            File gameDir,
            File assetsDir,
            File resourcePacksDir,
            File dataPacksDir,
            boolean loadSM,
            File SMDir,
            String playername,
            String javaPath,
            int maxMemory,
            int miniMemory,
            int width,
            int height,
            boolean fullscreen,
            String accessToken) throws
            LaunchException,
            IOException,
            JSONException {
        log.setText(null);
        if (!new File(javaPath).exists()) {
            throw new LaunchException(Strings.EXCEPTION_VERSION_JSON_NOT_FOUND);
        }
        if (gameDir == null) {
            gameDir = new File(".minecraft");
        }
        if (assetsDir == null) {
            assetsDir = new File(".minecraft/assets");
        }
        if (resourcePacksDir == null) {
            resourcePacksDir = new File(".minecraft/resourcepacks");
        }
        if (dataPacksDir == null) {
            dataPacksDir = new File(".minecraft/datapacks");
        }
        if (SMDir == null) {
            SMDir = new File(".minecraft/simplemods");
        }
        if (!gameDir.exists()) {
            throw new LaunchException(Strings.MESSAGE_NOT_FOUND_GAME_DIR);
        }
        if (maxMemory == 0) {
            throw new LaunchException(Strings.EXCEPTION_MAX_MEMORY_IS_ZERO);
        }

        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
        }

        if (!resourcePacksDir.exists()) {
            resourcePacksDir.mkdirs();
        }

        if (!dataPacksDir.exists()) {
            dataPacksDir.mkdirs();
        }

        if (!SMDir.exists()) {
            SMDir.mkdirs();
        }

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;

        if (maxMemory > physicalTotal) {
            throw new LaunchException(Strings.EXCEPTION_MAX_MEMORY_TOO_BIG);
        }

        String contentOfJsonFile;
        if (!minecraftVersionJsonFile.exists()) {
            throw new LaunchException(Strings.EXCEPTION_VERSION_JSON_NOT_FOUND);
        } else {
            contentOfJsonFile = readFileContent(minecraftVersionJsonFile);
        }
        if (!minecraftJarFile.exists()) {
            throw new LaunchException(Strings.EXCEPTION_VERSION_NOT_FOUND);
        }
        log.setText(Strings.MESSAGE_STARTING_GAME + "\n");
        JSONObject headJsonObject = new JSONObject(contentOfJsonFile);
        JSONArray libraries = headJsonObject.optJSONArray("libraries");
        File librariesFile = addTo(gameDir, "libraries");
        List<String> librariesPaths = new ArrayList();
        //List<String> librariesParentsPaths = new ArrayList();
        for (int i = 0; i < libraries.length(); i++) {
            JSONObject library = libraries.optJSONObject(i);
            String name = library.optString("name");
            String[] nameSplit = name.split(":");
            String libraryFileName = nameSplit[1] + "-" + nameSplit[2] + ".jar";
            String libraryFileAndDirectoryName = nameSplit[0].replace(".", "/") + "/" + nameSplit[1] + "/" + nameSplit[2];
            File libraryFile = addTo(addTo(librariesFile, libraryFileAndDirectoryName), libraryFileName);

            /*String parent=libraryFile.getParentFile().getParent();

            boolean chongTu=false;

            for(String string:librariesPaths){
                if(string.startsWith(parent)){
                    chongTu=true;
                    break;
                }
            }


            int libraryFileParLf = libraryFile.getParentFile().getParentFile().listFiles().length;

            if(name.contains("lwjgl")) {
                if (libraryFileParLf == 1) {
                    if (libraryFile.exists() && !librariesPaths.contains(libraryFile.getAbsolutePath()) && !chongTu) {
                        librariesPaths.add(libraryFile.getAbsolutePath());

                    }
                } else {
                    File[] files = libraryFile.getParentFile().getParentFile().listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.startsWith("0") ||
                                    name.startsWith("1") ||
                                    name.startsWith("2") ||
                                    name.startsWith("3") ||
                                    name.startsWith("4") ||
                                    name.startsWith("5") ||
                                    name.startsWith("6") ||
                                    name.startsWith("7") ||
                                    name.startsWith("8") ||
                                    name.startsWith("9");
                        }
                    });
                    System.out.println(Arrays.toString(files));
                    String at = twoPointsVersionThanner(files[0].getName(), files[1].getName());
                    File file = new File(libraryFile.getParentFile().getParentFile(), at);
                    File[] files1 = file.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return !name.contains("native") && name.endsWith(".jar");
                        }
                    });
                    if (files1[0] != null && files1[0].exists() && !librariesPaths.contains(files1[0].getAbsolutePath()) && !chongTu) {
                        librariesPaths.add(files1[0].getAbsolutePath());
                    }
                }
            }else{
                if (libraryFile.exists() && !librariesPaths.contains(libraryFile.getAbsolutePath()) && !chongTu) {
                    librariesPaths.add(libraryFile.getAbsolutePath());

                }
            }*/

            //if (libraryFileParLf == 1) {
                /*if (libraryFile.exists() && !librariesPaths.contains(libraryFile.getAbsolutePath()) && !chongTu) {
                    librariesPaths.add(libraryFile.getAbsolutePath());
                }*/
            /*} else {
                File file = libraryFile.getParentFile().getParentFile().listFiles()[libraryFile.getParentFile().getParentFile().listFiles().length - 1];
                List<File> files = new ArrayList<>();//new File(file, file.list()[0]);
                File[]var=file.listFiles();

                for(File file1:var){
                    if(!file1.getName().toLowerCase().contains("-natives-")) {
                        files.add(file1);
                    }
                }

                for(File file2:files) {
                    if (file2.exists() && !librariesPaths.contains(file2.getAbsolutePath())) {
                        librariesPaths.add(file2.getAbsolutePath());
                    }
                }
            }*/


            if(librariesPaths.size()>=1) {
                for (int n = 0; n < librariesPaths.size(); n++) {
                    if (librariesPaths.get(n).startsWith(addSeparatorToPath(libraryFile.getParentFile().getParent()))) {
                        librariesPaths.remove(n);
                        break;
                    }
                }
            }
            if (libraryFile.exists() && !librariesPaths.contains(libraryFile.getAbsolutePath())) {
                librariesPaths.add(libraryFile.getAbsolutePath());
            }

        }
        String arguments = "";
        String id = headJsonObject.optString("id", "1.0");


        JSONObject assetIndexObject = headJsonObject.optJSONObject("assetIndex");

        String assetsIndex = assetIndexObject.optString("id");


        if (id.startsWith("1.")) {
            if (!id.startsWith("1.RV-Pre1")) {
                String[] ids = id.split("\\.");
                //int id1stPart=Integer.parseInt(ids[0]);
                int id2ndPart = Integer.parseInt(ids[1].substring(0, numberOfAStringStartInteger(ids[1])));

                if (id2ndPart >= 13) {
                    JSONObject argumentsArray = headJsonObject.optJSONObject("arguments");
                    JSONArray gameArray = argumentsArray.optJSONArray("game");
                    for (int i = 0; i < gameArray.length(); i++) {
                        if (gameArray.opt(i) instanceof String) {
                            arguments = arguments + gameArray.opt(i) + " ";
                        } else {
                            arguments = arguments.substring(0, arguments.length() - 1);
                            break;
                        }
                    }
                } else {
                    arguments = headJsonObject.optString("minecraftArguments");
                }
            } else {
                arguments = headJsonObject.optString("minecraftArguments");
            }
        } else {
            char[] idChars = id.toCharArray();

            if (idChars[2] == 'w') {
                String[] idsForSnapshot = id.split("w");
                if (Integer.parseInt(idsForSnapshot[0]) > 17) {
                    arguments = getArguments(headJsonObject);
                } else if (Integer.parseInt(idsForSnapshot[0]) < 17) {
                    arguments = headJsonObject.optString("minecraftArguments");
                } else if (Integer.parseInt(idsForSnapshot[0]) == 17) {
                    int partOfWeekNumber = Integer.parseInt(idsForSnapshot[1].substring(0,/*idsForSnapshot[1].length()-1*/numberOfAStringStartInteger(idsForSnapshot[1])));
                    if (partOfWeekNumber >= 43) {
                        arguments = getArguments(headJsonObject);
                    } else {
                        arguments = headJsonObject.optString("minecraftArguments");
                    }
                }
            } else {
                if (id.equals("3D Shareware v1.34")) {
                    arguments = getArguments(headJsonObject);
                } else {
                    arguments = headJsonObject.optString("minecraftArguments");
                }
            }
        }


        String mainClass = headJsonObject.optString("mainClass", "net.minecraft.client.main.Main");
        File nativesFolder = addTo(minecraftVersionJsonFile.getParentFile(), "natives");
        String librariesString = "";
        for (String librariesPath : librariesPaths) {
            librariesString = librariesString + librariesPath.replace("\\", "\\\\") + (isWindows() ? ";" : ":");
        }
        librariesString = librariesString + minecraftJarFile.getAbsolutePath().replace("\\", "\\\\");

        if (loadSM) {
            mainClass = "com.mrshiehx.simplemod.loader.Main";
            arguments = "--smDir ${sm_directory} --mainClass ${main_class}" + arguments;
            File smloaderFile = new File("simplemod-loader-1.0.jar");
            copyFile(new File(MCLX.class.getResource("assets/simplemod-loader-1.0.jar").getFile()), smloaderFile);
            librariesString = librariesString + (isWindows() ? ";" : ":") + smloaderFile.getAbsolutePath().replace("\\", "\\\\");
        }

        String parsed = arguments.replace("${main_class}", headJsonObject.optString("mainClass", "net.minecraft.client.main.Main")).replace("${sm_directory}", addShuangyinhaoToPath(SMDir.getAbsolutePath())).replace("${auth_player_name}", playername).replace("${version_name}", "\"MCLX " + MCLX_VERSION + "\"").replace("${version_type}", "\"MCLX " + MCLX_VERSION + "\"").replace("${auth_access_token}", accessToken).replace("${game_directory}", addShuangyinhaoToPath(gameDir.getAbsolutePath())).replace("${assets_root}", addShuangyinhaoToPath(assetsDir.getAbsolutePath())).replace("${assets_index_name}",/*ids[0]+"."+ids[1]*/assetsIndex).replace("--uuid ${auth_uuid}", "").replace("${user_type}", "mojang").replace("${auth_session}", "0").replace("${game_assets}", addShuangyinhaoToPath(assetsDir.getAbsolutePath()));

        parsed = parsed + " --resourcePackDir " + addShuangyinhaoToPath(resourcePacksDir.getAbsolutePath()) + " --dataPackDir " + addShuangyinhaoToPath(dataPacksDir.getAbsolutePath());
        if (fullscreen) {
            parsed = parsed + " --fullscreen";
        }

        parsed = parsed + " --width " + width + " --height " + height;


        String javaLibraryPath;
        if (nativesFolder.exists()) {
            javaLibraryPath = "\"-Djava.library.path=" + nativesFolder.getAbsolutePath().replace("\\", "\\\\") + "\"";
        } else {
            throw new LaunchException(Strings.EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND);
        }

        String command = addShuangyinhaoToPath(javaPath) + " -Xmn" + miniMemory + "m -Xmx" + maxMemory + "m " + javaLibraryPath + " -XX:+UseG1GC -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true -Dminecraft.launcher.brand=MCLX -Dminecraft.launcher.version=" + MCLX_VERSION + " -cp \"" + librariesString + "\" " + mainClass + " " + parsed;
        //System.out.println(command+"\n");
        /*if(logsOutput!=null) {net.minecraft.client.main.Main --username XPlayer --version "MCLX 1.0" --gameDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft" --assetsDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\assets" --assetIndex 1.16  --accessToken 0 --userType legacy --versionType "MCLX 1.0" --resourcePackDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\resourcepacks" --dataPackDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\datapacks" --width 854 --height 480
            System.setOut(new PrintStream(logsOutput));
        }*/
        System.out.println(command);
        Runtime run = Runtime.getRuntime();
        return run.exec(command);
    }

    public static String addShuangyinhaoToPath(String path) {
        if (path.startsWith("\"")) {
            if (!path.endsWith("\"")) {
                path = path + "\"";
            }
        } else if (path.endsWith("\"")) {
            if (!path.startsWith("\"")) {
                path = "\"" + path;
            }
        } else {
            path = "\"" + path + "\"";
        }
        return path.replace("\\", "\\\\");
    }

    public static String addSeparatorToPath(String path) {
        String separator=getFileSeparator(path);
        if (!path.endsWith(separator)) {
            path = path + separator;
        }
        return path;
    }

    public static void copyDirectory(File from,String toWillNewDirNameIsAtFromName,String afterThatName)throws IOException{
        if(from!=null&&!isEmpty(toWillNewDirNameIsAtFromName)&&from.exists()){
            if(from.isFile()) {
                copyFile(from, new File(toWillNewDirNameIsAtFromName, afterThatName));
                return;
            }
            File toWillNewDirNameIsAtFrom=new File(toWillNewDirNameIsAtFromName);
            File to=new File(toWillNewDirNameIsAtFrom,afterThatName);
            if(!to.exists())to.mkdirs();
            for(File file:from.listFiles()){
                if(file.isFile()){
                    copyFile(file,new File(to,file.getName()));
                }else{
                    copyDirectory(file,to.getAbsolutePath(),file.getName());
                }
            }
        }
    }

    public static void copyFile(File source, File to)
            throws IOException {
        if(source==null)return;
        if(source.isDirectory())copyDirectory(source,to.getParent(),to.getName());
        if (to.exists()) {
            to.delete();
        }
        to.createNewFile();
        InputStream input = new FileInputStream(source);
        OutputStream output = new FileOutputStream(to);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buf)) != -1) {
            output.write(buf, 0, bytesRead);
        }
        input.close();
        output.close();
    }

    public static String readFileContent(File file) throws IOException {
        BufferedReader reader;
        StringBuffer sbf = new StringBuffer();
        reader = new BufferedReader(new FileReader(file));
        String tempStr;
        while ((tempStr = reader.readLine()) != null) {
            sbf.append(tempStr);
        }
        reader.close();
        return sbf.toString();
    }

    public static boolean hasWindowsFileSeparator(String path) {
        return path.contains("\\");
    }

    public static File addTo(File startFile, String needToAddNoSeparatorStart) {
        String path = startFile.getAbsolutePath();
        String separator = "/";
        if (hasWindowsFileSeparator(path)) {
            separator = "\\";
        }
        if (path.endsWith(separator)) {
            return new File(path + needToAddNoSeparatorStart);
        } else {
            return new File(path + separator + needToAddNoSeparatorStart);
        }
    }

    public static String getFileSeparator(String path) {
        return hasWindowsFileSeparator(path) ? "\\" : "/";
    }

    public static int numberOfAStringStartInteger(String target) {
        int r = 0;
        char[] targetChars = target.toCharArray();
        for (int i = 0; i < target.length(); i++) {
            if (targetChars[i] == '0' || targetChars[i] == '1' || targetChars[i] == '2' || targetChars[i] == '3' || targetChars[i] == '4' || targetChars[i] == '5' || targetChars[i] == '6' || targetChars[i] == '7' || targetChars[i] == '8' || targetChars[i] == '9') {
                r++;
            } else {
                break;
            }
        }
        return r;
    }

    private static String getArguments(JSONObject headJsonObject) {
        String arguments = "";
        JSONObject argumentsArray = headJsonObject.optJSONObject("arguments");
        JSONArray gameArray = argumentsArray.optJSONArray("game");
        for (int i = 0; i < gameArray.length(); i++) {
            if (gameArray.opt(i) instanceof String) {
                arguments = arguments + gameArray.opt(i) + " ";
            } else {
                arguments = arguments.substring(0, arguments.length() - 1);
                break;
            }
        }
        return arguments;
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

    private static void downloadFile(String url, File to) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(to);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
    }

    private static void downloadFile(String urla, File to, JProgressBar progressBar) throws IOException {
        URL url = new URL(urla);
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        int completeFileSize = httpConnection.getContentLength();
        if (progressBar != null)
            progressBar.setMaximum(completeFileSize);

        BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
        FileOutputStream fos = new java.io.FileOutputStream(to);
        BufferedOutputStream bout = new BufferedOutputStream(
                fos, 1024);
        if (progressBar != null)
            progressBar.setMaximum(100);
        byte[] data = new byte[1024];
        long downloadedFileSize = 0;
        int x = 0;
        while ((x = in.read(data, 0, 1024)) >= 0) {
            downloadedFileSize += x;

            // calculate progress
            final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);

            // update progress bar
            if (progressBar != null)
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setValue(currentProgress);
                    }
                });
            bout.write(data, 0, x);
        }
        if (progressBar != null)
            progressBar.setValue(0);
        bout.close();
        in.close();
    }

    private static String showInputNameDialog(Component parent, String defaultName, String message) {
        String ret = (String) JOptionPane.showInputDialog(parent, message, Strings.MENU_INSTALL_NEW_VERSION, JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
        if (ret != null) {
            if (ret.length() == 0) {
                return showInputNameDialog(parent, ret, Strings.MESSAGE_INSTALL_INPUT_NAME_IS_EMPTY);
            }
            if(versionsDir.exists()) {
                String[] versions = versionsDir.list();
                if(versions!=null&&versions.length!=0) {
                    if (Arrays.asList(versions).contains(ret)) {
                        return showInputNameDialog(parent, ret, String.format(Strings.MESSAGE_INSTALL_INPUT_NAME_EXISTS, ret));
                    }
                }
            }
        }
        return ret;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }


    public static void addLog(JTextArea textArea, String message) {
        if (textArea != null) {
            textArea.setText(textArea.getText() + message + "\n");
            Document doc = textArea.getDocument();
            textArea.setCaretPosition(doc.getLength());
        }
    }

    public static void unZip(File srcFile, File to, JProgressBar progressBar) throws IOException {
        int BUFFER_SIZE = 2048;
        if (srcFile!=null&&srcFile.exists()) {
            ZipFile zipFile = new ZipFile(srcFile);

            if (progressBar != null)
                progressBar.setMaximum(zipFile.size());
            Enumeration<?> entries = zipFile.entries();
            int progress = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                File targetFile = new File(to, entry.getName());
                if (entry.isDirectory()) {
                    targetFile.mkdirs();
                } else {
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
                progress++;
                if (progressBar != null)
                    progressBar.setValue(progress);
            }
            zipFile.close();
            if (progressBar != null)
                progressBar.setValue(0);
        }
    }

    public static void deleteDirectory(File directory) {
        if (directory != null) {
            if (directory.exists()) {
                if (directory.isDirectory()) {
                    if (directory.listFiles() != null) {
                        if (directory.listFiles().length != 0) {
                            File[] files = directory.listFiles();
                            for (File file : files) {
                                if (file.isFile()) {
                                    file.delete();
                                } else {
                                    deleteDirectory(file);
                                }
                            }
                        }
                    }
                }
                directory.delete();

            }
        }
    }

    /*public static String twoPointsVersionThanner(String str1, String str2){
        String[]str1s=str1.split("\\.");
        String[]str2s=str2.split("\\.");
        int oo=Integer.parseInt(str1s[0]);
        int ow=Integer.parseInt(str1s[1]);
        int oh=Integer.parseInt(str1s[2]);
        int wo=Integer.parseInt(str2s[0]);
        int ww=Integer.parseInt(str2s[1]);
        int wh=Integer.parseInt(str2s[2]);

        if(oo==wo){
            if(ow==ww){
                if(oh==wh){
                    return str1;
                }else if(oh>wh){
                    return str1;
                }else {
                    return str2;
                }
            }else if(ow>ww){
                return str1;
            }else {
                return str2;
            }
        }else if(oo>wo){
            return str1;
        }else {
            return str2;
        }
    }*/

}