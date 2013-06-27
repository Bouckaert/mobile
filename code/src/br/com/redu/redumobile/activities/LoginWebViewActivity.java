package br.com.redu.redumobile.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.util.PinCodeHelper;
import br.com.redu.redumobile.util.WebUtil;

public class LoginWebViewActivity extends BaseActivity {

	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(PinCodeHelper.has(this)) {
			startActivity(new Intent(this, HomeActivity.class));
			finish();
			
		} else if(WebUtil.checkConnection(LoginWebViewActivity.this)) {
			setContentView(R.layout.activity_login_web);
	
			mWebView = (WebView) findViewById(R.id.webview);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
	
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					if (url.equals("http://www.redu.com.br/oauth/authorize")) {
						showProgressDialog("Aguarde alguns instantes enquanto você é redirecionado ao aplicativo do Redu…", false);
					}
				}
				@Override
				public void onPageFinished(WebView view, String url) {
					// This call inject JavaScript into the page which just finished loading.
					if (url.equals("http://www.redu.com.br/oauth/authorize")) {
						mWebView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('code')[0].innerHTML);");
					}
				}
			});
	
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
			 		DefaultReduClient client = ReduApplication.getReduClient(LoginWebViewActivity.this);
			 		return client.getAuthorizeUrl();
				}
				@Override
				protected void onPostExecute(String authorizeUrl) {
					mWebView.loadUrl(authorizeUrl);			
				}
			}.execute();
		
		} else {
			showAlertDialog(LoginWebViewActivity.this, "Verifique sua conexão com a Internet e tente novamente.", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		}
	}

	// An instance of this class will be registered as a JavaScript interface
	class MyJavaScriptInterface {
		public void processHTML(String pinCode) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
			 		DbHelper dbHelper = DbHelper.getInstance(LoginWebViewActivity.this);
			 		User appUser = ReduApplication.getUser(LoginWebViewActivity.this);
			 		dbHelper.putAppUser(appUser);
			 		return null;
				}
			}.execute();
			
			PinCodeHelper.set(getApplicationContext(), pinCode);
			
			Intent it = new Intent(LoginWebViewActivity.this, HomeActivity.class);
			startActivity(it);
			
			dismissProgressDialog();
			finish();
		}
	}
}
