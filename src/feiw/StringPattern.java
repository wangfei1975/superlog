package feiw;
public final class StringPattern {
    private final String   mOrgStr;
    private final char []  mPattern;
    private final int  []  mTable;
    private final boolean  mCaseSenstive;
    public final String toString() {
        return mOrgStr;
    }
    private int [] createTable(final char[] pattern) {
        int patlen = pattern.length;
        int[] t = new int[patlen];
        int i = 2;
        int j = 0;
        t[0] = -1;
        t[1] = 0;
        while (i < patlen) {
            if (pattern[i - 1] == pattern[j]) {
                t[i] = j + 1;
                j++;
                i++;
            } else if (j > 0) {
                j = t[j];
            } else {
                t[i] = 0;
                i++;
                j = 0;
            }
        }
        return t;
    }
    public StringPattern(final String pattern, final boolean caseSenstive) {
        mOrgStr = pattern;
        mCaseSenstive = caseSenstive;
        if (!caseSenstive) {
            mPattern = pattern.toLowerCase().toCharArray();
        } else {
            mPattern = pattern.toCharArray();
        }
        mTable = createTable(mPattern);
    }
    public int isContainedBy(final String str) {
        int r = -1;
        if (mCaseSenstive) {
            r =  isCaseSenstiveContainedBy(str);
            /*
            if (r != str.indexOf(mOrgStr)) {
                
                  System.out.println("match error:: str = " + str);
                  System.out.println("pattern =  " + mOrgStr);
                
            }
            */
        } else {
            r =  isCaseInsenstiveContainedBy(str);
        /*
            if (r != str.toLowerCase().indexOf(mOrgStr.toLowerCase())) {
            System.out.println("ci match error:: str = " + str);
            System.out.println("pattern =  " + mOrgStr);
            System.out.println("r =  " + r + "  index of = " + str.toLowerCase().indexOf(mOrgStr.toLowerCase()));
            }  
            */
        }
        return r;
    }

    private int isCaseInsenstiveContainedBy(final String str) {
        final int slen = str.length();
        final int plen = mPattern.length;
        final char [] pat = mPattern;
        final int  [] t = mTable;
        
        int m = 0;
        int i = 0;
        while(((m+i) < slen) ) {
            if (Character.toLowerCase(str.charAt(m+i)) == pat[i]) {
                if (i == plen - 1) {
                    return m;
                }
                i++;
            } else {
                m += i - t[i];
                i = t[i] > -1 ? t[i] : 0;
            }
        }
 
        return -1;
    }
    private int isCaseSenstiveContainedBy(final String str) {
        final int slen = str.length();
        final int plen = mPattern.length;
        final char [] pat = mPattern;
        final int  [] t = mTable;
        
        int m = 0;
        int i = 0;
        while(((m+i) < slen) && i < plen) {
            if (str.charAt(m+i) == pat[i]) {
                if (i == plen - 1) {
                    return m;
                }
                i++;
            } else {
                m += i - t[i];
                i = t[i] > -1 ? t[i] : 0;
            }
        }
        return -1;
    }
   
}
