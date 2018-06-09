package com.lx.media.capture.video;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Haimo on 2018/6/6.
 */

public abstract class VideoCaptureImpl implements IVideoCapture {

    protected VideoCaptureConfig mVideoCpatureConfig;

    protected LinkedBlockingDeque<byte[]> mBufferDeque;

    public VideoCaptureImpl(VideoCaptureConfig videoCaptureConfig) {
        if (videoCaptureConfig == null || !videoCaptureConfig.isValid()) {
            throw new IllegalArgumentException("VideoCaptureConfig is null or invalid, config" + videoCaptureConfig);
        }

        mVideoCpatureConfig = videoCaptureConfig;
    }

    private OnPreviewCallback mOnPreviewCallback;

    @Override
    public void setOnPreviewCallback(OnPreviewCallback onPreviewCallback) {
        this.mOnPreviewCallback = onPreviewCallback;
    }
}
