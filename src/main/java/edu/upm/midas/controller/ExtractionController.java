package edu.upm.midas.controller;

import edu.upm.midas.model.Request;
import edu.upm.midas.model.Response;
import edu.upm.midas.service.ExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    public Response extract(@RequestBody @Valid Request request, HttpServletRequest httpRequest) throws Exception {
        return extractService.extract(request);
    }


}
