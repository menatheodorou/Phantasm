package com.phantasm.phantasm.main.model;

import android.content.Context;

import com.phantasm.phantasm.R;

import junit.framework.Assert;

/**
 * Created by ABC on 2015/12/17.
 */
public enum PTTabID {
    TAB_CREATE (0),
    TAB_CONNECT (1),
    TAB_FLICK (2);

    private int value;

    private PTTabID(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public String getValue(Context context) {
        String value = null;
        switch (this) {
            case TAB_CREATE:
                value = context.getString(R.string.create);
                break;
            case TAB_CONNECT:
                value = context.getString(R.string.connect);
                break;
            case TAB_FLICK:
                value = context.getString(R.string.flick);
                break;
            default:
                Assert.assertTrue("Unknown type" == null);
        }

        return value;
    }

    public boolean compare(int value){return this.value == value;}

    public static PTTabID getValue(int value) {
        PTTabID[] as = PTTabID.values();
        for(int i = 0; i < as.length; i++) {
            if(as[i].compare(value))
                return as[i];
        }

        return PTTabID.TAB_CREATE;
    }
}
