package feiw;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;

public class FileTabFrame extends SlogTabFrame {

    public FileTabFrame(CTabFolder parent, String txt, int style, String fname) {
        super(parent, txt, style, new FileLogSource(fname), null);
        setImage(Resources.openfile_32);
    }
    
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
