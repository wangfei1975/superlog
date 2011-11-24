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
    }
    final static int MAX_NOTIFY_COUNT = 100;
    int remotepid = 0;
    String serverIp;
    int serverPort;
    LogListener listener = null;
    Vector<String[]> data = new Vector<String[]>(1000, 1000);

    
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
       
       public int curresult;
    }
    private SearchCtx searchCtx = new SearchCtx();
 
    private int search_p(String str, int start, int dir, int flag){
        String [] item;
        for (int i = start; i < getDataSize(); i+= dir)
        {
            item = getData(i);
            if (item != null) {
                if (item[4] != null && !item[4].isEmpty()) {
                    if ((flag & SearchCtx.FLAG_CASE_SENSITIVE) != 0)
                    {
                        if(item[4].toLowerCase().contains(str.toLowerCase()))
                            return i;
                    }
                    else {
                    if (item[4].contains(str))
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public int searchNext() {
        if (searchCtx.curresult < 0)
            searchCtx.curresult = search_p(searchCtx.str, searchCtx.start, searchCtx.dir, searchCtx.flag);
            else
                searchCtx.curresult = search_p(searchCtx.str, searchCtx.curresult, searchCtx.dir, searchCtx.flag);    
        return searchCtx.curresult;
    }
    public int search(String str, int startline, int dir, int flag) {
        if (str == null || str.isEmpty())
            return -1;
        searchCtx.str = str;
        int maxline = getDataSize();
        if (startline < 0)
            searchCtx.start = 0;
        else if (startline > maxline-1)
            searchCtx.start = maxline-1;
        else 
            searchCtx.start = startline;
        if (dir > 0) searchCtx.dir = 1;
        else searchCtx.dir = -1;
        
        searchCtx.flag = flag;
        searchCtx.curresult = -1;
        
        return searchNext();
    }
    public void clearLogs() {
        data.clear();
        if (listener != null)
            listener.handleLogs();
    }
    public synchronized int getDataSize() {
        return data.size();
    }
    
    public synchronized String[]  getData(int idx) {
        if (idx < 0 || idx >= data.size())
            return null;
        return data.get(idx);
    }

    public void disconnect() {
        if (remotepid == 0)
            return;
        
        Socket sock;
        try {
            sock = new Socket(serverIp, serverPort);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            BufferedReader din = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            String str = din.readLine();
            System.out.print(str + "\n");
            out.writeBytes("service launcher\r\n");
            System.out.print(din.readLine() + "\n");

         //   String killcmd = "start /bin/kill kill " + remotepid + "\r\n";
          //  out.writeBytes(killcmd);
  
        //    System.out.print(din.readLine() + "\n");
            
            out.writeBytes("start slay -f sloginfo\r\n");
            System.out.print(din.readLine() + "\n");
            remotepid = 0;

            sock.close();

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
            //din.readLine();
            new Thread() {
                public void run() {
                      Socket sock;
                    try {
                        sock = new Socket(serverIp, serverPort);
                       
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

                         din = new BufferedReader(new InputStreamReader(
                                sock.getInputStream()));
                 
                        str = din.readLine();
                        System.out.print(str + "\n");
                        out.writeBytes("service launcher\r\n");
                        System.out.print(din.readLine() + "\n");
                        out.writeBytes("start /bin/sloginfo sloginfo -c -w\r\n");
                        String ret = din.readLine();
                        if (ret.startsWith("OK")) {
                            System.out.print("launch success, pid is:" + ret.substring(3) + "\n");
                            remotepid = Integer.parseInt(ret.substring(3));

                        } else {
                            remotepid = 0;
                        }
                        
                    int newlines = 0;
                    din.readLine();
                        str = din.readLine();
                        while (str != null) {
                            if (!str.isEmpty()) {
                                String[] log = parseLogLine(str);
                                synchronized (this) { 
                                data.add(log);
                                }
                                newlines++;
                                if (sock.getInputStream().available() == 0 || newlines > MAX_NOTIFY_COUNT) {
                                    if (o != null) {
                                        o.handleLogs();
                                    }
                                    newlines = 0;
                                }
                            }
                            str = din.readLine();
                        }
                        sock.close();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                
            }
        }.start();
    }
}
