package qnx;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.SlogTable;

public class SlogTabView  {

    CTabItem  mTabItem;
    SlogTable mTable;
    
    public SlogTable getTable() {
        return mTable;
    }
    public SlogTabView(CTabFolder parent, int style) {
        mTable = new SlogTable(parent, style);
        mTabItem = new CTabItem(parent, style);
        mTabItem.setControl(mTable);
    }

}
