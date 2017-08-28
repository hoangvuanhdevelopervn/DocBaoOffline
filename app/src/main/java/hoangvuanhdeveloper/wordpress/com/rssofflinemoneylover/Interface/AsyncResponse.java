package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Interface;

import java.util.List;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.Article;

/**
 * Created by HoangVuAnh on 8/28/17.
 */

public interface AsyncResponse {
    void processFinish(List<Article> articleList);
}
