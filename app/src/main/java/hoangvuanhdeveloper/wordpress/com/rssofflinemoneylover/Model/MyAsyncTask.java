package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.Constant;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Interface.AsyncResponse;



public class MyAsyncTask extends AsyncTask<Void, Void, List<Article>> {
    public AsyncResponse delegate = null;
    private String TAG = "ASYNTASK";
    private String rssFeedLink = "http://feeds.feedburner.com/tinhte";
    private List<Article> mFeedModelList = new ArrayList<>();


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Article> doInBackground(Void... voids) {
        if (TextUtils.isEmpty(rssFeedLink))
            return null;

        try {
            if (!rssFeedLink.startsWith("http://") && !rssFeedLink.startsWith("https://"))
                rssFeedLink = "http://" + rssFeedLink;

            URL url = new URL(rssFeedLink);
            InputStream inputStream = url.openConnection().getInputStream();
            mFeedModelList = parseFeed(inputStream);
            return mFeedModelList;
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Error", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Article> articles) {
        super.onPostExecute(articles);
        delegate.processFinish(articles);
    }

    private List<Article> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
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


}
