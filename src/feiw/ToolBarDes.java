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
        int    mKeyAccelerator;
        ToolItemDes(String n, String t, Image i, int s, int acc) {
            mName = n;
            mTipText = t;
            mImage = i;
            mStyle = s;
            mKeyAccelerator = acc;
        }
    }
    
    public static final String TBN_FILE = "FILE";
    public static final String TBN_EDIT = "EDIT";
    public static final String TBN_SEARCH = "SEARCH";
    public static final String TBN_TARGET = "TARGET";
    public static final String TBN_CONFIG = "CONFIG";
    
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
    public static final String TN_PREFERENCE = "Preference";
    
    @SuppressWarnings("serial")
    static public final ArrayList<ToolBarDes> TOOBARS = new ArrayList <ToolBarDes>(5){ {
        add(new ToolBarDes(TBN_FILE, new ToolItemDes [] { 
                new ToolItemDes(TN_CONNECT,   "Connect to a QCONN device", Resources.connected_32, SWT.DROP_DOWN, SWT.COMMAND|'d'),
                new ToolItemDes(TN_OPEN,      "Open a Log file", Resources.openfile_32, SWT.DROP_DOWN, SWT.COMMAND|'o'),
                new ToolItemDes(TN_FILTER,    "Open a Filted view", Resources.filter_32, SWT.DROP_DOWN, 0),
        }));
        add(new ToolBarDes(TBN_SEARCH, new ToolItemDes [] { 
                new ToolItemDes(TN_SEARCH,   "Search", Resources.search_32, SWT.PUSH, SWT.COMMAND|'f'),
                new ToolItemDes(TN_NEXT,   "Next Result", Resources.down_32, SWT.PUSH, SWT.COMMAND|'n'),
                new ToolItemDes(TN_PREV,   "Previous Result", Resources.up_32, SWT.PUSH, SWT.COMMAND|'p'),
        }));
        
        add(new ToolBarDes(TBN_EDIT, new ToolItemDes [] {
                new ToolItemDes(TN_CLEAR,   "Clear Logs", Resources.trash_32, SWT.PUSH, SWT.COMMAND|'r'),
                new ToolItemDes(TN_COPY,    "Copy selection", Resources.copy_32, SWT.PUSH, SWT.SHIFT|SWT.COMMAND|'c'),
                new ToolItemDes(TN_COPYALL,    "Copy selection(All columns)", Resources.copyall_32, SWT.PUSH, SWT.COMMAND|'c'),
                new ToolItemDes(TN_SAVEAS,    "Save selection as", Resources.save_32, SWT.PUSH, SWT.COMMAND|'s'),
        }));
        
        add(new ToolBarDes(TBN_TARGET, new ToolItemDes [] { 
                new ToolItemDes(TN_DISCONNECT,   "Disconnect device", Resources.disconnected_32, SWT.PUSH, 0),
                new ToolItemDes(TN_PAUSE,        "Pause", Resources.pause_32, SWT.PUSH, 0)
        }));
        add(new ToolBarDes(TBN_CONFIG, new ToolItemDes [] { 
                new ToolItemDes(TN_PREFERENCE,   "Preference", Resources.config_32, SWT.PUSH, 0),
                new ToolItemDes(TN_PREFERENCE,   "Help", Resources.help_32, SWT.PUSH, 0),
        }));
    }};
        
    
    
}
