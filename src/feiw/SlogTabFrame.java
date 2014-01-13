package feiw;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.SlogTable;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogItem;
import feiw.LogSource.LogListener;
import feiw.LogSource.LogView;
import feiw.LogSource.StatusListener;

public class SlogTabFrame extends CTabItem implements LogListener, StatusListener{
 
    private SlogTable mTable;
    private LogView mLogView;
    protected LogSource mLogSrc;
    private Label mLineCountLabel;
    private Label mStatusLabel;
    
    public void onClose() {
        mLogSrc.removeLogView(mLogView);
        mLogSrc.removeStatusListener(this);
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
    
    class DropdownSelectionListener extends SelectionAdapter {
        private ToolItem dropdown;

        private Menu menu;

        public DropdownSelectionListener(ToolItem dropdown) {
          this.dropdown = dropdown;
          menu = new Menu(dropdown.getParent().getShell());
        }

        public void add(String item) {
          MenuItem menuItem = new MenuItem(menu, SWT.NONE);
          menuItem.setText(item);
          menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
              MenuItem selected = (MenuItem) event.widget;
            //  dropdown.setText(selected.getText());
              System.out.println(selected.getText() + " Pressed");
            }
          });
        }

        public void widgetSelected(SelectionEvent event) {
          if (!dropdown.getEnabled()) {
              return;
          }
          if (event.detail == SWT.ARROW) {
            ToolItem item = (ToolItem) event.widget;
            Rectangle rect = item.getBounds();
            Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
            menu.setLocation(pt.x, pt.y + rect.height);
            menu.setVisible(true);
          } else {
            System.out.println(dropdown.getText() + " Pressed");
          }
        }
      }
    
    void updateToolItem(ToolItem tit) {
        tit.setEnabled(false);
    }

    void createToolItems(ToolBar tb) {
        ToolItem it = new ToolItem(tb, SWT.PUSH);
       // it.setText("Find");
        it.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                SearchDlg d = new SearchDlg(Slogmain.getApp().getMainFrame().getShell());
                 d.open();
            }
        });
        
        it.setImage(Resources.search_32);
        
        it = new ToolItem(tb, SWT.PUSH);
      //  it.setText("Next");
        it.setImage(Resources.down_32);
    //    ti.addListener(SWT.Selection, onClickSearchNext);
        it.setData("Next");

        it = new ToolItem(tb, SWT.PUSH);
   //     it.setText("Prev");
      //  ti.addListener(SWT.Selection, onClickSearchPrev);
        it.setData("Prev");
        it.setImage(Resources.up_32);


        
        it = new ToolItem(tb, SWT.SEPARATOR);
        it = new ToolItem(tb, SWT.DROP_DOWN);
      //  it.setText("Pause (Running)     ");
        it.setImage(Resources.filter_32);
        //mToolPause.addListener(SWT.Selection, onClickPause);
        it.setData("Filter");
        
        DropdownSelectionListener listenerOne = new DropdownSelectionListener(it);
        listenerOne.add("Option One for One");
        listenerOne.add("Option Two for One");
        listenerOne.add("Option Three for One");
        it.addSelectionListener(listenerOne);
        it.setEnabled(false);
/*

        it.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                
                FilterTabFrame ltab = new FilterTabFrame(getParent(), getText(), SWT.FLAT|SWT.CLOSE|SWT.ICON, mLogSrc, new LogFilter() {
                    @Override
                    public boolean filterLog(LogItem item) {
                        return (item.getLevel() <= 5);
                     }
                    
                });
                
                getParent().setSelection(ltab);
            }
        });
        */
    }
 
    void createToolbar(Composite parent) {
        CoolBar coolbar = new CoolBar(parent, SWT.FLAT);
        ToolBar tb = new ToolBar(coolbar, SWT.FLAT);
        coolbar.setBackground(new Color(getDisplay(), 255,255,255));
        
    
        
        createToolItems(tb);
 
        CoolItem item = new CoolItem(coolbar, SWT.FLAT);
        Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        tb.setSize(p);
        Point p2 = item.computeSize(p.x, p.y);
        item.setControl(tb);
        item.setSize(p2);

        coolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
    }
    public SlogTabFrame(CTabFolder parent, String txt, int style, LogSource logsrc, LogFilter logFilter) {
        super(parent, style);
        setText(txt);
        Composite com = new Composite(parent, style);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        com.setLayout(layout);
        
       //createToolbar(com);

        SlogTable tb = new SlogTable(com, SWT.FLAT);
        tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        mTable = tb;
       
        
        mStatusLabel = new Label(com, SWT.BORDER_SOLID|SWT.ICON);
        mStatusLabel.setImage(logsrc.getStatus() == LogSource.stConnected ? Resources.connected_16 :Resources.disconnected_16);

        mStatusLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        mStatusLabel.setAlignment(SWT.LEFT);
        
        
         Label lb = new Label(com, SWT.SEPARATOR);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
         gd.heightHint = 16;
         lb.setLayoutData(gd);
        
         mLineCountLabel = new Label(com, SWT.BORDER);
         mLineCountLabel.setText("0 lines of log");
         mLineCountLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        
        setControl(com);
        mLogSrc = logsrc;
        mLogView = mLogSrc.newLogView(this, logFilter);
        mLogSrc.addStatusListener(this);
        mTable.setLogView(mLogView);
        
//        mTable.addSelectionListener()
 
    }
    @Override
    public void onLogChanged() {
        Display display = getDisplay();
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (mTable.isDisposed())
                    return;
                if (!mLogView.getChangedFlag())
                    return;
                
                int cnt = mLogView.size(); 
                int cnto = mTable.getItemCount();
                
                //System.out.println("log changed old cnt = " + cnto + " new cnt = " + cnt);
                mTable.setItemCount(0);
                mTable.setRedraw(true);
                mTable.setItemCount(cnt);
                mLogView.setChangeFlag(false);
                
                //mLogger.resetChangeFlag();
                if (cnto != cnt) {
                    mTable.setTopIndex(cnt - 2);
                    mLineCountLabel.setText("" + cnt + " lines of log");
                    mLineCountLabel.update();
                   // String txt = "" + cnt + " lines of log";
                   // mStatusLabelLogs.setText(txt);
                  //  mStatusLabelLogs.update();
                }
            }
        });
    }
    @Override
    public void onStatusChanged(int oldStatus, int newStatus) {
        if (getDisplay().isDisposed() || mStatusLabel.isDisposed()) {
            return;
        }
        final Image img ;
        switch(newStatus) {
        case LogSource.stIdle:
            img = Resources.disconnected_16;
            break;
        case LogSource.stConnecting:
            img = Resources.disconnected_16;
            break;
        case LogSource.stConnected:
            img = Resources.connected_16;
            break;
         default:
             img = Resources.connected_16;
                break;
        }
       
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!mStatusLabel.isDisposed()) {
                    mStatusLabel.setImage(img);
                    mStatusLabel.update();
                }
            }
           }
       );
       
    }
    
}
