package qnx;

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
    public static Image iconOpenFile = null;
    
    static ImageData newImgData(ImageData imd) {
        return ImageData.internal_new(imd.width, 
                imd.height, imd.depth, imd.palette, 
                imd.scanlinePad, 
                imd.data, imd.maskPad, 
                imd.maskData, 
                imd.alphaData, imd.
                alpha, imd.transparentPixel, 
                imd.type, imd.x, imd.y, imd.disposalMethod, imd.delayTime);
    }
   static void setAllFields(ImageData imd, ImageData imd1) {

       imd.width = imd1.width;
       imd.height = imd1.height;
       imd.depth = imd1.depth;
       imd.scanlinePad = imd1.scanlinePad;
       imd.bytesPerLine = imd1.bytesPerLine;
       imd.data = imd1.data;
       imd.palette = imd1.palette;
       imd.transparentPixel = imd1.transparentPixel;
       imd.maskData = imd1.maskData;
       imd.maskPad = imd1.maskPad;
       imd.alphaData = imd1.alphaData;
       imd.alpha = imd1.alpha;
       imd.type = imd1.type;
       imd.x = imd1.x;
       imd.y = imd1.y;
       imd.disposalMethod = imd1.disposalMethod;
       imd.delayTime = imd1.delayTime;
        }
    static void loadResources(AppContext ctx) {
      //  InputStream is = ctx.getClass().getClassLoader().getResourceAsStream("search_64.png");
      //  if (is == null) {
        if (iconSearch == null ){
            iconSearch = new Image(ctx.getDisplay(), "resources/search_64.png");
           iconOpenDevice =new Image(ctx.getDisplay(), "resources/connectDevice.ico");
            
            ImageData [] imd = new ImageLoader().load("resources/connectDevice.ico");
         //   ImageData timd = new ImageData(imd[2].width, imd[2].height, imd[2].depth, imd[2].palette);
            
            
           // setAllFields(timd, imd[2]);
            //Color bk= iconOpenDevice.getBackground();
            iconOpenDevice = new Image(ctx.getDisplay(), newImgData(imd[5]));
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
            iconOpenFile = new Image(ctx.getDisplay(), "resources/folder_64.png");
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
