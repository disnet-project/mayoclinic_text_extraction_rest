package edu.ctb.upm.midas.schedulling;

import edu.ctb.upm.midas.common.util.TimeProvider;
import edu.ctb.upm.midas.constants.Constants;
import edu.ctb.upm.midas.enums.StatusHttpEnum;
import edu.ctb.upm.midas.model.Request;
import edu.ctb.upm.midas.model.Response;
import edu.ctb.upm.midas.model.document_structure.Doc;
import edu.ctb.upm.midas.model.document_structure.Section;
import edu.ctb.upm.midas.model.document_structure.Source;
import edu.ctb.upm.midas.service.ExtractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by gerardo on 16/07/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project mayoclinic_text_extraction_rest
 * @className ExtractionSchedulling
 * @see
 */
@Service
public class ExtractionSchedulling {

    private static final Logger logger = LoggerFactory.getLogger(ExtractionSchedulling.class);

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ExtractService extractService;

    /**
     * Método para extraer una nueva lista de enfermedades desde DBPedia y almacenar en la
     * base de datos "addb".
     *
     * Se ejecutará cada día primero de cada mes a la 12:00 horas = @Scheduled(cron = "0 0 12 1 * ? ").
     *
     * Explicación de las expresiones Cron:
     *
     * Dada la siguiente expresión: @Scheduled(cron = "0 9 23 ? * 5 ")
     * La tarea anterior se ejecutará a las 23 horas con 9 minutos y 0 segundos, todos los meses, los días 5 (viernes).

     Las expresiones cron tienen 6 valores obligatorios.

     Segundos. En nuestro ejemplo tiene el valor 0. Acepta valores del 0-59 y caracteres especiales como , - * /
     Minutos. En nuestro ejemplo tiene el valor 9. Acepta valores del 0-59 y caracteres especiales como , - * /
     Hora. En nuestro ejemplo tiene el valor 23. Acepta valores del 0-23 y caracteres especiales como , - * /
     Día del mes. En nuestro ejemplo tiene el caracter especial “?” el cual significa no definido
     ya que no deseamos que se ejecute un determinado día del mes,
     en su lugar deseamos que se ejecute un determinado día de la semana.
     Acepta valores del 1-31 y caracteres especiales como , - * ? /
     Mes. En nuestro ejemplo tiene el caracter especial “*” el cuál significa todos , es decir, deseamos se ejecute todos los meses. Acepta valores del 1-12 o abreviaturas JAN-DEC y caracteres especiales como , - * /
     Día de la semana. En nuestro ejemplo tiene el valor 5, es decir, deseamos se ejecute el quinto día (Viernes). Acepta valores del 1-7 o abreviaturas SUN-SAT y caracteres especiales como , - * ? /
     El día del mes y el día de la semana son excluyentes, es decir que podemos definir solo uno de los dos, no ámbos. En nuestro ejemplo queremos que se ejecute siempre un día de la semana por lo tanto en la posición de día del mes asignaremos un “?” para indicar que no está definido.

     El caracter especial “/” se usa para especificar incrementos.
     Por ejemplo en el campo de minutos, un valor como 0/1 indica que la tarea se ejecutará cada minuto,
     en el campo de segundos un valor como 0/15 indica una ejecución cada 15 segundos.
     Se ejecuta cada minuto de todos los dias sábados a media noche.
     @Scheduled(cron = "0 0/1 0 ? * 6 ")

     El caracter especial “,” se usa para especificar un conjunto de valores.
     Por ejemplo en el campo de día de la semana, un valor como “6,7”
     indica que la tarea se ejecutará todos los sábados y domingos.
     Se ejecuta cada 15 segundos los días sábados y domingos a media noche.
     @Scheduled(cron = "0/15 * 0 ? * 6,7 ")
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void extractionEveryFirstDayOfTheMonth() {
        try {
            logger.info("Start!. Scheduled task for the knowledge retrieval procedure for each first day of each month at midnight.");
            logger.info("MayoClinic snapshot: {}", timeProvider.getNowFormatyyyyMMdd());
            extractionProcedure(timeProvider.getNowFormatyyyyMMdd(), true);
            logger.info("Finish it!. Scheduled task for the knowledge retrieval procedure for each first day of each month at midnight.");
        }catch (Exception e){
            logger.error("Error. Scheduled task for the knowledge retrieval procedure for each first day of each month at midnight.", e);
        }
    }


    private void extractionProcedure(String snapshot, boolean saveJason) throws Exception {
        Request request = new Request(snapshot, saveJason);
        Response response = extractService.extract(request);
        if (response.getResponseCode().equals(StatusHttpEnum.OK.getClave())) {
            logger.info("{}: {}", response.getResponseCode(), response.getResponseMessage());
            retrievalShortReport(response);
        }else {
            logger.error("{}: {}", response.getResponseCode(), response.getResponseMessage());
            retrievalShortReport(response);
//              Enviar mensaje a correo electrónico de fallo en la recuperación de conocimiento
        }

    }


    private void retrievalShortReport(Response response){
        for (Source source: response.getSources()) {
            logger.info("Number of retrieved documents: {}", source.getDocumentCount());
            int textCount = 0;
            for (Doc doc: source.getDocuments()) {
                if (doc.getSectionList()!=null)
                for (Section section: doc.getSectionList()) {
                    textCount = textCount + section.getTextCount();
                }
            }
            logger.info("Number of retrieved documents with texts: {}", textCount);
        }
        logger.info("Start time: {} - End time: {}", response.getStart_time(), response.getEnd_time());
    }


    /**
     * Método para extraer una nueva lista de enfermedades desde DBPedia y almacenar en la
     * base de datos "addb".
     *
     * Se ejecutará cada día quince de cada mes a la 12:00 horas = @Scheduled(cron = "0 0 12 15 * ? ").
     */
    //@Scheduled(cron = "0 15 14 15 * ?" )
    //@Scheduled(cron="*/5 * * * * ?")
    @Scheduled(cron = "0 0 0 15 * ?")
    public void extractionEvery15thDayOfTheMonth() throws Exception {
        try {
            logger.info("Start!. Scheduled task for the knowledge retrieval procedure for each 15th day of each month at midnight.");
            logger.info("MayoClinic snapshot: {}", timeProvider.getNowFormatyyyyMMdd());
            extractionProcedure(timeProvider.getNowFormatyyyyMMdd(), true);
            logger.info("Finish it!. Scheduled task for the knowledge retrieval procedure for each 15th day of each month at midnight.");
        }catch (Exception e){
            logger.error("Error. Scheduled task for the knowledge retrieval procedure for each 15th day of each month at midnight.", e);
        }
    }
}
