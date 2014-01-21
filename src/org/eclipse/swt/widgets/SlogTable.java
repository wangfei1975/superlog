package org.eclipse.swt.widgets;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import feiw.LogParser;
import feiw.Resources;
import feiw.Slogmain;
import feiw.SystemConfigs;
import feiw.LogSource.LogView;

public final class SlogTable extends Table {

    LogView mLogView = null;

    public void setLogView(LogView v) {
        mLogView = v;
    }
    public SlogTable(Composite parent, int style, LogView v) {
        super(parent, style | SWT.BORDER | SWT.VIRTUAL | SWT.MULTI);
        mLogView = v;
        setLinesVisible(true);
        setHeaderVisible(true);
        
        
        //final String[] title = { "Flag", "Line", "Time", "Level", "Major", "Minor", "Args" };
        final String[] title = mLogView.getLogParser().getTableHeader();
        final int[] width = mLogView.getLogParser().getHeaderWidth();

        for (int i = 0; i < title.length; i++) {
            TableColumn column = new TableColumn(this, SWT.NONE);
            column.setText(title[i]);
           // column.setAlignment(SWT.CENTER);
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

                final TableItem item = (TableItem) e.item;
                final int index = SlogTable.this.indexOf(item);
                item.setText(1, Integer.toString(index));
                
                mLogView.getLogParser().updateTableItem(mLogView.getLog(index), item, mLogView.getSearchPattern());
            }
        });

    }
}
