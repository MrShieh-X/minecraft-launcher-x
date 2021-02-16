package com.mrshiehx.mclx;

import java.util.Locale;

public class Strings {
    public static String APPLICATION_NAME;

    public static String BUTTON_START_NAME;
    public static String MENU_SETTINGS_NAME;
    public static String MENU_ABOUT_NAME;
    public static String MENU_NAME;
    public static String MENU_KILL_MINECRAFT;

    public static String SETTINGS_TEXTFIELD_PLAYERNAME_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_MAX_MEMORY_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_OS_MAX_MEMORY_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_JAVA_PATH_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_GAME_WINDOW_SIZE_TIP_TEXT;
    public static String SETTINGS_CHECKBOX_FULLSCREEN_TEXT;
    public static String SETTINGS_CHECKBOX_LOAD_SM_TEXT;
    public static String SETTINGS_BUTTON_WHAT_TEXT;
    public static String SETTINGS_CHECKBOX_CUSTOM_WORK_PATHS_TEXT;
    public static String SETTINGS_TEXTFIELD_CUSTOM_GAME_DIR_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_CUSTOM_ASSETS_DIR_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_CUSTOM_RESOURCE_PACK_DIR_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_CUSTOM_DATA_PACK_DIR_TIP_TEXT;
    public static String SETTINGS_TEXTFIELD_CUSTOM_SM_DIR_TIP_TEXT;
    public static String SETTINGS_BUTTON_BROWSE_TEXT;
    public static String SETTINGS_BUTTON_SAVE_TEXT;
    public static String SETTINGS_BROSE_FILE_TYPE_TEXT;
    public static String SETTINGS_BROSE_DIR_TYPE_TEXT;

    public static String DIALOG_WHAT_IS_SM_TEXT;
    public static String DIALOG_NO_MINECRAFT_DIR_TEXT;
    public static String DIALOG_TARGET_FILE_NOT_EXISTS_TEXT;
    public static String DIALOG_BUTTON_CANCEL_TEXT;
    public static String DIALOG_BUTTON_OK_TEXT;
    public static String DIALOG_BUTTON_YES_TEXT;
    public static String DIALOG_BUTTON_EXIT_TEXT;
    public static String DIALOG_ABOUT_DESCRIPTION;
    public static String DIALOG_CHOOSE_JAVA_FILE_TITLE;
    public static String DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE;
    public static String DIALOG_CHOOSE_GAME_DIR_TITLE;
    public static String DIALOG_CHOOSE_ASSETS_DIR_TITLE;
    public static String DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE;
    public static String DIALOG_CHOOSE_DATA_PACK_DIR_TITLE;
    public static String DIALOG_CHOOSE_SM_DIR_TITLE;
    public static String DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT;
    public static String DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT;
    public static String DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT;
    public static String DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT;
    public static String DIALOG_CHOOSE_TYPE_SM_DIR_TEXT;
    public static String DIALOG_TITLE_NOTICE;

    public static String MESSAGE_NOT_FOUND_TARGET_VERSION;
    public static String MESSAGE_NOT_FOUND_GAME_DIR;
    public static String MESSAGE_NOT_FOUND_ASSETS_DIR;
    public static String MESSAGE_NOT_FOUND_RESOURCE_PACKS_DIR;
    public static String MESSAGE_NOT_FOUND_DATA_PACKS_DIR;
    public static String MESSAGE_NOT_FOUND_SM_DIR;
    public static String MESSAGE_NOT_FOUND_JAVA;
    public static String MESSAGE_STARTING_GAME;
    public static String MESSAGE_FINISHED_GAME;

    public static String EXCEPTION_VERSION_JSON_NOT_FOUND;
    public static String EXCEPTION_VERSION_NOT_FOUND;
    public static String EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND;
    public static String EXCEPTION_JAVA_NOT_FOUND;
    public static String EXCEPTION_MAX_MEMORY_TOO_BIG;
    public static String EXCEPTION_MAX_MEMORY_IS_ZERO;

    static {
        if(Locale.getDefault().getLanguage().equals("zh")){
            APPLICATION_NAME="MCLX";

            BUTTON_START_NAME="\u5f00\u59cb\u6e38\u620f";
            MENU_SETTINGS_NAME="\u8bbe\u7f6e";
            MENU_ABOUT_NAME="\u5173\u4e8e";
            MENU_NAME="\u83dc\u5355";
            MENU_KILL_MINECRAFT="\u7ec8\u6b62 Minecraft \u8fdb\u7a0b";

            SETTINGS_TEXTFIELD_PLAYERNAME_TIP_TEXT="\u73a9\u5bb6\u540d\u79f0";
            SETTINGS_TEXTFIELD_MAX_MEMORY_TIP_TEXT="\u6700\u5927\u5185\u5b58";
            SETTINGS_TEXTFIELD_JAVA_PATH_TIP_TEXT="Java \u8def\u5f84";
            SETTINGS_TEXTFIELD_GAME_WINDOW_SIZE_TIP_TEXT="\u6e38\u620f\u7a97\u53e3\u5927\u5c0f";
            SETTINGS_CHECKBOX_FULLSCREEN_TEXT="\u5168\u5c4f";
            SETTINGS_CHECKBOX_LOAD_SM_TEXT="\u52a0\u8f7dSM";
            SETTINGS_BUTTON_WHAT_TEXT="SM\u662f\u4ec0\u4e48\uff1f";
            SETTINGS_CHECKBOX_CUSTOM_WORK_PATHS_TEXT="\u81ea\u5b9a\u4e49\u5de5\u4f5c\u76ee\u5f55";
            SETTINGS_TEXTFIELD_CUSTOM_GAME_DIR_TIP_TEXT="\u6e38\u620f\u76ee\u5f55";
            SETTINGS_TEXTFIELD_CUSTOM_ASSETS_DIR_TIP_TEXT="Assets \u76ee\u5f55";
            SETTINGS_TEXTFIELD_CUSTOM_RESOURCE_PACK_DIR_TIP_TEXT="\u8d44\u6e90\u5305\u76ee\u5f55";
            SETTINGS_TEXTFIELD_CUSTOM_DATA_PACK_DIR_TIP_TEXT="\u6570\u636e\u5305\u76ee\u5f55";
            SETTINGS_TEXTFIELD_CUSTOM_SM_DIR_TIP_TEXT="SM\u76ee\u5f55";
            SETTINGS_BUTTON_BROWSE_TEXT="\u6d4f\u89c8";
            SETTINGS_BUTTON_SAVE_TEXT="\u4fdd\u5b58";
            SETTINGS_BROSE_FILE_TYPE_TEXT="%s \u6587\u4ef6";
            SETTINGS_BROSE_DIR_TYPE_TEXT="%s \u76ee\u5f55";
            SETTINGS_TEXTFIELD_OS_MAX_MEMORY_TIP_TEXT="\u6700\u5927\uff1a%sMB";

            DIALOG_WHAT_IS_SM_TEXT="SM\u662f\u4ec0\u4e48\uff1f\nSM\u5168\u79f0 Simple Mod\uff08\u7b80\u6613\u7684\u6a21\u7ec4\uff09\uff0c\n\u5b83\u662f\u4e00\u79cd\u5168\u65b0\u7684 Minecraft \u6a21\u7ec4\u5f00\u53d1\u65b9\u5f0f\uff0c\n\u4f7f\u7528\u4e86\u5168\u65b0\u7684\u5f00\u53d1\u65b9\u5f0f\uff0c\u5b58\u50a8\u5728JSON\u6587\u4ef6\u4e2d\uff0c\u514d\u7f16\u8bd1\u3002\n\u5b83\u80fd\u4f7f\u60a8\u5f00\u53d1 Minecraft \u7684\u6a21\u7ec4\u66f4\u52a0\u65b9\u4fbf\u3001\u5feb\u6377\uff0c\n\u4f46\u662f\u4e0d\u80fd\u5f00\u53d1\u8fc7\u4e8e\u9ad8\u7ea7\u7684 Minecraft \u6a21\u7ec4\u3002\n\u9ed8\u8ba4\u60c5\u51b5\u4e0b\uff0cSM\u76ee\u5f55\u4f4d\u4e8e.minecraft\u7684simplemods\u4e2d\u3002\nGithub\uff1ahttps://www.github.com/MrShieh-X/simple-mod-loader\n\u6b64\u529f\u80fd\u5c1a\u672a\u5b8c\u6210\u5f00\u53d1\u3002";
            DIALOG_NO_MINECRAFT_DIR_TEXT=".minecraft \u76ee\u5f55\u4e0d\u5b58\u5728\u3002\n\u8bf7\u4f7f\u7528\u5176\u4ed6\u7684 Minecraft \u542f\u52a8\u5668\u751f\u6210 .minecraft \u76ee\u5f55\u540e\uff0c\n\u590d\u5236\u5230\u672c\u7a0b\u5e8f\u6240\u5728\u7684\u76ee\u5f55\u4e2d\u6216\u81ea\u5b9a\u4e49\u7684\u6e38\u620f\u76ee\u5f55\u4e2d\u3002\n\u672c\u5e94\u7528\u7a0b\u5e8f\u6682\u4e0d\u652f\u6301\u751f\u6210 .minecraft \u76ee\u5f55\uff0c\u975e\u5e38\u62b1\u6b49\u3002";
            DIALOG_TARGET_FILE_NOT_EXISTS_TEXT="%s\uff1a\u76ee\u6807\u6587\u4ef6\u6216\u76ee\u5f55\u4e0d\u5b58\u5728";
            DIALOG_BUTTON_CANCEL_TEXT="\u53d6\u6d88";
            DIALOG_BUTTON_OK_TEXT="\u597d\u7684";
            DIALOG_BUTTON_YES_TEXT="\u786e\u5b9a";
            DIALOG_BUTTON_EXIT_TEXT="\u9000\u51fa";
            DIALOG_ABOUT_DESCRIPTION="Minecraft Launcher X 1.0\n\u4e00\u4e2a Minecraft Java \u7248\u7684\u542f\u52a8\u5668\n\u6e90\u4ee3\u7801\u4ed3\u5e93\uff1ahttps://www.github.com/MrShieh-X/minecraft-launcher-x\nCopyright \u00a9 2021 MrShiehX";
            DIALOG_CHOOSE_JAVA_FILE_TITLE="\u8bf7\u9009\u62e9 java \u6587\u4ef6";
            DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE="\u8bf7\u9009\u62e9 java.exe \u6587\u4ef6";

            DIALOG_CHOOSE_GAME_DIR_TITLE="\u8bf7\u9009\u62e9\u81ea\u5b9a\u4e49\u7684\u6e38\u620f\u76ee\u5f55";
            DIALOG_CHOOSE_ASSETS_DIR_TITLE="\u8bf7\u9009\u62e9\u81ea\u5b9a\u4e49\u7684 Assets \u76ee\u5f55";
            DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE="\u8bf7\u9009\u62e9\u81ea\u5b9a\u4e49\u7684\u8d44\u6e90\u5305\u76ee\u5f55";
            DIALOG_CHOOSE_DATA_PACK_DIR_TITLE="\u8bf7\u9009\u62e9\u81ea\u5b9a\u4e49\u7684\u6570\u636e\u5305\u76ee\u5f55";
            DIALOG_CHOOSE_SM_DIR_TITLE="\u8bf7\u9009\u62e9\u81ea\u5b9a\u4e49\u7684SM\u76ee\u5f55";

            DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT="\u6e38\u620f\u76ee\u5f55 (.minecraft)";
            DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT="Assets \u76ee\u5f55 (assets)";
            DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT="\u8d44\u6e90\u5305\u76ee\u5f55 (resourcepacks)";
            DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT="\u6570\u636e\u5305\u76ee\u5f55 (datapacks)";
            DIALOG_CHOOSE_TYPE_SM_DIR_TEXT="SM\u76ee\u5f55 (simplemods)";
            DIALOG_TITLE_NOTICE="\u63d0\u793a";

            MESSAGE_NOT_FOUND_TARGET_VERSION="\u627e\u4e0d\u5230\u76ee\u6807\u7248\u672c";
            MESSAGE_NOT_FOUND_GAME_DIR="\u627e\u4e0d\u5230\u6e38\u620f\u76ee\u5f55";
            MESSAGE_NOT_FOUND_ASSETS_DIR="\u627e\u4e0d\u5230 Assets \u76ee\u5f55";
            MESSAGE_NOT_FOUND_RESOURCE_PACKS_DIR="\u627e\u4e0d\u5230\u8d44\u6e90\u5305\u76ee\u5f55";
            MESSAGE_NOT_FOUND_DATA_PACKS_DIR="\u627e\u4e0d\u5230\u6570\u636e\u5305\u76ee\u5f55";
            MESSAGE_NOT_FOUND_SM_DIR="\u627e\u4e0d\u5230 SM \u76ee\u5f55";
            MESSAGE_NOT_FOUND_JAVA="\u8bf7\u5728\u8bbe\u7f6e\u91cc\u8f93\u5165\u6b63\u786e\u7684 Java \u8def\u5f84";
            MESSAGE_STARTING_GAME="\u542f\u52a8\u6e38\u620f\u4e2d...";
            MESSAGE_FINISHED_GAME="\u6e38\u620f\u7ed3\u675f";

            EXCEPTION_VERSION_JSON_NOT_FOUND="\u76ee\u6807\u542f\u52a8\u7248\u672c\u7684JSON\u6587\u4ef6\u6216JAR\u6587\u4ef6\u4e0d\u5b58\u5728";
            EXCEPTION_VERSION_NOT_FOUND="\u76ee\u6807\u7248\u672c\u4e0d\u5b58\u5728";
            EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND="\u627e\u4e0d\u5230 natives \u76ee\u5f55";
            EXCEPTION_JAVA_NOT_FOUND="\u627e\u4e0d\u5230 Java";
            EXCEPTION_MAX_MEMORY_TOO_BIG="\u6700\u5927\u5185\u5b58\u5927\u4e8e\u7269\u7406\u5185\u5b58\u603b\u5927\u5c0f";
            EXCEPTION_MAX_MEMORY_IS_ZERO="\u6700\u5927\u5185\u5b58\u4e3a0";
        }else{
            APPLICATION_NAME="MCLX";

            BUTTON_START_NAME="Start Game";

            MENU_SETTINGS_NAME="Settings";
            MENU_ABOUT_NAME="About";
            MENU_NAME="Menu";
            MENU_KILL_MINECRAFT="Terminate Minecraft Process";

            SETTINGS_TEXTFIELD_PLAYERNAME_TIP_TEXT="Player Name";
            SETTINGS_TEXTFIELD_MAX_MEMORY_TIP_TEXT="Maximum Memory";
            SETTINGS_TEXTFIELD_JAVA_PATH_TIP_TEXT="Java Path";
            SETTINGS_TEXTFIELD_GAME_WINDOW_SIZE_TIP_TEXT="Game Window Size";
            SETTINGS_TEXTFIELD_CUSTOM_GAME_DIR_TIP_TEXT="Game Directory";
            SETTINGS_TEXTFIELD_CUSTOM_ASSETS_DIR_TIP_TEXT="Assets Directory";
            SETTINGS_TEXTFIELD_CUSTOM_RESOURCE_PACK_DIR_TIP_TEXT="Resource Pack Directory";
            SETTINGS_TEXTFIELD_CUSTOM_DATA_PACK_DIR_TIP_TEXT="Data Pack Directory";
            SETTINGS_TEXTFIELD_CUSTOM_SM_DIR_TIP_TEXT="SM Directory";
            SETTINGS_CHECKBOX_FULLSCREEN_TEXT="Fullscreen";
            SETTINGS_CHECKBOX_LOAD_SM_TEXT="Load SM";
            SETTINGS_BUTTON_WHAT_TEXT="What is SM?";
            SETTINGS_CHECKBOX_CUSTOM_WORK_PATHS_TEXT="Custom Working Directories";
            SETTINGS_BUTTON_BROWSE_TEXT="Browse";
            SETTINGS_BUTTON_SAVE_TEXT="Save";
            SETTINGS_BROSE_FILE_TYPE_TEXT="%s File";
            SETTINGS_BROSE_DIR_TYPE_TEXT="%s Directory";
            SETTINGS_TEXTFIELD_OS_MAX_MEMORY_TIP_TEXT="Max: %sMB";

            DIALOG_WHAT_IS_SM_TEXT="What is SM?\nThe full name of SM is Simple Mod, which is a brand-new Minecraft mod development method,\nwhich uses a brand-new development method and is stored in the JSON file without compilation.\nIt can make you develop Minecraft mods more convenient and faster,\nbut you can't develop too advanced Minecraft mods.\nThe SM directory is in simplemods in .minecraft by default.\nGithub: https://www.github.com/MrShieh-X/simple-mod-loader\nThis feature has not yet been developed.";
            DIALOG_NO_MINECRAFT_DIR_TEXT="The .minecraft directory does not exist.\nPlease use other Minecraft launcher to generate the .minecraft directory and copy it to the directory of this program or the customized game directory.\nThis application does not support the generation of .minecraft directory, sorry.";
            DIALOG_TARGET_FILE_NOT_EXISTS_TEXT="%s: The target file or directory does not exist";
            DIALOG_BUTTON_CANCEL_TEXT="Cancel";
            DIALOG_BUTTON_OK_TEXT="OK";
            DIALOG_BUTTON_YES_TEXT="Yes";
            DIALOG_BUTTON_EXIT_TEXT="Exit";
            DIALOG_ABOUT_DESCRIPTION="Minecraft Launcher X 1.0\nA Launcher of Minecraft Java Edition\nSource code repository: https://www.github.com/MrShieh-X/minecraft-launcher-x\nCopyright \u00a9 2021 MrShiehX";
            DIALOG_CHOOSE_JAVA_FILE_TITLE="Please select the java file";
            DIALOG_CHOOSE_JAVA_EXE_FILE_TITLE="Please select the java.exe file";
            DIALOG_CHOOSE_GAME_DIR_TITLE="Please select a custom game directory";
            DIALOG_CHOOSE_ASSETS_DIR_TITLE="Please select a custom assets directory";
            DIALOG_CHOOSE_RESOURCE_PACK_DIR_TITLE="Please select a custom resource pack directory";
            DIALOG_CHOOSE_DATA_PACK_DIR_TITLE="Please select a custom data pack directory";
            DIALOG_CHOOSE_SM_DIR_TITLE="Please select a custom SM directory";

            DIALOG_CHOOSE_TYPE_GAME_DIR_TEXT="Game Directory (.minecraft)";
            DIALOG_CHOOSE_TYPE_ASSETS_DIR_TEXT="Assets Directory (assets)";
            DIALOG_CHOOSE_TYPE_RESOURCE_PACK_DIR_TEXT="Resource Packs Directory (resourcepacks)";
            DIALOG_CHOOSE_TYPE_DATA_PACK_DIR_TEXT="Data Packs Directory (datapacks)";
            DIALOG_CHOOSE_TYPE_SM_DIR_TEXT="SM Directory (simplemods)";
            DIALOG_TITLE_NOTICE="Notice";

            MESSAGE_NOT_FOUND_TARGET_VERSION="Target version not found";
            MESSAGE_NOT_FOUND_GAME_DIR="Game directory not found";
            MESSAGE_NOT_FOUND_ASSETS_DIR="Assets directory not found";
            MESSAGE_NOT_FOUND_RESOURCE_PACKS_DIR="Resource packs directory not found";
            MESSAGE_NOT_FOUND_DATA_PACKS_DIR="Data packs directory not found";
            MESSAGE_NOT_FOUND_SM_DIR="SM directory not found";
            MESSAGE_NOT_FOUND_JAVA="Please enter the correct Java path in the settings";
            MESSAGE_STARTING_GAME="Starting game...";
            MESSAGE_FINISHED_GAME="Game finished";

            EXCEPTION_VERSION_JSON_NOT_FOUND="The JSON file of the target startup version does not exist";
            EXCEPTION_VERSION_NOT_FOUND="The target startup version does not exist";
            EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND="Cannot find the natives directory";
            EXCEPTION_JAVA_NOT_FOUND="Java not found";
            EXCEPTION_MAX_MEMORY_TOO_BIG="The maximum memory is larger than the total physical memory size";
            EXCEPTION_MAX_MEMORY_IS_ZERO="The maximum memory is 0";
        }
    }
}
