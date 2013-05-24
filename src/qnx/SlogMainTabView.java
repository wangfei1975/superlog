package qnx;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.SlogTable.LogProvider;

import qnx.SlogInfo.LogItem;
import qnx.SlogInfo.LogListener;

public final class SlogMainTabView extends SlogTabView implements LogProvider, LogListener {

    public SlogMainTabView(CTabFolder parent, int style) {
        super(parent, style);
        mTable.setLogProvider(this);
    }

    @Override
    public LogItem getLog(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void handleLogs() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleSearchResult() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleStatusChanged(boolean connected) {
        // TODO Auto-generated method stub
        
    }

}
