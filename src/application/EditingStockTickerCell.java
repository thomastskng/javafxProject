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
import javafx.scene.control.TextFormatter;
import java.text.ParsePosition;
import java.text.DecimalFormat;
import java.util.*;

public class EditingStockTickerCell extends TableCell<Trade,String>{
	private TextField textField;
	 
    public EditingStockTickerCell() {
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
        setText((String) getItem());
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
        return getItem() == null ? "" : getItem().toString();
    }
    
    private void createTextField(){
    	    	
        NumberFormat nf = NumberFormat.getIntegerInstance();        
        textField = new TextField();

        // add filter to allow for typing only integer
        textField.setTextFormatter( new TextFormatter<>( c ->
        {
        	if (c.getControlNewText().isEmpty()) {
        	    return c;
        	}
            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = nf.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
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
