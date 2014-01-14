package feiw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public final class QconnLogSource extends LogSource {

    private String mServerIp;
    private int mServerPort;
    private Socket mSock;
    private int mRemotepid = 0;
 
    public QconnLogSource(final String ip, final int port) {
        super();
        setStatus(stConnecting);
        mServerIp = ip;
        mServerPort = port;
        new Thread() {
            public void run() {
                try {
                    Socket sock = new Socket(ip, port);
                    mSock = sock;
    
                    sock.setKeepAlive(true);
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
                  //  sock.setKeepAlive(true);
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
                            //skip sloginfo header
                        } else {
                            mRemotepid = 0;
                        }
                    }

                    if (mRemotepid == 0) {
                        setStatus(stIdle);
                        sock.close();
                        mSock = null;
                        return;
                    }
                    
                    mSock = sock;
                    ret = din.readLine();
                    setStatus(stConnected);
                    fetchLogs(sock.getInputStream());
                    sock.close();
                    setStatus(stIdle);
                } catch (IOException e) {
//                    e.printStackTrace();
                    setStatus(stIdle);
                }
                mSock = null;
            }
        }.start();
    }

    public void disconnect() {
        if (mRemotepid == 0 || mSock == null)
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
            String killcmd = "start slay sloginfo " + mRemotepid + "\r\n";
            System.out.print(killcmd);
            out.writeBytes(killcmd);
            System.out.print(din.readLine() + "\n");


            mSock.close();
            setStatus(stIdle);

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
        mRemotepid = 0;
        mSock = null;

    }
}
