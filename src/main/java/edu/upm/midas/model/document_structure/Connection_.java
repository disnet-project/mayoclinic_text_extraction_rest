package edu.upm.midas.model.document_structure;

import org.jsoup.nodes.Document;

/**
 * Created by gerardo on 04/05/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesWikipedia
 * @className Connection_
 * @see
 */
public class Connection_ {

    private String link;
    private String status;
    private String statusCode;
    private Document oDoc;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Document getoDoc() {
        return oDoc;
    }

    public void setoDoc(Document oDoc) {
        this.oDoc = oDoc;
    }
}
