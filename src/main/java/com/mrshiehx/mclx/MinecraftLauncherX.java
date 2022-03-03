package com.mrshiehx.mclx;

import com.mrshiehx.mclx.bean.Library;
import com.mrshiehx.mclx.dialog.UnExitableDialog;
import com.mrshiehx.mclx.enums.GameCrashError;
import com.mrshiehx.mclx.exceptions.EmptyNativesException;
import com.mrshiehx.mclx.exceptions.LaunchException;
import com.mrshiehx.mclx.exceptions.LibraryDefectException;
import com.mrshiehx.mclx.modules.MinecraftLauncher;
import com.mrshiehx.mclx.modules.version.NativesReDownloader;
import com.mrshiehx.mclx.modules.version.VersionInstaller;
import com.mrshiehx.mclx.settings.Settings;
import com.mrshiehx.mclx.utils.OperatingSystem;
import com.mrshiehx.mclx.utils.SwingUtils;
import com.mrshiehx.mclx.utils.Utils;
import com.mrshiehx.mclx.modules.version.VersionsManager;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.mrshiehx.mclx.modules.MinecraftLauncher.*;
import static com.mrshiehx.mclx.utils.DownloadDialog.*;

public class MinecraftLauncherX {
    public static final String CLIENT_ID = "bcb89757-1625-4561-8bc6-34d04a11a07f";
    private static final String MCLX_COPYRIGHT = "Copyright \u00a9 2022 MrShiehX";
    public static File gameDir;
    public static File assetsDir;
    public static File respackDir;
    public static File smDir;
    public static File versionsDir;
    static JMenuBar menuBar;
    static JButton startGame;
    static JComboBox<String> versionChooser;
    static JTextArea log;

    static File configFile = new File("mclx.json");
    static Process runningMc;

    public static String configContent = "";
    public static String javaPath = "";

    public static String MCLX_VERSION = "1.4";

    public static final ImageIcon icon;

    private static JSONObject enUSText;
    private static JSONObject zhCNText;

    static String language;

    static {
        URL url = MinecraftLauncherX.class.getResource("/icon.png");
        if (url != null) icon = new ImageIcon(url);
        else icon = null;

        if (OperatingSystem.CURRENT_OS == OperatingSystem.OSX) {
            System.setProperty("apple.awt.UIElement", "true");
        }
    }

    public static void main(String[] args) {
        JSONObject startConfig = initConfig();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }
        JFrame frame = new JFrame(getString("APPLICATION_SHORT_NAME"));
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }
        int frameWidth = 630;
        int frameHeight = 525;
        Point framePoint = SwingUtils.getCenterLocation(frameWidth, frameHeight);
        frame.setBounds(framePoint.x, framePoint.y, frameWidth, frameHeight);
        //Toolkit kit = Toolkit.getDefaultToolkit();
        //Dimension screenSize = kit.getScreenSize();
        /*int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        int height = frame.getHeight();
        int width = frame.getWidth();*/
        //frame.setLocation(screenWidth - width / 2, screenHeight - height / 2);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        startGame = new JButton(getString("BUTTON_START_NAME"));
        versionChooser = new JComboBox<>();
        log = new JTextArea();

        JMenu gameMenu = new JMenu(getString("MENU_GAME"));
        JMenu versionMenu = new JMenu(getString("MENU_VERSION"));
        JMenu launcherMenu = new JMenu(getString("MENU_LAUNCHER"));

        JMenuItem settingsMenu = new JMenuItem(getString("MENU_SETTINGS_NAME"));
        JMenuItem copyCommand = new JMenuItem(getString("MENU_COPY_COMMAND"));
        JMenuItem installNewVersionMenu = new JMenuItem(getString("MENU_INSTALL_NEW_VERSION"));
        JMenuItem aboutMenu = new JMenuItem(getString("MENU_ABOUT_NAME"));
        JMenuItem exitMenu = new JMenuItem(getString("DIALOG_BUTTON_EXIT_TEXT"));
        JMenuItem killMc = new JMenuItem(getString("MENU_KILL_MINECRAFT"));

        settingsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Settings settings = new Settings(frame, true);
                //settings.setVisible(true);
                Settings.showIt(frame, true, true);
            }
        });
        aboutMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon2 = null;
                if (icon != null) {
                    icon2 = new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
                }
                JOptionPane.showMessageDialog(frame, String.format(getString("DIALOG_ABOUT_DESCRIPTION"), MCLX_VERSION, MCLX_COPYRIGHT), getString("MENU_ABOUT_NAME"), JOptionPane.INFORMATION_MESSAGE, icon2);
            }
        });
        exitMenu.addActionListener(e -> System.exit(0));
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
                VersionInstaller.showDialog(frame);
            }
        });

        gameMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (runningMc != null) {
                    killMc.setEnabled(runningMc.isAlive());
                } else {
                    killMc.setEnabled(false);
                }

                copyCommand.setEnabled(versionChooser.getModel().getSelectedItem() != null);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });


        copyCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    configContent = Utils.readFileContent(configFile);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(frame, exception, getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                }
                JSONObject jsonObject = new JSONObject(configContent);
                if (!configFile.exists() || null == configContent || null == javaPath || !new File(javaPath).exists()) {
                    Object[] options = new Object[]{getString("MENU_SETTINGS_NAME"), getString("DIALOG_BUTTON_CANCEL_TEXT")};
                    int optionSelected = JOptionPane.showOptionDialog(
                            frame,
                            getString("MESSAGE_NOT_FOUND_JAVA"),
                            getString("DIALOG_TITLE_NOTICE"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    if (optionSelected >= 0) {
                        if (optionSelected == 0) {
                            //Settings settings = new Settings(frame, true);
                            //settings.setVisible(true);
                            Settings.showIt(frame, true, true);
                        }
                    }
                } else {
                    String selected = (String) versionChooser.getSelectedItem();
                    File versionsFolder = new File(gameDir, "versions");
                    File versionFolder = new File(versionsFolder, selected);
                    File versionJarFile = new File(versionFolder, selected + ".jar");
                    File versionJsonFile = new File(versionFolder, selected + ".json");
                    try {
                        String at = "0", uu = null;
                        if (jsonObject.optInt("loginMethod") > 0) {
                            at = jsonObject.optString("accessToken", "0");
                            uu = jsonObject.optString("uuid", null);
                        }
                        copyText(getMinecraftLaunchCommand(versionJarFile,
                                versionJsonFile,
                                gameDir,
                                assetsDir,
                                respackDir,
                                jsonObject.optString("playerName", "XPlayer"),
                                jsonObject.optString("javaPath", Utils.getDefaultJavaPath()),
                                jsonObject.optInt("maxMemory", 1024),
                                128,
                                jsonObject.optInt("windowSizeWidth", 854),
                                jsonObject.optInt("windowSizeHeight", 480),
                                jsonObject.optBoolean("isFullscreen"),
                                at,
                                uu,
                                false,
                                !jsonObject.optBoolean("isFullscreen")));
                    } catch (EmptyNativesException ex) {
                        ex.printStackTrace();
                        int se = JOptionPane.showConfirmDialog(frame, getString("DIALOG_NOT_FOUND_NATIVES_MESSAGE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null);
                        if (se == JOptionPane.YES_OPTION)
                            NativesReDownloader.reDownload(frame, versionFolder, ex.libraries);
                    } catch (LibraryDefectException ex) {
                        ex.printStackTrace();
                        defectLibrary(frame, ex.list);
                    } catch (LaunchException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, ex, getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JMenuItem manageMenu = new JMenuItem(getString("MENU_MANAGE"));
        manageMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VersionsManager.showDialog(frame, versionsDir);
            }
        });

        gameMenu.add(killMc);
        gameMenu.add(copyCommand);
        versionMenu.add(installNewVersionMenu);
        versionMenu.add(manageMenu);
        launcherMenu.add(settingsMenu);
        launcherMenu.add(aboutMenu);
        launcherMenu.add(exitMenu);
        menuBar.add(gameMenu);
        menuBar.add(versionMenu);
        menuBar.add(launcherMenu);
        frame.setJMenuBar(menuBar);

        frame.setResizable(false);

        //versionChooser:startGame 8:7
        versionChooser.setBounds(10, 10, /*200*//*250*/(frameWidth - 50) / 2, 30);
        startGame.setBounds(20 + (frameWidth - 50) / 2, 10, /*175*//*220*/(frameWidth - 50) / 2 + 5, 30);
        startGame.setFont(new Font(null, Font.BOLD, 17));
        versionChooser.setFont(new Font(null, Font.PLAIN, 15));
        log.setBounds(10, 50, 594, 402);
        log.setEditable(false);
        log.setFont(new Font(null, Font.BOLD, 12));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);

        JScrollPane jsp = new JScrollPane(log);
        jsp.setBounds(10, 50, 594, 402);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(startGame);
        frame.add(versionChooser);
        frame.add(jsp);


        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (versionChooser.getModel().getSelectedItem() != null) {
                    try {
                        configContent = Utils.readFileContent(configFile);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(frame, exception, getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                    }
                    JSONObject jsonObject = new JSONObject(configContent);
                    if (!configFile.exists() || null == configContent || null == javaPath || !new File(javaPath).exists()) {
                        Object[] options = new Object[]{getString("MENU_SETTINGS_NAME"), getString("DIALOG_BUTTON_CANCEL_TEXT")};
                        int optionSelected = JOptionPane.showOptionDialog(
                                frame,
                                getString("MESSAGE_NOT_FOUND_JAVA"),
                                getString("DIALOG_TITLE_NOTICE"),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE,
                                null,
                                options,
                                options[0]
                        );

                        if (optionSelected >= 0) {
                            if (optionSelected == 0) {
                                //Settings settings = new Settings(frame, true);
                                //settings.setVisible(true);
                                Settings.showIt(frame, true, true);
                            }
                        }
                    } else {
                        String selected = (String) versionChooser.getSelectedItem();
                        File versionsFolder = new File(gameDir, "versions");
                        File versionFolder = new File(versionsFolder, selected);
                        File versionJarFile = new File(versionFolder, selected + ".jar");
                        File versionJsonFile = new File(versionFolder, selected + ".json");
                        try {
                            String at = "0", uu = null;
                            if (jsonObject.optInt("loginMethod") > 0) {
                                at = jsonObject.optString("accessToken", "0");
                                uu = jsonObject.optString("uuid", null);
                            }
                            runningMc = launchMinecraft(
                                    versionJarFile,
                                    versionJsonFile,
                                    gameDir,
                                    assetsDir,
                                    respackDir,
                                    jsonObject.optString("playerName", "XPlayer"),
                                    jsonObject.optString("javaPath", Utils.getDefaultJavaPath()),
                                    jsonObject.optInt("maxMemory", 1024),
                                    128,
                                    jsonObject.optInt("windowSizeWidth", 854),
                                    jsonObject.optInt("windowSizeHeight", 480),
                                    jsonObject.optBoolean("isFullscreen"),
                                    at,
                                    uu,
                                    log,
                                    false,
                                    !jsonObject.optBoolean("isFullscreen"));
                            startGame.setEnabled(false);
                            versionChooser.setEnabled(false);

                            final GameCrashError[] crashError = {null};
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    BufferedReader dis = new BufferedReader(new InputStreamReader(runningMc.getInputStream()));
                                    String line;
                                    try {
                                        while ((line = dis.readLine()) != null) {
                                            if (line.contains("cannot be cast to class java.net.URLClassLoader"))
                                                crashError[0] = GameCrashError.URLClassLoader;//旧版本Minecraft的Java版本过高问题，报Exception in thread "main" java.lang.ClassCastException: class jdk.internal.loader.ClassLoaders$AppClassLoader cannot be cast to class java.net.URLClassLoader，因为在Java9对相关代码进行了修改，所以要用Java8及更旧
                                            else if (line.contains("Failed to load a library. Possible solutions:"))
                                                crashError[0] = GameCrashError.LWJGLFailedLoad;
                                            else if (line.contains("java.lang.OutOfMemoryError: Java heap space") || line.contains("Too small maximum heap"))
                                                crashError[0] = GameCrashError.MemoryTooSmall;
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
                                        startGame.setEnabled(true);
                                        versionChooser.setEnabled(true);
                                        log.setText(log.getText() + getString("MESSAGE_FINISHED_GAME"));
                                        if (crashError[0] != null) {
                                            log.setText(log.getText() + "\n\n" + String.format(getString("MESSAGE_GAME_CRASH_CAUSE_TIPS"), crashError[0].cause));
                                        }
                                        Document doc = log.getDocument();
                                        log.setCaretPosition(doc.getLength());
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (EmptyNativesException ex) {
                            ex.printStackTrace();
                            int se = JOptionPane.showConfirmDialog(frame, getString("DIALOG_NOT_FOUND_NATIVES_MESSAGE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null);
                            if (se == JOptionPane.YES_OPTION)
                                NativesReDownloader.reDownload(frame, versionFolder, ex.libraries);
                            startGame.setEnabled(true);
                            versionChooser.setEnabled(true);
                            log.setText(log.getText() + getString("MESSAGE_FINISHED_GAME"));
                        } catch (LibraryDefectException ex) {
                            ex.printStackTrace();
                            defectLibrary(frame, ex.list);
                            startGame.setEnabled(true);
                            versionChooser.setEnabled(true);
                            log.setText(log.getText() + getString("MESSAGE_FINISHED_GAME"));
                        } catch (LaunchException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, ex.getMessage(), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                            startGame.setEnabled(true);
                            versionChooser.setEnabled(true);
                            log.setText(log.getText() + getString("MESSAGE_FINISHED_GAME"));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, ex, getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                            startGame.setEnabled(true);
                            log.setText(log.getText() + getString("MESSAGE_FINISHED_GAME"));
                        }
                    }
                } else {
                    startGame.setEnabled(false);
                }
            }
        });

        /*if (!gameDir.exists()) {
            Object[] options = new Object[]{getString("MENU_SETTINGS_NAME"), getString("DIALOG_BUTTON_EXIT_TEXT")};

            int optionSelected = JOptionPane.showOptionDialog(
                    frame,
                    getString("DIALOG_NO_MINECRAFT_DIR_TEXT"),
                    getString("DIALOG_TITLE_NOTICE"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (optionSelected >= 0) {
                if (optionSelected == 0) {
                                //Settings settings = new Settings(frame, true);
                                //settings.setVisible(true);
                                Settings.showIt(frame, true);
                } else if (optionSelected == 1) {
                    System.exit(0);
                }
            }
        }*/
        /*if (gameDir.getAbsolutePath().endsWith("/") || gameDir.getAbsolutePath().endsWith("\\")) {
            versionsDir = new File(gameDir.getAbsolutePath() + "versions");
        } else {
            //versionsDir=new File(gameDir.getAbsolutePath()+File.separator+"versions");
            if (gameDir.getAbsolutePath().lastIndexOf("\\") != -1) {
                versionsDir = new File(gameDir.getAbsolutePath() + "\\" + "versions");
            } else {
                versionsDir = new File(gameDir.getAbsolutePath() + "/" + "versions");
            }
        }*/
        versionsDir = new File(gameDir/*.getAbsolutePath() + "/" +*/, "versions");

        updateVersions(startConfig);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void defectLibrary(JFrame frame, List<Library> list) {
        Object[] options = {getString("DIALOG_SOME_LIBRARIES_NOT_FOUND_SEE"), getString("DIALOG_BUTTON_CANCEL_TEXT"), getString("DIALOG_SOME_LIBRARIES_NOT_FOUND_DOWNLOAD")};

        int select = JOptionPane.showOptionDialog(frame,
                getString("DIALOG_SOME_LIBRARIES_NOT_FOUND_MESSAGE"),
                getString("DIALOG_TITLE_NOTICE"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[2]);
        if (select == 0) {
            JTextArea label = new JTextArea();
            label.setEditable(false);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i).libraryJSONObject.optString("name");
                sb.append(s);
                if (i + 1 != list.size()) {
                    sb.append('\n');
                }
            }
            label.setText(sb.toString());
            label.setBounds(10, 40, 215, 70);
            label.setEditable(false);
            label.setFont(new Font(null, Font.PLAIN, 12));
            JScrollPane jsp = new JScrollPane(label);
            jsp.setBounds(10, 40, 215, 70);
            //jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            JDialog dialog = new JDialog(frame, true);
            dialog.setTitle(getString("DIALOG_SOME_LIBRARIES_NOT_FOUND_SEE_TITLE"));
            int width = 500;
            int height = 400;
            Point point = SwingUtils.getCenterLocation(width, height);
            dialog.setBounds(point.x, point.y, width, height);
            dialog.setMaximumSize(new Dimension(700, 560));
            dialog.setMinimumSize(new Dimension(width, height));
            dialog.add(jsp);
            dialog.setVisible(true);
            defectLibrary(frame, list);
        } else if (select == 2) {
            JProgressBar progressBar = createProgressBar();
            JTextArea textArea = createTextArea();
            UnExitableDialog dialog = createDownloadDialog(frame, progressBar, textArea, getString("DIALOG_DOWNLOAD_LIBRARIES"));
            dialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //downloadFile(url, jarFile, progressBar);
                                File librariesDir = new File(gameDir, "libraries");
                                //System.out.println(assetsDir.getAbsolutePath());
                                //System.out.println(assetsDir.getAbsolutePath());
                                librariesDir.mkdirs();

                                addLog("DownloadDefectLibrary", textArea, getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"));
                                if (list != null) {
                                    for (Library library : list) {
                                        JSONObject jsonObject = library.libraryJSONObject;
                                        if (jsonObject != null) {
                                            boolean meet = true;
                                            JSONArray rules = jsonObject.optJSONArray("rules");
                                            if (rules != null) {
                                                meet = MinecraftLauncher.isMeetConditions(rules, false, false);
                                            }
                                            //System.out.println(meet);

                                            JSONObject downloadsJo = jsonObject.optJSONObject("downloads");
                                            if (meet && downloadsJo != null) {
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
                                                                String text = String.format(getString("MESSAGE_DOWNLOADING_FILE"), url.substring(url.lastIndexOf("/") + 1));
                                                                //print("DownloadDefectLibrary",text);
                                                                addLog("DownloadDefectLibrary", textArea, text);
                                                                downloadFile(url, file, progressBar);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            addLog("DownloadDefectLibrary", textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARY"), url, e));
                                                        }
                                                    }
                                                }


                                            }
                                        }
                                    }
                                    addLog("DownloadDefectLibrary", textArea, getString("MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"));
                                } else {
                                    addLog("DownloadDefectLibrary", textArea, getString("MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"));
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                addLog("DownloadDefectLibrary", textArea, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES"), e));
                                JOptionPane.showMessageDialog(frame, String.format(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES"), e), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                            }
                            progressBar.setValue(progressBar.getMaximum());
                            dialog.setExitable(true);
                            //dialog.dispose();
                        }
                    }).start();
                }
            });
            dialog.setVisible(true);

        }
    }

    private static JSONObject initConfig() {
        if (configFile.exists()) {
            try {
                configContent = Utils.readFileContent(configFile);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(configContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                javaPath = jsonObject.optString("javaPath");
                if (jsonObject.optBoolean("customWorkPaths")) {

                    gameDir = new File(!isEmpty(jsonObject.optString("gameDir")) ? jsonObject.optString("gameDir") : ".minecraft");
                    assetsDir = !isEmpty(jsonObject.optString("assetsDir")) ? new File(jsonObject.optString("assetsDir")) : new File(gameDir, "assets");
                    respackDir = !isEmpty(jsonObject.optString("resourcesDir")) ? new File(jsonObject.optString("resourcesDir")) : new File(gameDir, "resourcepacks");
                    //datapackDir = !isEmpty(jsonObject.optString("dataDir")) ? new File(jsonObject.optString("dataDir")) : new File(gameDir, "datapacks");
                    smDir = !isEmpty(jsonObject.optString("simpleModsDir")) ? new File(jsonObject.optString("simpleModsDir")) : new File(gameDir, "simplemods");

                } else {
                    initDefaultDirs();
                }
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            initDefaultDirs();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("language", Locale.getDefault().getLanguage());
            jsonObject.put("playerName", "XPlayer");
            jsonObject.put("javaPath", javaPath = Utils.getDefaultJavaPath());
            jsonObject.put("maxMemory", 1024);
            jsonObject.put("windowSizeWidth", 854);
            jsonObject.put("windowSizeHeight", 480);
            configContent = jsonObject.toString();
            try {
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile, false);
                writer.write(jsonObject.toString(Settings.INDENT_FACTOR));
                writer.close();
            } catch (IOException E) {
                E.printStackTrace();
            }
            return jsonObject;
        }
        return null;
    }

    public static void updateVersions(JSONObject config) {
        String[] strArray = Utils.listVersions(versionsDir);
        ComboBoxModel<String> spinnerListModel = new DefaultComboBoxModel<>(strArray);
        versionChooser.setModel(spinnerListModel);
        startGame.setEnabled(versionChooser.getSelectedItem() != null);
        versionChooser.addItemListener(e -> {
            boolean notnul = versionChooser.getSelectedItem() != null;
            startGame.setEnabled(notnul);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(configContent);
            } catch (Exception e3) {
                e3.printStackTrace();
                jsonObject = new JSONObject();
            }
            jsonObject.put("selectedVersion", notnul ? String.valueOf(versionChooser.getSelectedItem()) : "");
            Utils.saveConfig(jsonObject);
        });
        if (config == null) {
            config = Utils.getConfig();
        }
        String sv = config.optString("selectedVersion");
        boolean did = false;
        if (!Utils.isEmpty(sv)) {
            for (int i = 0; i < strArray.length; i++) {
                if (sv.equals(strArray[i])) {
                    did = true;
                    versionChooser.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (!did) {
            config.put("selectedVersion", versionChooser.getSelectedItem() != null ? String.valueOf(versionChooser.getSelectedItem()) : "");
            Utils.saveConfig(config);
        }
    }


    public static void initDefaultDirs() {
        gameDir = new File(".minecraft");
        assetsDir = new File(gameDir, "assets");
        respackDir = new File(gameDir, "resourcepacks");
        //datapackDir = new File(gameDir, "datapacks");
        smDir = new File(gameDir, "simplemods");
        versionsDir = new File(gameDir, "versions");
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

    public static void downloadFile(String url, File to) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(to);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
        fileOutputStream.close();
    }

    public static void downloadFile(String urla, File to, @Nullable JProgressBar progressBar) throws IOException {
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
        fos.close();
        in.close();
    }

    /*public static void downloadFile(String urla, File to, @Nullable JProgressBar progressBar, @Nullable JTextArea textArea) throws IOException {
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
        fos.close();
        in.close();
    }*/


    public static String showInputNameDialog(Component parent, String defaultName, String message) {
        String ret = (String) JOptionPane.showInputDialog(parent, message, getString("MENU_INSTALL_NEW_VERSION"), JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
        if (ret != null) {
            if (ret.length() == 0) {
                return showInputNameDialog(parent, ret, getString("MESSAGE_INSTALL_INPUT_NAME_IS_EMPTY"));
            }
            if (versionsDir.exists()) {
                String[] versions = versionsDir.list();
                if (versions != null && versions.length != 0) {
                    if (Arrays.asList(versions).contains(ret)) {
                        return showInputNameDialog(parent, ret, String.format(getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS"), ret));
                    }
                }
            }
        }
        return ret;
    }

    public static boolean isEmpty(String s) {
        return null == s || s.length() == 0;
    }


    public static void addLog(String moduleName, JTextArea textArea, String message) {
        print(moduleName, message);
        if (textArea != null) {
            textArea.setText(textArea.getText() + "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" + message + "\n");

            Document doc = textArea.getDocument();
            textArea.setCaretPosition(doc.getLength());
        }
    }

    public static void unZip(File srcFile, File to, JProgressBar progressBar) throws IOException {
        int BUFFER_SIZE = 2048;
        if (srcFile != null && srcFile.exists()) {
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

    public static String getString(String name) {
        if (getLanguage().equals("zh")) {
            if (null == zhCNText) {
                try {
                    InputStream inputStream = MinecraftLauncherX.class.getResourceAsStream("/texts/zh_cn.json");
                    BufferedReader reader;
                    StringBuilder sbf = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String tempStr;
                    while ((tempStr = reader.readLine()) != null) {
                        sbf.append(tempStr);
                    }
                    reader.close();
                    JSONObject j = Utils.parseJSONObject(sbf.toString());
                    if (j != null) zhCNText = j;
                    else zhCNText = new JSONObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String text = zhCNText.optString(name);
            if (!Utils.isEmpty(text)) return text;
        }
        if (null == enUSText) {
            try {
                InputStream inputStream = MinecraftLauncherX.class.getResourceAsStream("/texts/en_us.json");
                BufferedReader reader;
                StringBuilder sbf = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    sbf.append(tempStr);
                }
                reader.close();
                JSONObject j = Utils.parseJSONObject(sbf.toString());
                if (j != null) enUSText = j;
                else enUSText = new JSONObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return enUSText.optString(name, name);
    }

    public static String getLanguage() {
        if (isEmpty(language)) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(configContent);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject = new JSONObject();
            }
            String lang = jsonObject.optString("language");
            if (isEmpty(lang)) {
                jsonObject.put("language", language = Locale.getDefault().getLanguage());
                Utils.saveConfig(jsonObject);
            } else {
                language = lang;
            }
        }
        return language;
    }

    public static void copyText(String text) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(text);
        clip.setContents(tText, null);
    }

    public static void errorDialog(Component frame, String message, String titleIfEmptyNotice) {
        JOptionPane.showMessageDialog(frame, message, titleIfEmptyNotice != null ? titleIfEmptyNotice : getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
    }

    public static void print(Class<?> clazz, String content) {
        print(clazz.getSimpleName(), content);
    }

    public static void print(String moduleName, String content) {
        String base = "[%s|%s]%s\n";

        Date now = new Date(); // 创建一个Date对象，获取当前时间
        // 指定格式化格式
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        String time = f.format(now);


        System.out.printf(base, time, moduleName, content);
    }

}