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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogView;

public class FilterDlg extends Dialog {

    int mSelection = SWT.CANCEL;
    final LogView mLogView;
    String mDefaultField;
    boolean mHasTagField = false;
    boolean mHasPidField = false;

    public FilterDlg(Shell parent, final LogView logView, String defaultField) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        mLogView = logView;
        String[] hd = logView.getLogParser().getTableHeader();
        for (int i = 0; i < hd.length; i++) {
            if (hd[i].equalsIgnoreCase(LogFilter.FIELD_TAG)) {
                mHasTagField = true;
            } else if (hd[i].equalsIgnoreCase(LogFilter.FIELD_PID)) {
                mHasPidField = true;
            }
        }
        mDefaultField = defaultField;
    }

    public FilterDlg(Shell parent, final LogView logView) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        mLogView = logView;
        String[] hd = logView.getLogParser().getTableHeader();
        for (int i = 0; i < hd.length; i++) {
            if (hd[i].equalsIgnoreCase(LogFilter.FIELD_TAG)) {
                mHasTagField = true;
            } else if (hd[i].equalsIgnoreCase(LogFilter.FIELD_PID)) {
                mHasPidField = true;
            }
        }
    }

    public int open() {
        // Create the dialog window
        Shell shell = new Shell(getParent(), getStyle());

        createContents(shell);

        shell.setText("Filter Builder");
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
        return mSelection;
    }

    public int open(ArrayList<RawRule> rawRules) {
        // Create the dialog window
        Shell shell = new Shell(getParent(), getStyle());

        createRawRuleContents(shell, rawRules);

        shell.setText("Filter Builder");
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
        return mSelection;
    }

    public static class RawRule {
        int index;
        String mop;
        String field;
        String op;
        String value;

        public String toString() {
            return "\nRawRule{index=" + index + ",mop=" + mop + ",field=" + field + ",op=" + op + ",value=" + value + "}\n";
        }
    };

    public static class Rule {
        int index;
        Composite holder;
        Combo mop;
        Combo field;
        Combo op;
        Text value;

        String verify() {
            String v = value.getText().trim();
            if (v == null || v.isEmpty()) {
                return "Rule " + index + " Value is empty";
            }
            String fie = field.getText();
            if (fie.equals(LogFilter.FIELD_PRIORITY)) {
                try {
                    Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    return "Rule " + index + " Value (" + v + ") is not a number";
                }
            }
            return null;
        }

        LogFilter toFilter() {
            String fie = field.getText();
            Object v;
            if (fie.equals(LogFilter.FIELD_PRIORITY)) {
                v = Integer.parseInt(value.getText());
            } else {
                v = value.getText();
            }
            return LogFilter.newLogFilter(fie, op.getText(), v);
        }

        RawRule toRawRule() {
            RawRule rawRule = new RawRule();

            rawRule.field = field.getText();
            rawRule.value = value.getText();
            if (mop != null)
                rawRule.mop = mop.getText();
            rawRule.op = op.getText();
            rawRule.index = index;

            return rawRule;
        }
    };

    private ArrayList<Rule> mRules = new ArrayList<Rule>(5);
    private ArrayList<RawRule> mRawRules = new ArrayList<RawRule>(5);

    static final int mLayoutCols = 4;

    private LogFilter mFilter = null;

    LogFilter getFilter() {
        return mFilter;
    }

    private Rule createUIRule(final Composite parent, int index, String defaultField) {

        final Rule r = new Rule();
        r.holder = new Composite(parent, SWT.NONE);
        r.holder.setData(Integer.valueOf(index));
        r.index = index;

        r.holder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, mLayoutCols, 1));
        GridLayout gly = new GridLayout(mLayoutCols, true);
        gly.verticalSpacing = 1;
        gly.marginHeight = 1;
        r.holder.setLayout(gly);
        if (index > 0) {
            Combo c = new Combo(r.holder, SWT.DROP_DOWN | SWT.READ_ONLY);
            c.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
            c.add("OR");
            c.add("AND");
            c.select(0);
            r.mop = c;
        }

        Group g = new Group(r.holder, SWT.BORDER_DASH);
        g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, mLayoutCols, 1));
        g.setLayout(gly);
        final Combo comb = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
        comb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        comb.add(LogFilter.FIELD_PRIORITY);
        comb.add(LogFilter.FIELD_CONTENT);
        // comb.add(LogFilter.FIELD_TIME);
        if (mHasTagField) {
            comb.add(LogFilter.FIELD_TAG);
        }
        if (mHasPidField) {
            comb.add(LogFilter.FIELD_PID);
        }
        comb.select(0);
        r.field = comb;

        final Combo combop = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
        combop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        combop.add(LogFilter.OP_LESSTHEN);
        combop.add(LogFilter.OP_GREATERTHAN);
        combop.add(LogFilter.OP_EQUALS);
        // comb.add(LogFilter.OP_CONTAINS);

        combop.select(0);
        r.op = combop;
        comb.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                combop.removeAll();
                if (comb.getSelectionIndex() == 0) {
                    combop.add(LogFilter.OP_LESSTHEN);
                    combop.add(LogFilter.OP_GREATERTHAN);
                    combop.add(LogFilter.OP_EQUALS);
                    combop.select(0);
                } else if (comb.getSelectionIndex() == 1) {
                    combop.add(LogFilter.OP_CONTAINS);
                    combop.select(0);
                } else if (comb.getSelectionIndex() == 2) {
                    combop.add(LogFilter.OP_EQUALS);
                    combop.add(LogFilter.OP_CONTAINS);
                    combop.select(0);
                } else {
                    combop.add(LogFilter.OP_EQUALS);
                    combop.select(0);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

        });
        if (defaultField != null) {
            if (defaultField.equals(LogFilter.FIELD_CONTENT)) {
                comb.select(1);
            } else if (mHasTagField && defaultField.equals(LogFilter.FIELD_TAG)) {
                comb.select(2);
            } else if (mHasPidField && defaultField.equals(LogFilter.FIELD_PID)) {
                comb.select(3);
            }
        }
        comb.notifyListeners(SWT.Selection, null);

        final Text text = new Text(g, SWT.BORDER);

        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        r.value = text;
        text.setFocus();
        Button b = new Button(g, SWT.PUSH);
        b.setText("-");
        b.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                Shell s = r.holder.getShell();
                r.holder.dispose();
                mRules.remove(r);
                parent.pack(true);
                parent.layout(true);
                s.pack();
                s.layout();

            }
        });
        if (index == 0) {
            b.setEnabled(false);
        }
        return r;
    }

    private Rule createUIRawRule(final Composite parent, RawRule rawRule) {
        int index = rawRule.index;

        final Rule rule = new Rule();

        rule.holder = new Composite(parent, SWT.NONE);
        rule.holder.setData(Integer.valueOf(index));
        rule.index = index;

        rule.holder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, mLayoutCols, 1));
        GridLayout gly = new GridLayout(mLayoutCols, true);
        gly.verticalSpacing = 1;
        gly.marginHeight = 1;
        rule.holder.setLayout(gly);

        if (index > 0) {
            Combo c = new Combo(rule.holder, SWT.DROP_DOWN | SWT.READ_ONLY);

            c.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
            c.add("OR");
            c.add("AND");

            if (rawRule.mop.toLowerCase().equals("or"))
                c.select(0);
            else
                c.select(1);

            rule.mop = c;
        }

        // Rule Filed
        Group g = new Group(rule.holder, SWT.BORDER_DASH);
        g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, mLayoutCols, 1));
        g.setLayout(gly);
        final Combo filedComb = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
        filedComb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        filedComb.add(LogFilter.FIELD_PRIORITY);
        filedComb.add(LogFilter.FIELD_CONTENT);
        // filedComb.add(LogFilter.FIELD_TIME);
        if (mHasTagField) {
            filedComb.add(LogFilter.FIELD_TAG);
        }
        if (mHasPidField) {
            filedComb.add(LogFilter.FIELD_PID);
        }
        if (rawRule.field.equals(LogFilter.FIELD_PRIORITY))
            filedComb.select(0);
        else if (rawRule.field.equals(LogFilter.FIELD_CONTENT))
            filedComb.select(1);
        else {
            if (mHasTagField) {
                if (rawRule.field.equals(LogFilter.FIELD_TAG))
                    filedComb.select(2);

                if (mHasPidField && rawRule.field.equals(LogFilter.FIELD_PID))
                    filedComb.select(3);
            } else {
                if (mHasPidField && rawRule.field.equals(LogFilter.FIELD_PID))
                    filedComb.select(2);
            }
        }

        rule.field = filedComb;

        // Rule Operator
        final Combo opCombo = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
        opCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        opCombo.add(LogFilter.OP_LESSTHEN);
        opCombo.add(LogFilter.OP_GREATERTHAN);
        opCombo.add(LogFilter.OP_EQUALS);
        // filedComb.add(LogFilter.OP_CONTAINS);

        if (rawRule.op.equals(LogFilter.OP_LESSTHEN))
            opCombo.select(0);
        else if (rawRule.op.equals(LogFilter.OP_GREATERTHAN))
            opCombo.select(1);
        else if (rawRule.op.equals(LogFilter.OP_EQUALS))
            opCombo.select(2);

        rule.op = opCombo;

        filedComb.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                opCombo.removeAll();
                if (filedComb.getSelectionIndex() == 0) {
                    opCombo.add(LogFilter.OP_LESSTHEN);
                    opCombo.add(LogFilter.OP_GREATERTHAN);
                    opCombo.add(LogFilter.OP_EQUALS);
                    opCombo.select(0);
                } else if (filedComb.getSelectionIndex() == 1) {
                    opCombo.add(LogFilter.OP_CONTAINS);
                    opCombo.select(0);
                } else if (filedComb.getSelectionIndex() == 2) {
                    opCombo.add(LogFilter.OP_EQUALS);
                    opCombo.add(LogFilter.OP_CONTAINS);
                    opCombo.select(0);
                } else {
                    opCombo.add(LogFilter.OP_EQUALS);
                    opCombo.select(0);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

        });

        filedComb.notifyListeners(SWT.Selection, null);

        // Rule Text Value
        final Text valueText = new Text(g, SWT.BORDER);
        valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        rule.value = valueText;
        valueText.setFocus();

        valueText.setText(rawRule.value);

        // Delete Button
        Button b = new Button(g, SWT.PUSH);
        b.setText("-");
        b.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                Shell s = rule.holder.getShell();
                rule.holder.dispose();
                mRules.remove(rule);
                parent.pack(true);
                parent.layout(true);
                s.pack();
                s.layout();

            }
        });
        if (index == 0) {
            b.setEnabled(false);
        }
        return rule;
    }

    Text mName;
    LogParser mParser;

    private void createContents(final Shell shell) {
        GridLayout gly = new GridLayout(mLayoutCols, true);

        shell.setLayout(gly);

        // Show the message
        Label label = new Label(shell, SWT.NONE);
        label.setText("Filter name:");
        label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

        mName = new Text(shell, SWT.BORDER);
        mName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btLoadRules = new Button(shell, SWT.PUSH);
        btLoadRules.setText("Load");
        btLoadRules.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        Button btSaveRules = new Button(shell, SWT.PUSH);
        btSaveRules.setText("Save");
        btSaveRules.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL)
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, mLayoutCols, 1));

        final Composite g = new Composite(shell, SWT.NONE);

        g.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, mLayoutCols, 1));
        gly.verticalSpacing = 4;
        gly.marginHeight = 4;
        g.setLayout(gly);
        label = new Label(g, SWT.NONE);
        label.setText("Apply following rules on every Log item:");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, mLayoutCols - 1, 1));

        Button b = new Button(g, SWT.PUSH);
        b.setText("+");
        b.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        mRules.add(createUIRule(g, 0, mDefaultField));

        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                mRules.add(createUIRule(g, mRules.size(), mDefaultField));
                g.pack(true);
                g.layout(true);
                shell.pack(true);
                shell.layout(true);

            }
        });

        label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, mLayoutCols, 1));

        new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        Button ok = new Button(shell, SWT.PUSH);
        ok.setText("    OK    ");
        ok.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                String s = mName.getText();
                if (s == null || s.trim().isEmpty()) {
                    // MessageBox m = new MessageBox(shell,
                    // SWT.OK|SWT.ICON_ERROR);
                    // m.setMessage("Filter name can't be empty.");

                    // m.open();

                    // return;
                }
                for (Rule r : mRules) {
                    String msg = r.verify();
                    if (msg != null) {
                        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        m.setMessage(msg);
                        m.open();
                        return;
                    }
                }

                mRawRules.clear();

                for (Rule r : mRules) {
                    LogFilter f = r.toFilter();
                    if (f == null) {
                        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        m.setMessage("Could not create LogFilter");
                        m.open();
                        return;
                    }
                    if (mFilter == null) {
                        mFilter = f;
                    } else if (r.mop.getText().toLowerCase().equals("or")) {
                        mFilter = mFilter.or(f);
                    } else {
                        mFilter = mFilter.and(f);
                    }

                    mRawRules.add(r.toRawRule());
                }

                String nm = mName.getText();
                if (nm != null && !nm.isEmpty()) {
                    mFilter.setName(nm);
                }

                //mLogView.setRawRules(mRawRules);
                mFilter.setRawRules(mRawRules);

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
            @Override
            public void widgetSelected(SelectionEvent event) {
                mSelection = SWT.CANCEL;
                shell.close();
            }
        });

        new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));

        // Set the OK button as the default, so
        // user can type input and press Enter
        // to dismiss
        shell.setDefaultButton(ok);
    }

    private void createRawRuleContents(final Shell shell, ArrayList<RawRule> rawRules) {
        GridLayout gly = new GridLayout(mLayoutCols, true);

        shell.setLayout(gly);

        // Show the message
        Label label = new Label(shell, SWT.NONE);
        label.setText("Filter name:");
        label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

        mName = new Text(shell, SWT.BORDER);
        mName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL)
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, mLayoutCols, 1));

        final Composite g = new Composite(shell, SWT.NONE);

        g.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, mLayoutCols, 1));
        gly.verticalSpacing = 4;
        gly.marginHeight = 4;
        g.setLayout(gly);
        label = new Label(g, SWT.NONE);
        label.setText("Apply following rules on every Log item:");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, mLayoutCols - 1, 1));
        Button b = new Button(g, SWT.PUSH);
        b.setText("+");
        b.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        mRules.clear();

        for (RawRule rawRule: rawRules)
            mRules.add(createUIRawRule(g, rawRule));

        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                mRules.add(createUIRule(g, mRules.size(), mDefaultField));
                g.pack(true);
                g.layout(true);
                shell.pack(true);
                shell.layout(true);
            }
        });

        label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, mLayoutCols, 1));

        new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        Button ok = new Button(shell, SWT.PUSH);
        ok.setText("    OK    ");
        ok.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                String s = mName.getText();

                if (s == null || s.trim().isEmpty()) {
                }

                for (Rule r : mRules) {
                    String msg = r.verify();
                    if (msg != null) {
                        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        m.setMessage(msg);
                        m.open();
                        return;
                    }
                }

                mRawRules.clear();

                for (Rule r : mRules) {
                    LogFilter f = r.toFilter();
                    if (f == null) {
                        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        m.setMessage("Could not create LogFilter");
                        m.open();
                        return;
                    }
                    if (mFilter == null) {
                        mFilter = f;
                    } else if (r.mop.getText().toLowerCase().equals("or")) {
                        mFilter = mFilter.or(f);
                    } else {
                        mFilter = mFilter.and(f);
                    }

                    RawRule rawRule = r.toRawRule();
                    if (rawRule == null) {
                        MessageBox m = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                        m.setMessage("Could not create RawRule");
                        m.open();
                        return;
                    }

                    mRawRules.add(rawRule);
                }

                String nm = mName.getText();
                if (nm != null && !nm.isEmpty()) {
                    mFilter.setName(nm);
                }

                //mLogView.setRawRules(mRawRules);
                mFilter.setRawRules(mRawRules);

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
            @Override
            public void widgetSelected(SelectionEvent event) {
                mSelection = SWT.CANCEL;
                shell.close();
            }
        });

        new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));

        // Set the OK button as the default, so
        // user can type input and press Enter
        // to dismiss
        shell.setDefaultButton(ok);
    }
}
