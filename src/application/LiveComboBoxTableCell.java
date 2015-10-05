package application;

import java.text.NumberFormat;
import java.text.ParsePosition;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class LiveComboBoxTableCell<S,T> extends TableCell<S, T> {
	private TextField textField;
    private final ComboBox<T> comboBox ;

    public LiveComboBoxTableCell(ObservableList<T> items) {
        this.comboBox = new ComboBox<>(items);

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        comboBox.valueProperty().addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> obs, T oldValue, T newValue) {
                // attempt to update property:
                ObservableValue<T> property = getTableColumn().getCellObservableValue(getIndex());
                if (property instanceof WritableValue) {
                    ((WritableValue<T>) property).setValue(newValue);
                }
            }
        });
    }
    
    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.requestFocus();
            //textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        //setText((String) getItem());
        setText(getString());
        setGraphic(null);
    }
    
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            comboBox.setValue(item);
            setGraphic(comboBox);
        }
    }
 
    private String getString() {
        return getItem() == null ? "" : String.format("%04d",getItem());
    }
    
    private void createTextField(){
    	
        textField = new TextField();



        textField.setText( getString() );

        textField.setMinWidth( this.getWidth() - this.getGraphicTextGap() * 2 );

        /*
        // commit on Enter
        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            commitEdit(newValue);
        });
        */
        
        


    }
    
    
}
