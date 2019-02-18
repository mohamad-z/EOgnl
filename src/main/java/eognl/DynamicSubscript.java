/*
 * Decompiled with CFR 0.139.
 */
package eognl;

public class DynamicSubscript {
    public static final int FIRST = 0;
    public static final int MID = 1;
    public static final int LAST = 2;
    public static final int ALL = 3;
    public static final DynamicSubscript first = new DynamicSubscript(FIRST);
    public static final DynamicSubscript mid = new DynamicSubscript(MID);
    public static final DynamicSubscript last = new DynamicSubscript(LAST);
    public static final DynamicSubscript all = new DynamicSubscript(ALL);
    private int flag;

    private DynamicSubscript(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }

    public String toString() {
        switch (this.flag) {
            case 0: {
                return "^";
            }
            case 1: {
                return "|";
            }
            case 2: {
                return "$";
            }
            case 3: {
                return "*";
            }
        }
        return "?";
    }
}

