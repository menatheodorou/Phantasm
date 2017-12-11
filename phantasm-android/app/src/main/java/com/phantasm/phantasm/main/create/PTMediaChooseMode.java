package com.phantasm.phantasm.main.create;

/**
 * Created by ABC on 2015/12/17.
 */
public enum PTMediaChooseMode {
    CHOOSE_CHANNEL(1),
    CHOOSE_MEDIA(2);

    private int value;

    private PTMediaChooseMode(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public boolean compare(int value){return this.value == value;}

    public static PTMediaChooseMode getValue(int value) {
        PTMediaChooseMode[] as = PTMediaChooseMode.values();
        for(int i = 0; i < as.length; i++) {
            if(as[i].compare(value))
                return as[i];
        }

        return PTMediaChooseMode.CHOOSE_CHANNEL;
    }
}
