package feiw;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.StatusListener;

public class QconnTabFrame extends SlogTabFrame implements  StatusListener {

    public QconnTabFrame(CTabFolder parent, String txt, int style, String ip, int port) throws DeviceNotConnected {
        super(parent, txt, style, new QconnLogSource(ip, port), null, new LogParser.QnxLogParser(), null);
        setImage(Resources.disconnected_32);
        mLogSrc.addStatusListener(this);
    }

    void updateToolItem(ToolItem tit) {
        tit.setEnabled(true);
        if (tit.getData().equals(ToolBarDes.TN_PAUSE)) {
           if (mLogSrc.getStatus() == LogSource.stConnected) {
           tit.setToolTipText(mLogView.isPaused() ? "Resume" : "Pause");
           tit.setImage(mLogView.isPaused() ?  Resources.go_32: Resources.pause_32);
           } else {
               tit.setEnabled(false);
           }
        } else if (tit.getData().equals(ToolBarDes.TN_DISCONNECT)) {
            tit.setEnabled(mLogSrc.getStatus() == LogSource.stConnected);
        } else {
            super.updateToolItem(tit);
        }
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
                    Slogmain.getApp().getMainFrame().updateToolBars(QconnTabFrame.this);
               //     mStatusLabel.setImage(img);
              //      mStatusLabel.update();
                }
            }
           }
       );
       
    }

    
    public void onDisconnect() {
        if (!isDisposed()) {
            mLogSrc.disconnect();
            setImage(Resources.disconnected_32);
            Slogmain.getApp().getMainFrame().updateToolBars(this);
        }
    }
}
