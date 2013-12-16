package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import qnx.LogSource.LogItem;
import qnx.LogSource.LogView;
import qnx.Resources;
import qnx.Slogmain;
import qnx.SystemConfigs;
public final class SlogTable extends Table {

    LogView mLogView = null;

    public void setLogView(LogView v) {
        mLogView = v;
    }
    public SlogTable(Composite parent, int style) {
        super(parent, style | SWT.BORDER | SWT.VIRTUAL | SWT.MULTI);
        setLinesVisible(true);
        setHeaderVisible(true);
        String[] title = { "Flag", "Line", "Time", "Sev", "Major", "Minor", "Args" };
        int[] width = { 28, 70, 155, 30, 50, 50, 1000 };

        for (int i = 0; i < title.length; i++) {
            TableColumn column = new TableColumn(this, SWT.NONE);
            column.setText(title[i]);
            column.setWidth(width[i]);
        }

        FontData[] fontData = getFont().getFontData();

        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(18);
        }
        Display display = getShell().getDisplay();
        Font font = new Font(display, "Monaco", 14, 0);
        setFont(font);

        addListener(SWT.SetData, new Listener() {
            public void handleEvent(Event e) {
                if (mLogView == null) {
                    return;
                }

                TableItem item = (TableItem) e.item;
                int index = SlogTable.this.indexOf(item);
                LogItem log = mLogView.getLog(index);

                if (log == null) {
                    return;
                }
                item.setText(1, "" + index);
                for (int i = 0; i < log.getTextCount(); i++) {
                    item.setText(i + 2, log.getText(i) == null ? "" : log.getText(i));
                }

                final SystemConfigs cfgs = Slogmain.getApp().getConfigs();
                Color bk = cfgs.getLogBackground(log.getLevel());
                if (bk != null) {
                item.setBackground(bk);
                }
                item.setForeground(cfgs.getLogForeground(log.getLevel()));

                if (log.getSearchMarker() != 0) {
                    item.setBackground(cfgs.getSearchMarkerBackground());
                    item.setImage(Resources.iconSearch);
                }
            }
        });

    }
}
