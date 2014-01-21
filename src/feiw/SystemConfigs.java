package feiw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import feiw.LogSource.LogFilter;


public final class SystemConfigs {

    public static final int MIN_NOTIFY_COUNT = 100;
    
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
    private static ArrayList<LogUrl> mRecentUrls = new ArrayList<LogUrl>(10);
    
    private  static Color [] mForeColors;
    private  static Color [] mBackColors;
    private  static Color mSearchBackColor;
    
    static void load(Display disp) {
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
        addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_LEVEL, LogFilter.OP_LESSTHEN, Integer.valueOf(6)));
        addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_LEVEL, LogFilter.OP_LESSTHEN, Integer.valueOf(5)));
        addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_LEVEL, LogFilter.OP_LESSTHEN, Integer.valueOf(4)));
    }

    public static Color getSearchMarkerBackground() {
        return mSearchBackColor;
    }
    public static Color getLogForeground(int level) {
        return mForeColors[level];
    }
    
    public static Color getLogBackground(int level) {
        if (level < 5)
            return mBackColors[level];
        else 
            return null;
    }
    
    static ArrayList <LogFilter> mRecentFilters = new ArrayList <LogFilter>(10);
    
    public static void addRecentFilter(LogFilter f) {
        mRecentFilters.add(0, f);
    }
    public static LogFilter getRecentFilter(int i) {
        if (i < mRecentFilters.size()) {
            return mRecentFilters.get(i);
        }
        return null;
    }
    
    static  ArrayList <String> mRecentFiles = new ArrayList <String>(10);
    
    static public void addRecentFile(String f) {
        mRecentFiles.add(0, f);
    }
    static public String getRecentFile(int i) {
        if (i < mRecentFiles.size()) {
            return mRecentFiles.get(i);
        }
        return null;
    }
    
    static public void addRecentUrl(LogUrl u) {
        mRecentUrls.add(0, u);
    }

    static public LogUrl getRecentUrl(int i) {
        if (i < mRecentUrls.size()) {
            return mRecentUrls.get(i);
        }
        return null;
    }
    
    static String mAdbPath = null;
    static public String getAdbPath() {
        return mAdbPath; ///Developer/SDKs/android-sdk/platform-tools/adb";
    }
    static public void setAdbPath(String p) {
        mAdbPath = p;
    }
    
    void save() {
        /*
        String home =  System.getProperty("user.home");
        File dir = new File(home);
        if (dir.exists()) {
            try {
                FileOutputStream fo = new FileOutputStream(home + "/.superlog");
                OutputStreamWriter wr = new OutputStreamWriter(fo);
                
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        */
    }
}
