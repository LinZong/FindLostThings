package misaka.nemesiss.com.findlostthings.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import com.bumptech.glide.Glide;
import misaka.nemesiss.com.findlostthings.Activity.PickupImageActivity;
import misaka.nemesiss.com.findlostthings.Activity.PreviewSelectedImageActivity;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.File;
import java.util.List;

public class PublishLostThingPreviewImageAdapter extends RecyclerView.Adapter<PublishLostThingPreviewImageAdapter.PublishImagePreviewViewHolder>
{
    private boolean IsReachMaxPic = false;
    private List<Uri> showImages;
    private Activity activity;

    //调起的Dialog中的Button，提前缓存。
    private Button CallCameraBtn;
    private Button CallAlbumBtn;
    private Button DeletePhotoBtn;

    private int CurrentSelectPosition = 0;

    @Override
    public PublishImagePreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (parent != null) {
            parent.removeAllViews();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_lost_thing_upload_preview, parent, false);
        return new PublishImagePreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PublishImagePreviewViewHolder holder, int position)
    {
        Uri CurrentPositionImageUri = showImages.get(position);
        Glide.with(activity).load(CurrentPositionImageUri).into(holder.imageView);
        if (ShouldBindTakeCamera(position))
        {
            holder.imageView.setOnClickListener(view -> AddAPhotoHandler(position));
        } else holder.imageView.setOnClickListener(view -> SelectPhotoHandler(position));
    }

    @Override
    public int getItemCount()
    {
        if (showImages.size() < 10)
        {
            IsReachMaxPic = false;
            return showImages.size();
        } else
        {
            IsReachMaxPic = true;
            return showImages.size() - 1;
        }
    }

    private boolean ShouldBindTakeCamera(int pos)
    {
        if (!IsReachMaxPic && pos == showImages.size() - 1) return true;
        else return false;
    }

    public PublishLostThingPreviewImageAdapter(List<Uri> ShowImages, Activity act)
    {
        SetShowImages(ShowImages);
        activity = act;
    }

    public void SetShowImages(List<Uri> ShowImages)
    {
        showImages = ShowImages;
    }



    class PublishImagePreviewViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.SingleLostThingPreviewImage)
        ImageView imageView;

        public PublishImagePreviewViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void AddAPhotoHandler(int position)
    {
        CurrentSelectPosition = position;
        View view = activity.getLayoutInflater().inflate(R.layout.add_photo_actions,null);
        AlertDialog dialog = AppUtils.ShowAlertDialog(activity, true, "请选择一个动作", null)
                .setView(view)
                .show();

        CallCameraBtn = view.findViewById(R.id.CallCameraBtn);
        CallAlbumBtn = view.findViewById(R.id.CallAlbumBtn);
        CallCameraBtn.setOnClickListener(v -> {
            CallCameraToTakePhoto();
            dialog.cancel();
        });
        CallAlbumBtn.setOnClickListener(v ->{
            CallPhotoPickerToPick();
            dialog.cancel();
        });
    }

    private void SelectPhotoHandler(int position)
    {
        CurrentSelectPosition = position;
        Intent it = new Intent(activity, PreviewSelectedImageActivity.class);
        it.putExtra("PreviewImageIndex",position);
        it.putExtra("PreviewImageUri",showImages.get(position));
        activity.startActivityForResult(it,PickupImageActivity.PREVIEW_ACTIVITY);
    }

    private void CallCameraToTakePhoto()
    {
        File DCIM = new File(AppUtils.GetDCIMPath(), "Camera");
        if(!DCIM.exists())
            DCIM.mkdirs();
        File TempFilePath = new File(DCIM, AppUtils.GetTempImageName());
        Uri TempFileUri = Uri.fromFile(TempFilePath);
        ((PickupImageActivity) (activity)).SetTempImageSavedUri(TempFileUri);
        AppUtils.OpenCamera(TempFileUri, activity);
    }
    private void CallPhotoPickerToPick()
    {
        RxGalleryFinal
                .with(activity)
                .imageLoader(ImageLoaderType.GLIDE)
                .image()
                .maxSize(10-getItemCount())
                .multiple()
                .subscribe(((PickupImageActivity)activity).getMultiImageSelectHandler())
                .openGallery();
    }
}
