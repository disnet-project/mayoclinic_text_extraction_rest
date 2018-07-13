package edu.upm.midas.model.document_structure.text;

/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationMayoClinic
 * @className Paragraph
 * @see
 */
public class Paragraph extends Text {

    private String text;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Paragraph{" +
                "id='" + super.getId() + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", textOrder='" + super.getTextOrder() + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
