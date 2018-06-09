
package com.lx.app;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;

import com.lx.media.capture.video.camera.CameraCapture;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CameraCapture mCameraMgr;

    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initView();
        initData();
    }

    private void initView() {
        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(TAG, "onSurfaceTextureAvailable surface=" + surface + ", width=" + width + ", height=" + height);

                boolean bRet = mCameraMgr.open();
                if (bRet) {
                    mCameraMgr.startPreview(surface);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.d(TAG, "onSurfaceTextureSizeChanged surface=" + surface + ", width=" + width + ", height=" + height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.d(TAG, "onSurfaceTextureDestroyed surface=" + surface);

                mCameraMgr.stopPreview();

                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.i(TAG, "onSurfaceTextureUpdated surface=" + surface);
            }
        });
    }

    private void initData() {
        mCameraMgr = new CameraCapture(this, new CameraCapture.CameraCaptureConfig());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCameraMgr.release();
    }
}
