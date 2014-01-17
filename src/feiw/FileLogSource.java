package feiw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class FileLogSource extends LogSource {
    String mFileUrl = null;

    public FileLogSource(final String fname) {
        super();
        mFileUrl = fname;
    }
    
  public void load() {
      try {
          final FileInputStream is = new FileInputStream(mFileUrl);
          new Thread() {
              public void run() {
                  try {
                      mNotifyTimeSpan = 1000;
                      setStatus(stConnected);
                      long start_time = System.currentTimeMillis();
                      fetchLogs(is);
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
