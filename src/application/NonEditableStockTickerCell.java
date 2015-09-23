package application;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.text.NumberFormat;
import java.text.ParseException;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.text.ParsePosition;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

public class NonEditableStockTickerCell<T> extends TableCell<T,String>{
	private TextField textField;
    
    public NonEditableStockTickerCell(String...styleClasses) {

        getStyleClass().addAll(styleClasses);
    }
    
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());

                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }
    
    private String getString() {
        return getItem() == null ? "" : String.format("%04d",Integer.parseInt(getItem()));
    }
}