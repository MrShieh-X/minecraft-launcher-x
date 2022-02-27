package com.mrshiehx.mclx.modules.version;

import com.mrshiehx.mclx.MinecraftLauncherX;
import com.mrshiehx.mclx.modules.MinecraftLauncher;
import com.mrshiehx.mclx.utils.IOUtils;
import com.mrshiehx.mclx.utils.Utils;
import org.json.JSONObject;

import javax.swing.*;

import java.io.File;

import static com.mrshiehx.mclx.MinecraftLauncherX.gameDir;
import static com.mrshiehx.mclx.MinecraftLauncherX.getString;

public class VersionsManager extends JDialog {
    public static void showDialog(JFrame frame, File versionsDir) {
        showDialog(frame, versionsDir,false);
    }
    public static void showDialog(JFrame frame, File versionsDir, boolean var) {
        String[] fs = Utils.listVersions(versionsDir);
        if (fs.length > 0) {
            String version = choose(frame, versionsDir);
            if (!Utils.isEmpty(version)) {
                VersionOperation versionOperation = chooseOperation(frame, versionsDir, version);
                if (versionOperation != null) {
                    versionOperation.operate();
                }
                String[] fs2 = Utils.listVersions(versionsDir);
                if (fs2.length > 0&&!var) showDialog(frame, versionsDir, false);
                MinecraftLauncherX.updateVersions(null);
            }
        }else{
            JOptionPane.showMessageDialog(frame,getString("DIALOG_NO_VERSIONS_MESSAGE"),getString("DIALOG_TITLE_NOTICE"),JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static String choose(JFrame frame, File versionsDir){
        Object[] selectionValues = Utils.listVersions(versionsDir);

        Object inputContent = JOptionPane.showInputDialog(
                frame,
                getString("MESSAGE_CHOOSE_A_VERSION"),
                getString("MENU_MANAGE"),
                JOptionPane.PLAIN_MESSAGE,
                null,
                selectionValues,
                selectionValues[0]
        );
        return (inputContent)!=null?inputContent.toString():null;
    }


    private static VersionOperation chooseOperation(JFrame frame, File versionsDir,String version){
        VersionOperation[] selectionValues = new VersionOperation[]{
                new VersionOperation() {
                    @Override
                    public String toString() {
                        return getString("MANAGE_VERSION_OPERATION_DELETE");
                    }

                    @Override
                    public boolean operate() {
                        File file=new File(versionsDir,version);
                        Utils.deleteDirectory(file);
                        return !file.exists();
                    }
                },
                new VersionOperation() {
                    @Override
                    public String toString() {
                        return getString("MANAGE_VERSION_OPERATION_RENAME");
                    }

                    @Override
                    public boolean operate() {
                        String name=MinecraftLauncherX.showInputNameDialog(frame,version,getString("MESSAGE_MANAGE_INPUT_NAME"));
                        File file=new File(versionsDir,version);
                        File newFile=new File(versionsDir,name);
                        File file2=new File(newFile,version+".jar");
                        File file3=new File(newFile,version+".json");
                        file.renameTo(newFile);
                        file2.renameTo(new File(newFile,name+".jar"));
                        file3.renameTo(new File(newFile,name+".json"));
                        return true;
                    }
                },
                new VersionOperation() {
                    @Override
                    public String toString() {
                        return getString("MANAGE_VERSION_REDOWNLOAD_NATIVES");
                    }

                    @Override
                    public boolean operate() {
                        try {
                            File versionDir = new File(versionsDir, version);
                            File json = new File(versionDir, version + ".json");
                            JSONObject jsonObject = new JSONObject(Utils.readFileContent(json));
                            NativesReDownloader.reDownload(frame, versionDir, jsonObject.optJSONArray("libraries"));
                            return true;
                        }catch (Exception e){
                            e.printStackTrace();
                            Utils.exceptionDialog(frame,e);
                        }
                        return false;
                    }
                },
        };

        Object inputContent = JOptionPane.showInputDialog(
                frame,
                getString("MESSAGE_CHOOSE_A_OPERATION"),
                getString("MENU_MANAGE"),
                JOptionPane.PLAIN_MESSAGE,
                null,
                selectionValues,
                selectionValues[0]
        );
        if(inputContent instanceof VersionOperation)return (VersionOperation)inputContent;
        return null;
    }


    private abstract static class VersionOperation{
        public abstract String toString();
        public abstract boolean operate();
    }

}