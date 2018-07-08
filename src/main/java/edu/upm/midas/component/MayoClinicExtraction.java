package edu.upm.midas.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.upm.midas.common.util.Common;
import edu.upm.midas.common.util.TimeProvider;
import edu.upm.midas.constants.Constants;
import edu.upm.midas.enums.StatusHttpEnum;
import edu.upm.midas.model.document_structure.*;
import edu.upm.midas.model.document_structure.text.List_;
import edu.upm.midas.model.document_structure.text.Paragraph;
import edu.upm.midas.model.document_structure.text.Table;
import edu.upm.midas.model.document_structure.text.Text;
import edu.upm.midas.model.xml.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by gerardo on 03/07/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project mayoclinic
 * @className MayoClinicExtraction
 * @see
 */
@Component
public class MayoClinicExtraction {

    private static final Logger logger = LoggerFactory.getLogger(MayoClinicExtraction.class);

    @Autowired
    private LoadSource loadSource;
    @Autowired
    private ConnectDocument connectDocument;

    @Autowired
    private Common common;
    @Autowired
    private TimeProvider timeProvider;

    private XmlSource sourceConf;
    private String mainSourceURL;
    private String mainDiseaseListsURL;
    private List<MenuItem> menuItemList;


    public void ini() {
            this.sourceConf = getSourceConfiguration();
            this.mainSourceURL = this.sourceConf.getLink().trim();
            this.mainDiseaseListsURL = getPrincipalDiseaseListURL(this.sourceConf.getLinkList());
            this.menuItemList = createMenuItemList();
            if (this.sourceConf == null || common.isEmpty(this.mainSourceURL) || common.isEmpty(this.mainDiseaseListsURL)) {
                logger.error("Error assigning to mainSourceURL and mainDiseaseListsURL {}",
                        (mainSourceURL + " - " + mainDiseaseListsURL));
            }


    }

    public XmlSource getSourceConf() {
        return sourceConf;
    }

    public void setSourceConf(XmlSource sourceConf) {
        this.sourceConf = sourceConf;
    }

    public String getMainSourceURL() {
        return mainSourceURL;
    }

    public void setMainSourceURL(String mainSourceURL) {
        this.mainSourceURL = mainSourceURL;
    }

    public String getMainDiseaseListsURL() {
        return mainDiseaseListsURL;
    }

    public void setMainDiseaseListsURL(String mainDiseaseListsURL) {
        this.mainDiseaseListsURL = mainDiseaseListsURL;
    }

    /**
     * Método para extraer información de una página Web de Wikipedia (documento)
     * Datos a estraer: 1) códigos del infobox, 2) secciones establecidas que hablan
     * de los signos o síntomas de una enfermedad y 3) sus enlaces (url)
     *
     * Lee las clases que obtuvieron información del archivo de configuración XML.
     *
     * Almacenará toda la información extraida desde wikipedia en una estructura de clases
     * hecha a medida de las páginas Web de wikipedia.
     *
     * Dicha estructura posteriormente se leerá para dar paso a insertar la información almacenada
     * en la base de datos genérica para diferentes tipos de fuentes "Sources".
     *
     * @return lista de fuentes de información "Source". Para ser insertados en la BD.
     * @throws Exception
     */
    public List<Source> extract(String snapshot) throws Exception {
        //<editor-fold desc="VARIABLES DE INICO">
        Connection_ connection_;
        Document document;

        Source source;
        Doc doc;
        Disease disease;
        Section section;
        Paragraph paragraph;
        List_ list_ = null;
        Table table;
        Link url;

        List<Source> sourceList;
        List<Doc> docList;
        List<Section> sectionList = new ArrayList<>();
        List<Text> textList;

        int countDoc;
        int countSections;
        int countText;

        Boolean isSection;
        Boolean isText;

        //String version = date.getNowFormatyyyyMMdd();
        //</editor-fold>
        System.out.println("Preparing model...");
        System.out.println("Reading data source configuration...");
        ini();
        // --------- Inicio del proceso de extracción
        // Se inicializa la lista de "Source"
        sourceList = new ArrayList<>();
        // Se leen y recorren los "Sources": wikipedia, medline, etc. NOTA. Aquí solo se leen páginas de wikipedia.
        //<editor-fold desc="LEER ARCHIVO DE CONFIGURACION">
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        System.out.print( gson.toJson(sourceConf) );
        // VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE WIKIPEDIA
        if (sourceConf.getName().equals(Constants.SOURCE_MAYOCLINIC)) {
            //<editor-fold desc="FUENTE">
            // Se crea el enlace de la fuente
            url = new Link(sourceConf.getId(), sourceConf.getLink());
            // Se crea la fuente "Source"
            source = new Source(sourceConf.getId(), sourceConf.getCode(), sourceConf.getName(), url);
            //</editor-fold>
            System.out.println("-----------------------------------------");
            System.out.println("Connecting to... " + sourceConf.getName() + ": " + sourceConf.getLink());
            System.out.println("-----------------------------------------");
            //<editor-fold desc="LISTA DE SECCIONES">
            System.out.println("List of sections...");
            // Se leen las secciones del XML y se enlaza la lista de secciones a la fuente "Source"
            source.setSectionMap(getSections());
            //</editor-fold>
            //<editor-fold desc="LISTA DE ITEMS A MINAR">
            System.out.println("List of menu items...");//Symptoms & causes... etc
            for (MenuItem menuItem:menuItemList) {System.out.println(menuItem.getName());}
            //</editor-fold>
            //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            //<editor-fold desc="PROCESO PARA EXTRACCIÓN DE TEXTOS">
            System.out.println("Procesing links and extracting...");
            //Obtiene todos los articulos que hablan de enfermedades en MayoClinic, asociados
            //con sus enfermedades y sus enlaces
            List<Doc> documentList = createDocumentList(snapshot);
            //Asignar documentos al source
            source.setDocuments(documentList);
            //<editor-fold desc="RECORRIDO DE TODAS LA ENFERMEDADES">
            System.out.println("---------------------------------------------------");
            System.out.println("START Procesing and extracting articles texts...");
            System.out.println("---------------------------------------------------");
            int countDisnetDoc = 1;
            for (Doc disnetDocument: source.getDocuments()){
                textExtract(disnetDocument);
                countDisnetDoc++;
            }
            System.out.println("---------------------------------------------------");
            System.out.println("END Procesing and extracting articles texts...");
            System.out.println("---------------------------------------------------");
            //</editor-fold>
            //</editor-fold>
            //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            // Se crea una lista de documentos "Doc". Doc: es una documento de wikipedia
            docList = new ArrayList<>();
            // Se inicializa un contador para todos los documentos
            countDoc = 1;
            //<editor-fold desc="LISTA DE ENLACES A ENFERMEDADES">
            //</editor-fold>

        }//--VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE WIKIPEDIA
        System.out.println("End to document_structure...");

        //</editor-fold>
        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return sourceList;
    }


    public void textExtract(Doc diseaseArticle) throws Exception {
        Document document;
        // Se conecta con cada documento que tiene una lista de enfermedades
        Connection_ connection_ = connectDocument.connect(diseaseArticle.getUrl().getUrl());
        // Se verifica si hubo conexión con el documento (enlace Web)
        // Se pinta en pantalla el status OK (esta disponible el enlace)
        System.out.println(diseaseArticle.getId() + ". mayoclinicDiseaseExtract " + connection_.getLink() + " ==> " + connection_.getStatus() + "("+connection_.getStatusCode()+")");
        //<editor-fold desc="SI SE HA CONECTADO CON EL DOCUMENTO EXITOSAMENTE">
        if (connection_.getStatus().equals(StatusHttpEnum.OK.getDescripcion()) && connection_.getDocument() != null) {
            document = connection_.getDocument();
            createDocumentLinkList(document);
        }else {//end if oConnect.connection_().equals("OK")
            // Mensaje mostrado al documento que no se pudo conectar
            System.out.println(connection_.getLink() + " ==> " + connection_.getStatus());
        }//end else if oConnect.connection_().equals("OK")
        //</editor-fold>


    }


    public List<Link> createDocumentLinkList(Document document){
        List<Link> linkList = new ArrayList<>();
            // Se obtiene de la configuración el ID del elemento HTML que almacena los enlaces relevantes del documento
            String mainContentElementClass = getHighlightXmlByDescription(Constants.XML_HL_TABLE_OF_CONTENTS, sourceConf).getClass_();
            Elements tableOfContentsElements = document.getElementsByClass(mainContentElementClass);
            int counLink = 1;
            for (Element item: tableOfContentsElements) {
                for (Element link:item.getElementsByTag(Constants.HTML_A)) {
//                System.out.println("QUE PASA: "+item.tagName() + " " + item.getElementsByTag("a"));
                    System.out.println(link.ownText().trim());
                    Link url = new Link(counLink, createCompleteURL(link.attr(Constants.HTML_HREF).trim()) );
                    linkList.add(url);
                    System.out.println(url);
                    counLink++;
                }
            }
        return linkList;
    }


    /**
     * @param tagName
     * @return
     */
    public boolean isElementIsATypeOfList(String tagName){
        boolean res = false;
        for (String listElement: Constants.HTML_LIST_TAG_PARENTS_LIST_TAG){
            res = tagName.equals(listElement);
            if(res) break;
        }
        return res;
    }


    /**
     * @param snapshot
     * @return
     * @throws Exception
     */
    public List<Doc> extractDiseaseList(String snapshot) throws Exception {
        List<Doc> documentList = new ArrayList<>();
        // Se leen y recorren los "Sources": wikipedia, medline, etc. NOTA. Aquí solo se leen páginas de wikipedia.
        //<editor-fold desc="LEER ARCHIVO DE CONFIGURACION">
//        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//        System.out.print( gson.toJson(loadSource.loadSources()) );
        for (XmlSource xmlSource : loadSource.loadSources()) {
            // VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE WIKIPEDIA
            if (xmlSource.getName().equals(Constants.SOURCE_MAYOCLINIC)) {
                //<editor-fold desc="LISTA DE LAS LISTAS DE ENFERMEDADES">
                //Se obtiene las listas generales de enfermedades en orden alfabético
                System.out.println("Getting list of the disease list by alphabetic order... (first list)");
                List<String> listOfTheDiseaseList = getListOfDiseaseListFromPrincipalLink();
                //</editor-fold>
                //<editor-fold desc="LISTA DE TODAS LAS ENFERMEDADES">
                //Se obtienen los enlaces de todas las enfermedades, recorriendo las listas de enfermedades
                documentList = createDocumentList(snapshot);
                System.out.println("(" + documentList.size() + ") MayoClinic disease articles retrieved");
                //</editor-fold>
                //<editor-fold desc="PROCESO PARA EXTRACCIÓN DE TEXTOS">
                System.out.println("Procesing links and extracting...");
                //</editor-fold>
            }//--VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE WIKIPEDIA
            System.out.println("End to document_structure...");
        }
        //</editor-fold>
        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return documentList;
    }


    /**
     * @param snapshot
     * @return
     * @throws Exception
     */
    public List<Doc> createDocumentList(String snapshot) throws Exception {//Second disease list
        List<Doc> documentList = new ArrayList<>();

        Connection_ connection_;
        Document document;

        //<editor-fold desc="CARGA DE LA CONFIGURACIÓN DE LA FUENTE Y LAS LISTAS DE ENFERMEDADES">
        List<String> listOfTheDiseaseList = getListOfDiseaseListFromPrincipalLink();
        //</editor-fold>

        //Recorre las listas generales de enfermedades
        int countUniqueDoc = 1, countDoc = 1, countList = 1;
        for (String diseaseListLink: listOfTheDiseaseList) {
//            System.out.println(mainLink + diseaseListLink);
            //Se forma la url de las listas de enfermedades
            String completeLink = createCompleteURL(diseaseListLink);
            // Se conecta con cada documento que tiene una lista de enfermedades
            connection_ = connectDocument.connect(completeLink);
            // Se verifica si hubo conexión con el documento (enlace Web)
            // Se pinta en pantalla el status OK (esta disponible el enlace)
            System.out.println(countList + ". mayoclinicExtract " + completeLink + " ==> " + connection_.getStatus() + "("+connection_.getStatusCode()+")");
            //<editor-fold desc="SI SE HA CONECTADO CON EL DOCUMENTO EXITOSAMENTE">
            if (connection_.getStatus().equals(StatusHttpEnum.OK.getDescripcion()) && connection_.getDocument() != null) {
                //Variables para controlar la url de la enfermedad y su nombre
                String diseaseUrl = "";
                String diseaseName = "";
                String diseaseSynonym = "";
                Synonym synonym = null;
                // Se obtiene el documento HTML
                document = connection_.getDocument();
                // Se obtiene de la configuración el ID del elemento HTML que almacena el nombre de la enfermedad
                String mainContentElementId = getHighlightXmlByDescription(Constants.XML_HL_MAIN, sourceConf).getId();
//                System.out.println("A consultar: "+mainContentElementId);
                //Obtiene el div que contiene la lista de lista de enfermedades en orden alfabético
                Element mainContent = document.getElementById(mainContentElementId);
                //Obtiene de la configuracióin el ID div que contiene la lista de enfermedades particulares del documento
                String indexContentElementId = getHighlightXmlByDescription(Constants.XML_HL_DISEASE_SUB_LIST, sourceConf).getId();
                //Obtiene el contenido el div por medio de su ID
                Element indexContent = document.getElementById(indexContentElementId);
                //Obtiene los elementos <li>
                org.jsoup.select.Elements liList = indexContent.getElementsByTag(Constants.HTML_LI);
                //<editor-fold desc="RECORRIDO DE LOS ENLACES A ENFERMEDADES">
                for (Element li: liList) {//solo da una vuela porque solo hay un elemento
                    //Obtenemos el nombre de la enfermedad
                    //Muchas veces redirecciona a otra enfermedad y por lo tanto este dato lo consideraremos como
                    //el un sinónimo de la enfermedad
                    diseaseSynonym = li.ownText().replace(Constants.MAYOCLINIC_DISEASE_NAME_ELEMENT_DELETE, Constants.BLANKS).trim();
//                    System.out.println("Disease name: " + diseaseSynonym);
                    //Si diseaseSynonym contiene información se crea un sinónimo
                    if (diseaseSynonym!="") synonym = new Synonym(diseaseSynonym);
                    //Obtiene los elementos <a> "url"
                    org.jsoup.select.Elements linkList = li.getElementsByTag(Constants.HTML_A);
                    for (Element link: linkList) {
                        //Se obtiene la url del documento
                        diseaseUrl = createCompleteURL(link.attr(Constants.HTML_HREF).trim());
                        //Se obtiene el nombre de la enfermedad contenido en el texto de la etiqueta <a>
                        // Si hay parentesis el nombre estará dentro de ellos y el resto se considerará como un sinónimo
                        //Abscess, Bartholin's (See: Bartholin's cyst)
                        diseaseName = link.text().trim();
//                        System.out.println("diseaseName: "+diseaseName);
                        break;//Para que solo se obtenga el primer elemento
                    }
                    //Verifica si existe un documento que hable de una enfermedad
                    //Si existe agregará el nombre como sinónimo y no creará un nuevo documento
                    Doc existDocument = getDocumentIfInsideTheDocumentList(documentList, diseaseName);
//                    System.out.println("existDocument:" + existDocument);
                    if (existDocument==null) {
                        //Se crea el link principal del documento
                        //Los documentos en MayoClinic tiene dos enlaces relevantes, le principal donde se encuentra
                        //la información de los síntomas y causas y el otro enlace que tiene información del diagnóstico
                        Link diseaseLink = new Link(diseaseUrl, diseaseName);
                        //Se crea un Documento
                        Doc disnetDocument = new Doc(countUniqueDoc, snapshot, diseaseLink, true, true);
                        //Se crea la enfermedad de la que habla el documento
                        Disease disease = new Disease(countUniqueDoc, diseaseName);
                        //Relaciona el sinónimo con la enfermedad
                        if (synonym != null) {
                            List<Synonym> synonymList = new ArrayList<>();
                            synonymList.add(synonym);
                            disease.setSynonyms(getSynonymList(synonym));
                        }
                        //Se relaciona el documento con su enfermedad
                        disnetDocument.setDisease(disease);
                        //Agrega documento a la lista de documentos
                        documentList.add(disnetDocument);
                        System.out.println(countUniqueDoc + ". disease: " + disease.getName() + " (" + disnetDocument.getUrl().getUrl() + ")");
                        countUniqueDoc++;
                    }else{//Si existe no se creará otra enfermedad y los sinónimos se agregarán a la
                        //Agrega sinónimo si existe
                        if (synonym != null) existDocument.getDisease().getSynonyms().add(synonym);
                    }
                    countDoc++;
                }
                countList++;
                //</editor-fold>
            } else {//end if oConnect.connection_().equals("OK")
                // Mensaje mostrado al documento que no se pudo conectar
                System.out.println(completeLink + " ==> " + connection_.getStatus());
            }//end else if oConnect.connection_().equals("OK")
            //</editor-fold>


        }
        System.out.println(documentList.size() + " unique disease articles found in MayoClinic to " + countDoc);
        return documentList;
    }


    /**
     * Método que retorna los enlaces Web de las listas de enfermedades generales
     * Se conecta a la página Web donde se encuentran las listas de enfermedades
     *
     * La lista se encuentra partida en cuantas letras tiene el abecesario inglés, por lo que
     * obtiene el enlace de cada una de las secciones del abecedario para despues entrar a cada una
     * de las páginas Web y recuperar la lista de enlaces a artículos MayoClinic
     *
     * @return
     * @throws Exception
     */
    public List<String> getListOfDiseaseListFromPrincipalLink() throws Exception {
        List<String> listOfTheDiseaseList = new ArrayList<>();

        Connection_ connection_;
        Document document;

        //Se obtiene las listas generales de enfermedades en orden alfabético
        System.out.println("Getting list of the disease list by alphabetic order... (first list)");
        //<editor-fold desc="SE OBTIENE EL ENLACE PRINCIPAL">
        if (mainDiseaseListsURL!=null) {
            // Se conecta con el documento que tiene la lista de enfermedades (DOCUMENTO PRINCIPAL)
            // en orden alfabetico
            connection_ = connectDocument.connect( mainDiseaseListsURL );
            // Se verifica si hubo conexión con el documento (enlace Web)
            // Se pinta en pantalla el status OK (esta disponible el enlace)
            System.out.println("mayoclinicExtract (MainPage) " + connection_.getLink() + " ==> " + connection_.getStatus() + "("+connection_.getStatusCode()+")");
            //<editor-fold desc="SI SE HA CONECTADO CON EL DOCUMENTO EXITOSAMENTE">
            if (connection_.getStatus().equals(StatusHttpEnum.OK.getDescripcion()) && connection_.getDocument() != null) {
                // Se obtiene el documento HTML
                document = connection_.getDocument();
                // Se obtiene de la configuración el ID del elemento HTML que almacena el nombre de la enfermedad
                String mainContentElementId = getHighlightXmlByDescription(Constants.XML_HL_MAIN, sourceConf).getId();
//                System.out.println("A consultar: "+mainContentElementId);
                //Obtiene el div que contiene la lista de lista de enfermedades en orden alfabético
                Element mainContent = document.getElementById(mainContentElementId);
                //Se obtiene de la configuración la clase de la lista que contiene la lista de enfermedades
                String class_ = getHighlightXmlByDescription(Constants.XML_HL_DISEASE_LIST + "", sourceConf).getClass_();
                //Se extraen los elementos de la lista "li" segun la clase
                org.jsoup.select.Elements alphabeticList = mainContent.getElementsByClass(class_);
                //Como sabemos que solo hay un elemento, cogemos el primero de la lista (el único)
                Element linkListContent = alphabeticList.get(0);
                org.jsoup.select.Elements linkList = linkListContent.getElementsByTag(Constants.HTML_A);
//                System.out.println(alphabeticList.toString());
                for (Element link: linkList) {
//                    System.out.println(link.attr(Constants.HTML_HREF));
                    listOfTheDiseaseList.add(link.attr(Constants.HTML_HREF).trim());
                }
            } else {//end if oConnect.connection_().equals("OK")
                // Mensaje mostrado al documento que no se pudo conectar
                System.out.println(connection_.getLink() + " ==> " + connection_.getStatus());
            }//end else if oConnect.connection_().equals("OK")
            //</editor-fold>
        }
        //</editor-fold>

        System.out.println(listOfTheDiseaseList.size() + " (elements) lists of diseases recovered...");
        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return listOfTheDiseaseList;
    }


    /**
     * Método que consulta los elementos relevantes (Highlight) en el XML según su descripción
     *
     * Con el fin de hacer consultas JSOUP más complejas valiendose de elementos importantes del documento
     * como lo puede ser la clase de los infobox o clases de lista que contienen códigos interesantes
     *
     * Ej, de descripción: diseasename, infobox, externalresource... ver etiqueta highlight de sources.xml
     *
     * @param description
     * @param xmlSource
     * @return objeto XmlHighlight
     */
    public XmlHighlight getHighlightXmlByDescription(String description, XmlSource xmlSource){
        XmlHighlight xmlHighlight = null;
        for (XmlHighlight oHighlight:
                xmlSource.getHighlightList()) {
            if( oHighlight.getDescription().equals(description) ){
                xmlHighlight = oHighlight;
                return xmlHighlight;
            }
        }
        return xmlHighlight;
    }


    /**
     * @param synonym
     * @return
     */
    public List<Synonym> getSynonymList(Synonym synonym){
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(synonym);
        return synonymList;
    }


    /**
     * @param documentList
     * @param diseaseName
     * @return
     */
    public boolean isTheDocumentInTheDocumentList(final List<Doc> documentList, String diseaseName){
        return documentList.stream()
                .filter(o -> o.getDisease().getName().trim() != null)
                .filter(o -> o.getDisease().getName().trim().contentEquals(diseaseName.trim()))
                .findFirst()
                .isPresent();
    }


    /**
     * @param documentList
     * @param diseaseName
     * @return
     */
    public Doc getDocumentIfInsideTheDocumentList(final List<Doc> documentList, String diseaseName){
        return documentList.stream()
                .filter(o -> o.getDisease().getName().trim() != null)
                .filter(o -> o.getDisease().getName().trim().contentEquals(diseaseName.trim()))
                .findAny()
                .orElse(null);
    }


    /**
     * @return
     */
    public TreeMap<String, String> getSections(){
        TreeMap<String, String> sectionMap = new TreeMap<>();
        for (XmlSection xmlSection : sourceConf.getSectionList()) {
            // Se almacenan las secciones en el Map
            sectionMap.put(xmlSection.getId().trim(), xmlSection.getName().trim());
            System.out.println(xmlSection.getName());
        }
        return sectionMap;
    }


    /**
     * @param xmlSections
     * @return
     */
    public List<Section> getSections(List<XmlSection> xmlSections){
        List<Section> sectionList = new ArrayList<>();
        for (XmlSection xmlSection: xmlSections) {
            Section section = new Section(Integer.parseInt(xmlSection.getId()), xmlSection.getName());
            sectionList.add(section);
        }
        return sectionList;
    }


    /**
     * @return
     */
    public List<MenuItem> createMenuItemList(){
        List<MenuItem> menuItemList = new ArrayList<>();
        int count = 1;
        for (XmlMenuItem xmlMenuItem: sourceConf.getXmlMenuItemList()){
//            System.out.println(xmlMenuItem.getName());
            MenuItem menuItem = new MenuItem(count, xmlMenuItem.getName(), getSections(xmlMenuItem.getSectionList()));
            menuItemList.add(menuItem);
//            System.out.println(menuItem);
            count++;
        }
        return menuItemList;
    }

    /**
     * @param linkList
     * @return
     */
    public String getPrincipalDiseaseListURL(List<XmlLink> linkList){
        String principalLink = "";
        for (XmlLink xmlLink : linkList) {
            principalLink = xmlLink.getUrl();
        }
        return principalLink;
    }


    /**
     * @param url
     * @return
     * @throws Exception
     */
    public String createCompleteURL(String url) {
        return mainSourceURL + url;
    }


    /**
     * @return
     * @throws Exception
     */
    public XmlSource getSourceConfiguration() {
        XmlSource source = null;
        try {
            for (XmlSource xmlSource : loadSource.loadSources()) {
                try {
                    source = xmlSource;
                    break;
                } catch (Exception e) {
                    logger.error("Error obtaining the source configuration {}", xmlSource, e);
                }
            }
        }catch (Exception e){
            logger.error("Error getting source configuration", e);
        }
        return source;
    }
}
