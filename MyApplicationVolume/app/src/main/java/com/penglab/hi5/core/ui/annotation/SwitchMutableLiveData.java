package com.penglab.hi5.core.ui.annotation;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Jackiexing on 12/28/21
 */
public class SwitchMutableLiveData<T> extends MutableLiveData<T> {

    private final T defaultValue;

    /**
     * Creates a SwitchMutableLiveData initialized with the given {@code value}.
     *
     * @param value initial value
     */
    public SwitchMutableLiveData(T value) {
        super(value);
        defaultValue = value;
    }

    public boolean postSwitchableValue(T value) {
        if (value != getValue()) {
            super.postValue(value);
            return true;
        }else {
            super.postValue(defaultValue);
            return false;
        }
    }

    public boolean setSwitchableValue(T value) {
        if (value != getValue()) {
            super.setValue(value);
            return true;
        }else {
            super.setValue(defaultValue);
            return false;
        }
    }

}
