package feiw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConnectDlg extends Dialog {
    private String message = "Target IP and port";
    private String ipaddr;
    private int port;
    private int selection = 0;

    public String getIp() {
        return ipaddr;
    }

    public int getPort() {
        return port;
    }

    public ConnectDlg(Shell parent, String ip, int p) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        if (ip != null) {
            ipaddr = ip;
            port = p;
        }
    }

    public ConnectDlg(Shell parent, int style) {
        super(parent, style);
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
        return selection;
    }

    /**
     * Creates the dialog's contents
     * 
     * @param shell
     *            the dialog window
     */
    private void createContents(final Shell shell) {
        shell.setLayout(new GridLayout(2, true));

        shell.setText("Input qconn target IP and port");
        // Show the message
        Label label = new Label(shell, SWT.NONE);
        label.setText(message);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        // Display the input box
        final Text text = new Text(shell, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        // data.horizontalSpan = 2;
        text.setText(ipaddr);
        text.setLayoutData(data);

        final Text textport = new Text(shell, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        // data.horizontalSpan = 2;
        textport.setText(Integer.toString(port));
        textport.setLayoutData(data);

        // Create the OK button and add a handler
        // so that pressing it will set input
        // to the entered value
        Button ok = new Button(shell, SWT.PUSH);
        ok.setText("OK");
        data = new GridData(GridData.FILL_HORIZONTAL);
        ok.setLayoutData(data);
        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ipaddr = text.getText();
                port = Integer.parseInt(textport.getText());
                selection = SWT.OK;
                shell.close();
            }
        });

        // Create the cancel button and add a handler
        // so that pressing it will set input to null
        Button cancel = new Button(shell, SWT.PUSH);
        cancel.setText("Cancel");
        data = new GridData(GridData.FILL_HORIZONTAL);
        cancel.setLayoutData(data);
        cancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                selection = SWT.CANCEL;
                shell.close();
            }
        });

        // Set the OK button as the default, so
        // user can type input and press Enter
        // to dismiss
        shell.setDefaultButton(ok);
    }
}
