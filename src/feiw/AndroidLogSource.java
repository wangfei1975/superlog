package feiw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndroidLogSource extends LogSource {

    Process mAdbProcess;
    
    static boolean checkAdb(String adbpath) {
        try {
            Process p = Runtime.getRuntime().exec(adbpath + " version");
            final BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = rd.readLine();
            return (s.contains("Android Debug Bridge version"));
        } catch (IOException e) {
        }
        return false;
    }
    public AndroidLogSource() throws DeviceNotConnected {
        super();
        setStatus(stConnecting);

            try {
                  List <String> adbcmd =   Arrays.asList(SystemConfigs.instance().getAdbPath() + "adb", "logcat", "-vthreadtime");
                   
                ProcessBuilder pb = new ProcessBuilder();
                pb.redirectErrorStream(true);
                pb.command(adbcmd);
                pb.directory(new File(SystemConfigs.instance().getAdbPath()));
                mAdbProcess = pb.start();
                BufferedReader rd = new BufferedReader(new InputStreamReader(mAdbProcess.getInputStream()));

                String s = rd.readLine();
                if (s.contains("waiting for device")) {
                    throw new DeviceNotConnected("No Android connected");
                }
                addLogItem(s, false);
                setStatus(stConnected);
                new Thread() {
                    public void run() {
                        try {
                             fetchLogs(mAdbProcess.getInputStream());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            setStatus(stIdle);
                        }
                    }
                }.start();
          
            } catch (IOException e1) {
                throw new DeviceNotConnected("No Android connected");
            }
 

    }
    
    public void disconnect() {
        if (mAdbProcess != null) {
            mAdbProcess.destroy();
            mAdbProcess = null;
            setStatus(stIdle);
        }
 
    }
}
