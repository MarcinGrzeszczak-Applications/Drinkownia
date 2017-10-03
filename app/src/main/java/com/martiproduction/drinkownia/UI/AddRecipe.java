package com.martiproduction.drinkownia.UI;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.martiproduction.drinkownia.Fragments.AddRecipePicture;
import com.martiproduction.drinkownia.Fragments.PickingImage;
import com.martiproduction.drinkownia.R;

import java.util.Objects;

import butterknife.BindView;

public class AddRecipe extends AppCompatActivity {

    private static final int CONTAINER = R.id.addRecipe_fragment_container;

    private final int PERMISSIONS = 1;
    private boolean mCameraPermissionGranted, mReadPermissionGranted;
    private FragmentTransaction fragmentTransaction;

    private AddRecipePicture mAddRecipePicture;
    private PickingImage mPickingImage;

    private Bitmap mPickedBitmapPicture;

    @BindView(R.id.addRecipe_toolbar)
    Toolbar mAddRecipeToolbar;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS){
            for(String permission : permissions) {

                if(permission.matches(Manifest.permission.CAMERA))
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        mCameraPermissionGranted = true;

                if(permission.matches(Manifest.permission.READ_EXTERNAL_STORAGE))
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        mReadPermissionGranted = true;
            }
       }

       fragmentsInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        setSupportActionBar(mAddRecipeToolbar);
        checkPermissions();
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraPermissionGranted = (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
            mReadPermissionGranted = (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            if(!mCameraPermissionGranted || !mReadPermissionGranted )
                requestPermissions(new String[] {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS);
            else
                fragmentsInit();
        }
    }

    private void fragmentsInit(){

        mAddRecipePicture = new AddRecipePicture();
        mPickingImage = new PickingImage();

        initAddRecipePictureFragment();
    }


    private void initAddRecipePictureFragment(){

        fragmentTransaction = getFragmentManager().beginTransaction();
        mAddRecipePicture.setPickingImageListener(pickingImageListener);
        mAddRecipePicture.cameraPermission(mCameraPermissionGranted);

        if(mPickedBitmapPicture != null){
            mAddRecipePicture.setBitmapPreview(mPickedBitmapPicture);
        }

        fragmentTransaction.replace(CONTAINER,mAddRecipePicture);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initPickingImageFragment(){

        fragmentTransaction = getFragmentManager().beginTransaction();

        mPickingImage.readPermissions(mReadPermissionGranted);
        mPickingImage.setListener(pickingImageListener);

        fragmentTransaction.replace(CONTAINER,mPickingImage);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private PickingImage.Listener pickingImageListener = new PickingImage.Listener() {
        @Override
        public void buttonClicked() {
            initPickingImageFragment();
        }

        @Override
        public void close(Bitmap picture) {
            mPickedBitmapPicture = picture;
            initAddRecipePictureFragment();
        }

    };
}

