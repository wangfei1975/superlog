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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AndroidLogSource extends LogSource {

    Process mAdbProcess;

    public static boolean checkAdb(String adbpath) {
        try {
            Process p = Runtime.getRuntime().exec(adbpath + " version");
            final BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = rd.readLine();
            return (s!= null && s.contains("Android Debug Bridge version"));
        } catch (IOException e) {
        }
        return false;
    }

    public static String [] enumDevices() {
        try {
            Process p = Runtime.getRuntime().exec(SystemConfigs.instance().getAdbPath() + "adb devices");
            final BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = rd.readLine();
            ArrayList<String> devs = new ArrayList<String>();
            if (s != null && s.contains("List of devices attached")) {
                s = rd.readLine();
                while(s != null) {
                    int idx = s.indexOf("device");
                    if (idx > 0) {
                       devs.add(s.substring(0, idx -1).trim());
                    }
                    s = rd.readLine();
                }
                return devs.size() > 0 ? devs.toArray(new String[devs.size()]) : null;
            }
            p.destroy();
        } catch (IOException e) {
        }
        return null;
    }

    public AndroidLogSource(String device) throws DeviceNotConnected {
        super();
        mRollLines = SystemConfigs.instance().getLogRollingLines();
        setStatus(stConnecting);
        try {
            List<String> adbcmd = new ArrayList<String>();
            adbcmd.add(SystemConfigs.instance().getAdbPath() + "adb");
            
            if (device != null && !device.isEmpty()) {
                adbcmd.add("-s");
                adbcmd.add(device);
            }
            adbcmd.add("logcat");
            adbcmd.add("-vthreadtime");

            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);
            pb.command(adbcmd);
            pb.directory(new File(SystemConfigs.instance().getAdbPath()));
            mAdbProcess = pb.start();
            BufferedReader rd = new BufferedReader(new InputStreamReader(mAdbProcess.getInputStream()));

            String s = rd.readLine();
            if (s.contains("waiting for device")) {
                throw new DeviceNotConnected("No Android Device connected");
            }
            addLogItem(s, false);
            setStatus(stConnected);
            new Thread() {
                @Override
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
            throw new DeviceNotConnected("No Android Device connected");
        }

    }

    @Override
    public void disconnect() {
        if (mAdbProcess != null) {
            mAdbProcess.destroy();
            mAdbProcess = null;
            setStatus(stIdle);
        }

    }
}
