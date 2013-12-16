package qnx;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public final class SlogLogSource extends LogSource {

    private String mServerIp;
    private int mServerPort;
    private Socket mSock;
    private int mRemotepid = 0;
 
    public SlogLogSource(final String ip, final int port) {
        super();
        mServerIp = ip;
        mServerPort = port;
        new Thread() {
            public void run() {
                try {
                    Socket sock = new Socket(ip, port);
                    mSock = sock;

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
                    sock = new Socket(ip, port);

                    out = new DataOutputStream(sock.getOutputStream());
                    din = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    str = din.readLine();
                    System.out.print(str + "\n");
                    out.writeBytes("service launcher\r\n");
                    System.out.print(din.readLine() + "\n");
                    out.writeBytes("start sloginfo sloginfo -t -c -w\r\n");
                    String ret = din.readLine();
                    if (ret.startsWith("OK")) {
                        System.out.print("launch success, pid is:" + ret.substring(3) + "\n");
                        mRemotepid = Integer.parseInt(ret.substring(3));

                    } else {
                        out.writeBytes("service launcher\r\n");
                        System.out.print(din.readLine() + "\n");
                        out.writeBytes("start /bin/sloginfo sloginfo -c -w\r\n");
                        ret = din.readLine();
                        if (ret.startsWith("OK")) {
                            System.out.print("launch success, pid is:" + ret.substring(3) + "\n");
                            mRemotepid = Integer.parseInt(ret.substring(3));
                        } else {
                            mRemotepid = 0;
                        }
                    }

                    int newlines = 0;
                    din.readLine();
                    str = din.readLine();
                    while (str != null) {
                        if (!str.isEmpty()) {
                            LogItem it = new LogItem(str);
                            if (sock.getInputStream().available() == 0
                                    || newlines > SystemConfigs.MIN_NOTIFY_COUNT) {
                                addLogItem(it, true);
                                newlines = 0;
                            } else {
                                addLogItem(it, false);
                                newlines++;
                            }
                        }
                        str = din.readLine();
                    }
                    sock.close();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                mSock = null;
            }
        }.start();
    }

    public void disconnect() {
        if (mRemotepid == 0)
            return;

        try {
            mSock.close();
            mSock = new Socket(mServerIp, mServerPort);
            DataOutputStream out = new DataOutputStream(mSock.getOutputStream());

            BufferedReader din = new BufferedReader(new InputStreamReader(mSock.getInputStream()));

            String str = din.readLine();
            System.out.print(str + "\n");
            out.writeBytes("service launcher\r\n");
            System.out.print(din.readLine() + "\n");

            // String killcmd = "start /bin/kill kill " + remotepid + "\r\n";
            // out.writeBytes(killcmd);

            // System.out.print(din.readLine() + "\n");
            String killcmd = "start slay -f sloginfo " + mRemotepid + "\r\n";
            System.out.print(killcmd);
            out.writeBytes(killcmd);
            System.out.print(din.readLine() + "\n");
            mRemotepid = 0;

            mSock.close();
            // setConnectStatus(false);
            // // if (listener != null) {
            // listener.handleStatusChanged(false);
            // }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
