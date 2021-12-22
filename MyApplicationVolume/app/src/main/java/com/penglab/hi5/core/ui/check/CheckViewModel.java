package com.penglab.hi5.core.ui.check;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jackiexing on 12/18/21
 */
public class CheckViewModel extends ViewModel {

    private final MutableLiveData<String> brainId = new MutableLiveData<>();
    private final MutableLiveData<String> neuronId = new MutableLiveData<>();
    private final MutableLiveData<String> anoId = new MutableLiveData<>();
    private ImageDataSource imageDataSource;

    public CheckViewModel(ImageDataSource imageDataSource) {
        this.imageDataSource = imageDataSource;
    }

    LiveData<String> getBrainId() {
        return brainId;
    }

    LiveData<String> getNeuronId() {
        return neuronId;
    }

    LiveData<String> getAnoId() {
        return anoId;
    }

    ImageDataSource getImageDataSource() {
        return imageDataSource;
    }

    public void getBrainList(){
        imageDataSource.getBrainList();
    }

    public void updateBrainList(){
        imageDataSource.getBrainList();
    }

    public void updateImageResult(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray) {
                try {
                    JSONObject jsonObject = ((JSONArray) data).getJSONObject(0);
                    String firstKey = jsonObject.keys().next().toString();
                    if (firstKey.equals("imageid")) {
                        String imageId = jsonObject.getString("imageid");
                        List<String> brainList = (List<String>) jsonObject.get("detail");
                        String url = jsonObject.getString("url");
                    } else if (firstKey.equals("somaid")) {
                        String somaId = jsonObject.getString("somaid");
                        String imageId = jsonObject.getString("imageid");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
