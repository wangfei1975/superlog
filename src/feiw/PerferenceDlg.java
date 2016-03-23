/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PerferenceDlg extends Dialog {
    private String message = "Perference";
    private int fontSize = 0;
    private int selection = 0;

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int s) {
        fontSize = s;
    }

    public PerferenceDlg(Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    }

    public PerferenceDlg(Shell parent, int style) {
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

        shell.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
                shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

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
        // final Text text = new Text(shell, SWT.BORDER);
        final Label lbFontSize = new Label(shell, SWT.NONE);
        data = new GridData(GridData.FILL_HORIZONTAL);
        // data.horizontalSpan = 2;
        // text.setText(ipaddr);
        lbFontSize.setText("Font Size");
        lbFontSize.setLayoutData(data);

        final Text textport = new Text(shell, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        // data.horizontalSpan = 2;
        textport.setText(Integer.toString(fontSize));
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
                int fs = Integer.parseInt(textport.getText());
                if (fs <= 0 || fs > 100) {
                    MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                    m.setMessage("Invalid font size [0 to 100]");
                    m.open();
                    return;
                }
                fontSize = Integer.parseInt(textport.getText());
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
