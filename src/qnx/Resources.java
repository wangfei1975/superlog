package qnx;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;

public final class Resources {
    public static Image iconSearch = null;
    
    
    static void loadResources(AppContext ctx) {
        InputStream is = ctx.getClass().getClassLoader().getResourceAsStream("search.png");
        if (is == null) {
            iconSearch = new Image(ctx.getMainFrame().getDisplay(), "resources/search.png");
        } else {
            iconSearch = new Image(ctx.getMainFrame().getDisplay(), is);
        }
    }

}
