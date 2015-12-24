package com.hawx.uestclibui;

/**
 * Created by Administrator on 2015/11/28.
 */
public class MenuItem {
    public MenuItem(String text, boolean isSelected, int icon, int iconSelected) {
        this.text = text;
        this.isSelected = isSelected;
        this.icon = icon;
        this.iconSelected = iconSelected;
    }

    boolean isSelected;
    String text;
    int icon;
    int iconSelected;
}
