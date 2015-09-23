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

public class EditingStockTickerCell<T> extends TableCell<T,String>{
	private TextField textField;
    private TextFormatter<String> textFormatter ;
    private Pattern partialInputPattern = Pattern.compile("[0-9]*(\\.[0-9]*)?");
    private NumberFormat nf ;
    
    public EditingStockTickerCell(String...styleClasses) {
        //Locale locale  = new Locale("en", "UK");
        //nf = NumberFormat.getIntegerInstance(locale);

        getStyleClass().addAll(styleClasses);
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
        setText(String.format("%04d", nf.format(getItem())));
        setGraphic(null);
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
    
    private void createTextField(){
    	    	
        nf = NumberFormat.getIntegerInstance();        
        textField = new TextField();


        // add filter to allow for typing only integer
        textField.setTextFormatter( new TextFormatter<>( c ->
        {
        	if (c.getControlNewText().isEmpty()) {
        	    return c;
        	}
            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = nf.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() || c.getControlNewText().length() > 4 )
            {
                return null;
            }
            else
            {
                return c;
            }
        } ) );

        textField.setText( getString() );

        textField.setMinWidth( this.getWidth() - this.getGraphicTextGap() * 2 );

        /*
        // commit on Enter
        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            commitEdit(newValue);
        });
        */
        
        
        // commit on Enter
        textField.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                commitEdit( textField.getText() );
            }
        } );

        textField.focusedProperty().addListener( new ChangeListener<Boolean>()
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> arg0,
                    Boolean arg1, Boolean arg2 )
            {
                if ( !arg2 )
                {
                    commitEdit( textField.getText() );
                }
            }
        } );

    }
}
