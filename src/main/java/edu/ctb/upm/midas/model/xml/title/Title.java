package edu.ctb.upm.midas.model.xml.title;
/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className Title
 * @see
 */
public abstract class Title {

    private char consult;
    private Integer id;
    private String name;

    public Title(char consult, Integer id, String name){
        setConsult(consult);
        setId(id);
        setName(name);
    }

    public char getConsult() {
        return consult;
    }

    public void setConsult(char consult) {
        this.consult = consult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}