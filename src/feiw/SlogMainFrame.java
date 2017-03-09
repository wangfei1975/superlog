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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;
import feiw.SystemConfigs.LogUrl;
import feiw.ToolBarDes.ToolItemDes;

public final class SlogMainFrame {

    private Display mDisplay;
    private Shell mShell;
    private static AtomicBoolean mWaitingDev = new AtomicBoolean(false);

    CoolBar mCoolBar = null;
    List<ToolItem> mToolItems = new ArrayList<ToolItem>(10);
    CTabFolder mTabFolder;

    public Display getDisplay() {
        return mDisplay;
    }

    public Shell getShell() {
        return mShell;
    }

    ToolItem getToolItem(String name) {
        for (ToolItem it : mToolItems) {
            if (name.equals(it.getData())) {
                return it;
            }
        }
        return null;
    }

    public SlogMainFrame(String caption, Display disp) {
        mDisplay = disp;
        mShell = new Shell(disp);
        mShell.setText(caption);
        mShell.setImage(Resources.android_log_32);
        createContents();
        mShell.addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                closeTabFrames();
            }
        });
    }

    void createToolBar(ToolBarDes tbdes) {
        ToolBar tb = new ToolBar(mCoolBar, SWT.FLAT);
        tb.setData(tbdes.mName);
        for (ToolItemDes itdes : tbdes.mItems) {
            // System.out.println("create tool bar " + itdes.mName);
            ToolItem it = new ToolItem(tb, itdes.mStyle);
            it.setData(itdes.mName);
            it.setData("KeyAccelerator", new Integer(itdes.mKeyAccelerator));
            it.setToolTipText(itdes.mTipText);
            if (itdes.mImage != null) {
                it.setImage(itdes.mImage);
                it.setDisabledImage(new Image(getDisplay(), itdes.mImage, SWT.IMAGE_GRAY));
            }
            mToolItems.add(it);
        }

        CoolItem item = new CoolItem(mCoolBar, SWT.NONE);
        Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        p.x += 2;
        tb.setSize(p);
        Point p2 = item.computeSize(p.x + 2, p.y);
        item.setControl(tb);

        item.setSize(p2);
        tb.pack();

    }

    abstract class DropdownListener extends SelectionAdapter {
        private ToolItem dropdown;

        private Menu menu;

        public abstract void init();

        public abstract void onToolSelected(ToolItem dropdown);

        public abstract void onListSelected(final Object o);

        public DropdownListener(ToolItem dropdown) {
            this.dropdown = dropdown;
            menu = new Menu(dropdown.getParent().getShell());
            init();
        }

        public void clearList() {
            menu.dispose();
            menu = new Menu(dropdown.getParent().getShell());
        }

        public void addListItem(String item, Image img, final Object o) {
            MenuItem menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText(item);
            menuItem.setImage(img);
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    onListSelected(o);
                }
            });
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            if (!dropdown.getEnabled()) {
                return;
            }
            if (event.detail == SWT.ARROW) {
                init();
                ToolItem item = (ToolItem) event.widget;
                Rectangle rect = item.getBounds();
                Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
                menu.setLocation(pt.x, pt.y + rect.height);
                menu.setVisible(true);
            } else {
                onToolSelected(dropdown);
            }
        }
    }

    public FilterTabFrame openFilterView(LogFilter f) {
        SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
        FilterTabFrame ltab = new FilterTabFrame(mTabFolder, "\"" + f.getName() + "\" on [" + tbf.getText() + "]",
                SWT.FLAT | SWT.CLOSE | SWT.ICON, tbf.getLogSource(), f, tbf.getLogView().getLogParser(),
                tbf.getLogView());
        mTabFolder.setSelection(ltab);
        SystemConfigs.instance().addRecentFilter(f);
        return ltab;
    }

    void createToolBars() {
        mCoolBar = new CoolBar(getShell(), SWT.FLAT);
        mCoolBar.setBackground(new Color(getDisplay(), 255, 255, 255));
        mCoolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
        for (ToolBarDes tdes : ToolBarDes.TOOBARS) {
            createToolBar(tdes);
        }

        getToolItem(ToolBarDes.TN_NEXT).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                if (tbf != null) {
                    tbf.onNext();
                }
            }

        });

        getToolItem(ToolBarDes.TN_PREV).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                if (tbf != null) {
                    tbf.onPrev();
                }
            }

        });

        getToolItem(ToolBarDes.TN_SEARCH).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SearchDlg d = new SearchDlg(Slogmain.getApp().getMainFrame().getShell());
                String txt = d.open();
                if (txt != null && !(txt.trim().isEmpty())) {
                    SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.onSearch(txt.trim(), d.isCaseSenstive());
                    }
                }
            }

        });

        getToolItem(ToolBarDes.TN_CONNECT)
                .addSelectionListener(new DropdownListener(getToolItem(ToolBarDes.TN_CONNECT)) {
                    @Override
                    public void onToolSelected(ToolItem dropdown) {
                        SystemConfigs.LogUrl lu = SystemConfigs.instance().getRecentUrl(0);
                        ConnectDlg dlg = new ConnectDlg(getShell(), lu == null ? "10.222.98.205" : lu.url,
                                lu == null ? 8000 : lu.port);
                        if (dlg.open() == SWT.OK) {
                            QconnTabFrame ltab;
                            try {
                                String title = "qconn://" + dlg.getIp() + ":" + dlg.getPort();
                                ltab = new QconnTabFrame(mTabFolder, title, SWT.FLAT | SWT.CLOSE | SWT.ICON,
                                        dlg.getIp(), dlg.getPort());
                                mTabFolder.setSelection(ltab);
                                SystemConfigs.instance().addRecentUrl(new LogUrl("qconn", dlg.getIp(), dlg.getPort()));
                                updateToolBars(ltab);
                            } catch (DeviceNotConnected e) {
                                MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
                                m.setText("Error");
                                m.setMessage(e.getMessage());
                                m.open();
                            }

                        }
                    }

                    @Override
                    public void onListSelected(Object o) {
                        if (o instanceof LogUrl) {
                            LogUrl lu = (LogUrl) o;
                            QconnTabFrame ltab;
                            try {
                                ltab = new QconnTabFrame(mTabFolder, lu.toString(), SWT.FLAT | SWT.CLOSE | SWT.ICON,
                                        lu.url, lu.port);
                                mTabFolder.setSelection(ltab);
                                SystemConfigs.instance().addRecentUrl(new LogUrl("qconn", lu.url, lu.port));
                                updateToolBars(ltab);
                            } catch (DeviceNotConnected e) {
                                MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
                                m.setText("Error");
                                m.setMessage(e.getMessage());
                                m.open();
                            }

                        }

                    }

                    @Override
                    public void init() {
                        clearList();
                        for (int i = 0; i < SystemConfigs.RECENTLIST_SIZE; i++) {
                            LogUrl lu = SystemConfigs.instance().getRecentUrl(i);
                            if (lu != null) {
                                addListItem(lu.toString(), Resources.connected_16, lu);
                            }
                        }

                    }
                });

        getToolItem(ToolBarDes.TN_OPEN).addSelectionListener(new DropdownListener(getToolItem(ToolBarDes.TN_OPEN)) {

            @Override
            public void onToolSelected(ToolItem dropdown) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                String[] filterNames = new String[]{"Log Files", "All Files (*)"};
                String[] filterExtensions = new String[]{"*.log;*.txt;", "*"};
                // String filterPath = "";
                /*
                 * String platform = SWT.getPlatform(); if
                 * (platform.equals("win32") || platform.equals("wpf")) {
                 * filterNames = new String [] {"Image Files", "All Files (*.*)"
                 * }; filterExtensions = new String []
                 * {"*.gif;*.png;*.bmp;*.jpg;*.jpeg;*.tiff", "*.*"}; filterPath
                 * = "c:\\"; }
                 */
                dialog.setFilterNames(filterNames);
                dialog.setFilterExtensions(filterExtensions);
                // dialog.setFilterPath (filterPath);
                String fname = dialog.open();
                if (fname != null) {
                    FileTabFrame ftb;
                    try {
                        ftb = new FileTabFrame(mTabFolder, fname, SWT.FLAT | SWT.CLOSE | SWT.ICON, fname);
                        mTabFolder.setSelection(ftb);

                        SystemConfigs.instance().addRecentFile(fname);
                        updateToolBars(ftb);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onListSelected(Object o) {

                if (o instanceof String) {
                    String fname = (String) o;
                    FileTabFrame ftb;
                    try {
                        ftb = new FileTabFrame(mTabFolder, fname, SWT.FLAT | SWT.CLOSE | SWT.ICON, fname);
                        mTabFolder.setSelection(ftb);
                        SystemConfigs.instance().addRecentFile(fname);
                        updateToolBars(ftb);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void init() {
                clearList();
                for (int i = 0; i < SystemConfigs.RECENTLIST_SIZE; i++) {
                    String fname = SystemConfigs.instance().getRecentFile(i);
                    if (fname != null) {
                        addListItem(fname, Resources.openfile_16, fname);
                    }
                }

            }
        });
        getToolItem(ToolBarDes.TN_OPENFIFO).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                String[] filterNames = new String[]{"Fifo", "All Files (*)"};
                String[] filterExtensions = new String[]{"*"};

                dialog.setFilterNames(filterNames);
                dialog.setFilterExtensions(filterExtensions);
                // dialog.setFilterPath (filterPath);
                String fname = dialog.open();
                if (fname != null) {
                    FifoTabFrame ftb;

                    try {
                        ftb = new FifoTabFrame(mTabFolder, fname, SWT.FLAT | SWT.CLOSE | SWT.ICON, fname);
                        mTabFolder.setSelection(ftb);

                        SystemConfigs.instance().addRecentFile(fname);
                        updateToolBars(ftb);
                    } catch (DeviceNotConnected e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });

        getToolItem(ToolBarDes.TN_FILTER).addSelectionListener(new DropdownListener(getToolItem(ToolBarDes.TN_FILTER)) {

            @Override
            public void onToolSelected(ToolItem dropdown) {
                SlogTabFrame ctab = (SlogTabFrame) mTabFolder.getSelection();
                String defaultField = LogFilter.FIELD_CONTENT;
                if (ctab.getLogSource() instanceof AndroidLogSource) {
                    defaultField = LogFilter.FIELD_TAG;
                }
                FilterDlg fdlg = new FilterDlg(getShell(), ctab.getLogView(), defaultField);
                if (fdlg.open() != SWT.OK) {
                    return;
                }
                LogFilter f = fdlg.getFilter();
                openFilterView(f);
            }

            @Override
            public void onListSelected(final Object o) {
                if (o instanceof LogFilter) {
                    openFilterView((LogFilter) o);
                }

            }

            @Override
            public void init() {
                clearList();
                for (int i = 0; i < SystemConfigs.RECENTLIST_SIZE; i++) {
                    LogFilter f = SystemConfigs.instance().getRecentFilter(i);
                    if (f != null) {
                        addListItem(f.getName(), Resources.filter_16, f);
                    }
                }
            }

        });

        getToolItem(ToolBarDes.TN_CLEAR).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                tbf.onClear();
            }
        });

        getToolItem(ToolBarDes.TN_PAUSE).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                if (tbf != null) {
                    tbf.onPause();
                }
            }
        });
        getToolItem(ToolBarDes.TN_DISCONNECT).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                if (tbf != null) {
                    tbf.onDisconnect();
                }
            }
        });
        getToolItem(ToolBarDes.TN_COPY).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                tbf.onCopy();
            }
        });
        getToolItem(ToolBarDes.TN_COPYALL).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                tbf.onCopyAll();
            }
        });

        getToolItem(ToolBarDes.TN_SAVEAS).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                final String[] filterNames = new String[]{"Log Files", "All Files (*)"};
                final String[] filterExtensions = new String[]{"*.log;*.txt;", "*"};
                dialog.setFilterNames(filterNames);
                dialog.setFilterExtensions(filterExtensions);
                String fname = dialog.open();
                if (fname != null) {
                    SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                    tbf.onSaveAs(fname);
                }
            }
        });

        getToolItem(ToolBarDes.TN_PREFERENCE).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PerferenceDlg dlg = new PerferenceDlg(getShell());
                dlg.setFontSize(SystemConfigs.instance().getLogFontSize());
                if (dlg.open() == SWT.OK) {
                    int fs = dlg.getFontSize();
                    SystemConfigs.instance().setLogFontSize(fs);
                    SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.setLogFont();
                    }
                }
            }
        });

        getToolItem(ToolBarDes.TN_HELP).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
                m.setText("About SuperLog");
                m.setMessage("SuperLog Version 1.0.5\n");
                m.open();
            }
        });

        getToolItem(ToolBarDes.TN_CONNECTANDROID).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!AndroidLogSource.checkAdb(SystemConfigs.instance().getAdbPath() + "/adb")) {
                    String adbPath = null;
                    do {

                        DirectoryDialog dialog = new DirectoryDialog(getShell());
                        dialog.setText("ADB not found, Please specify directory that contains ADB");
                        adbPath = dialog.open();
                    } while (adbPath != null && !AndroidLogSource.checkAdb(adbPath + "/adb"));
                    if (adbPath == null) {
                        return;
                    }
                    SystemConfigs.instance().setAdbPath(adbPath);
                }

                // try connecting existing device
                if (connectDevice() == true)
                    return;

                if (mWaitingDev.compareAndSet(false, true) == false)
                    return;

                // if no existing device connected, start a new thread to wait for device connection
                new Thread() {
                    public void run() {

                        String[] devs = AndroidLogSource.enumDevices();
                        int wait = 0;

                        while (devs == null) {
                            if (wait == 0) {
                                // Info the user for the 1st time
                                getDisplay().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
                                        m.setText("Warning!");
                                        m.setMessage("Waiting for Android devices to be connected");
                                        m.open();
                                    }
                                });
                            }

                            // wait one second
                            wait++;

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }

                            // if more than 60 seconds passed, give up
                            if (wait > 60) {
                                getDisplay().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
                                        m.setText("Warning!");
                                        m.setMessage("No Android device connected in 1 minute, give up!");
                                        m.open();
                                    }
                                });

                                mWaitingDev.set(false);

                                //exit the thread
                                return;
                            }

                            // check again for any device connection
                            devs = AndroidLogSource.enumDevices();
                        }

                        // try connecting the connected device, will update UI from external thread
                        getDisplay().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                connectDevice();
                            }
                        });

                        mWaitingDev.set(false);
                    }
                }.start();
            }
        });
    }

    public boolean connectDevice() {
        String[] devs = AndroidLogSource.enumDevices();
        int selectedDevice = 0;

        if (devs == null)
            return false;

        if (devs.length > 1) {
            //multiple device, choice
            AndroidDeviceChoiceDlg d = new AndroidDeviceChoiceDlg(getShell(), devs, 0);
            if (d.open() != SWT.OK) {
                return true;
            }
            selectedDevice = d.getSelection();
        }

        try {
            SlogTabFrame ltab = new AndroidTabFrame(mTabFolder,
                    SWT.FLAT | SWT.CLOSE | SWT.ICON, devs[selectedDevice]);
            mTabFolder.setSelection(ltab);
            updateToolBars(ltab);
            return true;
        } catch (DeviceNotConnected e1) {
            return false;
        }
    }

    void updateToolItem(ToolItem tit) {

        String tn = (String) tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        }
        if (tn.equals(ToolBarDes.TN_CONNECT)) {
            tit.setEnabled(true);
        } else if (tn.equals(ToolBarDes.TN_OPEN)) {
            tit.setEnabled(true);
        } else if (tn.equals(ToolBarDes.TN_PREFERENCE)) {
            tit.setEnabled(true);
        } else if (tn.equals(ToolBarDes.TN_HELP)) {
            tit.setEnabled(true);
        } else if (tn.equals(ToolBarDes.TN_CONNECTANDROID)) {
            tit.setEnabled(true);
        } else if (tn.equals(ToolBarDes.TN_OPENFIFO)) {
            tit.setEnabled(true);
        } else {
            tit.setEnabled(false);
        }

    }

    void updateToolBars(SlogTabFrame it) {
        for (ToolItem tit : mToolItems) {
            if (it == null) {
                updateToolItem(tit);
            } else {
                it.updateToolItem(tit);
            }
        }
        mCoolBar.pack(true);
    }

    protected Control createContents() {

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        getShell().setLayout(layout);
        createToolBars();
        updateToolBars(null);
        mTabFolder = new CTabFolder(getShell(), SWT.BORDER);
        mTabFolder.setSimple(false);
        mTabFolder.setUnselectedCloseVisible(true);
        mTabFolder.setUnselectedImageVisible(true);

        mTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        /*
         * mTabFolder.addSelectionListener(new SelectionListener() {
         *
         * @Override public void widgetSelected(SelectionEvent e) { if (e.item
         * instanceof SlogTabFrame) { SlogTabFrame it = (SlogTabFrame)e.item; //
         * updateToolBars(it); } }
         *
         * @Override public void widgetDefaultSelected(SelectionEvent e) { if
         * (e.item instanceof SlogTabFrame) { SlogTabFrame it =
         * (SlogTabFrame)e.item; // updateToolBars(it); } }
         *
         * });
         */
        mTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {
                if (event.item instanceof SlogTabFrame) {
                    SlogTabFrame it = (SlogTabFrame) event.item;
                    it.onClose();
                    if (mTabFolder.getItemCount() == 1) {
                        updateToolBars(null);
                    }
                }
            }
        });

        mTabFolder.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                Point point = mDisplay.map(null, mTabFolder, new Point(event.x, event.y));
                SlogTabFrame cTabItem = (SlogTabFrame) mTabFolder.getItem(point);
                if (cTabItem != null) {
                    System.out.println("MenuDetect on tab: " + cTabItem.getText());
                    Menu menu = new Menu(mTabFolder);
                    MenuItem menuItem;

                    menuItem = new MenuItem(menu, SWT.PUSH);
                    menuItem.setText("Load Filter Settings");
                    menuItem.setImage(Resources.update);
                    menuItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            int index = mTabFolder.getSelectionIndex();
                            System.out.println("Load filter for tab index " + index);
                            loadTabFilter(index);
                        }
                    });

                    if (cTabItem.getLogView().getRawRules() != null) {

                        menuItem = new MenuItem(menu, SWT.PUSH);
                        menuItem.setText("Save Filter Settings");
                        menuItem.setImage(Resources.update);
                        menuItem.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                int index = mTabFolder.getSelectionIndex();
                                System.out.println("Save filter for tab index " + index);
                                saveTabFilter(index);
                            }
                        });

                        menuItem = new MenuItem(menu, SWT.PUSH);
                        menuItem.setText("Update Filter Settings");
                        menuItem.setImage(Resources.update);
                        menuItem.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                int index = mTabFolder.getSelectionIndex();
                                System.out.println("Update filter for tab index " + index);
                                updateTabFilter(index);
                            }
                        });
                    }

                    menuItem = new MenuItem(menu, SWT.PUSH);
                    menuItem.setText("Close Right Tabs");
                    menuItem.setImage(Resources.right_arrow);
                    menuItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            int index = mTabFolder.getSelectionIndex();
                            System.out.println("Closing right tabs for index " + index);
                            closeRightTabFrames(index);
                        }
                    });

                    menuItem = new MenuItem(menu, SWT.PUSH);
                    menuItem.setText("Close Left Tabs");
                    menuItem.setImage(Resources.left_arrow);
                    menuItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            int index = mTabFolder.getSelectionIndex();
                            System.out.println("Closing left tabs for index " + index);
                            closeLeftTabFrames(index);
                        }
                    });

                    menuItem = new MenuItem(menu, SWT.PUSH);
                    menuItem.setText("Close This Tab!");
                    menuItem.setImage(Resources.self_arrow);
                    menuItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            int index = mTabFolder.getSelectionIndex();
                            System.out.println("Closing this tab for index " + index);
                            closeThisTabFrames(index);
                        }
                    });

                    menuItem = new MenuItem(menu, SWT.PUSH);
                    menuItem.setText("Edit Tab Name");
                    menuItem.setImage(Resources.self_arrow);
                    menuItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            int index = mTabFolder.getSelectionIndex();
                            System.out.println("Edit Tab Name " + index);
                            MessageDlg d = new MessageDlg(Slogmain.getApp().getMainFrame().getShell());
                            SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                            d.setInput(tbf.getText());
                            d.setMessage("Please Copy or Edit the Tab Name");
                            d.setText("Edit Tab Name");
                            String txt = d.open();
                            if (txt != null && !(txt.trim().isEmpty())) {
                                tbf.setText(txt);
                            }
                        }
                    });

                    menu.setLocation(event.x, event.y);
                    menu.setVisible(true);
                }
            }
        });


        mTabFolder.setFocus();

        getDisplay().addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {

                for (ToolItem it : mToolItems) {
                    if (it.isEnabled()) {
                        Integer key = (Integer) it.getData("KeyAccelerator");
                        if (key.intValue() == (e.stateMask | e.keyCode)) {
                            it.notifyListeners(SWT.Selection, null);
                        }

                    }
                }

                if (e.keyCode == SWT.F3) {
                    SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.onNext();
                    }
                } else if (e.keyCode == SWT.F4) {
                    SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.onPrev();
                    }
                }

                if (((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'f')) {
                    SearchDlg d = new SearchDlg(Slogmain.getApp().getMainFrame().getShell());
                    String txt = d.open();
                    if (txt != null && !(txt.trim().isEmpty())) {
                        SlogTabFrame tbf = (SlogTabFrame) mTabFolder.getSelection();
                        if (tbf != null) {
                            tbf.onSearch(txt.trim(), d.isCaseSenstive());
                        }
                    }
                }
                /*
                 * if(((e.stateMask & SWT.COMMAND) == SWT.COMMAND) && (e.keyCode
                 * == SWT.ARROW_RIGHT)) { System.out.print(" mask = " +
                 * e.stateMask + " key code = " + e.keyCode); int sidx =
                 * mTabFolder.getSelectionIndex(); if (sidx+1 <
                 * mTabFolder.getItemCount()) { mTabFolder.setSelection(sidx+1);
                 * } else if (sidx != 0){ mTabFolder.setSelection(0); } }
                 */
            }
        });

        getShell().setSize(1200, 800);

        return getShell();

    }

    public void closeTabFrames() {
        for (CTabItem it : mTabFolder.getItems()) {
            SlogTabFrame tbf = (SlogTabFrame) it;
            tbf.onClose();
        }
    }

    public void closeLeftTabFrames(int index) {
        CTabItem it;

        if (index < 0)
            return;

        for (int i = 0; i < index; i++) {
            it = mTabFolder.getItem(i);
            SlogTabFrame tbf = (SlogTabFrame) it;
            tbf.onClose();
            it.dispose();
        }
    }

    public void closeThisTabFrames(int index) {
        CTabItem it;

        if (index >= mTabFolder.getItemCount() || index < 0)
            return;

        it = mTabFolder.getItem(index);
        SlogTabFrame tbf = (SlogTabFrame) it;
        tbf.onClose();
        it.dispose();
    }

    public void updateTabFilter(int index) {
        CTabItem it;

        if (index >= mTabFolder.getItemCount() || index < 0)
            return;

        it = mTabFolder.getItem(index);
        SlogTabFrame tbf = (SlogTabFrame) it;

        if (tbf instanceof FilterTabFrame) {
            FilterDlg fdlg = new FilterDlg(getShell(), tbf.getLogView());
            if (fdlg.open(tbf.getLogView().getLogFilter().getRawRules()) != SWT.OK) {
                return;
            }
            LogFilter f = fdlg.getFilter();
            tbf.onUpdateFilter(f);
        }
    }

    public ArrayList<FilterDlg.RawRule> loadRawRules(String fileName) {
        ArrayList<FilterDlg.RawRule> rawRules = new ArrayList<>(5);

        if (fileName == null || rawRules == null)
            return null;

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        rawRules.clear();

        if (reader == null)
            return rawRules;

        String[] nextLine;

        int index = 0;

        try {
            while ((nextLine = reader.readNext()) != null) {
                FilterDlg.RawRule rawRule = new FilterDlg.RawRule();
                // nextLine[] is an array of values from the line
                rawRule.index = index++;
                rawRule.mop = nextLine[0];
                rawRule.field = nextLine[1];
                rawRule.op = nextLine[2];
                rawRule.value = nextLine[3];

                System.out.println("Parsed " + rawRule);

                rawRules.add(rawRule);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rawRules;
    }

    public void saveRawRules(String fileName, ArrayList<FilterDlg.RawRule> rawRules) {
        CSVWriter writer = null;

        if (fileName == null)
            return;

        try {
            writer = new CSVWriter(new FileWriter(fileName), ',');
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writer == null)
            return;

        // feed in your array (or convert your data to an array)
        String[] entries = new String[4];

        for (FilterDlg.RawRule rawRule : rawRules) {

            entries[0] = rawRule.mop;
            entries[1] = rawRule.field;
            entries[2] = rawRule.op;
            entries[3] = rawRule.value;

            writer.writeNext(entries);
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String openFileDialog(int openOrSave) {

        FileDialog dialog = new FileDialog(getShell(), openOrSave);
        String[] filterNames = new String[]{"Log Filter Files", "All Files (*)"};
        String[] filterExtensions = new String[]{"*.csv", "*"};
        dialog.setFilterNames(filterNames);
        dialog.setFilterExtensions(filterExtensions);
        // dialog.setFilterPath (filterPath);

        return dialog.open();
    }

    public void loadTabFilter(int index) {

        if (index >= mTabFolder.getItemCount() || index < 0)
            return;

        String fileName = openFileDialog(SWT.OPEN);

        ArrayList<FilterDlg.RawRule> rawRules = loadRawRules(fileName);

        if (rawRules == null)
            return;

        CTabItem it = mTabFolder.getItem(index);
        SlogTabFrame tbf = (SlogTabFrame) it;

        FilterDlg filterDlg = new FilterDlg(getShell(), tbf.getLogView());
        if (filterDlg.open(rawRules) != SWT.OK) {
            return;
        }
        LogFilter filter = filterDlg.getFilter();

        if (tbf instanceof FilterTabFrame) {
            tbf.onUpdateFilter(filterDlg.getFilter());
        } else if (tbf instanceof FileTabFrame) {
            Slogmain.getApp().getMainFrame().openFilterView(filter);
        }

        SystemConfigs.instance().addRecentFilter(filter);
    }

    public void saveTabFilter(int index) {
        CTabItem it;

        if (index >= mTabFolder.getItemCount() || index < 0)
            return;

        it = mTabFolder.getItem(index);
        SlogTabFrame tbf = (SlogTabFrame) it;

        if (tbf instanceof FilterTabFrame) {
            String fileName = openFileDialog(SWT.SAVE);
            saveRawRules(fileName, tbf.getLogView().getLogFilter().getRawRules());
        }
    }

    public void closeRightTabFrames(int index) {
        CTabItem it;

        if (index >= mTabFolder.getItemCount())
            return;

        for (int i = mTabFolder.getItemCount() - 1; i > index; i--) {
            it = mTabFolder.getItem(i);
            SlogTabFrame tbf = (SlogTabFrame) it;
            tbf.onClose();
            it.dispose();
        }
    }

    public void run() {
        mShell.open();
        Display display = getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        SystemConfigs.instance().save();
        display.dispose();
    }
}
