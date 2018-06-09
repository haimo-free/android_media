
package com.lx.media.capture.video.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.lx.media.capture.video.VideoCaptureImpl;
import com.lx.media.capture.video.camera.utils.CameraUtils;

import java.util.concurrent.LinkedBlockingDeque;

public class CameraCapture extends VideoCaptureImpl {

    private static final String TAG = "media_capture";

    private Activity mActivity;

    private Camera mCamera;
    private CameraCaptureConfig mCameraCaptureConfig;

    public CameraCapture(Activity activity, CameraCaptureConfig cameraCaptureConfig) {
        super(cameraCaptureConfig);

        this.mActivity = activity;
        this.mCameraCaptureConfig = cameraCaptureConfig;
    }

    @Override
    public boolean open() {
        int cameraId = CameraUtils.getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);

        mCamera = CameraUtils.open(cameraId);
        if (mCamera == null) {
            return false;
        }

        mCamera.setDisplayOrientation(CameraUtils.getCameraDisplayOrientation(cameraId,
                mActivity.getWindowManager().getDefaultDisplay().getRotation()));

        return true;
    }

    @Override
    public boolean startPreview(SurfaceTexture surfaceTexture) {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mCameraCaptureConfig.mPreviewWidth, mCameraCaptureConfig.mPreviewHeight);
        parameters.setPreviewFormat(ImageFormat.NV21);
        try {
            mCamera.setParameters(parameters);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (mBufferDeque == null) {
                mBufferDeque = new LinkedBlockingDeque<>(mCameraCaptureConfig.mBufferSize);
            } else {
                mBufferDeque.clear();
            }

            for (int i = 0; i < mCameraCaptureConfig.mBufferSize; i++) {
                byte[] buffer = new byte[mCameraCaptureConfig.mPreviewWidth * mCameraCaptureConfig.mPreviewHeight * 3 / 2];
                mCamera.addCallbackBuffer(buffer);
            }

            mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void release() {
        mActivity = null;

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.i(TAG, "onPreviewFrame data=" + data + "");

            mCamera.addCallbackBuffer(data);
        }
    };

    public static class CameraCaptureConfig extends VideoCaptureConfig {

        public int mCameraId = -1;

        public int mDisplayOrientation = 0;
    }
}
