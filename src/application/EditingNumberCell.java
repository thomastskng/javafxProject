package application;
import javafx.stage.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.io.IOException;
import java.lang.*;
import javafx.scene.control.cell.*;
import javafx.collections.*;
import javafx.scene.control.MultipleSelectionModel.*;
import javafx.scene.control.SelectionModel.*;
import javafx.util.converter.*;
import javafx.scene.control.TableColumn.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.input.MouseEvent;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import java.util.function.Function;
import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.beans.property.SimpleObjectProperty;
import java.util.*;
import javafx.css.PseudoClass;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.util.StringConverter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.beans.property.ReadOnlyBooleanWrapper;
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
import java.text.ParseException;
import java.util.regex.Pattern;

public class EditingNumberCell<T> extends TableCell<T,Number>{

    private TextField textField;
    private TextFormatter<Number> textFormatter ;

    private Pattern partialInputPattern = Pattern.compile(
            "[-]?[,0-9]*(\\.[0-9]*)?");

    private DecimalFormat df ;

    public EditingNumberCell(String...styleClasses) {
        Locale locale  = new Locale("en", "UK");
        String pattern = "###,###.###;-###,###.###";
        df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern(pattern);
        //df.setNegativePrefix("(" + df.getPositivePrefix());
        //df.setNegativeSuffix(")");
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(10);

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
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(df.format(getItem()));
        setGraphic(null);
    }


    @Override
    public void updateItem(Number item, boolean empty) {
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
        return getItem() == null ? "" : df.format(getItem());
    }

    private void createTextField(){

        textField = new TextField();

        StringConverter<Number> converter = new StringConverter<Number>() {

            @Override
            public String toString(Number number) {
                return df.format(number);
            }

            @Override
            public Double fromString(String string) {
                try {
                    double value = df.parse(string).doubleValue() ;
                    return value;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0.0 ;
                }
            }

        };

        textFormatter = new TextFormatter<>(converter,  0.0, c -> {
            if (partialInputPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        }) ;

        // add filter to allow for typing only integer
        textField.setTextFormatter( textFormatter);

        textField.setText( getString() );

        textField.setMinWidth( this.getWidth() - this.getGraphicTextGap() * 2 );

        // commit on Enter
        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            commitEdit(newValue);
        });
    }
}
