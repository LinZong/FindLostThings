package misaka.nemesiss.com.findlostthings.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import misaka.nemesiss.com.findlostthings.Activity.LostThingDetailActivity;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;

import java.util.List;

public class LostThingsInfoAdapter extends RecyclerView.Adapter<LostThingsInfoAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<LostThingsInfo> mLostThingsInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView LostThingsInfoImage;
        TextView LostThingsInfoTextView;
        public int CurrentPosition;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            LostThingsInfoImage = (ImageView) view.findViewById(R.id.lost_things_info_image);
            LostThingsInfoTextView = (TextView) view.findViewById(R.id.lost_things_info_textview);
        }
    }

    public LostThingsInfoAdapter(List<LostThingsInfo> lostThingsInfoList, Activity activity) {
        mLostThingsInfoList = lostThingsInfoList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_things_info, parent, false);
        return new ViewHolder(view);
    }

    private void EnterThingDetail(int position) {
        LostThingsInfo lti = mLostThingsInfoList.get(position);
        Intent intent = new Intent(mActivity, LostThingDetailActivity.class);
        intent.putExtra("LostThingsInfo", lti);
        mActivity.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind onClick handler.
        int pos = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(v -> EnterThingDetail(pos));

        LostThingsInfo lostThingsInfo = mLostThingsInfoList.get(pos);
        //整个发布步骤大致为 填写失物信息 --> 拍照 --> 照片传到存储桶 --> 上传完成后，得到照片位于存储桶的URL地址 --> 将照片URL放入数组，
        // 转成JSON数组之后连同失物信息一起上传给服务器 这样一来，服务器上就只存储照片的URL，获取失物信息的时候客户端应该解析这些照片的URL，
        // 再根据这些URL去下载图片资源。
        // holder.LostThingsInfoImage.setImageResource(Integer.parseInt(lostThingsInfo.getThingPhotoUrls()));
        holder.LostThingsInfoTextView.setText(lostThingsInfo.getTitle());
        holder.CurrentPosition = pos;
        Gson gson = new Gson();
        String[] images = gson.fromJson(lostThingsInfo.getThingPhotoUrls(), new TypeToken<String[]>() {
        }.getType());
        holder.LostThingsInfoImage.setImageDrawable(null);
        if (images.length > 0 && !TextUtils.isEmpty(images[0])) {
            Glide.with(mContext).load(images[0]).into(holder.LostThingsInfoImage);
        }
    }

    @Override
    public int getItemCount() {
        return mLostThingsInfoList.size();
    }
}
