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
    public static Image iconConnected24 = null;
    public static Image iconConnected16 = null;
    public static Image iconDisconnected32 = null;
    public static Image iconDisconnected16 = null;
    
    static void loadResources(AppContext ctx) {
      //  InputStream is = ctx.getClass().getClassLoader().getResourceAsStream("search_64.png");
      //  if (is == null) {
        if (iconSearch == null ){
            Display dis = ctx.getDisplay();
            iconSearch = new Image(dis, "resources/search_32.png");
            iconOpenDevice =new Image(dis, "resources/connectDevice_32.png");
            iconOpenDevice16 =new Image(dis, "resources/connectDevice_16.png");
            iconNext = new Image (dis, "resources/down_32.png");
            iconPrev = new Image (dis, "resources/up_32.png");
            iconGo = new Image (dis, "resources/go_32.png");
            iconStop = new Image (dis, "resources/pause_32.png");
            
            iconFilter = new Image (dis, "resources/filter_32.png");
            iconFilter16 = new Image (dis, "resources/filter_16.png");
            
            iconConnected24 = new Image (dis, "resources/connected_24.png");
            iconConnected16 = new Image (dis, "resources/connected_16.png");
            iconDisconnected32 = new Image (dis, "resources/disconnected_32.png");
            iconDisconnected16 = new Image (dis, "resources/disconnected_16.png");
            
            
            
//            ImageData [] imd = new ImageLoader().load("resources/connectDevice.ico");
         //   ImageData timd = new ImageData(imd[2].width, imd[2].height, imd[2].depth, imd[2].palette);
            
            
           // setAllFields(timd, imd[2]);
            //Color bk= iconOpenDevice.getBackground();
  //          iconOpenDevice = new Image(dis, imd[0]);
         //   iconOpenDevice.setBackground(bk);
         //   iconOpenDevice.setBackground(timd.data[timd.transparentPixel])
            
            
            /*
            for (ImageData im :imd) {
                if (im.width == 32) {
                    iconOpenDevice = new Image(Display.getCurrent(), im);
                   // iconOpenDevice.setBackground(new Color(dis, 255,255,255));
                    break;
                }
            }
            */
            iconOpenFile = new Image(dis, "resources/folder_32.png");
        }
        //} else {
         //   iconSearch = new Image(dis, is);
       // }
    }

    static void freeResources(AppContext ctx) {
        if (iconSearch != null) {
            iconSearch.dispose();
        }
    }
}
