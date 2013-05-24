package qnx;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MainFrame {
    
    private Display mDisplay;
    private Shell   mShell;
    
    
    public MainFrame() {
        mDisplay = new Display();
        mShell = new Shell(mDisplay);
    }
    
    public Display getDisplay() {
        return mDisplay;
    }
    public Shell getShell() {
        return mShell;
    }
    
    public void run() {
        mShell.open();
        Display display = getDisplay();
        while (!mShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
