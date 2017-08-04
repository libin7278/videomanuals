package ecarx.videomanuals.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ecarx.videomanuals.R;
import ecarx.videomanuals.bean.VideoListBean;

/**
 * Created by doudou on 2017/8/4.
 */

public class VideoListAdapter extends BaseQuickAdapter<VideoListBean, BaseViewHolder> {

    public VideoListAdapter(@Nullable List<VideoListBean> data) {
        super(R.layout.video_list_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoListBean item) {
        helper.setImageDrawable(R.id.iv_cover, item.getCover())
                .setText(R.id.tv_video_name, item.getVideoName()+"  时长："+item.getVideoLength());
    }
}
