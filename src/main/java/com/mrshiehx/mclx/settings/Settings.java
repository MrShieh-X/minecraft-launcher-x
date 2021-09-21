package com.mrshiehx.mclx.settings;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.modules.account.loginner.MicrosoftAccountLoginner;
import com.mrshiehx.mclx.modules.account.skin.SkinChanger;
import com.mrshiehx.mclx.modules.account.skin.SkinDownloader;
import com.mrshiehx.mclx.modules.account.skin.SkinResetter;
import com.mrshiehx.mclx.swing.documents.NumberLenghtLimitedDmt;
import com.mrshiehx.mclx.utils.Utils;
import com.mrshiehx.mclx.microsoft.MicrosoftAuthenticationServer;
import com.sun.management.OperatingSystemMXBean;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.UUID;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;
import static com.mrshiehx.mclx.utils.Utils.post;
import static java.nio.charset.StandardCharsets.UTF_8;
import static com.mrshiehx.mclx.modules.account.loginner.MicrosoftAccountLoginner.*;
import static com.mrshiehx.mclx.modules.account.loginner.MojangAccountLoginner.*;

public class Settings extends JDialog {
    JLabel playernameLabel = new JLabel();
    JLabel maxMemoryLabel = new JLabel();
    JLabel memoryUnitLabel = new JLabel();
    JLabel javaPathLabel = new JLabel();
    JLabel gameWindowSizeLabel = new JLabel();
    JLabel customGameDir = new JLabel();
    JLabel customAssetsDirL = new JLabel();
    JLabel customResourcePackDirLabel = new JLabel();
    JLabel customDataPackDirLabel = new JLabel();
    JLabel customSMDirLabel = new JLabel();
    JLabel osMax = new JLabel();
    JCheckBox fullscreen = new JCheckBox();
    JCheckBox loadSM = new JCheckBox();
    JCheckBox customWorkPaths = new JCheckBox();
    JButton whatIsSM = new JButton();
    JButton browse = new JButton();
    JButton save = new JButton();
    JButton cancel = new JButton();
    JButton browseGameDir = new JButton();
    JButton browseAssetsDir = new JButton();
    JButton browseResourcePackDir = new JButton();
    JButton browseDataPackDir = new JButton();
    JButton browseSMDir = new JButton();
    JButton loginOrOut = new JButton();
    public JComponent playerName;
    JTextField maxMemory = new JTextField();
    JTextField javaPath = new JTextField();
    JTextField windowSizeWidth = new JTextField();
    JTextField windowSizeHeight = new JTextField();
    JTextField smPath = new JTextField();
    JTextField gameDir = new JTextField();
    JTextField assetsDir = new JTextField();
    JTextField resourcePackDir = new JTextField();
    JTextField dataPackDir = new JTextField();
    JLabel widthHeightX = new JLabel();
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu(getString("SETTINGS_MENU_OFFICIAL_ACCOUNT_NAME"));

    public static File configFile = new File("mclx.json");
    //static String configContent;

    private static Settings instance;

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
        customDataPackDirLabel.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_DATA_PACK_DIR_TIP_TEXT"));
        customSMDirLabel.setText(getString("SETTINGS_TEXTFIELD_CUSTOM_SM_DIR_TIP_TEXT"));
        fullscreen.setText(getString("SETTINGS_CHECKBOX_FULLSCREEN_TEXT"));
        loadSM.setText(getString("SETTINGS_CHECKBOX_LOAD_SM_TEXT"));
        customWorkPaths.setText(getString("SETTINGS_CHECKBOX_CUSTOM_WORK_PATHS_TEXT"));
        whatIsSM.setText(getString("SETTINGS_BUTTON_WHAT_TEXT"));
        browse.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseGameDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseAssetsDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseResourcePackDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
        browseDataPackDir.setText(getString("SETTINGS_BUTTON_BROWSE_TEXT"));
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

        JMenuItem refreshOA = new JMenuItem(getString("SETTINGS_BUTTON_REFRESH_OFFICIAL_ACCOUNT_TEXT"));
        JMenuItem downloadSkin = new JMenuItem(getString("SETTINGS_MENU_DOWNLOAD_SKIN"));
        JMenuItem changeSkin = new JMenuItem(getString("SETTINGS_MENU_CHANGE_SKIN"));
        JMenuItem resetSkin = new JMenuItem(getString("SETTINGS_MENU_RESET_SKIN"));
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
        menu.add(refreshOA);
        menu.add(downloadSkin);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(configContent);
        } catch (JSONException ee) {
            ee.printStackTrace();
        }
        if(jsonObject.optInt("lm")==1) {
            menu.add(changeSkin);
            menu.add(resetSkin);
        }
        menu.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(configContent);
                } catch (JSONException ee) {
                    ee.printStackTrace();
                }
                int lm=jsonObject.optInt("lm");
                boolean var = (lm > 0);
                refreshOA.setEnabled(var);
                downloadSkin.setEnabled(var);
                changeSkin.setVisible(lm==1);
                resetSkin.setVisible(lm==1);
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
        menuBar.add(menu);
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
                fileChooser.setDialogTitle(MinecraftLauncherX.isWindows() ? getString("DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE") : getString("DIALOG_CHOOSE_JAVA_FILE_TITLE"));
                fileChooser.setApproveButtonText(getString("DIALOG_BUTTON_YES_TEXT"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (MinecraftLauncherX.isWindows()) {
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
        fullscreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowSizeHeight.setEnabled(!fullscreen.isSelected());
                windowSizeWidth.setEnabled(!fullscreen.isSelected());
            }
        });
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

        int smSuffix=loadSM.getHeight()+(whatIsSM.getY()-(fullscreen.getY()+fullscreen.getHeight()))+5;

        customWorkPaths.setBounds(12, 185-smSuffix, 385, 15);
        customWorkPaths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCustomWorkPathsEnabled();
            }
        });


        customGameDir.setBounds(15, 215-smSuffix, 110, 15);
        gameDir.setBounds(140, 210-smSuffix, 150, 25);
        browseGameDir.setBounds(295, 210-smSuffix, 80, 25);
        setBrowseButtonListener(browseGameDir, gameDir, getString("DIALOG_CHOOSE_GAME_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT"));

        customAssetsDirL.setBounds(15, 245-smSuffix, 110, 15);
        assetsDir.setBounds(140, 240-smSuffix, 150, 25);
        browseAssetsDir.setBounds(295, 240-smSuffix, 80, 25);
        setBrowseButtonListener(browseAssetsDir, assetsDir, getString("DIALOG_CHOOSE_ASSETS_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT"));

        customResourcePackDirLabel.setBounds(15, 275-smSuffix, 110, 15);
        resourcePackDir.setBounds(140, 270-smSuffix, 150, 25);
        browseResourcePackDir.setBounds(295, 270-smSuffix, 80, 25);
        setBrowseButtonListener(browseResourcePackDir, resourcePackDir, getString("DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT"));

        customDataPackDirLabel.setBounds(15, 305-smSuffix, 110, 15);
        dataPackDir.setBounds(140, 300-smSuffix, 150, 25);
        browseDataPackDir.setBounds(295, 300-smSuffix, 80, 25);
        setBrowseButtonListener(browseDataPackDir, dataPackDir, getString("DIALOG_CHOOSE_DATA_PACK_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT"));

        customSMDirLabel.setBounds(15, 335-smSuffix, 110, 15);
        smPath.setBounds(140, 330-smSuffix, 150, 25);
        browseSMDir.setBounds(295, 330-smSuffix, 80, 25);
        setBrowseButtonListener(browseSMDir, smPath, getString("DIALOG_CHOOSE_SM_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_SM_DIR_TEXT"));

        cancel.setBounds(295, 360-smSuffix, 80, 25);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.this.setVisible(false);
                if (null == parent) {
                    System.exit(0);
                }
            }
        });

        save.setBounds(210, 360-smSuffix, 80, 25);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(readFileContent(configFile));
                } catch (Exception ee) {
                    jsonObject = new JSONObject();
                }

                if (!(jsonObject.optInt("lm") > 0)) {
                    jsonObject.put("pn", ((JTextField) playerName).getText());
                }
                jsonObject.put("mm", maxMemory.getText().length() != 0 ? Integer.parseInt(maxMemory.getText()) : 0);
                jsonObject.put("jp", javaPath.getText());
                jsonObject.put("fs", fullscreen.isSelected());
                jsonObject.put("ww", windowSizeWidth.getText().length() != 0 ? Integer.parseInt(windowSizeWidth.getText()) : 0);
                jsonObject.put("wh", windowSizeHeight.getText().length() != 0 ? Integer.parseInt(windowSizeHeight.getText()) : 0);
                jsonObject.put("ls", loadSM.isSelected());
                jsonObject.put("cw", customWorkPaths.isSelected());
                jsonObject.put("gd", gameDir.getText());
                jsonObject.put("ad", assetsDir.getText());
                jsonObject.put("rd", resourcePackDir.getText());
                jsonObject.put("dd", dataPackDir.getText());
                jsonObject.put("sd", smPath.getText());
                MinecraftLauncherX.configContent = jsonObject.toString();
                MinecraftLauncherX.javaPath = javaPath.getText();
                if (customWorkPaths.isSelected()) {
                    MinecraftLauncherX.gameDir = new File(gameDir.getText());
                    MinecraftLauncherX.assetsDir = !Utils.isEmpty(assetsDir.getText())?new File(assetsDir.getText()):new File(MinecraftLauncherX.gameDir, "assets");
                    MinecraftLauncherX.respackDir = !Utils.isEmpty(resourcePackDir.getText())?new File(resourcePackDir.getText()):new File(MinecraftLauncherX.gameDir, "resourcepacks");
                    MinecraftLauncherX.datapackDir = !Utils.isEmpty(dataPackDir.getText())?new File(dataPackDir.getText()):new File(MinecraftLauncherX.gameDir, "datapacks");
                    MinecraftLauncherX.smDir = !Utils.isEmpty(smPath.getText())?new File(smPath.getText()):new File(MinecraftLauncherX.gameDir, "simplemods");

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
                    MinecraftLauncherX.datapackDir = new File(MinecraftLauncherX.gameDir, "datapacks");
                    MinecraftLauncherX.smDir = new File(MinecraftLauncherX.gameDir, "simplemods");
                }
                MinecraftLauncherX.versionsDir = new File(MinecraftLauncherX.gameDir, "versions");
                MinecraftLauncherX.updateVersions();
                try {
                    if (!configFile.exists()) {
                        configFile.createNewFile();
                    }
                    FileWriter writer = new FileWriter(configFile, false);
                    writer.write(jsonObject.toString());
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
                switch (var.optInt("lm")) {
                    case 1:
                        try {
                            account = var.getString("ea");
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                        loginMojangAccount(Settings.this,account, account == null, var, getString("DIALOG_OFFICIAL_REFRESHED_TITLE"));
                        break;
                    case 2:
                        try {
                            JSONObject mcSecond = Utils.parseJSONObject(Utils.get("https://api.minecraftservices.com/minecraft/profile", var.optString("tt", "Bearer"), var.optString("at")));

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
                                    var.put("lm", 2);
                                    var.put("uu", mcSecond.optString("id"));
                                    var.put("pn", mcSecond.optString("name"));
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
                                    ((JLabel) playerName).setText(var.optString("pn"));
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

        this.setBounds(100, 100, 405, 455-smSuffix);
        this.add(maxMemoryLabel);
        this.add(memoryUnitLabel);
        this.add(javaPathLabel);
        this.add(gameWindowSizeLabel);
        this.add(customGameDir);
        this.add(customAssetsDirL);
        this.add(customResourcePackDirLabel);
        this.add(customDataPackDirLabel);
        this.add(customSMDirLabel);
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
        this.add(smPath);
        this.add(gameDir);
        this.add(assetsDir);
        this.add(resourcePackDir);
        this.add(dataPackDir);
        this.add(osMax);
        this.add(widthHeightX);
        this.add(browseGameDir);
        this.add(browseAssetsDir);
        this.add(browseResourcePackDir);
        this.add(browseDataPackDir);
        this.add(browseSMDir);
        this.add(cancel);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        int height = this.getHeight();
        int width = this.getWidth();
        setLocation(screenWidth - width / 2, screenHeight - height / 2);
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
        dataPackDir.setEnabled(customWorkPaths.isSelected());
        browseDataPackDir.setEnabled(customWorkPaths.isSelected());
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
                configContent = MinecraftLauncherX.readFileContent(configFile);
                jsonObject = new JSONObject(configContent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance.loadAccount(jsonObject, true);
        JComponent component = instance.playerName;
        if ((jsonObject.optInt("lm") > 0)) {
            ((JLabel) component).setText(jsonObject.optString("pn"));
        } else {
            ((JTextField) component).setText(jsonObject.optString("pn"));
        }
        instance.javaPath.setText(jsonObject.optString("jp"));
        instance.maxMemory.setText(String.valueOf(jsonObject.optInt("mm")));
        instance.windowSizeWidth.setText(String.valueOf(jsonObject.optInt("ww")));
        instance.windowSizeHeight.setText(String.valueOf(jsonObject.optInt("wh")));
        instance.fullscreen.setSelected(jsonObject.optBoolean("fs"));
        instance.loadSM.setSelected(jsonObject.optBoolean("ls"));
        instance.customWorkPaths.setSelected(jsonObject.optBoolean("cw"));
        instance.gameDir.setText(jsonObject.optString("gd"));
        instance.assetsDir.setText(jsonObject.optString("ad"));
        instance.resourcePackDir.setText(jsonObject.optString("rd"));
        instance.dataPackDir.setText(jsonObject.optString("dd"));
        instance.smPath.setText(jsonObject.optString("sd"));

        instance.windowSizeWidth.setEnabled(!jsonObject.optBoolean("fs"));
        instance.windowSizeHeight.setEnabled(!jsonObject.optBoolean("fs"));
        instance.setCustomWorkPathsEnabled();

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
        boolean var = (jsonObject.optInt("lm") > 0);
        //refreshOA.setEnabled(var);
        if (var) {
            if (shouldAdd) {
                playerName = new JLabel();
                ((JLabel) playerName).setText(jsonObject.optString("pn"));
            }
            switch(jsonObject.optInt("lm")){
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
                        jsonObject.remove("lm"/*,"offline"*/);
                        jsonObject.remove("at");
                        jsonObject.remove("uu");
                        jsonObject.remove("ea");
                        MinecraftLauncherX.configContent = jsonObject.toString();
                        try {
                            FileWriter writer = new FileWriter(configFile, false);
                            writer.write(jsonObject.toString());
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        loadAccount(jsonObject, false);

                        remove(playerName);
                        playerName = new JTextField();
                        playerName.setBounds(140, 15, 135, 25);
                        add(playerName);
                        ((JTextField) playerName).setText(jsonObject.optString("pn"));

                        //((JTextField)playerName).setText(jsonObject.optString("pn"));
                    }
                }
            };
        } else {
            if (shouldAdd) {
                playerName = new JTextField();
                ((JTextField) playerName).setText(jsonObject.optString("pn"));
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
                            loginMojangAccount(Settings.this,null, true, jsonObject, getString("DIALOG_OFFICIAL_LOGINED_TITLE"));
                            break;
                        case 1:
                            MicrosoftAccountLoginner.loginMicrosoftAccount(Settings.this,jsonObject, getString("DIALOG_OFFICIAL_LOGINED_TITLE"));
                            break;
                    }

                }
            };
        }
        loginOrOut.addActionListener(actionListener);
        playerName.setBounds(140, 15, 135, 25);
        loginOrOut.setBorder(new EmptyBorder(0, 0, 0, 0));
        if (!MinecraftLauncherX.getLanguage().equals("zh")) {
            loginOrOut.setFont(new Font(null, Font.PLAIN, 9));
        }
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


    public static File selectSkin(Component f,String title) {
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
