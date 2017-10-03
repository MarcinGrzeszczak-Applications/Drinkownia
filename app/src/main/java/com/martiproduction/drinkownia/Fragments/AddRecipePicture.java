package com.martiproduction.drinkownia.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import com.martiproduction.drinkownia.R;
import com.martiproduction.drinkownia.core.CameraV2;
import com.martiproduction.drinkownia.core.RecipePictureListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AddRecipePicture extends Fragment implements RecipePictureListener{

    private boolean mCameraPermissionGranted;
    private Unbinder bind;
  //  private PopupTitle mPopupTitle;
    private View mView;
    private CameraV2 mCameraV2;
    private PickingImage.Listener mPickingImageListener;
    private Bitmap mPickedBitmapPreview;

    @BindView(R.id.addPicture_circlePreview)
    TextureView circlePreview;

    @BindView(R.id.addPicture_takePictureButton)
    FloatingActionButton takePictureButton;

    @Override
    public void onPause() {
        if(mCameraV2 != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCameraV2.pause();
            }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        bind.unbind();
    //    mPopupTitle.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_recipe_picture,container,false);
        bind = ButterKnife.bind(this,mView);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    //    mPopupTitle = new PopupTitle(getActivity().getApplicationContext(),mView);
        mView.post(() -> {
      //      mPopupTitle.show(getString(R.string.addDrink_add_picture),5);
            if(mPickedBitmapPreview !=null){
                Canvas canvas = circlePreview.lockCanvas();
                canvas.drawBitmap(mPickedBitmapPreview,0f,0f,null);
                circlePreview.draw(canvas);
            }
            else
                openCamera(mCameraPermissionGranted);

        });

    }

    public void cameraPermission(boolean permission){
        mCameraPermissionGranted = permission;
    }

    public void setBitmapPreview(Bitmap preview){
        mPickedBitmapPreview = preview;
    }

    public void setPickingImageListener(PickingImage.Listener pickingImageListener) {
        this.mPickingImageListener = pickingImageListener;
    }


    @OnClick(R.id.addPicture_pickPictureButton)
    public void pickPictureButton(){
        mPickingImageListener.buttonClicked();
    }

    @OnClick(R.id.addPicture_takePictureButton)
    public void takePictureButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mCameraV2.takePicture();
    }

    private void openCamera(boolean permission){
        if (!permission){
            Log.d(getActivity().getLocalClassName(),"DUMMY PICTURE");
            takePictureButton.setEnabled(false);
            }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if(mCameraV2 != null)
                        mCameraV2.resume();

                else {
                    mCameraV2 = new CameraV2(getActivity());
                    mCameraV2.openCamera(circlePreview);
                }
            }
        }
    }



    @Override
    public void getPicture(byte[] picture) {

    }
}
