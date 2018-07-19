package edu.upm.midas.model.document_structure;

import java.util.Objects;

/**
 * Created by gerardo on 08/06/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project edsssdb
 * @className Link
 * @see
 */
public class Link {

    private int id;
    private String url;
    private String description;
    private Source source;


    public Link() {
    }

    public Link(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public Link(int id, String url, String description) {
        this.id = id;
        this.url = url;
        this.description = description;
    }

    public Link(String url, Source source) {
        this.url = url;
        this.source = source;
    }

    public Link(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        Link link = (Link) o;
        return Objects.equals(getUrl(), link.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }


//    @Override
//    public String toString() {
//        return "Link{" +
//                "id=" + id +
//                ", url='" + url + '\'' +
//                '}';
//    }
}
