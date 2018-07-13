package edu.upm.midas.component;

import edu.upm.midas.constants.Constants;
import edu.upm.midas.model.xml.*;
import edu.upm.midas.model.xml.title.*;
import edu.upm.midas.common.util.Common;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gerardo on 04/05/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesMayoClinic
 * @className ReadXml
 * @see
 */
@Component
public class ReadXml {

    private static final Logger logger = LoggerFactory.getLogger(ReadXml.class);

    private XmlSource oXml = new XmlSource();
    @Autowired
    private Common common;



    /**
     * @return
     * @throws Exception
     */
    public XmlSource read() throws JDOMException, IOException {

//        ArrayList<XmlSource> xmlList = new ArrayList<>();
        XmlSource sourceConfiguration = null;
        ArrayList<XmlMenuItem> xmlMenuItemList = new ArrayList<>();
        ArrayList<XmlSection> sectionList = new ArrayList<>();
        ArrayList<XmlHighlight> highlightList = new ArrayList<>();
        ArrayList<XmlLink> linkList = new ArrayList<>();

        File xmlFile = oXml.getXmlFile(); //new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/") + "/resources/bd/bd.xml");
        try {
            SAXBuilder constructorXml = new SAXBuilder();
            //Se crea el documento a traves del archivo
            Document docXml = constructorXml.build(xmlFile);
            //Se obtiene la raiz 'sources'
            Element rootSources = docXml.getRootElement();
            //Se obtiene los hijos 'source' de la raiz 'sources'
//            List sourceList = rootSources.getChildren(Constants.XML_ROOT_TAG);
            Element source = rootSources.getChildren(Constants.XML_ROOT_TAG).get(0);
            //Se recorren los hijos de 'source'
            //<editor-fold desc="RECORRIDO DE LAS FUENTES EXISTENTES (SOLO HAY UNA)">
//            for (int i = 0; i < sourceList.size(); i++)
            if (source!=null){
                oXml = new XmlSource();

                //Se obtiene el elemento 'source'
//                Element source = (Element) sourceList.get(i);

                //Se obtiene el atributo 'consult', 'id' y 'code' que esta en el tag 'source'
                oXml.setConsultSource(source.getAttributeValue(Constants.XML_ATT_CONSULT).trim());
                oXml.setId(Integer.parseInt(source.getAttributeValue(Constants.XML_ATT_ID)));
                oXml.setCode(source.getAttributeValue(Constants.XML_ATT_CODE.trim()));

                //Si se permite 'y' se consulta un recurso
                if (oXml.getConsultSource().equals(Constants.XML_ATT_CONSULT_YES)) {
                    //Se obtiene el valor del tag 'name' y 'link'
                    oXml.setName(source.getChild(Constants.XML_TAG_NAME).getTextTrim());
                    oXml.setLink(source.getChild(Constants.XML_TAG_LINK).getTextTrim());

                    //<<<<<<<<<<<<<<<<<<<<< TABLE OF CONTENTS <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    Element tableOfContents = source.getChild(Constants.XML_TAG_TABLEOFCONTENTS);
                    //Se permite 'y' consultar la table of contents
                    if (tableOfContents.getAttributeValue(Constants.XML_ATT_CONSULT).trim().equals(Constants.Y_YES)) {
                        processXmlTableOfContents(tableOfContents, xmlMenuItemList, sectionList);
                    }
                    oXml.setXmlMenuItemList(xmlMenuItemList);
                    oXml.setSectionList(sectionList);
                    //<<<<<<<<<<<<<<<<<<<<< TABLE OF CONTENTS <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    Element highlights = source.getChild(Constants.XML_TAG_HIGHLIGTS);
                    //Se permite 'y' consultar todas las highlights
                    if (highlights.getAttributeValue(Constants.XML_ATT_CONSULT).trim().equals(Constants.XML_ATT_CONSULT_YES)) {
                        processXmlHighlight(highlights, highlightList);
                    }//end if( highlights.getAttributeValue("consult").charAt(0) == 'y') Se permite 'y' consultar las highlights
                    //Se almacena la lista de highlights al objeto
                    oXml.setHighlightList(highlightList);
                    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    Element links = source.getChild(Constants.XML_TAG_LINKS);
                    //Se permite 'y' consultar todos las links
                    if (links.getAttributeValue(Constants.XML_ATT_CONSULT).trim().equals(Constants.XML_ATT_CONSULT_YES)) {
                        processXmlLink(links, linkList);
                    }//end if( links.getAttributeValue("consult").charAt(0) == 'y') Se permite 'y' consultar los links
                    //Se almacena la lista de sections al objeto
                    oXml.setLinkList(linkList);
                    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

                }//end if(getConsultSource() == 'y')
                sourceConfiguration = oXml;
//                xmlList.add(oXml);
            }
            //</editor-fold>
        }catch (Exception e){
            logger.error("Error to build xml source configuration {} (ReadXml.read())", xmlFile, e);
        }
        return sourceConfiguration;
    }


    /**
     * @param xmlFile
     */
    public void file(File xmlFile){
        oXml.setXmlFile(xmlFile);
    }


    /**
     * @param elementsList
     */
    public void removeRepetedSections(List<XmlSection> elementsList){
        Set<XmlSection> linkedHashSet = new LinkedHashSet<XmlSection>();
        linkedHashSet.addAll(elementsList);
        elementsList.clear();
        elementsList.addAll(linkedHashSet);
    }


    /**
     * @param elementsList
     */
    public void removeRepetedLinks(List<XmlLink> elementsList){
        Set<XmlLink> linkedHashSet = new LinkedHashSet<XmlLink>();
        linkedHashSet.addAll(elementsList);
        elementsList.clear();
        elementsList.addAll(linkedHashSet);
    }


    /**
     * @param sType
     * @return
     */
    //Es una constante
    public Title getTypeTitle(String sType){

        Title typeTitle;

        switch (sType){
            case "h1": typeTitle = new H1('y', 1);
                break;
            case "h2": typeTitle = new H2('y', 2);
                break;
            case "h3": typeTitle = new H3('y', 3);
                break;
            case "h4": typeTitle = new H4('y', 4);
                break;
            case "h5": typeTitle = new H5('y', 5);
                break;
            default: typeTitle = new H2('y', 2);
                break;
        }

        return typeTitle;

    }


    public void processXmlTableOfContents(Element tableOfContents, List<XmlMenuItem> xmlMenuItemList, List<XmlSection> sectionList){
        //<<<<Se obtiene el hijo 'tableofcontents' y después la lista de sus hijos>>>>
        List itemList = tableOfContents.getChildren();
        //Validación de lista de enlaces vacía
        if (itemList.size() > 0) {
            //Se recorren los hijos de 'tableofcontents'
            for (int l = 0; l < itemList.size(); l++) {
                XmlMenuItem xmlMenuItem = new XmlMenuItem();
                //Se obtiene el prelemento 'section'
                Element item = (Element) itemList.get(l);
                if( item.getAttributeValue( Constants.XML_ATT_CONSULT ).trim().equals( Constants.XML_ATT_CONSULT_YES ) ) {
                    //Se obtiene el valor de los atributos de cada tag 'section'
                    xmlMenuItem.setConsult( item.getAttributeValue( Constants.XML_ATT_CONSULT ).trim() );
                    xmlMenuItem.setId( item.getAttributeValue( Constants.XML_ATT_ID ).trim() );
                    xmlMenuItem.setRole( item.getAttributeValue( Constants.XML_ATT_ROLE ).trim() );
                    xmlMenuItem.setType( item.getAttributeValue( Constants.XML_ATT_TYPE ).trim() );
                    xmlMenuItem.setName( item.getAttributeValue( Constants.XML_ATT_NAME ).trim() );
                    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< SECTIONS <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    Element sections = item.getChild( Constants.XML_TAG_SECTIONS );
                    //Se permite 'y' consultar todas las secciones
                    if( sections.getAttributeValue( Constants.XML_ATT_CONSULT ).trim().equals(Constants.Y_YES) ){
                        List<XmlSection> localSections = processXmlSection(sections, sectionList);
                        //Se almacena la lista de sections al objeto
                        xmlMenuItem.setSectionList(localSections);
                    }//end if( sections.getAttributeValue("consult").charAt(0) == 'y') Se permite 'y' consultar las secciones
                    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                }
                //Agrega un elemento a la lista de elementos del objeto principal
                xmlMenuItemList.add(xmlMenuItem);
            }
        }
    }


    /**
     * @param sections
     * @param sectionList
     */
    public List<XmlSection> processXmlSection(Element sections, List<XmlSection> sectionList){
        List<XmlSection> localSectionList = new ArrayList<>();
        //<<<<Se obtiene el hijo 'section' y después la lista de sus hijos>>>>
        List sectionsList = sections.getChildren();
        //Validación de lista de enlaces vacía
        if (sectionsList.size() > 0) {
            //Se recorren los hijos de 'section'
            for (int l = 0; l < sectionsList.size(); l++) {
                XmlSection oSec = new XmlSection();
                oSec.setTypeTitle( getTypeTitle( sections.getAttributeValue( Constants.XML_ATT_TYPE ).trim() ) );
                oSec.setClass_( sections.getAttributeValue( Constants.XML_ATT_CLASS ).trim() );
                //Se obtiene el prelemento 'section'
                Element section = (Element) sectionsList.get(l);

                if( section.getAttributeValue( Constants.XML_ATT_CONSULT ).trim().equals( Constants.XML_ATT_CONSULT_YES ) ) {
                    //Se obtiene el valor de los atributos de cada tag 'section'
                    oSec.setConsult( section.getAttributeValue( Constants.XML_ATT_CONSULT ).trim() );
                    oSec.setId( section.getAttributeValue( Constants.XML_ATT_ID ).trim() );
                    oSec.setType( section.getAttributeValue( Constants.XML_ATT_TYPE ).trim() );
                    oSec.setName( section.getAttributeValue( Constants.XML_ATT_NAME ).trim() );
//                    System.out.print(section.getAttributeValue( Constants.XML_ATT_NAME ).trim());
                    localSectionList.add(oSec);
                    sectionList.add(oSec);
                }

            }//end for (int l = 0; l < sectionsList.size(); l++)
            //Se eliminan las 'section' repetidas
//            removeRepetedSections(sectionList);

        }//end if (sectionsList.size() > 0)
        return localSectionList;

    }//end method public void processXmlSection(Element sections, List_<XmlSection> sectionList)


    /**
     * @param highlights
     * @param highlightList
     */
    public void processXmlHighlight(Element highlights, List<XmlHighlight> highlightList){

        //<<<<Se obtiene el hijo 'highlight' y después la lista de sus hijos>>>>
        List highlightListsList = highlights.getChildren();
        //Validación de lista de enlaces vacía
        if (highlightListsList.size() > 0) {

            //Se recorren los hijos de 'highlights'
            for (int l = 0; l < highlightListsList.size(); l++) {
                XmlHighlight oHighlight = new XmlHighlight();
                //Se obtiene el prelemento 'highlight'
                Element highlight = (Element) highlightListsList.get(l);

                if( highlight.getAttributeValue( Constants.XML_ATT_CONSULT ).charAt(0) == 'y' ) {
                    //Se obtiene el valor de los atributos de cada tag 'highlight'
                    oHighlight.setConsult( highlight.getAttributeValue( Constants.XML_ATT_CONSULT ).trim() );
                    oHighlight.setId( highlight.getAttributeValue( Constants.XML_ATT_ID ).trim() );
                    oHighlight.setClass_( highlight.getAttributeValue( Constants.XML_ATT_CLASS ).trim() );
                    oHighlight.setType( highlight.getAttributeValue( Constants.XML_ATT_TYPE ).trim() );
                    oHighlight.setDescription( highlight.getAttributeValue( Constants.XML_ATT_DESC ).trim() );

                    highlightList.add(oHighlight);
                }
            }//end for (int l = 0; l < highlightListsList.size(); l++)

        }//end if (highlightListsList.size() > 0)

    }//end method public void processXmlHighlight(Element highlights, List_<XmlHighlight> highlightList){


    /**
     * @param links
     * @param linkList
     */
    public void processXmlLink(Element links, List<XmlLink> linkList){

        //<<<<Se obtiene el hijo 'link' y después la lista de sus hijos>>>>
        List linksList = links.getChildren();
        //Validación de lista de enlaces vacía
        if (linksList.size() > 0) {

            //Se recorren los hijos de 'section'
            for (int l = 0; l < linksList.size(); l++) {
                XmlLink oLink = new XmlLink();
                //Se obtiene el prelemento 'section'
                Element link = (Element) linksList.get(l);

                if( link.getAttributeValue( Constants.XML_ATT_CONSULT ).trim().equals( Constants.XML_ATT_CONSULT_YES ) ) {
                    //Se obtiene el valor de los atributos de cada tag 'section'
                    oLink.setConsult( link.getAttributeValue( Constants.XML_ATT_CONSULT ).trim() );
                    oLink.setId( Integer.parseInt( link.getAttributeValue( Constants.XML_ATT_ID ).trim() ) );
                    oLink.setUrl( common.replaceUnicodeToSpecialCharacters( link.getTextTrim() ) );

                    linkList.add(oLink);
                }
            }//end for (int l = 0; l < linksList.size(); l++)

            //Se eliminan las 'link' repetidas
            removeRepetedLinks(linkList);

        }//end if (linksList.size() > 0)

    }


}
