package movi.lib.camera;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

public class VideoNewSelectActivity extends CameraBaseActivity {
    /**
     * 图片展示器
     */
    private GridView gridview;
    /**
     * 图片适配器
     */
    private ViewNewSelectAdapter adapter;
    /**
     * 数据集
     */
    private ArrayList<ViewNewSelectBean> list;

    private ImageView img_back;

    /**
     * 显示图片的宽
     */
    private int width;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_video_new_select;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void findViews() {
        img_back = (ImageView) findViewById(R.id.video_new_img_back);
        gridview = (GridView) findViewById(R.id.video_new_select_gridview);
        width = (getWindowManager().getDefaultDisplay().getWidth() - DisplayUtil.dip2px(VideoNewSelectActivity.this, 60)) / 3;
    }

    @Override
    protected void init() {
        list = new ArrayList<ViewNewSelectBean>();
        adapter = new ViewNewSelectAdapter(list, width, VideoNewSelectActivity.this);
        gridview.setAdapter(adapter);
        getList();
    }

    @Override
    protected void widgetListener() {
        img_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*	Bundle bundle = new Bundle();
                bundle.putSerializable("serializable", list.get(position));
				Intent intent = new Intent(VideoNewSelectActivity.this, VideoNewCutActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);*/


                //测试播放相关视频
                Intent it = new Intent(VideoNewSelectActivity.this, VideoActivity.class);
                it.putExtra("path", list.get(position).getPath());
                startActivity(it);
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                adapter.notifyDataSetChanged();
            }
        }

        ;
    };

    /**
     * 获取数据
     *
     * @version 1.0
     * @createTime 2015年6月16日, 下午6:14:09
     * @updateTime 2015年6月16日, 下午6:14:09
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void getList() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 若为图片则为MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(originalUri, null, null, null, null);
                if (cursor == null) {
                    return;
                }
                while (cursor.moveToNext()) {
                    ViewNewSelectBean bean = new ViewNewSelectBean();
                    bean.set_id(cursor.getLong(cursor.getColumnIndex("_ID")));
                    bean.setName(cursor.getString(cursor.getColumnIndex("_display_name")));// 视频名字
                    bean.setPath(cursor.getString(cursor.getColumnIndex("_data")));// 路径
                    bean.setWidth(cursor.getInt(cursor.getColumnIndex("width")));// 视频宽
                    bean.setHeight(cursor.getInt(cursor.getColumnIndex("height")));// 视频高
                    bean.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));// 时长
                    if (new File(bean.getPath()).exists()) {
                        list.add(bean);
                    }


                }

                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);

                // /data/data/com.android.providers.media/databases/external.db
                // {5dd10730} 数据库位置
            }
        }).start();
    }

    private class ViewNewSelectAdapter extends BaseAdapter {
        private Context context;
        /**
         * 数据集
         */
        private ArrayList<ViewNewSelectBean> list;
        /**
         * 图片宽
         */
        private int width;

        public ViewNewSelectAdapter(ArrayList<ViewNewSelectBean> list, int width, Context context) {
            this.list = list;
            this.width = width;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final ViewNewSelectBean bean = list.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_video_new_select_gridview, null);
                viewHolder.txt = (TextView) convertView.findViewById(R.id.item_video_new_select_txt_time);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.item_video_new_select_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            LayoutParams layoutParams = (LayoutParams) viewHolder.img.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            viewHolder.img.setLayoutParams(layoutParams);
            // 获取图片
            Bitmap bitmap = getVideoThumbnail(bean.getPath(), width, width, MediaStore.Images.Thumbnails.MICRO_KIND);
            if (bitmap != null) {
                // 设置图片
                viewHolder.img.setImageBitmap(bitmap);
            }
            // 设置时长
            viewHolder.txt.setText(String.format("时长：%1$s s", bean.getDuration() / 1000));

            return convertView;
        }

        /**
         * 获取视频的缩略图
         * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
         * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
         *
         * @param videoPath 视频的路径
         * @param width     指定输出视频缩略图的宽度
         * @param height    指定输出视频缩略图的高度度
         * @param kind      参照MediaStore.Images.
         *                  Thumbnails类中的常量MINI_KIND和MICRO_KIND。
         *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
         * @return 指定大小的视频缩略图
         */
        private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
            Bitmap bitmap = null;
            // 获取视频的缩略图
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            return bitmap;
        }

        private class ViewHolder {
            private ImageView img;
            private TextView txt;
        }

    }

}
