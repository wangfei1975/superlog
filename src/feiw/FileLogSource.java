package feiw;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
                      setStatus(stConnected);
                      mNotifyTimeSpan = 500;
                      fetchLogs(is);
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
