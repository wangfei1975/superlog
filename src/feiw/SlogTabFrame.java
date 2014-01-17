package feiw;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogListener;
import feiw.LogSource.LogParser;
import feiw.LogSource.LogView;
import feiw.LogSource.StatusListener;

public class SlogTabFrame extends CTabItem implements LogListener{
 
    private SlogTable mTable;
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

    void updateToolItem(ToolItem tit) {
        String tn = (String)tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        } 
        if (tn.equals(ToolBarDes.TN_COPY) || tn.equals(ToolBarDes.TN_COPYALL)) {
          tit.setEnabled(mTable.getSelectionCount() > 0);    
        } else if (tn.equals(ToolBarDes.TN_NEXT) || tn.equals(ToolBarDes.TN_PREV)) {
            tit.setEnabled(mLogView.getSearchResults() > 0);
        }
    }
    
    public SlogTabFrame(CTabFolder parent, String txt, int style, LogSource logsrc, LogFilter logFilter, LogView parentLogView) {
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
       
        
  //      mStatusLabel = new Label(com, SWT.BORDER_SOLID|SWT.ICON);
//        mStatusLabel.setImage(logsrc.getStatus() == LogSource.stConnected ? Resources.connected_16 :Resources.disconnected_16);

    //    mStatusLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
    //    mStatusLabel.setAlignment(SWT.LEFT);
        
        
         mLineCountLabel = new Label(com, SWT.BORDER);
         mLineCountLabel.setText("0 lines of log          ");
         mLineCountLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
         
         Label lb = new Label(com, SWT.SEPARATOR);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
         gd.heightHint = 16;
         lb.setLayoutData(gd);
        
         mSearchResult = new Label(com, SWT.BORDER);
         mSearchResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        
        setControl(com);
        mLogSrc = logsrc;
        mLogView = mLogSrc.newLogView(this, logFilter, parentLogView);
        mTable.setLogView(mLogView);
        
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
        
        com.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
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

 
    }
    int mLastSearchResults = -1;
    boolean mVisible = false;
    private void updateSearchUI() {
        int nresults = mLogView.getSearchResults();
        //System.out.println("nresults = " + nresults + " last results = " + mLastSearchResults);
        if (nresults != mLastSearchResults && nresults >= 0) {
            mSearchResult.setText("Found " + nresults + " results of \"" + mLogView.getSearchPattern() + "\"");
            mLastSearchResults  = nresults;
        }
    }
    private void updateLogUI() {
        if (!mVisible||mTable.isDisposed() || !mTable.isVisible())
            return;
        
        if (!mLogView.getChangedFlag() ||  mLogView.isPaused())
            return;
        
        final int cnt = mLogView.size(); 
        final int cnto = mTable.getItemCount();
        
        //System.out.println("log changed old cnt = " + cnto + " new cnt = " + cnt);
        mTable.setItemCount(0);
        mTable.setRedraw(true);
        mTable.setItemCount(cnt);
        mLogView.setChangeFlag(false);

        if (cnto != cnt) {
            mTable.setTopIndex(cnt - 2);
            mLineCountLabel.setText("" + cnt + " lines of log");
            updateSearchUI();
        }
    }
    

    @Override
    public void onLogChanged() {
 
        if (mVisible) {
        Display display = getDisplay();
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
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
        int n =  mLogView.getNextSearchResult(sel+1);
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
        int n =  mLogView.getPrevSearchResult(sel-1);
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
                txt.append(LogParser.parseContent(l));
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
    private int getTableVisibleCount() {
        Rectangle rect = mTable.getClientArea ();
        int itemHeight = mTable.getItemHeight ();
        int headerHeight = mTable.getHeaderHeight ();
        return (rect.height - headerHeight - itemHeight - 1) / itemHeight;
    }
    
    public void onSearch(String txt, boolean caseSensitive) {
        mLogView.search(txt, caseSensitive);
    }
    @Override
    public void onSearchResult() {
 //       Display display = getDisplay();
     //   display.asyncExec(new Runnable() {
       //     @Override
        //    public void run() {
                if (mTable.isDisposed() || !mTable.isVisible())
                    return;
                if (!mLogView.getChangedFlag())
                    return;
                
                int top = mTable.getTopIndex();

                int nresults = mLogView.getSearchResults();
                if (nresults == 0) {
                    mTable.setItemCount(0);
                    mTable.setRedraw(true);
                    mTable.setItemCount(mLogView.size());
                    mLogView.setChangeFlag(false);
                    mTable.setTopIndex(top);

                } else {
                    int first = mLogView.getNextSearchResult(0);

                    if (first >= 0) {
                        mTable.setItemCount(0);
                        mTable.setRedraw(true);
                        mTable.setItemCount(mLogView.size());
                        mLogView.setChangeFlag(false);
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
                
         //   }
       // });
    }
    
}
