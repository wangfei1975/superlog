package qnx;

/*
 * 
 * appcontext 
 *      mainframe 
 *          tabframe1, tabframe2, ..., tabframe m
 *      maindocs
 *          doc1, doc2, ..., doc n
 *      resources
 *      
 * main menu:
 *    new tab and connect to 
 *    new tab and open file
 *    new tab from filter
 *    close tab
 * 
 * */
public final class Slogmain  {
 
    static AppContext mApp = new AppContext("QSlog");
    
    static public AppContext getApp() {
        return mApp;
    }
    
    public static void main(String[] args) {
        System.out.println("fdsa");
        mApp.run();
    }

}
