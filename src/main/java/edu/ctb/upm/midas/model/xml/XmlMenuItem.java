package edu.ctb.upm.midas.model.xml;
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
public class XmlMenuItem {

    private String consult;
    private String id;
    private String type;
    private String name;
    private String role;
    private List<XmlSection> sectionList;


    public XmlMenuItem() {
    }

    public XmlMenuItem(String consult, String id, String type, String name, String role) {
        this.consult = consult;
        this.id = id;
        this.type = type;
        this.name = name;
        this.role = role;
    }

    public XmlMenuItem(String consult, String id, String type, String name, String role, List<XmlSection> sectionList) {
        this.consult = consult;
        this.id = id;
        this.type = type;
        this.name = name;
        this.role = role;
        this.sectionList = sectionList;
    }

    public String getConsult() {
        return consult;
    }

    public void setConsult(String consult) {
        this.consult = consult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<XmlSection> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<XmlSection> sectionList) {
        this.sectionList = sectionList;
    }
}
