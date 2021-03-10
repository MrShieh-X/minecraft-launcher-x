package com.mrshiehx.mclx;

import com.sun.management.OperatingSystemMXBean;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static com.mrshiehx.mclx.MCLX.*;
import static java.nio.charset.StandardCharsets.UTF_8;

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
    JComponent playerName;
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

    static File configFile = new File("mclx.json");
    //static String configContent;

    static Settings instance;

    public Settings(JFrame parent, boolean modal) {
        /*frame = new JFrame(getString("APPLICATION_NAME"));
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
        downloadSkin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = selectSkin(getString("DIALOG_DOWNLOAD_SKIN_FILE_TITLE"));
                if (file != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = new JSONObject(configContent);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    String uuid = jsonObject.getString("uu");
                    if (!isEmpty(uuid)) {
                        try {
                            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                            JSONObject result = new JSONObject(httpURLConnection2String((HttpURLConnection) url.openConnection()));
                            JSONArray properties = result.getJSONArray("properties");
                            for (int i = 0; i < properties.length(); i++) {
                                JSONObject jsonObject1 = properties.optJSONObject(i);
                                if (jsonObject1 != null) {
                                    if (jsonObject1.optString("name").equals("textures")) {
                                        JSONObject jsonObject2 = new JSONObject(new String(Base64.getDecoder().decode(jsonObject1.optString("value"))));
                                        JSONObject var = jsonObject2.optJSONObject("textures");
                                        if (var.has("SKIN")) {
                                            MCLX.downloadFile(var.optJSONObject("SKIN").optString("url"), file);
                                            JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_DOWNLOADED_SKIN_FILE_TEXT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_DOWNLOAD_SKIN_FILE_NOT_SET_TEXT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.WARNING_MESSAGE);
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            errorDialog(Settings.this, exception.toString(), null);
                        }
                    } else {
                        errorDialog(Settings.this, getString("MESSAGE_UUID_ACCESSTOKEN_EMPTY"), null);
                    }
                }
            }
        });
        changeSkin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = selectSkin(getString("DIALOG_CHANGE_SKIN_FILE_TITLE"));
                if (file != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = new JSONObject(configContent);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
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
                            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
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
                                    Settings.this,
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
                            copyTo(new FileInputStream(file), byteArrayOutputStream, new byte[8192]);
                            byteArrayOutputStream.write(sl);
                            byteArrayOutputStream.write(("--" + boundary + "--").getBytes(UTF_8));
                            connection.setRequestProperty("Content-Length", String.valueOf(byteArrayOutputStream.size()));
                            OutputStream outputStream = connection.getOutputStream();

                            outputStream.write(byteArrayOutputStream.toByteArray());

                            //System.out.println(byteArrayOutputStream.toString());

                            String var = httpURLConnection2String(connection);

                            if (!isEmpty(var) && var.startsWith("{")) {
                                JSONObject result = new JSONObject(var);
                                JOptionPane.showMessageDialog(Settings.this, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result.optString("error"), result.optString("errorMessage")), getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_CHANGED_SKIN_FILE_TEXT"), getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            errorDialog(Settings.this, exception.toString(), null);
                        }


                    } else {
                        errorDialog(Settings.this, getString("MESSAGE_UUID_ACCESSTOKEN_EMPTY"), null);
                    }
                }
            }
        });
        menu.add(refreshOA);
        menu.add(downloadSkin);
        menu.add(changeSkin);
        menu.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(configContent);
                } catch (JSONException ee) {
                    ee.printStackTrace();
                }
                boolean var = jsonObject.optBoolean("ol");
                refreshOA.setEnabled(var);
                downloadSkin.setEnabled(var);
                changeSkin.setEnabled(var);
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
                fileChooser.setDialogTitle(MCLX.isWindows() ? getString("DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE") : getString("DIALOG_CHOOSE_JAVA_FILE_TITLE"));
                fileChooser.setApproveButtonText(getString("DIALOG_BUTTON_YES_TEXT"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (MCLX.isWindows()) {
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

        customWorkPaths.setBounds(12, 185, 385, 15);
        customWorkPaths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCustomWorkPathsEnabled();
            }
        });


        customGameDir.setBounds(15, 215, 110, 15);
        gameDir.setBounds(140, 210, 150, 25);
        browseGameDir.setBounds(295, 210, 80, 25);
        setBrowseButtonListener(browseGameDir, gameDir, getString("DIALOG_CHOOSE_GAME_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT"));

        customAssetsDirL.setBounds(15, 245, 110, 15);
        assetsDir.setBounds(140, 240, 150, 25);
        browseAssetsDir.setBounds(295, 240, 80, 25);
        setBrowseButtonListener(browseAssetsDir, assetsDir, getString("DIALOG_CHOOSE_ASSETS_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT"));

        customResourcePackDirLabel.setBounds(15, 275, 110, 15);
        resourcePackDir.setBounds(140, 270, 150, 25);
        browseResourcePackDir.setBounds(295, 270, 80, 25);
        setBrowseButtonListener(browseResourcePackDir, resourcePackDir, getString("DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT"));

        customDataPackDirLabel.setBounds(15, 305, 110, 15);
        dataPackDir.setBounds(140, 300, 150, 25);
        browseDataPackDir.setBounds(295, 300, 80, 25);
        setBrowseButtonListener(browseDataPackDir, dataPackDir, getString("DIALOG_CHOOSE_DATA_PACK_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT"));

        customSMDirLabel.setBounds(15, 335, 110, 15);
        smPath.setBounds(140, 330, 150, 25);
        browseSMDir.setBounds(295, 330, 80, 25);
        setBrowseButtonListener(browseSMDir, smPath, getString("DIALOG_CHOOSE_SM_DIR_TITLE"), getString("DIALOG_CHOOSE_TYPE_SM_DIR_TEXT"));

        cancel.setBounds(295, 360, 80, 25);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.this.setVisible(false);
                if (null == parent) {
                    System.exit(0);
                }
            }
        });

        save.setBounds(210, 360, 80, 25);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(readFileContent(configFile));
                } catch (Exception ee) {
                    jsonObject = new JSONObject();
                }

                if (!jsonObject.optBoolean("ol")) {
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
                MCLX.configContent = jsonObject.toString();
                MCLX.javaPath = javaPath.getText();
                if (customWorkPaths.isSelected()) {
                    MCLX.gameDir = new File(gameDir.getText());
                    MCLX.assetsDir = new File(assetsDir.getText());
                    MCLX.respackDir = new File(resourcePackDir.getText());
                    MCLX.datapackDir = new File(dataPackDir.getText());
                    MCLX.smDir = new File(smPath.getText());

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
                    MCLX.gameDir = new File(".minecraft");
                    MCLX.assetsDir = new File(MCLX.gameDir, "assets");
                    MCLX.respackDir = new File(MCLX.gameDir, "resourcepacks");
                    MCLX.datapackDir = new File(MCLX.gameDir, "datapacks");
                    MCLX.smDir = new File(MCLX.gameDir, "simplemods");
                }
                MCLX.versionsDir = new File(MCLX.gameDir, "versions");
                MCLX.updateVersions();
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
                    var = new JSONObject(MCLX.configContent);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                try {
                    account = var.getString("ea");
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                loginAccount(account, account == null, var, getString("DIALOG_OFFICIAL_REFRESHED_TITLE"));
            }
        });

        this.setBounds(100, 100, 405, 455);
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
        this.add(loadSM);
        this.add(customWorkPaths);
        this.add(whatIsSM);
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
                configContent = MCLX.readFileContent(configFile);
                jsonObject = new JSONObject(configContent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance.loadAccount(jsonObject, true);
        JComponent component = instance.playerName;
        if (jsonObject.optBoolean("ol")) {
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


    private void loadAccount(JSONObject jsonObject, boolean shouldAdd) {
        if (shouldAdd) {
            if (playerName != null) this.remove(playerName);
            this.remove(playernameLabel);
            this.remove(loginOrOut);
        }
        for (ActionListener actionListener : loginOrOut.getActionListeners()) {
            loginOrOut.removeActionListener(actionListener);
        }
        ActionListener actionListener;
        boolean var = jsonObject.optBoolean("ol");
        //refreshOA.setEnabled(var);
        if (var) {
            if (shouldAdd) {
                playerName = new JLabel();
                ((JLabel) playerName).setText(jsonObject.optString("pn"));
            }
            playernameLabel.setText(getString("SETTINGS_TEXTFIELD_OFFICIAL_PLAYERNAME_TIP_TEXT"));
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
                        jsonObject.remove("ol"/*,"offline"*/);
                        jsonObject.remove("at");
                        jsonObject.remove("uu");
                        jsonObject.remove("ea");
                        MCLX.configContent = jsonObject.toString();
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
                    loginAccount(null, true, jsonObject, getString("DIALOG_OFFICIAL_LOGINED_TITLE"));
                }
            };
        }
        loginOrOut.addActionListener(actionListener);
        playerName.setBounds(140, 15, 135, 25);
        loginOrOut.setBorder(new EmptyBorder(0, 0, 0, 0));
        if (!MCLX.getLanguage().equals("zh")) {
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

    private void loginAccount(String account, boolean haveADialogToGetAccount, JSONObject jsonObject, String successText) {
        if (haveADialogToGetAccount || account == null) account = JOptionPane.showInputDialog(
                Settings.this,
                getString("DIALOG_OFFICIAL_LOGIN_TIP_ENTER_ACCOUNT_TEXT"),
                getString("SETTINGS_BUTTON_OFFICIAL_LOGIN_TEXT"),
                JOptionPane.QUESTION_MESSAGE
        );
        if (!isEmpty(account)) {
            String password = JOptionPane.showInputDialog(
                    Settings.this,
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
                        result = new JSONObject(httpURLConnection2String(con));
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                    if (result != null) {
                        if (result.has("error") || result.has("errorMessage")) {
                            String var = result.optString("errorMessage");
                            if (var.equals("Invalid credentials. Invalid username or password.")) {
                                JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_OFFICIAL_LOGIN_ERROR_INVALID_AOP_TEXT"), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(Settings.this, String.format(getString("DIALOG_OFFICIAL_LOGIN_FAILED_TEXT"), result.optString("error"), var), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JSONObject selectedProfileJo = result.optJSONObject("selectedProfile");
                            JSONObject userJo = result.optJSONObject("user");
                            if (userJo != null && !isEmpty(userJo.optString("username"))) {
                                account = userJo.optString("username");
                            }
                            jsonObject.put("ol", true);
                            jsonObject.put("ea", account);
                            jsonObject.put("at", result.optString("accessToken"));
                            jsonObject.put("uu", selectedProfileJo.optString("id"));
                            jsonObject.put("pn", selectedProfileJo.optString("name"));
                            MCLX.configContent = jsonObject.toString();
                            try {
                                FileWriter writer = new FileWriter(configFile, false);
                                writer.write(jsonObject.toString());
                                writer.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            //((JLabel) playerName).setText(selectedProfileJo.optString("name"));
                            loadAccount(jsonObject, false);

                            remove(playerName);
                            playerName = new JLabel();
                            playerName.setBounds(140, 15, 135, 25);
                            add(playerName);
                            ((JLabel) playerName).setText(jsonObject.optString("pn"));
                            JOptionPane.showMessageDialog(Settings.this, successText, getString("DIALOG_TITLE_NOTICE"), JOptionPane.INFORMATION_MESSAGE);

                        }
                    } else {
                        JOptionPane.showMessageDialog(Settings.this, getString("DIALOG_OFFICIAL_LOGIN_FAILED_NORESPONSE_TEXT"), getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Settings.this, ex, getString("DIALOG_OFFICIAL_LOGIN_FAILED_TITLE"), JOptionPane.ERROR_MESSAGE);

                }
            }
        }
    }

    private File selectSkin(String title) {
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

        result = fileChooser.showDialog(Settings.this, getString("DIALOG_BUTTON_YES_TEXT"));
        if (JFileChooser.APPROVE_OPTION == result) {
            file = fileChooser.getSelectedFile();
        }
        return file;
    }
}
