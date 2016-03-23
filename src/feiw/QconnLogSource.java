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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public final class QconnLogSource extends LogSource {

    private String mServerIp;
    private int mServerPort;
    private Socket mSock;
    private int mRemotepid = 0;

    public QconnLogSource(final String ip, final int port) throws DeviceNotConnected {
        super();
        mRollLines = SystemConfigs.instance().getLogRollingLines();
        mServerIp = ip;
        mServerPort = port;
        setStatus(stConnecting);

        Socket sock = new Socket();
        try {
            sock.connect(new InetSocketAddress(ip, port), 1000);
            sock.setKeepAlive(true);
        } catch (IOException e1) {
            throw new DeviceNotConnected("could connect to qconn://" + ip + ":" + port);
        }
        mSock = sock;

        new Thread() {
            @Override
            public void run() {
                try {
                    setStatus(stConnecting);

                    DataOutputStream out = new DataOutputStream(mSock.getOutputStream());

                    BufferedReader din = new BufferedReader(new InputStreamReader(mSock.getInputStream()));

                    din.readLine();
                    // System.out.print(str + "\n");
                    // out.writeBytes("service launcher\r\n");
                    // System.out.print(din.readLine() + "\n");
                    // out.writeBytes("start slay -f sloginfo\r\n");
                    // System.out.println(din.readLine());
                    // mSock.close();
                    // mSock = new Socket(ip, port);

                    // out = new DataOutputStream(mSock.getOutputStream());
                    // din = new BufferedReader(new
                    // InputStreamReader(mSock.getInputStream()));

                    // sock.setKeepAlive(true);
                    // str = din.readLine();
                    // System.out.println(str);
                    out.writeBytes("service launcher\r\n");
                    System.out.println(din.readLine());
                    out.writeBytes("start sloginfo sloginfo -t -c -w\r\n");
                    String ret = din.readLine();
                    System.out.println(ret);
                    if (ret.startsWith("OK")) {
                        System.out.print("launch success, pid is:" + ret.substring(3) + "\n");
                        mRemotepid = Integer.parseInt(ret.substring(3));

                    } else {
                        out.writeBytes("service launcher\r\n");
                        // System.out.print(din.readLine() + "\n");
                        out.writeBytes("start /bin/sloginfo sloginfo -t -c -w\r\n");
                        ret = din.readLine();
                        if (ret.startsWith("OK")) {
                            // System.out.print("launch success, pid is:" +
                            // ret.substring(3) + "\n");
                            mRemotepid = Integer.parseInt(ret.substring(3));
                            // skip sloginfo header
                        } else {
                            mRemotepid = 0;
                        }
                    }

                    if (mRemotepid == 0) {
                        setStatus(stIdle);
                        mSock.close();
                        mSock = null;
                        return;
                    }

                    // mSock = mSock;
                    ret = din.readLine();
                    setStatus(stConnected);
                    fetchLogs(mSock.getInputStream());
                    mSock.close();
                    setStatus(stIdle);
                } catch (IOException e) {
                    // e.printStackTrace();
                    setStatus(stIdle);
                }
                mSock = null;
            }
        }.start();
    }

    @Override
    public void disconnect() {
        if (mRemotepid == 0 || mSock == null)
            return;

        try {
            mSock.close();
            Socket sock = new Socket();
            try {
                sock.connect(new InetSocketAddress(mServerIp, mServerPort), 1000);
                sock.setKeepAlive(true);
            } catch (IOException e1) {
                return;
            }
            mSock = sock;
            DataOutputStream out = new DataOutputStream(mSock.getOutputStream());

            BufferedReader din = new BufferedReader(new InputStreamReader(mSock.getInputStream()));

            String str = din.readLine();
            System.out.print(str + "\n");
            out.writeBytes("service launcher\r\n");
            System.out.print(din.readLine() + "\n");

            // String killcmd = "start /bin/kill kill " + remotepid + "\r\n";
            // out.writeBytes(killcmd);

            // System.out.print(din.readLine() + "\n");
            String killcmd = "start slay slay -f " + mRemotepid + "\r\n";
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
