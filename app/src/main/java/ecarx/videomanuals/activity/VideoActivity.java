package ecarx.videomanuals.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import ecarx.videomanuals.R;
import ecarx.videomanuals.application.Settings;
import ecarx.videomanuals.bean.VideoInfoBean;
import ecarx.videomanuals.utils.CacheUtils;
import ecarx.videomanuals.widget.media.AndroidMediaController;
import ecarx.videomanuals.widget.media.IProgressChangeListener;
import ecarx.videomanuals.widget.media.IjkVideoView;
import ecarx.videomanuals.widget.media.MeasureHelper;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static ecarx.videomanuals.activity.MainActivity.VIDEOIBFO;
import static ecarx.videomanuals.activity.MainActivity.VIDEONAME;
import static ecarx.videomanuals.activity.MainActivity.VIDEOPATH;
import static ecarx.videomanuals.activity.MainActivity.VIDEOPOSITION;

public class VideoActivity extends AppCompatActivity implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener {

    private IjkVideoView ijk_video_view;
    private LinearLayout ll_progress_toast;
    private TextView toast_text_view;
    private TableLayout mHudView;
    private TextView tv_video_title;
    private TextView tv_progress_change_time;
    private TextView tv_progress_total_time;
    private ImageView iv_back;
    private ImageView iv_reverse_speed;

    private AndroidMediaController mMediaController;
    Settings mSettings;
    private String mVideoPath;
    private String mVideoName;
    private int mVideoPosition;

    private boolean mBackPressed;

    private List<VideoInfoBean> videoInfo = new ArrayList<VideoInfoBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initData();

        findView();

        setVideoView();

        onlistener();
    }

    private void initData() {
        mVideoPath = getIntent().getStringExtra(VIDEOPATH);
        mVideoName = getIntent().getStringExtra(VIDEONAME);
        mVideoPosition = getIntent().getIntExtra(VIDEOPOSITION,0);

        videoInfo = (List<VideoInfoBean>) CacheUtils.getInstance().getSerializable(VIDEOIBFO);
    }

    private void onlistener() {
        ijk_video_view.setOnPreparedListener(this);
        ijk_video_view.setOnCompletionListener(this);
        ijk_video_view.setOnErrorListener(this);
        ijk_video_view.setOnInfoListener(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoInfo != null){
                    if(mVideoPosition < videoInfo.size()-1){
                        ijk_video_view.setVideoPath(videoInfo.get(mVideoPosition = mVideoPosition+1).getVideoPath());
                        String videoName = videoInfo.get(mVideoPosition).getVideoName();
                        tv_video_title.setText(videoName.substring(videoName.lastIndexOf("/") + 1));
                    }else {
                        Toast.makeText(VideoActivity.this,"已经是最后一个",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoInfo != null){
                    if(mVideoPosition > 0){
                        ijk_video_view.setVideoPath(videoInfo.get(mVideoPosition = mVideoPosition-1).getVideoPath());
                        String videoName = videoInfo.get(mVideoPosition).getVideoName();
                        tv_video_title.setText(videoName.substring(videoName.lastIndexOf("/") + 1));
                    }else {
                        Toast.makeText(VideoActivity.this,"已经是第一个",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        final long[] oldPersion = {0};
        mMediaController.setProgressLIstener(new IProgressChangeListener() {
            @Override
            public void onStartTrackingTouch() {
                ll_progress_toast.setVisibility(View.VISIBLE);

            }
            @Override
            public void onProgressChanged(String s, long newposition) {
                tv_progress_change_time.setText(s);

                if(oldPersion[0] > newposition){
                    iv_reverse_speed.setImageResource(R.drawable.icon_media_reverse);
                    oldPersion[0] = newposition;
                }else {
                    iv_reverse_speed.setImageResource(R.drawable.icon_media_speed);
                    oldPersion[0] = newposition;
                }
            }

            @Override
            public void onStopTrackingTouch() {
                ll_progress_toast.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void setVideoView() {
        mSettings = new Settings(this);

        // init UI
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        //setData
//        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(mVideoPath);
        ijk_video_view.setHudView(mHudView);
        ijk_video_view.setVideoPath(mVideoPath);
        ijk_video_view.setMediaController(mMediaController);
        tv_video_title.setText(mVideoName.substring(mVideoName.lastIndexOf("/") + 1));

        //设置scale
        int aspectRatio = ijk_video_view.toggleAspectRatio();
        String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
        toast_text_view.setText(aspectRatioText);
        mMediaController.showOnce(toast_text_view);

    }

    private void findView() {
        ijk_video_view = (IjkVideoView) findViewById(R.id.ijk_video_view);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        toast_text_view = (TextView) findViewById(R.id.toast_text_view);
        tv_progress_total_time = (TextView) findViewById(R.id.tv_progress_total_time);
        tv_progress_change_time = (TextView) findViewById(R.id.tv_progress_change_time);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_reverse_speed = (ImageView) findViewById(R.id.iv_reverse_speed);
        tv_video_title = (TextView) findViewById(R.id.tv_video_title);
        ll_progress_toast = (LinearLayout) findViewById(R.id.ll_progress_toast);
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ijk_video_view.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !ijk_video_view.isBackgroundPlayEnabled()) {
            ijk_video_view.stopPlayback();
            ijk_video_view.release(true);
            ijk_video_view.stopBackgroundPlay();
        } else {
            ijk_video_view.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    /**
     * 视频准备播放
     *
     * @param iMediaPlayer
     */
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        //监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
        if (mVideoPath != null) {
            ijk_video_view.start();
        }
    }

    /**
     * 播放完成
     *
     * @param iMediaPlayer
     */
    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        finish();
    }

    /**
     * 播放失败
     *
     * @param iMediaPlayer
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    /**
     * 设置视频相关信息
     *
     * @param iMediaPlayer
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        tv_progress_total_time.setText(stringForTime( (int) iMediaPlayer.getDuration()));

        return false;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
