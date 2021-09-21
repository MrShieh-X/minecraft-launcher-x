package com.mrshiehx.mclx.utils;

import com.mrshiehx.mclx.MinecraftLauncherX;
import javafx.util.Pair;
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

    public static boolean isEmpty(CharSequence c){
        return c==null||c.length()==0;
    }

    public static JSONObject parseJSONObject(String j){
        if(isEmpty(j))return null;
        try{
            return new JSONObject(j);
        }catch (Exception ignore){
        }
        return null;
    }

    public static JSONArray parseJSONArray(String j){
        if(isEmpty(j))return null;
        try{
            return new JSONArray(j);
        }catch (Exception ignore){
        }
        return null;
    }



    public static String post(String first, String second) throws IOException {
        return post(first, second,"application/json",null);
    }
    public static String post(String first, String second, String contentType, String accept) throws IOException {
        URL ConnectUrl = new URL(first);
        HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        //here is your code above
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);
        if(!Utils.isEmpty(accept))connection.setRequestProperty("Accept", accept);
        OutputStream wrt = ((connection.getOutputStream()));

        if (second != null) wrt.write(second.getBytes());
        return (Utils.readData(connection));
    }

    public static String get(String url, String tokenType, String token) throws IOException{
        return get(url, tokenType,token,"application/json","application/json");
    }
    public static String get(String url, String tokenType, String token, String contentType, String accept) throws IOException{
        URL ConnectUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        //here is your code above
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Authorization",tokenType+" "+token);
        //System.out.println(tokenType);
        //System.out.println(token);
        if(!Utils.isEmpty(accept))connection.setRequestProperty("Accept", accept);
        //connection.getOutputStream();//.write("\"publicCreateProfileDTO\":\"45\"".getBytes(UTF_8));
        return (Utils.readData(connection));
    }
    public static String delete(String url, String tokenType, String token) throws IOException{
        return delete(url, tokenType,token,"application/json","application/json");
    }
    public static String delete(String url, String tokenType, String token, String contentType, String accept) throws IOException{
        URL ConnectUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        //here is your code above
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Authorization",tokenType+" "+token);
        //System.out.println(tokenType);
        //System.out.println(token);
        if(!Utils.isEmpty(accept))connection.setRequestProperty("Accept", accept);
        //connection.getOutputStream();//.write("\"publicCreateProfileDTO\":\"45\"".getBytes(UTF_8));
        return (Utils.readData(connection));
    }

    public static String getLibraryName(String path){
        if(Utils.isEmpty(path))return "";
        String splitter= File.separator;
        if(!path.contains(splitter))return path;
        path=path.replace(File.separatorChar,'/');
        splitter="/";
        String[] strings=path.split(splitter);
        if(strings.length<4)return path;

        return strings[strings.length-3];
    }

    public static String getNativeLibraryName(String path){
        if(Utils.isEmpty(path))return "";
        String splitter=File.separator;
        if(!path.contains(splitter)&&!path.contains("\\")&&!path.contains("/"))return path;
        path=path.replace(File.separatorChar,'/');
        splitter="/";
        String[] strings=path.split(splitter);
        if(strings.length<3)return path;

        return strings[strings.length-3];
    }


    public static String[]listVersions(File versionsDir) {
        List<String> versionsStrings = new ArrayList<>();
        File[] files = versionsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(!pathname.isDirectory())return false;
                File[]files=pathname.listFiles();
                if(files==null||files.length<2)return false;
                return new File(pathname,pathname.getName()+".json").exists()&&new File(pathname,pathname.getName()+".jar").exists();
            }
        });
        if (files != null&&files.length>0) {
            for (File file : files) {
                versionsStrings.add(getVersion(file.getAbsolutePath()));
            }
            if(versionsStrings.size()>0){
            String[] strArray = new String[versionsStrings.size()];
            return versionsStrings.toArray(strArray);
            }else{
                return new String[0];
            }
        }else{
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
        return createFile(file,true);
    }
    public static File createFile(File file,boolean delete) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (delete&&file.exists()) file.delete();
        if(!file.exists())file.createNewFile();
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
        return value==null?"":value.toString();
    }
}
