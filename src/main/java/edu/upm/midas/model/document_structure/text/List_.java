package edu.upm.midas.model.document_structure.text;

import java.util.List;

/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationWikipedia
 * @className List_
 * @see
 */
public class List_ extends Text {

    private List<String> bulletList;//Texto al final de cuentas


    public List<String> getBulletList() {
        return bulletList;
    }

    public void setBulletList(List<String> bulletList) {
        this.bulletList = bulletList;
    }
}
