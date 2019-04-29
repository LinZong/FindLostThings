package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.View.PinchImageView;

import java.io.File;

public class PreviewSelectedImageActivity extends FindLostThingsActivity
{

    @BindView(R.id.PreviewImageView)
    PinchImageView PreviewImageView;

    @BindView(R.id.EnterUCropBtn)
    Button EditBtn;

    @BindView(R.id.DeleteImageBtn)
    Button DeleteImageBtn;

    @BindView(R.id.HomeArrowBtn)
    Button HomeArrowBtn;

    private Uri CurrentPreviewImageUri;
    private int CurrentPreviewImageFromGridIndex = 0;
    private boolean IsNormalPreview = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_selected_image);
        ButterKnife.bind(this);
        InitClickListeners();

        CurrentPreviewImageFromGridIndex = getIntent().getIntExtra("PreviewImageIndex",-1);
        CurrentPreviewImageUri = getIntent().getParcelableExtra("PreviewImageUri");

        IsNormalPreview = getIntent().getBooleanExtra("IsNormalPreview",false);
        if(IsNormalPreview) {

            EditBtn.setVisibility(View.GONE);
        }
        RequestRenderImage(CurrentPreviewImageUri);
    }

    private void InitClickListeners()
    {
        EditBtn.setOnClickListener(this::HandleEnterUCrop);
        DeleteImageBtn.setOnClickListener(this::HandleDeleteImage);
        HomeArrowBtn.setOnClickListener(this::HandleReturnArrow);
    }

    private void HandleEnterUCrop(View v)
    {
        AppUtils.OpenUCrop(CurrentPreviewImageUri)
                .start(PreviewSelectedImageActivity.this);
    }

    private void RequestRenderImage(Uri ImageUri)
    {
        Glide.with(PreviewSelectedImageActivity.this)
                .load(ImageUri)
                .into(PreviewImageView);
    }

    private void HandleDeleteImage(View v)
    {
        PassInfoToPublishPage(true);
        finish();
    }

    private void HandleReturnArrow(View v)
    {
        PassInfoToPublishPage(false);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Handle从UCrop回来
        switch (resultCode){
            case Activity.RESULT_OK:{
                switch (requestCode){
                    case UCrop.REQUEST_CROP:{
                        //如果旧的存在，先把旧的删了
                        if(CurrentPreviewImageUri!=null)
                        {
                            File file = new File(CurrentPreviewImageUri.getPath());
                            if(file.exists())
                            {
                                file.delete();
                            }
                        }
                        CurrentPreviewImageUri = UCrop.getOutput(data);
                        RequestRenderImage(CurrentPreviewImageUri);
                        break;
                    }
                }
                break;
            }
        }
    }


    private void PassInfoToPublishPage(boolean ShouldDeleteImage)
    {
        Intent intent = new Intent();
        intent.putExtra("ShouldDeleteImage",ShouldDeleteImage);
        intent.putExtra("PreviewImageUri",CurrentPreviewImageUri);
        intent.putExtra("PreviewImageIndex",CurrentPreviewImageFromGridIndex);
        PreviewSelectedImageActivity.this.setResult(Activity.RESULT_OK,intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        PassInfoToPublishPage(false);
        finish();
    }
}
