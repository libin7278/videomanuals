package ecarx.videomanuals.widget.media;

/**
 * Created by doudou on 2017/8/9.
 */

public interface IProgressChangeListener {
    void onStartTrackingTouch();

    void onProgressChanged(String s, long newposition);

    void onStopTrackingTouch();
}
