package feiw;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileLogSource extends LogSource {
    String mFileUrl = null;

    public FileLogSource(final String fname) {
        super();
        mFileUrl = fname;
    }
    
  public void load(final LogParser parser) {
      try {
          final FileInputStream is = new FileInputStream(mFileUrl);
          new Thread() {
              @Override
            public void run() {
                  try {
                      setStatus(stConnected);
                      long start_time = System.currentTimeMillis();
                      fetchLogs(is, parser);
                      notifyViews();
                      System.out.println("loading time = " + (System.currentTimeMillis() - start_time));

                  } catch (IOException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
              }
          }.start();
      } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
  }
}
