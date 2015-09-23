package application;

import java.util.function.Function;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import javafx.beans.binding.IntegerExpression;
import javafx.scene.input.MouseEvent;

public class AnimatedPortfolioTableRow<T> extends TableRow<T> {

    private static final PseudoClass PS_NEW = PseudoClass.getPseudoClass("new-row");
    private static final PseudoClass PS_TARGET_FLASH = PseudoClass.getPseudoClass("target-flash-row");
    private static final PseudoClass PS_TARGET_HL = PseudoClass.getPseudoClass("target-highlight");
    private static final PseudoClass PS_STOPLOSS_FLASH = PseudoClass.getPseudoClass("stopLoss-flash-row");
    private static final PseudoClass PS_STOPLOSS_HL = PseudoClass.getPseudoClass("stopLoss-highlight");
    private static final PseudoClass PS_PNL_LOSS_HL = PseudoClass.getPseudoClass("pnl-loss-highlight");
    private static final PseudoClass PS_PNL_PROFIT_HL = PseudoClass.getPseudoClass("pnl-profit-highlight");
    private static final PseudoClass PS_PNL_NEUTRAL_HL = PseudoClass.getPseudoClass("pnl-neutral-highlight");
    private static final PseudoClass PS_UPNL_LOSS_HL = PseudoClass.getPseudoClass("uPnl-loss-highlight");
    private static final PseudoClass PS_UPNL_PROFIT_HL = PseudoClass.getPseudoClass("uPnl-profit-highlight");
    private static final PseudoClass PS_UPNL_NEUTRAL_HL = PseudoClass.getPseudoClass("uPnl-neutral-highlight");
    
    
    private final ObjectExpression<T> recentItem;
    private final InvalidationListener recentlyAddedListener = fObs -> recentItemChanged();

    private final Function<T, StringExpression> tickerExtractor;
    private final Function<T, BooleanExpression> targetExtractor;
    private final Function<T, BooleanExpression> stopLossExtractor;
    private final Function<T, IntegerExpression> uPnlExtractor;
    private final Function<T, IntegerExpression> pnlExtractor;
    
    private final ChangeListener<Boolean> targetListener = (fObs, fOld, fNew) -> targetChanged(fNew);
    private final ChangeListener<Boolean> stopLossListener = (fObs, fOld, fNew) -> stopLossChanged(fNew);
    private final ChangeListener<Number> uPnlListener = (fObs, fOld, fNew) -> uPnlChanged(fNew);
    private final ChangeListener<Number> pnlListener = (fObs, fOld, fNew) -> pnlChanged(fNew);

    private TabPane fxTabPaneLower;
    
    /**
     * The data as an observable list of Trade.
     */
	private ObservableList<Trade> observableListOfTrades;
	private FilteredList<Trade> filterListOfTrades;
	
    private final Timeline targetFlashTimeline;
    private final Timeline stopLossFlashTimeline;


    public AnimatedPortfolioTableRow(ObjectExpression<T> fRecentlyAddedProperty,Function<T,StringExpression> tickerExtractor,Function<T, BooleanExpression> targetExtractor, Function<T, BooleanExpression> stopLossExtractor, Function<T,IntegerExpression> uPnlExtractor, Function<T,IntegerExpression> pnlExtractor, ObservableList<Trade> observableListOfTrades, FilteredList<Trade> filterListOfTrades, TabPane fxTabPaneLower) {
        recentItem = fRecentlyAddedProperty;
        recentItem.addListener(new WeakInvalidationListener(recentlyAddedListener));

        this.tickerExtractor = tickerExtractor;
        this.targetExtractor = targetExtractor;
        this.stopLossExtractor = stopLossExtractor;
        this.pnlExtractor = pnlExtractor;
        this.uPnlExtractor = uPnlExtractor;
        
        this.fxTabPaneLower = fxTabPaneLower;
        
        this.observableListOfTrades = observableListOfTrades;
        this.filterListOfTrades = filterListOfTrades;
       
        targetFlashTimeline = new Timeline(
        		new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_TARGET_HL, false)),
                new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_TARGET_FLASH, true)),
        		new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_TARGET_HL, true)),
                new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_TARGET_FLASH, false)));
        targetFlashTimeline.setCycleCount(Animation.INDEFINITE);
        
        stopLossFlashTimeline = new Timeline(
        		new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_STOPLOSS_HL, false)),
                new KeyFrame(Duration.seconds(0.5), e -> pseudoClassStateChanged(PS_STOPLOSS_FLASH, true)),
        		new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_STOPLOSS_HL, true)),
                new KeyFrame(Duration.seconds(1.0), e -> pseudoClassStateChanged(PS_STOPLOSS_FLASH, false)));
        stopLossFlashTimeline.setCycleCount(Animation.INDEFINITE);
        
        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!isEmpty()) ) {
                final StringExpression tickerBE = tickerExtractor.apply(getItem());
                filterListOfTrades.setPredicate(trade -> trade.getStockTicker().equals(tickerBE.get()));
                System.out.println(tickerBE.get());
                
                fxTabPaneLower.getSelectionModel().select(1);
                fxTabPaneLower.getTabs().get(1).setText("Filtered History: " + tickerBE.get());  
                //.setItems(filterListOfTrades)
                ((TableView<Trade>)fxTabPaneLower.getTabs().get(1).getContent()).setItems(filterListOfTrades);
                System.out.println(((TableView<Trade>)fxTabPaneLower.getTabs().get(1).getContent()).getItems().size());
                System.out.println(filterListOfTrades.size());
                System.out.println(observableListOfTrades.size());
          } else if(isEmpty()){
        	  getTableView().getSelectionModel().clearSelection();
          }
        });
    }

    private void targetChanged(boolean fNew) {
        if (fNew) {
        	targetFlashTimeline.play();
            
        } else {
        	targetFlashTimeline.stop();
            pseudoClassStateChanged(PS_TARGET_FLASH, false);
            pseudoClassStateChanged(PS_TARGET_HL,false);

        }
    }

    private void stopLossChanged(boolean fNew) {
        if (fNew) {
        	stopLossFlashTimeline.play();
            
        } else {
            pseudoClassStateChanged(PS_STOPLOSS_FLASH, false);
        	stopLossFlashTimeline.stop();
            pseudoClassStateChanged(PS_STOPLOSS_HL,false);

        }
    }
    
    private void pnlChanged(Number fNew) {
    	//System.out.println("PNL:");
        if ((int) fNew >0) {
        	//System.out.println(fNew + " > 0");
        	pseudoClassStateChanged(PS_PNL_PROFIT_HL, true);
        	pseudoClassStateChanged(PS_PNL_NEUTRAL_HL, false);
        	pseudoClassStateChanged(PS_PNL_LOSS_HL, false);
        } else if((int) fNew < 0 ){
        	//System.out.println(fNew + " < 0");
        	pseudoClassStateChanged(PS_PNL_LOSS_HL, true);
        	pseudoClassStateChanged(PS_PNL_NEUTRAL_HL, false);
            pseudoClassStateChanged(PS_PNL_PROFIT_HL, false);
        } else{
        	//System.out.println(fNew + " = 0");
            pseudoClassStateChanged(PS_PNL_NEUTRAL_HL, true);
        	pseudoClassStateChanged(PS_PNL_LOSS_HL, false);
            pseudoClassStateChanged(PS_PNL_PROFIT_HL, false);
        }
    }
    
    private void uPnlChanged(Number fNew) {
    	//System.out.println("U_PNL:");

        if ((int) fNew >0) {
        	//System.out.println(fNew + " > 0");
        	pseudoClassStateChanged(PS_UPNL_PROFIT_HL, true);
        	pseudoClassStateChanged(PS_UPNL_NEUTRAL_HL, false);
        	pseudoClassStateChanged(PS_UPNL_LOSS_HL, false);
        } else if((int) fNew < 0 ){
        	//System.out.println(fNew + " < 0");
        	pseudoClassStateChanged(PS_UPNL_LOSS_HL, true);
        	pseudoClassStateChanged(PS_UPNL_NEUTRAL_HL, false);
            pseudoClassStateChanged(PS_UPNL_PROFIT_HL, false);
        } else{
        	//System.out.println(fNew + " = 0");
            pseudoClassStateChanged(PS_UPNL_NEUTRAL_HL, true);
        	pseudoClassStateChanged(PS_UPNL_LOSS_HL, false);
            pseudoClassStateChanged(PS_UPNL_PROFIT_HL, false);
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
            final BooleanExpression targetBE = targetExtractor.apply(getItem());
            final BooleanExpression stopLossBE = stopLossExtractor.apply(getItem());
            final IntegerExpression pnlIE = pnlExtractor.apply(getItem());
            final IntegerExpression uPnlIE = uPnlExtractor.apply(getItem());
            
            
            if (targetBE != null) {
            	targetBE.removeListener(targetListener);
            }
            if(stopLossBE != null){
            	stopLossBE.removeListener(stopLossListener);
            }
            if(pnlIE != null){
            	pnlIE.removeListener(pnlListener);
            }
            if(uPnlIE != null){
            	uPnlIE.removeListener(uPnlListener);
            }
        }

        super.updateItem(item, empty);

        // add listener
        if(getItem() == null || empty){
        	targetChanged(false);
        	stopLossChanged(false);
        	uPnlChanged(0);
        	pnlChanged(0);
        } else if (getItem() != null) {
            final BooleanExpression targetBE = targetExtractor.apply(getItem());
            final BooleanExpression stopLossBE = stopLossExtractor.apply(getItem());
            final IntegerExpression pnlIE = pnlExtractor.apply(getItem());
            final IntegerExpression uPnlIE = uPnlExtractor.apply(getItem());
 

            if (targetBE != null) {
            	targetBE.addListener(targetListener);
                targetChanged(targetBE.get());
            }
            if(stopLossBE != null){
            	stopLossBE.addListener(stopLossListener);
            	stopLossChanged(stopLossBE.get());
            }
            if(pnlIE != null){
            	pnlIE.addListener(pnlListener);
            	pnlChanged(pnlIE.get());
            }
            if(uPnlIE != null){
            	uPnlIE.addListener(uPnlListener);
            	uPnlChanged(uPnlIE.get());
            }
        }
        recentItemChanged();
    }
    
    	
    
}