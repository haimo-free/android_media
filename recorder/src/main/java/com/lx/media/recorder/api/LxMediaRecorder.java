
package com.lx.media.recorder.api;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.lx.media.capture.video.camera.CameraCapture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LxMediaRecorder {

    private static final String TAG = "LxMediaRecorder";

    private CameraCapture mCameraVideoCapture;
    MediaFormat mVideoMediaFormat;
    private MediaCodec mMediaCodec;
    private MediaMuxer mMediaMuxer;
    private int mVideoTrackIndex;

    private Object mLock = new Object();

    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    public LxMediaRecorder() {

    }

    public void prepare(CameraCapture cameraCapture) {
        mCameraVideoCapture = cameraCapture;
        mCameraVideoCapture.setOnPreviewCallback(mOnPreviewFrameListener);

        mVideoMediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 360, 640);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 800);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);

        try {
            mMediaCodec = MediaCodec.createByCodecName("video/avc");
            mMediaCodec.configure(mVideoMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String path = Environment.getExternalStorageDirectory() + "/AAAAA/" + System.currentTimeMillis() + ".mp4";
            mMediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVideoTrackIndex = mMediaMuxer.addTrack(mVideoMediaFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (mMediaCodec == null || mMediaMuxer == null) {
            return;
        }

        mMediaCodec.start();
        mMediaMuxer.start();
    }

    public void stop() {

    }

    public void reset() {

    }

    public void release() {

    }

    public static interface OnPrepareListener {

        void onPrepared();
    }

    private OnPrepareListener mOnPrepareListener;

    public void setOnPrepareListener(OnPrepareListener onPrepareListener) {
        synchronized (mLock) {
            this.mOnPrepareListener = onPrepareListener;
        }
    }

    private void notifyOnPrepareListener() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    mOnPrepareListener.onPrepared();
                }
            }
        });
    }

    private CameraCapture.OnPreviewCallback mOnPreviewFrameListener = new CameraCapture.OnPreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, int width, int height) {
            if (mMediaCodec == null || mMediaMuxer == null) {
                return;
            }

            int inputBufferIndex = mMediaCodec.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mMediaCodec.getInputBuffers()[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(data);

                mMediaCodec.queueInputBuffer(mVideoTrackIndex, 0, data.length, System.nanoTime(), MediaCodec.BUFFER_FLAG_KEY_FRAME);

                //test
                executorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 1000);

                        if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

                        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                        } else if (outputBufferIndex >= 0) {
                            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffers()[outputBufferIndex];


                        }
                    }
                });
            }
        }
    };

    private ExecutorService executorCompletionService = Executors.newSingleThreadExecutor();
}
