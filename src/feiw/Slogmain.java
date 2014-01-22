package feiw;



public final class Slogmain  {
 
    static AppContext mApp = new AppContext("SuperLog");
    
    static public AppContext getApp() {
        return mApp;
    }
    /*
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
    */
    /*
    public static void testParse() {
        
        ArrayList<String> arr = new ArrayList<String>();
        try {
          FileInputStream is = new FileInputStream("/Users/feiwang/qnx/s.log");
          BufferedReader din = new BufferedReader(new InputStreamReader(is));
          long startTime = System.currentTimeMillis();
          String str = din.readLine();
          while (str != null) {
              arr.add(str);
            //  parseLog(str);
              str = din.readLine();
          }
          long endTime = System.currentTimeMillis();
          System.out.println("took " + (endTime - startTime) + " millseconds");
          is.close();
          
          
          String targetstr = "Avi";
          System.out.println("string KMP search test start: " + arr.size() + " strings ingore case:" );
          startTime = System.currentTimeMillis();
          StringPattern pt = new StringPattern(targetstr, false);
          int results = 0;
          for (String s : arr) {
              if (pt.isContainedBy(s) >= 0) {
                  results++;
              }
          }
          endTime = System.currentTimeMillis();
          System.out.println("found " + results + " results, took " + (endTime - startTime) + " millseconds");
          
          System.out.println("string KMP search test start: " + arr.size() + " strings case sensitive:" );
          startTime = System.currentTimeMillis();
           pt = new StringPattern(targetstr, true);
           results = 0;
          for (String s : arr) {
              if (pt.isContainedBy(s) >= 0) {
                  results++;
              }
          }
          endTime = System.currentTimeMillis();
          System.out.println("found " + results + " results, took " + (endTime - startTime) + " millseconds");
          
          System.out.println("string JAVA search test start: " + arr.size() + " strings ignore case:" );
          startTime = System.currentTimeMillis();
          String spt = targetstr.toLowerCase();
          results = 0;
          for (String s : arr) {
              if (s.toLowerCase().contains(spt)) {
                  results++;
              }
          }
          endTime = System.currentTimeMillis();
          System.out.println("found " + results + " results, took " + (endTime - startTime) + " millseconds");
          
          System.out.println("string JAVA search test start: " + arr.size() + " strings case senstive:" );
          startTime = System.currentTimeMillis();
           spt =targetstr;
          results = 0;
          for (String s : arr) {
              if (s.contains(spt)) {
                  results++;
              }
          }
          endTime = System.currentTimeMillis();
          System.out.println("found " + results + " results, took " + (endTime - startTime) + " millseconds");
          
          
          
          
          System.out.println("string apache search test start: " + arr.size() + " strings case insensitive:" );
          startTime = System.currentTimeMillis();
           spt =targetstr;
          results = 0;
          for (String s : arr) {
              if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(s, spt)) {
                  results++;
              }
          }
          endTime = System.currentTimeMillis();
          System.out.println("found " + results + " results, took " + (endTime - startTime) + " millseconds");
          
      } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
        
    }
    */
 
    public static void main(String[] args) {
 
//            Gson gson = new Gson();
   //     Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        /*
        String js = SystemConfigs.instance().toJson();
        System.out.println(js);
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().registerTypeAdapter(Color.class, new SystemConfigs.ColorSerializer())
                .registerTypeAdapter(Color.class, new ColorDeserializer()).create();
        
        SystemConfigs cfgs = gson.fromJson(js, SystemConfigs.class);
        System.out.println(cfgs.toJson());
        */
  //      StringPattern pt = new StringPattern("avi", false);
//        pt.isContainedBy("a_vifdsa");
       mApp.run();
        
       
   //     System.out.println(new SimpleDateFormat("MMM dd HH:mm:ss.SSS").format(new Date()));
   //     testParse();
    }

}
