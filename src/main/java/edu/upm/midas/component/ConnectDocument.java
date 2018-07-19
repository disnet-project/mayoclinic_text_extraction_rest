package edu.upm.midas.component;

import edu.upm.midas.common.util.Common;
import edu.upm.midas.constants.Constants;
import edu.upm.midas.enums.StatusHttpEnum;
import edu.upm.midas.model.document_structure.Connection_;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by gerardo on 29/3/17.
 * @project ExtractionInformationMayoClinic
 * @version ${<VERSION>}
 * @author Gerardo Lagunes G.
 * @className ConnectDisease
 * @see
 */
@Component
public class ConnectDocument {

    private static final Logger logger = LoggerFactory.getLogger(ConnectDocument.class);

    @Autowired
    private Common common;
    @Value(value = "${my.service.rest.timeout.jsoup}")
    public Integer JSOUP_TIMEOUT;

    /**
     * Con esta método compruebo el Status code de la respuesta que recibo al hacer la petición
     * EJM:
     * 		200 OK			        300 Multiple Choices
     * 		301 Moved Permanently	305 Use Proxy
     * 		400 Bad Request		    403 Forbidden
     * 		404 Not Found		    500 Internal Server Error
     * 		502 Bad Gateway		    503 Service Unavailable
     *
     * @paramFromObject getLink()
     * @return Devuelve una conexión a un documento HTML (status, url y el documento)
     *//*REGRESAR UN OBJETO CONNECTOBJECTRESPONSE*/
    public Connection_ connect(String link) throws Exception {
        //Response oResponse; ya no se usa
        Connection_ connection_ = new Connection_();
        connection_.setLink( link );
        //System.out.println(URLEncoder.encode(common.cutStringPerformance(30, 0,Constants.HTTP_HEADER +link), "UTF-8"));

        try {//Constants.HTTP_HEADER +
            Connection connection = Jsoup.connect( connection_.getLink() ).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").timeout(JSOUP_TIMEOUT*1000);
            // Connection connection = Jsoup.connect( connection_.getLink().replace("http", "https") )
            //oResponse = connection.execute(); || Jsoup.connect(Constants.HTTP_HEADER + connection_.getLink()
            connection_.setDocument( connection.get()/*getHtmlDocument(connection)*/ );
            connection_.setStatus( connection.execute().statusMessage() );
            connection_.setStatusCode( connection.execute().statusCode()+"" );
        } catch (Exception e) {
            connection_ = tryConnect(connection_);
            if (connection_.getDocument() == null) {
                connection_.setStatus(StatusHttpEnum.NOT_FOUND.getDescripcion());
                connection_.setStatusCode(StatusHttpEnum.NOT_FOUND.getClave());
                connection_.setDocument(null);
            }
            logger.error("Exception to connect with the page: ({}) ", connection_.getLink(), e);
        }

        return connection_;
    }


    public Connection_ tryConnect(Connection_ connection_) throws UnsupportedEncodingException {
        String convertLink = Constants.HTTPS_HEADER + "www.mayoclinic.org/" + URLEncoder.encode(common.cutStringPerformance(26, 0, connection_.getLink()), "UTF-8");
        System.out.println(connection_.getLink()+ " - " +convertLink);

        try {//Constants.HTTP_HEADER +
            Connection connection = Jsoup.connect( convertLink ).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").timeout(JSOUP_TIMEOUT*1000);
            // Connection connection = Jsoup.connect( connection_.getLink().replace("http", "https") )
            //oResponse = connection.execute(); || Jsoup.connect(Constants.HTTP_HEADER + connection_.getLink()
            connection_.setDocument( connection.get()/*getHtmlDocument(connection)*/ );
            connection_.setStatus( connection.execute().statusMessage() );
            connection_.setStatusCode( connection.execute().statusCode()+"" );
        } catch (Exception e) {
            connection_.setStatus( StatusHttpEnum.NOT_FOUND.getDescripcion() );
            connection_.setStatusCode( StatusHttpEnum.NOT_FOUND.getClave() );
            connection_.setDocument(null);
            logger.error("Exception to connect with the page: ({}) ", connection_.getLink(), e);
        }
        return connection_;
    }

}
