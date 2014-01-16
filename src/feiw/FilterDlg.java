package feiw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import feiw.LogSource.LogFilter;


public class FilterDlg extends Dialog {

    int mSelection = SWT.CANCEL;
    public FilterDlg(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
     }
    public int open() {
        // Create the dialog window
        Shell shell = new Shell(getParent(), getStyle());
        shell.setText(getText());
        createContents(shell);
        shell.pack();

        Rectangle shellBounds = getParent().getBounds();

        Point dialogSize = shell.getSize();

        shell.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2, shellBounds.y
                + (shellBounds.height - dialogSize.y) / 2);

        shell.open();

        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        // Return the entered value, or null
        return mSelection;
    }
    
    private void createUIRule(final Composite parent) {
        
        final Group g = new Group(parent, SWT.BORDER_DASH);
        g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 6, 1));
        g.setLayout(new GridLayout(6, false));
        Combo comb = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
        comb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        comb.add(LogFilter.FIELD_LEVEL);
        comb.add(LogFilter.FIELD_CONTENT);
        comb.add(LogFilter.FIELD_TIME);
                        comb.select(0);
                        
                          comb = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
                        comb.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1));
                        comb.add(LogFilter.OP_LESSTHEN);
                        comb.add(LogFilter.OP_GREATERTHAN);
                        comb.add(LogFilter.OP_EQUALS);
                       // comb.add(LogFilter.OP_CONTAINS);
 
                                        comb.select(0);
                                   

                                       
        final Text textport = new Text(g, SWT.BORDER);
 
        // data.horizontalSpan = 2;
    //    textport.setText(Integer.toString(port));
        textport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        Button b = new Button(g, SWT.PUSH);
        b.setText("-");
        b.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        b.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                
                Shell s =  parent.getShell();
                parent.dispose();
            
             s.pack(true);
            s.layout(true);
                
            }
        });
        
    }
    private void createContents(final Shell shell) {
        final int cols = 6;
        shell.setLayout(new GridLayout(cols, false));

        // Show the message
        Label label = new Label(shell, SWT.NONE);
        label.setText("Filter Builder");
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        Button  b = new Button(shell, SWT.PUSH);
        b.setText("+");
         b.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, cols-1, 1));
        label = new Label(shell, SWT.SEPARATOR|SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, cols, 1));

        
        final Composite g = new Composite(shell, SWT.NONE);
        g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, cols, 1));
        g.setLayout(new GridLayout(6, false));
        createUIRule(g);
        
        b.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
              
                final Composite gg = new Composite(g, SWT.NONE);
                gg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, cols, 1));
                gg.setLayout(new GridLayout(6, false));
              Combo  c = new Combo(gg, SWT.DROP_DOWN | SWT.READ_ONLY);
              c.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1));
              c.add("OR");
              c.add("And");
              c.select(0);
                
                createUIRule(gg);
                g.pack(true);
                g.layout(true);
                shell.pack(true);
                shell.layout(true);
                
            }
        });
        
        label = new Label(shell, SWT.SEPARATOR|SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, cols, 1));
        // Create the OK button and add a handler
        // so that pressing it will set input
        // to the entered value
        Button ok = new Button(shell, SWT.PUSH);
        ok.setText("  OK  ");
        ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, cols-1, 1));
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                mSelection = SWT.OK;
                shell.close();
            }
        });

        // Create the cancel button and add a handler
        // so that pressing it will set input to null
        Button cancel = new Button(shell, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        cancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                mSelection = SWT.CANCEL;
                shell.close();
            }
        });

        
    
        // Set the OK button as the default, so
        // user can type input and press Enter
        // to dismiss
     //   shell.setDefaultButton(ok);
    }
}
