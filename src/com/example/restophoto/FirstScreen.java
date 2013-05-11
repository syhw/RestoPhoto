package com.example.restophoto;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;

public class FirstScreen extends Activity {
	public static String DEBUG_TAG;
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);       
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_pic);

		preview=(SurfaceView)findViewById(R.id.cameraPreview);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			/*Camera.CameraInfo info=new Camera.CameraInfo();

      for (int i=0; i < Camera.getNumberOfCameras(); i++) {
        Camera.getCameraInfo(i, info);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
          camera=Camera.open(i);
        }
      }*/
			camera=Camera.open();
		}

		if (camera == null) {
			camera=Camera.open();
		}

		startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera=null;
		inPreview=false;

		super.onPause();
	}

	/*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.options, menu);

    return(super.onCreateOptionsMenu(menu));
  }*/

	/*@Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.camera) {
      if (inPreview) {
        camera.takePicture(null, null, photoCallback);
        inPreview=false;
      }
    }

    return(super.onOptionsItemSelected(item));
  }*/

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result=size;
				}
				else {
					int resultArea=result.width * result.height;
					int newArea=size.width * size.height;

					if (newArea > resultArea) {
						result=size;
					}
				}
			}
		}

		return(result);
	}

	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result=size;
			}
			else {
				int resultArea=result.width * result.height;
				int newArea=size.width * size.height;

				if (newArea < resultArea) {
					result=size;
				}
			}
		}

		return(result);
	}

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(FirstScreen.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters=camera.getParameters();
				Camera.Size size=getBestPreviewSize(width, height, parameters);
				Camera.Size pictureSize=getSmallestPictureSize(parameters);

				if (size != null && pictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(pictureSize.width,
							pictureSize.height);
					parameters.setPictureFormat(ImageFormat.JPEG);
					camera.setParameters(parameters);
					cameraConfigured=true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview=true;
		}
	}

	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};

	Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePhotoTask().execute(data);
			camera.startPreview();
			inPreview=true;
		}
	};

	class SavePhotoTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo=
					new File(Environment.getExternalStorageDirectory(),
							"photo.jpg");

			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos=new FileOutputStream(photo.getPath());

				fos.write(jpeg[0]);
				fos.close();
			}
			catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}

			return(null);
		}
	}
}


/*
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class FirstScreen extends Activity {
    private static final int REQUEST_CODE = 1;
	public static String DEBUG_TAG = "Debug tag";
    private Bitmap bitmap;
    private ImageView imageView;
	private Camera camera;
	private Preview preview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_take_pic);
      boolean qOpened = false;
		try {
			preview = new Preview(this, camera);
			FrameLayout fl_preview = (FrameLayout) findViewById(R.id.cameraPreview);
	        fl_preview.addView(preview);
			releaseCameraAndPreview();
			camera = Camera.open(); // open without args open the rear facing cam
			qOpened = (camera != null);
		} catch (Exception e) {
			Log.e(getString(R.string.app_name), "failed to open Camera");
			e.printStackTrace();
		}
	}

    private boolean safeCameraOpen() {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open();
            qOpened = (camera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;    
    }

	public void onClick(View view) {
		camera.takePicture(null, null,
				new PhotoHandler(getApplicationContext()));
	}

	private void releaseCameraAndPreview() {
		preview.setCamera(null);
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	@Override
	protected void onPause() {
		releaseCameraAndPreview();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
	    // TODO Auto-generated method stub
	    super.onDestroy();
	    releaseCameraAndPreview();
	}

}
 */
