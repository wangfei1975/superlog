/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package feiw;

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogListener;
import feiw.LogSource.LogView;
import feiw.widgets.SlogTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlogTabFrame extends CTabItem implements LogListener {

    private SlogTable mTable;
    private TableEditor mTableEditor;
    protected LogView mLogView = null;
    protected LogSource mLogSrc;
    private Label mLineCountLabel;
    private Label mSearchResult;

    public void onClose() {
        mLogSrc.removeLogView(mLogView);
    }

    public LogView getLogView() {
        return mLogView;
    }

    public LogSource getLogSource() {
        return mLogSrc;
    }

    public SlogTable getTable() {
        return mTable;
    }

    public void setLogFont() {

        Display display = getDisplay();
        Font ff = new Font(display, SystemConfigs.instance().getLogFontName(),
                SystemConfigs.instance().getLogFontSize(), 0);
        mTable.setFont(ff);
    }

    void updateToolItem(ToolItem tit) {
        String tn = (String) tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        }
        if (tn.equals(ToolBarDes.TN_COPY) || tn.equals(ToolBarDes.TN_COPYALL)) {
            tit.setEnabled(mTable.getSelectionCount() > 0);
        } else if (tn.equals(ToolBarDes.TN_NEXT) || tn.equals(ToolBarDes.TN_PREV)) {
            tit.setEnabled(mLogView.getSearchResults() > 0);
        } else if (tn.equals(ToolBarDes.TN_SAVEAS)) {
            // tit.setEnabled(!(mLogSrc instanceof FileLogSource));
            tit.setEnabled(true);
        }
    }

    void createContextMenu(int x, int y) {
        Menu menu = new Menu(mTable);
        MenuItem menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Copy Selected Items");
        Integer kacc = (Integer) Slogmain.getApp().getMainFrame().getToolItem(ToolBarDes.TN_COPYALL)
                .getData("KeyAccelerator");
        if (kacc != null) {
            menuItem.setAccelerator(kacc.intValue());
        }
        menuItem.setImage(Resources.copyall_16);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                onCopyAll();
            }
        });
        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Copy Selected Items (Message Only)");
        // menuItem.setAccelerator(SWT.SHIFT|SWT.COMMAND | 'c');
        kacc = (Integer) Slogmain.getApp().getMainFrame().getToolItem(ToolBarDes.TN_COPY).getData("KeyAccelerator");
        if (kacc != null) {
            menuItem.setAccelerator(kacc.intValue());
        }
        menuItem.setImage(Resources.copy_16);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                onCopy();
            }
        });
        menuItem = new MenuItem(menu, SWT.SEPARATOR);

        final int sels[] = mTable.getSelectionIndices();
        if (sels != null && sels.length > 1) {
            final Date sTime = mLogView.getLogParser().parseTime(mLogView.getLog(sels[0]));
            final Date eTime = mLogView.getLogParser().parseTime(mLogView.getLog(sels[sels.length - 1]));
            if (sTime != null && eTime != null) {
                menuItem = new MenuItem(menu, SWT.NONE);
                menuItem.setText("Time diff: " + (eTime.getTime() - sTime.getTime()) + " MS");
                menuItem = new MenuItem(menu, SWT.SEPARATOR);
            }
        }

        SelectionAdapter lisener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Object o = event.widget.getData();
                if (o instanceof Integer) {
                    LogFilter f = LogFilter.newLogFilter(LogFilter.FIELD_PRIORITY, LogFilter.OP_LESSTHEN, o);
                    Slogmain.getApp().getMainFrame().openFilterView(f);
                }
            }
        };

        final int it = mTable.getSelectionIndex();
        if (it >= 0) {
            String log = mLogView.getLog(it);
            final String tag = mLogView.getLogParser().parseTag(log);
            if (tag != null && !tag.trim().isEmpty()) {
                menuItem = new MenuItem(menu, SWT.NONE);
                menuItem.setText("Filter  [Tag = \"" + tag.trim() + "\"]");
                menuItem.setImage(Resources.filter_16);
                menuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        LogFilter f = LogFilter.newLogFilter(LogFilter.FIELD_TAG, LogFilter.OP_EQUALS, tag.trim());
                        Slogmain.getApp().getMainFrame().openFilterView(f);
                    }
                });
            }
            {
                final String pid = mLogView.getLogParser().parsePID(log);
                if (pid != null && !pid.trim().isEmpty()) {
                    menuItem = new MenuItem(menu, SWT.NONE);
                    menuItem.setText("Filter  [PID = \"" + pid.trim() + "\"]");
                    menuItem.setImage(Resources.filter_16);
                    menuItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            LogFilter f = LogFilter.newLogFilter(LogFilter.FIELD_PID, LogFilter.OP_EQUALS, pid.trim());
                            Slogmain.getApp().getMainFrame().openFilterView(f);
                        }
                    });
                }
            }

            final String tid = mLogView.getLogParser().parseTID(log);
            if (tid != null && !tid.trim().isEmpty()) {
                menuItem = new MenuItem(menu, SWT.NONE);
                menuItem.setText("Filter  [TID = \"" + tid.trim() + "\"]");
                menuItem.setImage(Resources.filter_16);
                menuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        LogFilter f = LogFilter.newLogFilter(LogFilter.FIELD_TID, LogFilter.OP_EQUALS, tid.trim());
                        Slogmain.getApp().getMainFrame().openFilterView(f);
                    }
                });
            }

        }

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Filter  [Priority < Verbos(7)]");
        menuItem.setImage(Resources.filter_16);
        menuItem.setData(Integer.valueOf(7));
        menuItem.addSelectionListener(lisener);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Filter  [Priority < Debug(6)]");
        menuItem.setImage(Resources.filter_16);
        menuItem.setData(Integer.valueOf(6));
        menuItem.addSelectionListener(lisener);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Filter  [Priority < Information(5)]");
        menuItem.setImage(Resources.filter_16);
        menuItem.setData(Integer.valueOf(5));
        menuItem.addSelectionListener(lisener);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Filter  [Message contains ...]");
        menuItem.setImage(Resources.filter_16);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                FilterDlg fdlg = new FilterDlg(getParent().getShell(), getLogView(), LogFilter.FIELD_CONTENT);
                if (fdlg.open() != SWT.OK) {
                    return;
                }
                LogFilter f = fdlg.getFilter();
                Slogmain.getApp().getMainFrame().openFilterView(f);
                SystemConfigs.instance().addRecentFilter(f);
            }
        });

        menu.setLocation(mTable.toDisplay(x, y));
        menu.setVisible(true);
    }

    public SlogTabFrame(CTabFolder parent, String txt, int style, LogSource logsrc, LogFilter logFilter,
            LogParser logParser, LogView parentLogView) {
        super(parent, style);

        mLogSrc = logsrc;
        mLogView = mLogSrc.newLogView(this, logFilter, logParser, parentLogView);

        setText(txt);
        Composite com = new Composite(parent, style);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        com.setLayout(layout);

        // createToolbar(com);

        SlogTable tb = new SlogTable(com, SWT.FLAT, mLogView);
        tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        mTable = tb;
        mTableEditor = new TableEditor(mTable);
        mTable.setLogView(mLogView);
        setLogFont();

        // mStatusLabel = new Label(com, SWT.BORDER_SOLID|SWT.ICON);
        // mStatusLabel.setImage(logsrc.getStatus() == LogSource.stConnected ?
        // Resources.connected_16 :Resources.disconnected_16);

        // mStatusLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
        // false, 1, 1));
        // mStatusLabel.setAlignment(SWT.LEFT);

        mLineCountLabel = new Label(com, SWT.BORDER);
        mLineCountLabel.setText("0 lines                ");
        mLineCountLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        Label lb = new Label(com, SWT.SEPARATOR);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        gd.heightHint = 16;
        lb.setLayoutData(gd);

        mSearchResult = new Label(com, SWT.BORDER);
        mSearchResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        setControl(com);

        mTable.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Slogmain.getApp().getMainFrame().updateToolBars(SlogTabFrame.this);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

        });

        mTableEditor.horizontalAlignment = SWT.LEFT;
        mTableEditor.grabHorizontal = true;
        mTable.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                Rectangle clientArea = mTable.getClientArea();
                Point pt = new Point(event.x, event.y);
                int index = mTable.getTopIndex();
                while (index < mTable.getItemCount()) {
                    boolean visible = false;
                    final TableItem item = mTable.getItem(index);
                    for (int i = 0; i < mTable.getColumnCount(); i++) {
                        Rectangle rect = item.getBounds(i);
                        if (rect.contains(pt)) {
                            final int column = i;
                            final Text text = new Text(mTable, SWT.NONE);
                            Listener textListener = new Listener() {
                                public void handleEvent(final Event e) {
                                    switch (e.type) {
                                        case SWT.FocusOut:
                                            item.setText(column, text.getText());
                                            text.dispose();
                                            break;
                                        case SWT.Traverse:
                                            switch (e.detail) {
                                                case SWT.TRAVERSE_RETURN:
                                                    item.setText(column, text.getText());
                                                    // FALL THROUGH
                                                case SWT.TRAVERSE_ESCAPE:
                                                    text.dispose();
                                                    e.doit = false;
                                            }
                                            break;
                                    }
                                }
                            };
                            text.addListener(SWT.FocusOut, textListener);
                            text.addListener(SWT.Traverse, textListener);
                            mTableEditor.setEditor(text, item, i);
                            text.setText(item.getText(i));
                            text.selectAll();
                            text.setFocus();
                            return;
                        }
                        if (!visible && rect.intersects(clientArea)) {
                            visible = true;
                        }
                    }
                    if (!visible)
                        return;
                    index++;
                }
            }
        });

        com.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // System.out.println("tab frame showing");
                mVisible = true;
                Slogmain.getApp().getMainFrame().updateToolBars(SlogTabFrame.this);
                updateLogUI();
            }

        });
        com.addListener(SWT.Hide, new Listener() {
            @Override
            public void handleEvent(Event event) {
                mVisible = false;
            }

        });

        mTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (e.button == 3) {
                    createContextMenu(e.x, e.y);
                }
            }

        });
    }

    int mLastSearchResults = 0;
    boolean mVisible = false;

    private void updateSearchUI() {
        int nresults = mLogView.getSearchResults();
        // System.out.println("nresults = " + nresults + " last results = " +
        // mLastSearchResults);
        if (nresults != mLastSearchResults) {
            if (nresults >= 0) {
                mSearchResult.setText("Found " + nresults + " results of \"" + mLogView.getSearchPattern() + "\"");
            } else if (nresults < 0) {
                mSearchResult.setText("");
            }
        }
        mLastSearchResults = nresults;
    }

    private void updateLogUI() {
        if (!mVisible || mTable.isDisposed())
            return;

        if (mLogView.isPaused())
            return;

        final int cnt = mLogView.size();
        final int cnto = mTable.getItemCount();
        final int rolls = mLogView.getRollLines();
        if (rolls > 0 && cnt >= rolls) {
            mTable.setRedraw(true);
            mTable.setItemCount(0);
            mTable.setItemCount(cnt);
            mTable.setTopIndex(cnt - 2);
            mLineCountLabel.setText("" + cnt + " lines");
            mLineCountLabel.pack();
            updateSearchUI();
        } else if (cnto != cnt) {
            // System.out.println("log changed old cnt = " + cnto + " new cnt =
            // " + cnt);
            // mTable.setItemCount(0);
            mTable.setRedraw(true);
            mTable.setItemCount(cnt);
            mTable.setTopIndex(cnt - 2);
            mLineCountLabel.setText("" + cnt + " lines");
            mLineCountLabel.pack();
            updateSearchUI();
        }
        //
    }

    AtomicBoolean mLogChangPosted = new AtomicBoolean(false);

    @Override
    public void onLogChanged() {

        if (mVisible && !mLogChangPosted.get()) {
            mLogChangPosted.set(true);
            Display display = getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    mLogChangPosted.set(false);
                    updateLogUI();
                }
            });

        }
    }

    public void onPause() {
        if (!isDisposed()) {
            if (mLogView.isPaused()) {
                setImage(Resources.connected_32);
                mLogView.resume();
                updateLogUI();
            } else {
                setImage(Resources.pause_32);
                mLogView.pause();
            }
            Slogmain.getApp().getMainFrame().updateToolBars(this);
        }
    }

    public void onDisconnect() {

    }

    public void onNext() {
        if (mLogView.getSearchResults() <= 0 || mTable.isDisposed()) {
            return;
        }
        int sel = mTable.getSelectionIndex();
        if (sel < 0) {
            sel = mTable.getTopIndex();
        }
        int n = mLogView.getNextSearchResult(sel + 1);
        if (n >= 0) {
            mTable.deselectAll();
            mTable.select(n);
            if (n < mTable.getTopIndex() || n >= mTable.getTopIndex() + getTableVisibleCount()) {
                mTable.setTopIndex(n);
            }
        }
    }

    public void onPrev() {
        if (mLogView.getSearchResults() <= 0 || mTable.isDisposed()) {
            return;
        }
        int sel = mTable.getSelectionIndex();
        if (sel < 0) {
            sel = mTable.getTopIndex();
        }
        int n = mLogView.getPrevSearchResult(sel - 1);
        if (n >= 0) {
            mTable.deselectAll();
            mTable.select(n);
            if (n < mTable.getTopIndex() || n >= mTable.getTopIndex() + getTableVisibleCount()) {
                mTable.setTopIndex(n);
            }
        }
    }

    void copyLog(boolean verbose) {
        Clipboard cb = new Clipboard(getDisplay());
        int sels[] = mTable.getSelectionIndices();
        if (sels == null || sels.length <= 0) {
            return;
        }
        StringBuffer txt = new StringBuffer();
        final LogView v = mLogView;
        for (int i = 0; i < sels.length; i++) {

            String l = v.getLog(sels[i]);
            if (l != null) {
                if (verbose) {
                    txt.append(l);
                } else {
                    txt.append(v.getLogParser().parseMessage(l));
                }
                if (i < sels.length) {
                    txt.append("\n");
                }
            }
        }
        cb.setContents(new Object[] { txt.toString() }, new Transfer[] { TextTransfer.getInstance() });
    }

    public void onCopyAll() {
        copyLog(true);
    }

    public void onCopy() {
        copyLog(false);
    }

    public void onSaveAs(String fname) {
        try {
            FileOutputStream os = new FileOutputStream(fname);
            mLogView.writeLogs(os);
            os.close();
        } catch (FileNotFoundException e) {
            MessageBox m = new MessageBox(null, SWT.OK | SWT.ICON_ERROR);
            m.setMessage("Cound not open " + fname + " ");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int getTableVisibleCount() {
        Rectangle rect = mTable.getClientArea();
        int itemHeight = mTable.getItemHeight();
        int headerHeight = mTable.getHeaderHeight();
        return (rect.height - headerHeight - itemHeight - 1) / itemHeight;
    }

    public void onSearch(String txt, boolean caseSensitive) {
        mLogView.search(txt, caseSensitive);
    }

    public void onClear() {
        mLogView.clear();
    }

    @Override
    public void onSearchResult() {
        // Display display = getDisplay();
        // display.asyncExec(new Runnable() {
        // @Override
        // public void run() {
        if (mTable.isDisposed() || !mTable.isVisible())
            return;

        int top = mTable.getTopIndex();

        int nresults = mLogView.getSearchResults();
        if (nresults == 0) {
            mTable.setItemCount(0);
            mTable.setRedraw(true);
            mTable.setItemCount(mLogView.size());

            mTable.setTopIndex(top);

        } else {
            int first = mLogView.getNextSearchResult(0);

            if (first >= 0) {
                mTable.setItemCount(0);
                mTable.setRedraw(true);
                mTable.setItemCount(mLogView.size());

                int visibleCount = getTableVisibleCount();

                if (first < top || first >= top + visibleCount) {
                    mTable.setTopIndex(first);
                } else {
                    mTable.setTopIndex(top);
                }
                mTable.select(first);
                mTable.setFocus();
            }
        }
        updateSearchUI();
        Slogmain.getApp().getMainFrame().updateToolBars(this);

        // }
        // });
    }

}
