/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package feiw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
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

        public static final String FIELD_PRIORITY = "priority";
        public static final String FIELD_TAG = "tag";
        public static final String FIELD_TIME = "time";
        public static final String FIELD_CONTENT = "message";
        public static final String FIELD_PID = "PID";
        public static final String FIELD_TID = "TID";

        public static final String FILTER_OP_AND = "and";
        public static final String FILTER_OP_OR = "or";

        public abstract boolean filterLog(final LogParser parser, final String item);

        String mName;

        LogFilter(String n) {
            mName = n;
        }

        void setName(String n) {
            mName = n;
        }

        String getName() {
            return mName;
        }

        @Override
        public String toString() {
            return mName;
        }

        ArrayList<FilterDlg.RawRule> mRawRules = null;

        void setRawRules(ArrayList<FilterDlg.RawRule> rawRules) {
            mRawRules = rawRules;
        }

        ArrayList<FilterDlg.RawRule> getRawRules() {
            return mRawRules;
        }

        @SuppressWarnings("serial")
        static class InvalidLogFilterStringException extends Exception {
            public InvalidLogFilterStringException(String s) {
                super(s);
            }
        }

        static LogFilter fromString(String s) throws InvalidLogFilterStringException {
            String fs[] = s.split(FILTER_OP_AND);
            if (fs != null && fs.length > 0) {
                System.out.println(fs[0]);
            }
            int idx = s.indexOf(FILTER_OP_AND);
            if (idx > 0) {
                return fromString(s.substring(0, idx)).and(fromString(s.substring(idx + FILTER_OP_AND.length())));
            }
            idx = s.indexOf(FILTER_OP_OR);
            if (idx > 0) {
                return fromString(s.substring(0, idx)).or(fromString(s.substring(idx + FILTER_OP_OR.length())));
            }

            String[] txt = s.split(" ");
            if (txt == null || txt.length != 3 || txt[0] == null || txt[1] == null || txt[2] == null) {
                throw new InvalidLogFilterStringException("invalid log filter string: " + s);
            }

            Object o = txt[2];
            if (txt[0].equals(FIELD_PRIORITY)) {
                o = Integer.parseInt(txt[2]);
            }
            return newLogFilter(txt[0], txt[1], o);

        }

        public LogFilter and(final LogFilter f) {
            return new LogFilter(getName() + FILTER_OP_AND + f.getName()) {
                @Override
                public boolean filterLog(final LogParser parser, final String item) {
                    return LogFilter.this.filterLog(parser, item) && f.filterLog(parser, item);
                }
            };
        }

        public LogFilter or(final LogFilter f) {
            return new LogFilter(getName() + FILTER_OP_OR + f.getName()) {
                @Override
                public boolean filterLog(final LogParser parser, final String item) {
                    return LogFilter.this.filterLog(parser, item) || f.filterLog(parser, item);
                }
            };
        }


        public static LogFilter newSelectedFilter(String name) {
            return new LogFilter("Selected" + name) {
                @Override
                public boolean filterLog(LogParser parser, String item) {
                    return true;
              }
            };
        }
      
        public LogFilter not() {
            return new LogFilter("NOT(" + getName() + ")") {
                @Override
                public boolean filterLog(LogParser parser, String item) {
                    return !LogFilter.this.filterLog(parser, item);
                }
            };
        }

        public static LogFilter newLogFilter(String field, String op, final Object dstObj) {
            LogFilter logFilter = null;
            FilterDlg.RawRule rawRule = new FilterDlg.RawRule();

            rawRule.index = 0;
            rawRule.field = field;
            rawRule.mop = null;
            rawRule.op = op;

            ArrayList<FilterDlg.RawRule> rawRules = new ArrayList<FilterDlg.RawRule>(5);
            rawRules.clear();
            rawRules.add(rawRule);

            if (FIELD_PRIORITY.equals(field)) {
                rawRule.value = "" + ((Integer) dstObj).intValue();
                if (OP_EQUALS.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return parser.parsePriority(item) == ((Integer) dstObj).intValue();
                        }
                    };
                } else if (OP_GREATERTHAN.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return parser.parsePriority(item) > ((Integer) dstObj).intValue();
                        }

                    };
                } else if (OP_LESSTHEN.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return parser.parsePriority(item) < ((Integer) dstObj).intValue();
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
                rawRule.value = (String) dstObj;
                if (OP_CONTAINS.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        private final StringPattern mPat = new StringPattern((String) dstObj, false);

                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return mPat.isContainedBy(parser.parseMessage(item)) >= 0;
                        }
                    };
                }
            } else if (FIELD_TAG.equals(field)) {
                rawRule.value = (String) dstObj;
                if (OP_EQUALS.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return dstObj.equals(parser.parseTag(item));
                        }
                    };
                } else if (OP_CONTAINS.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            String tag = parser.parseTag(item);
                            return (tag != null) && tag.contains((String) dstObj);
                        }
                    };
                }
            } else if (FIELD_PID.equals(field)) {
                rawRule.value = (String) dstObj;
                if (OP_EQUALS.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return dstObj.equals(parser.parsePID(item));
                        }
                    };
                }
            } else if (FIELD_TID.equals(field)) {
                rawRule.value = (String) dstObj;
                if (OP_EQUALS.equals(op)) {
                    logFilter = new LogFilter(field + " " + op + " " + dstObj) {
                        @Override
                        public boolean filterLog(final LogParser parser, final String item) {
                            return dstObj.equals(parser.parseTID(item));
                        }
                    };
                }
            }

            if (logFilter != null)
                logFilter.setRawRules(rawRules);

            return logFilter;
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

    protected long mNotifyTimeSpan = SystemConfigs.instance().LIVE_LOG_NOTIFYTIME;

    protected int fetchLogs(InputStream is) throws IOException {
        BufferedReader din = new BufferedReader(new InputStreamReader(is));
        int line = 0;
        String str = din.readLine();
        long start_time = System.currentTimeMillis();
        while (str != null) {
            str = str.trim();
            if (!str.isEmpty()) {
                line++;
                long curtime = System.currentTimeMillis();
                if (is.available() == 0 || curtime - start_time > mNotifyTimeSpan) {
                    addLogItem(str, true);
                    start_time = curtime;
                } else {
                    addLogItem(str, false);
                }
            }

            str = din.readLine();

        }
        return line;
        // System.out.println(" log lines = " + line);
    }

    protected int fetchLogs(InputStream is, LogParser parser) throws IOException {
        BufferedReader din = new BufferedReader(new InputStreamReader(is));
        int line = 0;
        String str = parser.readOneLog(din);
        long start_time = System.currentTimeMillis();
        while (str != null) {
            line++;
            long curtime = System.currentTimeMillis();
            if (is.available() == 0 || curtime - start_time > mNotifyTimeSpan) {
                addLogItem(str, true);
                start_time = curtime;
            } else {
                addLogItem(str, false);
            }
            str = parser.readOneLog(din);
        }
        return line;
        // System.out.println(" log lines = " + line);
    }

    public static class LogView {
        private List<String> mFilteredItems;
        private LogListener mListener = null;
        private LogFilter mFilter = null;
        private StringPattern mSearchPattern = null;
        private LogParser mParser;
        private List<String> mSource;
        private SlogTabFrame mLogTabFrame = null;
        private int mRollLines;
        private AtomicBoolean mLogChanged = new AtomicBoolean(false);
        private AtomicBoolean mPaused = new AtomicBoolean(false);
        private TreeMap<Integer, Integer> mFilteredLineMap = new TreeMap<Integer, Integer>();
        private Integer mSourceLines = 0;
        private Integer mFilteredLines = 0;
        private boolean mIsSelectedLogView = false;
        ArrayList<FilterDlg.RawRule> mRawRules = null;

        void setRawRules(ArrayList<FilterDlg.RawRule> rawRules) {
            mRawRules = rawRules;
        }

        public void setLogTabFrame(SlogTabFrame logTabFrame) {
            mLogTabFrame = logTabFrame;
        }

        public SlogTabFrame getLogTabFrame() {
            return mLogTabFrame;
        }

        public void setSelectedLogView(boolean selectedLogView) {
            this.mIsSelectedLogView = selectedLogView;
        }

        public boolean isSelectedLogView() {
            return this.mIsSelectedLogView;
        }

        public TreeMap<Integer, Integer> getFilterLineMap() {
            return mFilteredLineMap;
        }

        ArrayList<FilterDlg.RawRule> getRawRules() {
            return mRawRules;
        }

        public LogParser getLogParser() {
            return mParser;
        }

        public int getRollLines() {
            return mRollLines;
        }

        public final StringPattern getSearchPattern() {
            return mSearchPattern;
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
            if (mListener != null && !mPaused.get() && mLogChanged.get()) {
                mListener.onLogChanged();
                mLogChanged.set(false);
            }
        }

        public void writeLogs(OutputStream os) throws IOException {
            BufferedWriter dw = new BufferedWriter(new OutputStreamWriter(os));
            synchronized (mFilteredItems) {
                final int s = mFilteredItems.size();
                int i = 0;
                for (i = 0; i < s - 1; i++) {
                    dw.write(mFilteredItems.get(i));
                    dw.write('\n');
                }
                dw.write(mFilteredItems.get(i));
            }
            dw.flush();
        }

        private LogView(LogListener listener, LogFilter filter, LogParser parser, List<String> source, int rollLines) {
            mListener = listener;
            mFilter = filter;
            mParser = parser;
            mRollLines = rollLines;
            mFilteredItems = Collections.synchronizedList(new ArrayList<String>());
            mSource = source;
            mFilteredLineMap.clear();

            if (source != null) {
                synchronized (source) {
                    for (String it : source) {
                        if (filter.filterLog(parser, it)) {
                            mFilteredLineMap.put(mFilteredLines, mSourceLines);
                            mFilteredItems.add(it);
                            mFilteredLines++;
                        }
                        mSourceLines++;
                    }
                }
            }

            if (mFilteredItems.size() > 0) {
                mLogChanged.set(true);
                notifyListener();
            }
        }

        public boolean isSearchResults(final String logmsg) {
            if (mSearchPattern != null) {
                return mSearchPattern.isContainedBy(logmsg) >= 0;
            }
            return false;
        }

        public void add(final String item, boolean notify) {
            if (mFilter == null || mFilter.filterLog(mParser, item)) {
                synchronized (mFilteredItems) {
                    if (isSearchResults(item)) {
                        mSearchResults++;
                    }
                    if (mRollLines > 0 && mFilteredItems.size() >= mRollLines) {
                        mFilteredItems.remove(0);
                        mFilteredLineMap.remove(0);
                    }
                    mFilteredLineMap.put(mFilteredLines, mSourceLines);
                    mFilteredItems.add(item);
                    mFilteredLines++;
                }
                mLogChanged.set(true);
                if (notify) {
                    notifyListener();
                }
            }
            mSourceLines++;
        }

        public void clear() {
            if (mFilteredItems.size() > 0) {
                mSearchResults = -1;
                mSearchPattern = null;
                mFilteredItems.clear();
                mLogChanged.set(true);
                mFilteredLineMap.clear();
                mSourceLines = 0;
                mFilteredLines = 0;
                notifyListener();
            }
        }

        public LogFilter getLogFilter() {
            return mFilter;
        }

        public void updateFilter(LogFilter filter) {
            this.clear();
            mFilter = filter;
            if (mSource != null) {
                synchronized (mSource) {
                    for (String it : mSource) {
                        if (mFilter.filterLog(mParser, it)) {
                            mFilteredLineMap.put(mFilteredLines, mSourceLines);
                            mFilteredItems.add(it);
                            mFilteredLines++;
                        }
                        mSourceLines++;
                    }
                }
            }

            if (mFilteredItems.size() > 0) {
                mLogChanged.set(true);
                notifyListener();
            }
        }

        public int size() {
            return mFilteredItems.size();
        }

        public final String getLog(int index) {
            synchronized (mFilteredItems) {
                if (index >= 0 && index < mFilteredItems.size()) {
                    return mFilteredItems.get(index);
                }
            }
            return null;
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
                for (String it : mFilteredItems) {
                    if (isSearchResults(it)) {
                        results++;
                    }
                }
            }
            mSearchResults = results;
            mLogChanged.set(true);
            mListener.onSearchResult();
        }
    }

    public synchronized void removeLogView(LogView v) {
        mViews.remove(v);
        if (mViews.size() == 0) {
            disconnect();
        }
    }

    int mRollLines = -1;

    public synchronized LogView newLogView(LogListener listener, LogFilter filter, LogParser parser,
                                           LogView parentView) {
        LogView v = new LogView(listener, filter, parser, parentView == null ? null : parentView.mFilteredItems,
                mRollLines);
        mViews.add(v);
        return v;
    }

    public synchronized void addLogItem(final String item, boolean notify) {
        for (LogView v : mViews) {
            v.add(item, notify);
        }
    }

    public synchronized void notifyViews() {
        for (LogView v : mViews) {
            v.notifyListener();
        }
    }

    public void disconnect() {

    }

    List<LogView> mViews = new ArrayList<LogView>(5);

    List<StatusListener> mStatusListeners = Collections
            .synchronizedList(new ArrayList<StatusListener>(5));

}
