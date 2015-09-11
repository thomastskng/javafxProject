package application;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of trades. This is used for saving the
 * list of trades to XML.
 * 
 */
@XmlRootElement(name = "trades")
public class TradeListWrapper {

    private List<Trade> trades;

    @XmlElement(name = "trade")
    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }
}