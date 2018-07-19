package edu.upm.midas.service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.upm.midas.common.util.Common;
import edu.upm.midas.common.util.TimeProvider;
import edu.upm.midas.component.MayoClinicExtraction;
import edu.upm.midas.constants.Constants;
import edu.upm.midas.enums.ApiErrorEnum;
import edu.upm.midas.enums.StatusHttpEnum;
import edu.upm.midas.model.Request;
import edu.upm.midas.model.Response;
import edu.upm.midas.model.document_structure.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerardo on 03/07/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project mayoclinic
 * @className ExtractService
 * @see
 */
@Service
public class ExtractService {

    private static final Logger logger = LoggerFactory.getLogger(ExtractService.class);

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private Common common;
    @Autowired
    private MayoClinicExtraction mayoClinicExtraction;


    /**
     * @param request
     * @return
     * @throws Exception
     */
    public Response extract(Request request) throws Exception {
        Response response = new Response();
        List<Source> sourceList = new ArrayList<>();

        String start = timeProvider.getTimestampFormat();
        String end;
        String snapshot = timeProvider.getNowFormatyyyyMMdd();

        response.setSources(sourceList);
        if (snapshot.equals(request.getSnapshot())) {

            try {
                sourceList = mayoClinicExtraction.extract(request.getSnapshot());
                if (sourceList.size()>0) {
                    response.setResponseCode(StatusHttpEnum.OK.getClave());
                    response.setResponseMessage(StatusHttpEnum.OK.getDescripcion());
                } else {
                    response.setResponseCode(ApiErrorEnum.RESOURCES_NOT_FOUND.getKey());
                    response.setResponseMessage(ApiErrorEnum.RESOURCES_NOT_FOUND.getDescription());
                }
            } catch (Exception e) {
                response.setResponseCode(ApiErrorEnum.INTERNAL_SERVER_ERROR.getKey());
                response.setResponseMessage(ApiErrorEnum.INTERNAL_SERVER_ERROR.getDescription());
            }
        }else{
            response.setResponseCode(ApiErrorEnum.INVALID_SNAPSHOT.getKey());
            response.setResponseMessage(ApiErrorEnum.INVALID_SNAPSHOT.getDescription());
        }
        response.setStart_time(start);
        end = timeProvider.getTimestampFormat();
        response.setEnd_time(end);
        //<editor-fold desc="ESCRIBIR JSON CON LA RESPUESTA">
        if (request.isJson() && sourceList.size()>0) {
            try {
                logger.info("Saving initiated MayoClinic texts in a JSON");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                common.writeJSONFile(gson.toJson(response), snapshot /*timeProvider.dateFormatyyyMMdd(snapshot)*/, Constants.RETRIEVAL_FILE_NAME);
                logger.info("Saving of finished MayoClinic texts in a JSON");
            } catch (Exception e) {
                logger.error("Error while save json {} ", snapshot /*timeProvider.dateFormatyyyMMdd(snapshot)*/ + Constants.RETRIEVAL_FILE_NAME + Constants.DOT_JSON);
            }
        }
        //</editor-fold>


        System.out.println("Inicio:" + start + " | Termino: " + end);

        return response;

    }


    /**
     * @param request
     * @return
     * @throws Exception
     */
    public Response extractJSON(Request request) throws Exception {
        Response response = new Response();
        String start = timeProvider.getTimestampFormat();
        String end;
        try {
            //Extrae el archivo de textos
            if (request.getExtractionType().equals(Constants.TEXTS)) {//System.out.println("entra texts: " + request.getSnapshot());
                response = common.readJSONFile(request.getSnapshot(), Constants.RETRIEVAL_FILE_NAME);
            }else{
                //Extrae resources
                response = common.readJSONFile(request.getSnapshot(), Constants.RETRIEVAL_RESOURCES_FILE_NAME);
            }
        }catch (Exception e){
            response.setSources(new ArrayList<>());
            response.setResponseCode(ApiErrorEnum.INTERNAL_SERVER_ERROR.getKey());
            response.setResponseMessage(ApiErrorEnum.INTERNAL_SERVER_ERROR.getDescription());
        }
        response.setStart_time(start);
        end = timeProvider.getTimestampFormat();
        response.setEnd_time(end);

        System.out.println("Inicio:" + start + " | Termino: " + end);

        return response;
    }



}