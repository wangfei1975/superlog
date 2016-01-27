package feiw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public int parsePID(final String log) {
        return -1;
    }

    public int parseTID(final String log) {
        return -1;
    }

    public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {

    }

    public static final int width[] = { 28, 50, 155, 30, 1000 };
    public int [] getHeaderWidth() {
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
        final String[] parserClass = new String[] {
                "feiw.LogParser$QnxLogParser", 
                "feiw.LogParser$QnxShortLogParser", 
                "feiw.LogParser$AndroidThreadtimeLogParser", 
                "feiw.LogParser$AndroidLogParser", 
                "feiw.LogParser$AndroidTimeLogParser",
        };
        if (logs == null || logs.isEmpty()) {
            return new QnxLogParser();
        }
        int maxscore = -1;
        Class<?> maxClass = null;
        try {
            for (String cn : parserClass) {
                Class<?> c  = Class.forName(cn);
                Method m = c.getMethod("taste", String.class);
                int score = 0;
                for (String log : logs) {
                    if ((Boolean) m.invoke(null, log)) {
                        score++;
                    }
                }
                if (score > maxscore) {
                    maxClass = c;
                    maxscore = score;
                }

            }
            if (maxClass != null) {
                return (LogParser)maxClass.newInstance();
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
            SystemConfigs scfgs = SystemConfigs.instance();
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

            Color bk = scfgs.getLogBackground(pri);
            if (bk != null) {
                item.setBackground(bk);
            }
            item.setForeground(scfgs.getLogForeground(pri));
            if (searchPat != null && searchPat.isContainedBy(msg) >= 0) {
                item.setImage(Resources.search_16);
                if (pri >= 4) {
                    item.setBackground(scfgs.getSearchMarkerBackground());
                }
            }
        }
    }
    public static final class AndroidTimeLogParser extends LogParser {
        public static boolean taste(final String log) {
            //01-17 20:28:35.379 D/Ethernet(  367): Interface eth0 link down
            if (log.length() > 22) {
                if (log.charAt(2) == '-' && log.charAt(5) == ' ' && log.charAt(8) == ':' && log.charAt(11) == ':' && log.charAt(14) == '.') {
                if (AndroidLogParser.mapLogPriority(log.charAt(19)) >= 0) {
                    if (log.charAt(20) == '/') {
                        int idx =  log.indexOf('(', 21);
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
       
        public static final int mWidth[] = { 28, 50, 150, 20, 190, 50, 1000 };
        static final String[] mTableHeader = { "Flag", "Line",  "Time",  "Prority", "Tag", "PID", "Message" };
        @Override
        public int [] getHeaderWidth() {
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
                if (idx >= 0) {
                	if (idx+2 < log.length()) {
                    return log.substring(idx + 2);
                	} else {
                		return log.substring(idx);
                	}
                }
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
                return AndroidLogParser.mapLogPriority(log.charAt(19));
            }
            return 7;
        }
        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            SystemConfigs scfgs = SystemConfigs.instance();
            if (taste(log)) {
                item.setText(2, log.substring(0, 19));
                char alogpri = log.charAt(19);
                item.setText(3, String.valueOf(alogpri));
                pri = AndroidLogParser.mapLogPriority(alogpri);
                int idx1 = log.indexOf('(', 21);
                if (idx1 >= 0) {
                    item.setText(4, log.substring(21, idx1));
                    int idx2 = log.indexOf(')', idx1 + 1);
                    if (idx2 >= 0) {
                        item.setText(5, log.substring(idx1+1, idx2).trim());
                        int idx = log.indexOf(':', idx2+1);
                        if (idx >= 0) {
                        	if (idx +2 < log.length()) {
                            msg = log.substring(idx + 2);
                        	} else {
                        		msg = log.substring(idx);
                        	}
                        }
                    }
                }
            }  
            item.setText(6, msg);

            Color bk = scfgs.getLogBackground(pri);
            if (bk != null) {
                item.setBackground(bk);
            }
            item.setForeground(scfgs.getLogForeground(pri));
            if (searchPat != null && searchPat.isContainedBy(msg) >= 0) {
                item.setImage(Resources.search_16);
                if (pri >= 4) {
                    item.setBackground(scfgs.getSearchMarkerBackground());
                }
            }
        }
    }
    public static final class AndroidLogParser extends LogParser {
        public static boolean taste(final String log) {
           //I/ActivityManager(   91): fsaf
            if (log.length() > 3) {
            char ch = log.charAt(0);
            if (AndroidLogParser.mapLogPriority(ch) >= 0) {
                if (log.charAt(1) == '/') {
                    int idx =  log.indexOf('(', 2);
                    if (idx >= 0) {
                        idx = log.indexOf(')', idx + 1);
                        if (idx >= 0) {
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
        public static final int mWidth[] = { 28, 50, 20, 190, 1000 };
        static final String[] mTableHeader = { "Flag", "Line",  "Prority", "Tag", "Message" };
        @Override
        public int [] getHeaderWidth() {
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
                if (idx >= 0) {
                    return log.substring(idx + 2);
                }
            }
            return log;
        }
        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return AndroidLogParser.mapLogPriority(log.charAt(0));
            }
            return 7;
        }
        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            SystemConfigs scfgs = SystemConfigs.instance();
            if (taste(log)) {
                item.setText(2, "");
                char alogpri = log.charAt(0);
                item.setText(2, String.valueOf(alogpri));
                pri = AndroidLogParser.mapLogPriority(alogpri);
                int idx1 = log.indexOf('(', 2);
                if (idx1 >= 0) {
                    item.setText(3, log.substring(2, idx1));
                    int idx = log.indexOf(':', idx1+1);
                    if (idx >= 0) {
                        msg = log.substring(idx + 2);
                    }
                }
            }  
            item.setText(4, msg);

            Color bk = scfgs.getLogBackground(pri);
            if (bk != null) {
                item.setBackground(bk);
            }
            item.setForeground(scfgs.getLogForeground(pri));
            if (searchPat != null && searchPat.isContainedBy(msg) >= 0) {
                item.setImage(Resources.search_16);
                if (pri >= 4) {
                    item.setBackground(scfgs.getSearchMarkerBackground());
                }
            }
        }
    }
    public static final class AndroidThreadtimeLogParser extends LogParser{
        public static boolean taste(final String log) {
            //01-17 20:28:35.379   367   424 D Ethernet: Interface eth0 link down
            //01-17 21:52:50.409  1330  1330 V GCMBroadcastReceiver: GCM IntentService class: com.espn.notifications.EspnGcmIntentService

            if (log.length() > 31) {
	            if (log.charAt(2) == '-' && log.charAt(5) ==' ' && log.charAt(8) == ':' && log.charAt(11) == ':' ) {
	                if (log.charAt(14) == '.' && log.charAt(18) == ' ') {
	                    char ch = log.charAt(31);
	                    if (AndroidLogParser.mapLogPriority(ch) >= 0) {
	                        return true;
	                    }
	                }
	            }
            }
            return false;
        }
        @Override
        public int parsePriority(final String log) {
            if (taste(log)) {
                return AndroidLogParser.mapLogPriority(log.charAt(31));
            }
            return 7;
        }
 
        public static final int mWidth[] =   { 28,      50,      155,   50,     25,        110, 1200 };
        static final String[] mTableHeader = { "Flag", "Line", "Time", "PID", "Prority", "Tag", "Message" };
        @Override
        public int [] getHeaderWidth() {
            return mWidth;
        }
       
        @Override
        public String[] getTableHeader() {
            return mTableHeader;
        }
        @Override
        public String parseTag(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(':', 32);
                if (idx >= 0) {
                    if (idx +2 < log.length())
                    return log.substring(32, idx).trim(); 
                    else 
                        return "";
              }
            }
            return null;
        }
        public int parsePID(final String log) {
           if (taste(log)) {
        	   return Integer.parseInt(log.substring(19, 25));
           }
           return 0;
        }
        @Override
        public String parseMessage(final String log) {
            if (taste(log)) {
                int idx = log.indexOf(':', 32);
                if (idx >= 0  && idx +2 < log.length()) {
                    return log.substring(idx + 2);
                }
            }
            return log;
        }

        @Override
        public void updateTableItem(final String log, final TableItem item, StringPattern searchPat) {
            int pri = 7;
            String msg = log;
            SystemConfigs scfgs = SystemConfigs.instance();
            if (taste(log)) {
                item.setText(2, log.substring(0, 18));
                 String pid = log.substring(19, 24);
               // String tid = log.substring(25, 30);
                char alogpri = log.charAt(31);
                item.setText(3, pid.trim());
                item.setText(4, String.valueOf(alogpri));
                pri = AndroidLogParser.mapLogPriority(alogpri);
                int idx = log.indexOf(':', 32);
                if (idx >= 0) {
                   if (idx + 2 < log.length())
                    msg = log.substring(idx + 2);
                   else 
                        msg = "";
                    item.setText(5, log.substring(33, idx));
                }
            }  
            item.setText(6, msg);

            Color bk = scfgs.getLogBackground(pri);
            if (bk != null) {
                item.setBackground(bk);
            }
            item.setForeground(scfgs.getLogForeground(pri));
            if (searchPat != null && searchPat.isContainedBy(msg) >= 0) {
                item.setImage(Resources.search_16);
                if (pri >= 4) {
                    item.setBackground(scfgs.getSearchMarkerBackground());
                }
            }
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

        static final String[] mTableHeader = { "Flag", "Line", "Time", "Prority", "Message" };

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
            SystemConfigs scfgs = SystemConfigs.instance();
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

            Color bk = scfgs.getLogBackground(pri);
            if (bk != null) {
                item.setBackground(bk);
            }
            item.setForeground(scfgs.getLogForeground(pri));
            if (searchPat != null && searchPat.isContainedBy(msg) >= 0) {
                item.setImage(Resources.search_16);
                if (pri >= 4) {
                    item.setBackground(scfgs.getSearchMarkerBackground());
                }
            }
        }
    }
}