package com.penglab.hi5.data;

import com.penglab.hi5.data.model.img.BasicFile;
import com.penglab.hi5.data.model.img.BasicImage;

/**
 * Class that requests image information from the remote data source and
 * maintains an in-memory cache of image information.
 *
 * Created by Jackiexing on 12/09/21
 */
public class ImageInfoRepository {

    private static volatile ImageInfoRepository INSTANCE;

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
}
