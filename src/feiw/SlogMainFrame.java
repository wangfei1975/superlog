package feiw;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public final class SlogMainFrame {
    
    private Display mDisplay;
    private Shell   mShell;
    
    //private ArrayList <LogSource> mLogSources = new ArrayList <LogSource>(10);
  //  private ArrayList <SlogTabFrame> mLogTabs = new  ArrayList <SlogTabFrame>(10);
    
    Label mStatusLabelLogs = null;
    Label mStatusLabelConnection = null;
    ToolItem mToolConnect = null;
    ToolItem mToolDisconnect = null;
    ToolItem mToolPause = null;
    MenuItem mMenuConnect = null;
    MenuItem mMenuDisconnect = null;

    Table mMainTable;
//    SlogTabView mMainTableView;
    
    Menu mPopupMenu = null;
    CTabFolder mTabFolder;
    
    public Display getDisplay() {
        return mDisplay;
    }
    public Shell getShell() {
        return mShell;
    }
    
    public SlogMainFrame(String caption, Display disp) {
        mDisplay = disp;
        mShell = new Shell(disp);
        mShell.setText(caption);
        createContents();
        mShell.addListener(SWT.Close, new Listener() {
           public void handleEvent(Event event) {
               closeTabFrames();
           }
         });
    }
    
    void createToolbar() {
            CoolBar coolbar = new CoolBar(getShell(), SWT.FLAT);
            ToolBar tb = new ToolBar(coolbar, SWT.FLAT);
            coolbar.setBackground(new Color(getDisplay(), 255,255,255));

            coolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
            mToolConnect = new ToolItem(tb, SWT.PUSH);
         //   mToolConnect.setText("Connect");
            mToolConnect.addListener(SWT.Selection, onClickConnect);
            mToolConnect.setImage(Resources.iconOpenDevice);
            mToolConnect.setToolTipText("Connect to a QCONN device");

            //new ToolItem(tb, SWT.SEPARATOR_FILL);
            new ToolItem(tb, SWT.SEPARATOR);
            ToolItem t = new ToolItem(tb, SWT.PUSH);
            
          //  t.setText("Open");
            t.setImage(Resources.iconOpenFile);
           
            /*
            mToolDisconnect = new ToolItem(tb, SWT.PUSH);
            mToolDisconnect.setText("Disconnect");
            mToolDisconnect.addListener(SWT.Selection, onClickConnect);

            ToolItem ti = new ToolItem(tb, SWT.SEPARATOR);

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Clean Logs");
            ti.addListener(SWT.Selection, onClickClearLogs);


            ti = new ToolItem(tb, SWT.SEPARATOR);

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Find ...");
            ti.addListener(SWT.Selection, onClickSearch);
            ti.setData("Find");

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Next");
            ti.addListener(SWT.Selection, onClickSearchNext);
            ti.setData("Next");

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Prev");
            ti.addListener(SWT.Selection, onClickSearchPrev);
            ti.setData("Prev");

            ti = new ToolItem(tb, SWT.SEPARATOR);
            mToolPause = new ToolItem(tb, SWT.PUSH);
            mToolPause.setText("Pause (Running)     ");
            mToolPause.addListener(SWT.Selection, onClickPause);
            mToolPause.setData("Pause");
 */
            CoolItem item = new CoolItem(coolbar, SWT.NONE);
            Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            tb.setSize(p);
            Point p2 = item.computeSize(p.x, p.y);
            item.setControl(tb);
            item.setSize(p2);
     
    }
 
    Listener onClickSearch = new Listener() {
        public void handleEvent(Event event) { /*
            SearchDlg d = new SearchDlg(getShell());
            String input = d.open();
            if (input != null) {
                input.trim();
                if (!input.isEmpty()) {
                    int flag = 0;
                    if (d.isCaseSenstive()) {
                        flag |= SlogInfo.SearchCtx.FLAG_CASE_SENSITIVE;
                    }
                    mLogger.searchMarkall(input, flag);
                }
            }
        */}
    };
    Listener onClickSearchNext = new Listener() {
        public void handleEvent(Event event) { /*
            if (mMainTable == null || mMainTable.isDisposed())
                return;
            int next = mLogger.searchNext();
            if (next >= 0) {
                mMainTable.setSelection(next);

            }
       */ }
    };
    Listener onClickSearchPrev = new Listener() {
        public void handleEvent(Event event) { /*
            if (mMainTable == null || mMainTable.isDisposed())
                return;
            int prev = mLogger.searchPrev();
            if (prev >= 0) {
                mMainTable.setSelection(prev);

            }
        */}
    };

    Listener onClickPause = new Listener() {
        public void handleEvent(Event event) { /*
            if (mMainTable == null || mMainTable.isDisposed())
                return;
            mLogger.pause();

            if (mLogger.isPaused()) {
                mToolPause.setText("Resume (Paused)");
            } else {
                mToolPause.setText("Pause (Running)");
            }
        */}
    };

    void copyLog(int startColum) {

        if (mMainTable == null || mMainTable.isDisposed())
            return;
        Clipboard cb = new Clipboard(getDisplay());
        TableItem[] items = mMainTable.getSelection();

        if (items != null && items.length > 0) {
            String txt = "";
            for (int i = 0; i < items.length; i++) {
                String line = "";
                for (int c = startColum; c < mMainTable.getColumnCount(); c++) {
                    line += " " + items[i].getText(c);
                }
                txt += line;
                if (i < items.length) {
                    txt += "\n";
                }
            }

            cb.setContents(new Object[] { txt }, new Transfer[] { TextTransfer.getInstance() });
        }
    }

    Listener onClickCopyLogv = new Listener() {
        public void handleEvent(Event event) {
            copyLog(1);
        }
    };
    Listener onClickCopyLog = new Listener() {
        public void handleEvent(Event event) {
            copyLog(6);
        }
    };

    Listener onClickClearLogs = new Listener() {
        public void handleEvent(Event event) {
         //   mLogger.clearLogs();
        }
    };
    Listener onClickConnect = new Listener() {
        public void handleEvent(Event event) { 
            if (event.widget == mToolConnect || event.widget == mMenuConnect ) {
                SystemConfigs.LogUrl lu = Slogmain.getApp().getConfigs().getLastLogUrl();
                ConnectDlg dlg = new ConnectDlg(getShell(), lu.url, lu.port);
                if (dlg.open() == 1) {
                    LogSource ls = new QconnLogSource(dlg.getIp(), dlg.getPort());
                    SlogTabFrame ltab = new SlogTabFrame(mTabFolder, lu.toString(), SWT.FLAT|SWT.CLOSE|SWT.ICON, ls, null);
                    ltab.setImage(Resources.iconOpenDevice16);
                    mTabFolder.setSelection(ltab);
                }
            } else {
             //   mLogger.disconnect();
            }
         }
    };
    /*
    SlogInfo.LogListener logListener = new SlogInfo.LogListener() {
        @Override
        public void handleLogs() {
            if (mMainTable == null || mMainTable.isDisposed())
                return;
            Display display = getShell().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (mMainTable.isDisposed())
                        return;
                    if (!mLogger.isDataChanged())
                        return;
                    if (mLogger.isPaused())
                        return;
                    int cnt = mLogger.getDataSize();
                    mMainTable.setRedraw(true);

                    int cntn = mMainTable.getItemCount();
                    mMainTable.setItemCount(0);
                    mMainTable.setItemCount(cnt);
                    mLogger.resetChangeFlag();
                    if (cntn != cnt) {
                        mMainTable.setTopIndex(cnt - 2);
                        String txt = "" + cnt + " lines of log";
                        mStatusLabelLogs.setText(txt);
                        mStatusLabelLogs.update();
                    }
                }
            });

        }

        @Override
        public void handleSearchResult() {
            if (mMainTable == null || mMainTable.isDisposed())
                return;
            Display display = getShell().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (mMainTable.isDisposed())
                        return;
                    if (!mLogger.isDataChanged())
                        return;
                    int cnt = mLogger.getDataSize();
                    mMainTable.setRedraw(true);

                    int cntn = mMainTable.getItemCount();
                    mMainTable.setItemCount(0);
                    mMainTable.setItemCount(cnt);
                    mLogger.resetChangeFlag();
                    mStatusLabelConnection.setText(mLogger.getSearchCtx().resultcount + " results found");
                    if (cntn != cnt) {
                        mMainTable.setTopIndex(cnt - 2);
                        String txt = "" + cnt + " lines of log";
                        mStatusLabelLogs.setText(txt);
                        mStatusLabelLogs.update();
                    } else {
                        int curres = mLogger.getSearchCtx().curresult;
                        if (curres >= 0) {
                            mMainTable.setTopIndex(curres);
                            mMainTable.setSelection(curres);
                        }
                    }
                }
            });

        }

        @Override
        public void handleStatusChanged(final boolean connected) {
            // TODO Auto-generated method stub
            if (mStatusLabelConnection == null || mStatusLabelConnection.isDisposed()) {
                return;
            }
            Display display = getShell().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    mToolConnect.setEnabled(!connected);
                    mMenuConnect.setEnabled(!connected);
                    mMenuDisconnect.setEnabled(connected);
                    mToolDisconnect.setEnabled(connected);
                    getShell().setText("QSLOG - " + getConnectStatus());
                }
            });
        }
    };

    String getConnectStatus() {
        if (mLogger.isConnected()) {
            return "Connected to:" + mLogger.serverIp + ":" + mLogger.serverPort;
        } else {
            return "Disconected";
        }
    }
   */

    void createMenus() {
        Menu m = new Menu(mShell, SWT.BAR);
        MenuItem mi = new MenuItem(m, SWT.CASCADE);
        mi.setText("&QLog");

        Menu qlogm = new Menu(mi);
        mi.setMenu(qlogm);

        mi = new MenuItem(m, SWT.CASCADE);
        mi.setText("&Edit");
        Menu editmenu = new Menu(mi);
        mi.setMenu(editmenu);

        mMenuConnect = new MenuItem(qlogm, SWT.CASCADE);
        mMenuConnect.setText("&Connect ...");
        mMenuConnect.setAccelerator(SWT.COMMAND | 'N');
        mMenuConnect.addListener(SWT.Selection, onClickConnect);

        mMenuDisconnect = new MenuItem(qlogm, SWT.CASCADE);
        mMenuDisconnect.setText("&Disconnect");
        mMenuDisconnect.setAccelerator(SWT.COMMAND | 'D');
        mMenuDisconnect.addListener(SWT.Selection, onClickConnect);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Clear Logs");
        mi.setAccelerator(SWT.COMMAND | 'R');
        mi.addListener(SWT.Selection, onClickClearLogs);

        mi = new MenuItem(editmenu, SWT.SEPARATOR);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Find ...");
        mi.setAccelerator(SWT.COMMAND | 'F');
        mi.addListener(SWT.Selection, onClickSearch);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Next");
        mi.setAccelerator(SWT.SHIFT | SWT.COMMAND | 'N');
        mi.addListener(SWT.Selection, onClickSearchNext);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Prev");
        mi.setAccelerator(SWT.SHIFT | SWT.COMMAND | 'P');
        mi.addListener(SWT.Selection, onClickSearchPrev);

        mi = new MenuItem(editmenu, SWT.SEPARATOR);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Copy Selection(All Colums)");
        mi.setAccelerator(SWT.COMMAND | 'V');
        mi.addListener(SWT.Selection, onClickCopyLogv);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Copy Selection");
        mi.setAccelerator(SWT.COMMAND | 'C');
        mi.addListener(SWT.Selection, onClickCopyLog);

        mPopupMenu = new Menu(getShell(), SWT.POP_UP);
        MenuItem item = new MenuItem(mPopupMenu, SWT.PUSH);
        item.setText("&Copy Selection(All Colums)");
        item.setAccelerator(SWT.COMMAND | 'V');
        item.addListener(SWT.Selection, onClickCopyLogv);

        item = new MenuItem(mPopupMenu, SWT.PUSH);
        item.setText("&Copy Selection");
        item.setAccelerator(SWT.COMMAND | 'C');
        item.addListener(SWT.Selection, onClickCopyLog);

        getShell().setMenuBar(m);
    }

    protected Control createContents() {

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        getShell().setLayout(layout);
        createToolbar();

        createMenus();

        mTabFolder = new CTabFolder(getShell(), SWT.BORDER);
        mTabFolder.setSimple(false);
        mTabFolder.setUnselectedCloseVisible(true);
        mTabFolder.setUnselectedImageVisible(true);
        
        mTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        
        mTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void close(CTabFolderEvent event) {
                if (event.item instanceof SlogTabFrame) {
                    SlogTabFrame it =  (SlogTabFrame)event.item;
                    it.onClose();
                }
            }
        });

        /*
        mMainTableView = new SlogMainTabView(mTabFolder, SWT.FLAT);
        mMainTableView.getTableFrame().setFocus();
        mTabFolder.setSelection(mMainTableView.getTabItem());
        */
 /*
        new SlogTabFrame(mTabFolder, "file://abcddfg.log", SWT.FLAT|SWT.CLOSE|SWT.ICON);
        new SlogTabFrame(mTabFolder, "qcon://192.168.0.1", SWT.FLAT|SWT.CLOSE|SWT.ICON);
        mTabFolder.setSelection(mTabFolder.getItem(0));
        */
       // mMainTable.setFocus();
        /*
        mStatusLabelLogs = new Label(getShell(), SWT.BORDER);
        mStatusLabelLogs.setText("0 lines of log");
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        // gridData.grabExcessHorizontalSpace = true;
        mStatusLabelLogs.setLayoutData(gridData);

        Label labelsep = new Label(getShell(), SWT.SHADOW_IN | SWT.BORDER);
        labelsep.setText("|");

        mStatusLabelConnection = new Label(getShell(), SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        mStatusLabelConnection.setLayoutData(gridData);

      
 */
        getShell().setSize(1200, 800);

        return getShell();

    }

    public void closeTabFrames() {
        for(CTabItem it : mTabFolder.getItems()) {
            SlogTabFrame tbf = (SlogTabFrame) it;
            tbf.onClose();
        }
    }
    public void run() {
        mShell.open();
        Display display = getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
