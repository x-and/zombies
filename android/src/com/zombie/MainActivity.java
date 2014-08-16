package com.zombie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends AndroidApplication implements IActivityRequestHandler {

	public static AdView adView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
	  config.useGL20 = true;
	  config.useCompass = false;
	  config.useAccelerometer = false;
	  config.numSamples = 1;
	  config.useWakelock = false;	
	  
	  //����� ������� ����	
	  RelativeLayout layout = new RelativeLayout(this);
	  //������������� �����, ������� ��������������� � ������ initialize() ������ ���
	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
	  getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

	  //������������� ��� LibGDX
	  View gameView = initializeForView(ZombieGame.getInstance(), config);
	  ZombieGame.adMob = this;
	  //������������� � ��������� AdMob
	  adView = new AdView(this, AdSize.BANNER, "pub-2650194333987629"); 
	  AdRequest adRequest = new AdRequest();
      adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
      adRequest.addTestDevice("EC0F719A8CDA9A026190187B8645D673");

	  adView.loadAd(adRequest); 
	   //���������� ������������� ����� ����
	  layout.addView(gameView);

	  RelativeLayout.LayoutParams adParams = 
	                new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, 
	                        RelativeLayout.LayoutParams.WRAP_CONTENT);
	  adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); 
	  adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); 
	  //���������� ������������� ������� � ����
	  layout.addView(adView, adParams);  
	      
	  //�� ��������� � ����� ����
	  setContentView(layout); 

	}

	 @Override
	  public void showAdMob(boolean show){
	    handler.sendEmptyMessage(show ? 1 : 0);
	  }

	  public static Handler handler = new Handler()
	  {
	    @Override
	    public void handleMessage(Message msg) {
	      if(msg.what ==0)
	        adView.setVisibility(View.GONE);
	      if(msg.what==1){
	        adView.setVisibility(View.VISIBLE);
	        AdRequest adRequest = new AdRequest();
	        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
	        adRequest.addTestDevice("EC0F719A8CDA9A026190187B8645D673");
	        adView.loadAd(adRequest); 
	      }
	    }
	  }; 


//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        
//        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
//        cfg.useGL20 = true;
//        cfg.useCompass = false;
//        cfg.useAccelerometer = false;
//        cfg.numSamples = 2;
//        initialize(ZombieGame.getInstance(), cfg);
//    }
}