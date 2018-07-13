package edu.upm.midas.model.document_structure;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by gerardo on 03/05/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesMayoClinic
 * @className LoadSource
 * @see
 */
public class Source {

    private int id;
    private String code;
    private String name;
    private Link url;

    private Integer documentCount;
    private List<Doc> documents;
    private TreeMap<String, String> sectionMap;


    public Source() {
    }

    public Source(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Source(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Source(int id, String code, String name, Link url) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Link getUrl() {
        return url;
    }

    public void setUrl(Link url) {
        this.url = url;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }

    public List<Doc> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Doc> documents) {
        this.documents = documents;
        if (this.documents!=null) this.documentCount = this.documents.size();
    }

    public TreeMap<String, String> getSectionMap() {
        return sectionMap;
    }

    public void setSectionMap(TreeMap<String, String> sectionMap) {
        this.sectionMap = sectionMap;
    }

}
