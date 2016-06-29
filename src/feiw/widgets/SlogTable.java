package feiw.widgets;


import feiw.LogSource.LogView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public final class SlogTable extends Table {

    LogView mLogView = null;

    public void setLogView(LogView v) {
        mLogView = v;
    }
    
    public SlogTable(Composite parent, int style, LogView v) {
        super(parent, style | SWT.BORDER | SWT.VIRTUAL | SWT.MULTI | SWT.FULL_SELECTION);
        mLogView = v;
        setLinesVisible(true);
        setHeaderVisible(true);
        
        
        //final String[] title = { "Flag", "Line", "Time", "Level", "Major", "Minor", "Args" };
        final String[] title = mLogView.getLogParser().getTableHeader();
        final int[] width = mLogView.getLogParser().getHeaderWidth();

        for (int i = 0; i < title.length; i++) {
            TableColumn column = new TableColumn(this, SWT.NONE);
            column.setText(title[i]);
           // column.setAlignment(SWT.CENTER);
            column.setWidth(width[i]);
            
        }
        /*
      
        FontData[] fontData = getFont().getFontData();

        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(16);
        }
*/
      //  setFontSize(10);
   
        addListener(SWT.SetData, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (mLogView == null) {
                    return;
                }

                final TableItem item = (TableItem) e.item;
                final int index = SlogTable.this.indexOf(item);
                item.setText(1, Integer.toString(index));
                
                String s = mLogView.getLog(index);
                if (s != null) {
                    mLogView.getLogParser().updateTableItem(s, item, mLogView.getSearchPattern());
                }
            }
        });

    }

	@Override
	protected void checkSubclass() {
        // do nothing, this is to avoid a SWT exception when overriding classes
	}
    
    
}
