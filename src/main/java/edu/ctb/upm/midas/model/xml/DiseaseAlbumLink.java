package edu.ctb.upm.midas.model.xml;

import java.util.Objects;

/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className XmlLink
 * @see
 */
public class DiseaseAlbumLink {

    private String consult;
    private Integer id;
    private String url;


    public String getConsult() {
        return consult;
    }

    public void setConsult(String consult) {
        this.consult = consult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiseaseAlbumLink)) return false;
        DiseaseAlbumLink link = (DiseaseAlbumLink) o;
        return Objects.equals(getUrl(), link.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}