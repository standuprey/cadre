package net.standupweb.cadre;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
	protected static final String BASE_URL = "file:///android_asset/www/";
	private static final String TAG = "cadre";
	protected JavascriptBridge jsBridge = JavascriptBridge.instance();
	protected WebView appView;
	private View loader;
	private Bundle instanceState;
	private String url;
	final Context myApp = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(R.id.full_web_view, R.layout.activity_main, "home.html");
	}

	protected void init(int webViewId, int layoutId, String url) {
		setContentView(layoutId);
		jsBridge.setApp(myApp);
		appView = ((WebView) findViewById(webViewId));
		loader = findViewById(R.id.loader);
		if (instanceState != null)
			appView.restoreState(instanceState);
		appView.addJavascriptInterface(jsBridge, "Native");
		appView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				loader.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d(TAG, "page started");
				loader.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!url.startsWith("http://")) {
					Log.d(TAG, "loading external url: " + url);
					view.loadUrl(url);
					return true;
				}
				return false;
			}

		});
		/* WebChromeClient must be set BEFORE calling loadUrl! */
		appView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final android.webkit.JsResult result) {
				new AlertDialog.Builder(myApp)
						.setTitle("javaScript dialog")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			};
		});
		appView.getSettings().setJavaScriptEnabled(true);
		jsBridge.setApp(this);
		this.url = url;
		Log.d(TAG, "init done");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "resume");
		String intentUrl = getIntent().getStringExtra("url");
		if ((intentUrl != null) && (intentUrl.equals("undefined")))
			url = intentUrl;
		goToUrl(url);
	}

	protected void goToUrl(String url) {
		appView.loadUrl(BASE_URL + url);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		appView.saveState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
