package com.mrshiehx.mclx.modules;

import com.mrshiehx.mclx.exceptions.LaunchException;
import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.utils.OperatingSystem;
import com.mrshiehx.mclx.utils.Utils;
import com.sun.management.OperatingSystemMXBean;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.text.JTextComponent;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;

public class MinecraftLauncher {
    public static final int LAUNCH_MODE_EXECUTE = 0;
    public static final int LAUNCH_MODE_GET_COMMAND = 1;

    /**
     * Launch Minecraft
     *
     * @param minecraftJarFile         jar file
     * @param minecraftVersionJsonFile json file
     * @param gameDir                  game directory
     * @param assetsDir                assets directory
     * @param resourcePacksDir         resource packs directory
     * @param dataPacksDir             data packs directory
     * @param loadSM                   is load simple mods
     * @param SMDir                    simple mods dir
     * @param playername               player name
     * @param javaPath                 java path
     * @param maxMemory                max memory
     * @param miniMemory               mini memory
     * @param width                    window width
     * @param height                   window height
     * @param fullscreen               is window fullscreen
     * @param accessToken              access token of official account
     * @param uuid                     uuid of official account
     * @param mode                     method mode: <code>LAUNCH_MODE_EXECUTE</code> or <code>LAUNCH_MODE_GET_COMMAND</code>
     * @param log                      a JTextComponent to print logs, nullable
     * @param isDemo                   is Minecraft demo
     * @param customScreenSize         does player custom the size of screen
     * @return If the <code>mode</code> is <code>LAUNCH_MODE_EXECUTE</code> than it will execute the command of launching and returns a <code>java.lang.Process</code>, else returns the command of launching
     * @throws LaunchException launch exception
     * @throws IOException     io or file exception
     * @throws JSONException   exception of parsing json
     * @author MrShiehX
     * @updateDate September 20, 2021
     */
    public static Object launchMinecraft(
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
            String accessToken,
            String uuid,
            int mode,
            @Nullable JTextComponent log,
            boolean isDemo,
            boolean customScreenSize) throws
            LaunchException,
            IOException,
            JSONException {
        if (mode == LAUNCH_MODE_EXECUTE && log != null) log.setText(null);
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
        if (null == dataPacksDir) {
            dataPacksDir = new File(gameDir, "datapacks");
        }
        if (null == SMDir) {
            SMDir = new File(gameDir, "simplemods");
        }
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

        if (!dataPacksDir.exists()) {
            dataPacksDir.mkdirs();
        }

        if (!SMDir.exists()) {
            SMDir.mkdirs();
        }

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1048576;

        if (maxMemory > physicalTotal) {
            throw new LaunchException(getString("EXCEPTION_MAX_MEMORY_TOO_BIG"));
        }

        String contentOfJsonFile;
        if (!minecraftVersionJsonFile.exists()) {
            throw new LaunchException(getString("EXCEPTION_VERSION_JSON_NOT_FOUND"));
        } else {
            contentOfJsonFile = readFileContent(minecraftVersionJsonFile);
        }
        if (!minecraftJarFile.exists()) {
            throw new LaunchException(getString("EXCEPTION_VERSION_NOT_FOUND"));
        }
        if (mode == LAUNCH_MODE_EXECUTE && log != null) log.setText(getString("MESSAGE_STARTING_GAME") + "\n");
        JSONObject headJsonObject = new JSONObject(contentOfJsonFile);
        JSONArray libraries = headJsonObject.optJSONArray("libraries");
        File librariesFile = addTo(gameDir, "libraries");
        List<String> librariesPaths = new ArrayList<>();
        //List<String> librariesParentsPaths = new ArrayList();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < libraries.length(); i++) {
            JSONObject library = libraries.optJSONObject(i);
            String name = library.optString("name");
            String[] nameSplit = name.split(":");
            String libName = nameSplit[1];
            if (!names.contains(libName)) {
                String libraryFileName = nameSplit[1] + "-" + nameSplit[2] + ".jar";
                String libraryFileAndDirectoryName = nameSplit[0].replace(".", "/") + "/" + nameSplit[1] + "/" + nameSplit[2];
                File libraryFile = new File(new File(librariesFile, libraryFileAndDirectoryName), libraryFileName);
                /*if (librariesPaths.size() >= 1) {
                    for (int n = 0; n < librariesPaths.size(); n++) {
                        if (addSeparatorToPath(librariesPaths.get(n)).startsWith(addSeparatorToPath(libraryFile.getParentFile().getParent()))) {
                            librariesPaths.remove(n);
                            break;
                        }
                    }
                }*/

                if (libraryFile.exists() && !librariesPaths.contains(libraryFile.getAbsolutePath())) {
                    librariesPaths.add(libraryFile.getAbsolutePath());
                }
                names.add(libName);
            }

        }
        String arguments;
        String id = headJsonObject.optString("id", "1.0");


        JSONObject assetIndexObject = headJsonObject.optJSONObject("assetIndex");

        String assetsIndex = assetIndexObject.optString("id");

        String jvmArgumentsBuilder=null;

        if (id.startsWith("1.")) {
            if (!id.startsWith("1.RV-Pre1")) {
                String[] ids = id.split("\\.");
                //int id1stPart=Integer.parseInt(ids[0]);
                int id2ndPart = Integer.parseInt(ids[1].substring(0, numberOfAStringStartInteger(ids[1])));

                if (id2ndPart >= 13) {
                    arguments = (getGameArguments(headJsonObject,isDemo,customScreenSize));
                    jvmArgumentsBuilder = (getJavaVirtualMachineArguments(headJsonObject,isDemo,customScreenSize));
                } else {
                    arguments = (headJsonObject.optString("minecraftArguments"));
                }
            } else {
                arguments = (headJsonObject.optString("minecraftArguments"));
            }
        } else {
            char[] idChars = id.toCharArray();

            if (idChars[2] == 'w') {
                String[] idsForSnapshot = id.split("w");
                if (Integer.parseInt(idsForSnapshot[0]) > 17) {
                    arguments = (getGameArguments(headJsonObject,isDemo,customScreenSize));
                    jvmArgumentsBuilder = (getJavaVirtualMachineArguments(headJsonObject,isDemo,customScreenSize));
                } else if (Integer.parseInt(idsForSnapshot[0]) < 17) {
                    arguments = (headJsonObject.optString("minecraftArguments"));
                } else /*if (Integer.parseInt(idsForSnapshot[0]) == 17) */{
                    int partOfWeekNumber = Integer.parseInt(idsForSnapshot[1].substring(0,/*idsForSnapshot[1].length()-1*/numberOfAStringStartInteger(idsForSnapshot[1])));
                    if (partOfWeekNumber >= 43) {
                        arguments = (getGameArguments(headJsonObject,isDemo,customScreenSize));
                        jvmArgumentsBuilder = (getJavaVirtualMachineArguments(headJsonObject,isDemo,customScreenSize));
                    } else {
                        arguments = (headJsonObject.optString("minecraftArguments"));
                    }
                }
            } else {
                if (id.equals("3D Shareware v1.34")) {
                    arguments = (getGameArguments(headJsonObject,isDemo,customScreenSize));
                    jvmArgumentsBuilder = (getJavaVirtualMachineArguments(headJsonObject,isDemo,customScreenSize));
                } else {
                    arguments = (headJsonObject.optString("minecraftArguments"));
                }
            }
        }


        String mainClass = headJsonObject.optString("mainClass", "net.minecraft.client.main.Main");
        File nativesFolder = addTo(minecraftVersionJsonFile.getParentFile(), "natives");
        StringBuilder librariesString = new StringBuilder();
        for (String librariesPath : librariesPaths) {
            librariesString.append(librariesPath.replace("\\", "\\\\")).append(File.pathSeparator);
        }
        librariesString.append(minecraftJarFile.getAbsolutePath().replace("\\", "\\\\"));

        if (loadSM) {
            mainClass = "com.mrshiehx.simplemod.loader.Main";
            arguments="--smDir ${sm_directory} --mainClass ${main_class} "+arguments;
            File smloaderFile = new File("simplemod-loader-1.0.jar");
            Utils.copyFile(new File(MinecraftLauncherX.class.getResource("assets/simplemod-loader-1.0.jar").getFile()), smloaderFile);
            librariesString.append(File.pathSeparator).append(smloaderFile.getAbsolutePath().replace("\\", "\\\\"));
        }

        String assetsPath = addShuangyinhaoToPath(assetsDir.getAbsolutePath());

        if (assetsIndex.equals("legacy")) {
            String s = getFileSeparator(assetsDir.getAbsolutePath());
            assetsPath = addShuangyinhaoToPath(addSeparatorToPath(assetsDir.getAbsolutePath()) + "virtual" + s + "legacy");
        }

        String parsed = arguments.toString().replace("${main_class}", headJsonObject.optString("mainClass", "net.minecraft.client.main.Main"))
                .replace("${sm_directory}", addShuangyinhaoToPath(SMDir.getAbsolutePath()))
                .replace("${auth_player_name}", playername)
                .replace("${version_name}", "\"MCLX " + MCLX_VERSION + "\"")
                .replace("${version_type}", "\"MCLX " + MCLX_VERSION + "\"")
                .replace("${auth_access_token}", accessToken)
                .replace("${game_directory}", addShuangyinhaoToPath(gameDir.getAbsolutePath()))
                .replace("${assets_root}", addShuangyinhaoToPath(assetsDir.getAbsolutePath()))
                .replace("${assets_index_name}",/*ids[0]+"."+ids[1]*/assetsIndex)
                .replace("${auth_uuid}", uuid)
                .replace("${user_type}", "mojang")
                .replace("${auth_session}", accessToken)
                .replace("${game_assets}", assetsPath)
                .replace("${user_properties}", "{}")
                .replace("${resolution_width}",String.valueOf(width))
                .replace("${resolution_height}",String.valueOf(height));

        parsed = parsed + " --resourcePackDir " + addShuangyinhaoToPath(resourcePacksDir.getAbsolutePath()) + " --dataPackDir " + addShuangyinhaoToPath(dataPacksDir.getAbsolutePath());
        if (fullscreen) {
            parsed = parsed + " --fullscreen";
        }

        //parsed = parsed + " --width " + width + " --height " + height;

        
        if(!nativesFolder.exists())throw new LaunchException(getString("EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"));
        String javaArgument;
        //System.out.println(jvmArgumentsBuilder);
        if(jvmArgumentsBuilder!=null){
            String str="\""+jvmArgumentsBuilder.toString()+"\"";
            str=str.replace(" ","\" \"");
            str="\"-Xmn"+miniMemory+"m\" \"-Xmx"+maxMemory+"m\" "+str
                    .replace("${natives_directory}",nativesFolder.getAbsolutePath().replace("\\", "\\\\"))
                    .replace("${launcher_name}","MCLX")
                    .replace("${launcher_version}",String.valueOf(MCLX_VERSION))
                    .replace("${classpath}",librariesString)
            .replace("\"-Dos.name=Windows\" \"10\"","\"-Dos.name=Windows 10\"");
            //str=str+" \"-Xmn"+miniMemory+"m\" \"-Xmx"+maxMemory+"m\"";
            javaArgument=str;
        }else{

            String javaLibraryPath = "\"-Djava.library.path=" + nativesFolder.getAbsolutePath().replace("\\", "\\\\") + "\"";

            javaArgument="-Xmn"+miniMemory+"m -Xmx"+maxMemory+"m "+javaLibraryPath+" -Dminecraft.launcher.brand=MCLX -Dminecraft.launcher.version=" + MCLX_VERSION + " -cp \"" + librariesString + "\"";
        }
        
        
        


        String command = String.format("%s %s %s %s",addShuangyinhaoToPath(javaPath),javaArgument,mainClass,parsed);
        //System.out.println(command+"\n");
        /*if(logsOutput!=null) {net.minecraft.client.main.Main --username XPlayer --version "MCLX 1.0" --gameDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft" --assetsDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\assets" --assetIndex 1.16  --accessToken 0 --userType legacy --versionType "MCLX 1.0" --resourcePackDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\resourcepacks" --dataPackDir "C:\\Users\\Administrator\\Documents\\MCLX\\.minecraft\\datapacks" --width 854 --height 480
            System.setOut(new PrintStream(logsOutput));
        }*/
        if (mode == LAUNCH_MODE_EXECUTE) return Runtime.getRuntime().exec(command);
        else return command;
    }

    private static boolean isFuheTiaojian(JSONArray rules, boolean isDemo, boolean customSize) {
        if (rules == null || rules.length() <= 0) return false;
        for (int i = 0; i < rules.length(); i++) {
            JSONObject first = rules.optJSONObject(i);

            if (first != null && (first.has("os") || first.has("features"))) {
                JSONObject os = first.optJSONObject("os");
                JSONObject features = first.optJSONObject("features");
                if (os != null) {
                    String name = os.optString("name");
                    if (name.equals(OperatingSystem.CURRENT_OS.getCheckedName())&&first.optString("action").equals("allow")){
                        if(os.has("arch"))
                            if(!os.optString("arch").equals(System.getProperty("os.arch")))return false;
                        if(os.has("version")){
                            String regex=os.optString("version");
                            String version=System.getProperty("os.version");
                            if(!Utils.isEmpty(version)&&version.endsWith("0"))version=version.substring(0,version.length()-1);
                            //System.out.println("regex: "+regex+", version: "+version+", matches: "+Pattern.matches(regex,version));
                            if(!Pattern.matches(regex,version))return false;

                        }
                    }else if(os.has("arch")){
                        if(!os.optString("arch").equals(System.getProperty("os.arch")))return false;
                    }
                    //else if (name.equals("unknown")&&os.optString("arch").equals(System.getProperty("os.arch"))&&first.optString("action").equals("allow")){}
                    else return false;
                }
                if (features != null) {
                    boolean allow=first.optString("action").equals("allow");
                    if(features.has("is_demo_user")) {
                        boolean is_demo_user = features.optBoolean("is_demo_user");
                        if(!allow||isDemo!=is_demo_user)return false;
                    }else if(features.has("has_custom_resolution")) {
                        boolean has_custom_resolution = features.optBoolean("has_custom_resolution");
                        if(!allow||has_custom_resolution!=customSize)return false;
                    }else{
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static String getJavaVirtualMachineArguments(JSONObject headJsonObject,boolean isDemo,boolean customScreenSize) {
        return getArguments(headJsonObject,"jvm",isDemo,customScreenSize);
    }

    private static String getGameArguments(JSONObject headJsonObject,boolean isDemo,boolean customScreenSize) {
        return getArguments(headJsonObject,"game",isDemo,customScreenSize);
    }
    private static String getArguments(JSONObject headJsonObject,String name,boolean isDemo,boolean customScreenSize) {
        StringBuilder arguments = new StringBuilder();
        JSONObject argumentsArray = headJsonObject.optJSONObject("arguments");
        JSONArray gameArray = argumentsArray.optJSONArray(name);
        for (int i = 0; i < gameArray.length(); i++) {
            if (gameArray.opt(i) instanceof String) {
                arguments.append(gameArray.opt(i)).append(" ");
            } else if (gameArray.opt(i) instanceof JSONObject) {
                JSONObject jsonObject = gameArray.optJSONObject(i);
                if (jsonObject != null && jsonObject.has("value") && jsonObject.has("rules")) {
                    Object value = jsonObject.opt("value");
                    JSONArray rules = jsonObject.optJSONArray("rules");
                    if (value != null && rules != null) {
                        if(isFuheTiaojian(rules,isDemo,customScreenSize)){

                            if(value instanceof JSONArray) {
                                JSONArray value2=(JSONArray) value;
                                for (int k = 0; k < value2.length(); k++) {
                                    if (value2.opt(k) instanceof String) {
                                        arguments.append(value2.opt(k)).append(" ");
                                    }
                                }
                            }else{
                                arguments.append(Utils.valueOf(value)).append(" ");
                            }
                        }
                    }
                }
            }
        }
        return arguments.substring(0, arguments.length()-1);
    }
}
