package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * Holds all top ten values in the form of a VBox, that will be displayed via the Score Scene class.
 */
public class ScoresList extends VBox {

    /**
     * List, containing all values from the external scores.txt file
     */
    private final SimpleListProperty<Pair<String, Integer>> simpleListProperty;

    /**
     * Creates the list that will hold all values
     *
     * @param width the width
     */
    public ScoresList(double width) {
        this.simpleListProperty = new SimpleListProperty<>();

        //Designing the basics of the VBox structure
        setWidth(width);
        setSpacing(20);
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.TOP_CENTER);
    }

    /**
     * Returns the list in the form of a property for easier binding
     *
     * @return the list
     */
    public SimpleListProperty<Pair<String, Integer>> simpleListPropertyProperty() {
        return simpleListProperty;
    }

    /**
     * Prepares the UI for the top 10 scores that are shown in the scores scene
     */
    public void reveal() {
        //The vertical box that will organize all results in one column
        var userBox = new VBox();

        //Extracting information from the list property field to prepare the UI components
        for (int i = 0; i < simpleListProperty.size(); i++) {

            //Stops preparing components when it already has 10 values
            if (i >= 10) {
                break;
            }

            //The text containing the key information
            var output = new Text(simpleListProperty.get(i).getKey() + ": " + simpleListProperty.get(i).getValue());

            //Basic design for the layout
            output.getStyleClass().add("heading");
            userBox.getChildren().add(output);
            userBox.setSpacing(10);
            HBox.setHgrow(output, Priority.ALWAYS);
        }

        userBox.setAlignment(Pos.CENTER);

        //Adding the whole box into the main one
        this.getChildren().add(userBox);
    }

}
