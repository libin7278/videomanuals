package ecarx.videomanuals.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarx.log.Lg;

import java.util.ArrayList;
import java.util.List;

import ecarx.videomanuals.R;
import ecarx.videomanuals.application.Settings;
import ecarx.videomanuals.bean.VideoInfoBean;
import ecarx.videomanuals.utils.CacheUtils;
import ecarx.videomanuals.widget.media.AndroidMediaController;
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
    private TextView toast_text_view;
    private TableLayout mHudView;
    private TextView tv_video_title;
    private ImageView iv_back;

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
                        Lg.e(mVideoPosition+"==========");
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
                        Lg.e(mVideoPosition+"==========");

                    }else {
                        Toast.makeText(VideoActivity.this,"已经是第一个",Toast.LENGTH_SHORT).show();
                    }
                }
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
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_video_title = (TextView) findViewById(R.id.tv_video_title);
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
        Lg.e("onPrepared==================");
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
        Lg.e("onCompletion==================");
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
        return false;
    }
}
