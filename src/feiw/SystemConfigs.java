package feiw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import feiw.LogSource.LogFilter;

public final class SystemConfigs {

    public static final int RECENTLIST_SIZE = 6;

    private static String CFG_FNAME = null;
    private static SystemConfigs mCfgs = null;
    static {
        if (SWT.getPlatform().contains("win")) {
            String path = System.getenv("APPDATA");
            if (!path.endsWith("\\")) {
                path += "\\";
            }
            File dir = new File(path);
            if (dir.exists() && dir.canWrite()) {
                CFG_FNAME = path + "superlog.cfg";
            }
        } else {
            String path = System.getProperty("user.home");
            if (!path.endsWith("/")) {
                path += "/";
            }
            File dir = new File(path);
            if (dir.exists() && dir.canWrite()) {
                CFG_FNAME = path + ".superlog";
            }
        }
        System.out.println("config file:" + CFG_FNAME);
        mCfgs = load();
    }

    public static SystemConfigs instance() {
        // mCfgs.toJson();
        return mCfgs;
    }

    public final long LIVE_LOG_NOTIFYTIME = 300;
    public final String copyright = "wangfei1975@gmail.com";

    public static final class LogUrl {
        public String scheme;
        public String url;
        public int port;

        public LogUrl(String s, String u, int p) {
            scheme = s;
            url = u;
            port = p;
        }

        @Override
        public String toString() {
            return scheme + "://" + url + ":" + port;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof LogUrl) {
                return ((LogUrl) o).url.equals(url) && (((LogUrl) o).port == port);
            }
            return false;
        }
    }

    private ArrayList<LogUrl> mRecentUrls = new ArrayList<LogUrl>(10);

    private class Colors {
        private Color[] mForeColors;
        private Color[] mBackColors;
        private Color mSearchBackColor;

        public Colors() {
            Display disp = Display.getCurrent();
            mForeColors = new Color[] { disp.getSystemColor(SWT.COLOR_BLACK),
                    disp.getSystemColor(SWT.COLOR_DARK_RED), disp.getSystemColor(SWT.COLOR_WHITE),
                    disp.getSystemColor(SWT.COLOR_BLUE), disp.getSystemColor(SWT.COLOR_DARK_BLUE),
                    disp.getSystemColor(SWT.COLOR_DARK_GREEN),
                    disp.getSystemColor(SWT.COLOR_BLACK), disp.getSystemColor(SWT.COLOR_DARK_GRAY) };
            mBackColors = new Color[] { disp.getSystemColor(SWT.COLOR_WHITE),
                    disp.getSystemColor(SWT.COLOR_WHITE), disp.getSystemColor(SWT.COLOR_RED),
                    disp.getSystemColor(SWT.COLOR_YELLOW), disp.getSystemColor(SWT.COLOR_WHITE),
                    disp.getSystemColor(SWT.COLOR_WHITE), disp.getSystemColor(SWT.COLOR_WHITE),
                    disp.getSystemColor(SWT.COLOR_WHITE) };
            mSearchBackColor = disp.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        }
    }

    Colors mColors = new Colors();

    public static class ColorSerializer implements JsonSerializer<Color> {
        @Override
        public JsonElement serialize(Color arg0, Type arg1, JsonSerializationContext arg2) {
            return arg2.serialize(arg0.getRGB());
        }
    }

    public static class ColorDeserializer implements JsonDeserializer<Color> {
        @Override
        public Color deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
                throws JsonParseException {
            return new Color(Display.getCurrent(), (RGB) (arg2.deserialize(arg0, RGB.class)));

        }

    }

    private void initDefault() {

        // mRecentUrls.add(new LogUrl("qconn", "10.222.98.205", 8000));
        // mRecentUrls.add(new LogUrl("qconn", "10.222.109.58", 8000));
        addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY, LogFilter.OP_LESSTHEN,
                Integer.valueOf(7)));
        addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY, LogFilter.OP_LESSTHEN,
                Integer.valueOf(6)));
        addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY, LogFilter.OP_LESSTHEN,
                Integer.valueOf(5)));
    }

    private SystemConfigs() {
        initDefault();
        // addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY,
        // LogFilter.OP_LESSTHEN, Integer.valueOf(6)));
        // addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY,
        // LogFilter.OP_LESSTHEN, Integer.valueOf(5)));
        // addRecentFilter(LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY,
        // LogFilter.OP_LESSTHEN, Integer.valueOf(4)));

    }

    public Color getSearchMarkerBackground() {
        return mColors.mSearchBackColor;
    }

    public Color getLogForeground(int level) {
        return mColors.mForeColors[level];
    }

    public Color getLogBackground(int level) {
        if (level < 5)
            return mColors.mBackColors[level];
        else
            return null;
    }

    int                            mLogRollingLines = 200000;
    
    public int getLogRollingLines() {
        return mLogRollingLines;
    }
    transient ArrayList<LogFilter> mRecentFilters = new ArrayList<LogFilter>(10);

    public void addRecentFilter(LogFilter f) {
        // TODO: no duplicate.
        mRecentFilters.add(0, f);
    }

    public LogFilter getRecentFilter(int i) {
        if (i < mRecentFilters.size()) {
            return mRecentFilters.get(i);
        }
        return null;
    }

    ArrayList<String> mRecentFiles = new ArrayList<String>(10);

    public void addRecentFile(String f) {
        if (mRecentFiles.contains(f)) {
            mRecentFiles.remove(f);
        }
        mRecentFiles.add(0, f);
        if (mRecentFiles.size() > 10) {
            mRecentFiles.remove(mRecentFiles.size() - 1);
        }
    }

    public String getRecentFile(int i) {
        if (i < mRecentFiles.size()) {
            return mRecentFiles.get(i);
        }
        return null;
    }

    public void addRecentUrl(LogUrl u) {
        if (mRecentUrls.contains(u)) {
            mRecentUrls.remove(u);
        }
        mRecentUrls.add(0, u);
        if (mRecentUrls.size() > 10) {
            mRecentUrls.remove(mRecentUrls.size() - 1);
        }
    }

    public LogUrl getRecentUrl(int i) {
        if (i < mRecentUrls.size()) {
            return mRecentUrls.get(i);
        }
        return null;
    }

    String mAdbPath = "/Developer/SDKs/android-sdk/platform-tools";

    public String getAdbPath() {
    	if (!mAdbPath.endsWith("/")) {
    		return mAdbPath + "/";
    	}
        return mAdbPath; // /Developer/SDKs/android-sdk/platform-tools/adb";
    }

    public void setAdbPath(String p) {
        mAdbPath = p;
    }

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapter(Color.class, new ColorSerializer()).create().toJson(this);
    }

    private static SystemConfigs load() {
        File cfgfile = new File(CFG_FNAME);
        if (cfgfile.exists()) {
            try {
                return new GsonBuilder()
                        .setPrettyPrinting()
                        .serializeNulls()
                        .registerTypeAdapter(Color.class, new ColorDeserializer())
                        .create()
                        .fromJson(new InputStreamReader(new FileInputStream(cfgfile), "UTF-8"),
                                SystemConfigs.class);

            } catch (FileNotFoundException e) {
                // e.printStackTrace();
            } catch (JsonSyntaxException e) {
                // e.printStackTrace();
            } catch (JsonIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new SystemConfigs();
    }

    void save() {
        if (CFG_FNAME != null) {
            try {
                OutputStreamWriter wr = new OutputStreamWriter(new FileOutputStream(CFG_FNAME));
                wr.write(toJson());
                wr.flush();
                wr.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
