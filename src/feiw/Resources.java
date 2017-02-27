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

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

public final class Resources {
    public static Image openfile_32 = null;
    public static Image openfile_16 = null;
    public static Image search_32 = null;
    public static Image down_32 = null;
    public static Image up_32 = null;
    public static Image go_32 = null;
    public static Image pause_32 = null;
    public static Image filter_32 = null;
    public static Image filter_16 = null;
    public static Image connected_32 = null;
    public static Image connected_16 = null;
    public static Image disconnected_32 = null;
    public static Image trash_32 = null;
    public static Image copy_32 = null;
    public static Image copyall_32 = null;
    public static Image copy_16 = null;
    public static Image copyall_16 = null;

    public static Image save_32 = null;
    public static Image search_16 = null;
    public static Image config_32 = null;
    public static Image help_32 = null;
    public static Image android_32 = null;
    public static Image androidpause_32 = null;
    public static Image disconnectedand_32 = null;
    public static Image android_log_32 = null;

    public static Image fifo_32 = null;
    public static Image fifo_16 = null;

    public static ArrayList<Image> mIcons = new ArrayList<Image>(30);

    static Image loadIcon(AppContext ctx, String name) {
        Image icon = null;
        InputStream is = ctx.getClass().getClassLoader().getResourceAsStream(name + ".png");
        if (is == null) {
            is = ctx.getClass().getClassLoader().getResourceAsStream("/resources/" + name + ".png");
        }
        if (is == null) {
            icon = new Image(ctx.getDisplay(), "resources/" + name + ".png");
        } else {
            icon = new Image(ctx.getDisplay(), is);
        }
        mIcons.add(icon);
        return icon;
    }

    static void loadResources(AppContext ctx) {
        openfile_32 = loadIcon(ctx, "openfile_32");
        openfile_16 = loadIcon(ctx, "openfile_16");
        search_32 = loadIcon(ctx, "search_32");
        down_32 = loadIcon(ctx, "down_32");
        up_32 = loadIcon(ctx, "up_32");
        go_32 = loadIcon(ctx, "go_32");
        pause_32 = loadIcon(ctx, "pause_32");
        filter_32 = loadIcon(ctx, "filter_32");
        filter_16 = loadIcon(ctx, "filter_16");
        connected_32 = loadIcon(ctx, "connected_32");
        connected_16 = loadIcon(ctx, "connected_16");
        disconnected_32 = loadIcon(ctx, "disconnected_32");
        android_log_32 = loadIcon(ctx, "android_log_32");

        trash_32 = loadIcon(ctx, "trash_32");
        copy_32 = loadIcon(ctx, "copy_32");
        copyall_32 = loadIcon(ctx, "copyall_32");
        copy_16 = loadIcon(ctx, "copy_16");
        copyall_16 = loadIcon(ctx, "copyall_16");
        save_32 = loadIcon(ctx, "save_32");
        search_16 = loadIcon(ctx, "search_16");
        config_32 = loadIcon(ctx, "config_32");
        help_32 = loadIcon(ctx, "help_32");

        android_32 = loadIcon(ctx, "android_32");
        disconnectedand_32 = loadIcon(ctx, "disconnectedand_32");
        androidpause_32 = loadIcon(ctx, "androidpause_32");

        fifo_32 = loadIcon(ctx, "fifo_32");
        fifo_16 = loadIcon(ctx, "fifo_16");
    }

    static void freeResources(AppContext ctx) {
        for (Image im : mIcons) {
            im.dispose();
        }
    }
}
