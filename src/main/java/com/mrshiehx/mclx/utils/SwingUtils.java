package com.mrshiehx.mclx.utils;

import java.awt.*;

public class SwingUtils {
    public static Point getCenterLocation(int width, int height){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        return new Point(screenWidth - width / 2, screenHeight - height / 2);
    }
}
