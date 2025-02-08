public class allData {
    private final String orderId;
    private final String SKU;
    private final int rate;
    private final int quantity;
    private final String date;

    public allData(String orderId, String SKU, int rate, int quantity,String date) {
        this.orderId = orderId;
        this.SKU = SKU;
        this.rate = rate;
        this.quantity = quantity;
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public String getSKU() {
        return SKU;
    }

    public int getRate() {
        return rate;
    }

    public int getQuantity() {
        return quantity;
    }

}
