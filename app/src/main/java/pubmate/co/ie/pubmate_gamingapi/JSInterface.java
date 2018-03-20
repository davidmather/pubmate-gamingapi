package pubmate.co.ie.pubmate_gamingapi;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.JavascriptInterface;

import static android.content.Context.MODE_PRIVATE;

public class JSInterface {
    private MainActivity currentActivity;

    public JSInterface(MainActivity pageActivity){
        currentActivity = pageActivity;
    }

    @JavascriptInterface
    public String requestPage(String pagename)
    {
        return "requested Page";
    }

    /*
    @JavascriptInterface
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
    */

    @JavascriptInterface
    public String createDatabase(String TableName){
        SQLiteDatabase myDB;
        String Data = "";
        myDB = currentActivity.openOrCreateDatabase("DatabaseNames", MODE_PRIVATE, null);

                /* Create a Table in the Database. */
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

                return Data;
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
    public String deleteDatabase(String TableName) {
        try {
            SQLiteDatabase myDB;
            myDB = currentActivity.openOrCreateDatabase("DatabaseNames", MODE_PRIVATE, null);

                /* Create a Table in the Database. */
            myDB.execSQL("DELETE FROM "+ TableName);

            return "success";

        } catch (Exception e){
            return "queryFailed";
        }

    }

    @JavascriptInterface
    public void setLandscape(){
        currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @JavascriptInterface
    public void setPortrait(){
        currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @JavascriptInterface
    public void createNotification(){
        currentActivity.createNotification();
    }
}
