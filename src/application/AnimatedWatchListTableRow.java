package application;
import java.util.function.Function;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.util.Duration;
import javafx.beans.binding.IntegerExpression;

public class AnimatedWatchListTableRow<T> extends TableRow<T> {
    private static final PseudoClass PS_NEW = PseudoClass.getPseudoClass("new-row");
    private static final PseudoClass PS_LastLessThanEqualToTarget_FLASH = PseudoClass.getPseudoClass("ticker-LastLessThanEqualToTarget-flash-row");
    private static final PseudoClass PS_LastLessThanEqualToTarget_HL = PseudoClass.getPseudoClass("ticker-LastLessThanEqualToTarget-highlight");
    private static final PseudoClass PS_LastGreaterThanEqualToTarget_FLASH = PseudoClass.getPseudoClass("ticker-LastGreaterThanEqualToTarget-flash-row");
    private static final PseudoClass PS_LastGreaterThanEqualToTarget_HL = PseudoClass.getPseudoClass("ticker-LastGreaterThanEqualToTarget-highlight");
 
    private final ObjectExpression<T> recentItem;
    private final InvalidationListener recentlyAddedListener = fObs -> recentItemChanged();

    private final Function<T, BooleanExpression> alertExtractor;
    private final Function<T, StringExpression> conditionExtractor;
    
    private final ChangeListener<Boolean> alertListener = (fObs, fOld, fNew) -> alertChanged(fNew);
    private final Timeline alertLastLessThanEqualToTargetFlashTimeline;
    private final Timeline alertLastGreaterThanEqualToTargetFlashTimeline;
    
    public AnimatedWatchListTableRow(ObjectExpression<T> fRecentlyAddedProperty,Function<T, BooleanExpression> alertExtractor, Function<T, StringExpression> conditionExtractor) {
        recentItem = fRecentlyAddedProperty;
        recentItem.addListener(new WeakInvalidationListener(recentlyAddedListener));

        this.alertExtractor = alertExtractor;
        this.conditionExtractor = conditionExtractor;

        
        alertLastLessThanEqualToTargetFlashTimeline = new Timeline(
        		new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_LastLessThanEqualToTarget_HL, false)),
                new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_LastLessThanEqualToTarget_FLASH, true)),
        		new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_LastLessThanEqualToTarget_HL, true)),
                new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_LastLessThanEqualToTarget_FLASH, false)));
        alertLastLessThanEqualToTargetFlashTimeline.setCycleCount(Animation.INDEFINITE);
        
        alertLastGreaterThanEqualToTargetFlashTimeline = new Timeline(
        		new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_HL, false)),
                new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_FLASH, true)),
        		new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_HL, true)),
                new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_FLASH, false)));
        alertLastGreaterThanEqualToTargetFlashTimeline.setCycleCount(Animation.INDEFINITE);
    }
    
    private void alertChanged(boolean fNew) {
        if (fNew) {
        	if(conditionExtractor.apply(getItem()).get().equals("Last <= Target")){
            	System.out.println("GREEEN!!!!!!!!!!!!!!!!!!!!");
                alertLastGreaterThanEqualToTargetFlashTimeline.stop();
                pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_FLASH, false);
                pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_HL,false);
        		alertLastLessThanEqualToTargetFlashTimeline.play();
        	} else{
        		alertLastLessThanEqualToTargetFlashTimeline.stop();
                pseudoClassStateChanged(PS_LastLessThanEqualToTarget_FLASH, false);
                pseudoClassStateChanged(PS_LastLessThanEqualToTarget_HL,false);
                alertLastGreaterThanEqualToTargetFlashTimeline.play();
                
        	}
        } else {

                alertLastGreaterThanEqualToTargetFlashTimeline.stop();
                pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_FLASH, false);
                pseudoClassStateChanged(PS_LastGreaterThanEqualToTarget_HL,false);
        		alertLastLessThanEqualToTargetFlashTimeline.stop();
                pseudoClassStateChanged(PS_LastLessThanEqualToTarget_FLASH, false);
                pseudoClassStateChanged(PS_LastLessThanEqualToTarget_HL,false);
        }
    }
    
    private void recentItemChanged() {
        final T tmpRecentItem = recentItem.get();
        pseudoClassStateChanged(PS_NEW, tmpRecentItem != null && tmpRecentItem == getItem());
    }
    
    @Override
    protected void updateItem(T item, boolean empty) {
    	// remove listener
        if (getItem() != null) {
            final BooleanExpression alertBE = alertExtractor.apply(getItem());
            if (alertBE != null) {
            	alertBE.removeListener(alertListener);
            }


        }

        super.updateItem(item, empty);

        // add listener
        if(getItem() == null || empty){
        	alertChanged(false);

        } else if (getItem() != null) {
            final BooleanExpression alertBE = alertExtractor.apply(getItem());
            if (alertBE != null) {
            	alertBE.addListener(alertListener);
                alertChanged(alertBE.get());
            }

        }
        recentItemChanged();
    }
}
