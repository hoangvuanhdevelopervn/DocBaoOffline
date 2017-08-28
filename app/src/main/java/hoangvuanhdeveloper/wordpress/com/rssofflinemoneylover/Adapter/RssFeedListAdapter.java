package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Database.DatabaseSaveArticle;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.CheckInternet;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.R;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.Article;

/**
 * Created by HoangVuAnh on 8/27/17.
 */

public class RssFeedListAdapter extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {
    private List<Article> mArticles;
    private Context context;
    private CheckInternet checkInternet;
    private DatabaseSaveArticle databaseSaveArticle;


    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;
        private TextView tvArTitle, tvArPubDate, tvArDescription, tvArLink;
        private ImageView imgArImage;
        private ImageButton btnSaveArticle;
        private CardView cardView;


        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
            imgArImage = (ImageView) v.findViewById(R.id.imgArticle);
            tvArTitle = (TextView) v.findViewById(R.id.tvArTitle);
            tvArPubDate = (TextView) v.findViewById(R.id.tvArticlePubDate);
            tvArDescription = (TextView) v.findViewById(R.id.tvArDescription);
            tvArLink = (TextView) v.findViewById(R.id.tvArLinkText);
            cardView = (CardView) v.findViewById(R.id.card_view);
            btnSaveArticle = (ImageButton) v.findViewById(R.id.btnSaveArticle);
        }


    }


    public RssFeedListAdapter(Context context, List<Article> mArticles) {
        this.mArticles = mArticles;
        this.context = context;
        checkInternet = CheckInternet.getInstance(context);
        databaseSaveArticle = DatabaseSaveArticle.getInstance(context);
    }


    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rss_feed, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);


        return holder;
    }

    @Override
    public void onBindViewHolder(final FeedModelViewHolder holder, int position) {
        final Article article = mArticles.get(position);
        final String arLinkImage = article.getArImage();
        final String arTitle = article.getArTitle();
        final String arPubDate = article.getArPubDate();
        final String arDes = article.getArDescription();
        final String arLink = article.getArLink();


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxWidth = displayMetrics.widthPixels;
        float dpWidth = pxWidth / displayMetrics.density;
        int pxHeight = displayMetrics.heightPixels;
        float dpHeight = pxHeight / displayMetrics.density;
        holder.imgArImage.getLayoutParams().height = pxHeight / 3;




        if (!article.isSave()) {
            holder.btnSaveArticle.setBackgroundResource(R.drawable.favorite_selector);
        } else {
            holder.btnSaveArticle.setBackgroundResource(R.drawable.ic_action_fv_on);
        }

        holder.btnSaveArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!article.isSave()) {
                    holder.btnSaveArticle.setBackgroundResource(R.drawable.ic_action_fv_on);
                    databaseSaveArticle.insertArticle(arLinkImage, arPubDate, arTitle, arDes, arLink);
                    Toast.makeText(context, R.string.save, Toast.LENGTH_SHORT).show();
                    article.setSave(true);
                } else {
                    holder.btnSaveArticle.setBackgroundResource(R.drawable.favorite_selector);
                    Toast.makeText(context, R.string.remove, Toast.LENGTH_SHORT).show();
                    databaseSaveArticle.removeArticle(arLink);
                    article.setSave(false);
                }


            }
        });


        if (!TextUtils.isEmpty(arLinkImage)) {
            Picasso.with(context).load(arLinkImage).into(holder.imgArImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    holder.imgArImage.setImageResource(R.drawable.ar_default);
                }
            });
        } else {
            holder.imgArImage.setImageResource(R.drawable.ar_default);
        }


        if (!TextUtils.isEmpty(arTitle)) {
            holder.tvArTitle.setText(arTitle);
        } else {
            holder.tvArTitle.setText(R.string.title_test);
        }

        if (!TextUtils.isEmpty(arPubDate)) {
            holder.tvArPubDate.setText(arPubDate);
        } else {
            holder.tvArPubDate.setText(R.string.time_test);
        }

        if (!TextUtils.isEmpty(arDes)) {
            holder.tvArDescription.setText(arDes);
        } else {
            holder.tvArDescription.setText(R.string.content_test);
        }

        if (!TextUtils.isEmpty(arLink)) {
            holder.tvArLink.setText(arLink);
        } else {
            holder.tvArLink.setText(R.string.link_default);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {
                if (checkInternet.isOnline()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arLink));
                    context.startActivity(browserIntent);
                } else {
                    Toast.makeText(context, R.string.offline, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}



















