package com.yalantis.ucrop.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.callback.CropBoundsChangeListener;
import com.yalantis.ucrop.callback.OverlayViewChangeListener;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;
import java.io.IOException;

public class UCropView extends FrameLayout {

    private final GestureCropImageView mGestureCropImageView;
    private final OverlayView mViewOverlay;
    private final ImageView mImageViewOverlay;

    private int mMaxBitmapSize = 0;

    public UCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.ucrop_view, this, true);
        mGestureCropImageView = (GestureCropImageView) findViewById(R.id.image_view_crop);
        mViewOverlay = (OverlayView) findViewById(R.id.view_overlay);
        mImageViewOverlay = (ImageView) findViewById(R.id.image_view_overlay);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView);
        mViewOverlay.processStyledAttributes(a);
        mGestureCropImageView.processStyledAttributes(a);
        a.recycle();


        mGestureCropImageView.setCropBoundsChangeListener(new CropBoundsChangeListener() {
            @Override
            public void onCropAspectRatioChanged(float cropRatio) {
                mViewOverlay.setTargetAspectRatio(cropRatio);
            }
        });
        mViewOverlay.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect) {
                mGestureCropImageView.setCropRect(cropRect);
            }
        });
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @NonNull
    public GestureCropImageView getCropImageView() {
        return mGestureCropImageView;
    }

    @NonNull
    public OverlayView getOverlayView() {
        return mViewOverlay;
    }

    public int getMaxBitmapSize() {
        if (mMaxBitmapSize <= 0) {
            mMaxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(getContext());
        }
        return mMaxBitmapSize;
    }

    public void setOverlayUri(Uri uri){
        int maxBitmapSize = getMaxBitmapSize();
        Uri output = null;

        try {
            output = Uri.fromFile(File.createTempFile("what", null, getContext().getCacheDir()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapLoadUtils.decodeBitmapInBackground(
                getContext(),
                uri,
                output,
                maxBitmapSize,
                maxBitmapSize,
                new BitmapLoadCallback() {

                    @Override
                    public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                        mImageViewOverlay.setImageBitmap(bitmap);
                        mImageViewOverlay.requestLayout();
                    }

                    @Override
                    public void onFailure(@NonNull Exception bitmapWorkerException) {
//                        Log.e(TAG, "onFailure: setImageUri", bitmapWorkerException);
//                        if (mTransformImageListener != null) {
//                            mTransformImageListener.onLoadFailure(bitmapWorkerException);
//                        }
                    }
                });
    }

}