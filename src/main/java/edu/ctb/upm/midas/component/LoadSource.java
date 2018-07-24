package edu.ctb.upm.midas.component;

import edu.ctb.upm.midas.constants.Constants;
import edu.ctb.upm.midas.model.xml.XmlSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Created by gerardo on 28/3/17.
 * @project ExtractionInformationMayoClinic
 * @version ${<VERSION>}
 * @author Gerardo Lagunes G.
 * @className LoadSource
 * @see
 */
/*HACER QUE COJA LA RUTA DESDE EL PROPERTIES*/
@Component
public class LoadSource {

    private static final Logger logger = LoggerFactory.getLogger(LoadSource.class);

    @Autowired
    private ReadXml oReadXml;

    /**
     * Método que carga todas las configuraciones de fuentes que se encuentren en el archivo XML de configuración.
     *
     * Carga el archivo desde dentro del JAR creando una en stream y creando un template del archivo.
     *
     * Puede haber muchas configuraciones de muchas fuentes.
     *
     * Las configuraciones que se cargan son acerca de los elementos que se quieren extraer o que son necesarios
     * para la extracción de los textos.
     *
     * @return
     *      La configuraciones de la fuente a hacer minería de texto
     */
    public XmlSource loadSource()  {
//        List<XmlSource> xmlSources = null
        XmlSource xmlSource = null;
        try {
//        File xmlFile = new File( Constants.XML_SOURCE_FOLDER + Constants.XML_SOURCE_FILE );
            ClassPathResource classPathResource = new ClassPathResource(Constants.XML_CONFIG_FOLDER + Constants.XML_CONFIG_FILE + Constants.DOT_XML);
            InputStream inputStream = classPathResource.getInputStream();
            File xmlFile = File.createTempFile(Constants.XML_CONFIG_FILE, Constants.DOT_XML);
            System.out.println(xmlFile.toString());
            try {
                FileUtils.copyInputStreamToFile(inputStream, xmlFile);
                oReadXml.file(xmlFile);
            } catch (Exception e) {
                logger.error("Error adding disease {}", xmlFile, e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }catch (Exception e){
            logger.error("Error creating template xml source configuration file {}", e);
        }
        try {
            xmlSource = oReadXml.read();
        } catch (JDOMException e) {
//            e.printStackTrace();
            logger.error("Error 1 reading xml source configuration {}", e);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("Error 2 reading xml source configuration {}", e);
        }
        return xmlSource;

    }

}
