package feiw;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;


public class AppContext {

    
    private SlogMainFrame  mMainFrame;
    private Display mDisplay =  new Display();

    
    public Display getDisplay() {
        return mDisplay;
    }
    public AppContext(String caption){
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
