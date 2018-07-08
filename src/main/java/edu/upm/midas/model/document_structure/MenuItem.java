package edu.upm.midas.model.document_structure;

import java.util.List;

/**
 * Created by gerardo on 03/07/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project mayoclinic_text_extraction_rest
 * @className XmlMenuItem
 * @see
 */
public class MenuItem {

    private int id;
    private String name;
    private List<Section> sectionList;


    public MenuItem() {
    }

    public MenuItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public MenuItem(int id, String name, List<Section> sectionList) {
        this.id = id;
        this.name = name;
        this.sectionList = sectionList;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }


    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sectionList=" + sectionList +
                '}';
    }
}
