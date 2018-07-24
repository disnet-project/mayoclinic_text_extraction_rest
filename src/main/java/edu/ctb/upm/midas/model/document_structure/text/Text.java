package edu.ctb.upm.midas.model.document_structure.text;


import com.fasterxml.jackson.annotation.JsonInclude;
import edu.ctb.upm.midas.model.document_structure.Link;

import java.util.List;

/**
 * Created by gerardo on 3/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className Text
 * @see
 */
//Un Text siempre será un <p>, <ul><ol>
public class Text {

    private int id;
    private String title;//EL nombre si tiene, será un <h3>
    private int textOrder;
    @JsonInclude(JsonInclude.Include.NON_NULL)
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


    @Override
    public String toString() {
        return "Text{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", textOrder='" + textOrder + '\'' +
//                ", urlList=" + urlList +
                '}';
    }
}
