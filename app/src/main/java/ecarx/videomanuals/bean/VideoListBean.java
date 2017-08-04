package ecarx.videomanuals.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by doudou on 2017/8/4.
 */

public class VideoListBean {
    String videoName;
    String videoLength;
    Drawable cover;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    String videoPath;

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

    public Drawable getCover() {
        return cover;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }

}
