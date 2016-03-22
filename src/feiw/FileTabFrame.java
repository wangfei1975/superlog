package feiw;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolItem;

public class FileTabFrame extends SlogTabFrame {

    public FileTabFrame(CTabFolder parent, String txt, int style, String fname) throws FileNotFoundException {
        super(parent, txt, style, new FileLogSource(fname), null, LogParser.newLogParser(new FileInputStream(fname)), null);
        setImage(Resources.openfile_32);
        ((FileLogSource)mLogSrc).load(mLogView.getLogParser());

    }
    
    @Override
    void updateToolItem(ToolItem tit) {
        String tn = (String)tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        }
        if (tn.equals(ToolBarDes.TN_DISCONNECT)) {
            tit.setEnabled(false);  
        }  else if (tn.equals(ToolBarDes.TN_PAUSE)) {
            tit.setEnabled(false);
        } else if (tn.equals(ToolBarDes.TN_CLEAR)) {
            tit.setEnabled(false);
        } else {
            tit.setEnabled(true);
        }
        super.updateToolItem(tit);
     }
 
}
