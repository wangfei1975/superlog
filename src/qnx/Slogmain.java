package qnx;


public final class Slogmain extends AppContext {

    private Slogmain() {
        super("QNX SLogInfo", new SlogMainFrame());
    }
    
    static Slogmain mApp = new Slogmain();
    
    static public Slogmain getInstance() {
        return mApp;
    }
    
    public static void main(String[] args) {
        getInstance().run();
    }

}
