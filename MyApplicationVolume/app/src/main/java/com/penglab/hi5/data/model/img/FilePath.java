package com.penglab.hi5.data.model.img;

/**
 * Created by Jackiexing on 12/22/21
 */
public class FilePath<T> {

    private final T data;

    public FilePath(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
