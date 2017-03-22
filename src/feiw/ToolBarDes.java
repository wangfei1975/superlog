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
import org.eclipse.swt.graphics.Image;

public final class ToolBarDes {
    String mName;
    ToolItemDes[] mItems;

    ToolBarDes(String n, ToolItemDes[] its) {
        mName = n;
        mItems = its;
    }

    public static final class ToolItemDes {
        String mName;
        String mTipText;
        Image mImage;
        int mStyle;
        int [] mKeyAccelerators;

        ToolItemDes(String n, String t, Image i, int s, int [] acc) {
            mName = n;
            mTipText = t;
            mImage = i;
            mStyle = s;
            mKeyAccelerators = acc;
        }
    }

    public static final String TBN_FILE = "FILE";
    public static final String TBN_EDIT = "EDIT";
    public static final String TBN_SEARCH = "SEARCH";
    public static final String TBN_TARGET = "TARGET";
    public static final String TBN_CONFIG = "CONFIG";

    public static final String TN_CONNECT = "Connect";
    public static final String TN_CONNECTANDROID = "ConnectAndroid";
    public static final String TN_OPEN = "Open";
    public static final String TN_OPENFIFO = "OpenFifo";
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
    public static final String TN_HELP = "Help";

    @SuppressWarnings("serial")
    static public final ArrayList<ToolBarDes> TOOBARS = new ArrayList<ToolBarDes>(5) {
        {

            int cmdkey = SWT.COMMAND;
            if (SWT.getPlatform().contains("win")) {
                cmdkey = SWT.CONTROL;
            }

            add(new ToolBarDes(TBN_FILE,
                    new ToolItemDes[] {
                            new ToolItemDes(TN_CONNECT, "Connect to QCONN device", Resources.connected_32,
                                    SWT.DROP_DOWN, new int[]{cmdkey | 'd'}),
                            new ToolItemDes(TN_CONNECTANDROID, "Connect to Android device", Resources.android_32,
                                    SWT.PUSH, new int[]{cmdkey | 'a'}),
                    // new ToolItemDes(null, null, null, SWT.SEPARATOR, 0),
                    new ToolItemDes(TN_OPEN, "Open Log file", Resources.openfile_32, SWT.DROP_DOWN, new int[]{cmdkey | 'o'}),
                    new ToolItemDes(TN_OPENFIFO, "Open Fifo to receive logs", Resources.fifo_32, SWT.PUSH,
                            new int[]{cmdkey | 'i'}),
                    new ToolItemDes(TN_SAVEAS, "Save Logs as", Resources.save_32, SWT.PUSH, new int[]{cmdkey | 's'}), }));
            add(new ToolBarDes("TN_FILTER", new ToolItemDes[] {
                    new ToolItemDes(TN_FILTER, "Open Filted view", Resources.filter_32, SWT.DROP_DOWN, null), }));
            add(new ToolBarDes(TBN_SEARCH,
                    new ToolItemDes[] {
                            new ToolItemDes(TN_SEARCH, "Search", Resources.search_32, SWT.PUSH, new int[]{cmdkey | 'f'}),
                            new ToolItemDes(TN_NEXT, "Next Result", Resources.down_32, SWT.PUSH, new int []{cmdkey | 'n', SWT.F4}),
                            new ToolItemDes(TN_PREV, "Previous Result", Resources.up_32, SWT.PUSH, new int[]{cmdkey | 'p', SWT.F3}), }));

            add(new ToolBarDes(TBN_EDIT,
                    new ToolItemDes[] {
                            new ToolItemDes(TN_CLEAR, "Clear Logs", Resources.trash_32, SWT.PUSH, new int[]{cmdkey | 'r'}),
                            new ToolItemDes(TN_COPY, "Copy selection", Resources.copy_32, SWT.PUSH,
                                    new int[]{SWT.SHIFT | cmdkey | 'c'}),
                    new ToolItemDes(TN_COPYALL, "Copy selection(All columns)", Resources.copyall_32, SWT.PUSH,
                            new int[]{cmdkey | 'c'}), }));

            add(new ToolBarDes(TBN_TARGET,
                    new ToolItemDes[] {
                            new ToolItemDes(TN_DISCONNECT, "Disconnect device", Resources.disconnected_32, SWT.PUSH, null),
                            new ToolItemDes(TN_PAUSE, "Pause", Resources.pause_32, SWT.PUSH, null) }));
            add(new ToolBarDes(TBN_CONFIG,
                    new ToolItemDes[] { new ToolItemDes(TN_PREFERENCE, "Preference", Resources.config_32, SWT.PUSH, null),
                            new ToolItemDes(TN_HELP, "Help", Resources.help_32, SWT.PUSH, null), }));
        }
    };

}
