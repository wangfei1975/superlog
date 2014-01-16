package feiw;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
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

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogItem;
import feiw.SlogTabFrame.DropdownSelectionListener;
import feiw.ToolBarDes.ToolItemDes;

public final class SlogMainFrame {
    

    private Display mDisplay;
    private Shell   mShell;

    
    CoolBar mCoolBar = null;    
    Map<String, ToolItem> mToolItems = new HashMap<String, ToolItem>(10);
    CTabFolder mTabFolder;
    
    public Display getDisplay() {
        return mDisplay;
    }
    public Shell getShell() {
        return mShell;
    }
    
    ToolItem getToolItem(String name) {
        return mToolItems.get(name);
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

    void createToolBar(ToolBarDes tbdes) {
        ToolBar tb = new ToolBar(mCoolBar, SWT.FLAT);
        tb.setData(tbdes.mName);
        for (ToolItemDes itdes : tbdes.mItems) {
            ToolItem it = new ToolItem(tb, itdes.mStyle);
            it.setData(itdes.mName);
            it.setData("KeyAccelerator", new Integer(itdes.mKeyAccelerator));
            it.setToolTipText(itdes.mTipText);
            it.setImage(itdes.mImage);
            it.setDisabledImage(new Image(getDisplay(), itdes.mImage, SWT.IMAGE_GRAY));
            mToolItems.put(itdes.mName, it);
        }
        CoolItem item = new CoolItem(mCoolBar, SWT.NONE);
        Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        tb.setSize(p);
        Point p2 = item.computeSize(p.x, p.y);
        item.setControl(tb);
        item.setSize(p2);
        
    }
    
    void createToolBars() {
        mCoolBar = new CoolBar(getShell(), SWT.FLAT);
        mCoolBar.setBackground(new Color(getDisplay(), 255,255,255));
        mCoolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
        for (ToolBarDes tdes : ToolBarDes.TOOBARS) {
            createToolBar(tdes);
        }

        mToolItems.get(ToolBarDes.TN_NEXT).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
             
                    SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.onNext();
                    }
            
            }
           
        });
        
        mToolItems.get(ToolBarDes.TN_PREV).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                    SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.onPrev();
                    }
            }
           
        });
        
        
        mToolItems.get(ToolBarDes.TN_SEARCH).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SearchDlg d = new SearchDlg(Slogmain.getApp().getMainFrame().getShell());
                String txt = d.open();
                if (txt != null && !(txt.trim().isEmpty())) {
                    SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                    if (tbf != null) {
                        tbf.onSearch(txt.trim(),  d.isCaseSenstive());
                    }
                }
            }
           
        });
        
        mToolItems.get(ToolBarDes.TN_CONNECT).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SystemConfigs.LogUrl lu = Slogmain.getApp().getConfigs().getLastLogUrl();
                ConnectDlg dlg = new ConnectDlg(getShell(), lu.url, lu.port);
                if (dlg.open() == 1) {
                    QconnTabFrame ltab = new QconnTabFrame(mTabFolder, lu.toString(), SWT.FLAT|SWT.CLOSE|SWT.ICON, dlg.getIp(), dlg.getPort());
                    mTabFolder.setSelection(ltab);
                }
            }
        });
        
        mToolItems.get(ToolBarDes.TN_OPEN).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog (getShell(), SWT.OPEN);
                String [] filterNames = new String [] {"Log Files", "All Files (*)"};
                String [] filterExtensions = new String [] {"*.log;*.txt;", "*"};
//                String filterPath = "";
                /*
                String platform = SWT.getPlatform();
                if (platform.equals("win32") || platform.equals("wpf")) {
                    filterNames = new String [] {"Image Files", "All Files (*.*)"};
                    filterExtensions = new String [] {"*.gif;*.png;*.bmp;*.jpg;*.jpeg;*.tiff", "*.*"};
                    filterPath = "c:\\";
                }
                */
                dialog.setFilterNames (filterNames);
                dialog.setFilterExtensions (filterExtensions);
            //    dialog.setFilterPath (filterPath);
                String fname = dialog.open();
                if (fname != null) {
                    FileTabFrame ftb = new FileTabFrame(mTabFolder, fname, SWT.FLAT|SWT.CLOSE|SWT.ICON, fname);
                    mTabFolder.setSelection(ftb);
                    updateToolBars(ftb);
                }
            }
        });
        
        mToolItems.get(ToolBarDes.TN_FILTER).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FilterDlg fdlg = new FilterDlg(getShell());
                if (fdlg.open() == SWT.OK) {
                    SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                    LogFilter f1 = LogFilter.newLogFilter(LogFilter.FIELD_LEVEL, LogFilter.OP_LESSTHEN, Integer.valueOf(7));
                    LogFilter f2 = LogFilter.newLogFilter(LogFilter.FIELD_CONTENT, LogFilter.OP_CONTAINS, "avi");
                   LogFilter f = f1.and(f2);
                    FilterTabFrame ltab = new FilterTabFrame(mTabFolder, "\"" + f.getName() + "\" on [" + tbf.getText() + "]", SWT.FLAT|SWT.CLOSE|SWT.ICON, tbf.getLogSource(), 
                             f);
                    mTabFolder.setSelection(ltab);
                    updateToolBars(ltab);    
                }
                
            }
        });
        
        mToolItems.get(ToolBarDes.TN_CLEAR).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                tbf.getLogView().clear();
            }
        });
        
        mToolItems.get(ToolBarDes.TN_PAUSE).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                if (tbf != null) {
                    tbf.onPause();
                }
            }
        });
        mToolItems.get(ToolBarDes.TN_DISCONNECT).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SlogTabFrame tbf = (SlogTabFrame)mTabFolder.getSelection();
                if (tbf != null) {
                    tbf.onDisconnect();
                }
            }
        });
 
    }
    
    void updateToolItem(ToolItem tit) {
        String tn = (String)tit.getData();
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
        } else {
            tit.setEnabled(false);
        }
    }
    void updateToolBars(SlogTabFrame it) {
        for (ToolItem tit : mToolItems.values()) {
            if (it == null) {
                updateToolItem(tit);
            } else {
                it.updateToolItem(tit);
            }
        }
        mCoolBar.update();
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
        mTabFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                  if (e.item instanceof SlogTabFrame) {
                      SlogTabFrame it = (SlogTabFrame)e.item;
//                      updateToolBars(it);
                  }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                if (e.item instanceof SlogTabFrame) {
                    SlogTabFrame it = (SlogTabFrame)e.item;
  //                  updateToolBars(it);
                }
            }
 
            
        });
        mTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void close(CTabFolderEvent event) {
                if (event.item instanceof SlogTabFrame) {
                    SlogTabFrame it =  (SlogTabFrame)event.item;
                    it.onClose();
                    if (mTabFolder.getItemCount() == 1) {
                        updateToolBars(null);
                    }
                }
            }
        });
        
        mTabFolder.setFocus();
        getDisplay().addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                
                for (ToolItem it : mToolItems.values()) {
                    if (it.isEnabled()) {
                        Integer key = (Integer) it.getData("KeyAccelerator");
                        if (key.intValue() == (e.stateMask|e.keyCode)) {
                            it.notifyListeners(SWT.Selection, null);
                        }
                        
                    }
                }
                /*
                if(((e.stateMask & SWT.COMMAND) == SWT.COMMAND) && (e.keyCode == 'f')) {
                    ToolItem it =  mToolItems.get(ToolBarDes.TN_SEARCH);
                    if (it.isEnabled()) {
                        mToolItems.get(ToolBarDes.TN_SEARCH).notifyListeners(SWT.Selection, null);
                    }
                }
                */
            }});
        
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
