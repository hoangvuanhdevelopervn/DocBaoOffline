package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Adapter.Adapter_Save_Article;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Adapter.RssFeedListAdapter;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Database.DatabaseSaveArticle;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.CheckInternet;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.Constant;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Interface.AsyncResponse;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.Article;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.MyAsyncTask;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.R;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Service.PushNotification;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    private static final String TAG = "MainActivity";
    private String rssFeedLink = "http://feeds.feedburner.com/tinhte";
    boolean doubleBackToExitPressedOnce = false;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private List<Article> mFeedModelList;
    private CheckInternet checkInternet;
    private DatabaseSaveArticle databaseSaveArticle;
    private SharedPreferences sharedPreferences;
    private String keyPref = null;
    private ProgressDialog progress;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setUpView();


        setCheckInternet();


    }



    @Override
    public void processFinish(List<Article> articleList) {
/*
  new  MyAsyncTask().execute();
        Log.i(TAG, "--------------------------------" + articleList);
 */

    }

    private void startServiceGetNews() {
        if (keyPref.equals(Constant.KEY_YES)) {
            Intent intent = new Intent(this, PushNotification.class);
            startService(intent);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();
        keyPref = sharedPreferences.getString(Constant.KEY_ANSWER, null);
        if (!TextUtils.isEmpty(keyPref)) {
            if (Objects.equals(keyPref, Constant.KEY_YES)) {
                startServiceGetNews();
            }
        }
    }

    private boolean askRunService() {
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor editor = sharedPreferences.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.ic_doc_bao_offline);
        builder.setTitle(R.string.get_news);
        builder.setMessage(R.string.is_get_news);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putString(Constant.KEY_ANSWER, Constant.KEY_YES);
                editor.apply();
                editor.commit();
                Toast.makeText(MainActivity.this, R.string.no_miss_anynews, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, R.string.will_miss, Toast.LENGTH_SHORT).show();
                editor.putString(Constant.KEY_ANSWER, Constant.KEY_NO);
                editor.apply();
                editor.commit();
                dialog.cancel();
            }
        }).create().show();
        return false;
    }

    private void setCheckInternet() {
        if (checkInternet.isOnline()) {
            new FetchFeedTask().execute((Void) null);
            progress.setMessage(getString(R.string.loading));
            progress.show();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progress = new ProgressDialog(MainActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkInternet = CheckInternet.getInstance(MainActivity.this);
        databaseSaveArticle = DatabaseSaveArticle.getInstance(MainActivity.this);
        sharedPreferences = getSharedPreferences(Constant.SHARE_PREF, MODE_PRIVATE);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkInternet.isOnline()) {
                    new FetchFeedTask().execute((Void) null);
                    mSwipeLayout.setRefreshing(true);
                    Toast.makeText(MainActivity.this, getString(R.string.refresh), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
                }
            }
        });
        keyPref = sharedPreferences.getString(Constant.KEY_ANSWER, null);
        if (TextUtils.isEmpty(keyPref)) {
            askRunService();
        }
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
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);
            if (success) {
                mRecyclerView.setAdapter(new RssFeedListAdapter(MainActivity.this, mFeedModelList));
                progress.dismiss();



            } else {
                Toast.makeText(MainActivity.this,
                        getString(R.string.error),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
/*
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
 */
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View dialogView = layoutInflater.inflate(R.layout.dialog_about, null);
            dialog.setContentView(dialogView);
            TextView tvClose = (TextView) dialog.findViewById(R.id.tvClose);
            TextView tvRate = (TextView) dialog.findViewById(R.id.tvRate);
            TextView tvDevelop = (TextView) dialog.findViewById(R.id.tvDeveloped);


            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            assert pInfo != null;
            String versionName = pInfo.versionName;
            tvDevelop.setText(getString(R.string.version) + " : " + versionName + "\n" + getString(R.string.developed_by_hvasoftware));


            tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            tvRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String appId = android.support.compat.BuildConfig.APPLICATION_ID;
                    String url = "https://play.google.com/store/apps/details?id=" + appId;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            dialog.show();


            return true;
        } else if (id == R.id.action_ar_save) {
            final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View dialogView = layoutInflater.inflate(R.layout.dialog_read_rss_offline, null);
            dialog.setContentView(dialogView);
            RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.mRecyclerView);
            ImageButton imageButton = (ImageButton) dialog.findViewById(R.id.btnClose);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            if (databaseSaveArticle.getAllArticles().size() == 0) {
                Toast.makeText(MainActivity.this, R.string.no_article, Toast.LENGTH_SHORT).show();
            } else {

                Adapter_Save_Article adapter_save_article = new Adapter_Save_Article(MainActivity.this, databaseSaveArticle.getAllArticles());
                recyclerView.setAdapter(adapter_save_article);
                dialog.show();
            }


        } else if (id == R.id.action_getNews) {
            askRunService();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.action_search), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
