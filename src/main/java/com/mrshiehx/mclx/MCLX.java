package com.mrshiehx.mclx;

import com.sun.management.OperatingSystemMXBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.List;

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

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
        JFrame frame = new JFrame(Strings.APPLICATION_NAME);
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
                JOptionPane.showMessageDialog(frame, Strings.DIALOG_ABOUT_DESCRIPTION, Strings.MENU_ABOUT_NAME, JOptionPane.INFORMATION_MESSAGE);
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
                if(runningMc!=null){
                    if(runningMc.isAlive()){
                        runningMc.destroy();
                    }
                }
                runningMc=null;
            }
        });

        menu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(runningMc!=null){
                    killMc.setEnabled(runningMc.isAlive());
                }else{
                    killMc.setEnabled(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        menu.add(killMc);
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

                    File gameDirFromFile = new File(jsonObject.optString("gd").length() != 0 ? jsonObject.optString("gd") : ".minecraft");
                    File assetDirFromFile = new File(jsonObject.optString("ad").length() != 0 ? jsonObject.optString("ad") : ".minecraft/assets");
                    File resPackDirFromFile = new File(jsonObject.optString("rd").length() != 0 ? jsonObject.optString("rd") : ".minecraft/resourcepacks");
                    File dataPackDirFromFile = new File(jsonObject.optString("dd").length() != 0 ? jsonObject.optString("dd") : ".minecraft/datapacks");
                    File smDirFromFile = new File(jsonObject.optString("sd").length() != 0 ? jsonObject.optString("sd") : ".minecraft/simplemods");
                    File[] filesFromFile = new File[]{gameDirFromFile, assetDirFromFile, resPackDirFromFile, dataPackDirFromFile, smDirFromFile};
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
                                    Settings settings = new Settings(frame, true);
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
                    }
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
            configContent=jsonObject.toString();
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
                try {
                    configContent = readFileContent(configFile);
                }catch (IOException exception){
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
                        runningMc = launchMinecraft(versionJarFile, versionJsonFile, gameDir, assetsDir, respackDir, datapackDir, jsonObject.optBoolean("ls"), smDir, jsonObject.optString("pn", "XPlayer"), jsonObject.optString("jp"), jsonObject.optInt("mm", 1024),128, jsonObject.optInt("ww", 854), jsonObject.optInt("wh", 480), jsonObject.optBoolean("fs"), "0");

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
        });

        if (!gameDir.exists()) {
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
                    Settings settings = new Settings(frame, true);
                    settings.setVisible(true);
                } else if (optionSelected == 1) {
                    System.exit(0);
                }
            }
        }
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
        List<String> versionsStrings = new ArrayList();
        File[] files = versionsDir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                versionsStrings.add(getVersion(files[i].getAbsolutePath()));
            }
            String[] strArray = new String[versionsStrings.size()];
            versionsStrings.toArray(strArray);
            ComboBoxModel<String> spinnerListModel = new DefaultComboBoxModel<String>(strArray);
            versionChooser.setModel(spinnerListModel);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
        assetsDir = new File(".minecraft/assets");
        respackDir = new File(".minecraft/resourcepacks");
        datapackDir = new File(".minecraft/datapacks");
        smDir = new File(".minecraft/simplemods");
        versionsDir = new File(".minecraft/versions");
    }

    public static boolean isWindows() {
        return /*File.separator.equals("\\")||File.separatorChar=='\\'||*//*AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.WINDOWS*/System.getProperty("os.name").startsWith("Windows")||System.getProperty("os.name").startsWith("windows");
    }


    public static byte[] int2Bytes(int num) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(num >>> 24);
        bytes[1] = (byte)(num >>> 16);
        bytes[2] = (byte)(num >>> 8);
        bytes[3] = (byte)num;
        return bytes;
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
        if(!new File(javaPath).exists()){
            throw new LaunchException(Strings.EXCEPTION_VERSION_JSON_NOT_FOUND);
        }
        if(gameDir==null){
            gameDir=new File(".minecraft");
        }
        if(assetsDir==null){
            assetsDir=new File(".minecraft/assets");
        }
        if(resourcePacksDir==null){
            resourcePacksDir=new File(".minecraft/resourcepacks");
        }
        if(dataPacksDir==null){
            dataPacksDir=new File(".minecraft/datapacks");
        }
        if(SMDir==null){
            SMDir=new File(".minecraft/simplemods");
        }
        if(!gameDir.exists()){
            throw new LaunchException(Strings.MESSAGE_NOT_FOUND_GAME_DIR);
        }
        if(maxMemory==0){
            throw new LaunchException(Strings.EXCEPTION_MAX_MEMORY_IS_ZERO);
        }

        if(!assetsDir.exists()){
            assetsDir.mkdirs();
        }

        if(!resourcePacksDir.exists()){
            resourcePacksDir.mkdirs();
        }

        if(!dataPacksDir.exists()){
            dataPacksDir.mkdirs();
        }

        if(!SMDir.exists()){
            SMDir.mkdirs();
        }

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;

        if(maxMemory>physicalTotal){
            throw new LaunchException(Strings.EXCEPTION_MAX_MEMORY_TOO_BIG);
        }

        String contentOfJsonFile;
        if(!minecraftVersionJsonFile.exists()) {
            throw new LaunchException(Strings.EXCEPTION_VERSION_JSON_NOT_FOUND);
        }else{
            contentOfJsonFile = readFileContent(minecraftVersionJsonFile);
        }
        if(!minecraftJarFile.exists()){
            throw new LaunchException(Strings.EXCEPTION_VERSION_NOT_FOUND);
        }
        log.setText(Strings.MESSAGE_STARTING_GAME+"\n");
        JSONObject headJsonObject=new JSONObject(contentOfJsonFile);
        JSONArray libraries=headJsonObject.optJSONArray("libraries");
        File librariesFile=addTo(gameDir,"libraries");
        List<String> librariesPaths=new ArrayList();
        for(int i=0;i<libraries.length();i++){
            JSONObject library = libraries.optJSONObject(i);
            String name=library.optString("name");
            String[] nameSplit=name.split(":");
            String libraryFileName=nameSplit[1]+"-"+nameSplit[2]+".jar";
            String libraryFileAndDirectoryName=nameSplit[0].replace(".","/")+"/"+nameSplit[1]+"/"+nameSplit[2];
            File libraryFile=addTo(addTo(librariesFile,libraryFileAndDirectoryName),libraryFileName);
            if(libraryFile.exists()&&!librariesPaths.contains(libraryFile.getAbsolutePath())) {
                librariesPaths.add(libraryFile.getAbsolutePath());
            }
        }
        String arguments = "";
        String id=headJsonObject.optString("id","1.0");


        JSONObject assetIndexObject=headJsonObject.optJSONObject("assetIndex");

        String assetsIndex=assetIndexObject.optString("id");


        if(id.startsWith("1.")) {
            if(!id.startsWith("1.RV-Pre1")) {
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
            }else{
                arguments = headJsonObject.optString("minecraftArguments");
            }
        }else{
            char[]idChars=id.toCharArray();

            if(idChars[2]=='w'){
                String[]idsForSnapshot=id.split("w");
                if(Integer.parseInt(idsForSnapshot[0])>17){
                    /**protected*/
                    JSONObject argumentsArray=headJsonObject.optJSONObject("arguments");
                    JSONArray gameArray=argumentsArray.optJSONArray("game");
                    for(int i=0;i<gameArray.length();i++){
                        if(gameArray.opt(i)instanceof String){
                            arguments=arguments+gameArray.opt(i)+" ";
                        }else{
                            arguments=arguments.substring(0,arguments.length()-1);
                            break;
                        }
                    }
                    /**protected*/
                }else if(Integer.parseInt(idsForSnapshot[0])<17){
                    arguments=headJsonObject.optString("minecraftArguments");
                }else if(Integer.parseInt(idsForSnapshot[0])==17){
                    int partOfWeekNumber=Integer.parseInt(idsForSnapshot[1].substring(0,/*idsForSnapshot[1].length()-1*/numberOfAStringStartInteger(idsForSnapshot[1])));
                    if(partOfWeekNumber>=43){
                        /**protected*/
                        JSONObject argumentsArray=headJsonObject.optJSONObject("arguments");
                        JSONArray gameArray=argumentsArray.optJSONArray("game");
                        for(int i=0;i<gameArray.length();i++){
                            if(gameArray.opt(i)instanceof String){
                                arguments=arguments+gameArray.opt(i)+" ";
                            }else{
                                arguments=arguments.substring(0,arguments.length()-1);
                                break;
                            }
                        }
                        /**protected*/
                    }else{
                        arguments = headJsonObject.optString("minecraftArguments");
                    }
                }
            }else {
                if(id.equals("3D Shareware v1.34")){


                    /**protected*/
                    JSONObject argumentsArray=headJsonObject.optJSONObject("arguments");
                    JSONArray gameArray=argumentsArray.optJSONArray("game");
                    for(int i=0;i<gameArray.length();i++){
                        if(gameArray.opt(i)instanceof String){
                            arguments=arguments+gameArray.opt(i)+" ";
                        }else{
                            arguments=arguments.substring(0,arguments.length()-1);
                            break;
                        }
                    }
                    /**protected*/


                }else {
                    arguments = headJsonObject.optString("minecraftArguments");
                }
            }
        }



        String mainClass=headJsonObject.optString("mainClass");
        File nativesFolder=addTo(minecraftVersionJsonFile.getParentFile(),"natives");
        String librariesString = "";
        for (String librariesPath : librariesPaths) {
            librariesString = librariesString + librariesPath.replace("\\","\\\\") + (isWindows()?";":":");
        }
        librariesString=librariesString+minecraftJarFile.getAbsolutePath().replace("\\","\\\\");

        if(loadSM){
            mainClass="com.mrshiehx.simplemod.Start";
            arguments="--smDir ${sm_directory} "+arguments;
            File smloaderFile=new File("simplemod-loader-1.0.jar");
            copyFile(new File(MCLX.class.getResource("assets/simplemod-loader-1.0.jar").getFile()),smloaderFile);
            librariesString=librariesString+(isWindows()?";":":")+smloaderFile.getAbsolutePath().replace("\\","\\\\");
        }

        String parsed=arguments.replace("${sm_directory}", addShuangyinhaoToPath(SMDir.getAbsolutePath())).replace("${auth_player_name}", playername).replace("${version_name}", "\"MCLX 1.0\"").replace("${version_type}","\"MCLX 1.0\"").replace("${auth_access_token}",accessToken).replace("${game_directory}",addShuangyinhaoToPath(gameDir.getAbsolutePath())).replace("${assets_root}",addShuangyinhaoToPath(assetsDir.getAbsolutePath())).replace("${assets_index_name}",/*ids[0]+"."+ids[1]*/assetsIndex).replace("--uuid ${auth_uuid}","").replace("${user_type}","legacy").replace("${auth_session}","0").replace("${game_assets}",addShuangyinhaoToPath(assetsDir.getAbsolutePath()));

        parsed=parsed+" --resourcePackDir "+addShuangyinhaoToPath(resourcePacksDir.getAbsolutePath())+" --dataPackDir "+addShuangyinhaoToPath(dataPacksDir.getAbsolutePath());
        if(fullscreen) {
            parsed=parsed+" --fullscreen";
        }

        parsed=parsed+" --width "+width+" --height "+height;


        String javaLibraryPath;
        if(nativesFolder.exists()){
            javaLibraryPath="\"-Djava.library.path="+nativesFolder.getAbsolutePath().replace("\\","\\\\")+"\"";
        }else{
            throw new LaunchException(Strings.EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND);
        }

        String command=addShuangyinhaoToPath(javaPath)+" -Xmn"+miniMemory+"m -Xmx"+maxMemory+"m "+javaLibraryPath+" -XX:+UseG1GC -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true -Dminecraft.launcher.brand=MCLX -Dminecraft.launcher.version=1.0 -cp \""+librariesString+"\" "+mainClass+" "+parsed;
        //System.out.println(command+"\n");
        /*if(logsOutput!=null) {net.minecraft.client.main.Main --username XPlayer --version "MCLX 1.0" --gameDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft" --assetsDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\assets" --assetIndex 1.16  --accessToken 0 --userType legacy --versionType "MCLX 1.0" --resourcePackDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\resourcepacks" --dataPackDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\datapacks" --width 854 --height 480
            System.setOut(new PrintStream(logsOutput));
        }*/
        Runtime run = Runtime.getRuntime();
        return run.exec(command);
    }

    public static String addShuangyinhaoToPath(String path){
        if(path.startsWith("\"")){
            if(!path.endsWith("\"")){
                path=path+"\"";
            }
        }else if(path.endsWith("\"")){
            if(!path.startsWith("\"")){
                path= "\""+path;
            }
        }else{
            path= "\""+path+"\"";
        }
        return path.replace("\\","\\\\");
    }

    public static void copyFile(File source, File to)
            throws IOException {
        if(to.exists()){
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

    public static boolean hasWindowsFileSeparator(String path){
        return path.contains("\\");
    }

    public static File addTo(File startFile,String needToAddNoSeparatorStart){
        String path=startFile.getAbsolutePath();
        String separator="/";
        if(hasWindowsFileSeparator(path)){
            separator="\\";
        }
        if(path.endsWith(separator)){
            return new File(path+needToAddNoSeparatorStart);
        }else{
            return new File(path+separator+needToAddNoSeparatorStart);
        }
    }
    public static String getFileSeparator(String path){
        return hasWindowsFileSeparator(path)?"\\":"/";
    }

    public static int numberOfAStringStartInteger(String target){
        int r=0;
        char[]targetChars=target.toCharArray();
        for(int i=0;i<target.length();i++){
            if(targetChars[i]=='0'||targetChars[i]=='1'||targetChars[i]=='2'||targetChars[i]=='3'||targetChars[i]=='4'||targetChars[i]=='5'||targetChars[i]=='6'||targetChars[i]=='7'||targetChars[i]=='8'||targetChars[i]=='9'){
                r++;
            }else{
                break;
            }
        }
        return r;
    }
}