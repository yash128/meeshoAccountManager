import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class sql {
    public Connection conn;
    public boolean open() {
        try {
            String CONNECTION_STRING = "jdbc:sqlite:D:\\accounts\\data.db";
            conn = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        }catch (SQLException e){
            System.out.println("could not connect to database");
            return false;
        }
    }
    public void close() {
        try {
            if (conn!=null)
                conn.close();
        }catch (SQLException ignored){

        }
    }
    public void addIntoMain(allData data,int num){
        String str;
        if (num==1){
            str = "radheProducts";
        }else {
            str = "amritTrading";
        }
        String addData = "INSERT INTO " + str + " (orderId,rate,quantity,SKU,date) VALUES (\""+data.getOrderId()+"\","+data.getRate()+","
                +data.getQuantity()+",\""+data.getSKU()+"\",\""+data.getDate()+"\")";
        try {
            conn.prepareStatement(addData).execute();
            System.out.println("data added successfully");
        }catch (SQLException e){
            System.out.println("error occurred");
            System.out.println(e.getMessage());
        }
    }
}
