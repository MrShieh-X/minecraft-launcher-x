package com.mrshiehx.mclx;

import com.mrshiehx.mclx.modules.version.VersionInstaller;
import com.mrshiehx.mclx.settings.Settings;
import com.mrshiehx.mclx.utils.Utils;
import com.mrshiehx.mclx.modules.version.VersionsManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.mrshiehx.mclx.modules.MinecraftLauncher.*;
public class MinecraftLauncherX {
    public static final String CLIENT_ID = "bcb89757-1625-4561-8bc6-34d04a11a07f";
    public static File gameDir;
    public static File assetsDir;
    public static File respackDir;
    public static File datapackDir;
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

    public static String MCLX_VERSION = "1.3";

    public static ImageIcon icon = new ImageIcon(MinecraftLauncherX.class.getResource("/icon.png"));

    private static JSONObject enUSText;
    private static JSONObject zhCNText;

    static String language;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }
        JFrame frame = new JFrame(getString("APPLICATION_SHORT_NAME"));
        if(icon!=null)frame.setIconImage(icon.getImage());
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
                JOptionPane.showMessageDialog(frame, getString("DIALOG_ABOUT_DESCRIPTION"), getString("MENU_ABOUT_NAME"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
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

        gameMenu.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (runningMc != null) {
                    killMc.setEnabled(runningMc.isAlive());
                } else {
                    killMc.setEnabled(false);
                }

                copyCommand.setEnabled(versionChooser.getModel().getSelectedItem() != null);
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

        copyCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    configContent = readFileContent(configFile);
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
                    File versionsFolder = addTo(gameDir, "versions");
                    File versionFolder = addTo(versionsFolder, selected);
                    File versionJarFile = addTo(versionFolder, selected + ".jar");
                    File versionJsonFile = addTo(versionFolder, selected + ".json");
                    try {
                        String at="0",uu="0";
                        if(jsonObject.optInt("lm")>0){
                            at=jsonObject.optString("at", "0");
                            uu=jsonObject.optString("uu", "0");
                        }
                        copyText((String) launchMinecraft(versionJarFile, versionJsonFile, gameDir, assetsDir, respackDir, datapackDir, jsonObject.optBoolean("ls"), smDir, jsonObject.optString("pn", "XPlayer"), jsonObject.optString("jp"), jsonObject.optInt("mm", 1024), 128, jsonObject.optInt("ww", 854), jsonObject.optInt("wh", 480), jsonObject.optBoolean("fs"), at,uu, LAUNCH_MODE_GET_COMMAND,log,false,!jsonObject.optBoolean("fs")));
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
                VersionsManager.showDialog(frame,versionsDir);
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
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(configContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                javaPath = jsonObject.optString("jp");
                if (jsonObject.optBoolean("cw")) {

                    gameDir = new File(!isEmpty(jsonObject.optString("gd")) ? jsonObject.optString("gd") : ".minecraft");
                    assetsDir = !isEmpty(jsonObject.optString("ad")) ? new File(jsonObject.optString("ad")) : new File(gameDir, "assets");
                    respackDir = !isEmpty(jsonObject.optString("rd")) ? new File(jsonObject.optString("rd")) : new File(gameDir, "resourcepacks");
                    datapackDir = !isEmpty(jsonObject.optString("dd")) ? new File(jsonObject.optString("dd")) : new File(gameDir, "datapacks");
                    smDir = !isEmpty(jsonObject.optString("sd")) ? new File(jsonObject.optString("sd")) : new File(gameDir, "simplemods");

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
                if (versionChooser.getModel().getSelectedItem() != null) {
                    try {
                        configContent = readFileContent(configFile);
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
                        File versionsFolder = addTo(gameDir, "versions");
                        File versionFolder = addTo(versionsFolder, selected);
                        File versionJarFile = addTo(versionFolder, selected + ".jar");
                        File versionJsonFile = addTo(versionFolder, selected + ".json");
                        try {
                            String at="0",uu="0";
                            if(jsonObject.optInt("lm")>0){
                                at=jsonObject.optString("at", "0");
                                uu=jsonObject.optString("uu", "0");
                            }
                            runningMc = (Process) launchMinecraft(versionJarFile, versionJsonFile, gameDir, assetsDir, respackDir, datapackDir, jsonObject.optBoolean("ls"), smDir, jsonObject.optString("pn", "XPlayer"), jsonObject.optString("jp"), jsonObject.optInt("mm", 1024), 128, jsonObject.optInt("ww", 854), jsonObject.optInt("wh", 480), jsonObject.optBoolean("fs"), at,uu, LAUNCH_MODE_EXECUTE,log,false,!jsonObject.optBoolean("fs"));

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
                                        log.setText(log.getText() + getString("MESSAGE_FINISHED_GAME"));
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, ex, getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }else{
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

        updateVersions();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void updateVersions() {
        String[] strArray = Utils.listVersions(versionsDir);
        ComboBoxModel<String> spinnerListModel = new DefaultComboBoxModel<>(strArray);
        versionChooser.setModel(spinnerListModel);
        startGame.setEnabled(versionChooser.getSelectedItem()!=null);
        versionChooser.addItemListener(e -> startGame.setEnabled(versionChooser.getSelectedItem()!=null));
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
        return File.separator.equals("\\")
                || File.separatorChar == '\\'
                ||/*AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.WINDOWS*/System.getProperty("os.name").toLowerCase().contains("windows");
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
        String separator = getFileSeparator(path);
        if (!path.endsWith(separator)) {
            path = path + separator;
        }
        return path;
    }


    public static String readFileContent(File file) throws IOException {
        BufferedReader reader;
        StringBuilder sbf = new StringBuilder();
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
        /*String path = startFile.getAbsolutePath();
        String separator = "/";
        if (hasWindowsFileSeparator(path)) {
            separator = "\\";
        }
        if (path.endsWith(separator)) {
            return new File(path + needToAddNoSeparatorStart);
        } else {
            return new File(path + separator + needToAddNoSeparatorStart);
        }*/
        return new File(startFile, needToAddNoSeparatorStart);
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

    public static void downloadFile(String urla, File to, JProgressBar progressBar) throws IOException {
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

    public static String showInputNameDialog(Component parent, String defaultName) {
        return showInputNameDialog(parent, defaultName,getString("MESSAGE_INSTALL_INPUT_NAME"));
    }
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


    public static void addLog(JTextArea textArea, String message) {
        if (textArea != null) {
            textArea.setText(textArea.getText() + message + "\n");
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

        return enUSText.optString(name);
    }

    public static String getLanguage() {
        if (isEmpty(language)) language = Locale.getDefault().getLanguage();
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

}