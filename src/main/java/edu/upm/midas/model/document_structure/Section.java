package edu.upm.midas.model.document_structure;


import edu.upm.midas.model.document_structure.text.Text;

import java.util.List;

/**
 * Created by gerardo on 3/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className XmlSection
 * @see
 */
//Una sección siempre será <h2>
public class Section {

    private Integer id;
    private String name;
    private String description;

    private Integer textCount;
    private List<Text> textList;

    public Section() {
    }

    public Section(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public List<Text> getTextList() {
        return textList;
    }

    public void setTextList(List<Text> textList) {
        this.textList = textList;
        if (this.textList!=null) this.textCount = this.textList.size();

    }


//    @Override
//    public String toString() {
//        return "Section{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", textCount=" + textCount +
//                ", textList=" + textList +
//                '}';
//    }
}
