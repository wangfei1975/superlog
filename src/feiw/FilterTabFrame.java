package feiw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;

public class FilterTabFrame extends SlogTabFrame {

    public FilterTabFrame(CTabFolder parent, String txt, int style, LogSource logsrc,
            LogFilter logFilter) {
        super(parent, txt, style, logsrc, logFilter);
        setImage(Resources.filter_32);
    }
    
    void updateToolItem(ToolItem tit) {
        
        String tn = (String)tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        } 
        if (mLogSrc instanceof FileLogSource) {
            if (tn.equals(ToolBarDes.TN_DISCONNECT)) {
                tit.setEnabled(false);  
            }  else if (tn.equals(ToolBarDes.TN_PAUSE)) {
                tit.setEnabled(false);
            } else if (tn.equals(ToolBarDes.TN_CLEAR)) {
                tit.setEnabled(false);
            } else {
                tit.setEnabled(true);
            }
        } else {
            tit.setEnabled(true);
        }
    }
}
