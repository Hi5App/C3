package com.penglab.hi5.data.model.img;

/**
 * Created by Jackiexing on 12/22/21
 */
public class FilePath<T> {

    private final T path;

    public FilePath(T path) {
        this.path = path;
    }

    public T getPath() {
        return path;
    }
}
