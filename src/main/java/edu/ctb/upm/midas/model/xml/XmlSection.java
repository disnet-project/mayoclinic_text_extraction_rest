package edu.ctb.upm.midas.model.xml;

import java.util.Objects;

/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className XmlSection
 * @see
 */
public class XmlSection extends XmlSectionFather {

    private String consult;
    private String id;
    private String type;
    private String name;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XmlSection)) return false;
        XmlSection section = (XmlSection) o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
