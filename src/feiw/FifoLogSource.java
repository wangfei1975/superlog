package feiw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
                public void run() {
                    try {
                    	setStatus(stConnected);
                        final FileInputStream is = new FileInputStream(mFifoUrl);
                        while(getStatus() == stConnected) {
	                      //  long start_time = System.currentTimeMillis();
	                        fetchLogs(is);
	                        notifyViews();
	                        //System.out.println("loading time = " + (System.currentTimeMillis() - start_time));
                        }
                  
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
    }
    public void disconnect() {
    	  setStatus(stIdle);
    }
}
