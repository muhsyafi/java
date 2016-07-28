package com.pdambaru;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AndroidCamera3 extends Activity {
	private Camera mCamera;
	private CameraPreview mPreview;
	private PictureCallback mPicture;
	private Button capture, nextButton;
	private Context myContext;
	private ImageView hasilJepret;
	int currentCamera = 1;
	private LinearLayout cameraPreview;
	private boolean cameraFront = false;
	static String nopelanggan = "na";
	String no = null;
	String nama = null;
	String alamat = null;
	static String timeStamp = "-";
	static String foto = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_camera3);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		myContext = this;
		Intent intent = getIntent();
		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("nopelanggan")) {
				nopelanggan = getIntent().getExtras().getString("nopelanggan",
						"na");
			}
			if (extras.containsKey("no")) {
				no = getIntent().getExtras().getString("no", "0");
			}

		}
		setTitle(nopelanggan);
		initialize();
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the back facing camera
		// get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		// for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	public void onResume() {
		super.onResume();
		if (!hasCamera(myContext)) {
			Toast toast = Toast.makeText(myContext,
					"Sorry, your phone does not have a camera!",
					Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		if (mCamera == null) {
			// if the front facing camera does not exist
			if (findFrontFacingCamera() < 0) {
				Toast.makeText(this, "No front facing camera found.",
						Toast.LENGTH_LONG).show();
				nextButton.setVisibility(View.GONE);
			}
			mCamera = Camera.open(findBackFacingCamera());
			mPicture = getPictureCallback();
			mPreview.refreshCamera(mCamera);
		}
	}

	public void initialize() {
		cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
		mPreview = new CameraPreview(myContext, mCamera);
		cameraPreview.addView(mPreview);
		hasilJepret = (ImageView) findViewById(R.id.hasilJepret);
		capture = (Button) findViewById(R.id.button_capture);
		capture.setOnClickListener(captureListener);

		nextButton = (Button) findViewById(R.id.button_next);
		nextButton.setEnabled(false);
		nextButton.setOnClickListener(NextListener);
		

	}

	OnClickListener switchCameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// get the number of cameras
			int camerasNumber = Camera.getNumberOfCameras();
			if (camerasNumber > 1) {
				// release the old camera instance
				// switch camera, from the front and the back and vice versa

				releaseCamera();
				chooseCamera();
			} else {
				Toast toast = Toast.makeText(myContext,
						"Sorry, your phone has only one camera!",
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
	};

	public void chooseCamera() {
		// if the camera preview is the front
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			currentCamera = cameraId;
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			currentCamera = cameraId;
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// when on Pause, release camera in order to be used from other
		// applications
		releaseCamera();
	}

	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	private boolean hasCamera(Context context) {
		// check if the device has camera
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// make a new picture file
				File pictureFile = getOutputMediaFile();

				Bitmap realImage = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
				android.hardware.Camera.getCameraInfo(currentCamera, info);
				Bitmap bitmap = rotate(realImage, info.orientation - 180);
				Bitmap scaledphoto = Bitmap.createScaledBitmap(bitmap, bitmap.getHeight()/2,
						bitmap.getHeight()/2, true);
				
				
				if (pictureFile == null) {
					return;

				}
				try {
					// write the file
					FileOutputStream fos = new FileOutputStream(pictureFile);
					// fos.write(data);
					scaledphoto.compress(Bitmap.CompressFormat.JPEG, 80, fos);

					fos.close();
					Toast toast = Toast.makeText(myContext, "Picture saved: "
							+ pictureFile.getName(), Toast.LENGTH_LONG);
					toast.show();
					nextButton.setEnabled(true);
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
				// File image = new File(“/sdcard/example/image.jpg”);
				if (pictureFile.exists()) {

					hasilJepret.setImageBitmap(BitmapFactory
							.decodeFile(pictureFile.getAbsolutePath()));
				}
				// refresh camera to continue preview
				mPreview.refreshCamera(mCamera);
			}
		};
		return picture;
	}

	OnClickListener captureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamera.takePicture(null, null, mPicture);
		}
	};
	OnClickListener NextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// open input data for camera
			Intent in = new Intent(getApplicationContext(),
					PelangganInputActivity.class);

			in.putExtra("no",no);

			in.putExtra("foto", foto);
			//finish();
			startActivity(in);
		}
	};

	// make picture and save to a folder
	private static File getOutputMediaFile() {
		// make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "PDAM");
		// if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			// if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// take the current timeStamp
		timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
		File mediaFile;
		// and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ timeStamp + "-" + nopelanggan + ".jpg");
		foto = timeStamp + "-" + nopelanggan + ".jpg";
		return mediaFile;
	}

	private void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

}