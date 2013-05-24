package qnx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

 

public final class SystemConfigs {

    private Color [] mForeColors;
    private Color [] mBackColors;
    private Color mSearchBackColor;
    
    public SystemConfigs(Display disp) {
        mForeColors = new Color[] {disp.getSystemColor(SWT.COLOR_BLACK),
                disp.getSystemColor(SWT.COLOR_DARK_RED),
                disp.getSystemColor(SWT.COLOR_RED),
                disp.getSystemColor(SWT.COLOR_BLUE),
                disp.getSystemColor(SWT.COLOR_DARK_BLUE),
                disp.getSystemColor(SWT.COLOR_DARK_GREEN),
                disp.getSystemColor(SWT.COLOR_BLACK),
                disp.getSystemColor(SWT.COLOR_DARK_GRAY)
                }; 
        mBackColors = new Color[] {disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_YELLOW),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE),
                disp.getSystemColor(SWT.COLOR_WHITE)
                }; 
        mSearchBackColor = disp.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
    }

    public Color getSearchMarkerBackground() {
        return mSearchBackColor;
    }
    public Color getLogForeground(int level) {
        return mForeColors[level];
    }
    
    public Color getLogBackground(int level) {
        return mBackColors[level];
    }
}
