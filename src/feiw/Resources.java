package feiw;

import java.io.InputStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

public final class Resources {
    public static Image iconSearch = null;
    public static Image iconOpenDevice = null;
    public static Image iconOpenDevice16 = null;
    public static Image iconOpenFile = null;
    public static Image iconNext = null;
    public static Image iconPrev = null;
    public static Image iconGo = null;
    public static Image iconStop = null;
    public static Image iconFilter = null;
    public static Image iconFilter16 = null;

    static void loadResources(AppContext ctx) {
      //  InputStream is = ctx.getClass().getClassLoader().getResourceAsStream("search_64.png");
      //  if (is == null) {
        if (iconSearch == null ){
            iconSearch = new Image(ctx.getDisplay(), "resources/search_32.png");
            iconOpenDevice =new Image(ctx.getDisplay(), "resources/connectDevice_32.png");
            iconOpenDevice16 =new Image(ctx.getDisplay(), "resources/connectDevice_16.png");
            iconNext = new Image (ctx.getDisplay(), "resources/down_32.png");
            iconPrev = new Image (ctx.getDisplay(), "resources/up_32.png");
            iconGo = new Image (ctx.getDisplay(), "resources/go_32.png");
            iconStop = new Image (ctx.getDisplay(), "resources/pause_32.png");
            
            iconFilter = new Image (ctx.getDisplay(), "resources/filter_32.png");
            iconFilter16 = new Image (ctx.getDisplay(), "resources/filter_16.png");
            
//            ImageData [] imd = new ImageLoader().load("resources/connectDevice.ico");
         //   ImageData timd = new ImageData(imd[2].width, imd[2].height, imd[2].depth, imd[2].palette);
            
            
           // setAllFields(timd, imd[2]);
            //Color bk= iconOpenDevice.getBackground();
  //          iconOpenDevice = new Image(ctx.getDisplay(), imd[0]);
         //   iconOpenDevice.setBackground(bk);
         //   iconOpenDevice.setBackground(timd.data[timd.transparentPixel])
            
            
            /*
            for (ImageData im :imd) {
                if (im.width == 32) {
                    iconOpenDevice = new Image(Display.getCurrent(), im);
                   // iconOpenDevice.setBackground(new Color(ctx.getDisplay(), 255,255,255));
                    break;
                }
            }
            */
            iconOpenFile = new Image(ctx.getDisplay(), "resources/folder_32.png");
        }
        //} else {
         //   iconSearch = new Image(ctx.getDisplay(), is);
       // }
    }

    static void freeResources(AppContext ctx) {
        if (iconSearch != null) {
            iconSearch.dispose();
        }
    }
}
