package com.patrupopa.wordscocktail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.preference.PreferenceManager;

public class Login extends Activity {

	public String URL = "http://wordscocktail.com/";
	private WebView webview;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		initWebkit();

		if(savedInstanceState != null) {
			webview.restoreState(savedInstanceState);
		} else {
			webview.loadUrl(URL + "session/");
		}
	}

	@SuppressLint("JavascriptInterface")
	public void initWebkit() {

        handler = new Handler();
        
		setContentView(R.layout.webview);

		webview = (WebView) findViewById(R.id.webview);

		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith(URL)) {
					return false;
				}
				Intent openURL = new Intent("android.intent.action.VIEW",
					Uri.parse(url));
				startActivity(openURL);
				return true;
			}
		});

		webview.addJavascriptInterface(new LoginProcessor(this),"login_processor");
		webview.addJavascriptInterface(new GameProcessor(),"game_processor");
		webview.getSettings().setJavaScriptEnabled(true); 
		webview.getSettings().setLightTouchEnabled(true); 

	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		webview.saveState(outState);
	}
	
	private void session(String id, String username) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString("login_id", id);
        editor.putString("login_name", username);
        editor.commit();
	}

	public class LoginProcessor {
		private Login login;

		protected LoginProcessor(Login login) {
			this.login = login;
		}

		public void process(String sessionid,String username) {
			SessionIdSaver idsaver = new SessionIdSaver(login, sessionid, username);

			handler.post(idsaver);
		}

		public void logout() {
			handler.post(new Runnable() {
				public void run() {
					session("", "");

		            webview.loadUrl(URL + "logout/");
				}
			});
		}
	}

	protected class SessionIdSaver implements Runnable {
		private String id;
		private String username;
		private Login login;
		
		protected SessionIdSaver (Login login, String id, String username) {
			this.id = id;
			this.login = login;
			this.username = username;
		}

		public void run() {			
			session(id, username);
		}
	}

	public class GameProcessor {
		public void start(int size) {
	
			handler.post(new Runnable() {
				public void run() {
					startActivity(new Intent("com.popapatru.wordscocktail.action.MULTIPLAYER_GAME",
			           Uri.parse(URL)));
				}
			});
		}
	}
	
}
