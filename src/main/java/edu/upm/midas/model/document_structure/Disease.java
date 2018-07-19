package edu.upm.midas.model.document_structure;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

/**
 * Created by gerardo on 29/3/17.
 * @project ExtractionInformationMayoClinic
 * @version ${<VERSION>}
 * @author Gerardo Lagunes G.
 * @className Disease
 * @see
 */
public class Disease {

    private int id;
    private String name;
    private String definition;
    private String cui;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer synonymCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Synonym> synonyms;

    public Disease() {
    }

    public Disease(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Disease(int id, String name, String definition, List<Synonym> synonyms) {
        this.id = id;
        this.name = name;
        this.definition = definition;
        this.synonyms = synonyms;
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public Integer getSynonymCount() {
        return synonymCount;
    }

    public void setSynonymCount(Integer synonymCount) {
        this.synonymCount = synonymCount;
    }

    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Synonym> synonyms) {
        this.synonyms = synonyms;
        if (this.synonyms!=null) this.synonymCount = this.synonyms.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disease)) return false;
        Disease disease = (Disease) o;
        return Objects.equals(getCui(), disease.getCui());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCui());
    }


//    @Override
//    public String toString() {
//        return "Disease{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
////                ", definition='" + definition + '\'' +
////                ", cui='" + cui + '\'' +
////                ", synonymCount=" + synonymCount +
////                ", synonyms=" + synonyms +
//                '}';
//    }
}
