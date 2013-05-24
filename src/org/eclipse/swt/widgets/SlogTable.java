package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


import qnx.Resources;
import qnx.SlogInfo;
import qnx.SlogInfo.LogItem;
import qnx.Slogmain;
import qnx.SystemConfigs;

public final class SlogTable extends Table {

    public interface LogProvider {
        public LogItem getLog(int index);
    }
    LogProvider mLogProvider = null;


    public void setLogProvider(LogProvider provider) {
        mLogProvider = provider;
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
                if (mLogProvider == null) {
                    return;
                }

                TableItem item = (TableItem) e.item;
                int index = SlogTable.this.indexOf(item);
                SlogInfo.LogItem log = mLogProvider.getLog(index);

                if (log == null) {
                    return;
                }
                item.setText(1, "" + index);
                for (int i = 0; i < log.getTextCount(); i++) {
                    item.setText(i + 2, log.getText(i) == null ? "" : log.getText(i));
                }

                final SystemConfigs cfgs = Slogmain.getInstance().getConfigs();
                item.setBackground(cfgs.getLogBackground(log.getLevel()));
                item.setForeground(cfgs.getLogForeground(log.getLevel()));

                if (log.getSearchMarker() != 0) {
                    item.setBackground(cfgs.getSearchMarkerBackground());
                    item.setImage(Resources.iconSearch);
                }
            }
        });

    }
}
