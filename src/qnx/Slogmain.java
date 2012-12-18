package qnx;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

public class Slogmain {

    Table table;
    SlogInfo logger;
    Display display = null;
    Shell shell = null;
    Label lineStatus = null;
    Label conStatus = null;
    ToolItem toolConnect = null;
    ToolItem toolDisconnect = null;
    ToolItem toolPause = null;
    MenuItem menuConnect = null;
    MenuItem menuDisconnect = null;

    public Slogmain() {

    }

    /**
     * @param args
     * @throws IOException
     */

    Shell getShell() {
        return shell;
    }

    Listener searchListener = new Listener() {
        public void handleEvent(Event event) {
            SearchDlg d = new SearchDlg(shell);
            String input = d.open();
            if (input != null) {
                input.trim();
                if (!input.isEmpty()) {
                    int flag = 0;
                    if (d.isCaseSenstive()) {
                        flag |= SlogInfo.SearchCtx.FLAG_CASE_SENSITIVE;
                    }
                    logger.searchMarkall(input, flag);
                }
            }
        }
    };
    Listener searchNextListener = new Listener() {
        public void handleEvent(Event event) {
            if (table == null || table.isDisposed())
                return;
            int next = logger.searchNext();
            if (next >= 0) {
                table.setSelection(next);

            }
        }
    };
    Listener searchPrevListener = new Listener() {
        public void handleEvent(Event event) {
            if (table == null || table.isDisposed())
                return;
            int prev = logger.searchPrev();
            if (prev >= 0) {
                table.setSelection(prev);

            }
        }
    };

    Listener pauseListener = new Listener() {
        public void handleEvent(Event event) {
            if (table == null || table.isDisposed())
                return;
            logger.pause();

            if (logger.isPaused()) {
                toolPause.setText("Resume (Paused)");
            } else {
                toolPause.setText("Pause (Running)");
            }
            // toolPause.setData("hello");
        }
    };

    void copyLog(int startColum) {

        if (table == null || table.isDisposed())
            return;
        Clipboard cb = new Clipboard(display);
        TableItem[] items = table.getSelection();

        if (items != null && items.length > 0) {
            String txt = "";
            for (int i = 0; i < items.length; i++) {
                String line = "";
                for (int c = startColum; c < table.getColumnCount(); c++) {
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

    Listener copyLogvListener = new Listener() {
        public void handleEvent(Event event) {
            copyLog(1);
        }
    };
    Listener copyLogListener = new Listener() {
        public void handleEvent(Event event) {
            copyLog(6);
        }
    };

    Listener clearLogsListener = new Listener() {
        public void handleEvent(Event event) {
            logger.clearLogs();
        }
    };
    Listener connectListener = new Listener() {
        public void handleEvent(Event event) {
            if (event.widget == menuConnect || event.widget == toolConnect) {
                ConnectDlg dlg = new ConnectDlg(shell, logger.getServerIp(), logger.getServerPort());
                if (dlg.open() == 1) {
                    logger.connect(dlg.getIp(), dlg.getPort(), logListener);
                }
            } else {
                logger.disconnect();
            }
        }
    };

    SlogInfo.LogListener logListener = new SlogInfo.LogListener() {
        @Override
        public void handleLogs() {
            if (table == null || table.isDisposed())
                return;
            Display display = getShell().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (table.isDisposed())
                        return;
                    if (!logger.isDataChanged())
                        return;
                    if (logger.isPaused())
                        return;
                    int cnt = logger.getDataSize();
                    table.setRedraw(true);

                    int cntn = table.getItemCount();
                    table.setItemCount(0);
                    table.setItemCount(cnt);
                    logger.resetChangeFlag();
                    if (cntn != cnt) {
                        table.setTopIndex(cnt - 2);
                        String txt = "" + cnt + " lines of log";
                        lineStatus.setText(txt);
                        lineStatus.update();
                    }
                }
            });

        }

        @Override
        public void handleSearchResult() {
            if (table == null || table.isDisposed())
                return;
            Display display = getShell().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (table.isDisposed())
                        return;
                    if (!logger.isDataChanged())
                        return;
                    int cnt = logger.getDataSize();
                    table.setRedraw(true);

                    int cntn = table.getItemCount();
                    table.setItemCount(0);
                    table.setItemCount(cnt);
                    logger.resetChangeFlag();
                    conStatus.setText(logger.getSearchCtx().resultcount + " results found");
                    if (cntn != cnt) {
                        table.setTopIndex(cnt - 2);
                        String txt = "" + cnt + " lines of log";
                        lineStatus.setText(txt);
                        lineStatus.update();
                    } else {
                        int curres = logger.getSearchCtx().curresult;
                        if (curres >= 0) {
                            table.setTopIndex(curres);
                            table.setSelection(curres);
                        }
                    }
                }
            });

        }

        @Override
        public void handleStatusChanged(final boolean connected) {
            // TODO Auto-generated method stub
            if (conStatus == null || conStatus.isDisposed()) {
                return;
            }
            Display display = getShell().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    toolConnect.setEnabled(!connected);
                    menuConnect.setEnabled(!connected);
                    menuDisconnect.setEnabled(connected);
                    toolDisconnect.setEnabled(connected);
                    shell.setText("QSLOG - " + getConnectStatus());
                }
            });
        }
    };

    String getConnectStatus() {
        if (logger.isConnected()) {
            return "Connected to:" + logger.serverIp + ":" + logger.serverPort;
        } else {
            return "Disconected";
        }
    }

    Image iconSearch = null;

    void createToolbar() {
        CoolBar composite = new CoolBar(shell, SWT.NONE);
        {
            CoolItem item = new CoolItem(composite, SWT.NONE);
            ToolBar tb = new ToolBar(composite, SWT.FLAT);

            toolConnect = new ToolItem(tb, SWT.PUSH);
            toolConnect.setText("Connect ...");
            toolConnect.addListener(SWT.Selection, connectListener);

            toolDisconnect = new ToolItem(tb, SWT.PUSH);
            toolDisconnect.setText("Disconnect");
            toolDisconnect.addListener(SWT.Selection, connectListener);

            ToolItem ti = new ToolItem(tb, SWT.SEPARATOR);

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Clean Logs");
            ti.addListener(SWT.Selection, clearLogsListener);

            ti = new ToolItem(tb, SWT.SEPARATOR);

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Find ...");
            ti.addListener(SWT.Selection, searchListener);
            ti.setData("Find");

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Next");
            ti.addListener(SWT.Selection, searchNextListener);
            ti.setData("Next");

            ti = new ToolItem(tb, SWT.PUSH);
            ti.setText("Prev");
            ti.addListener(SWT.Selection, searchPrevListener);
            ti.setData("Prev");

            ti = new ToolItem(tb, SWT.SEPARATOR);
            toolPause = new ToolItem(tb, SWT.PUSH);
            toolPause.setText("Pause (Running)     ");
            toolPause.addListener(SWT.Selection, pauseListener);
            toolPause.setData("Pause");

            Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            tb.setSize(p);
            Point p2 = item.computeSize(p.x, p.y);
            item.setControl(tb);
            item.setSize(p2);
        }
    }

    void createMenus() {
        Menu m = new Menu(shell, SWT.BAR);
        MenuItem mi = new MenuItem(m, SWT.CASCADE);
        mi.setText("&QLog");

        Menu qlogm = new Menu(mi);
        mi.setMenu(qlogm);

        mi = new MenuItem(m, SWT.CASCADE);
        mi.setText("&Edit");
        Menu editmenu = new Menu(mi);
        mi.setMenu(editmenu);

        menuConnect = new MenuItem(qlogm, SWT.CASCADE);
        menuConnect.setText("&Connect ...");
        menuConnect.setAccelerator(SWT.COMMAND | 'N');
        menuConnect.addListener(SWT.Selection, connectListener);

        menuDisconnect = new MenuItem(qlogm, SWT.CASCADE);
        menuDisconnect.setText("&Disconnect");
        menuDisconnect.setAccelerator(SWT.COMMAND | 'D');
        menuDisconnect.addListener(SWT.Selection, connectListener);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Clear Logs");
        mi.setAccelerator(SWT.COMMAND | 'R');
        mi.addListener(SWT.Selection, clearLogsListener);

        mi = new MenuItem(editmenu, SWT.SEPARATOR);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Find ...");
        mi.setAccelerator(SWT.COMMAND | 'F');
        mi.addListener(SWT.Selection, searchListener);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Next");
        mi.setAccelerator(SWT.SHIFT | SWT.COMMAND | 'N');
        mi.addListener(SWT.Selection, searchNextListener);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Prev");
        mi.setAccelerator(SWT.SHIFT | SWT.COMMAND | 'P');
        mi.addListener(SWT.Selection, searchPrevListener);

        mi = new MenuItem(editmenu, SWT.SEPARATOR);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Copy Selection(All Colums)");
        mi.setAccelerator(SWT.COMMAND | 'V');
        mi.addListener(SWT.Selection, copyLogvListener);

        mi = new MenuItem(editmenu, SWT.CASCADE);
        mi.setText("&Copy Selection");
        mi.setAccelerator(SWT.COMMAND | 'C');
        mi.addListener(SWT.Selection, copyLogListener);

        popupMenu = new Menu(shell, SWT.POP_UP);
        MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
        item.setText("&Copy Selection(All Colums)");
        item.setAccelerator(SWT.COMMAND | 'V');
        item.addListener(SWT.Selection, copyLogvListener);

        item = new MenuItem(popupMenu, SWT.PUSH);
        item.setText("&Copy Selection");
        item.setAccelerator(SWT.COMMAND | 'C');
        item.addListener(SWT.Selection, copyLogListener);

        shell.setMenuBar(m);
    }

    Menu popupMenu = null;

    protected Control createContents() {

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        shell.setLayout(layout);
        createToolbar();

        createMenus();

        table = new Table(shell, SWT.BORDER | SWT.VIRTUAL | SWT.MULTI);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 4;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        table.setLayoutData(gridData);
        table.setFocus();

        String[] title = { "Flag", "Line", "Time", "Sev", "Major", "Minor", "Args" };
        int[] width = { 28, 70, 155, 30, 50, 50, 1000 };

        for (int i = 0; i < title.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title[i]);
            column.setWidth(width[i]);
        }

        table.addListener(SWT.SetData, new Listener() {
            public void handleEvent(Event e) {
                Display display = getShell().getDisplay();

                TableItem item = (TableItem) e.item;
                int index = table.indexOf(item);
                SlogInfo.LogItem log = logger.getData(index);
                if (log == null)
                    return;

                item.setText(1, "" + index);
                for (int i = 0; i < log.getTextCount(); i++) {
                    item.setText(i + 2, log.getText(i) == null ? "" : log.getText(i));
                }

                if (log.getText(1) != null && !log.getText(1).isEmpty()) {
                    char ser = log.getText(1).charAt(0);
                    switch (ser) {
                    case '0':
                        item.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
                        item.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
                        break;
                    case '1':
                        item.setBackground(display.getSystemColor(SWT.COLOR_DARK_RED));
                        item.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
                        break;
                    case '2':
                        item.setBackground(display.getSystemColor(SWT.COLOR_RED));
                        item.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
                        break;
                    case '3':
                        item.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
                        item.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
                        // item.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
                        // //Warning
                        break;
                    case '4':
                        // item.setForeground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
                        // //Notice
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
                        break;
                    case '5':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN)); // Info
                        break;
                    case '6':
                        item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                        break;
                    case '7':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
                        break;

                    default:
                        item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                        break;
                    }
                }
                if (log.getSearchMarker() != 0) {
                    item.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                    item.setImage(iconSearch);
                }

            }
        });

        Font f = table.getFont();
        FontData[] fontData = f.getFontData();

        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(18);
        }
        Display display = getShell().getDisplay();
        // Font font = new Font(display, fontData);
        Font font = new Font(display, "Monaco", 14, 0);
        table.setFont(font);

        lineStatus = new Label(shell, SWT.BORDER);
        lineStatus.setText("0 lines of log");
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        // gridData.grabExcessHorizontalSpace = true;
        lineStatus.setLayoutData(gridData);

        Label labelsep = new Label(shell, SWT.SHADOW_IN | SWT.BORDER);
        labelsep.setText("|");

        conStatus = new Label(shell, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        conStatus.setLayoutData(gridData);

        table.setMenu(popupMenu);

        shell.setSize(1200, 800);

        return shell;

    }

    public void run() {
        // Don't return from open() until window closes
        // setBlockOnOpen(true);

        display = new Display();
        shell = new Shell(display);

        shell.setText("QSLOG");

        InputStream is = getClass().getClassLoader().getResourceAsStream("search.png");
        if (is == null) {
            iconSearch = new Image(display, "resources/search.png");
        } else {
            iconSearch = new Image(display, is);
        }

        logger = new SlogInfo();

        // Open the main window
        createContents();
        shell.open();
        logger.connect("10.222.96.245", 8000, logListener);
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();

        logger.disconnect();
    }

    public static void main(String[] args) {
        new qnx.Slogmain().run();

    }

}
