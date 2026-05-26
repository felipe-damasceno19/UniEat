package com.example.unieat.view;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private MediaPlayer mediaPlayer;
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 6000);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mediaPlayer = new MediaPlayer();
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.abertura_intro);
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setSurface(new Surface(surfaceTexture));
            mediaPlayer.setVolume(0f, 0f);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnVideoSizeChangedListener((mp, videoWidth, videoHeight) ->
                    runOnUiThread(() -> adjustVideoSize(videoWidth, videoHeight)));
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            navigateToLogin();
        }
    }

    private void adjustVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) return;
        int viewWidth = textureView.getWidth();
        int viewHeight = textureView.getHeight();
        if (viewWidth == 0 || viewHeight == 0) return;

        float scaleX = (float) videoWidth / viewWidth;
        float scaleY = (float) videoHeight / viewHeight;
        float scale = Math.max(scaleX, scaleY);

        float scaledW = videoWidth / scale;
        float scaledH = videoHeight / scale;
        float dx = (viewWidth - scaledW) / 2f;
        float dy = (viewHeight - scaledH) / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scaledW / viewWidth, scaledH / viewHeight);
        matrix.postTranslate(dx, dy);
        textureView.setTransform(matrix);
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
