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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.TableItem;

public class LogParser {

    public int parsePriority(final String log) {
        return 7;
    }

    public String parseMessage(final String log) {
        return log;
    }

    public Date parseTime(final String log) {
        return null;
    }

    public String parseTag(final String log) {
        return null;
    }

    public String parsePID(final String log) {
        return null;
    }

    public String parseTID(final String log) {
        return null;
    }

    public String readOneLog(final BufferedReader is) throws IOException {
        String str;
        do {
            str = is.readLine();
            if (str == null) {
                return null;
            }
            str = str.trim();
        } while (str.isEmpty());
        return str;
    }

    public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {

    }

    final public void updateTableItemColors(final TableItem item, int prority, boolean searchMarker) {
        final SystemConfigs scfgs = SystemConfigs.instance();
        final Color bk = scfgs.getLogBackground(prority);
        if (bk != null) {
            item.setBackground(bk);
        }
        item.setForeground(scfgs.getLogForeground(prority));
        if (searchMarker) {
            item.setImage(Resources.search_16);
            if (prority >= 4) {
                item.setBackground(scfgs.getSearchMarkerBackground());
            }
        }
    }

    public static final int width[] = { 50, 100, 200, 80, 1000 };

    public int[] getHeaderWidth() {
        return width;
    }

    public String[] getTableHeader() {
        final String[] title = { "Flag", "Line", "Time", "Prority", "Message" };
        return title;
    }

    public static LogParser newLogParser(InputStream is) {
        BufferedReader din = new BufferedReader(new InputStreamReader(is));
        int line = 0;
        String str;
        ArrayList<String> logs = new ArrayList<String>();
        try {
            str = din.readLine();
            while (str != null && line < 100) {
                str = str.trim();
                if (!str.isEmpty()) {
                    line++;
                    logs.add(str);
                }
                str = din.readLine();
            }

            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newLogParser(logs);
    }

    public static LogParser newLogParser(List<String> logs) {
        final String[] parserClass = new String[] { "feiw.LogParser$QnxLogParser", "feiw.LogParser$QnxShortLogParser",
                "feiw.LogParser$AndroidThreadtimeLogParser", "feiw.LogParser$AndroidBriefLogParser",
                "feiw.LogParser$AndroidTimeLogParser", "feiw.LogParser$AndroidLongLogParser", };
        if (logs == null || logs.isEmpty()) {
            return new QnxLogParser();
        }
        int maxscore = -1;
        Class<?> maxClass = null;
        try {
            for (String cn : parserClass) {
                Class<?> c = Class.forName(cn);
                Method m = c.getMethod("taste", String.class);
                Object parser = c.newInstance();
                int score = 0;
                for (String log : logs) {
                    if ((Boolean) m.invoke(parser, log)) {
                        score++;
                    }
                }
                if (score > maxscore) {
                    maxClass = c;
                    maxscore = score;
                }

            }
            if (maxClass != null) {
                return (LogParser) maxClass.newInstance();
            } else {
                return new QnxLogParser();
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new QnxLogParser();
    }

    public static final class QnxShortLogParser extends LogParser {

        public static boolean taste(final String log) {
            // MMM dd HH:mm:ss 7 20 2 m
            if (log.length() > 33 && (log.charAt(32) == ' ' || log.charAt(33) == ' ')) {
                if (log.charAt(3) == ' ' && log.charAt(6) == ' ') {
                    if (log.charAt(9) == ':' && log.charAt(12) == ':') {
                        if (log.charAt(15) == ' ' && log.charAt(16) == ' ' && log.charAt(17) == ' '
                                && log.charAt(18) == ' ') {
                            char ch = log.charAt(19);
                            return ch >= '0' && ch <= '7';
                        }
                    }
                }
            }
            return false;
        }

        static final String[] mTableHeader = { "Flag", "Line", "Time", "Prority", "Message" };

        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }

        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return log.charAt(19) - '0';
            }
            return 7;
        }

        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                return log.substring(33);
            }
            return log;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg;

            if (taste(log)) {
                item.setText(2, log.substring(0, 16));
                char chpri = log.charAt(19);
                item.setText(3, String.valueOf(chpri));
                pri = chpri - '0';
                msg = log.substring(33);
            } else {
                msg = log;
            }
            item.setText(4, msg);
            updateTableItemColors(item, pri, (searchPat != null && searchPat.isContainedBy(msg) >= 0));
        }
    }

    public static final class QnxLogParser extends LogParser {
        public static boolean taste(final String log) {
            // MMM dd HH:mm:ss.SSS 7 20 2 m
            if (log.length() > 37 && log.charAt(36) == ' ') {
                if (log.charAt(3) == ' ' && log.charAt(6) == ' ') {
                    if (log.charAt(9) == ':' && log.charAt(12) == ':') {
                        if (log.charAt(15) == '.' && log.charAt(19) == ' ') {
                            char ch = log.charAt(23);
                            return ch >= '0' && ch <= '7';
                        }
                    }
                }
            }
            return false;
        }

        static final String[] mTableHeader = { "Flag", "Line", "Time", "Priority", "Message" };

        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }

        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return log.charAt(23) - '0';
            }
            return 7;
        }

        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                return log.substring(37);
            }
            return log;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg;
            if (taste(log)) {
                item.setText(2, log.substring(0, 20));
                char chpri = log.charAt(23);
                item.setText(3, String.valueOf(chpri));
                pri = chpri - '0';
                msg = log.substring(37);
            } else {
                msg = log;
            }
            item.setText(4, msg);
            updateTableItemColors(item, pri, (searchPat != null && searchPat.isContainedBy(msg) >= 0));
        }
    }

    // adb logcat -v brief
    public static final class AndroidBriefLogParser extends LogParser {
        public static boolean taste(final String log) {
            // I/ActivityManager( 91): fsaf
            if (log.length() > 3) {
                final char ch = log.charAt(0);
                if (AndroidBriefLogParser.mapLogPriority(ch) >= 0) {
                    if (log.charAt(1) == '/') {
                        int idx = log.indexOf('(', 2);
                        if (idx >= 0) {
                            idx = log.indexOf(')', idx + 1);
                            if (idx >= 0 && idx + 2 < log.length()) {
                                return (log.charAt(idx + 1) == ':') && (log.charAt(idx + 2) == ' ');
                            }
                        }
                    }
                }
            }
            return false;
        }

        public static int mapLogPriority(char andLogPri) {
            switch (andLogPri) {
            case 'V':
                return 7;
            case 'D':
                return 6;
            case 'I':
                return 5;
            case 'W':
                return 3;
            case 'E':
                return 2;
            case 'F':
                return 1;
            case 'S':
                return 0;
            }
            return -1;
        }

        public static final int mWidth[] = { 50, 100, 80, 300, 1000 };
        static final String[] mTableHeader = { "Flag", "Line", "Priority", "Tag", "Message" };

        @Override
        public int[] getHeaderWidth() {
            return mWidth;
        }

        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }

        @Override
        public String parseTag(final String log) {
            if (taste(log)) {
                int idx1 = log.indexOf('(', 2);
                if (idx1 >= 0) {
                    return log.substring(2, idx1).trim();
                }
            }
            return null;
        }

        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(':', 2);
                if (idx >= 0 && idx + 2 < log.length()) {
                    return log.substring(idx + 2);
                }
                return "";
            }
            return log;
        }

        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return AndroidBriefLogParser.mapLogPriority(log.charAt(0));
            }
            return 7;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            if (taste(log)) {
                char alogpri = log.charAt(0);
                item.setText(2, String.valueOf(alogpri));
                pri = AndroidBriefLogParser.mapLogPriority(alogpri);
                int idx1 = log.indexOf('(', 2);
                if (idx1 >= 0) {
                    item.setText(3, log.substring(2, idx1));
                    int idx = log.indexOf(':', idx1 + 1);
                    if (idx >= 0 && idx + 2 < log.length()) {
                        msg = log.substring(idx + 2);
                    } else {
                        msg = "";
                    }
                }
            }
            item.setText(4, msg);
            updateTableItemColors(item, pri, (searchPat != null && searchPat.isContainedBy(msg) >= 0));
        }
    }
    
    static final DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    // adb logcat -v time
    public static final class AndroidTimeLogParser extends LogParser {
        public static boolean taste(final String log) {
            // 01-17 20:28:35.379 D/Ethernet( 367): Interface eth0 link down
            if (log.length() > 22) {
                if (log.charAt(2) == '-' && log.charAt(5) == ' ' && log.charAt(8) == ':' && log.charAt(11) == ':'
                        && log.charAt(14) == '.') {
                    if (AndroidBriefLogParser.mapLogPriority(log.charAt(19)) >= 0) {
                        if (log.charAt(20) == '/') {
                            int idx = log.indexOf('(', 21);
                            if (idx >= 0) {
                                idx = log.indexOf(')', idx + 1);
                                if (idx >= 0) {
                                    if (idx + 2 < log.length()) {
                                        return (log.charAt(idx + 1) == ':') && (log.charAt(idx + 2) == ' ');
                                    } else {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        public static final int mWidth[] = { 50, 100, 200, 80, 300, 100, 1000 };
        static final String[] mTableHeader = { "Flag", "Line", "Time", "Priority", "Tag", "PID", "Message" };

        @Override
        public int[] getHeaderWidth() {
            return mWidth;
        }

        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }

        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(':', 21);
                if (idx > 0 && idx + 2 < log.length()) {
                    return log.substring(idx + 2);
                }
                return "";
            }
            return log;
        }

        @Override
        public String parseTag(final String log) {
            if (taste(log)) {
                int idx1 = log.indexOf('(', 21);
                if (idx1 >= 0) {
                    return log.substring(21, idx1).trim();
                }
            }
            return null;
        }

        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return AndroidBriefLogParser.mapLogPriority(log.charAt(19));
            }
            return 7;
        }
        @Override
        public String parsePID(final String log) {
            if (taste(log)) {
                int idx1 = log.indexOf('(', 21);
                if (idx1 >= 0) {
                    int idx2 = log.indexOf(')', idx1 + 1);
                    if (idx2 > idx1) {
                        return log.substring(idx1 + 1, idx2).trim();
                    }
                }
            }
            return null;
        }

        @Override
        public Date parseTime(final String log) {
            if (taste(log)) {
                try {
                    return dateFormat.parse(log);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            if (taste(log)) {
                item.setText(2, log.substring(0, 19));
                char alogpri = log.charAt(19);
                item.setText(3, String.valueOf(alogpri));
                pri = AndroidBriefLogParser.mapLogPriority(alogpri);
                int idx1 = log.indexOf('(', 21);
                if (idx1 >= 0) {
                    item.setText(4, log.substring(21, idx1));
                    int idx2 = log.indexOf(')', idx1 + 1);
                    if (idx2 >= 0) {
                        item.setText(5, log.substring(idx1 + 1, idx2).trim());
                        int idx = log.indexOf(':', idx2 + 1);
                        if (idx >= 0 && idx + 2 < log.length()) {
                            msg = log.substring(idx + 2);
                        } else {
                            msg = "";
                        }
                    }
                }
            }
            item.setText(6, msg);
            updateTableItemColors(item, pri, (searchPat != null && searchPat.isContainedBy(msg) >= 0));
        }
    }

    // adb logcat -v threadtime
    public static final class AndroidThreadtimeLogParser extends LogParser {
        static final int IDX_TIME_START = 0;
        static final int IDX_TIME_END = 18;
        static final int IDX_PID_START = 19;
        static final int IDX_PID_END = 24;
        static final int IDX_TID_START = 25;
        static final int IDX_TID_END = 30;
        static final int IDX_PRIORITY = 31;
        static final int IDX_TAG_START = 33;

        public static boolean taste(final String log) {
            // 01-17 20:28:35.379 367 424 D Ethernet: Interface eth0 link down
            // 01-17 21:52:50.409 1330 1330 V GCMBroadcastReceiver: GCM
            // IntentService class: com.espn.notifications.EspnGcmIntentService
            if (log.length() > IDX_TAG_START) {
                if (log.charAt(2) == '-' && log.charAt(5) == ' ' && log.charAt(8) == ':' && log.charAt(11) == ':') {
                    if (log.charAt(14) == '.' && log.charAt(IDX_TIME_END) == ' ') {
                        char ch = log.charAt(IDX_PRIORITY);
                        if (AndroidBriefLogParser.mapLogPriority(ch) >= 0) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        

        @Override
        public Date parseTime(final String log) {
            if (taste(log)) {
                try {
                    return dateFormat.parse(log);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return AndroidBriefLogParser.mapLogPriority(log.charAt(IDX_PRIORITY));
            }
            return 7;
        }

        static final int mWidth[] = { 50, 100, 200, 100, 100, 80, 300, 1200 };
        static final String[] mTableHeader = { "Flag", "Line", "Time", "PID", "TID", "Priority", "Tag", "Message" };

        @Override
        public int[] getHeaderWidth() {
            return mWidth;
        }

        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }

        @Override
        public String parseTag(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(':', IDX_TAG_START);
                if (idx > IDX_TAG_START) {
                    return log.substring(33, idx).trim();
                }
            }
            return null;
        }

        public String parsePID(final String log) {
            if (taste(log)) {
                return log.substring(IDX_PID_START, IDX_PID_END).trim();
            }
            return null;
        }

        public String parseTID(final String log) {
            if (taste(log)) {
                return log.substring(IDX_TID_START, IDX_TID_END).trim();
            }
            return null;
        }

        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(':', IDX_TAG_START);
                if (idx >= IDX_TAG_START && idx + 2 < log.length()) {
                    return log.substring(idx + 2);
                }
                return "";
            }
            return log;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, final StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            if (taste(log)) {
                final String pid = log.substring(IDX_PID_START, IDX_PID_END).trim();
                final String tid = log.substring(IDX_TID_START, IDX_TID_END).trim();
                final char alogpri = log.charAt(IDX_PRIORITY);
                // time
                item.setText(2, log.substring(IDX_TIME_START, IDX_TIME_END));
                item.setText(3, pid);
                item.setText(4, tid);
                item.setText(5, String.valueOf(alogpri));
                pri = AndroidBriefLogParser.mapLogPriority(alogpri);
                int idx = log.indexOf(':', IDX_TAG_START);
                if (idx >= IDX_TAG_START) {
                    if (idx + 2 < log.length()) {
                        msg = log.substring(idx + 2);
                    } else {
                        msg = "";
                    }
                    item.setText(6, log.substring(IDX_TAG_START, idx));
                }
            }
            item.setText(7, msg);
            updateTableItemColors(item, pri, (searchPat != null && searchPat.isContainedBy(msg) >= 0));
        }
    }

    // adb logcat -v long
    public static final class AndroidLongLogParser extends LogParser {
        int IDX_PRIORITY = 33;
        static final int IDX_TIME_START = 2;
        static final int IDX_TIME_END = 20;
        static final int IDX_PID_START = 20;
        static final int IDX_PID_END = 26;
        static final int IDX_TID_START = 27;
        int IDX_TID_END = 33;
        int IDX_TAG_START = 35;

        public boolean taste(final String log) {
            // [ 03-22 12:45:12.856 885: 902 D/PowerManagerService ]
            // acquireWakeLockInternal: lock=138024456, flags=0x1,
            // tag="NlpWakeLock", ws=null, uid=10025, pid=2347
            // [ 03-21 11:13:58.176 5315: 5336 I/DisplayPowerController ]
            // Blocking screen on until initial contents have been drawn.
            // [ 04-07 09:18:23.955 37:0x25 I/boot_progress_start ]
            // 4085
            // [ 04-07 09:18:25.535 37:0x25 I/boot_progress_preload_start ]
            // 5663
            // [ 04-07 09:19:27.466 89:0x67 I/SystemServer ]
            // Device Policy
            // [ 04-07 09:19:45.511 215:0x108 D/RILJ ]
            // [0012]< OPERATOR {Android, Android, 310260}

            if (log.length() > IDX_TAG_START && log.charAt(0) == '[') {
                if (log.charAt(26) == ':' && log.charAt(16) == '.' && log.charAt(20) == ' ') {
                    if (log.charAt(34) == '/') {
                        return (AndroidBriefLogParser.mapLogPriority(log.charAt(IDX_PRIORITY)) >= 0);
                    } else if (log.charAt(33) == '/') {
                        IDX_TID_END = 32;
                        IDX_TAG_START = 34;
                        IDX_PRIORITY = 32;
                        return (AndroidBriefLogParser.mapLogPriority(log.charAt(IDX_PRIORITY)) >= 0);
                    }
                }

            }
            return false;
        }

        String mLastLineLog = null;

        @Override
        public String readOneLog(final BufferedReader is) throws IOException {

            String str;
            StringBuilder sb = new StringBuilder();
            do {
                if (mLastLineLog != null) {
                    str = mLastLineLog;
                    mLastLineLog = null;
                } else {
                    str = is.readLine();
                    if (str == null) {
                        return sb.length() == 0 ? null : sb.toString();
                    }
                    str = str.trim();
                }
                if (!str.isEmpty()) {
                    if (taste(str) && sb.length() > 0) {
                        mLastLineLog = str;
                        return sb.toString();
                    }
                    if (sb.length() > 0) {
                        sb.append("\r\n");
                    }
                    sb.append(str);
                }
            } while (true);
        }

        static final DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

        @Override
        public Date parseTime(final String log) {
            if (taste(log)) {
                try {
                    return dateFormat.parse(log.substring(IDX_TIME_START));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return AndroidBriefLogParser.mapLogPriority(log.charAt(IDX_PRIORITY));
            }
            return 7;
        }

        public static final int mWidth[] = { 50, 100, 200, 100, 100, 80, 300, 1200 };
        static final String[] mTableHeader = { "Flag", "Line", "Time", "PID", "TID", "Priority", "Tag", "Message" };

        @Override
        public int[] getHeaderWidth() {
            return mWidth;
        }

        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }

        @Override
        public String parseTag(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(']', IDX_TAG_START);
                if (idx > IDX_TAG_START) {
                    return log.substring(IDX_TAG_START, idx - 1);
                } else {
                    return log.substring(IDX_TAG_START);
                }
            }
            return null;
        }

        public String parsePID(final String log) {
            if (taste(log)) {
                return log.substring(IDX_PID_START, IDX_PID_END).trim();
            }
            return null;
        }

        @Override
        public String parseTID(final String log) {
            if (taste(log)) {
                return log.substring(IDX_TID_START, IDX_TID_END).trim();
            }
            return null;
        }

        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(']', IDX_TAG_START);
                if (idx > IDX_TAG_START && idx + 1 < log.length()) {
                    return log.substring(idx + 1);
                }
                return "";
            }
            return log;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            if (taste(log)) {
                // time
                item.setText(2, log.substring(IDX_TIME_START, IDX_TIME_END));
                String pid = log.substring(IDX_PID_START, IDX_PID_END);
                String tid = log.substring(IDX_TID_START, IDX_TID_END);
                char alogpri = log.charAt(IDX_PRIORITY);
                item.setText(3, pid.trim());
                item.setText(4, tid.trim());
                item.setText(5, String.valueOf(alogpri));
                pri = AndroidBriefLogParser.mapLogPriority(alogpri);
                int idx = log.indexOf(']', IDX_TAG_START);
                if (idx > IDX_TAG_START) {
                    if (idx + 1 < log.length()) {
                        msg = log.substring(idx + 1);
                    } else {
                        msg = "";
                    }
                    item.setText(6, log.substring(IDX_TAG_START, idx - 1));
                }
            }
            item.setText(7, msg);
            updateTableItemColors(item, pri, (searchPat != null && searchPat.isContainedBy(msg) >= 0));
        }
    }

}