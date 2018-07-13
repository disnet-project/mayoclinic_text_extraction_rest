package edu.upm.midas.model.xml;

//Manipulación de XML
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by gerardo on 27/3/17.
 * @project ExtractionInformationMayoClinic
 * @version ${<VERSION>}
 * @author Gerardo Lagunes G.
 * @className XmlSource
 * @see
 */
public class XmlSource implements Serializable {

    private File xmlFile;

    private String consultSource;
    private Integer id;
    private String code;
    private String name;

    private String link;//URL Principal de la fuente

    private List<XmlMenuItem> xmlMenuItemList;
    private List<XmlSection> sectionList;
    private List<XmlHighlight> highlightList;
    private List<XmlLink> linkList;
    private List<DiseaseAlbumLink> diseaseAlbumLinkList;


    public File getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public String getConsultSource() {
        return consultSource;
    }

    public void setConsultSource(String consultSource) {
        this.consultSource = consultSource;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<XmlMenuItem> getXmlMenuItemList() {
        return xmlMenuItemList;
    }

    public void setXmlMenuItemList(List<XmlMenuItem> xmlMenuItemList) {
        this.xmlMenuItemList = xmlMenuItemList;
    }

    public List<XmlSection> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<XmlSection> sectionList) {
        this.sectionList = sectionList;
    }

    public List<XmlHighlight> getHighlightList() {
        return highlightList;
    }

    public void setHighlightList(List<XmlHighlight> highlightList) {
        this.highlightList = highlightList;
    }

    public List<XmlLink> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<XmlLink> linkList) {
        this.linkList = linkList;
    }

    public List<DiseaseAlbumLink> getDiseaseAlbumLinkList() {
        return diseaseAlbumLinkList;
    }

    public void setDiseaseAlbumLinkList(List<DiseaseAlbumLink> diseaseAlbumLinkList) {
        this.diseaseAlbumLinkList = diseaseAlbumLinkList;
    }
}
