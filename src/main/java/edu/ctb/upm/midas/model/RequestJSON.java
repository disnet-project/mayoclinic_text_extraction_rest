package edu.ctb.upm.midas.model;


import edu.ctb.upm.midas.constants.Constants;
import edu.ctb.upm.midas.constants.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by gerardo on 14/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project mayoclinic_text_extraction_rest
 * @className RequestJSON
 * @see
 */
public class RequestJSON {

    @Valid
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = Constants.ERR_NO_PARAMETER)
    @Size(min = 10, max = 10, message = Constants.ERR_EMPTY_PARAMETER)
    private String snapshot;


    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public String toString() {
        return "RequestJSON{" +
                "snapshot='" + snapshot + '\'' +
                '}';
    }
}
