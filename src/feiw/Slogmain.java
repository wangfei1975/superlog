package feiw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class Slogmain  {
 
    static AppContext mApp = new AppContext("SuperLog");
    
    static public AppContext getApp() {
        return mApp;
    }
    static final String[] seperator = { "    ", " ", " ", " " };
    public static String[] parseLog(final String str) {
        final String [] ret = new String[5];
        int idx = 0, nextidx;
        final int slen = str.length();
        for (int i = 0; i < 4; i++) {
            nextidx = str.indexOf(seperator[i], idx);
            if (nextidx <= 0) {
                ret[0] = ret[1] = ret[2] = ret[3] = null;
                ret[4] = str;
                return ret;
            }
            ret[i] = str.substring(idx, nextidx);
            idx = nextidx + seperator[i].length();
            while (slen > idx && str.charAt(idx) == ' ')
                idx++;
        }
        ret[4] = str.substring(idx);
        int lev;
        if (!ret[1].isEmpty()) {
             lev = ret[1].charAt(0) - '0';
            if (lev < 0 || lev > 7) {
                lev = 6;
            }
        }
        return ret;
    }
    public static void testParse() {
        
        ArrayList<String> arr = new ArrayList<String>();
        try {
          FileInputStream is = new FileInputStream("/Users/feiwang/qnx/s.log");
          BufferedReader din = new BufferedReader(new InputStreamReader(is));
          long startTime = System.currentTimeMillis();
          String str = din.readLine();
          while (str != null) {
              arr.add(str);
              parseLog(str);
              str = din.readLine();
          }
          long endTime = System.currentTimeMillis();
          System.out.println("took " + (endTime - startTime) + " millseconds");
          is.close();
          
      } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
        
    }
    public static void main(String[] args) {
        
  //      StringPattern pt = new StringPattern("avi", false);
//        pt.isContainedBy("a_vifdsa");
 //       System.out.print(Character.toLowerCase('_'));
       mApp.run();
        
   //     System.out.println(new SimpleDateFormat("MMM dd HH:mm:ss.SSS").format(new Date()));
//        testParse();
    }

}
