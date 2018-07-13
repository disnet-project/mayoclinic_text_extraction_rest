package edu.upm.midas.model.xml;

import edu.upm.midas.model.xml.title.Title;

/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className XmlSectionFather
 * @see
 */
public class XmlSectionFather {

    private Title typeTitle;
    private String class_;

    public XmlSectionFather(){}

    public XmlSectionFather(Title typeTitle, String class_){
        setTypeTitle(typeTitle);
        setClass_(class_);
    }

    public Title getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(Title typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
