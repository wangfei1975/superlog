package qnx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.swt.widgets.SlogTable;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import qnx.LogSource.LogListener;
import qnx.LogSource.LogView;

public final class SlogTabFrame extends CTabItem implements LogListener{
 
    private SlogTable mTable;
    private LogView mLogView;
    private LogSource mLogSrc;
   
    
    public LogSource getLogSource() {
        return mLogSrc;
    }
    public SlogTable getTable() {
        return mTable;
    }
 
    void createToolbar(Composite parent) {
        CoolBar coolbar = new CoolBar(parent, SWT.FLAT);
        ToolBar tb = new ToolBar(coolbar, SWT.FLAT);
        
        ToolItem it = new ToolItem(tb, SWT.PUSH);
        it.setText("Find ...");
        it.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                SearchDlg d = new SearchDlg(Slogmain.getApp().getMainFrame().getShell());
                 d.open();
            }
        });
 
        CoolItem item = new CoolItem(coolbar, SWT.FLAT);
        Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        tb.setSize(p);
        Point p2 = item.computeSize(p.x, p.y);
        item.setControl(tb);
        item.setSize(p2);
   
    }
    public SlogTabFrame(CTabFolder parent, String txt, int style, LogSource logsrc) {
        super(parent, style);
        setText(txt);
        
        setImage(Resources.iconSearch);
        Composite com = new Composite(parent, style);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        com.setLayout(layout);
        // TODO Auto-generated constructor stub
        createToolbar(com);

        GridData gridData = new GridData();
        gridData.horizontalSpan = 4;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        SlogTable tb = new SlogTable(com, SWT.FLAT);
        mTable = tb;
      //  tb.setText("fdsafdsafdsafhello");
        tb.setLayoutData(gridData);
        
        Label lb = new Label(com, SWT.BORDER);
        lb.setText("0 lines of log");
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        // gridData.grabExcessHorizontalSpace = true;
        lb.setLayoutData(gridData);
        
        setControl(com);
        mLogSrc = logsrc;
        mLogView = mLogSrc.newLogView(this, null);
        mTable.setLogView(mLogView);
 
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
                   // String txt = "" + cnt + " lines of log";
                   // mStatusLabelLogs.setText(txt);
                  //  mStatusLabelLogs.update();
                }
            }
        });
    }
    
}
