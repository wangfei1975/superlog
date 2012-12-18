package qnx;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public final class SlogInfo {
    public interface LogListener {
        public void handleLogs();

        public void handleSearchResult();

        public void handleStatusChanged(final boolean connected);
    }

    final static int MAX_NOTIFY_COUNT = 100;
    int remotepid = 0;
    String serverIp = "10.222.96.245";
    int serverPort = 8000;
    LogListener listener = null;

    boolean connected = false;

    boolean paused = false;

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnectStatus(boolean con) {
        connected = con;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void pause() {
        paused = !paused;
        if (!paused) {
            listener.handleLogs();
        }
    }

    public class LogItem {
        public String[] texts;
        public int searchMarker;

        public String getText(int i) {
            if (texts != null && i >= 0 && i < texts.length) {
                return texts[i];
            }
            return null;
        }

        public LogItem() {
            texts = null;
            searchMarker = 0;
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
    }

    Vector<LogItem> data = new Vector<LogItem>(1000, 1000);

    public int changedflag = 0;

    public synchronized boolean isDataChanged() {
        return changedflag != 0;
    }

    public synchronized void resetChangeFlag() {
        changedflag = 0;
    }

    public synchronized void setChanged() {
        changedflag = 1;
    }

    String[] parseLogLine(String str) {

        String[] seperator = { "    ", " ", " ", " " };
        String[] ret = new String[5];
        int idx = 0, nextidx;
        for (int i = 0; i < 4; i++) {
            nextidx = str.indexOf(seperator[i], idx);
            if (nextidx <= 0) {
                ret[0] = ret[1] = ret[2] = ret[3] = null;
                ret[4] = str;
                return ret;
            }
            ret[i] = str.substring(idx, nextidx);
            idx = nextidx + seperator[i].length();
            while (str.charAt(idx) == ' ')
                idx++;
        }

        ret[4] = str.substring(idx);
        return ret;
    }

    public class SearchCtx {
        public static final int FLAG_CASE_SENSITIVE = 1;
        public String str;
        public int start;
        public int dir;
        public int flag;

        public int curresult = -1;
        public int resultcount = 0;
    }

    private SearchCtx searchCtx = new SearchCtx();

    public final SearchCtx getSearchCtx() {
        return searchCtx;
    }

    private int search_p(String str, int start, int dir, int flag) {
        LogItem item;
        for (int i = start; i < getDataSize(); i += dir) {
            item = getData(i);
            if (item != null) {
                item.searchMarker = 0;
                if (item.getText(4) != null && !item.getText(4).isEmpty()) {
                    if ((flag & SearchCtx.FLAG_CASE_SENSITIVE) == 0) {
                        if (item.getText(4).toLowerCase().contains(str.toLowerCase()))
                            return i;
                    } else {
                        if (item.getText(4).contains(str))
                            return i;
                    }
                }
            }
        }
        return -1;
    }

    public int searchPrev() {
        int dsize = getDataSize();
        int start = dsize - 1;
        if (searchCtx.curresult >= 0) {
            start = searchCtx.curresult - 1;
        }
        while (start >= 0) {
            if (getData(start).searchMarker != 0) {
                searchCtx.curresult = start;
                return start;
            }
            start--;
        }
        return -1;
    }

    public int searchNext() {
        int dsize = getDataSize();
        int start = 0;
        if (searchCtx.curresult >= 0) {
            start = searchCtx.curresult + 1;
        }
        while (start < dsize) {
            if (getData(start).searchMarker != 0) {
                searchCtx.curresult = start;
                return start;
            }
            start++;
        }
        return -1;
    }

    public int searchMarkall(String str, int flag) {

        int cnt = 0;
        int r = search_p(str, 0, 1, flag);
        searchCtx.curresult = -1;
        searchCtx.resultcount = 0;
        while (r >= 0) {
            if (searchCtx.curresult < 0) {
                searchCtx.curresult = r;
            }
            LogItem l = getData(r);
            l.searchMarker = 1;
            searchCtx.resultcount++;
            setChanged();
            cnt++;
            r = search_p(str, r + 1, 1, flag);
        }
        if (listener != null && isDataChanged())
            listener.handleSearchResult();
        return cnt;

    }

    public int search(String str, int startline, int dir, int flag) {
        if (str == null || str.isEmpty())
            return -1;
        searchCtx.str = str;
        int maxline = getDataSize();
        if (startline < 0)
            searchCtx.start = 0;
        else if (startline > maxline - 1)
            searchCtx.start = maxline - 1;
        else
            searchCtx.start = startline;
        if (dir > 0)
            searchCtx.dir = 1;
        else
            searchCtx.dir = -1;

        searchCtx.flag = flag;
        searchCtx.curresult = -1;

        return searchNext();
    }

    public void clearLogs() {
        data.clear();
        this.setChanged();
        if (listener != null)
            listener.handleLogs();
    }

    public synchronized int getDataSize() {
        return data.size();
    }

    public synchronized LogItem getData(int idx) {
        if (idx < 0 || idx >= data.size())
            return null;
        return data.get(idx);
    }

    Socket sock;

    public void disconnect() {
        if (remotepid == 0)
            return;

        try {
            sock.close();
            sock = new Socket(serverIp, serverPort);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            BufferedReader din = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            String str = din.readLine();
            System.out.print(str + "\n");
            out.writeBytes("service launcher\r\n");
            System.out.print(din.readLine() + "\n");

            // String killcmd = "start /bin/kill kill " + remotepid + "\r\n";
            // out.writeBytes(killcmd);

            // System.out.print(din.readLine() + "\n");

            out.writeBytes("start slay -f sloginfo\r\n");
            System.out.print(din.readLine() + "\n");
            remotepid = 0;

            sock.close();
            setConnectStatus(false);
            if (listener != null) {
                listener.handleStatusChanged(false);
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void connect(String ip, int port, final LogListener o) {

        serverIp = ip;
        serverPort = port;
        listener = o;

        // ignore the first line
        // din.readLine();
        new Thread() {
            public void run() {

                try {
                    sock = new Socket(serverIp, serverPort);
                    setConnectStatus(true);
                    if (o != null) {
                        o.handleStatusChanged(true);
                    }
                    DataOutputStream out = new DataOutputStream(sock.getOutputStream());

                    BufferedReader din = new BufferedReader(new InputStreamReader(
                            sock.getInputStream()));

                    String str = din.readLine();
                    System.out.print(str + "\n");
                    out.writeBytes("service launcher\r\n");
                    System.out.print(din.readLine() + "\n");
                    out.writeBytes("start slay -f sloginfo\r\n");
                    System.out.print(din.readLine() + "\n");
                    sock.close();

                    sock = new Socket(serverIp, serverPort);

                    out = new DataOutputStream(sock.getOutputStream());

                    din = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                    str = din.readLine();
                    System.out.print(str + "\n");
                    out.writeBytes("service launcher\r\n");
                    System.out.print(din.readLine() + "\n");
                    out.writeBytes("start sloginfo sloginfo -c -w\r\n");
                    String ret = din.readLine();
                    if (ret.startsWith("OK")) {
                        System.out.print("launch success, pid is:" + ret.substring(3) + "\n");
                        remotepid = Integer.parseInt(ret.substring(3));

                    } else {
                        out.writeBytes("service launcher\r\n");
                        System.out.print(din.readLine() + "\n");
                        out.writeBytes("start /bin/sloginfo sloginfo -c -w\r\n");
                        ret = din.readLine();
                        if (ret.startsWith("OK")) {
                            System.out.print("launch success, pid is:" + ret.substring(3) + "\n");
                            remotepid = Integer.parseInt(ret.substring(3));
                        } else {
                            remotepid = 0;
                        }
                    }

                    int newlines = 0;
                    din.readLine();
                    str = din.readLine();
                    while (str != null) {
                        if (!str.isEmpty()) {
                            String[] logtxt = parseLogLine(str);
                            LogItem log = new LogItem(logtxt);
                            synchronized (this) {
                                data.add(log);
                                setChanged();
                            }
                            newlines++;
                            if (sock.getInputStream().available() == 0
                                    || newlines > MAX_NOTIFY_COUNT) {
                                if (o != null && !isPaused()) {
                                    o.handleLogs();
                                }
                                newlines = 0;
                            }
                        }
                        str = din.readLine();
                    }
                    sock.close();
                    setConnectStatus(false);
                    if (o != null) {
                        o.handleStatusChanged(false);
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                    setConnectStatus(false);
                    if (o != null) {
                        o.handleStatusChanged(false);
                    }
                }

            }
        }.start();
    }
}
