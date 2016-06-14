package org.lmw.tools.qr;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.lmw.tools.qr.common.App;
import org.lmw.tools.qr.common.BaseActivity;
import org.lmw.tools.qr.common.LogEntity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;

public class ScanActivity extends BaseActivity implements Callback,OnClickListener,OnTouchListener{
	/** scan */
	public CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	public Vector<BarcodeFormat> decodeFormats;
	public String characterSet;
	public InactivityTimer inactivityTimer;
	public MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	/** Called when the activity is first created. */
	
	ImageButton torch;
	ImageButton loger;
	private Parameters parameters;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_scan);
		initView();
	}

	
	public void initView(){
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		torch=(ImageButton)findViewById(R.id.torch);
		loger=(ImageButton)findViewById(R.id.loger);
		torch.setOnClickListener(this);
		loger.setOnClickListener(this);
		loger.setOnTouchListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		if(v==torch){
			try {
				parameters = CameraManager.get().camera.getParameters();  
				if(parameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)){
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					torch.setImageResource(R.drawable.qb_scan_btn_flash_nor);
				}else{
					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
					torch.setImageResource(R.drawable.qb_scan_btn_flash_down);
				}
				CameraManager.get().camera.setParameters(parameters);	
			} catch (Exception e) {
				showToast("设备故障");
			}
		}
		if(v==loger){
			startActivity(new Intent(getApplicationContext(), LogerActivity.class));
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//按下
			loger.setImageResource(R.drawable.qb_scan_btn_myqrcode_down);
			break;
		case MotionEvent.ACTION_MOVE:
			//移动
			break;
		case MotionEvent.ACTION_UP:
			loger.setImageResource(R.drawable.qb_scan_btn_myqrcode_nor);
			//抬起
			break;
		}
		return false;
	}
	
	/** 以下是扫描 相关代码 */
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	public void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String scan_res = result.getText();

		if (scan_res.equals("")) {
			showToast("扫描失败");
		} else {
			
			String scan_pic=App.saveBitmap(barcode); 
			
			App.rs.add(new LogEntity(scan_res, scan_pic));
			App.witeToSp();
			
			Intent it = new Intent(getApplicationContext(), GoodsInfoActivity.class);
			//Intent it = new Intent(getApplicationContext(), GoodsInfoByBlueTooth.class);
			it.putExtra("scan_res", scan_res);
			
			startActivity(it);
			torch.setImageResource(R.drawable.qb_scan_btn_flash_nor);
		}
		onPause();
		// onResume();
	}
	
	
}