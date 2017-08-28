package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Activities.MainActivity;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Adapter.RssFeedListAdapter;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.CheckInternet;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.Constant;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.Article;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PushNotification extends IntentService {
    private static final String TAG = PushNotification.class.getSimpleName();
    private String rssFeedLink = "http://feeds.feedburner.com/tinhte";
    public static final int NOTIFICATION_ID = 234;
    private Handler mHandler;
    private List<Article> articleList = new ArrayList<>();



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    public PushNotification() {
        super("PushNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            synchronized (this) {
                try {
                    int onwHours = 1000 * 60 * 60;
                    wait(onwHours * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new FetchFeedTask().execute((Void) null);
                        createNotification();
                    }
                });
            }
        }
    }

    public void createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.read_title))
                .setContentText(getString(R.string.read_now))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    public List<Article> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String imgLink = null;
        String title = null;
        String link = null;
        String description = null;
        String pubDate = null;
        boolean isItem = false;
        List<Article> articleList = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;

                } else if (name.equalsIgnoreCase("link")) {
                    link = result;

                } else if (name.equalsIgnoreCase("description")) {
                    Character mChracter = '"';
                    description = result;
                    if (description.contains("href=") && description.contains("attachments") && description.contains("target")) {
                        String srcLink = description.substring(description.indexOf("https://"));
                        String subStr = srcLink.substring(0, srcLink.indexOf(mChracter));
                        if (subStr.contains("attachments")) {
                            imgLink = subStr;
                        }
                    } else {
                        imgLink = Constant.LINK_IMG_ARTICLE_DEFAULT;
                    }

                } else if (name.equalsIgnoreCase("pubDate")) {
                    pubDate = result;
                }


                if (imgLink != null && pubDate != null && title != null && description != null && link != null) {
                    if (isItem) {
                        Article article = new Article();
                        article.setArImage(imgLink);
                        article.setArPubDate(pubDate);
                        article.setArTitle(title);
                        article.setArDescription(description);
                        article.setArLink(link);
                        articleList.add(article);
                    }
                    title = null;
                    link = null;
                    description = null;
                    pubDate = null;
                    isItem = false;
                }
            }

            return articleList;
        } finally {
            inputStream.close();
        }
    }


    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(rssFeedLink))
                return false;

            try {
                if (!rssFeedLink.startsWith("http://") && !rssFeedLink.startsWith("https://"))
                    rssFeedLink = "http://" + rssFeedLink;

                URL url = new URL(rssFeedLink);
                InputStream inputStream = url.openConnection().getInputStream();
                articleList = parseFeed(inputStream);
                return true;
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

            } else {
                Log.i(TAG, getString(R.string.error));
            }
        }
    }






















}
