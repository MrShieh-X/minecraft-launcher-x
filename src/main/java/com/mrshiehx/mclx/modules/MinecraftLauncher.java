package com.mrshiehx.mclx.modules;

import com.mrshiehx.mclx.bean.Library;
import com.mrshiehx.mclx.exceptions.EmptyNativesException;
import com.mrshiehx.mclx.exceptions.LaunchException;
import com.mrshiehx.mclx.exceptions.LibraryDefectException;
import com.mrshiehx.mclx.interfaces.Void;
import com.mrshiehx.mclx.utils.OperatingSystem;
import com.mrshiehx.mclx.utils.Utils;
import com.sun.management.OperatingSystemMXBean;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;

public class MinecraftLauncher {
    /**
     * Get Minecraft Launch Command Arguments
     *
     * @param minecraftJarFile         jar file
     * @param minecraftVersionJsonFile json file
     * @param gameDir                  game directory
     * @param assetsDir                assets directory
     * @param resourcePacksDir         resource packs directory
     * @param playerName               player name
     * @param javaPath                 java path
     * @param maxMemory                max memory
     * @param miniMemory               mini memory
     * @param width                    window width
     * @param height                   window height
     * @param fullscreen               is window fullscreen
     * @param accessToken              access token of official account
     * @param uuid                     uuid of official account
     * @param isDemo                   is Minecraft demo
     * @param customScreenSize         does player custom the size of screen
     * @param startLaunch              after judgment, prepare to start, nullable
     * @return Launch Command Arguments
     * @throws LaunchException          launch exception
     * @throws IOException              io or file exception
     * @throws JSONException            exception to parsing json
     * @throws LibraryDefectException   exception to if some libraries are not found
     * @author MrShiehX
     * @updateDate February 22, 2022
     */
    public static List<String> getMinecraftLaunchCommandArguments(
            File minecraftJarFile,
            File minecraftVersionJsonFile,
            File gameDir,
            File assetsDir,
            File resourcePacksDir,
            String playerName,
            String javaPath,
            int maxMemory,
            int miniMemory,
            int width,
            int height,
            boolean fullscreen,
            String accessToken,
            String uuid,
            boolean isDemo,
            boolean customScreenSize,
            @Nullable Void startLaunch) throws
            LibraryDefectException,
            EmptyNativesException,
            LaunchException,
            IOException,
            JSONException {
        if (!new File(javaPath).exists()) {
            throw new LaunchException(getString("EXCEPTION_VERSION_JSON_NOT_FOUND"));
        }
        if (null == gameDir) {
            gameDir = new File(".minecraft");
        }
        if (null == assetsDir) {
            assetsDir = new File(gameDir, "assets");
        }
        if (null == resourcePacksDir) {
            resourcePacksDir = new File(gameDir, "resourcepacks");
        }
        /*if (null == dataPacksDir) {
            dataPacksDir = new File(gameDir, "datapacks");
        }*/
        /*if (null == SMDir) {
            SMDir = new File(gameDir, "simplemods");
        }*/
        if (!gameDir.exists()) {
            throw new LaunchException(getString("MESSAGE_NOT_FOUND_GAME_DIR"));
        }
        if (maxMemory == 0) {
            throw new LaunchException(getString("EXCEPTION_MAX_MEMORY_IS_ZERO"));
        }

        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
        }

        if (!resourcePacksDir.exists()) {
            resourcePacksDir.mkdirs();
        }

        /*if (!dataPacksDir.exists()) {
            dataPacksDir.mkdirs();
        }*/

        /*if (!SMDir.exists()) {
            SMDir.mkdirs();
        }*/

        long physicalTotal = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize() / 1048576;

        if (maxMemory > physicalTotal) {
            throw new LaunchException(getString("EXCEPTION_MAX_MEMORY_TOO_BIG"));
        }

        String contentOfJsonFile;
        if (!minecraftVersionJsonFile.exists()) {
            throw new LaunchException(getString("EXCEPTION_VERSION_JSON_NOT_FOUND"));
        } else {
            contentOfJsonFile = Utils.readFileContent(minecraftVersionJsonFile);
        }
        if (!minecraftJarFile.exists()) {
            throw new LaunchException(getString("EXCEPTION_VERSION_NOT_FOUND"));
        }

        if (startLaunch != null) startLaunch.execute();

        JSONObject headJsonObject = new JSONObject(contentOfJsonFile);

        JSONObject javaVersionJO=headJsonObject.optJSONObject("javaVersion");
        int javaVersionInt=Utils.getJavaVersion(javaPath);
        if(javaVersionInt>-1&&javaVersionJO!=null){
            int majorVersion=javaVersionJO.optInt("majorVersion",-1);
            if(majorVersion!=-1&&javaVersionInt<majorVersion){
                throw new LaunchException(String.format(getString("EXCEPTION_JAVA_VERSION_TOO_LOW"),majorVersion,javaVersionInt));
            }
        }



        JSONArray libraries = headJsonObject.optJSONArray("libraries");
        File librariesFile = new File(gameDir, "libraries");
        List<String> librariesPaths = new ArrayList<>();
        List<Library>notFound=new LinkedList<>();
        //List<String> names = new ArrayList<>();
        for (int i = 0; i < libraries.length(); i++) {
            JSONObject library = libraries.optJSONObject(i);
            boolean meet = true;
            JSONArray rules = library.optJSONArray("rules");
            if (rules != null) {
                meet = isMeetConditions(rules, false, false);
            }
            if (meet) {
                String name = library.optString("name");
                String[] nameSplit = name.split(":");
                //String libName = nameSplit[1];
                //if (!names.contains(libName)) {
                String libraryFileName = nameSplit[1] + "-" + nameSplit[2] + ".jar";
                String libraryFileAndDirectoryName = nameSplit[0].replace(".", "/") + "/" + nameSplit[1] + "/" + nameSplit[2];
                File libraryFile = new File(new File(librariesFile, libraryFileAndDirectoryName), libraryFileName);

                if (libraryFile.exists()) {
                    if (!librariesPaths.contains(libraryFile.getAbsolutePath())) {
                        librariesPaths.add(libraryFile.getAbsolutePath());
                    }
                }else{
                    Library lb=new Library(library);
                    if(!notFound.contains(lb)&&library.has("downloads")&&library.optJSONObject("downloads").has("artifact"))notFound.add(lb);
                    //等循环完成之后就抛出错误LibraryDefectException（notFound），返回之后提示要下载库
                }
                //names.add(libName);
                //}
            }
        }

        if(notFound.size()>0){
            throw new LibraryDefectException(notFound);
        }


        List<String> arguments = new LinkedList<>();
        List<String> minecraftArguments = new LinkedList<>();
        List<String> jvmArguments = new LinkedList<>();
        String id = headJsonObject.optString("id", "1.0");


        JSONObject assetIndexObject = headJsonObject.optJSONObject("assetIndex");

        String assetsIndex = assetIndexObject.optString("id");

        //String jvmArgumentsBuilder=null;

            /*18wXXa;arguments
            16wXXa;minecraftArguments
            17w44a;arguments
            17w42a;minecraftArguments
            3D Shareware v1.34;arguments
            1.RV-Pre1; minecraftArguments
            1.13 arguments
            1.12 minecraftArguments
            */

        if (id.startsWith("1.")) {
            if (!id.startsWith("1.RV-Pre1")) {
                String[] ids = id.split("\\.");
                //int id1stPart=Integer.parseInt(ids[0]);
                int id2ndPart = Integer.parseInt(ids[1].substring(0, numberOfAStringStartInteger(ids[1])));

                if (id2ndPart >= 13) {
                    getGameArguments(headJsonObject, isDemo, customScreenSize, minecraftArguments);
                    getJavaVirtualMachineArguments(headJsonObject, isDemo, customScreenSize, jvmArguments);
                } else {
                    minecraftArguments.addAll(Arrays.asList(clearRedundantSpaces(headJsonObject.optString("minecraftArguments")).split(" ")));
                }
            } else {
                minecraftArguments.addAll(Arrays.asList(clearRedundantSpaces(headJsonObject.optString("minecraftArguments")).split(" ")));
            }
        } else {
            char[] idChars = id.toCharArray();
            if (idChars[2] == 'w') {
                String[] idsForSnapshot = id.split("w");
                if (Integer.parseInt(idsForSnapshot[0]) > 17) {
                    getGameArguments(headJsonObject, isDemo, customScreenSize, minecraftArguments);
                    getJavaVirtualMachineArguments(headJsonObject, isDemo, customScreenSize, jvmArguments);
                } else if (Integer.parseInt(idsForSnapshot[0]) < 17) {
                    minecraftArguments.addAll(Arrays.asList(clearRedundantSpaces(headJsonObject.optString("minecraftArguments")).split(" ")));
                } else /*if (Integer.parseInt(idsForSnapshot[0]) == 17) */ {
                    int partOfWeekNumber = Integer.parseInt(idsForSnapshot[1].substring(0,/*idsForSnapshot[1].length()-1*/numberOfAStringStartInteger(idsForSnapshot[1])));
                    if (partOfWeekNumber >= 43) {
                        getGameArguments(headJsonObject, isDemo, customScreenSize, minecraftArguments);
                        getJavaVirtualMachineArguments(headJsonObject, isDemo, customScreenSize, jvmArguments);
                    } else {
                        minecraftArguments.addAll(Arrays.asList(clearRedundantSpaces(headJsonObject.optString("minecraftArguments")).split(" ")));
                    }
                }
            } else {
                if (id.equals("3D Shareware v1.34")) {
                    getGameArguments(headJsonObject, isDemo, customScreenSize, minecraftArguments);
                    getJavaVirtualMachineArguments(headJsonObject, isDemo, customScreenSize, jvmArguments);
                } else {
                    minecraftArguments.addAll(Arrays.asList(clearRedundantSpaces(headJsonObject.optString("minecraftArguments")).split(" ")));
                }
            }
        }


        String mainClass = headJsonObject.optString("mainClass", "net.minecraft.client.main.Main");
        File nativesFolder = new File(minecraftVersionJsonFile.getParentFile(), Utils.getNativesDirName());
        StringBuilder librariesString = new StringBuilder();
        for (String librariesPath : librariesPaths) {
            librariesString.append(librariesPath).append(File.pathSeparator);
        }
        librariesString.append(minecraftJarFile.getAbsolutePath());

        /*if (loadSM) {
            mainClass = "com.mrshiehx.simplemod.loader.Main";
            arguments="--smDir ${sm_directory} --mainClass ${main_class} "+arguments;
            File smloaderFile = new File("simplemod-loader-1.0.jar");
            Utils.copyFile(new File(MinecraftLauncherX.class.getResource("assets/simplemod-loader-1.0.jar").getFile()), smloaderFile);
            librariesString.append(File.pathSeparator).append(smloaderFile.getAbsolutePath().replace("\\", "\\\\"));
        }*/

        String assetsPath = (assetsDir.getAbsolutePath());

        if (assetsIndex.equals("legacy")) {
            assetsPath = new File(assetsDir, "virtual/legacy").getAbsolutePath();
        }


        for (int i = 0; i < minecraftArguments.size(); i++) {
                /*boolean replace = true;
                String replaceTo = null;*/
            String source;
            String s = minecraftArguments.get(i);

            if (s.contains(source = "${main_class}")) {
                minecraftArguments.set(i, s.replace(source, headJsonObject.optString("mainClass", "net.minecraft.client.main.Main")));
            } else if (s.contains(source = "${auth_player_name}")) {
                minecraftArguments.set(i, s.replace(source, playerName));
            } else if (s.contains(source = "${version_name}")) {
                minecraftArguments.set(i, s.replace(source, "MCLX " + MCLX_VERSION));
            } else if (s.contains(source = "${version_type}")) {
                minecraftArguments.set(i, s.replace(source, "MCLX " + MCLX_VERSION));
            } else if (s.contains(source = "${auth_access_token}")) {
                minecraftArguments.set(i, s.replace(source, accessToken));
            } else if (s.contains(source = "${auth_session}")) {
                minecraftArguments.set(i, s.replace(source, accessToken));
            } else if (s.contains(source = "${game_directory}")) {
                minecraftArguments.set(i, s.replace(source, gameDir.getAbsolutePath()));
            } else if (s.contains(source = "${assets_root}")) {
                minecraftArguments.set(i, s.replace(source, assetsDir.getAbsolutePath()));
            } else if (s.contains(source = "${assets_index_name}")) {
                minecraftArguments.set(i, s.replace(source, assetsIndex));
            } else if (s.contains(source = "${auth_uuid}")) {
                minecraftArguments.set(i, s.replace(source, isEmpty(uuid)?UUID.nameUUIDFromBytes(("OfflinePlayer:"+playerName).getBytes(StandardCharsets.UTF_8)).toString().replace("-",""):uuid));
            } else if (s.contains(source = "${user_type}")) {
                minecraftArguments.set(i, s.replace(source, "mojang"));
            } else if (s.contains(source = "${game_assets}")) {
                minecraftArguments.set(i, s.replace(source, assetsPath));
            } else if (s.contains(source = "${user_properties}")) {
                minecraftArguments.set(i, s.replace(source, "{}"));
            } else if (s.contains(source = "${resolution_width}")) {
                minecraftArguments.set(i, s.replace(source, String.valueOf(width)));
            } else if (s.contains(source = "${resolution_height}")) {
                minecraftArguments.set(i, s.replace(source, String.valueOf(height)));
            }
        }

        /*for (int i = 0; i < minecraftArguments.size(); i++) {
            boolean replace = true;
            String replaceTo = null;
            String source = null;
            switch (minecraftArguments.get(i)) {
                case "${main_class}" -> {
                    source = "${main_class}";
                    replaceTo = headJsonObject.optString("mainClass", "net.minecraft.client.main.Main");
                }
                case "${auth_player_name}" -> {
                    source = "${auth_player_name}";
                    replaceTo = playerName;
                }
                case "${version_name}" -> {
                    source = "${version_name}";
                    replaceTo = "MCLX " + MCLX_VERSION;
                }
                case "${version_type}" -> {
                    source = "${version_type}";
                    replaceTo = "MCLX " + MCLX_VERSION;
                }
                case "${auth_access_token}" -> {
                    source = "${auth_access_token}";
                    replaceTo = accessToken;
                }
                case "${auth_session}" -> {
                    source = "${auth_session}";
                    replaceTo = accessToken;
                }
                case "${game_directory}" -> {
                    source = "${game_directory}";
                    replaceTo = gameDir.getAbsolutePath();
                }
                case "${assets_root}" -> {
                    source = "${assets_root}";
                    replaceTo = assetsDir.getAbsolutePath();
                }
                case "${assets_index_name}" -> {
                    source = "${assets_index_name}";
                    replaceTo = assetsIndex;
                }
                case "${auth_uuid}" -> {
                    source = "${auth_uuid}";
                    replaceTo = uuid;
                }
                case "${user_type}" -> {
                    source = "${user_type}";
                    replaceTo = "mojang";
                }
                case "${game_assets}" -> {
                    source = "${game_assets}";
                    replaceTo = assetsPath;
                }
                case "${user_properties}" -> {
                    source = "${user_properties}";
                    replaceTo = "{}";
                }
                case "${resolution_width}" -> {
                    source = "${resolution_width}";
                    replaceTo = String.valueOf(width);
                }
                case "${resolution_height}" -> {
                    source = "${resolution_height}";
                    replaceTo = String.valueOf(height);
                }
                default -> replace = false;
            }
            if (replace && replaceTo != null &&!Utils.isEmpty(source)) {
                String get=minecraftArguments.get(i);
                if(get.contains(source)){
                    minecraftArguments.set(i, get.replace(source,replaceTo));
                }
            }
        }*/

        minecraftArguments.add("--resourcePackDir");
        minecraftArguments.add(resourcePacksDir.getAbsolutePath());
        if (fullscreen) minecraftArguments.add("--fullscreen");

        /*String parsed = minecraftArguments.replace("${main_class}", headJsonObject.optString("mainClass", "net.minecraft.client.main.Main"))
                .replace("${auth_player_name}", playerName)
                .replace("${version_name}", "\"MCLX " + MCLX_VERSION + "\"")
                .replace("${version_type}", "\"MCLX " + MCLX_VERSION + "\"")
                .replace("${auth_access_token}", accessToken)
                .replace("${game_directory}", addDoubleQuotationMarkToPath(gameDir.getAbsolutePath()))
                .replace("${assets_root}", addDoubleQuotationMarkToPath(assetsDir.getAbsolutePath()))
                .replace("${assets_index_name}",*//*ids[0]+"."+ids[1]*//*assetsIndex)
                .replace("${auth_uuid}", uuid)
                .replace("${user_type}", "mojang")
                .replace("${auth_session}", accessToken)
                .replace("${game_assets}", assetsPath)
                .replace("${user_properties}", "{}")
                .replace("${resolution_width}",String.valueOf(width))
                .replace("${resolution_height}",String.valueOf(height));*/

        /*parsed = parsed + " --resourcePackDir " + addDoubleQuotationMarkToPath(resourcePacksDir.getAbsolutePath());
        if (fullscreen) {
            parsed = parsed + " --fullscreen";
        }*/


        File[]nativesFiles=nativesFolder.listFiles();
        if (!nativesFolder.exists()||nativesFiles==null||nativesFiles.length==0) throw new EmptyNativesException(libraries);

        //String javaArgument;
        if (jvmArguments.size() > 0) {
            jvmArguments.add(0, "-Xmn" + miniMemory + "m");
            jvmArguments.add(1, "-Xmx" + maxMemory + "m");
            jvmArguments.add(2, "-Dfile.encoding=UTF-8");

            for (int i = 0; i < jvmArguments.size(); i++) {
                /*boolean replace = true;
                String replaceTo = null;*/
                String source;
                String s = jvmArguments.get(i);

                if (s.contains(source = "${natives_directory}")) {
                    jvmArguments.set(i, s.replace(source, nativesFolder.getAbsolutePath()));
                } else if (s.contains(source = "${launcher_name}")) {
                    jvmArguments.set(i, s.replace(source, "MCLX"));
                } else if (s.contains(source = "${launcher_version}")) {
                    jvmArguments.set(i, s.replace(source, MCLX_VERSION));
                } else if (s.contains(source = "${classpath}")) {
                    jvmArguments.set(i, s.replace(source, librariesString.toString()));
                }
            }

            /*String str="\""+jvmArgumentsBuilder+"\"";
            //str=str.replace(" ","\" \"");
            str="\"-Dfile.encoding=UTF-8\" \"-Xmn"+miniMemory+"m\" \"-Xmx"+maxMemory+"m\" "+str
                    .replace("${natives_directory}",nativesFolder.getAbsolutePath().replace("\\", "\\\\"))
                    .replace("${launcher_name}","MCLX")
                    .replace("${launcher_version}",String.valueOf(MCLX_VERSION))
                    .replace("${classpath}",librariesString)
            .replace("\"-Dos.name=Windows\" \"10\"","\"-Dos.name=Windows 10\"");
            javaArgument=str;*/
        } else {
            jvmArguments.add("-Xmn" + miniMemory + "m");
            jvmArguments.add("-Xmx" + maxMemory + "m");
            jvmArguments.add("-Djava.library.path=" + nativesFolder.getAbsolutePath());
            jvmArguments.add("-Dminecraft.launcher.brand=MCLX");
            jvmArguments.add("-Dminecraft.launcher.version=" + MCLX_VERSION);
            jvmArguments.add("-cp");
            jvmArguments.add(librariesString.toString());


            //String javaLibraryPath = "\"-Djava.library.path=" + nativesFolder.getAbsolutePath().replace("\\", "\\\\") + "\"";

            //javaArgument="-Xmn"+miniMemory+"m -Xmx"+maxMemory+"m "+javaLibraryPath+" -Dminecraft.launcher.brand=MCLX -Dminecraft.launcher.version=" + MCLX_VERSION + " -cp \"" + librariesString + "\"";
        }

        arguments.add(javaPath);
        arguments.addAll(jvmArguments);
        arguments.add(mainClass);
        arguments.addAll(minecraftArguments);

        return arguments;
    }


    /**
     * Get Minecraft Launch Command, see {@link com.mrshiehx.mclx.modules.MinecraftLauncher#getMinecraftLaunchCommandArguments}
     *
     * @author MrShiehX
     */
    public static String getMinecraftLaunchCommand(
            File minecraftJarFile,
            File minecraftVersionJsonFile,
            File gameDir,
            File assetsDir,
            File resourcePacksDir,
            String playerName,
            String javaPath,
            int maxMemory,
            int miniMemory,
            int width,
            int height,
            boolean fullscreen,
            String accessToken,
            String uuid,
            boolean isDemo,
            boolean customScreenSize) throws
            LibraryDefectException,
            EmptyNativesException,
            LaunchException,
            IOException,
            JSONException{
        List<String> args = getMinecraftLaunchCommandArguments(minecraftJarFile,
                minecraftVersionJsonFile,
                gameDir,
                assetsDir,
                resourcePacksDir,
                playerName,
                javaPath,
                maxMemory,
                miniMemory,
                width,
                height,
                fullscreen,
                accessToken,
                uuid,
                isDemo,
                customScreenSize,
                null);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            String str = args.get(i);
            /*System.out.println(str);*/


            if (str.contains(" ")) {
                str = "\"" + str + "\"";
                if (str.contains("\\")) {
                    str = str.replace("\\", "\\\\");
                }
            }
            stringBuilder.append(str);
            if (i + 1 != args.size()) {
                stringBuilder.append(" ");
            }
        }
        String s=stringBuilder.toString();
        print(MinecraftLauncher.class,String.format("Copy Launch Command: %s",s));
        return s;
    }

    /**
     * 检测是否符合条件（jvm参数、游戏参数natives文件和依赖库文件）
     *
     * @param rules      规则
     * @param isDemo     游戏参数时用
     * @param customSize 游戏参数时用
     **/
    public static boolean isMeetConditions(JSONArray rules, boolean isDemo, boolean customSize) {
        //应将disallow的jsonObject放在前面

        if (rules == null || rules.length() <= 0) return false;


        for (int i = 0; i < rules.length(); i++) {
            JSONObject first = rules.optJSONObject(i);
            if (first != null) {
                if ((first.has("os") || first.has("features"))) {
                    JSONObject os = first.optJSONObject("os");
                    JSONObject features = first.optJSONObject("features");
                    if (os != null) {
                        String action = first.optString("action");

                        String name = os.optString("name");
                        String version = os.optString("version");
                        String arch = os.optString("arch");


                        boolean hasNoConditionOfRules = false;
                        for (int j = 0; j < rules.length(); j++) {
                            if (j != i) {
                                JSONObject second = rules.optJSONObject(j);
                                if (second != null) {

                                    if (!second.has("name") && !second.has("version") && !second.has("arch")) {
                                        if ("allow".equals(second.optString("action"))) {
                                            hasNoConditionOfRules = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (isEmpty(name) && isEmpty(version) && isEmpty(arch)) {
                            if ("disallow".equals(action)) {
                                return false;
                            } else {
                                if (!hasNoConditionOfRules) {
                                    return false;
                                }
                            }
                        }
                        boolean base = true;
                        boolean namae=false;
                        if (!isEmpty(name)) {
                            base = (namae=name.equals(OperatingSystem.CURRENT_OS.getCheckedName()));
                        }
                        if (base/*如果osname匹配了才能判断version是否匹配*/&&!isEmpty(version)) {
                            String sversion = /*OperatingSystem.SYSTEM_VERSION*/System.getProperty("os.version");
                            if ("^10\\.".equals(version)) {
                                base = base && "10.0".equals(sversion);
                            } else {
                                base=base&&Pattern.compile(version).matcher(sversion).matches();
                            }
                            /*if (!Utils.isEmpty(sversion) && sversion.endsWith("0"))
                                sversion = sversion.substring(0, sversion.length() - 1);
                            //System.out.println("regex: "+regex+", version: "+version+", matches: "+Pattern.matches(regex,version));
                            base= base&&Pattern.matches(version, sversion);*/
                        }
                        if (!isEmpty(arch)) {
                            if(!isEmpty(name)){
                                if(namae){
                                    base = base && arch.equals(System.getProperty("os.arch"));
                                }
                            }else {
                                base = base && arch.equals(System.getProperty("os.arch"));
                            }
                        }
                        if (base) {
                            if ("disallow".equals(action)) {
                                return false;
                            }
                        } else {
                            if ("allow".equals(action)) {
                                if (!hasNoConditionOfRules) {
                                    return false;
                                }
                            }

                        }


                        /*String name = os.optString("name");
                        if (name.equals(OperatingSystem.CURRENT_OS.getCheckedName()) && first.optString("action").equals("allow")) {
                            if (os.has("arch"))
                                allow = os.optString("arch").equals(System.getProperty("os.arch"));
                            if (os.has("version")) {
                                String regex = os.optString("version");
                                String version = System.getProperty("os.version");
                                if (!Utils.isEmpty(version) && version.endsWith("0"))
                                    version = version.substring(0, version.length() - 1);
                                //System.out.println("regex: "+regex+", version: "+version+", matches: "+Pattern.matches(regex,version));
                                allow = Pattern.matches(regex, version);

                            }
                            //if(first.optString("action").equals("disallow"))return false;

                        } else if (os.has("arch")) {
                            allow = os.optString("arch").equals(System.getProperty("os.arch"));
                        } else
                            allow = !(!os.has("arch") && !os.has("name") && "disallow".equals(first.optString("action")));*/
                    }
                    if (features != null) {
                        boolean allow2 = first.optString("action").equals("allow");
                        if (features.has("is_demo_user")) {
                            boolean is_demo_user = features.optBoolean("is_demo_user");
                            if (!(allow2 && isDemo == is_demo_user)) return false;
                        } else if (features.has("has_custom_resolution")) {
                            boolean has_custom_resolution = features.optBoolean("has_custom_resolution");
                            if (!(allow2 && has_custom_resolution == customSize)) {
                                return false;
                            }
                        } else if (!allow2) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Launch Minecraft, see {@link com.mrshiehx.mclx.modules.MinecraftLauncher#getMinecraftLaunchCommandArguments}
     *
     * @author MrShiehX
     */
    public static Process launchMinecraft(
            File minecraftJarFile,
            File minecraftVersionJsonFile,
            File gameDir,
            File assetsDir,
            File resourcePacksDir,
            String playerName,
            String javaPath,
            int maxMemory,
            int miniMemory,
            int width,
            int height,
            boolean fullscreen,
            String accessToken,
            String uuid,
            JTextComponent log,
            boolean isDemo,
            boolean customScreenSize) throws
            LibraryDefectException,
            EmptyNativesException,
            LaunchException,
            IOException,
            JSONException{
        if (log != null) log.setText(null);
        StringBuilder stringBuilder = new StringBuilder();
        List<String> args=getMinecraftLaunchCommandArguments(minecraftJarFile,
                minecraftVersionJsonFile,
                gameDir,
                assetsDir,
                resourcePacksDir,
                playerName,
                javaPath,
                maxMemory,
                miniMemory,
                width,
                height,
                fullscreen,
                accessToken,
                uuid,
                isDemo,
                customScreenSize,
                () -> {
                    if (log != null) log.setText(getString("MESSAGE_STARTING_GAME") + "\n");
                });
        for (int i = 0; i < args.size(); i++) {
            String str = args.get(i);
            /*System.out.println(str);*/


            if (str.contains(" ")) {
                str = "\"" + str + "\"";
                if (str.contains("\\")) {
                    str = str.replace("\\", "\\\\");
                }
            }
            stringBuilder.append(str);
            if (i + 1 != args.size()) {
                stringBuilder.append(" ");
            }
        }
        print(MinecraftLauncher.class,String.format("Launch Command: %s",stringBuilder));
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    private static void getJavaVirtualMachineArguments(JSONObject headJsonObject, boolean isDemo, boolean customScreenSize, List<String> args) {
        getArguments(headJsonObject, "jvm", isDemo, customScreenSize, args);
    }

    private static void getGameArguments(JSONObject headJsonObject, boolean isDemo, boolean customScreenSize, List<String> args) {
        getArguments(headJsonObject, "game", isDemo, customScreenSize, args);
    }

    private static void getArguments(JSONObject headJsonObject, String name, boolean isDemo, boolean customScreenSize, List<String> args) {
        //StringBuilder arguments = new StringBuilder();
        JSONObject argumentsArray = headJsonObject.optJSONObject("arguments");
        JSONArray array = argumentsArray.optJSONArray(name);
        for (int i = 0; i < array.length(); i++) {
            if (array.opt(i) instanceof String a) {
                args.add(a);
            } else if (array.opt(i) instanceof JSONObject) {
                JSONObject jsonObject = array.optJSONObject(i);
                if (jsonObject != null && jsonObject.has("value") && jsonObject.has("rules")) {
                    Object value = jsonObject.opt("value");
                    JSONArray rules = jsonObject.optJSONArray("rules");
                    if (value != null && rules != null) {
                        if (isMeetConditions(rules, isDemo, customScreenSize)) {

                            if (value instanceof JSONArray value2) {
                                for (int k = 0; k < value2.length(); k++) {
                                    if (value2.opt(k) instanceof String) {
                                        args.add(Utils.valueOf(value2.opt(k)));
                                    }
                                }
                            } else {
                                args.add(Utils.valueOf(value));
                            }
                        }
                    }
                }
            }
        }
        //return arguments.substring(0, arguments.length()-1);
    }


    private static String[] split(String src) {
        List<String> list = new ArrayList<>();
        /*List<Boolean> yinyongs=new ArrayList<>();
        String[]split=src.split(String.valueOf(symbol));*/
        boolean yinyong = false;
        for (int i = 0; i < src.length(); i++) {
            char str = src.charAt(i);
            if (str == '\"') yinyong = !yinyong;
            if (!yinyong) {
                if (str != ' ') {
                    if (i == 0) list.add(String.valueOf(str));
                    else list.set(list.size() - 1, list.get(list.size() - 1) + str);
                } else {
                    list.add("");
                }
            } else {
                list.set(list.size() - 1, list.get(list.size() - 1) + str);
            }
        }
        return list.toArray(new String[0]);
    }

    private static String clearRedundantSpaces(String string) {
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
