package edu.upm.midas.model.document_structure;



import edu.upm.midas.model.document_structure.code.Code;

import java.util.List;

/**
 * Created by gerardo on 08/06/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project edsssdb
 * @className Doc
 * @see
 */
public class Doc {

    private int id;
    private String date;
    private Link url;
    private List<Link> urlList;
    private boolean hasConnected;
    private boolean diseaseArticle;

    private Disease disease;
    private Integer sectionCount;
    private List<Section> sectionList;
    private Integer codeCount;
    private List<Code> codeList;


    public Doc() {
    }

    public Doc(int id, String date, Link url, boolean hasConnected, boolean diseaseArticle) {
        this.id = id;
        this.date = date;
        this.url = url;
        this.hasConnected = hasConnected;
        this.diseaseArticle = diseaseArticle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Link getUrl() {
        return url;
    }

    public void setUrl(Link url) {
        this.url = url;
    }

    public List<Link> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<Link> urlList) {
        this.urlList = urlList;
    }

    public boolean isHasConnected() {
        return hasConnected;
    }

    public void setHasConnected(boolean hasConnected) {
        this.hasConnected = hasConnected;
    }

    public boolean isDiseaseArticle() {
        return diseaseArticle;
    }

    public void setDiseaseArticle(boolean diseaseArticle) {
        this.diseaseArticle = diseaseArticle;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public Integer getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public Integer getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(Integer codeCount) {
        this.codeCount = codeCount;
    }

    public List<Code> getCodeList() {
        return codeList;
    }

    public void setCodeList(List<Code> codeList) {
        this.codeList = codeList;
    }


    @Override
    public String toString() {
        return "Doc{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", url=" + url +
                ", hasConnected=" + hasConnected +
                ", diseaseArticle=" + diseaseArticle +
                ", disease=" + disease +
//                ", sectionCount=" + sectionCount +
//                ", sectionList=" + sectionList +
//                ", codeCount=" + codeCount +
//                ", codeList=" + codeList +
                '}';
    }
}
