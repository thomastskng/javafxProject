package application;

import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.util.Duration;

public class AnimatedTransactionLogTableRow<T> extends TableRow<T> {

    private static final PseudoClass PS_NEW = PseudoClass.getPseudoClass("new-row");
    private static final PseudoClass PS_FLASH = PseudoClass.getPseudoClass("flash-row");
    private static final PseudoClass PS_CF = PseudoClass.getPseudoClass("cell-positive");

    
    private final ObjectExpression<T> recentItem;
    private final InvalidationListener recentlyAddedListener = fObs -> recentItemChanged();

    private final Function<T, BooleanExpression> flashExtractor;
    private final ChangeListener<Boolean> flashListener = (fObs, fOld, fNew) -> flasherChanged(fNew);
    private final Timeline flashTimeline;

    public AnimatedTransactionLogTableRow(ObjectExpression<T> fRecentlyAddedProperty
    									 ,Function<T, BooleanExpression> fFlashExtractor
    									) {
        recentItem = fRecentlyAddedProperty;
        recentItem.addListener(new WeakInvalidationListener(recentlyAddedListener));
        
        
        flashExtractor = fFlashExtractor;
        flashTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_FLASH, true)),
                new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_FLASH, false)));
        flashTimeline.setCycleCount(Animation.INDEFINITE);
        
        setOnMouseClicked(event -> {
        	if(isEmpty()){
          	  getTableView().getSelectionModel().clearSelection();
        	}
        });
        
    }

    
    private void flasherChanged(boolean fNew) {
        if (fNew) {
            flashTimeline.play();
            pseudoClassStateChanged(PS_CF,true);
        } else {
            flashTimeline.stop();
            pseudoClassStateChanged(PS_FLASH, false);
            pseudoClassStateChanged(PS_CF,false);

        }
    }
    

    private void recentItemChanged() {
        final T tmpRecentItem = recentItem.get();
        pseudoClassStateChanged(PS_NEW, tmpRecentItem != null && tmpRecentItem == getItem());
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        if (getItem() != null) {
            final BooleanExpression be = flashExtractor.apply(getItem());
            if (be != null) {
                be.removeListener(flashListener);
            }
        }

        super.updateItem(item, empty);

        if(getItem() == null || empty){
        	flasherChanged(false);
        } else  if (getItem() != null) {
            final BooleanExpression be = flashExtractor.apply(getItem());
            if (be != null) {
                be.addListener(flashListener);
                flasherChanged(be.get());
            }
        }
        recentItemChanged();
    }
    
}