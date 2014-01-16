package feiw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogSource {
    public static final int stIdle = 0;
    public static final int stConnecting = 1;
    public static final int stConnected = 2;

    
    public static abstract class LogFilter {
        public static final String OP_EQUALS = "=";
        public static final String OP_CONTAINS = "contains";
        public static final String OP_GREATERTHAN = ">";
        public static final String OP_LESSTHEN = "<";
        public static final String FIELD_LEVEL = "level";
        public static final String FIELD_TIME = "time";
        public static final String FIELD_CONTENT = "content";
        
        public abstract boolean filterLog(LogItem item);
        
        String mName;
        LogFilter(String n) {
            mName = n;
        }
        
        String getName() {
            return mName;
        }
        
        public LogFilter and(final LogFilter f) {
            return new LogFilter(getName() + " and " + f.getName()) {
                @Override
                public boolean filterLog(LogItem item) {
                   return LogFilter.this.filterLog(item) && f.filterLog(item);
                }
            };
        }
        
        public  LogFilter or(final LogFilter f) {
            return new LogFilter(getName() + " or " + f.getName()) {
                @Override
                public boolean filterLog(LogItem item) {
                   return LogFilter.this.filterLog(item) || f.filterLog(item);
                }
            };
        }
            public static LogFilter newLogFilter(String field, String op, final Object dstObj) {
            if (FIELD_LEVEL.equals(field)) {
                if (OP_EQUALS.equals(op)) {
                    return new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(LogItem item) {
                            return item.getLevel() == ((Integer)dstObj).intValue();
                        }
                    };
                } else if (OP_GREATERTHAN.equals(op)) {
                    return new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(LogItem item) {
                            return item.getLevel() > ((Integer)dstObj).intValue();
                        }
                        
                    };
                } else if (OP_LESSTHEN.equals(op)) {
                    return new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(LogItem item) {
                            return item.getLevel() < ((Integer)dstObj).intValue();
                        }
                        
                    };
                }
            } else if (FIELD_TIME.equals(field)) {
                if (OP_EQUALS.equals(op)) {
                    
                } else if (OP_CONTAINS.equals(op)) {
                    
                } else if (OP_GREATERTHAN.equals(op)) {
                    
                } else if (OP_LESSTHEN.equals(op)) {
                    
                }
                
            } else if (FIELD_CONTENT.equals(field)) {
                if (OP_CONTAINS.equals(op)) {
                    return new LogFilter(field + " " + op + " " + dstObj) {
                        private final StringPattern mPat = new StringPattern((String)dstObj, false);
                        @Override
                        public boolean filterLog(LogItem item) {
                            return mPat.isContainedBy(item.getText()) >= 0;
                        }
                    };
                }
            }
            return null;
        }
        
    }
 
    public interface LogListener {
        public void onLogChanged();

        public void onSearchResult();
    }

    public interface StatusListener {
        public void onStatusChanged(int oldStatus, int newStatus);
    }

    protected int mStatus = stIdle;

    public synchronized int getStatus() {
        return mStatus;
    }

    public synchronized void addStatusListener(StatusListener slis) {
        if (slis != null) {
            mStatusListeners.add(slis);
        }
    }

    public synchronized void removeStatusListener(StatusListener slis) {
        mStatusListeners.remove(slis);
    }

    protected synchronized void setStatus(int st) {
        if (st != mStatus) {
            for (StatusListener li : mStatusListeners) {
                li.onStatusChanged(mStatus, st);
            }
            mStatus = st;
        }
    }
    protected void fetchLogs(InputStream is) throws IOException {
        BufferedReader din = new BufferedReader(new InputStreamReader(is));
        String str = din.readLine();
        long start_time = System.currentTimeMillis();
        while (str != null) {
            if (!str.isEmpty()) {
                LogItem it = new LogItem(str);
                long curtime = System.currentTimeMillis();
                if (is.available() < 1 || curtime - start_time > 100) {
                    addLogItem(it, true);
                    start_time = curtime;
                } else {
                    addLogItem(it, false);
                }
            }
            str = din.readLine();
        }
    }
    protected void fetchLogsoo(InputStream is) throws IOException {
        BufferedReader din = new BufferedReader(new InputStreamReader(is));
        String str = din.readLine();
        int newlines = 0;
        while (str != null) {
            if (!str.isEmpty()) {
                LogItem it = new LogItem(str);
                if (is.available() == 0 || newlines > SystemConfigs.MIN_NOTIFY_COUNT) {
                    addLogItem(it, true);
                    newlines = 0;
                } else {
                    addLogItem(it, false);
                    newlines++;
                }
            }
            str = din.readLine();
        }
    }

    public static final class LogItem {
        private final String[] texts;
        private Date   mTime;
        static final SimpleDateFormat mDfmt = new SimpleDateFormat("MMM dd HH:mm:ss.SSS");
        static final SimpleDateFormat mDfmts = new SimpleDateFormat("MMM dd HH:mm:ss");
        static private SimpleDateFormat mParser = mDfmt;
        private int mLevel = 7;

        Date getTime() {
            return mTime;
        }
        
        public String getText() {
            return texts[4];
        }
        public String getText(int i) {
            if (i >= 0 && i < texts.length) {
                return texts[i];
            }
            return null;
        }

       static final String[] seperator = { "    ", " ", " ", " " };
        public LogItem(final String str) {
            final String [] ret = new String[5];
            texts = ret;
            int idx = 0, nextidx;
            final int slen = str.length();
            for (int i = 0; i < 4; i++) {
                nextidx = str.indexOf(seperator[i], idx);
                if (nextidx <= 0) {
                    ret[0] = ret[1] = ret[2] = ret[3] = null;
                    ret[4] = str;
                    return;
                }
                ret[i] = str.substring(idx, nextidx);
                idx = nextidx + seperator[i].length();
                while (slen > idx && str.charAt(idx) == ' ')
                    idx++;
            }
            ret[4] = str.substring(idx);
            if (!ret[1].isEmpty()) {
                mLevel = ret[1].charAt(0) - '0';
                if (mLevel < 0 || mLevel > 7) {
                    mLevel = 6;
                }
            }

            try {
                mTime = mParser.parse(ret[0]);
            } catch (ParseException e) {
                try {
                    mParser = mDfmts;
                    mTime = mParser.parse(ret[0]);
                } catch (ParseException e1) {
                    mTime = null;
                }
            }
        }

        public LogItem(String[] txt) {
            texts = txt;
        }

        public int getTextCount() {
            if (texts != null) {
                return texts.length;
            }
            return 0;
        }

        public int getLevel() {
            return mLevel;
        }
    }

    public static class LogView {
        private List<LogItem> mFilteredItems;
        private LogListener mListener = null;
        private LogFilter mFilter = null;
        private StringPattern mSearchPattern = null;

        private AtomicBoolean mLogChanged = new AtomicBoolean(false);
        private AtomicBoolean mPaused = new AtomicBoolean(false);

        public final String getSearchPattern() {
            return mSearchPattern.toString();
        }
        public boolean isPaused() {
            return mPaused.get();
        }

        public void resume() {
            mPaused.set(false);
        }

        public void pause() {
            mPaused.set(true);
        }

        private void notifyListener() {
            mLogChanged.set(true);
            if (mListener != null && !mPaused.get()) {
                mListener.onLogChanged();
            }
        }

        public boolean getChangedFlag() {
            return mLogChanged.get();
        }

        public void setChangeFlag(boolean flag) {
            mLogChanged.set(flag);
        }

        private LogView(LogListener listener, LogFilter filter, List<LogItem> source) {
            mListener = listener;
            mFilter = filter;
            if (filter == null) {
                mFilteredItems = source;
                if (source.size() > 0) {
                    notifyListener();
                }
                return;
            }
            mFilteredItems = (List<LogItem>) Collections.synchronizedList(new ArrayList<LogItem>(
                    10000));
 
            synchronized (source) {
                for (LogItem it : source) {
                    if (filter.filterLog(it)) {
                        mFilteredItems.add(it);
                    }
                }
            }
 
            if (mFilteredItems.size() > 0) {
                notifyListener();
            }
        }
        
        public boolean isSearchResults(final LogItem item) {
            if (mSearchPattern != null) {
                return mSearchPattern.isContainedBy(item.getText()) >= 0;
            }
            return false;
        }
        public void add(LogItem item, boolean notifylistner) {
            if (mFilter == null) {
                if (isSearchResults(item)) {
                    mSearchResults++;
                }
                if (notifylistner) {
                    // System.out.println("notifiy listener. log size = " +
                    // mFilteredItems.size());
                    notifyListener();

                }
            } else if (mFilter.filterLog(item)) {
                if (isSearchResults(item)) {
                    mSearchResults++;
                }
                mFilteredItems.add(item);
                if (notifylistner) {
                    notifyListener();
                }
            }
        }

        public void clear() {
            mSearchResults = 0;
            mFilteredItems.clear();
            notifyListener();
        }

        public int size() {
                return mFilteredItems.size();
        }

        public LogItem getLog(int index) {
            return mFilteredItems.get(index);
        }

        private int mSearchResults = -1;

        public int getSearchResults() {
            return mSearchResults;
        }

        public int getPrevSearchResult(int start) {
            if (mSearchResults <= 0) {
                return -1;
            }
            synchronized (mFilteredItems) {
                if (start < 0 || start >= mFilteredItems.size()) {
                    start = mFilteredItems.size() - 1;
                }
                for (int i = start; i >= 0; i--) {
                    if (isSearchResults(mFilteredItems.get(i))) {
                        return i;
                    }
                }
                for (int i = mFilteredItems.size() - 1; i > start; i--) {
                    if (isSearchResults(mFilteredItems.get(i))) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public int getNextSearchResult(int start) {
            if (mSearchResults <= 0) {
                return -1;
            }
            synchronized (mFilteredItems) {
                for (int i = start; i < mFilteredItems.size(); i++) {
                    if (isSearchResults(mFilteredItems.get(i))) {
                        return i;
                    }
                }

                for (int i = 0; i < start; i++) {
                    if (isSearchResults(mFilteredItems.get(i))) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public void search(String txt, boolean caseSenstive) {
            if (mFilteredItems == null || mFilteredItems.size() <= 0) {
                return;
            }
            mSearchPattern = new StringPattern(txt, caseSenstive);
            int results = 0;
            synchronized (mFilteredItems) {
                for (LogItem it : mFilteredItems) {
                    if(isSearchResults(it)) {
                            results++;
                     }
                }
            }
            mSearchResults = results;
            mLogChanged.set(true);
            mListener.onSearchResult();
        }
    }

    public void removeLogView(LogView v) {
        mViews.remove(v);
        if (mViews.size() == 0) {
            disconnect();
        }
    }

    public LogView newLogView(LogListener listener, LogFilter filter) {
        LogView v = new LogView(listener, filter, mItems);
        mViews.add(v);
        return v;
    }

    public void addLogItem(LogItem item, boolean notifylistener) {
        mItems.add(item);
        synchronized (mViews) {
            for (LogView v : mViews) {
                v.add(item, notifylistener);
            }
        }
    }

    public void disconnect() {

    }

    List<LogItem> mItems = (List<LogItem>) Collections.synchronizedList(new ArrayList<LogItem>(
            10000));
    List<LogView> mViews = (List<LogView>) Collections.synchronizedList(new ArrayList<LogView>(5));

    List<StatusListener> mStatusListeners = (List<StatusListener>) Collections
            .synchronizedList(new ArrayList<StatusListener>(5));

}
