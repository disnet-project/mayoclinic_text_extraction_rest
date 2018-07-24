package edu.ctb.upm.midas.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ctb.upm.midas.common.util.Common;
import edu.ctb.upm.midas.constants.Constants;
import edu.ctb.upm.midas.common.util.Common;
import edu.ctb.upm.midas.constants.Constants;
import edu.ctb.upm.midas.enums.StatusHttpEnum;
import edu.ctb.upm.midas.model.document_structure.*;
import edu.ctb.upm.midas.model.document_structure.text.List_;
import edu.ctb.upm.midas.model.document_structure.text.Paragraph;
import edu.ctb.upm.midas.model.document_structure.text.Text;
import edu.ctb.upm.midas.model.xml.*;
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


    //<editor-fold desc="VARIABLES LOCALES">
    private XmlSource sourceConf;
    private String mainSourceURL;
    private String mainDiseaseListsURL;
    private List<MenuItem> menuItemList;
    //</editor-fold>


    /**
     * Método que inicializa las variables locales de la clase
     *
     * Carga la configuración de extracción para MayoClinic
     *
     */
    public void ini() {
        System.out.println("Start ini() <<XML configuration source>>");
        //Carga
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
     * Método para extraer información médica de MayoClinic.
     *
     * Primero, extrae una lista (lista general) de los enlaces de cada una de las listas de enfermedades.
     *
     * Segundo, extrae de cada uno de los elementos de la lista general, los enlaces de cada enfermedad, formando
     * la lista principal de enfermedades.
     *
     * Tercero, se conecta a cada elemento de la lista principal de enfermedades y obtiene dos enlaces de la tabla
     * de contenidos del documento relativo a una enfermedad, estos dos enlaces, uno habla de los síntomas de la
     * enfermedad y sus causas y el otro enlace habla del diagnóstico de la enfermedad.
     *
     * Cuarto, cada documento de enfermedad por lo tanto tiene dos URLs para minar.
     *
     * La información que se recupera de cada documento son solo textos (párrafos y listas).
     *
     * No hay códigos de enfermedad.
     *
     * @return lista de fuentes de información "Source". Para ser insertados en la BD.
     * @throws Exception
     *              Puede lanzar una excepción
     *
     * @see
     *              createDocumentList
     *              retrieveMedicalKnowledge
     *
     */
    public List<Source> extract(String snapshot) throws Exception {
        //<editor-fold desc="VARIABLES DE INICO">
        Source source;
        Link url;
        List<Source> sourceList = new ArrayList<>();
        //String snapshot = date.getNowFormatyyyyMMdd();
        //</editor-fold>
        System.out.println("Preparing model...");
        System.out.println("Reading data source configuration...");
        ini();
        // --------- Inicio del proceso de extracción
        // Se leen y recorren los "Sources": mayoclinic, wikipedia, medline, etc. NOTA. Aquí solo se leen páginas de mayoclinic.
        //<editor-fold desc="LEER ARCHIVO DE CONFIGURACION">
//        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//        System.out.print( gson.toJson(sourceConf) );
        // VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE MAYOCLINIC
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
            for (MenuItem menuItem: menuItemList) {System.out.println(menuItem.getName());}
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
            for (Doc disnetDocument: source.getDocuments()){
                retrieveMedicalKnowledge(disnetDocument, source.getDocuments().size());
            }
            System.out.println("---------------------------------------------------");
            System.out.println("END Procesing and extracting articles texts...");
            System.out.println("---------------------------------------------------");
            sourceList.add(source);
            //</editor-fold>
            //</editor-fold>
            //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        }//--VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE MAYOCLINIC
        System.out.println("End to document_structure...");
//        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//        System.out.print(gson.toJson(sourceList));
        //</editor-fold>
        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return sourceList;
    }


    /**
     * Método que crea documentos DISNET y los relaciona con enfermedades y ellas con sus sinónimos.
     *
     * A partir de las diferentes listas de enfermedades (nombradas por cada letra del alfabeto inglés), básicamente
     * una lista que contiene otras listas (lista general).
     *
     * Este método se encarga de conectarse a cada elemento de la lista general y recuperar la URL y el nombre
     * de una enfermedad (o documento de enfermedad).
     *
     * @param snapshot
     *              Recibe la fecha de la snapshot
     * @return  Retorna la lista de documentos DISNET relacionados con su enfermedad
     * @throws Exception
     */
    public List<Doc> createDocumentList(String snapshot) throws Exception {//Second disease list
        List<Doc> documentList = new ArrayList<>();

        Connection_ connection_;
        Document document;

        //<editor-fold desc="CARGA LAS LISTAS DE ENFERMEDADES">
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
            System.out.println(countList + " to " + ". mayoclinicExtract " + completeLink + " ==> " + connection_.getStatus() + "("+connection_.getStatusCode()+")");
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
                    //<editor-fold desc="PROCESO PARA OBTENER UN SINONIMO DE LA ENFERMEDAD">
                    //Muchas veces redirecciona a otra enfermedad y por lo tanto este dato lo consideraremos como
                    //el un sinónimo de la enfermedad
                    diseaseSynonym = li.ownText().replace(Constants.MAYOCLINIC_DISEASE_NAME_ELEMENT_DELETE, Constants.BLANKS).trim();
//                    System.out.println("Disease name: " + diseaseSynonym);
                    //Si diseaseSynonym contiene información se crea un sinónimo
                    if (!common.isEmpty(diseaseSynonym)) synonym = new Synonym(diseaseSynonym);
                    //</editor-fold>
                    //<editor-fold desc="PROCESO DE OBTENCIÓN DE LA URL PRINCIPAL DEL DOCUMENTO">
                    //Obtiene los elementos <a> "url"
                    org.jsoup.select.Elements linkList = li.getElementsByTag(Constants.HTML_A);
                    for (Element link: linkList) {
                        //Se obtiene la url del documento
                        diseaseUrl = createCompleteURL(link.attr(Constants.HTML_HREF).trim());
                        //Se obtiene el nombre de la enfermedad contenido en el texto de la etiqueta <a>
                        // Si hay parentesis el nombre estará dentro de ellos y el resto se considerará como un sinónimo
                        //Obtenemos el nombre de la enfermedad
                        //Abscess, Bartholin's (See: Bartholin's cyst)
                        diseaseName = link.text().trim();
//                        System.out.println("diseaseName: "+diseaseName);
                        break;//Para que solo se obtenga el primer elemento
                    }
                    //</editor-fold>
                    //<editor-fold desc="PROCESO DE CREACIÓN DE UN DOCUMENTO">
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
                            synonym.setId(1);
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
                        if (synonym != null){
                            if (existDocument.getDisease().getSynonyms()==null){
                                existDocument.getDisease().setSynonyms(new ArrayList<>());
                                synonym.setId(1);
                                existDocument.getDisease().getSynonyms().add(synonym);
                            }else {
                                int id = existDocument.getDisease().getSynonyms().size() + 1;
                                synonym.setId(id);
                                existDocument.getDisease().getSynonyms().add(synonym);
                            }
                        }
                    }
                    //</editor-fold>
                    countDoc++;
                }
                countList++;
//                if (countList==1) break;
                //</editor-fold>
            } else {//end if oConnect.connection_().equals("OK")
                // Mensaje mostrado al documento que no se pudo conectar
                System.out.println(completeLink + " ==> " + connection_.getStatus());
            }//end else if oConnect.connection_().equals("OK")
            //</editor-fold>
        }

        //<editor-fold desc="PROCESO PARA RECUPERAR LOS ENLACES RELEVANTES DEL DOCUMENTO">
        getAllRelevantLinksFromTheDocument(documentList);
        //</editor-fold>

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
//                    break;
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
     * Método que busca en un artículo de enfermedad los enlaces de los item relevantes de la tabla de contenidos.
     *
     * Cada tiene secciones y textos. En la configuración base se buscan los enlaces de los item:
     * "Symptoms & causes" y "Diagnosis & treatment".
     *
     * Cada artículo MayoClinic al menos debe tener alguno de estos dos items pero no todos tienen secciones
     * relevantes ni textos relevantes.
     *
     * @param disnetDocuments
     * @throws Exception
     */
    public void getAllRelevantLinksFromTheDocument(List<Doc> disnetDocuments) throws Exception {
        for (Doc disnetDocument: disnetDocuments){
            Document document;
            // Se conecta con cada documento que tiene una lista de enfermedades
            Connection_ connection_ = connectDocument.connect(disnetDocument.getUrl().getUrl());
            // Se verifica si hubo conexión con el documento (enlace Web)
            // Se pinta en pantalla el status OK (esta disponible el enlace)
            System.out.println(disnetDocument.getId() + ". mayoclinicLinksDiseaseExtract " + connection_.getLink() + " ==> " + connection_.getStatus() + "("+connection_.getStatusCode()+")");
            //<editor-fold desc="SI SE HA CONECTADO CON EL DOCUMENTO EXITOSAMENTE">
            if (connection_.getStatus().equals(StatusHttpEnum.OK.getDescripcion()) && connection_.getDocument() != null) {
                document = connection_.getDocument();
                disnetDocument.setUrlList( getDocumentLinkList(document, disnetDocument) );
//                break;
            }else {//end if oConnect.connection_().equals("OK")
                // Mensaje mostrado al documento que no se pudo conectar
                System.out.println(connection_.getLink() + " ==> " + connection_.getStatus());
            }//end else if oConnect.connection_().equals("OK")
            //</editor-fold>
        }
    }


    /**
     * Método que según un documento extrae los enlaces de los elementos relevantes de la tabla de contenido.
     *
     * Para esta primera versión nos interesan los enlaces de los item "Symptoms & causes" y "Diagnosis & treatment"
     *
     * @param document
     * @return
     */
    public List<Link> getDocumentLinkList(Document document, Doc disnetDocument){
        List<Link> linkList = new ArrayList<>();
        // Se obtiene del xml de configuración el class del elemento HTML que almacena los enlaces relevantes del documento
        String mainContentElementClass = getHighlightXmlByDescription(Constants.XML_HL_TABLE_OF_CONTENTS, sourceConf).getClass_();
        //Obtiene los elementos hijos segunla clase
        Elements tableOfContentsElements = document.getElementsByClass(mainContentElementClass);
        int counLink = 1;
        //Se recorren los hijos
        for (Element item: tableOfContentsElements) {
            //Por cada hijo se obtiene su elemento <a>
            for (Element link:item.getElementsByTag(Constants.HTML_A)) {
//                System.out.println("QUE PASA: "+item.tagName() + " " + item.getElementsByTag("a"));
                if (isRelevantMenuItem(link.ownText().trim(), false, disnetDocument)) {
//                        System.out.println(link.ownText().trim());
                    Link url = new Link(counLink, createCompleteURL(link.attr(Constants.HTML_HREF).trim()), link.ownText().trim());
                    linkList.add(url);
//                    System.out.println(url);
                    counLink++;
                }
            }
        }
        return linkList;
    }


    /**
     * Método que por cada documento DISNET de la lista lee y crea documentos JSOUP "DOM" a partir de las URLs
     * (items de la tabla de contenido del documento).
     *
     * Obtiene las secciones pertenecientes a cada uno de los item (que es un documento DOM) de la tabla de contenidos.
     *
     * Por ejemplo, el item "Symptoms & causes" tiene las secciones relevantes "Symptoms" y "Causes".
     *
     * @param disnetDocument
     *              Recibe la lista de documentos DISNET
     * @throws Exception
     *              Puede lanzar una excepción
     * @see
     *              getSectionsListByMenuItemName
     *              extractDocumentContent
     */
    public void retrieveMedicalKnowledge(Doc disnetDocument, int size) throws Exception {
        List<Section> sections = new ArrayList<>();
        if (disnetDocument.getUrlList()!=null) {
            //Se conectara a los dos enlaces del documento donde hay secciones y textos
            //<editor-fold desc="RECORRIDO DE LOS ENLACES DEL DOCUMENTO PARA EXTRAER DE SUS SECCIONES Y TEXTOS">
            for (Link documentLink : disnetDocument.getUrlList()) {
                //Se recuperan las secciones según el item de menu que se este consultando
                List<Section> sectionSearchList = getSectionsListByMenuItemName(documentLink.getDescription());
//            System.out.println("sections: " + sectionSearchList.toString());
                Document document;
                // Se conecta con cada documento que tiene una lista de enfermedades
                Connection_ connection_ = connectDocument.connect(documentLink.getUrl());
                // Se verifica si hubo conexión con el documento (enlace Web)
                // Se pinta en pantalla el status OK (esta disponible el enlace)
                System.out.println(disnetDocument.getId() + " to " + size + ". mayoclinicDiseaseExtract " + connection_.getLink() + " ==> " + connection_.getStatus() + "(" + connection_.getStatusCode() + ")");
                //<editor-fold desc="SI SE HA CONECTADO CON EL DOCUMENTO EXITOSAMENTE">
                if (connection_.getStatus().equals(StatusHttpEnum.OK.getDescripcion()) && connection_.getDocument() != null) {
                    //Se crea el documento de la página Web
                    document = connection_.getDocument();
                    extractDocumentContent(disnetDocument, document, sectionSearchList);
                } else {//end if oConnect.connection_().equals("OK")
                    // Mensaje mostrado al documento que no se pudo conectar
                    System.out.println(connection_.getLink() + " ==> " + connection_.getStatus());
                }//end else if oConnect.connection_().equals("OK")
                //</editor-fold>
            }
        }
        disnetDocument.updateCounts();
//        if (disnetDocument.getDisease().getName().equalsIgnoreCase("Thalassemia")) {
//            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//            System.out.print(gson.toJson(disnetDocument));
//        }
        //</editor-fold>
    }


    /**
     * Método que a partir de uno de los dos documentos DOM de un documento DISNET extrae el contenido de la etiqueta
     * que contiene toda su información mñedica relevante para nosotros (<article></article>).
     *
     * Busca el elemento "article" y si lo encuentra lo envía para ser procesado.
     *
     * @param disnetDocument
     *              Recibe la lista de documentos DISNET
     * @param document
     *              Recibe el documento JSOUP "DOM" a procesar (extraer sus textos)
     * @param sectionList
     *              Recibe la lista de secciones que tiene un documento (un item de la tabla de contenido)
     * @see
     *              extractTexts
     */
    public void extractDocumentContent(Doc disnetDocument, Document document, List<Section> sectionList){
        //Obtiene la etiqueta "tag" general que contiene las secctiones y textos
        String articleTag = getHighlightXmlByDescription(Constants.XML_HL_BODY_ARTICLE, sourceConf).getType();
        //Obtiene el nombre de la clase content
        String contentClass = getHighlightXmlByDescription(Constants.XML_HL_CONTENT_CLASS, sourceConf).getClass_();
        //Obtiene el primer elemento de la etiqueta <article> y el div con clase "content"
        Element articleContent = document.select(articleTag).select(Constants.HTML_DIV + Constants.DOT + contentClass).first();
        //<editor-fold desc="PROCESO DE EXTRACCIÓN DE TEXTOS">
        extractTexts(disnetDocument, sectionList, articleContent);
        //</editor-fold>
        //Obtener todos los textos de las secciones relevantes
    }


    /**
     * Método que según un documento DOM extrae los textos de las secciones relevantes.
     *
     * Busca dentro del elemento "article" aquellos elementos que son secciones (elementos "h2") y
     * recorre su hermano en busca de párrafos o listas que agregar en la lista de textos.
     *
     * Obtiene el objeto de sección que se está procesando y si se han encontrado textos, los anexa a dicha sección.
     *
     * Se crea una lista de textos vacía para si se encuentran textos se agreguen ahí y después a la sección.
     *
     * Las secciones con textos se agregan al documento DISNET que se recibe (que proviene de la lista de documentos).
     *
     * @param disnetDocument
     *              Recibe documento DISNET
     * @param sectionList
     *              Recibe la lista de secciones de uno de los dos documentos DOM del documento DISNET
     * @param articleElement
     *              Recibe el contenido del elemento que contiene textos referentes a información fenotípica
     *
     * @see
     *              identifyAndAddTypesOfText
     *              initializeSectionListIfNotExist
     */
    public void extractTexts(Doc disnetDocument, List<Section> sectionList, Element articleElement){
        //<editor-fold desc="VERIFICA QUE EL ELEMENTO TENGA CONTENIDO">
        if (articleElement!=null) {
            //Obtiene todos los elementos <h2> del contenido principal
            Elements h2Elements = articleElement.getElementsByTag(Constants.HTML_H2);
            //Se recorren todos los elementos <h2> encontrados si los hay
            if (h2Elements!=null) {
                //<editor-fold desc="PROCESAMIENTO DE LAS SECCIONES Y SUS TEXTOS">
                for (Element h2Element : h2Elements) {
                    //Se obtiene el texto propio de cada elemento <h2>
                    //Obtenemos el texto (nombre de la sección, elemento "h2")
//            System.out.println("h2Element. " + h2Element.ownText());
                    String sectionName = h2Element.ownText().trim();
                    //Se verifica que el nombre de la sección sea relevante
                    if (isRelevantSection(sectionName, sectionList)) {
                        //Obtiene el objeto de la sección que se esta procesando para agregarla al documento
                        Section section = getSection(sectionName, sectionList);
                        //Crea la lista de textos que se agregará a la sección
                        List<Text> textList = new ArrayList<>();
//                        System.out.println("Relevant section: " + section.getName());
//                        System.out.println("Section: " + section);
                        //Obtenemos el hermano de la etiqueta "h2" (de la sección)
                        // y recorremos para buscar parrafos <p> o listas <lu>
                        Element nextElementBro = h2Element.nextElementSibling();
                        int countText = 1;
                        //<editor-fold desc="OBTENER TEXTOS DE PARRAFOS O LISTAS">
                        // Recorrido de los elementos que contienen texto dentro de una sección (<p>, <ul><ol>)
                        // Mientras no sea nulo, y el tag sea diferente <h2> no saldrá del ciclo
                        while (nextElementBro != null && nextElementBro.tagName() !=  getSectionConf(sectionName).getTypeTitle().getName()) {
//                            System.out.println(nextElementBro.tagName() + " == " + nextElementBro.text());
                            countText = identifyAndAddTypesOfText(nextElementBro, textList, countText);
//                            if (isText) countText++;
                            // Obtiene el siguiente hermano del nodo para seguir con el ciclo while
                            nextElementBro = nextElementBro.nextElementSibling();
                        }//end while (nextElementBro != null && nextElementBro.tagName() != "h2")
                        //</editor-fold>
                        //Si se encontró al menos un texto se relaciona la sección con el documento y los textos
                        //con la sección
                        if (countText>0) {
                            //Agrega la lista de textos a la sección
                            section.setTextList(textList);
                            //Crea una lista de secciones y la agrega al documento DISNET y si ya existe solo agrega
                            //la nueva sección
                            initializeSectionListIfNotExist(disnetDocument, section);
//                            disnetDocument.getSectionList().add(section);
                        }
                    }
                }
                //</editor-fold>
            }else{
                logger.info("No items were found (h2 elements size: {})", h2Elements);
            }
        }
        //</editor-fold>
    }


    /**
     * Método que identifica el elemento recibido es un párrafo <p></p> o una lista <ul></ul>, <ol></ol> o <dl></dl>.
     *  Y si es uno de esos elementos se considera un texto y se procede a recuperarlo.
     *
     * Cada que encuentra un texto, aumenta la variable que cuenta el número de textos encontrados y que controla
     * el orden en que se han extraido.
     *
     * Cada texto relevante encontrado se agrega a la lista de textos.
     *
     * @param element
     *              Recibe un elemento que puede ser un texto válido
     * @param textList
     *              Recibe la lista de textos que pertenecerán a la sección
     * @param countText
     *              Recibe el número de texto será su id y el número de orden
     * @return  La cuenta actualizada de textos encontrados y procesados
     * @see
     *              setParagraphData
     *              setList_Data
     *              isElementIsATypeOfList
     *              getTitle
     */
    public int identifyAndAddTypesOfText(Element element, List<Text> textList, int countText){
        if (element!=null) {
            //Variable que indicará en caso de ser true, si un elemento es un texto relevante
            boolean isText = false;
            // Extrae el texto si es una etiqueta <p> (paragraph)
            if (element.tagName().equalsIgnoreCase(Constants.HTML_P)){
                //<editor-fold desc="EXTRAE TEXTO DE UN PARRAFO Y LO ALMACENA EN UN OBJETO PARAGRAPH">
                // Se crea un párrafo y se extrae su información
                Paragraph paragraph = setParagraphData(element, countText, getTitle(element));
                if (paragraph!=null) {//System.out.println("ENTRA_paragraph");
                    // Agrega el párrafo a la lista de textos
                    isText = true;
                    textList.add(paragraph);
//                    System.out.println("CREA PARA: " + paragraph);
                }
                //</editor-fold>
            // Extrae el texto si es una etiqueta <ul> o <ol> o <dl>
            } else if (isElementIsATypeOfList(element.tagName())) {//&& !nextElementBro.text().isEmpty()
                //<editor-fold desc="EXTRAE TEXTO DE UN PARRAFO Y LO ALMACENA EN UN OBJETO LIST_">
                // Guarda la información extraida de una lista mayoclinic en un objeto
                List_ list_ = setList_Data(element, countText, getTitle(element));
                if (list_!=null) {//System.out.println("ENTRA_list_");
                    isText = true;
                    // Agrega la lista "List_" a la lista de textos
                    textList.add(list_);
//                    System.out.println("CREA LIST: " + list_);
                }
                //</editor-fold>
                //Extrae el texto si es una etiqueta <table> y class="wikitable"
            }
            if (isText) countText++;
        }
        return countText;
    }


    /**
     * Método que crea una lista de secciones y le añade la sección recibida.
     *
     * Si la lista de secciones del documento ya cuenta con algún elemento (sección), solo se
     * le agrega la sección recibida.
     *
     * @param disnetDocument
     *              Recibe un documento DISNET
     * @param section
     *              Recibe una sección
     */
    public void initializeSectionListIfNotExist(Doc disnetDocument, Section section){
        List<Section> sections = null;
        if(disnetDocument.getSectionList()==null){
            sections = new ArrayList<>();
            sections.add(section);
            disnetDocument.setSectionList(sections);
        }else{
            disnetDocument.getSectionList().add(section);
        }
    }


    /**
     * Método que obtiene el titulo de un párrado o lista si es que existe.
     *
     * Busca si el elemento anterior al recibido es un <h3></h3> o <h4></h4> y si es así
     * se considera como un titulo y se coloca como propio del elemento recibido.
     *
     * @param element
     *              Recibe elemento DOM
     * @return
     */
    public String getTitle(Element element){
        //<editor-fold desc="TITULO DE PÁRRAFO O LISTA">
        //System.out.println(nextElementBro.tagName() + " == " + nextElementBro.text());
        //Obtener el hermano anterior para ver si tiene titulo el parrafo o la lista
        Element prevElementBro = element.previousElementSibling();
        // Obtiene el titulo del párrafo o nombre del síntoma si existe
        String title = "";
        //Verifica que sean elementos h3 o h4
        if (prevElementBro.tagName() == Constants.HTML_H3 || prevElementBro.tagName() == Constants.HTML_H4) {
            //Si encuentra texto
            if (prevElementBro.text() != null) title = prevElementBro.text();
        }
        //</editor-fold>
        return title;
    }


    /**
     * Método que recupera el texto de un elemento párrafo <p></p>
     *
     * @param element
     *              Recibe elemento DOM
     * @param countText
     *              Recibe la cuenta actual de textos para id y orden de recuperación
     * @param title
     *              Recibe el titulo del texto recuperado exista o no
     * @return objeto Paragraph de tipo Text
     */
    public Paragraph setParagraphData(Element element, int countText, String title){
        Paragraph paragraph = null;
        if (!common.isEmpty(element.text())){
            paragraph = new Paragraph();
            paragraph.setId( countText );
            paragraph.setText( element.text().trim());
            paragraph.setTextOrder( countText );
            paragraph.setTitle(title);
            // Agrega la lista de enlaces al párrafo
            // (INHABILITADO PORQUE EN MAYO CLINIC NO SE ENCUENTRAN ENLACES EN LOS TEXTOS)
//            paragraph.setUrlList( getTextUrls( element ) );
        }

        return paragraph;
    }


    /**
     * Método que recupera el texto de un elemento lista <ul></ul>, <ol></ol> o <dl></dl>.
     *
     * @param element
     *              Recibe elemento DOM
     * @param countText
     *              Recibe la cuenta actual de texto para id y orden de recuperación
     * @param title
     *              Recibe el titulo del texto recuperado exista o no
     * @return objeto List_ de tipo Text
     * @see
     *              Constants.HTML_LIST_TAG_CHILD_LIST_TAG
     */
    public List_ setList_Data(Element element, int countText, String title){
        List_ list_ = null;
        List<String> liList = new ArrayList<>();
        // Recorrido de la lista. Recorre los elementos <li> de un <ul>
//        Elements li = element.select(Constants.HTML_LI); // select all li from ul
        //Actualización para que recorra todos los elementos hijos de ol, ul y dl
        for (String listItem: Constants.HTML_LIST_TAG_CHILD_LIST_TAG){
            Elements childTagList = element.select(listItem);
            for (int i = 0; i < childTagList.size(); i++) {
                // Agrega las filas a la lista de elementos de una lista
                liList.add( childTagList.get(i).text() );
            }
            if (liList.size() > 0){
                list_ = new List_();
                list_.setId( countText );
                list_.setTextOrder( countText );
                list_.setTitle(title);
                // Agrega la lista al objeto lista "List_"
                list_.setBulletList( liList );

                // Agrega la lista de enlaces al objeto lista "List_"
                // (INHABILITADO PORQUE EN MAYO CLINIC NO SE ENCUENTRAN ENLACES EN LOS TEXTOS)
//                list_.setUrlList( getTextUrls( element ) );
            }
        }
        return list_;
    }


    /**
     * Método que verifica según un nombre de una sección si es relevante o no.
     *
     * Se le pasa como parametros el nombre de la sección a validar y la lista de
     * secciones relevantes.
     *
     * @param sectionName
     *              Recibe el nombre de una sección
     * @param sectionList
     *              Recibe la lista de secciones relevantes en la que se buscará el nombre de la sección recibida
     * @return Retorna true si es relevante
     */
    public boolean isRelevantSection(String sectionName, List<Section> sectionList){
        boolean res = false;
        for (Section section: sectionList) {
            if (section.getName().equals(sectionName)){
                res = true;break;
            }
        }
        return res;
    }


    /**
     * Método que según el nombre de un item de la tabla de contenidos retorna la lista de secciones
     * que le pertenece.
     *
     * Se lee la lista de items de la tabla de contenidos obtenida del archivo XML de configuración de la fuente.
     *
     * @param description
     *              Recibe el nombre o descripción de un item de la tabla de contenidos
     * @return  Retorna una lista de secciones
     * @see
     *              menuItemList
     */
    public List<Section> getSectionsListByMenuItemName(String description){
        List<Section> sectionList = new ArrayList<>();
        //Se recorre los items de la tabla de contenido
        for (MenuItem menuItem: menuItemList) {
            //Si encuentra una coincidencia en el nombre retorna su lista de secciones
            if (menuItem.getName().equals(description)){
                sectionList = menuItem.getSectionList();
            }
        }
        return sectionList;
    }


    /**
     * Método que verifica si el elemento DOM es un tipo de lista según el nombre de la etiqueta (tagName) HTML.
     *
     * @param tagName
     *              Recibe el nombre de una etiqueta "tag" HTML
     * @return Retorna true si el nombre de etiqueta se encuentra en la lista
     * @see
     *              Constants.HTML_LIST_TAG_PARENTS_LIST_TAG
     */
    public boolean isElementIsATypeOfList(String tagName){
        boolean res = false;
        //Recorre la lista de elementos de lista relevantes
        for (String listElement: Constants.HTML_LIST_TAG_PARENTS_LIST_TAG){
            //Si se encuentra significa que es un tipo de lista
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
        // Se leen y recorren los "Sources": mayoclinic, wikipedia, medline, etc. NOTA. Aquí solo se leen páginas de wikipedia.
        //<editor-fold desc="LEER ARCHIVO DE CONFIGURACION">
//        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//        System.out.print( gson.toJson(loadSource.loadSource()) );
//        for (XmlSource xmlSource : loadSource.loadSource())
        XmlSource xmlSource = loadSource.loadSource();
        if (xmlSource!=null){
            // VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE MAYOCLINIC
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
            }//--VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE MAYOCLNIC
            System.out.println("End to document_structure...");
        }
        //</editor-fold>
        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return documentList;
    }


    /**
     * Método que verifica si el nombre de un item de la tabla de contenido es relevante.
     *
     * @param menuItemName
     *              Recibe el nombre del item
     * @param addSections
     *              Recibe indicador para agregar lista de secciones en el documento DISNET recibido
     * @param disnetDocument
     *              Recibe documento DISNET
     * @return  Retorna true si el nombre es relevante
     * @see
     *              addSections
     */
    public boolean isRelevantMenuItem(String menuItemName, boolean addSections, Doc disnetDocument){
        boolean res = false;
        //Recorre los items de la tabla de contenido de un documento
        for (MenuItem menuItem: menuItemList) {
//            System.out.println(menuItemName +" == "+ menuItem.getName());
            //Si el parametro addSections es true se agrega la lista de secciones del item al documento recibido
            if (menuItemName.equals(menuItem.getName())){
                res = true;
                if (addSections){ addSections(disnetDocument, menuItem);}
                break;
            }
        }
        return res;
    }


    /**
     * Mñetodo que agrega una lista de secciones al documento DISNET recibido
     *
     * @param disnetDocument
     * @param menuItem
     */
    public void addSections(Doc disnetDocument, MenuItem menuItem){
        disnetDocument.setSectionList(menuItem.getSectionList());
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
     * Método que al objeto Synonym recibido lo coloca dentro de una lista de Synonyms y retorna dicha lista.
     *
     * @param synonym
     *              Recibe objeto Synonym
     * @return  Retorna una lista de Synonyms
     */
    public List<Synonym> getSynonymList(Synonym synonym){
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(synonym);
        return synonymList;
    }


    /**
     * Método que verifica si el nombre de una enfermedad se encuentra asociado a un documento DISNET, dentro
     * de una lista de documentos DISNET.
     *
     * @param documentList
     *              Recibe la lista de documentos DISNET en la que se buscará
     * @param diseaseName
     *              Recibe el nombre de la enfermedad a buscar
     * @return  Retorna true si el nombdre de la enfermedad se encuentra asociada a un documento DISNET
     */
    public boolean isTheDocumentInTheDocumentList(final List<Doc> documentList, String diseaseName){
        return documentList.stream()
                .filter(o -> o.getDisease().getName().trim() != null)
                .filter(o -> o.getDisease().getName().trim().contentEquals(diseaseName.trim()))
                .findFirst()
                .isPresent();
    }


    /**
     * Método que recupera un documento DISNET a partir de nombre de su enfermedad asociada, dentro de la lista
     * oficial de documentos DISNET relevantes.
     *
     * @param documentList
     *              Recibe la lista de documentos DISNET en la que se buscará
     * @param diseaseName
     *              Recibe el nombre de la enfermedad a buscar
     * @return  Retorna el objeto Doc "documento DISNET" que se encuentra asociado al nombre de la enfermedad
     */
    public Doc getDocumentIfInsideTheDocumentList(final List<Doc> documentList, String diseaseName){
        return documentList.stream()
                .filter(o -> o.getDisease().getName().trim() != null)
                .filter(o -> o.getDisease().getName().trim().contentEquals(diseaseName.trim()))
                .findAny()
                .orElse(null);
    }


    /**
     * Método que generá un TreeMap con las secciones relevantes a partir de la información contenida en el
     * archivo xml de configuración de la fuente.
     *
     * @return Retorna un TreeMap con la descripción de las secciones relevantes
     * @see
     *              sourceConf.getSectionList()
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
     * Método que convierte una lista de secciones provenientes del archivo xml de configuración en una lista de
     * secciones habilidatas para ser usadas en la respuesta del controller
     * import edu.ctb.upm.midas.model.document_structure.Section
     *
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


    public XmlSection getSectionConf(String sectionName){
        XmlSection sectionConf = null;
        for (XmlSection sec: sourceConf.getSectionList()) {
            if (sec.getName().equalsIgnoreCase(sectionName)){
                sectionConf = sec;
                break;
            }
        }
        return sectionConf;
    }


    public Section getSection(String name, List<Section> sectionList){
        Section section = null;
        for (Section sec: sectionList) {
            if (sec.getName().equalsIgnoreCase(name)){
                section = sec;
                break;
            }
        }
        return section;
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
     * Método que retorna la URL de la fuente (la raíz) que contiene la lista de listas de enfermedades.
     *
     * @param linkList
     *              Recibe una lista de URLs raíz a minar
     * @return
     *              Retorna la URL de la página Web general de extracción
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
        try {
            return loadSource.loadSource();
        }catch (Exception e){
            logger.error("Error getting xml source configuration", e);
            return null;
        }
    }
}
