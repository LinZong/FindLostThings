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
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.Thing.ThingServices;
import misaka.nemesiss.com.findlostthings.Model.*;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<LostThingsInfo> mLostThingsInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView Result_ImageView;
        TextView Result_Title;
        TextView Result_ThingCategory;
        TextView Result_ThingDetail;
        TextView Result_PublishTime;
        TextView Result_IsGiven;

        public int CurrentPosition;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            Result_ImageView = view.findViewById(R.id.Result_ImageView);
            Result_Title = view.findViewById(R.id.Result_Title);
            Result_ThingCategory = view.findViewById(R.id.Result_ThingCategory);
            Result_ThingDetail = view.findViewById(R.id.Result_ThingDetail);
            Result_PublishTime = view.findViewById(R.id.Result_PublishTime);
            Result_IsGiven = view.findViewById(R.id.Result_IsGiven);
        }
    }

    public SearchResultAdapter(List<LostThingsInfo> lostThingsInfoList, Activity activity) {
        mLostThingsInfoList = lostThingsInfoList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result, parent, false);
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
            int arrowDrawable = LostThingDetailActivity.ComputeHomeArrowColor(holder.Result_ImageView);
            EnterThingDetail(pos,arrowDrawable);
        });

        ThingServices ts = FindLostThingsApplication.getThingServices();
        LostThingsInfo lostThingsInfo = mLostThingsInfoList.get(pos);
        LostThingsCategory cat = ts.getThingCategory().get(lostThingsInfo.getThingCatId());
        LostThingDetail dt = ts.getThingDetails().get(lostThingsInfo.getThingCatId()).get(lostThingsInfo.getThingDetailId());

        holder.Result_Title.setText(lostThingsInfo.getTitle());
        holder.Result_ThingCategory.setText(cat.getName());
        holder.Result_ThingDetail.setText(dt.getName());
        holder.Result_PublishTime.setText(AppUtils.UnixStampToFmtString(lostThingsInfo.getPublishTime()));
        holder.Result_IsGiven.setText(lostThingsInfo.getIsgiven() == 0 ? "未归还" : "已归还");
        holder.CurrentPosition = pos;

        Gson gson = new Gson();
        String[] images = gson.fromJson(lostThingsInfo.getThingPhotoUrls(), new TypeToken<String[]>() {
        }.getType());
        holder.Result_ImageView.setImageDrawable(null);
        if (images.length > 0 && !TextUtils.isEmpty(images[0])) {
            Glide.with(mContext).load(images[0]).into(holder.Result_ImageView);
        }
    }


    @Override
    public int getItemCount() {
        return mLostThingsInfoList.size();
    }
}
