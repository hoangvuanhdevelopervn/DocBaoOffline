package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model;

/**
 * Created by HoangVuAnh on 8/27/17.
 */

public class Article {
    private String arImage;
    private String arPubDate;
    private String arTitle;
    private String arLink;
    private String arDescription;
    private boolean isSave;

    public Article() {
    }


    public String getArImage() {
        return arImage;
    }

    public void setArImage(String arImage) {
        this.arImage = arImage;
    }

    public String getArPubDate() {
        return arPubDate;
    }

    public void setArPubDate(String arPubDate) {
        this.arPubDate = arPubDate;
    }

    public String getArTitle() {
        return arTitle;
    }

    public void setArTitle(String arTitle) {
        this.arTitle = arTitle;
    }

    public String getArLink() {
        return arLink;
    }

    public void setArLink(String arLink) {
        this.arLink = arLink;
    }

    public String getArDescription() {
        return arDescription;
    }

    public void setArDescription(String arDescription) {
        this.arDescription = arDescription;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }


}
