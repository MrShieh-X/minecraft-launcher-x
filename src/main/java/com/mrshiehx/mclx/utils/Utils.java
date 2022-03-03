package com.mrshiehx.mclx.utils;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.bean.Pair;
import com.mrshiehx.mclx.settings.Settings;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Utils {
    private static final String[] linuxBrowsers = {
            "xdg-open",
            "google-chrome",
            "firefox",
            "microsoft-edge",
            "opera",
            "konqueror",
            "mozilla"
    };

    public static void openLink(String link) {
        if (link == null)
            return;

        if (java.awt.Desktop.isDesktopSupported()) {
            new Thread(() -> {
                if (OperatingSystem.CURRENT_OS == OperatingSystem.LINUX) {
                    for (String browser : linuxBrowsers) {
                        try (final InputStream is = Runtime.getRuntime().exec(new String[]{"which", browser}).getInputStream()) {
                            if (is.read() != -1) {
                                Runtime.getRuntime().exec(new String[]{browser, link});
                                return;
                            }
                        } catch (Throwable ignored) {
                        }
                        //Logging.LOG.log(Level.WARNING, "No known browser found");
                    }
                }
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(link));
                } catch (Throwable e) {
                    if (OperatingSystem.CURRENT_OS == OperatingSystem.OSX)
                        try {
                            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", link});
                        } catch (IOException ex) {
                            //Logging.LOG.log(Level.WARNING, "Unable to open link: " + link, ex);
                        }
                    //Logging.LOG.log(Level.WARNING, "Failed to open link: " + link, e);
                }
            }).start();

        }
    }

    public static String getString(String name) {
        return MinecraftLauncherX.getString(name);
    }

    public static <K, V> Map<K, V> mapOf(Iterable<Pair<K, V>> pairs) {
        Map<K, V> map = new LinkedHashMap<>();
        for (Pair<K, V> pair : pairs)
            map.put(pair.getKey(), pair.getValue());
        return map;
    }

    public static byte[] inputStream2ByteArray(InputStream resourceAsStream) throws IOException {
        if (resourceAsStream != null) {
        /*byte[] bytes = new byte[0];
        bytes = new byte[resourceAsStream.available()];
        resourceAsStream.read(bytes);
        return new String(bytes);*/
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = resourceAsStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } else return null;
    }

    public static String readData(HttpURLConnection con) throws IOException {
        try {
            try (InputStream stdout = con.getInputStream()) {
                return IOUtils.readFullyAsString(stdout, UTF_8);
            }
        } catch (IOException e) {
            try (InputStream stderr = con.getErrorStream()) {
                if (stderr == null)
                    throw e;
                return IOUtils.readFullyAsString(stderr, UTF_8);
            }
        }
    }

    public static boolean isEmpty(CharSequence c) {
        return c == null || c.length() == 0;
    }

    public static JSONObject parseJSONObject(String j) {
        if (isEmpty(j)) return null;
        try {
            return new JSONObject(j);
        } catch (Exception ignore) {
        }
        return null;
    }

    public static JSONArray parseJSONArray(String j) {
        if (isEmpty(j)) return null;
        try {
            return new JSONArray(j);
        } catch (Exception ignore) {
        }
        return null;
    }


    public static String post(String first, String second) throws IOException {
        return post(first, second, "application/json", null);
    }

    public static String post(String first, String second, String contentType, String accept) throws IOException {
        URL ConnectUrl = new URL(first);
        HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        //here is your code above
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);
        if (!Utils.isEmpty(accept)) connection.setRequestProperty("Accept", accept);
        OutputStream wrt = ((connection.getOutputStream()));

        if (second != null) wrt.write(second.getBytes());
        return (Utils.readData(connection));
    }

    public static String get(String url, String tokenType, String token) throws IOException {
        return get(url, tokenType, token, "application/json", "application/json");
    }

    public static String get(String url, String tokenType, String token, String contentType, String accept) throws IOException {
        URL ConnectUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        //here is your code above
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Authorization", tokenType + " " + token);
        //System.out.println(tokenType);
        //System.out.println(token);
        if (!Utils.isEmpty(accept)) connection.setRequestProperty("Accept", accept);
        //connection.getOutputStream();//.write("\"publicCreateProfileDTO\":\"45\"".getBytes(UTF_8));
        return (Utils.readData(connection));
    }

    public static String delete(String url, String tokenType, String token) throws IOException {
        return delete(url, tokenType, token, "application/json", "application/json");
    }

    public static String delete(String url, String tokenType, String token, String contentType, String accept) throws IOException {
        URL ConnectUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        //here is your code above
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Authorization", tokenType + " " + token);
        //System.out.println(tokenType);
        //System.out.println(token);
        if (!Utils.isEmpty(accept)) connection.setRequestProperty("Accept", accept);
        //connection.getOutputStream();//.write("\"publicCreateProfileDTO\":\"45\"".getBytes(UTF_8));
        return (Utils.readData(connection));
    }

    public static String getLibraryName(String path) {
        if (Utils.isEmpty(path)) return "";
        String splitter = File.separator;
        if (!path.contains(splitter)) return path;
        path = path.replace(File.separatorChar, '/');
        splitter = "/";
        String[] strings = path.split(splitter);
        if (strings.length < 4) return path;

        return strings[strings.length - 3];
    }

    public static String getNativeLibraryName(String path) {
        if (Utils.isEmpty(path)) return "";
        String splitter = File.separator;
        if (!path.contains(splitter) && !path.contains("\\") && !path.contains("/")) return path;
        path = path.replace(File.separatorChar, '/');
        splitter = "/";
        String[] strings = path.split(splitter);
        if (strings.length < 3) return path;

        return strings[strings.length - 3];
    }


    public static String[] listVersions(File versionsDir) {
        List<String> versionsStrings = new ArrayList<>();
        File[] files = versionsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isDirectory()) return false;
                File[] files = pathname.listFiles();
                if (files == null || files.length < 2) return false;
                return new File(pathname, pathname.getName() + ".json").exists() && new File(pathname, pathname.getName() + ".jar").exists();
            }
        });
        if (files != null && files.length > 0) {
            for (File file : files) {
                versionsStrings.add(getVersion(file.getAbsolutePath()));
            }
            String[] strArray = new String[versionsStrings.size()];
            return versionsStrings.toArray(strArray);
        } else {
            return new String[0];
        }
    }

    public static String getVersion(String path) {
        /*String split = "/";
        if (path.contains("\\")) {
            path=path.replace('\\','/');
        }
        String noDriver = path.substring(path.indexOf(split));
        int indexOf = noDriver.lastIndexOf(split);
        return noDriver.substring(indexOf + 1);*/
        return new File(path).getName();
    }

    public static void deleteDirectory(File directory) {
        if (directory != null) {
            if (directory.exists()) {
                if (directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        if (files.length != 0) {
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


    public static void copyDirectory(File from, String toWillNewDirNameIsAtFromName, String afterThatName) throws IOException {
        if (from != null && !isEmpty(toWillNewDirNameIsAtFromName) && from.exists()) {
            if (from.isFile()) {
                Utils.copyFile(from, new File(toWillNewDirNameIsAtFromName, afterThatName));
                return;
            }
            File toWillNewDirNameIsAtFrom = new File(toWillNewDirNameIsAtFromName);
            File to = new File(toWillNewDirNameIsAtFrom, afterThatName);
            if (!to.exists()) to.mkdirs();
            for (File file : from.listFiles()) {
                if (file.isFile()) {
                    Utils.copyFile(file, new File(to, file.getName()));
                } else {
                    copyDirectory(file, to.getAbsolutePath(), file.getName());
                }
            }
        }
    }

    public static File createFile(File file) throws IOException {
        return createFile(file, true);
    }

    public static File createFile(File file, boolean delete) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (delete && file.exists()) file.delete();
        if (!file.exists()) file.createNewFile();
        return file;
    }

    public static void copyFile(File source, File to)
            throws IOException {
        if (null == source) return;
        if (source.isDirectory()) copyDirectory(source, to.getParent(), to.getName());
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

    public static String valueOf(Object value) {
        return value == null ? "" : value.toString();
    }

    public static boolean isWindows() {
        /*return File.separator.equals("\\")
                || File.separatorChar == '\\'
                ||*//*AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.WINDOWS*//*System.getProperty("os.name").toLowerCase().contains("windows");*/
        return OperatingSystem.CURRENT_OS == OperatingSystem.WINDOWS;
    }

    public static String getDefaultJavaPath() {
        String s = System.getProperty("java.home");
        return isEmpty(s) ? "" : new File(s, isWindows() ? "bin\\java.exe" : "bin/java").getAbsolutePath();
    }

    public static void saveConfig(JSONObject jsonObject) {
        MinecraftLauncherX.configContent = jsonObject.toString();
        try {
            FileWriter writer = new FileWriter(Settings.configFile, false);
            writer.write(jsonObject.toString(Settings.INDENT_FACTOR));
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static JSONObject getConfig() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(MinecraftLauncherX.configContent);
        } catch (Exception e3) {
            e3.printStackTrace();
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    public static File getAssets(String assetsDirPath, File gameDir) {
        return !isEmpty(assetsDirPath) ? new File(assetsDirPath) : new File(gameDir, "assets");
    }

    public static int getJavaVersion(String javaFile) {
        try {
            String version = null;
            Process process = new ProcessBuilder(javaFile, "-XshowSettings:properties", "-version").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), OperatingSystem.NATIVE_CHARSET))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    Matcher m = Pattern.compile("java\\.version = (?<version>.*)").matcher(line);
                    if (m.find()) {
                        version = m.group("version");
                        break;
                    }
                }
            }

            if (version == null) {
                process = new ProcessBuilder(javaFile, "-version").start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), OperatingSystem.NATIVE_CHARSET))) {
                    for (String line; (line = reader.readLine()) != null; ) {
                        Matcher m = Pattern.compile("version \"(?<version>(.*?))\"").matcher(line);
                        if (m.find()) {
                            version = m.group("version");
                            break;
                        }
                    }
                }
            }
            if (version != null) {
                if (version.startsWith("1.")) {
                    version = version.substring(2, 3);
                } else {
                    int dot = version.indexOf(".");
                    if (dot != -1) {
                        version = version.substring(0, dot);
                    }
                }
                return Integer.parseInt(version);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    public static String getNativesDirName() {
        return "natives-" + OperatingSystem.CURRENT_OS.getCheckedName();
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

    public static void exceptionDialog(JFrame frame, Throwable throwable) {
        JOptionPane.showMessageDialog(frame, throwable, getString("DIALOG_TITLE_NOTICE"), JOptionPane.ERROR_MESSAGE);
    }


    public static String[] split(String src) {
        List<String> list = new ArrayList<>();
        /*List<Boolean> yinyongs=new ArrayList<>();
        String[]split=src.split(String.valueOf(symbol));*/
        boolean yinyong = false;
        for (int i = 0; i < src.length(); i++) {
            char str = src.charAt(i);
            boolean modifiedYinyong = false;
            if (str == '\"') {
                yinyong = !yinyong;
                modifiedYinyong = true;
            }
            if (!yinyong) {
                if (!modifiedYinyong) {
                    if (str != ' ') {
                        if (i == 0) list.add(String.valueOf(str));
                        else list.set(list.size() - 1, list.get(list.size() - 1) + str);
                    } else {
                        list.add("");
                    }
                }
            } else {
                if (!modifiedYinyong) {
                    if (list.size() > 0) {
                        list.set(list.size() - 1, list.get(list.size() - 1) + str);
                    } else {
                        list.add("" + str);
                    }
                }
            }
        }
        return list.toArray(new String[0]);
    }

    public static String clearRedundantSpaces(String string) {
        char[] sourceChars = string.toCharArray();
        Object space = new Object();
        Object[] objects = new Object[string.length()];
        boolean yinyong = false;
        for (int i = 0; i < sourceChars.length; i++) {
            char cha = sourceChars[i];
            if (cha == '\"') {
                yinyong = !yinyong;
            }
            objects[i] = !yinyong && cha == ' ' ? space : cha;
        }
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == space) {
                list.add(' ');
                for (int j = i; j < objects.length; j++) {
                    if (objects[j] != space) {
                        i = j - 1;
                        break;
                    }
                }

            } else if (object instanceof Character) {
                list.add((Character) object);
            }
        }

        char[] chars = new char[list.size()];
        for (int i = 0; i < list.size(); i++) {
            chars[i] = list.get(i);
        }
        return new String(chars);
    }
}
