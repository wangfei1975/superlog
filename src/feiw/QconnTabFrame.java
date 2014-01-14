package feiw;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;
import feiw.LogSource.StatusListener;

public class QconnTabFrame extends SlogTabFrame implements  StatusListener {

    public QconnTabFrame(CTabFolder parent, String txt, int style, String ip, int port) {
        super(parent, txt, style, new QconnLogSource(ip, port), null);
        setImage(Resources.connected_32);
        mLogSrc.addStatusListener(this);
    }

    void updateToolItem(ToolItem tit) {
        tit.setEnabled(true);
        super.updateToolItem(tit);
    }
    public void onClose() {
        mLogSrc.removeStatusListener(this);
        super.onClose();
    }
    @Override
    public void onStatusChanged(int oldStatus, int newStatus) {
        if (getDisplay().isDisposed() || isDisposed()) {
            return;
        }
        final Image img ;
        switch(newStatus) {
        case LogSource.stIdle:
            img = Resources.disconnected_32;
            break;
        case LogSource.stConnecting:
            img = Resources.disconnected_32;
            break;
        case LogSource.stConnected:
            img = Resources.connected_32;
            break;
         default:
             img = Resources.connected_32;
                break;
        }
       
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed()) {
                    setImage(img);
               //     mStatusLabel.setImage(img);
              //      mStatusLabel.update();
                }
            }
           }
       );
       
    }
    

    public void onPause() {
        if (!isDisposed()) {
            setImage(Resources.paused_32);
        }
    }
    
    public void onDisconnect() {
        if (!isDisposed()) {
            setImage(Resources.disconnected_32);
        }
    }
}
