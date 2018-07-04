package edu.upm.midas.enums;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by gerardo on 20/07/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project eidw
 * @className ContentType
 * @see
 */
public enum ContentType {

    PARA("PARA", "Paragraph"),
    LIST("LIST", "List"),
    TABLE("TABLE", "Table");

    private String clave;
    private String descripcion;


    private ContentType(String clave, String descripcion) {
        this.clave = clave;
        this.descripcion = descripcion;
    }

    public static ContentType getEnum(String clave) {
        if (StringUtils.isNotBlank(clave)) {
            for (ContentType type : ContentType.values()) {
                if (clave.equals(type.getClave()))
                    return type;
            }
        }
        return null;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
