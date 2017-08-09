package ecarx.videomanuals.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ecarx.log.Lg;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ecarx.videomanuals.R;
import ecarx.videomanuals.adapter.VideoListAdapter;
import ecarx.videomanuals.bean.VideoInfoBean;
import ecarx.videomanuals.bean.VideoListBean;
import ecarx.videomanuals.utils.CacheUtils;
import ecarx.videomanuals.utils.FileUtils;

public class MainActivity extends AppCompatActivity {
    private List<VideoListBean> videoList = new ArrayList<VideoListBean>();
    private List<VideoInfoBean> videoInfo = new ArrayList<VideoInfoBean>();
    private VideoListAdapter videoListAdapter;
    private RecyclerView rv_video_list;

    public static final String VIDEOPATH = "videoPath";
    public static final String VIDEONAME = "videoName";
    public static final String VIDEOPOSITION = "videoPosition";
    public static final String VIDEOIBFO = "videoInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        String path = "/private/VideoManual";
        boolean fileExists = FileUtils.isFileExists(path);

        Lg.e("fileExists=======>"+fileExists);
        if(fileExists){
            List<File> files = FileUtils.listFilesInDir(path);
            videoList.clear();
            for (int i = 0;i<files.size();i++){
                VideoListBean videoListBean = new VideoListBean();
                VideoInfoBean videoInfoBean = new VideoInfoBean();
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(files.get(i).getPath());

                String videoLength = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                videoListBean.setVideoLength(videoLength);
                videoListBean.setVideoName(files.get(i).getName());
                videoListBean.setVideoPath(files.get(i).getPath());
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(20000*1000);
                Drawable drawable= new BitmapDrawable(bitmap);
                videoListBean.setCover(drawable);
                videoList.add(videoListBean);

                videoInfoBean.setVideoName(files.get(i).getName());
                videoInfoBean.setVideoPath(files.get(i).getPath());
                videoInfo.add(videoInfoBean);
            }

            Lg.e("fileExists=======>"+files);
            CacheUtils.getInstance().put(VIDEOIBFO, (Serializable) videoInfo);
        }else {
            Toast.makeText(this,"暂无视频！！！！！",Toast.LENGTH_SHORT).show();
        }

        if (videoListAdapter == null) {
            videoListAdapter = new VideoListAdapter(videoList);
        }
        rv_video_list.setLayoutManager(new GridLayoutManager(this,3));
        rv_video_list.setAdapter(videoListAdapter);

        videoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(VIDEOPATH,videoList.get(position).getVideoPath());
                intent.putExtra(VIDEONAME,videoList.get(position).getVideoName());
                intent.putExtra(VIDEOPOSITION,position);

                startActivity(intent);
            }
        });
    }

    private void findView() {
        rv_video_list  = (RecyclerView) findViewById(R.id.rv_video_list);
    }
}
