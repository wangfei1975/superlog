package qnx;

import java.util.Collection;
import java.util.Vector;
import qnx.SlogInfo.LogItem;

public class SlogDocument {
    
    Vector <LogItem> mData = new Vector < LogItem>(1000, 1000);

 
    
    public interface LogListener {
        public void onNewLogs(Collection <LogItem> logs);
        public void onRemoveAll();
    }
}
