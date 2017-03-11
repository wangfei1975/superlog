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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolItem;

public class FileTabFrame extends SlogTabFrame {

    public FileTabFrame(CTabFolder parent, String txt, int style, String fname) throws FileNotFoundException {
        super(parent, txt, style, new FileLogSource(fname), null, LogParser.newLogParser(new FileInputStream(fname)),
                null);
        setImage(Resources.openfile_32);
        ((FileLogSource) mLogSource).load(mLogView.getLogParser());

    }

    @Override
    void updateToolItem(ToolItem tit) {
        String tn = (String) tit.getData();
        if (tn == null || tn.isEmpty()) {
            tit.setEnabled(false);
            return;
        }
        if (tn.equals(ToolBarDes.TN_DISCONNECT)) {
            tit.setEnabled(false);
        } else if (tn.equals(ToolBarDes.TN_PAUSE)) {
            tit.setEnabled(false);
        } else if (tn.equals(ToolBarDes.TN_CLEAR)) {
            tit.setEnabled(false);
        } else {
            tit.setEnabled(true);
        }
        super.updateToolItem(tit);
    }

}
