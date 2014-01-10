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
        setImage(Resources.filter_16);
    }
    void createToolItems(ToolBar tb) {
        super.createToolItems(tb);
    }
}
