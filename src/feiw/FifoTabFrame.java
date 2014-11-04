package feiw;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.StatusListener;

public final class FifoTabFrame extends SlogTabFrame implements  StatusListener {

    public FifoTabFrame(CTabFolder parent, String txt, int style, String fifo) throws DeviceNotConnected {
        super(parent, txt, style, new FifoLogSource(fifo), null, new LogParser.QnxLogParser(), null);
        setImage(Resources.fifo_32);
        mLogSrc.addStatusListener(this);
    }

    @Override
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
    @Override
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
            img = Resources.fifo_32;
            break;
         default:
             img = Resources.fifo_32;
                break;
        }
       
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed()) {
                    setImage(img);
                    Slogmain.getApp().getMainFrame().updateToolBars(FifoTabFrame.this);
               //     mStatusLabel.setImage(img);
              //      mStatusLabel.update();
                }
            }
           }
       );
       
    }

    
    @Override
    public void onDisconnect() {
        if (!isDisposed()) {
            mLogSrc.disconnect();
            setImage(Resources.disconnected_32);
            Slogmain.getApp().getMainFrame().updateToolBars(this);
        }
    }
}
