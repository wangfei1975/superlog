package qnx;


public class AppContext {
    
    private MainFrame  mMainFrame;
    
    private SystemConfigs mConfigs;
    
    public SystemConfigs getConfigs() {
        return mConfigs;
    }
    public AppContext(String caption, MainFrame mf){
        mMainFrame = mf;
        Resources.loadResources(this);
        mConfigs = new SystemConfigs(mf.getDisplay());
    }

    MainFrame getMainFrame() {
        return mMainFrame;
    }
    
    public void run() {
        mMainFrame.run();
    }
    
}
