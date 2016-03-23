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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FifoLogSource extends LogSource {

    private String mFifoUrl;

    public FifoLogSource(final String fifo) throws DeviceNotConnected {
        super();
        mRollLines = SystemConfigs.instance().getLogRollingLines();

        if (!new File(fifo).exists()) {
            try {
                Runtime.getRuntime().exec("mkfifo " + fifo);
            } catch (IOException e) {
                throw new DeviceNotConnected("could not mkfifo " + fifo);
            }
        }
        mFifoUrl = fifo;
        setStatus(stConnected);
        load();
    }

    public void load() {
        new Thread() {
            @Override
            public void run() {
                try {
                    setStatus(stConnected);
                    final FileInputStream is = new FileInputStream(mFifoUrl);
                    while (getStatus() == stConnected) {
                        // long start_time = System.currentTimeMillis();
                        if (is.available() > 0 && fetchLogs(is) > 0) {
                            notifyViews();
                        } else {
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        // System.out.println("loading time = " +
                        // (System.currentTimeMillis() - start_time));
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void disconnect() {
        setStatus(stIdle);
        File f = new File(mFifoUrl);
        if (f.exists()) {
            f.delete();
        }
    }
}
