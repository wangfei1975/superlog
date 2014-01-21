package feiw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogView;

public class FilterTabFrame extends SlogTabFrame {

    public FilterTabFrame(CTabFolder parent, String txt, int style, LogSource logsrc,
            LogFilter logFilter, LogParser logParser, LogView parentView) {
        super(parent, txt, style, logsrc, logFilter, logParser, parentView);
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
            } else {
                super.updateToolItem(tit);
            }
        } else {
            if (tn.equals(ToolBarDes.TN_DISCONNECT)) {
                tit.setEnabled(mLogSrc.getStatus() == LogSource.stConnected);  
            }  else if (tn.equals(ToolBarDes.TN_PAUSE)) {
                if (mLogSrc.getStatus() == LogSource.stConnected) {
                    tit.setToolTipText(mLogView.isPaused() ? "Resume" : "Pause");
                    tit.setImage(mLogView.isPaused() ?  Resources.go_32: Resources.pause_32);
                    } else {
                        tit.setEnabled(false);
                    }
            } else {
                super.updateToolItem(tit);
            }
        }
    }
}
