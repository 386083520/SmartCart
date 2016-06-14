package org.lmw.tools.qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends Activity{
ImageView welcome_iv;
TextView welcome_tv;

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.welcome_ui);
	welcome_iv=(ImageView) findViewById(R.id.welcome_iv);
	welcome_tv=(TextView) findViewById(R.id.welcome_tv);
	welcome_tv.setText("欢迎使用智能购物系统");
	Animation welcomeAnim = AnimationUtils.loadAnimation(this, R.anim.welcome);  
	LinearInterpolator lin = new LinearInterpolator(); 
	welcomeAnim.setInterpolator(lin);
	
	
	welcomeAnim.setAnimationListener(new ImaAnimEndListener());
    
    if (welcomeAnim != null) {  
        welcome_iv.startAnimation(welcomeAnim);  
    }  
}

private class ImaAnimEndListener implements AnimationListener{

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
		welcome_iv.clearAnimation();
		Intent intent=new Intent();
		intent.setClass(WelcomeActivity.this, MethodChoiceActivity.class);
		startActivity(intent);
		finish();
		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
}
