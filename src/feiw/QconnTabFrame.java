package feiw;

import java.util.HashMap;
import java.util.Map;

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

    void updateToolItem(ToolItem tit) {
        tit.setEnabled(true);
    }
}
