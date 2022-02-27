package com.mrshiehx.mclx.utils;

import com.mrshiehx.mclx.dialog.UnExitableDialog;

import javax.swing.*;
import java.awt.*;

import static com.mrshiehx.mclx.MinecraftLauncherX.getString;

public class DownloadDialog {
    public static JProgressBar createProgressBar(){
        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(10, 10, 464, 20);

        progressBar.setOrientation(JProgressBar.HORIZONTAL);
        //progressBar.setMinimum(0);

        //progressBar.setMaximum(30);
        //progressBar.setValue(29);
        progressBar.setStringPainted(true);
        //progressBar.addChangeListener(this);
        //progressBar.setPreferredSize(new Dimension(300,20));
        progressBar.setBorderPainted(true);
        return progressBar;
    }
    public static JTextArea createTextArea(){
        JTextArea textArea = new JTextArea();
        textArea.setBounds(10, 40, 464, 230);
        textArea.setEditable(false);
        textArea.setFont(new Font(null, Font.BOLD, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    public static UnExitableDialog createDownloadDialog(Frame owner, JProgressBar progressBar, JTextArea textArea) {
        return createDownloadDialog(owner, progressBar, textArea, getString("MENU_INSTALL_NEW_VERSION"));
    }
    public static UnExitableDialog createDownloadDialog(Frame owner, JProgressBar progressBar, JTextArea textArea, String title) {
        UnExitableDialog dialog = new UnExitableDialog(owner, title, true);
        dialog.setSize(250*2, 160*2);
        dialog.setResizable(false);
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(owner);

        //progressBar.setBackground(Color.pink);



        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setBounds(10, 40, textArea.getWidth(), textArea.getHeight());
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        dialog.add(progressBar);
        dialog.add(jsp);

        return dialog;
    }
}
