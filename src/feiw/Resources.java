package feiw;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

public final class Resources {
    public static Image openfile_32 = null;
    public static Image search_32 = null;
    public static Image connectDevice_32 = null;
    public static Image connectDevice_16 = null;
    public static Image down_32 = null;
    public static Image up_32 = null;
    public static Image go_32 = null;
    public static Image pause_32 = null;
    public static Image filter_32 = null;
    public static Image filter_16 = null;
    public static Image connected_16 = null;
    public static Image disconnected_32 = null;
    public static Image disconnected_16 = null;
    public static Image file_16 = null;
    public static Image clear_32 = null;
    public static Image trash_32 = null;
    public static Image copy_32 = null;
    public static Image copyall_32 = null;
    public static Image save_32 = null;
    
    public static ArrayList <Image> mIcons = new ArrayList<Image> (30);
 
    static Image loadIcon(AppContext ctx, String name) {
        Image icon = null;
        InputStream is = ctx.getClass().getClassLoader().getResourceAsStream(name + ".png");
        if (is == null) {
            icon = new Image(ctx.getDisplay(), "resources/" + name + ".png");
        } else {
            icon = new Image(ctx.getDisplay(), is);
        }
        mIcons.add(icon);
        return icon;
    }
    
    static void loadResources(AppContext ctx) {
        openfile_32 = loadIcon(ctx, "openfile_32");
        search_32 = loadIcon(ctx, "search_32");
        connectDevice_32 = loadIcon(ctx, "connectDevice_32");
        connectDevice_16 = loadIcon(ctx, "connectDevice_16");
        down_32 = loadIcon(ctx, "down_32");
        up_32 = loadIcon(ctx, "up_32");
        go_32 = loadIcon(ctx, "go_32");
        pause_32 = loadIcon(ctx, "pause_32");
        filter_32 = loadIcon(ctx, "filter_32");
        filter_16 = loadIcon(ctx, "filter_16");
        connected_16 = loadIcon(ctx, "connected_16");
        disconnected_32 = loadIcon(ctx, "disconnected_32");
        disconnected_16 = loadIcon(ctx, "disconnected_16");
        file_16 = loadIcon(ctx, "file_16");
        clear_32 = loadIcon(ctx, "clear_32");
        trash_32 = loadIcon(ctx, "trash_32");
        copy_32 = loadIcon(ctx, "copy_32");
        copyall_32 = loadIcon(ctx, "copyall_32");
        save_32 = loadIcon(ctx, "save_32");
    }

    static void freeResources(AppContext ctx) {
        for (Image im : mIcons) {
            im.dispose();
        }
    }
}
