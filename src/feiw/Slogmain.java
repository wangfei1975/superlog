package feiw;

public final class Slogmain  {
 
    static AppContext mApp = new AppContext("SuperLog");
    
    static public AppContext getApp() {
        return mApp;
    }
    
    public static void main(String[] args) {
        mApp.run();
    }

}
