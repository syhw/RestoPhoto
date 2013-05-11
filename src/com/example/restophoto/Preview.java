package com.example.restophoto;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class Preview extends ViewGroup implements Callback {

	SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera mCamera;
	private List<Size> mSupportedPreviewSizes;
	private Size mPreviewSize;
	private static String DEBUG_TAG;

    Preview(Context context, Camera camera) {
        super(context);
        
        mCamera = camera;
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
	 
	public void setCamera(Camera camera) {
	    if (mCamera == camera) { return; }
	    
	    stopPreviewAndFreeCamera();
	    
	    mCamera = camera;
	    
	    if (mCamera != null) {
	        List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
	        mSupportedPreviewSizes = localSizes;
	        requestLayout();
	      
	        try {
	            mCamera.setPreviewDisplay(mHolder);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	      
	        /*
	          Important: Call startPreview() to start updating the preview surface. Preview must 
	          be started before you can take a picture.
	          */
	        mCamera.startPreview();
	    }
	}

    
	private void stopPreviewAndFreeCamera() {
		 if (mCamera != null) {
		        /*
		          Call stopPreview() to stop updating the preview surface.
		        */
		        mCamera.stopPreview();
		    
		        /*
		          Important: Call release() to release the camera for use by other applications. 
		          Applications should release the camera immediately in onPause() (and re-open() it in
		          onResume()).
		        */
		        mCamera.release();
		    
		        mCamera = null;
		    }
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(DEBUG_TAG, "Error starting camera preview: " + e.getMessage());
        }
	}


	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		if (mCamera == null) {
	        mCamera = Camera.open();
	        try {
	            mCamera.setPreviewDisplay(mHolder);

	            // TODO test how much setPreviewCallbackWithBuffer is faster
	            mCamera.setOneShotPreviewCallback((PreviewCallback) this);
	        } catch (IOException e) {
	            mCamera.release();
	            mCamera = null;
	        }
	    }

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (mCamera != null) {
	        mCamera.stopPreview();
	        mCamera.setPreviewCallback(null);
	        mCamera.release();
	        mCamera = null;
	    }
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}

}
