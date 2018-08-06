package edu.ctb.upm.midas.model.document_structure;

import edu.ctb.upm.midas.model.document_structure.text.TextJSON;

import java.util.List;

public class SectionJSON {

    private Integer id;
    private String name;
    private String description;

    private Integer textCount;
    private List<TextJSON> textList;
    private String update;

    public SectionJSON() {
    }

    public SectionJSON(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public SectionJSON(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTextCount() {
        return textCount;
    }

    public void setTextCount(Integer textCount) {
        this.textCount = textCount;
    }

    public List<TextJSON> getTextList() {
        return textList;
    }

    public void setTextList(List<TextJSON> textList) {
        this.textList = textList;
        if (this.textList!=null) this.textCount = this.textList.size();

    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", textCount=" + textCount +
                ", textList=" + textList +
                '}';
    }
}
