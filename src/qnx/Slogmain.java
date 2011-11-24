package qnx;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
public class Slogmain extends  ApplicationWindow {

    public Slogmain() {
        super(null);
        // TODO Auto-generated constructor stub
      //  
    //    addMenuBar();
      //  addToolBar(SWT.FLAT | SWT.WRAP);
    }

    /**
     * @param args
     * @throws IOException
     */

    //Display display;
    Table table;
    SlogInfo logger;
    ToolBar toolBar;
   // Shell shell;
    Listener searchListener = new Listener(){
         
        Shell shell = getShell();
        public void handleEvent(Event event) {
            ToolItem item = (ToolItem)event.widget;
            System.out.println(item.getText() + " is selected");
           
            SearchDlg d = new SearchDlg(shell);
           
            String input = d.open();
            if (input != null)
            {
                int r = logger.search(input, 0, 1, 0);
                if (r >= 0 && r < table.getItemCount()) {
                    table.select(r);
                    table.setTopIndex(r-2);
                }
            }

            
          }
    };
    void createToolbar(Composite shell)    {
          
        Listener selectionListener = new Listener() {
            public void handleEvent(Event event) {
              ToolItem item = (ToolItem)event.widget;
              System.out.println(item.getText() + " is selected");
              logger.clearLogs();
            }
          };
          CoolBar composite = new CoolBar(shell, SWT.NONE);
          {  
      
        CoolItem item = new CoolItem(composite, SWT.NONE);
        ToolBar tb = new ToolBar(composite, SWT.FLAT);
        ToolItem ti = new ToolItem(tb, SWT.NONE);
        ti.setText("Clean Logs");
        ti.addListener(SWT.Selection, selectionListener);
         Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
         tb.setSize(p);
          Point p2 = item.computeSize(p.x, p.y);
          item.setControl(tb);
          item.setSize(p2);
          }
          {
          CoolItem item = new CoolItem(composite, SWT.NONE);
          ToolBar tb = new ToolBar(composite, SWT.FLAT);
          ToolItem ti = new ToolItem(tb, SWT.NONE);
          ti.setText("Search");
          ti.addListener(SWT.Selection, searchListener);
           Point p = tb.computeSize(SWT.DEFAULT, SWT.DEFAULT);
           tb.setSize(p);
            Point p2 = item.computeSize(p.x, p.y);
            item.setControl(tb);
            item.setSize(p2);
            }
          
    }
    
    protected Control createContents(Composite shell) {

     //   display = new Display();
       //  shell = new Shell(display, SWT.SHELL_TRIM);
         
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        shell.setLayout(layout);
        createToolbar(shell);
        
       
        table = new Table(shell, SWT.BORDER | SWT.VIRTUAL | SWT.MULTI);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData gridData = new GridData();
         gridData.horizontalAlignment = GridData.FILL;
         gridData.verticalAlignment = GridData.FILL;
         gridData.grabExcessHorizontalSpace = true;
         gridData.grabExcessVerticalSpace = true;
         table.setLayoutData(gridData);
        table.setFocus();
        
        String[] title = {"Line Number", "Time", "Sev", "Major", "Minor", "Args" };
        int[] width = {70, 155, 30, 50, 50, 1000 };
        
        for (int i = 0; i < title.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title[i]);
            column.setWidth(width[i]);
        }
        
        table.addListener(SWT.SetData, new Listener() {
            public void handleEvent(Event e) {
                Display display = getShell().getDisplay();
                
                TableItem item = (TableItem) e.item;
                int index = table.indexOf(item);
                String[] log = logger.getData(index);
                if (log == null)
                    return;
                    
                item.setText(0, ""+index);
                for (int i = 0; i < log.length; i++) {
                    item.setText(i+1, log[i] == null ? "" : log[i]);
                }

                if (log[1] != null && !log[1].isEmpty()) {
                    char ser = log[1].charAt(0);
                    switch (ser) {
                    case '0':
                        item.setBackground(display.getSystemColor(SWT.COLOR_RED));
                        item.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
                        break;
                    case '1':
                        item.setForeground(display.getSystemColor(SWT.COLOR_RED));
                        break;
                    case '2':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
                        break;
                    case '3':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
                        break;
                    case '4':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
                        break;
                    case '5':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
                        break;
                    case '6':
                        item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                        break;
                    case '7':
                        item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
                        break;
                     
                    default:
                        item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                        break;

                    }

                }
             
            }
        });
        
     
        Font f = table.getFont();
        FontData[] fontData = f.getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(18);
        }
        Display display = getShell().getDisplay();
        Font newFont = new Font(display, fontData);
        table.setFont(newFont);
 
       shell.setSize(1200, 800); 
        
       return shell;
        
    }
    
    public void run() {
        // Don't return from open() until window closes
        setBlockOnOpen(true);
 
        logger = new SlogInfo();
        logger.connect("172.16.235.128", 8000, new SlogInfo.LogListener(){
            @Override
            public void handleLogs() {
                if (table == null || table.isDisposed())
                    return;
                Display display = getShell().getDisplay();
                display.asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (table.isDisposed())
                            return;
                        int cnt = logger.getDataSize();
                        if (table.getItemCount() == cnt)
                            return;
                        table.setItemCount(cnt);
                        table.setTopIndex(cnt - 2);
                         getShell().setText("" + cnt + " lines of log");
                       // System.out.print("data cnt:" + cnt + " table cnt" + table.getItemCount() + "\n");
                    }
                });
                
            }
        });
        
        // Open the main window
        open();
     
        // Dispose the display
        if (Display.getCurrent() != null)
        Display.getCurrent().dispose();
       
        logger.disconnect();
      }


    public static void main(String[] args) {
        new Slogmain().run();

    }

}
