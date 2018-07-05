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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.model.util.Elements;
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

    @Autowired
    private LoadSource loadSource;
    @Autowired
    private ConnectDocument connectDocument;

    @Autowired
    private Common common;
    @Autowired
    private TimeProvider timeProvider;

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
        List<Section> sectionList;
        List<Text> textList;

        TreeMap<String, String> sectionMap;

        int countDoc;
        int countSections;
        int countText;

        Boolean isSection;
        Boolean isText;

        //String version = date.getNowFormatyyyyMMdd();
        //</editor-fold>

        System.out.println("Preparing model...");
        System.out.println("Reading data source configuration...");

        // --------- Inicio del proceso de extracción
        // Se inicializa la lista de "Source"
        sourceList = new ArrayList<>();
        // Se leen y recorren los "Sources": wikipedia, medline, etc. NOTA. Aquí solo se leen páginas de wikipedia.
        //<editor-fold desc="LEER ARCHIVO DE CONFIGURACION">
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

        System.out.print( gson.toJson(loadSource.loadSources()) );
        for (XmlSource xmlSource : loadSource.loadSources()) {
            // VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE WIKIPEDIA
            if (xmlSource.getName().equals(Constants.SOURCE_MAYOCLINIC)) {
                // Se crea una nueva fuente "Source" y un enlace "Link"
                //<editor-fold desc="FUENTE">
                source = new Source();
                url = new Link();
                // Se lee del XML y almacena la información básica de una fuente
                source.setId(xmlSource.getId());
                source.setName(xmlSource.getName());
                // Se lee del XML la información básica del enlace de la fuente
                url.setId(xmlSource.getId());
                url.setUrl(xmlSource.getLink());
                // Se liga el enlace a la fuente
                source.setUrl(url);
                //</editor-fold>
                System.out.println("-----------------------------------------");
                System.out.println("Connecting to... " + xmlSource.getName() + ": " + xmlSource.getLink());
                System.out.println("-----------------------------------------");
                System.out.println("List of sections...");
                //<editor-fold desc="LISTA DE SECCIONES">
                // Se crea un Map donde para almacenar todas las secciones importantes a leer de wikipedia
                sectionMap = new TreeMap<>();
                // Se leen las secciones del XML
                for (XmlSection xmlSection : xmlSource.getSectionList()) {
                    // Se almacenan las secciones en el Map
                    sectionMap.put(xmlSection.getId().trim(), xmlSection.getName());
                    System.out.println(xmlSection.getName());
                }
                // Se enlaza la lista de secciones a la fuente "Source"
                source.setSectionMap(sectionMap);
                //</editor-fold>
                //<editor-fold desc="LISTA DE ITEMS A MINAR">
                System.out.println("List of menu items...");
                for (MenuItem menuItem: xmlSource.getMenuItemList()){
                    System.out.println(menuItem.getName());
                }
                //</editor-fold>
                System.out.println("Procesing links and extracting...");
                //READ DISEASE ALBUM LINKS
                //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                // Se crea una lista de documentos "Doc". Doc: es una documento de wikipedia
                docList = new ArrayList<>();
                // Se inicializa un contador para todos los documentos
                countDoc = 1;
                //<editor-fold desc="LISTA DE ENLACES A ENFERMEDADES">
                //</editor-fold>

            }//--VALIDAR QUE SOLO SE RECUPERE INFORMACIÓN DE WIKIPEDIA
            System.out.println("End to document_structure...");
        }
        //</editor-fold>
        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return sourceList;


    }


    public XmlLink getPrincipalLink(List<XmlLink> linkList){
        XmlLink principalLink = null;
        for (XmlLink xmlLink : linkList) {
            principalLink = xmlLink;
        }
        return principalLink;
    }


    /**
     * Método que se conecta a la página Web donde se encuentra la lista de enfermedades
     *
     * La lista se encuentra partida en cuantas letras tiene el abecesario inglés, por lo que
     * obtiene el enlace de cada una de las secciones del abecedario para despues entrar a cada una
     * de las páginas Web y recuperar la lista de enlaces a artículos MayoClinic
     *
     * @param linkList
     * @return
     */
    public List<XmlLink> getAllLinksFromPrincipalLink(List<XmlLink> linkList, XmlSource xmlSource) throws Exception {
        List<XmlLink> diseaseLinkList = new ArrayList<>();

        Connection_ connection_;
        Document document;

        //<editor-fold desc="SE OBTIENE EL ENLACE PRINCIPAL">
        int countDoc = 1;
        for (XmlLink xmlLink : linkList) {
            // Se conecta con el documento que tiene la lista de enfermedades
            // en orden alfabetico
            connection_ = connectDocument.connect(xmlLink.getUrl());
            // Se verifica si hubo conexión con el documento (enlace Web)
            // Se pinta en pantalla el status OK (esta disponible el enlace)
            System.out.println(countDoc + " mayoclinicExtract " + xmlLink.getUrl() + " ==> " + connection_.getStatus() + "("+connection_.getStatusCode()+")");
            //<editor-fold desc="SI SE HA CONECTADO CON EL DOCUMENTO EXITOSAMENTE">
            if (connection_.getStatus().equals(StatusHttpEnum.OK.getDescripcion()) && connection_.getDocument() != null) {
                // Se obtiene el documento HTML
                document = connection_.getDocument();
                // Se obtiene el elemento HTML que almacena el nombre de la enfermedad
                String mainContentElementId = getHighlightXmlByDescription(Constants.XML_HL_MAIN, xmlSource).getId();
                Elements listsOfDiseases = document.

            } else {//end if oConnect.connection_().equals("OK")
                // Mensaje mostrado al documento que no se pudo conectar
                System.out.println(xmlLink.getUrl() + " ==> " + connection_.getStatus());
            }//end else if oConnect.connection_().equals("OK")
            //</editor-fold>
            countDoc++;
        }
        //</editor-fold>

        // Retorna la lista de fuentes, con sus documentos, enfermedades, secciones, códigos y textos...
        return diseaseLinkList;
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

}
