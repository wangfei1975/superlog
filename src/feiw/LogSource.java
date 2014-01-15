package feiw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogSource {
    public static final int stIdle = 0;
    public static final int stConnecting = 1;
    public static final int stConnected = 2;

    public interface LogFilter {
        public boolean filterLog(LogItem item);
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
        public String[] texts;
        public int searchMarker = 0;
        private int mLevel = 7;

        public String getText(int i) {
            if (texts != null && i >= 0 && i < texts.length) {
                return texts[i];
            }
            return null;
        }

        public LogItem(LogItem o) {
            texts = o.texts;
            mLevel = o.mLevel;
            searchMarker = 0;
        }

        public LogItem(String str) {
            String[] seperator = { "    ", " ", " ", " " };
            String[] ret = new String[5];
            texts = ret;

            int idx = 0, nextidx;
            for (int i = 0; i < 4; i++) {
                nextidx = str.indexOf(seperator[i], idx);
                if (nextidx <= 0) {
                    ret[0] = ret[1] = ret[2] = ret[3] = null;
                    ret[4] = str;
                    return;
                }
                ret[i] = str.substring(idx, nextidx);
                idx = nextidx + seperator[i].length();
                while (str.length() > idx && str.charAt(idx) == ' ')
                    idx++;
            }
            ret[4] = str.substring(idx);
            if (ret[1] != null && !ret[1].isEmpty()) {
                mLevel = ret[1].charAt(0) - '0';
                if (mLevel < 0 || mLevel > 7) {
                    mLevel = 6;
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

        public int getSearchMarker() {
            return searchMarker;
        }

        public int getLevel() {
            return mLevel;
        }
    }

    public static class LogView {
        private List<LogItem> mFilteredItems;
        private LogListener mListener = null;
        private LogFilter mFilter = null;
        private String mSearchStr = null;
        private boolean mSearchCase = false;
        private AtomicBoolean mLogChanged = new AtomicBoolean(false);
        private AtomicBoolean mPaused = new AtomicBoolean(false);

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
                    1000));
            synchronized (source) {
                for (LogItem it : source) {
                    if (filter.filterLog(it)) {
                        mFilteredItems.add(new LogItem(it));
                    }
                }
            }
            if (mFilteredItems.size() > 0) {
                notifyListener();
            }
        }

        public void add(LogItem item, boolean notifylistner) {
            if (mFilter == null) {
                if (mSearchStr != null && !mSearchStr.isEmpty()) {
                    String slog = item.getText(4);
                    if (slog != null) {
                        if (strContains(slog, mSearchStr, mSearchCase)) {
                            item.searchMarker = 1;
                            mSearchResults++;
                        }
                    }
                }

                if (notifylistner) {
                    // System.out.println("notifiy listener. log size = " +
                    // mFilteredItems.size());
                    notifyListener();

                }
            } else if (mFilter.filterLog(item)) {
                LogItem ni = new LogItem(item);
                if (mSearchStr != null && !mSearchStr.isEmpty()) {
                    String slog = ni.getText(4);
                    if (slog != null) {
                        if (strContains(slog, mSearchStr, mSearchCase)) {
                            ni.searchMarker = 1;
                            mSearchResults++;
                        }
                    }
                }
                mFilteredItems.add(ni);
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

        private int mSearchResults = 0;

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
                    if (mFilteredItems.get(i).searchMarker == 1) {
                        return i;
                    }
                }
                for (int i = mFilteredItems.size() - 1; i > start; i--) {
                    if (mFilteredItems.get(i).searchMarker == 1) {
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
                    if (mFilteredItems.get(i).searchMarker == 1) {
                        return i;
                    }
                }

                for (int i = 0; i < start; i++) {
                    if (mFilteredItems.get(i).searchMarker == 1) {
                        return i;
                    }
                }
            }
            return -1;
        }

        private static boolean strContains(String str, String sub, boolean caseSenstive) {
            if (!caseSenstive) {
                str.toLowerCase();
                sub.toLowerCase();
            }
            return str.contains(sub);
        }

        public void search(String txt, boolean caseSenstive) {
            if (mFilteredItems == null || mFilteredItems.size() <= 0) {
                return;
            }
            mSearchStr = txt;
            mSearchCase = caseSenstive;
            int results = 0;
            synchronized (mFilteredItems) {
                for (LogItem it : mFilteredItems) {
                    it.searchMarker = 0;
                    String slog = it.getText(4);
                    if (slog != null) {
                        if (strContains(slog, txt, caseSenstive)) {
                            it.searchMarker = 1;
                            results++;
                        }
                    }
                }
            }
            if (results > 0 || results != mSearchResults) {
                mSearchResults = results;
                mLogChanged.set(true);
                mListener.onSearchResult();
            }
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
