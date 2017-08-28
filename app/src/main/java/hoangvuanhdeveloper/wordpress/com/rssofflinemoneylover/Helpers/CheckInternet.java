package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by HoangVuAnh on 8/27/17.
 */

public class CheckInternet {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static CheckInternet instance;

    static {
        instance = new CheckInternet();
    }

    private boolean connected;
    private NetworkInfo mobileInfo;
    private NetworkInfo wifiInfo;

    public CheckInternet() {
        this.connected = false;
    }

    public static CheckInternet getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean z = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            this.connected = z;
            return this.connected;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
            return this.connected;
        }
    }
}
