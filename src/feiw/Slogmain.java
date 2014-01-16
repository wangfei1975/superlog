package feiw;

public final class Slogmain  {
 
    static AppContext mApp = new AppContext("SuperLog");
    
    static public AppContext getApp() {
        return mApp;
    }
    
    public static void main(String[] args) {
        
  //      StringPattern pt = new StringPattern("avi", false);
//        pt.isContainedBy("a_vifdsa");
 //       System.out.print(Character.toLowerCase('_'));
       mApp.run();
    }

}
