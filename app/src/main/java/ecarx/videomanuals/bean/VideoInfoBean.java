package ecarx.videomanuals.bean;

import java.io.Serializable;

/**
 * Created by doudou on 2017/8/8.
 */

public class VideoInfoBean implements Serializable {
    String videoName;
    String videoLength;
    String videoPath;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }
}
