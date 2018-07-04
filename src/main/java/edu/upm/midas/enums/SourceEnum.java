package edu.upm.midas.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gerardo on 09/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className SourceEnum
 * @see
 */
public enum SourceEnum {

    //MESH(1, "MESH"),
    MeSH(1, "MeSH"),
    ICD_9(2, "ICD-9"),
    //ICD9(2, "ICD9"),
    ICD_10(3, "ICD-10"),
    //ICD10(3, "ICD10"),
    OMIM(4, "OMIM"),
    SNOMED_CT(5, "SNOMED_CT"),
    //SNOMEDCT(5, "SNOMEDCT"),
    UMLS(6, "UMLS"),
    DISEASE_ONTOLOGY(7, "DiseaseOntology"),
    NCI(8, "NCI"),
    CSP(9, "CSP")
    ;

    private int clave;
    private String descripcion;

    private SourceEnum(int clave, String descripcion) {
        this.clave = clave;
        this.descripcion = descripcion;
    }

    public static SourceEnum getEnum(String clave) {
        if (StringUtils.isNotBlank(clave)) {
            for (SourceEnum source : SourceEnum.values()) {
                if (clave.equals(source.getClave()))
                    return source;
            }
        }
        return null;
    }

    public int getClave() {
        return clave;
    }

    public void setClave(int clave) {
        this.clave = clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
