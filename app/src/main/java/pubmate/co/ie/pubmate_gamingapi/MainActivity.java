package pubmate.co.ie.pubmate_gamingapi;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private WebView webView;

    /*
    final class JSInterface
    {
        @JavascriptInterface
        public String requestPage(String pagename)
        {
            // start requested Activity
            return "requested Page";
        }


        @JavascriptInterface
        public String createDatabase(String TableName){
            SQLiteDatabase myDB;
            String Data = "";
            myDB = openOrCreateDatabase("DatabaseNames", MODE_PRIVATE, null);

            // Create a Table in the Database.
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " (Field1 VARCHAR PRIMARY KEY, Field2 INT(3))"
            );

            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName, null);

            try {

                int Column1 = c.getColumnIndex("Field1");
                int Column2 = c.getColumnIndex("Field2");

                // Check if our result was valid.
                c.moveToFirst();
                if (c != null) {
                    // Loop through all Results
                    do {
                        String Name = c.getString(Column1);
                        int Age = c.getInt(Column2);
                        Data = Data + Name + "/" + Age + "\n";
                    } while (c.moveToNext());

                    return "User exists";
                }
                return "queryFailed";
            } catch (Exception e){
                myDB.execSQL("INSERT OR REPLACE INTO "
                        + TableName + " (Field1, Field2)"
                        + " VALUES ('user', 23)");
                return  "User created";
            }
        }

        @JavascriptInterface
        private String deleteTable(String TableName) {
            try {
                SQLiteDatabase myDB;
                myDB = openOrCreateDatabase("DatabaseNames", MODE_PRIVATE, null);

                myDB.execSQL("DELETE FROM "+ TableName);

                return "success";

            } catch (Exception e){
                return "queryFailed";
            }

        }

    }

    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "file:///android_asset/index.html";
        webView =(WebView) this.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(this), "android");
        webView.loadUrl(url);

    }

    @Override
    protected void onPause(){
        super.onPause();

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                createNotification();
            }
        }, 0, 2, TimeUnit.MINUTES);
    }

    public String getResponseFromUrl(String URL) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(URL));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                //..more logic
                return responseString;
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException Exception){
            return "error";
        }

    }

    private int notificationId = 0;
    private String data = "";
    private String newData;
    public void createNotification() {
        View view = webView;

        if(data.equals("")){
            data = getResponseFromUrl("http://192.168.1.1:8080/");
            return;
        } else {
            newData = getResponseFromUrl("http://192.168.1.1:8080/");

        }

        if(newData.equals(data)){
            return;
        }
        data = newData;

        long[] pattern = {500,500,500,500,500,500,500,500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
            .setContentTitle("New mail from " + "test@gmail.com")
            .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pIntent)
            .addAction(R.drawable.ic_launcher_background, "Call", pIntent)
            .addAction(R.drawable.ic_launcher_background, "More", pIntent)
            .setAutoCancel(true)
            .setLights(Color.BLUE, 500, 500)
            .setVibrate(pattern)
            .setSound(alarmSound)
            .addAction(R.drawable.ic_launcher_background, "And more", pIntent).build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(notificationId, noti);
        notificationId++;

    }
    //@Override
    //protected void onResume() {
    //    super.onResume();
    //    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    //}
}
