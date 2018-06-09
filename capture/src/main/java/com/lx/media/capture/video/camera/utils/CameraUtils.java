
package com.lx.media.capture.video.camera.utils;

import android.hardware.Camera;
import android.view.Surface;

public class CameraUtils {

    public static int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public static int getCameraId(int facing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0, count = getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }

        return -1;
    }

    private static Camera.CameraInfo getCameraInfo(int facing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0, count = getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return cameraInfo;
            }
        }

        return null;
    }

    public static boolean hasFrontCamera() {
        return getFrontCameraId() != -1;
    }

    public static int getFrontCameraId() {
        return getCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public static boolean hasBackCamera() {
        return getBackCameraId() != -1;
    }

    public static int getBackCameraId() {
        return getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public static Camera open() {
        return open(getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK));
    }

    public static Camera open(int cameraId) {
        if (cameraId < 0 || cameraId >= getNumberOfCameras()) {
            return null;
        }

        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return camera;
    }

    public static int getCameraDisplayOrientation(int cameraId, int surfaceRotation) {
        int degrees = 0;
        switch (surfaceRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
