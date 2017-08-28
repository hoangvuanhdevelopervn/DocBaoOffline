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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Activities.MainActivity;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Database.DatabaseSaveArticle;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Helpers.CheckInternet;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.Article;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.R;

/**
 * Created by HoangVuAnh on 8/27/17.
 */

public class Adapter_Save_Article extends RecyclerView.Adapter<Adapter_Save_Article.FeedModelViewHolder> {

    private List<Article> mListArticles;
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

    public Adapter_Save_Article(Context context, List<Article> mListArticles) {
        this.mListArticles = mListArticles;
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
    public void onBindViewHolder(final FeedModelViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Article article = mListArticles.get(position);
        final String arLinkImage = article.getArImage();
        final String arTitle = article.getArTitle();
        final String arPubDate = article.getArPubDate();
        final String arDes = article.getArDescription();
        final String arLink = article.getArLink();
        holder.btnSaveArticle.setBackgroundResource(R.drawable.ic_action_fv_on);


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxWidth = displayMetrics.widthPixels;
        float dpWidth = pxWidth / displayMetrics.density;
        int pxHeight = displayMetrics.heightPixels;
        float dpHeight = pxHeight / displayMetrics.density;
        holder.imgArImage.getLayoutParams().height = pxHeight / 3;


        holder.btnSaveArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListArticles.remove(position);
                notifyDataSetChanged();
                databaseSaveArticle.removeArticle(arLink);
                Toast.makeText(context, R.string.remove, Toast.LENGTH_SHORT).show();

            }
        });

        holder.imgArImage.setBackgroundResource(R.drawable.ar_default);


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
                    /**
                     *
                     final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                     LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     @SuppressLint("InflateParams") View dialogView = layoutInflater.inflate(R.layout.dialog_read_article_offline, null);
                     dialog.setContentView(dialogView);
                     TextView tvArTittleTab = (TextView) dialog.findViewById(R.id.tvArTittleTab);
                     ImageButton imageButton = (ImageButton) dialog.findViewById(R.id.btnClose);
                     TextView tvArTitle = (TextView) dialog.findViewById(R.id.tvArTitle);
                     TextView tvArticlePubDate = (TextView) dialog.findViewById(R.id.tvArticlePubDate);
                     TextView tvArDescription = (TextView) dialog.findViewById(R.id.tvArDescription);

                     tvArTittleTab.setText(mListArticles.get(position).getArTitle());
                     tvArTitle.setText(mListArticles.get(position).getArTitle());
                     tvArticlePubDate.setText(mListArticles.get(position).getArPubDate());
                     tvArDescription.setText(mListArticles.get(position).getArDescription());

                     imageButton.setOnClickListener(new View.OnClickListener() {
                     @Override public void onClick(View v) {
                     dialog.cancel();
                     }
                     });
                     dialog.show();
                     */
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListArticles.size();
    }


}
