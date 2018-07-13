package edu.upm.midas.common.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by gerardo on 09/05/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesMayoClinic
 * @className UniqueId
 * @see
 */
@Service
public class UniqueId {

    @Autowired
    private TimeProvider timeProvider;

    public String generate(int length){
        return RandomStringUtils.randomAlphanumeric( length ).toLowerCase();
    }

    public String generateDisease(int disease){
        String dis = String.format("%06d", disease);
        return "DIS" + dis;
    }

    public String generateSource(int source){
        String sou = (source < 10)?String.format("%02d", source):source+"";
        return "SO" + sou;
    }

    public String generateDocument(String sourceId, int document){
        String doc = (document < 10)?String.format("%02d", document):document+"";
        return sourceId + ".DOC" + doc;
    }

    public String generateCode(String code, int resourceId){
        //String cod = (code < 10)?String.format("%02d", code):code+"";
        //return documentId + ".C" + cod;
        return "C" + code + "/" + resourceId;
    }

    public String generateSection(int section){
        String sec = (section < 10)?String.format("%02d", section):section+"";
        return "SEC" + sec;
    }

    public String generateText(String documentId, String sectionId, int text){
        String txt = (text < 10)?String.format("%02d", text):text+"";
        return documentId + "." + sectionId + ".T" + txt;
    }

    public String generateUrl(String id, int url){
        String u = (url < 10)?String.format("%02d", url):url+"";
        return id + ".U" + u;
    }

    public String generateConfiguration(String source, String version){
        String configurationId = source + ":" + version + "_" + timeProvider.getTimestampNumber();
        return configurationId;
    }



}
