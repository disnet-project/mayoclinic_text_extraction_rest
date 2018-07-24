package edu.ctb.upm.midas.model.xml;
/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className XmlHighlight
 * @see
 */
public class XmlHighlight {

    private String consult;
    private String id;
    private String class_;
    private String type;
    private String description;


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

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
