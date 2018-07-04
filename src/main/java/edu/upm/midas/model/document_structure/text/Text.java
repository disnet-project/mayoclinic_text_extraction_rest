package edu.upm.midas.model.document_structure.text;


import edu.upm.midas.model.document_structure.Link;

import java.util.List;

/**
 * Created by gerardo on 3/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationWikipedia
 * @className Text
 * @see
 */
//Un Text siempre será un <p>, <ul><ol>
public class Text {

    private int id;
    private String title;//EL nombre si tiene, será un <h3>
    private int textOrder;

    private List<Link> urlList;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTextOrder() {
        return textOrder;
    }

    public void setTextOrder(int textOrder) {
        this.textOrder = textOrder;
    }

    public List<Link> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<Link> urlList) {
        this.urlList = urlList;
    }

}
