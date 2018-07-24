package edu.ctb.upm.midas.controller;

import edu.ctb.upm.midas.model.Request;
import edu.ctb.upm.midas.model.RequestJSON;
import edu.ctb.upm.midas.model.Response;
import edu.ctb.upm.midas.service.ExtractService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by gerardo on 03/07/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project mayoclinic
 * @className ExtractionController
 * @see
 */
@RestController
@RequestMapping("${my.service.rest.request.mapping.general.url}")
public class ExtractionController {

    @Autowired
    private ExtractService extractService;


    @RequestMapping(path =  {  "${my.service.rest.request.mapping.texts.path}" },
            method = RequestMethod.POST)
    public Response extract(@RequestBody @Valid Request request, HttpServletRequest httpRequest/*, Device device*/) throws Exception {
        return extractService.extract(request);
    }


    @RequestMapping(path =  {  "${my.service.rest.request.mapping.texts.json.path}" },
            method = RequestMethod.POST/*,
            params = {"snapshot", "action"}*/)
    public Response extractJSON(/*@RequestParam(value = "snapshot") @Valid @NotBlank @NotEmpty @NotNull String snapshot,
                                @RequestParam(value = "action") @Valid @NotBlank @NotEmpty @NotNull  String action,*/
                                @RequestBody @Valid RequestJSON request,
                                HttpServletRequest httpRequest/*, Device device*/) throws Exception {
        return extractService.extractJSON(request);
    }


    @RequestMapping(path =  {  "${my.service.rest.request.mapping.report.path}" },
            method = RequestMethod.GET,
            params = {"snapshot", "action"})
    public void extractionReport(
            @RequestParam(value = "snapshot") @Valid @NotBlank @NotEmpty @NotNull String snapshot,
            @RequestParam(value = "action") @Valid @NotBlank @NotEmpty @NotNull  String action,
            HttpServletRequest httpRequest/*, Device device*/) throws Exception {
        Request request = new Request(snapshot, false, action);
//        extractService.extractionReport(request);
    }


}
