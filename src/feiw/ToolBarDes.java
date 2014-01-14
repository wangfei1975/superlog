package feiw;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public final class ToolBarDes {
    String mName;
    ToolItemDes [] mItems;
    
    ToolBarDes(String n, ToolItemDes [] its) {
        mName = n;
        mItems = its;
    }
    
    public static final class ToolItemDes{
        String mName;
        String mTipText;
        Image  mImage;
        int    mStyle;
        ToolItemDes(String n, String t, Image i, int s) {
            mName = n;
            mTipText = t;
            mImage = i;
            mStyle = s;
        }
    }
    
    public static final String TBN_FILE = "FILE";
    public static final String TBN_EDIT = "EDIT";
    public static final String TBN_SEARCH = "SEARCH";
    public static final String TBN_TARGET = "TARGET";
    
    public static final String TN_CONNECT = "Connect";
    public static final String TN_OPEN = "Open";
    public static final String TN_FILTER = "Filter";
    public static final String TN_CLEAR = "Clear";
    public static final String TN_COPY = "Copy";
    public static final String TN_COPYALL = "CopyAll";
    public static final String TN_SAVEAS = "SaveAs";
    
    public static final String TN_SEARCH = "Search";
    public static final String TN_NEXT = "Next";
    public static final String TN_PREV = "Prev";
    
    public static final String TN_DISCONNECT = "Disconnect";
    public static final String TN_PAUSE = "Pause";
    
    @SuppressWarnings("serial")
    static public final ArrayList<ToolBarDes> TOOBARS = new ArrayList <ToolBarDes>(5){ {
        add(new ToolBarDes(TBN_FILE, new ToolItemDes [] { 
                new ToolItemDes(TN_CONNECT,   "Connect to a QCONN device", Resources.connected_32, SWT.PUSH),
                new ToolItemDes(TN_OPEN,      "Open a Log file", Resources.openfile_32, SWT.PUSH),
                new ToolItemDes(TN_FILTER,    "Open a Filted view", Resources.filter_32, SWT.PUSH),
        }));
        add(new ToolBarDes(TBN_SEARCH, new ToolItemDes [] { 
                new ToolItemDes(TN_SEARCH,   "Search", Resources.search_32, SWT.PUSH),
                new ToolItemDes(TN_NEXT,   "Next Result", Resources.down_32, SWT.PUSH),
                new ToolItemDes(TN_PREV,   "Previous Result", Resources.up_32, SWT.PUSH),
        }));
        
        add(new ToolBarDes(TBN_EDIT, new ToolItemDes [] {
                new ToolItemDes(TN_CLEAR,   "Clear Logs", Resources.trash_32, SWT.PUSH),
                new ToolItemDes(TN_COPY,    "Copy selection", Resources.copy_32, SWT.PUSH),
                new ToolItemDes(TN_COPYALL,    "Copy selection(All columns)", Resources.copyall_32, SWT.PUSH),
                new ToolItemDes(TN_SAVEAS,    "Save selection as", Resources.save_32, SWT.PUSH),
        }));
        
        add(new ToolBarDes(TBN_TARGET, new ToolItemDes [] { 
                new ToolItemDes(TN_DISCONNECT,   "Disconnect device", Resources.disconnected_32, SWT.PUSH),
                new ToolItemDes(TN_PAUSE,        "Pause", Resources.pause_32, SWT.PUSH)
        }));
        
    }};
        
    
    
}
