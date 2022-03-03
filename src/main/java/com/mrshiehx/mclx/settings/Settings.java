package com.mrshiehx.mclx.settings;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.modules.account.loginner.MicrosoftAccountLoginner;
import com.mrshiehx.mclx.modules.account.skin.SkinChanger;
import com.mrshiehx.mclx.modules.account.skin.SkinDownloader;
import com.mrshiehx.mclx.modules.account.skin.SkinResetter;
import com.mrshiehx.mclx.swing.documents.NumberLenghtLimitedDmt;
import com.mrshiehx.mclx.utils.SwingUtils;
import com.mrshiehx.mclx.utils.Utils;
import com.sun.management.OperatingSystemMXBean;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.Locale;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static com.mrshiehx.mclx.modules.account.loginner.MojangAccountLoginner.*;

public class Settings extends JDialog {
    private final JLabel playernameLabel = new JLabel();
    private final JLabel maxMemoryLabel = new JLabel();
    private final JLabel memoryUnitLabel = new JLabel();
    private final JLabel javaPathLabel = new JLabel();
    private final JLabel gameWindowSizeLabel = new JLabel();
    private final JLabel customGameDir = new JLabel();
    private final JLabel customAssetsDirL = new JLabel();
    private final JLabel customResourcePackDirLabel = new JLabel();
    //private final JLabel customDataPackDirLabel = new JLabel();
    private final JLabel customSMDirLabel = new JLabel();
    private final JLabel osMax = new JLabel();
    private final JCheckBox fullscreen = new JCheckBox();
    private final JCheckBox loadSM = new JCheckBox();
    private final JCheckBox customWorkPaths = new JCheckBox();
    private final JButton whatIsSM = new JButton();
    private final JButton browse = new JButton();
    private final JButton save = new JButton();
    private final JButton cancel = new JButton();
    private final JButton browseGameDir = new JButton();
    private final JButton browseAssetsDir = new JButton();
    private final JButton browseResourcePackDir = new JButton();
    //private final JButton browseDataPackDir = new JButton();
    private final JButton browseSMDir = new JButton();
    private final JButton loginOrOut = new JButton();
    public JComponent playerName;
    private final JTextField maxMemory = new JTextField();
    private final JTextField javaPath = new JTextField();
    private final JTextField windowSizeWidth = new JTextField();
    private final JTextField windowSizeHeight = new JTextField();
    private final JTextField smPath = new JTextField();
    private final JTextField gameDir = new JTextField();
    private final JTextField assetsDir = new JTextField();
    private final JTextField resourcePackDir = new JTextField();
    //private final JTextField dataPackDir = new JTextField();
    private final JLabel widthHeightX = new JLabel();
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu officialMenu = new JMenu(getString("SETTINGS_MENU_OFFICIAL_ACCOUNT_NAME"));
    private final JMenu languageMenu = new JMenu(getString("SETTINGS_MENU_LANGUAGE"));
    private final JMenuItem changeSkin, resetSkin, refreshOA, downloadSkin;
    private final JRadioButtonMenuItem english = new JRadioButtonMenuItem(getString("SETTINGS_MENU_LANGUAGE_ENGLISH"));
    private final JRadioButtonMenuItem simplifiedChinese = new JRadioButtonMenuItem(getString("SETTINGS_MENU_LANGUAGE_SIMPLIFIED_CHINESE"));

    public static final int INDENT_FACTOR = 2;//JsonObject转String的间隔
    public static File configFile = new File("mclx.json");
    //static String configContent;

    private static Settings instance;
    private String language;

    public Settings(JFrame parent, boolean modal) {
        /*frame = new JFrame(getString("APPLICATION_SHORT_NAME"));
        frame.setLayout(null);
        frame.setResizable(false);
        //frame.setVisible(true);
        frame.setBounds(124, 120, 295, 250);*/
        //JDialog this = this;//new JFrame(getString("BUTTON_SETTINGS_NAME"));
        super(parent, modal);
        this.setLayout(null);
        this.setResizable(false);
        this.setTitle(getString("MENU_SETTINGS_NAME"));
        //setFont(new Font(null,Font.PLAIN,6));
        maxMemoryLabel.setText(getString("SETTINGS_TEXTFIELD_MAX_MEMORY_TIP_TEXT"));
        javaPathLabel.setText(getString("SETTINGS_TEXTFIELD_JAVA_PATH_TIP_TEXT"));
        gameWindowSizeLabel.setText(getString("SETTINGS_TEXTFIELD_GAME_WINDOW_SIZE_TIP_TEXT"));
        customGameDir.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_GAME_DIR_TIP_TEXT"));
        customAssetsDirL.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_ASSETS_DIR_TIP_TEXT"));
        customResourcePackDirLabel.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_RESOURCE_PACK_DIR_TIP_TEXT"));
        //customDataPackDirLabel.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_DATA_PACK_DIR_TIP_TEXT"));
        customSMDirLabel.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_SM_DIR_TIP_TEXT"));
        fullscreen.setText(getString("SETTINGS_CHECKBOX_FULLSCREEN_TEXT"));
        loadSM.setText(getString("SETTINGS_CHECKBOX_LOAD_SM_TEXT"));
        customWorkPaths.setText(getString("SETTINGS_CHECKBOX_CUSTOM_WORK_PATHS_TEXT"));
        whatIsSM.setText(getString("SETTINGS_BUTTON_WHAT_TEXT"));
        browse.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseGameDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseAssetsDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseResourcePackDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        // browseDataPackDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseSMDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        save.setText(getString("SETTINGS_BUTTON_SAVE_TEXT"));
        cancel.setText(getString("DIALOG_BUTTON_CANCEL_TEXT"));
        memoryUnitLabel.setText("MB");
        widthHeightX.setText("x");
        //refreshOA.setText(getString("SETTINGS_BUTTON_REFRESH_OFFICIAL_ACCOUNT_TEXT"));

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;
        maxMemory.setDocument(new NumberLenghtLimitedDmt(Long.toString(physicalTotal).length()));
        windowSizeWidth.setDocument(new NumberLenghtLimitedDmt(5));
        windowSizeHeight.setDocument(new NumberLenghtLimitedDmt(5));

        playernameLabel.setBounds(15, 20, 110, 15);

        maxMemoryLabel.setBounds(15, 55, 110, 15);
        maxMemory.setBounds(140, 50, 65, 25);
        memoryUnitLabel.setBounds(210, 55, 30, 15);
        /*OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;*/
        osMax.setText(String.format(getString("SETTINGS_TEXTFIELD_OS_MAX_MEMORY_TIP_TEXT"), physicalTotal));
        osMax.setBounds(240, 55, 110, 15);

        refreshOA = new JMenuItem(getString("SETTINGS_BUTTON_REFRESH_OFFICIAL_ACCOUNT_TEXT"));
        downloadSkin = new JMenuItem(getString("SETTINGS_MENU_DOWNLOAD_SKIN"));
        changeSkin = new JMenuItem(getString("SETTINGS_MENU_CHANGE_SKIN"));
        resetSkin = new JMenuItem(getString("SETTINGS_MENU_RESET_SKIN"));
        resetSkin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SkinResetter.showDialog(Settings.this);
            }
        });
        downloadSkin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SkinDownloader.start(Settings.this);
            }
        });
        changeSkin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SkinChanger.start(Settings.this);
            }
        });

        officialMenu.add(refreshOA);
        officialMenu.add(downloadSkin);
        officialMenu.add(changeSkin);
        officialMenu.add(resetSkin);
        /*JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(configContent);
        } catch (JSONException ee) {
            ee.printStackTrace();
        }*/
        //if(jsonObject.optInt("loginMethod")==1) {
        //}
        /*int lm=jsonObject.optInt("loginMethod");
        changeSkin.setVisible(lm==1);
        resetSkin.setVisible(lm==1);*/

        officialMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(configContent);
                } catch (JSONException ee) {
                    ee.printStackTrace();
                }
                int lm = jsonObject.optInt("loginMethod");
                boolean var = (lm > 0);
                refreshOA.setEnabled(var);
                downloadSkin.setEnabled(var);
                changeSkin.setVisible(lm == 1);
                resetSkin.setVisible(lm == 1);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }

        });

        languageMenu.add(english);
        languageMenu.add(simplifiedChinese);


        english.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                language = "en";
            }
        });
        simplifiedChinese.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                language = "zh";
            }
        });


        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(english);
        btnGroup.add(simplifiedChinese);

        menuBar.add(officialMenu);
        menuBar.add(languageMenu);


        setJMenuBar(menuBar);

        javaPathLabel.setBounds(15, 90, 110, 15);
        javaPath.setBounds(140, 85,/*185*/150, 25);
        browse.setBounds(295, 85, 80, 25);
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result;
                String path;
                JFileChooser fileChooser = new JFileChooser();
                //FileSystemView fsv = FileSystemView.getFileSystemView();
                fileChooser.setCurrentDirectory(/*fsv.getHomeDirectory()*/new File("."));
                fileChooser.setDialogTitle(Utils.isWindows() ? getString("DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE") : getString("DIALOG_CHOOSE_JAVA_FILE_TITLE"));
                fileChooser.setApproveButtonText(getString("DIALOG_BUTTON_YES_TEXT"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (Utils.isWindows()) {
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            if (f.isDirectory()) {
                                return true;
                            } else {
                                return f.getName().toLowerCase().endsWith(".exe");
                            }
                        }

                        @Override
                        public String getDescription() {
                            return String.format(getString("SETTINGS_BROSE_FILE_TYPE_TEXT"), "EXE") + " (*.exe)";
                        }
                    });
                }
                result = fileChooser.showDialog(Settings.this, getString("DIALOG_BUTTON_YES_TEXT"));
                if (JFileChooser.APPROVE_OPTION == result) {
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    javaPath.setText(path);
                }

            }
        });

        fullscreen.setBounds(12, 125, 95, 15);

        gameWindowSizeLabel.setBounds(140, 125, 110, 15);
        windowSizeWidth.setBounds(265, 120, 50, 25);
        widthHeightX.setBounds(316, 125, 10, 15);
        windowSizeHeight.setBounds(325, 120, 50, 25);

        loadSM.setBounds(12, 155, 75, 15);
        loadSM.setEnabled(false);

        whatIsSM.setBounds(140, 150, 120, 25);
        whatIsSM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_WHAT_IS_SM_TEXT"), getString("SETTINGS_BUTTON_WHAT_TEXT"), JOptionPane.QUESTION_MESSAGE);
            }
        });

        int smSuffix = loadSM.getHeight()
                + (whatIsSM.getY() - (fullscreen.getY() + fullscreen.getHeight()))//间距
                + 5;

        customWorkPaths.setBounds(12, 185 - smSuffix, 385, 15);
        customWorkPaths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCustomWorkPathsEnabled();
            }
        });


        customGameDir.setBounds(15, 215 - smSuffix, 110, 15);
        gameDir.setBounds(140, 210 - smSuffix, 150, 25);
        browseGameDir.setBounds(295, 210 - smSuffix, 80, 25);
        setBrowseButtonListener(browseGameDir, gameDir, getString("DIALOG_CHOOSE_GAME_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT"));

        customAssetsDirL.setBounds(15, 245 - smSuffix, 110, 15);
        assetsDir.setBounds(140, 240 - smSuffix, 150, 25);
        browseAssetsDir.setBounds(295, 240 - smSuffix, 80, 25);
        setBrowseButtonListener(browseAssetsDir, assetsDir, getString("DIALOG_CHOOSE_ASSETS_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT"));

        customResourcePackDirLabel.setBounds(15, 275 - smSuffix, 110, 15);
        resourcePackDir.setBounds(140, 270 - smSuffix, 150, 25);
        browseResourcePackDir.setBounds(295, 270 - smSuffix, 80, 25);
        setBrowseButtonListener(browseResourcePackDir, resourcePackDir, getString("DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT"));

        /*customDataPackDirLabel.setBounds(15, 305-smSuffix, 110, 15);
        dataPackDir.setBounds(140, 300-smSuffix, 150, 25);
        browseDataPackDir.setBounds(295, 300-smSuffix, 80, 25);
        setBrowseButtonListener(browseDataPackDir, dataPackDir, getString("DIALOG_CHOOSE_DATA_PACK_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT"));
*/
        customSMDirLabel.setBounds(15, 335 - smSuffix, 110, 15);
        smPath.setBounds(140, 330 - smSuffix, 150, 25);
        browseSMDir.setBounds(295, 330 - smSuffix, 80, 25);
        setBrowseButtonListener(browseSMDir, smPath, getString("DIALOG_CHOOSE_SM_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_SM_DIR_TEXT"));

        int smPathSuffix = smPath.getHeight()
                + (smPath.getY() - (resourcePackDir.getY() + resourcePackDir.getHeight()))//间距
                ;

        cancel.setBounds(295, 360 - smSuffix - smPathSuffix, 80, 25);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.this.setVisible(false);
                if (null == parent) {
                    System.exit(0);
                }
            }
        });

        save.setBounds(210, 360 - smSuffix - smPathSuffix, 80, 25);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(Utils.readFileContent(configFile));
                } catch (Exception ee) {
                    jsonObject = new JSONObject();
                }

                if (!(jsonObject.optInt("loginMethod") > 0)) {
                    jsonObject.put("playerName", ((JTextField) playerName).getText());
                }
                jsonObject.put("maxMemory", maxMemory.getText().length() != 0 ? Integer.parseInt(maxMemory.getText()) : 0);
                jsonObject.put("javaPath", javaPath.getText());
                jsonObject.put("isFullscreen", fullscreen.isSelected());
                jsonObject.put("windowSizeWidth", windowSizeWidth.getText().length() != 0 ? Integer.parseInt(windowSizeWidth.getText()) : 0);
                jsonObject.put("windowSizeHeight", windowSizeHeight.getText().length() != 0 ? Integer.parseInt(windowSizeHeight.getText()) : 0);
                //jsonObject.put("loadSM", loadSM.isSelected());
                jsonObject.put("customWorkPaths", customWorkPaths.isSelected());
                jsonObject.put("gameDir", gameDir.getText());
                jsonObject.put("assetsDir", assetsDir.getText());
                jsonObject.put("resourcesDir", resourcePackDir.getText());
                //jsonObject.put("dataDir", dataPackDir.getText());
                jsonObject.put("language", language);
                //jsonObject.put("simpleModsDir", smPath.getText());
                MinecraftLauncherX.configContent = jsonObject.toString();
                MinecraftLauncherX.javaPath = javaPath.getText();
                if (customWorkPaths.isSelected()) {
                    MinecraftLauncherX.gameDir = new File(gameDir.getText());
                    MinecraftLauncherX.assetsDir = !Utils.isEmpty(assetsDir.getText()) ? new File(assetsDir.getText()) : new File(MinecraftLauncherX.gameDir, "assets");
                    MinecraftLauncherX.respackDir = !Utils.isEmpty(resourcePackDir.getText()) ? new File(resourcePackDir.getText()) : new File(MinecraftLauncherX.gameDir, "resourcepacks");
                    //MinecraftLauncherX.datapackDir = !Utils.isEmpty(dataPackDir.getText())?new File(dataPackDir.getText()):new File(MinecraftLauncherX.gameDir, "datapacks");
                    MinecraftLauncherX.smDir = !Utils.isEmpty(smPath.getText()) ? new File(smPath.getText()) : new File(MinecraftLauncherX.gameDir, "simplemods");

                    /*if(gameDir.getText().endsWith("/")||gameDir.getText().endsWith("\\")){
                        MCLX.versionsDir=new File(gameDir.getText()+"versions");
                    }else{
                        //versionsDir=new File(gameDir.getAbsolutePath()+File.separator+"versions");
                        if(gameDir.getText().lastIndexOf("\\")!=-1){
                        }else{
                            MCLX.versionsDir=new File(gameDir.getText()+"/"+"versions");
                        }
                    }*/

                } else {
                    MinecraftLauncherX.gameDir = new File(".minecraft");
                    MinecraftLauncherX.assetsDir = new File(MinecraftLauncherX.gameDir, "assets");
                    MinecraftLauncherX.respackDir = new File(MinecraftLauncherX.gameDir, "resourcepacks");
                    //MinecraftLauncherX.datapackDir = new File(MinecraftLauncherX.gameDir, "datapacks");
                    MinecraftLauncherX.smDir = new File(MinecraftLauncherX.gameDir, "simplemods");
                }
                MinecraftLauncherX.versionsDir = new File(MinecraftLauncherX.gameDir, "versions");
                MinecraftLauncherX.updateVersions(jsonObject);
                try {
                    if (!configFile.exists()) {
                        configFile.createNewFile();
                    }
                    FileWriter writer = new FileWriter(configFile, false);
                    writer.write(jsonObject.toString(Settings.INDENT_FACTOR));
                    writer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Settings.this.setVisible(false);
                if (null == parent) {
                    System.exit(0);
                }
            }
        });


        //refreshOA.setBounds(12, 360, 175, 25);
        //refreshOA.setBorder(new EmptyBorder(0,0,0,0));
        refreshOA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String account = null;
                JSONObject var = new JSONObject();
                try {
                    var = new JSONObject(MinecraftLauncherX.configContent);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                switch (var.optInt("loginMethod")) {
                    case 1:
                        try {
                            account = var.getString("ea");
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                        loginMojangAccount(Settings.this, account, account == null, var, getString("DIALOG_OFFICIAL_REFRESHED_TITLE"));
                        break;
                    case 2:
                        try {
                            JSONObject mcSecond = Utils.parseJSONObject(Utils.get("https://api.minecraftservices.com/minecraft/profile", var.optString("tokenType", "Bearer"), var.optString("accessToken")));

                            if (mcSecond != null) {

                                //stem.ut.println("microsoft_first : " + first);
                                //stem.ut.println("microsoft_second: " + second);
                                //stem.ut.println("xboxLive first  : " + xboxLive);
                                //stem.ut.println("xstsResult first: " + xstsResult);
                                //stem.ut.println("mc         first: " + mcFirst);
                                //stem.ut.println("mc        second: " + mcSecond);
                                if (mcSecond.has("error") || mcSecond.has("errorMessage")) {
                                    String var2 = mcSecond.optString("errorMessage");
                                    JOptionPane.showMessageDialog(Settings.this, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), mcSecond.optString("error"), var2), getString("DIALOG_OFFICIAL_REFRESHED_TITLE"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    var.put("loginMethod", 2);
                                    var.put("uuid", mcSecond.optString("id"));
                                    var.put("playerName", mcSecond.optString("name"));
                                    MinecraftLauncherX.configContent = var.toString();
                                    try {
                                        FileWriter writer = new FileWriter(configFile, false);
                                        writer.write(MinecraftLauncherX.configContent);
                                        writer.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    //((JLabel) playerName).setText(selectedProfileJo.optString("name"));
                                    loadAccount(var, false);

                                    remove(playerName);
                                    playerName = new JLabel();
                                    playerName.setBounds(140, 15, 135, 25);
                                    add(playerName);
                                    ((JLabel) playerName).setText(var.optString("playerName"));
                                    JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_OFFICIAL_REFRESHED_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);

                                    return;
                                }
                            } else {
                                //stem.ut.println(849);
                                JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_OFFICIAL_FAILED_REFRESH_TITLE"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ee) {
                            //stem.ut.println(849);
                            JOptionPane.showMessageDialog(Settings.this, ee, getString("DIALOG_OFFICIAL_FAILED_REFRESH_TITLE"), JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                }
            }
        });

        this.setBounds(100, 100, 405, 455 - smSuffix - smPathSuffix);
        this.add(maxMemoryLabel);
        this.add(memoryUnitLabel);
        this.add(javaPathLabel);
        this.add(gameWindowSizeLabel);
        this.add(customGameDir);
        this.add(customAssetsDirL);
        this.add(customResourcePackDirLabel);

        /*this.add(customDataPackDirLabel);
        this.add(dataPackDir);
        this.add(browseDataPackDir);*/



        /*this.add(customSMDirLabel);
        this.add(smPath);
        this.add(browseSMDir);*/


        this.add(fullscreen);
        //this.add(loadSM);
        this.add(customWorkPaths);
        //this.add(whatIsSM);
        this.add(browse);
        this.add(save);
        this.add(maxMemory);
        this.add(javaPath);
        this.add(windowSizeWidth);
        this.add(windowSizeHeight);
        this.add(gameDir);
        this.add(assetsDir);
        this.add(resourcePackDir);
        this.add(osMax);
        this.add(widthHeightX);
        this.add(browseGameDir);
        this.add(browseAssetsDir);
        this.add(browseResourcePackDir);
        this.add(cancel);


        setLocation(SwingUtils.getCenterLocation(getWidth(), getHeight()));
        //this.setVisible(true);

    }

    public void setBrowseButtonListener(JButton button, JTextField textField, String title, String typeText) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result;
                String path;
                JFileChooser fileChooser = new JFileChooser();
                //FileSystemView fsv = FileSystemView.getFileSystemView();
                fileChooser.setCurrentDirectory(new File("."));
                fileChooser.setDialogTitle(title);
                fileChooser.setApproveButtonText(getString("DIALOG_BUTTON_YES_TEXT"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return typeText;
                    }
                });
                result = fileChooser.showDialog(Settings.this, getString("DIALOG_BUTTON_YES_TEXT"));
                if (JFileChooser.APPROVE_OPTION == result) {
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    textField.setText(path);
                }
            }
        });
    }

    public void setCustomWorkPathsEnabled() {
        gameDir.setEnabled(customWorkPaths.isSelected());
        browseGameDir.setEnabled(customWorkPaths.isSelected());
        resourcePackDir.setEnabled(customWorkPaths.isSelected());
        browseResourcePackDir.setEnabled(customWorkPaths.isSelected());
        assetsDir.setEnabled(customWorkPaths.isSelected());
        browseAssetsDir.setEnabled(customWorkPaths.isSelected());
        //dataPackDir.setEnabled(customWorkPaths.isSelected());
        //browseDataPackDir.setEnabled(customWorkPaths.isSelected());
        smPath.setEnabled(customWorkPaths.isSelected());
        browseSMDir.setEnabled(customWorkPaths.isSelected());
    }

    public static void showIt(JFrame parent, boolean modal, boolean show) {
        if (instance == null) {
            instance = new Settings(parent, modal);
        }

        JSONObject jsonObject = new JSONObject();
        if (configFile.exists()) {
            try {
                configContent = Utils.readFileContent(configFile);
                jsonObject = new JSONObject(configContent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance.loadAccount(jsonObject, true);
        JComponent component = instance.playerName;
        boolean logged = jsonObject.optInt("loginMethod") > 0;
        if ((logged)) {
            ((JLabel) component).setText(jsonObject.optString("playerName"));
        } else {
            ((JTextField) component).setText(jsonObject.optString("playerName"));
        }


        instance.javaPath.setText(jsonObject.optString("javaPath"));
        instance.maxMemory.setText(String.valueOf(jsonObject.optInt("maxMemory")));
        instance.windowSizeWidth.setText(String.valueOf(jsonObject.optInt("windowSizeWidth")));
        instance.windowSizeHeight.setText(String.valueOf(jsonObject.optInt("windowSizeHeight")));
        instance.fullscreen.setSelected(jsonObject.optBoolean("isFullscreen"));
        //instance.loadSM.setSelected(jsonObject.optBoolean("loadSM"));
        instance.customWorkPaths.setSelected(jsonObject.optBoolean("customWorkPaths"));
        instance.gameDir.setText(jsonObject.optString("gameDir"));
        instance.assetsDir.setText(jsonObject.optString("assetsDir"));
        instance.resourcePackDir.setText(jsonObject.optString("resourcesDir"));
        //instance.dataPackDir.setText(jsonObject.optString("dataDir"));
        //instance.smPath.setText(jsonObject.optString("simpleModsDir"));

        //instance.windowSizeWidth.setEnabled(!jsonObject.optBoolean("isFullscreen"));
        //instance.windowSizeHeight.setEnabled(!jsonObject.optBoolean("isFullscreen"));
        instance.setCustomWorkPathsEnabled();

        String language;
        String lang = jsonObject.optString("language");
        if (isEmpty(lang)) {
            jsonObject.put("language", language = Locale.getDefault().getLanguage());
            Utils.saveConfig(jsonObject);
        } else {
            language = lang;
        }
        if ("zh".equals(language)) {
            instance.simplifiedChinese.setSelected(true);
        } else {
            instance.english.setSelected(true);
        }
        instance.language = jsonObject.optString("language");


        /*int lm=jsonObject.optInt("loginMethod");

        instance.refreshOA.setEnabled(logged);
        instance.downloadSkin.setEnabled(logged);
        instance.changeSkin.setVisible(lm==1);
        instance.resetSkin.setVisible(lm==1);*/

        if (show)
            instance.setVisible(true);
    }


    public void loadAccount(JSONObject jsonObject, boolean shouldAdd) {
        if (shouldAdd) {
            if (playerName != null) this.remove(playerName);
            this.remove(playernameLabel);
            this.remove(loginOrOut);
        }
        for (ActionListener actionListener : loginOrOut.getActionListeners()) {
            loginOrOut.removeActionListener(actionListener);
        }
        ActionListener actionListener;
        boolean var = (jsonObject.optInt("loginMethod") > 0);
        //refreshOA.setEnabled(var);
        if (var) {
            if (shouldAdd) {
                playerName = new JLabel();
                ((JLabel) playerName).setText(jsonObject.optString("playerName"));
            }
            switch (jsonObject.optInt("loginMethod")) {
                case 1:
                    playernameLabel.setText(getString("SETTINGS_TEXTFIELD_OFFICIAL_MOJANG_PLAYERNAME_TIP_TEXT"));
                    break;
                case 2:
                    playernameLabel.setText(getString("SETTINGS_TEXTFIELD_OFFICIAL_MICROSOFT_PLAYERNAME_TIP_TEXT"));
                    break;
                default:
                    playernameLabel.setText(getString("SETTINGS_TEXTFIELD_OFFICIAL_PLAYERNAME_TIP_TEXT"));
                    break;
            }
            loginOrOut.setText(getString("SETTINGS_BUTTON_OFFICIAL_LOGOUT_TEXT"));
            actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //getString("DIALOG_LOGOUT_MESSAGE");
                    int result = JOptionPane.showConfirmDialog(
                            Settings.this,
                            getString("DIALOG_LOGOUT_MESSAGE"),
                            getString("DIALOG_TITLE_NOTICE"),
                            JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        jsonObject.remove("loginMethod"/*,"offline"*/);
                        jsonObject.remove("accessToken");
                        jsonObject.remove("uuid");
                        jsonObject.remove("ea");
                        MinecraftLauncherX.configContent = jsonObject.toString();
                        try {
                            FileWriter writer = new FileWriter(configFile, false);
                            writer.write(jsonObject.toString(Settings.INDENT_FACTOR));
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        loadAccount(jsonObject, false);

                        remove(playerName);
                        playerName = new JTextField();
                        playerName.setBounds(140, 15, 135, 25);
                        add(playerName);
                        ((JTextField) playerName).setText(jsonObject.optString("playerName"));

                        //((JTextField)playerName).setText(jsonObject.optString("playerName"));
                    }
                }
            };
        } else {
            if (shouldAdd) {
                playerName = new JTextField();
                ((JTextField) playerName).setText(jsonObject.optString("playerName"));
            }
            playernameLabel.setText(getString("SETTINGS_TEXTFIELD_OFFLINE_PLAYERNAME_TIP_TEXT"));
            loginOrOut.setText(getString("SETTINGS_BUTTON_OFFICIAL_LOGIN_TEXT"));
            actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String[] models = new String[]{getString("LOGIN_METHOD_MOJANG"), getString("LOGIN_METHOD_MICROSOFT")};

                    String methodString = (String) JOptionPane.showInputDialog(
                            Settings.this,
                            getString("LOGIN_METHOD_MESSAGE"),
                            getString("DIALOG_TITLE_NOTICE"),
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            models,
                            models[0]
                    );

                    int method = -1;
                    if (!isEmpty(methodString) && methodString.equals(models[0])) {
                        method = 0;
                    } else if (!isEmpty(methodString) && methodString.equals(models[1])) {
                        method = 1;
                    }

                    switch (method) {
                        case 0:
                            loginMojangAccount(Settings.this, null, true, jsonObject, getString("DIALOG_OFFICIAL_LOGINED_TITLE"));
                            break;
                        case 1:
                            MicrosoftAccountLoginner.loginMicrosoftAccount(Settings.this, jsonObject, getString("DIALOG_OFFICIAL_LOGINED_TITLE"));
                            break;
                    }

                }
            };
        }
        loginOrOut.addActionListener(actionListener);
        playerName.setBounds(140, 15, 135, 25);
        /*loginOrOut.setBorder(new EmptyBorder(0, 0, 0, 0));
        if (!MinecraftLauncherX.getLanguage().equals("zh")) {
            loginOrOut.setFont(new Font(null, Font.PLAIN, 9));
        }*/
        loginOrOut.setBounds(280, 15, 95, 25);

        if (shouldAdd) {
            this.add(playerName);
            this.add(playernameLabel);
            this.add(loginOrOut);
        }
    }


    public static String httpURLConnection2String(HttpURLConnection con) throws IOException {
        try {
            try (InputStream stdout = con.getInputStream()) {
                return inputStream2String(stdout);
            }
        } catch (IOException e) {
            try (InputStream stderr = con.getErrorStream()) {
                if (stderr == null) throw e;
                return inputStream2String(stderr);
            }
        }
    }

    public static void copyTo(InputStream src, OutputStream dest, byte[] buf) throws IOException {
        while (true) {
            int len = src.read(buf);
            if (len == -1)
                break;
            dest.write(buf, 0, len);
        }
    }

    public static String inputStream2String(InputStream stream) throws IOException {
        try (InputStream is = stream) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while (true) {
                int len = is.read(buf);
                if (len == -1)
                    break;
                result.write(buf, 0, len);
            }
            return result.toString(UTF_8.name());
        }
    }


    public static File selectSkin(Component f, String title) {
        int result;
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        //FileSystemView fsv = FileSystemView.getFileSystemView();
        fileChooser.setCurrentDirectory(/*fsv.getHomeDirectory()*/new File("."));
        fileChooser.setDialogTitle(title);
        fileChooser.setApproveButtonText(getString("DIALOG_BUTTON_YES_TEXT"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".png");
                }
            }

            @Override
            public String getDescription() {
                return getString("DIALOG_DOWNLOAD_SKIN_FILE_TYPE_TEXT") + " (*.png)";
            }
        });

        result = fileChooser.showDialog(f, getString("DIALOG_BUTTON_YES_TEXT"));
        if (JFileChooser.APPROVE_OPTION == result) {
            file = fileChooser.getSelectedFile();
        }
        return file;
    }
}
