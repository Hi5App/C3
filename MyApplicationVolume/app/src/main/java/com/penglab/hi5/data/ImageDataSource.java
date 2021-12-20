package com.penglab.hi5.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsImage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Class that handles image information.
 *
 * Created by Jackiexing on 12/09/21
 */
public class ImageDataSource {

    private final MutableLiveData<Result> result = new MutableLiveData<>();

    public LiveData<Result> getResult() {
        return result;
    }

    public void getBrainList(){
        HttpUtilsImage.getBrainListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void getNeuronList(String brainId){
        HttpUtilsImage.getNeuronListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), brainId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void getAnoList(String neuronId){
        HttpUtilsImage.getAnoListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), neuronId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void downloadImage(String imageInfo, int offsetX, int offsetY, int offsetZ, int size){
        HttpUtilsImage.downloadImageWithOkHttp(
                InfoCache.getAccount(), InfoCache.getToken(), imageInfo,
                offsetX, offsetY, offsetZ, size, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
