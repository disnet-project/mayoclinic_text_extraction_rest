package edu.ctb.upm.midas.model.document_structure.code;


import edu.ctb.upm.midas.model.document_structure.Link;
import edu.ctb.upm.midas.model.document_structure.Link;

import java.util.List;
import java.util.Objects;

/**
 * Created by gerardo on 01/06/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesMayoClinic
 * @className Resource
 * @see
 */
public class Resource {

    private int id;
    private String name;
    private String nameDisease;

    private List<Link> linkList;


    public Resource() {}

    public Resource(int id, String name) {
        this.id = id;
        this.name = name;
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

    public List<Link> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<Link> linkList) {
        this.linkList = linkList;
    }

    public String getNameDisease() {
        return nameDisease;
    }

    public void setNameDisease(String nameDisease) {
        this.nameDisease = nameDisease;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getName(), resource.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
