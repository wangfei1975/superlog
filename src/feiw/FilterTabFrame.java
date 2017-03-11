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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolItem;

import feiw.LogSource.LogFilter;
import feiw.LogSource.LogView;

public class FilterTabFrame extends SlogTabFrame {

    public FilterTabFrame(CTabFolder parent, String txt, int style, LogSource logSource, LogFilter logFilter,
            LogParser logParser, LogView parentView) {
        super(parent, txt, style, logSource, logFilter, logParser, parentView);
        setImage(Resources.filter_32);
    }

    @Override
    void updateToolItem(ToolItem tit) {

        String tn = (String) tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        }
        if (mLogSource instanceof FileLogSource) {
            if (tn.equals(ToolBarDes.TN_DISCONNECT)) {
                tit.setEnabled(false);
            } else if (tn.equals(ToolBarDes.TN_PAUSE)) {
                tit.setEnabled(false);
            } else {
                super.updateToolItem(tit);
            }
        } else {
            if (tn.equals(ToolBarDes.TN_DISCONNECT)) {
                tit.setEnabled(mLogSource.getStatus() == LogSource.stConnected);
            } else if (tn.equals(ToolBarDes.TN_PAUSE)) {
                if (mLogSource.getStatus() == LogSource.stConnected) {
                    tit.setToolTipText(mLogView.isPaused() ? "Resume" : "Pause");
                    tit.setImage(mLogView.isPaused() ? Resources.go_32 : Resources.pause_32);
                } else {
                    tit.setEnabled(false);
                }
            } else {
                super.updateToolItem(tit);
            }
        }
    }
}
