package edu.ctb.upm.midas.common.util;

import com.google.gson.Gson;
import edu.ctb.upm.midas.constants.Constants;
import edu.ctb.upm.midas.model.Response;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

;

/**
 * Created by gerardo on 10/05/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesMayoClinic
 * @className Validations
 * @see
 */
@Service
public class Common {

    private static final Logger logger = LoggerFactory.getLogger(Common.class);

    public boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        else {
            if (string.trim().equalsIgnoreCase("")) {
                return true;
            }
            else {
                return false;
            }

        }
    }

    public String cutString(String str) {
        return str = str.substring(0, str.length()-2);
    }


    /**
     * @param cutStart
     * @param cutFinal
     * @param str
     * @return
     */
    public String cutStringPerformance(int cutStart, int cutFinal, String str) {
        return str = str.substring(cutStart, str.length() - cutFinal);
    }

    public String cutString(int cutStart, int cutFinal, String str) {
        return str = str.substring(cutStart, cutFinal);
    }


    public String replaceUnicodeToSpecialCharacters(String data){

        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        return m.appendTail(buf).toString();

    }

    public String getUnicode(char character){
        return "\\u" + Integer.toHexString(character | 0x10000).substring(1);
    }

    public String replaceSpecialCharactersToUnicode(String text){
        return StringEscapeUtils.escapeJava(text);
    }



    public void removeRepetedElementsList(List<String> elementsList){
        Set<String> linkedHashSet = new LinkedHashSet<String>();
        linkedHashSet.addAll(elementsList);
        elementsList.clear();
        elementsList.addAll(linkedHashSet);
    }



    public boolean itsFound(String originalStr, String findStr){
//        System.out.println("RECIBE itsFound: ORI:" + originalStr + " | FIND: " + findStr);
        return originalStr.trim().indexOf(findStr.trim()) != -1;// Retorna true si ha encontrado la subcadena en la cadena
    }

    /**
     * @param jsonBody
     * @param snapshot
     * @throws IOException
     */
    public void writeJSONFile(String jsonBody, String snapshot, String file_name) throws IOException {
        String fileName = snapshot + file_name + Constants.DOT_JSON;
        String path = Constants.RETRIEVAL_HISTORY_FOLDER + fileName;
        InputStream in = getClass().getResourceAsStream(path);
        //BufferedReader bL = new BufferedReader(new InputStreamReader(in));
        File file = new File(path);
        BufferedWriter bW;

        if (!file.exists()){
            bW = new BufferedWriter(new FileWriter(file));
            bW.write(jsonBody);
            bW.close();
        }
    }


    /**
     * @param version
     * @return
     * @throws Exception
     */
    public Response readJSONFile(String version, String file_name) throws Exception {
        Response response = null;
        System.out.println("Read JSON!... ");
        Gson gson = new Gson();
        String fileName = version + file_name + Constants.DOT_JSON;
        String path = Constants.RETRIEVAL_HISTORY_FOLDER + fileName;
        System.out.println(path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            response = gson.fromJson(br, Response.class);
        }catch (Exception e){
            logger.error("Error to read or convert JSON {}", path, e);
//            System.out.println("Error to read or convert JSON!..." + e.getLocalizedMessage() + e.getMessage() + e.getCause());
        }
        return response;
    }

}
