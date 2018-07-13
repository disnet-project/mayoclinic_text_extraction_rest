package edu.upm.midas.model.document_structure;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.upm.midas.model.document_structure.code.Code;

import java.util.List;

/**
 * Created by gerardo on 09/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className Synonym
 * @see
 */
public class Synonym {

    private int id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codeCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Code> codes;

    public Synonym() {
    }

    public Synonym(String name) {
        this.name = name;
    }

    public Synonym(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(Integer codeCount) {
        this.codeCount = codeCount;
    }

    public List<Code> getCodes() {
        return codes;
    }

    public void setCodes(List<Code> codes) {
        this.codes = codes;
    }

    @Override
    public String toString() {
        return "Synonym{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", codeCount=" + codeCount +
                ", codes=" + codes +
                '}';
    }
}
