package qnx;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;


public class AppContext {

    private SystemConfigs mConfigs;
    private SlogMainFrame  mMainFrame;
    private Display mDisplay =  new Display();

    
    public Display getDisplay() {
        return mDisplay;
    }
    public SystemConfigs getConfigs() {
        return mConfigs;
    }
    public AppContext(String caption){
        Resources.loadResources(this);
        mConfigs = new SystemConfigs(mDisplay);
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
