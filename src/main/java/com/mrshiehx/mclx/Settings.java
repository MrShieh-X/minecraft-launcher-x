package com.mrshiehx.mclx;

import com.sun.management.OperatingSystemMXBean;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.management.ManagementFactory;

public class Settings extends JDialog {
    static JLabel playernameLabel=new JLabel();
    static JLabel maxMemoryLabel=new JLabel();
    static JLabel memoryUnitLabel=new JLabel();
    static JLabel javaPathLabel=new JLabel();
    static JLabel gameWindowSizeLabel=new JLabel();
    static JLabel customGameDir=new JLabel();
    static JLabel customAssetsDirL=new JLabel();
    static JLabel customResourcePackDirLabel=new JLabel();
    static JLabel customDataPackDirLabel=new JLabel();
    static JLabel customSMDirLabel=new JLabel();
    static JLabel osMax=new JLabel();
    static JCheckBox fullscreen=new JCheckBox();
    static JCheckBox loadSM=new JCheckBox();
    static JCheckBox customWorkPaths=new JCheckBox();
    static JButton whatIsSM=new JButton();
    static JButton browse=new JButton();
    static JButton save=new JButton();
    static JButton cancel=new JButton();
    static JButton browseGameDir=new JButton();
    static JButton browseAssetsDir=new JButton();
    static JButton browseResourcePackDir=new JButton();
    static JButton browseDataPackDir=new JButton();
    static JButton browseSMDir=new JButton();
    static JTextField playerName=new JTextField();
    static JTextField maxMemory=new JTextField();
    static JTextField javaPath=new JTextField();
    static JTextField windowSizeWidth=new JTextField();
    static JTextField windowSizeHeight=new JTextField();
    static JTextField smPath=new JTextField();
    static JTextField gameDir=new JTextField();
    static JTextField assetsDir=new JTextField();
    static JTextField resourcePackDir=new JTextField();
    static JTextField dataPackDir=new JTextField();
    static JLabel widthHeightX=new JLabel();

    static File configFile=new File("mclx.json");
    static String configContent;
    public Settings(JFrame parent, boolean modal) {
        /*frame = new JFrame(Strings.APPLICATION_NAME);
        frame.setLayout(null);
        frame.setResizable(false);
        //frame.setVisible(true);
        frame.setBounds(124, 120, 295, 250);*/
        //JDialog this = this;//new JFrame(Strings.BUTTON_SETTINGS_NAME);
        super(parent,modal);
        this.setLayout(null);
        this.setResizable(false);
        this.setTitle(Strings.MENU_SETTINGS_NAME);
        //setFont(new Font(null,Font.PLAIN,6));
        playernameLabel.setText(Strings.SETTINGS_TEXTFIELD_PLAYERNAME_TIP_TEXT);
        maxMemoryLabel.setText(Strings.SETTINGS_TEXTFIELD_MAX_MEMORY_TIP_TEXT);
        javaPathLabel.setText(Strings.SETTINGS_TEXTFIELD_JAVA_PATH_TIP_TEXT);
        gameWindowSizeLabel.setText(Strings.SETTINGS_TEXTFIELD_GAME_WINDOW_SIZE_TIP_TEXT);
        customGameDir.setText(Strings.SETTINGS_TEXTFIELD_CUSTOM_GAME_DIR_TIP_TEXT);
        customAssetsDirL.setText(Strings.SETTINGS_TEXTFIELD_CUSTOM_ASSETS_DIR_TIP_TEXT);
        customResourcePackDirLabel.setText(Strings.SETTINGS_TEXTFIELD_CUSTOM_RESOURCE_PACK_DIR_TIP_TEXT);
        customDataPackDirLabel.setText(Strings.SETTINGS_TEXTFIELD_CUSTOM_DATA_PACK_DIR_TIP_TEXT);
        customSMDirLabel.setText(Strings.SETTINGS_TEXTFIELD_CUSTOM_SM_DIR_TIP_TEXT);
        fullscreen.setText(Strings.SETTINGS_CHECKBOX_FULLSCREEN_TEXT);
        loadSM.setText(Strings.SETTINGS_CHECKBOX_LOAD_SM_TEXT);
        customWorkPaths.setText(Strings.SETTINGS_CHECKBOX_CUSTOM_WORK_PATHS_TEXT);
        whatIsSM.setText(Strings.SETTINGS_BUTTON_WHAT_TEXT);
        browse.setText(Strings.SETTINGS_BUTTON_BROWSE_TEXT);
        browseGameDir.setText(Strings.SETTINGS_BUTTON_BROWSE_TEXT);
        browseAssetsDir.setText(Strings.SETTINGS_BUTTON_BROWSE_TEXT);
        browseResourcePackDir.setText(Strings.SETTINGS_BUTTON_BROWSE_TEXT);
        browseDataPackDir.setText(Strings.SETTINGS_BUTTON_BROWSE_TEXT);
        browseSMDir.setText(Strings.SETTINGS_BUTTON_BROWSE_TEXT);
        save.setText(Strings.SETTINGS_BUTTON_SAVE_TEXT);
        cancel.setText(Strings.DIALOG_BUTTON_CANCEL_TEXT);
        memoryUnitLabel.setText("MB");
        widthHeightX.setText("x");

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;
        maxMemory.setDocument(new NumberLenghtLimitedDmt(Long.toString(physicalTotal).length()));
        windowSizeWidth.setDocument(new NumberLenghtLimitedDmt(5));
        windowSizeHeight.setDocument(new NumberLenghtLimitedDmt(5));

        if(configFile.exists()){
            try {
                configContent = MCLX.readFileContent(configFile);
                JSONObject jsonObject=new JSONObject(configContent);
                playerName.setText(jsonObject.optString("pn"));
                javaPath.setText(jsonObject.optString("jp"));
                maxMemory.setText(String.valueOf(jsonObject.optInt("mm")));
                windowSizeWidth.setText(String.valueOf(jsonObject.optInt("ww")));
                windowSizeHeight.setText(String.valueOf(jsonObject.optInt("wh")));
                fullscreen.setSelected(jsonObject.optBoolean("fs"));
                loadSM.setSelected(jsonObject.optBoolean("ls"));
                customWorkPaths.setSelected(jsonObject.optBoolean("cw"));
                gameDir.setText(jsonObject.optString("gd"));
                assetsDir.setText(jsonObject.optString("ad"));
                resourcePackDir.setText(jsonObject.optString("rd"));
                dataPackDir.setText(jsonObject.optString("dd"));
                smPath.setText(jsonObject.optString("sd"));

                if(jsonObject.optBoolean("fs")){
                    windowSizeWidth.setEnabled(false);
                    windowSizeHeight.setEnabled(false);
                }else{
                    windowSizeWidth.setEnabled(true);
                    windowSizeHeight.setEnabled(true);
                }
                setCustomWorkPathsEnabled();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        setCustomWorkPathsEnabled();

        playernameLabel.setBounds(15,20,110,15);
        playerName.setBounds(140,15,135,25);

        maxMemoryLabel.setBounds(15,55,110,15);
        maxMemory.setBounds(140,50,65,25);
        memoryUnitLabel.setBounds(210,55,30,15);
        /*OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;*/
        osMax.setText(String.format(Strings.SETTINGS_TEXTFIELD_OS_MAX_MEMORY_TIP_TEXT, physicalTotal));
        osMax.setBounds(240,55,110,15);


        javaPathLabel.setBounds(15,90,110,15);
        javaPath.setBounds(140,85,/*185*/150,25);
        browse.setBounds(295,85,80,25);
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result;
                String path;
                JFileChooser fileChooser=new JFileChooser();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
                fileChooser.setDialogTitle(MCLX.isWindows()?Strings.DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE:Strings.DIALOG_CHOOSE_JAVA_FILE_TITLE);
                fileChooser.setApproveButtonText(Strings.DIALOG_BUTTON_YES_TEXT);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if(MCLX.isWindows()){
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            if(f.isDirectory()){
                                return true;
                            }else{
                                return f.getName().endsWith(".exe");
                            }
                        }

                        @Override
                        public String getDescription() {
                            return String.format(Strings.SETTINGS_BROSE_FILE_TYPE_TEXT,"EXE")+" (*.exe)";
                        }
                    });
                }
                result = fileChooser.showDialog(Settings.this,Strings.DIALOG_BUTTON_YES_TEXT);
                if (JFileChooser.APPROVE_OPTION == result) {
                    path=fileChooser.getSelectedFile().getAbsolutePath();
                    javaPath.setText(path);
                }

            }
        });

        fullscreen.setBounds(12,125,95,15);
        fullscreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowSizeHeight.setEnabled(!fullscreen.isSelected());
                windowSizeWidth.setEnabled(!fullscreen.isSelected());
            }
        });
        gameWindowSizeLabel.setBounds(140,125,110,15);
        windowSizeWidth.setBounds(265,120,50,25);
        widthHeightX.setBounds(316,125,10,15);
        windowSizeHeight.setBounds(325,120,50,25);

        loadSM.setBounds(12,155,75,15);
        loadSM.setEnabled(false);

        whatIsSM.setBounds(140,150,120,25);
        whatIsSM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Settings.this,Strings.DIALOG_WHAT_IS_SM_TEXT,Strings.SETTINGS_BUTTON_WHAT_TEXT,JOptionPane.QUESTION_MESSAGE);
            }
        });

        customWorkPaths.setBounds(12,185,385,15);
        customWorkPaths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCustomWorkPathsEnabled();
            }
        });


        customGameDir.setBounds(15,215,110,15);
        gameDir.setBounds(140,210,150,25);
        browseGameDir.setBounds(295,210,80,25);
        setBrowseButtonListener(browseGameDir,gameDir,Strings.DIALOG_CHOOSE_GAME_DIR_TITLE,Strings.DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT);

        customAssetsDirL.setBounds(15,245,110,15);
        assetsDir.setBounds(140,240,150,25);
        browseAssetsDir.setBounds(295,240,80,25);
        setBrowseButtonListener(browseAssetsDir,assetsDir,Strings.DIALOG_CHOOSE_ASSETS_DIR_TITLE,Strings.DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT);

        customResourcePackDirLabel.setBounds(15,275,110,15);
        resourcePackDir.setBounds(140,270,150,25);
        browseResourcePackDir.setBounds(295,270,80,25);
        setBrowseButtonListener(browseResourcePackDir,resourcePackDir,Strings.DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE,Strings.DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT);

        customDataPackDirLabel.setBounds(15,305,110,15);
        dataPackDir.setBounds(140,300,150,25);
        browseDataPackDir.setBounds(295,300,80,25);
        setBrowseButtonListener(browseDataPackDir,dataPackDir,Strings.DIALOG_CHOOSE_DATA_PACK_DIR_TITLE,Strings.DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT);

        customSMDirLabel.setBounds(15,335,110,15);
        smPath.setBounds(140,330,150,25);
        browseSMDir.setBounds(295,330,80,25);
        setBrowseButtonListener(browseSMDir,smPath,Strings.DIALOG_CHOOSE_SM_DIR_TITLE,Strings.DIALOG_CHOOSE_TYPE_SM_DIR_TEXT);

        cancel.setBounds(295,360,80,25);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.this.setVisible(false);
                if(parent==null){
                    System.exit(0);
                }
            }
        });

        save.setBounds(210,360,80,25);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject;
                try{
                    jsonObject=new JSONObject(MCLX.configContent);
                }catch (Exception ee){
                    jsonObject=new JSONObject();
                }
                jsonObject.put("pn", playerName.getText());
                jsonObject.put("mm", maxMemory.getText().length()!=0?Integer.parseInt(maxMemory.getText()):0);
                jsonObject.put("jp", javaPath.getText());
                jsonObject.put("fs", fullscreen.isSelected());
                jsonObject.put("ww", windowSizeWidth.getText().length()!=0?Integer.parseInt(windowSizeWidth.getText()):0);
                jsonObject.put("wh", windowSizeHeight.getText().length()!=0?Integer.parseInt(windowSizeHeight.getText()):0);
                jsonObject.put("ls", loadSM.isSelected());
                jsonObject.put("cw", customWorkPaths.isSelected());
                jsonObject.put("gd", gameDir.getText());
                jsonObject.put("ad", assetsDir.getText());
                jsonObject.put("rd", resourcePackDir.getText());
                jsonObject.put("dd", dataPackDir.getText());
                jsonObject.put("sd", smPath.getText());
                MCLX.configContent = jsonObject.toString();
                MCLX.javaPath = javaPath.getText();
                if(customWorkPaths.isSelected()) {
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

                }else{
                    MCLX.gameDir = new File(".minecraft");
                    MCLX.assetsDir = new File(MCLX.gameDir,"assets");
                    MCLX.respackDir = new File(MCLX.gameDir,"resourcepacks");
                    MCLX.datapackDir = new File(MCLX.gameDir,"datapacks");
                    MCLX.smDir = new File(MCLX.gameDir,"simplemods");
                }
                MCLX.versionsDir=new File(MCLX.gameDir,"versions");
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
                if(parent==null){
                    System.exit(0);
                }
            }
        });

        this.add(playernameLabel);
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
        this.add(playerName);
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

        this.setBounds(100, 100, 405, 435);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width/2;
        int screenHeight = screenSize.height/2;
        int height = this.getHeight();
        int width = this.getWidth();
        setLocation(screenWidth-width/2, screenHeight-height/2);
        //this.setVisible(true);

    }

    public void setBrowseButtonListener(JButton button, JTextField textField, String title, String typeText){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result;
                String path;
                JFileChooser fileChooser = new JFileChooser();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
                fileChooser.setDialogTitle(title);
                fileChooser.setApproveButtonText(Strings.DIALOG_BUTTON_YES_TEXT);
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
                result = fileChooser.showDialog(Settings.this,Strings.DIALOG_BUTTON_YES_TEXT);
                if (JFileChooser.APPROVE_OPTION == result) {
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    textField.setText(path);
                }
            }
        });
    }

    public void setCustomWorkPathsEnabled(){
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
}
