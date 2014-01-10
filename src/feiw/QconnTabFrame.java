package feiw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;

public class QconnTabFrame extends SlogTabFrame {

    public QconnTabFrame(CTabFolder parent, String txt, int style, String ip, int port) {
        super(parent, txt, style, new QconnLogSource(ip, port), null);
        setImage(Resources.connectDevice_16);
    }
    
    void createToolItems(ToolBar tb) {
        ToolItem it;
        
        it = new ToolItem(tb, SWT.PUSH);
        it.setImage(Resources.disconnected_32);

        it = new ToolItem(tb, SWT.PUSH);
      //  it.setText("Pause (Running)     ");
        it.setImage(Resources.pause_32);
        //mToolPause.addListener(SWT.Selection, onClickPause);
        it.setData("Pause");
        
        new ToolItem(tb, SWT.SEPARATOR);        
        
        super.createToolItems(tb);
    }
}
