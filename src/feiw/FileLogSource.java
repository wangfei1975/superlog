package feiw;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class FileLogSource extends LogSource {
    String mFileUrl = null;
    FileInputStream mFileIs = null;
    public FileLogSource(final String fname) {
        super();
        mFileUrl = fname;
        try {
            mFileIs = new FileInputStream(fname);
            new Thread() {
                public void run() {
                    try {
                        setStatus(stConnected);
                        fetchLogs(mFileIs);
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
