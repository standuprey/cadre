package net.standupweb.cadre;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

public class JavascriptBridge {
  private Context app;
  private static JavascriptBridge instance;

  public static JavascriptBridge instance() {
    if (instance == null)
      instance = new JavascriptBridge();
    return instance;
  }

  public void setApp(Context c) {
    app = c;
  }
  
  @JavascriptInterface
  public String test() {
    return "test";
  }

  @JavascriptInterface
  public void startActivity(String className, String url) {
    try {
      Intent intent = new Intent(app.getApplicationContext(), Class.forName(this.getClass().getPackage().getName()+".activity."+className));
      if (url != null) intent.putExtra("url", url);
      app.startActivity(intent);
    } catch (Exception e) {e.printStackTrace();}
  }
}
