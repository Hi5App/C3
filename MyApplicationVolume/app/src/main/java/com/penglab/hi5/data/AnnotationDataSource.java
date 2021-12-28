package com.penglab.hi5.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsImage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Yihang zhu 12/27/21
 */
public class AnnotationDataSource {

    private final MutableLiveData<Result> result = new MutableLiveData<>();

    public LiveData<Result> getResult() {
        return result;
    }

    public void downloadSWC(String brainId, String roi, int x, int y, int z, int size) {
        try {
            HttpUtilsImage.getBBSwcWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), brainId, roi, x, y, z, size, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            byte[] fileContent = response.body().bytes();
                            String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Resources/Annotation";
                            if (!FileHelper.storeFile(storePath, "1.eswc", fileContent)) {
                            }
                            result.postValue(new Result.Success(storePath + "/" + "1.eswc"));
                        } else {
                            result.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.postValue(new Result.Error(new Exception("Fail to download swc file !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }
}
