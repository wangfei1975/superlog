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

import org.eclipse.swt.widgets.Display;

public class AppContext {

    private SlogMainFrame mMainFrame;
    private Display mDisplay = new Display();

    public Display getDisplay() {
        return mDisplay;
    }

    public AppContext(String caption) {
        Resources.loadResources(this);
        mMainFrame = new SlogMainFrame(caption, mDisplay);
    }

    SlogMainFrame getMainFrame() {
        return mMainFrame;
    }

    public void run() {
        mMainFrame.run();
        Resources.freeResources(this);

    }

}
