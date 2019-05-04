package misaka.nemesiss.com.findlostthings.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import java.io.File;
import java.util.List;

public class ImageHelper {
    public static void CompressAllImage(List<Uri> OriginalImageUri, EventProxy.EventResult<Uri> callback)
    {
        Context context = FindLostThingsApplication.getContext();
        EventProxy<Uri> eventList = new EventProxy<>();

        eventList.all(callback,OriginalImageUri.toArray(new Uri[0]));
        for (Uri ur : OriginalImageUri) {
            Luban.with(context)
                    .load(ur)
                    .setFocusAlpha(false)
                    .setTargetDir(AppUtils.GetAppCachePath())
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            Log.d("ImageHelper","Begin compress image : " + ur.getPath());
                        }

                        @Override
                        public void onSuccess(File file) {
                            Log.d("ImageHelper","Compress finished! : " + file.getAbsolutePath());
                            eventList.emit(ur, EventProxy.EventStatus.Finish, file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("ImageHelper","Compress failed! : " + e.getMessage() + "  " + ur.getPath());
                            eventList.emit(ur, EventProxy.EventStatus.Fail,"null");
                        }
                    }).launch();
        }
    }
}
