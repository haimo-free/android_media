package com.lx.media.capture.video;

import android.graphics.SurfaceTexture;

public interface IVideoCapture {

    boolean open();

    boolean startPreview(SurfaceTexture surfaceTexture);

    void stopPreview();

    void close();

    void release();

    /**
     *
     */
    public interface OnPreviewCallback {

        /**
         * @param data
         * @param width
         * @param height
         */
        void onPreviewFrame(byte[] data, int width, int height);
    }

    void setOnPreviewCallback(OnPreviewCallback onPreviewCallback);

    public static class VideoCaptureConfig {

        public int mPreviewWidth = 1280;

        public int mPreviewHeight = 720;

        public int mBufferSize = 3;

        protected boolean isValid() {
            return true;
        }
    }
}
