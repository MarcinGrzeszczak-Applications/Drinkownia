package com.martiproduction.drinkownia.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Collections;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraV2 {

    private Activity mActivity;
    private TextureView mPreviewTexture;
    private Surface mPreviewSurface;
    private Handler mCameraHandler;
    private HandlerThread mCameraHandlerThread;
    private CameraDevice mCameraDevice;

    public CameraV2(Activity activity) {
        mActivity = activity;
    }

    public void openCamera(TextureView previewTexture) {
        mPreviewTexture = previewTexture;
        resume();

    }

    public void resume() {
        startCameraHandler();
        resumeCamera();
    }

    public void pause() {
        stopCamera();
        stopCameraHandler();
    }


    public void takePicture(){
        try {
            mCameraDevice.createCaptureSession(Collections.singletonList(mPreviewSurface),captureSessionCallback,mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void deletePicture(){
        createPreview();
    }

    private void startCameraHandler() {
        mCameraHandlerThread = new HandlerThread("CameraHandlerV2");
        mCameraHandlerThread.start();

        mCameraHandler = new Handler(mCameraHandlerThread.getLooper());
    }

    private void stopCameraHandler() {
        mCameraHandlerThread.quitSafely();
        try {
            mCameraHandlerThread.join();

            mCameraHandlerThread = null;
            mCameraHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resumeCamera(){
        if(mPreviewTexture.isAvailable())
            setupCamera();
        else
            mPreviewTexture.setSurfaceTextureListener(surfaceTextureListener);
    }

    private void stopCamera(){
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            setupCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private CameraDevice.StateCallback openCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
    };

    private CameraCaptureSession.StateCallback previewSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            try {
                CaptureRequest.Builder previewRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequest.addTarget(mPreviewSurface);

                cameraCaptureSession.setRepeatingRequest(previewRequest.build(),null,mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };

    private CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

            try {
                CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                cameraCaptureSession.capture(captureRequest.build(),null,null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };

    private void setupCamera() {
        CameraManager cameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);

        String backCameraID = null;

        try {
            for (String cameraID : cameraManager.getCameraIdList()) {
                if (cameraManager.getCameraCharacteristics(cameraID).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    backCameraID = cameraID;
                    break;
                }
            }

                if (backCameraID != null) {
                    Size cameraFormat;

                    StreamConfigurationMap cameraMap = cameraManager.getCameraCharacteristics(backCameraID).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    assert cameraMap != null;
                    cameraFormat = cameraMap.getOutputSizes(SurfaceTexture.class)[0];

                    SurfaceTexture previewTexture = mPreviewTexture.getSurfaceTexture();
                    previewTexture.setDefaultBufferSize(cameraFormat.getWidth(), cameraFormat.getHeight());
                    mPreviewSurface = new Surface(previewTexture);

                    if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                        cameraManager.openCamera(backCameraID, openCameraCallback, mCameraHandler);
                }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createPreview(){

        try {
            mCameraDevice.createCaptureSession(Collections.singletonList(mPreviewSurface),previewSessionCallback,mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


}
