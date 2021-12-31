package com.penglab.hi5.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.data.model.img.BasicFile;
import com.penglab.hi5.data.model.img.BasicImage;
import com.penglab.hi5.data.model.img.FilePath;

/**
 * Class that requests image information from the remote data source and
 * maintains an in-memory cache of image information.
 *
 * Created by Jackiexing on 12/09/21
 */
public class ImageInfoRepository {

    private static volatile ImageInfoRepository INSTANCE;

    private final MutableLiveData<FilePath<?>> screenCapture = new MutableLiveData<>();

    private BasicImage basicImage = new BasicImage();

    private BasicFile basicFile = new BasicFile();

    private ImageInfoRepository(){ }

    public static ImageInfoRepository getInstance(){
        if (INSTANCE == null){
            synchronized (ImageInfoRepository.class){
                if (INSTANCE == null){
                    INSTANCE = new ImageInfoRepository();
                }
            }
        }
        return INSTANCE;
    }

    public BasicImage getBasicImage() {
        return basicImage;
    }

    public void setBasicImage(BasicImage basicImage) {
        this.basicImage = basicImage;
    }

    public BasicFile getBasicFile() {
        return basicFile;
    }

    public void setBasicFile(BasicFile basicFile) {
        this.basicFile = basicFile;
    }

    public MutableLiveData<FilePath<?>> getScreenCapture() {
        return screenCapture;
    }
}
