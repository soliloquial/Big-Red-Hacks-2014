package com.example.spencer.brh2014;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.spencer.brh2014.gles.EglCore;
import com.example.spencer.brh2014.gles.EglSurfaceBase;
import com.example.spencer.brh2014.gles.FullFrameRect;
import com.example.spencer.brh2014.gles.Texture2dProgram;
import com.example.spencer.brh2014.gles.WindowSurface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener {
    private SurfaceHolder mHolder;
    private SurfaceTexture mTexture;
    private Camera mCamera;
    private final float[] mTmpMatrix = new float[16];
    public static String TAG = "ERROR";
    private int mTextureId = 1;
    private long mFrameNum = 0;
    private EglSurfaceBase mDisplaySurface;
    private EglCore mEglCore;
    private FullFrameRect mFullFrameBlit;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder=" + holder);

        // Set up everything that requires an EGL context.
        //
        // We had to wait until we had a surface because you can't make an EGL context current
        // without one, and creating a temporary 1x1 pbuffer is a waste of time.
        //
        // The display surface that we use for the SurfaceView, and the encoder surface we
        // use for video, use the same EGL context.
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, holder.getSurface(), false);
        mDisplaySurface.makeCurrent();

        mFullFrameBlit = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mTextureId = mFullFrameBlit.createTextureObject();
        mTexture = new SurfaceTexture(mTextureId);
        mTexture.setOnFrameAvailableListener(this);
        drawFrame();
        Log.d(TAG, "starting camera preview");
        try {
            mCamera.setPreviewTexture(mTexture);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mCamera.startPreview();
        Log.d(TAG, "done starting preview");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "frame available");
        drawFrame();
    }

    private void drawFrame() {

        // Latch the next frame from the camera.
        mDisplaySurface.makeCurrent();
        mTexture.updateTexImage();
        mTexture.getTransformMatrix(mTmpMatrix);

        // Fill the SurfaceView with it.
        int viewWidth = this.getWidth();
        int viewHeight = this.getHeight();
        GLES20.glViewport(0, 0, viewWidth, viewHeight);
        GLES20.glClearColor(1,0,0,1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
        mDisplaySurface.swapBuffers();

        mFrameNum++;
    }

    public void takePicture(Activity mainScreen) {
        Log.d("NOTICE", "Taking and uploading picture");
        final Activity mainScreen2 = mainScreen;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(bytes);
                    fos.close();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }
                new ImageUploadTask(bytes) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("NOTICE", "image uploaded as "+s);
                    }
                }.execute();
                camera.startPreview();
            }
        });
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Lingo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Lingo", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}