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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import misaka.nemesiss.com.findlostthings.Activity.LostThingDetailActivity;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.util.List;

public class MyFindBackAdapter extends RecyclerView.Adapter<MyFindBackAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<LostThingsInfo> mLostThingsInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView LostThingsInfoImage;
        TextView LostThingsInfoTextView1;
        TextView LostThingsInfoTextView2;

        public int CurrentPosition;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            LostThingsInfoImage = (ImageView) view.findViewById(R.id.myPublish_lost_things_info_image);
            LostThingsInfoTextView1 = (TextView) view.findViewById(R.id.myPublish_lost_things_info_text1);
            LostThingsInfoTextView2 = (TextView) view.findViewById(R.id.myPublish_lost_things_info_text2);
        }
    }

    public MyFindBackAdapter(List<LostThingsInfo> lostThingsInfoList, Activity activity) {
        mLostThingsInfoList = lostThingsInfoList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_publish_lostthings, parent, false);
        return new ViewHolder(view);
    }

    private void EnterThingDetail(int position,int arrowDrawable) {
        LostThingsInfo lti = mLostThingsInfoList.get(position);
        Intent intent = new Intent(mActivity, LostThingDetailActivity.class);
        intent.putExtra("LostThingsInfo", lti);
        intent.putExtra("ArrowDrawableRes",arrowDrawable);
        mActivity.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind onClick handler.
        int pos = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(v -> {
            int arrowDrawable = LostThingDetailActivity.ComputeHomeArrowColor(holder.LostThingsInfoImage);
            EnterThingDetail(pos,arrowDrawable);
        });

        LostThingsInfo lostThingsInfo = mLostThingsInfoList.get(pos);
        holder.LostThingsInfoTextView1.setText(lostThingsInfo.getTitle());
        holder.LostThingsInfoTextView2.setText("找回时间："+AppUtils.UnixStampToFmtString(lostThingsInfo.getGivenTime()));
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

