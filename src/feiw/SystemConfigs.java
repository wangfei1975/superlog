package feiw;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

 

public final class SystemConfigs {

    public static final int MIN_NOTIFY_COUNT = 300;
    
    public static final class LogUrl {
        public String scheme;
        public String url;
        public int    port;
        public LogUrl(String s, String u, int p) {
            scheme = s;
            url = u;
            port = p;
        }
        
        public String toString() {
            return scheme + "://" + url + ":" + port;
        }
    }
    private ArrayList<LogUrl> mRecentUrls = new ArrayList<LogUrl>(10);
    
    private Color [] mForeColors;
    private Color [] mBackColors;
    private Color mSearchBackColor;
    
    public SystemConfigs(Display disp) {
        mForeColors = new Color[] {disp.getSystemColor(SWT.COLOR_BLACK),
                disp.getSystemColor(SWT.COLOR_DARK_RED),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_BLUE),
                disp.getSystemColor(SWT.COLOR_DARK_BLUE),
                disp.getSystemColor(SWT.COLOR_DARK_GREEN),
                disp.getSystemColor(SWT.COLOR_BLACK),
                disp.getSystemColor(SWT.COLOR_DARK_GRAY)
                }; 
        mBackColors = new Color[] {disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_RED),
                disp.getSystemColor(SWT.COLOR_YELLOW),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE)
                }; 
        mSearchBackColor = disp.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        
        mRecentUrls.add(new LogUrl("qconn", "10.222.98.205", 8000));
        mRecentUrls.add(new LogUrl("qconn", "10.222.109.58", 8000));
        mRecentUrls.add(new LogUrl("file", "Users/feiwang/qnx/abcd.log", 0));
        
    }

    public LogUrl getLastLogUrl() {
        return mRecentUrls.get(0);
    }
    public Color getSearchMarkerBackground() {
        return mSearchBackColor;
    }
    public Color getLogForeground(int level) {
        return mForeColors[level];
    }
    
    public Color getLogBackground(int level) {
        if (level < 5)
            return mBackColors[level];
        else 
            return null;
    }
}
