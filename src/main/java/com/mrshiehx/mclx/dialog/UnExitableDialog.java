package com.mrshiehx.mclx.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class UnExitableDialog extends JDialog {
    private boolean exitable;

    public UnExitableDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public void setExitable(boolean exitable) {
        this.exitable = exitable;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (exitable)
                this.setVisible(false);
        } else {
            super.processWindowEvent(e);
        }
    }
}
