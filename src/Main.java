import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
public class Main {
    private static ArrayList<allData> data;
    private static final sql sql = new sql();
    public static void main(String[] args) {
        boolean opened = sql.open();
        data = new ArrayList<>();
        if(!opened) {return; }
        if (args.length == 1) {
            new Thread(Main::fileCheck).start();
            new Thread(Main::putIntoMain).start();
            putIntoMain();
        }
        else {
            while (true) {
                Scanner input = new Scanner(System.in);
                System.out.println("Press 1 to get file PDF");
                System.out.println("Press 2 for received");
                System.out.println("Press 3 to cancel product");
                System.out.print("directly enter order_id to add products :");
                String check;
                check = input.nextLine();
                if(check.equals("1") || check.equals("2") || check.equals("3")){
                    System.out.print("Enter order id : ");
                    String id = input.nextLine();
                    if (check.equals("1")) getFile(id);
                    if (check.equals("2")) received(id);
                    if (check.equals("3")) cancel(id);
                }else {
                    add(check);
                }
            }
        }
    }

    private static void putIntoMain(){
        while (true) {
            int quantity = 0;
            String orderNo = "";
            float amnt = 0;
            String SKU = "";
            PDFManager manager = new PDFManager();
            String path="";
            try {
                Thread.sleep(5000);
                path = array.take();
                String location = "D:\\accounts\\" + path;
                manager.setFilePath(location);
                System.out.println("take");
                ArrayList<String> list = manager.toText();
                for (String str : list){
                    Scanner scan = new Scanner(str);
                    String line;
                    while (scan.hasNext()) {
                        line = scan.nextLine().replaceAll(" ", "");
                        if (line.contains("TotalRs.")) {
                            amnt = Float.parseFloat(line.replaceAll("[^0-9]", "")) / 100;
                            continue;
                        }
                        if (line.contains("SKU:")) {
                            SKU = line.replace("SKU:", "");
                            continue;
                        }
                        if (line.contains("Quantity")) {
                            quantity = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            continue;
                        }
                        if (line.contains("OrderNumber:")) {
                            orderNo = line.replace("OrderNumber:", "").replaceAll("_[0-9]", "");
                        }
                    }
                    if (!SKU.equals("")) {
                        sqlMain(orderNo, amnt, quantity, SKU,path);
                    }
                }
            } catch (IOException e) {
                if (!path.equals("")){
                    try {
                        array.put(path);
                    } catch (InterruptedException ignored) {
                        writeError("error occurred in IOException : " + e.getMessage() + " :- pdf scan area " + orderNo);
                    }
                }
            } catch (InterruptedException f) {
                writeError("error occurred in Interrupted : " + f.getMessage() + " :- pdf scan area " + orderNo);
            } catch (Exception d) {
                d.printStackTrace();
                writeError(d.getMessage() + " pdf scan area " + orderNo + " " + SKU);
            }
        }
    }

    private static void add(String id){
        if (id.equals("")){
            System.out.println("does not entered anything");
            return;
        }
        Scanner input = new Scanner(System.in);
        allData info = null;
        try (Statement statement = sql.conn.createStatement()){
            ResultSet set = statement.executeQuery("SELECT * FROM whole where orderId like \""+id+"%\"");
            while (set.next()){
                info = new allData(set.getString("orderId"), set.getString("SKU"), set.getInt("rate"),set.getInt("quantity"),set.getString("dateOfDownload"));
                System.out.println(info.getOrderId() + "   " + info.getQuantity() + "  " + info.getRate() + "  " + info.getSKU());
                data.add(info);
            }
        }catch(SQLException e){
            System.out.println("error occurred");
            System.out.println(e.getMessage());
        }
        if (data.size()==0){
            System.out.println("no data found");
        }else if(data.size()==1){
            if (search(info.getOrderId())==0) {
                System.out.println("Enter 1 for Radhe Products :");
                System.out.println("Enter 2 for amrit trading :");
                int c = input.nextInt();
                if (c == 1 || c == 2) {
                    sql.addIntoMain(info, c);
                } else {
                    System.out.println("error!! enter 1 or 2 only");
                }
                data.clear();
            }else {
                System.out.println("data already exists");
            }
        }else {
            System.out.print("please enter more digits : ");
            data.clear();
            add(input.nextLine());
        }
    }

    public static int search(String id){
        try {
            ResultSet set = sql.conn.prepareStatement("SELECT id from radheProducts where orderId==\""+id+"\"").executeQuery();
            ResultSet set2 = sql.conn.prepareStatement("SELECT id from amritTrading where orderId==\""+id+"\"").executeQuery();
            if (!set2.isClosed()){
                return 2;//found in amrit
            }
            if (!set.isClosed()){
                return 1;//found in radhe
            }
            return 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    private static void getFile(String id){
        File f = new File("D:\\javaProject\\Accounts\\accessFile.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(f))){
            String s;
            while ((s=reader.readLine())!=null){
                String[] data = s.split("/");
                if (id.equals(data[0])){
                    System.out.println("opening file");
                    if (Desktop.isDesktopSupported()){
                        try {
                            File g = new File("D:\\accounts\\" + data[1]);
                            Desktop.getDesktop().open(g);
                        }catch (IOException e){
                            e.printStackTrace();
                            System.out.println("error while opening file file name is : " + data[1]);
                        }
                    }
                    return;
                }
            }
            System.out.println("no such id found");
        }catch (IOException e){
            System.out.println("error occurred : " + e.getMessage());
        }
    }

    private static void received(String id){
        try {
            int a = search(id);
            if (a==1){
                sql.conn.prepareStatement("UPDATE radheProducts SET received=\"yes\" where orderId==\""+id+"\"").execute();
                System.out.println("updated successfully");
            } else if (a==2) {
                sql.conn.prepareStatement("UPDATE amritTrading SET received=\"yes\" where orderId==\""+id+"\"").execute();
                System.out.println("updated successfully");
            }else {
                System.out.println("id does not exists");
            }
        }catch (SQLException e){
            writeError("error in putting received value " + e.getMessage() + " " + id);
        }
    }

    private static void cancel(String id){
        int search = search(id);
        System.out.print("Press 1 for RTO.\nPress 2 if cancelled : ");
        Scanner s = new Scanner(System.in);
        int inp = s.nextInt();
        try {
            if(inp==1) {
                if (search == 1) {
                    sql.conn.prepareStatement("UPDATE radheProducts SET cancel=\"RTO\" where orderId==\"" + id + "\"").execute();
                    System.out.println("updated successfully");
                } else if (search == 2) {
                    sql.conn.prepareStatement("UPDATE amritTrading SET cancel=\"RTO\" where orderId==\"" + id + "\"").execute();
                    System.out.println("updated successfully");
                } else {
                    System.out.println("id does not exists.");
                }
            }
            if (inp==2){
                if (search == 1) {
                    sql.conn.prepareStatement("UPDATE radheProducts SET cancel=\"c\" where orderId==\"" + id + "\"").execute();
                    System.out.println("updated successfully");
                } else if (search == 2) {
                    sql.conn.prepareStatement("UPDATE amritTrading SET cancel=\"c\" where orderId==\"" + id + "\"").execute();
                    System.out.println("updated successfully");
                } else {
                    System.out.println("id does not exists.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void sqlMain(String orderId,float rate,int quantity,String SKU,String path){
        String date = LocalDate.now().toString();
        try {
            String str = "INSERT INTO whole(orderId,quantity,rate,SKU,dateOfDownload) VALUES(\""+orderId+"\","+quantity+","+rate+",\""+SKU+"\",\""+date+"\")";
            System.out.println(str);
            sql.conn.prepareStatement(str).execute();
            writeFile(orderId + "/" + path);
        }catch (SQLException e){
            if(!e.getMessage().contains("SQLITE_CONSTRAINT_PRIMARYKEY")) {
                writeError("error in inserting main data for order number :" + orderId + ".Message is " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void writeError(String str){
        File f = new File("D:\\javaProject\\Accounts\\error.txt");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(f,true))){
            System.out.println(str + "   " + LocalDate.now());
            w.write(str+"\n");
        }catch (IOException g){
            System.out.println("error while writing");
        }
    }
    private static void writeFile(String str){
        File f = new File("D:\\javaProject\\Accounts\\accessFile.txt");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(f,true))){
            System.out.println(str);
            w.write(str+"\n");
        }catch (IOException g){
            System.out.println("error while writing");
        }
    }


    private static final ArrayBlockingQueue<String> array = new ArrayBlockingQueue<>(90);
    public static void fileCheck() {
        ArrayList<String> arr = new ArrayList<>();
        try(WatchService service = FileSystems.getDefault().newWatchService()){
            Map<WatchKey, Path> keyMap = new HashMap<>();
            Path path = Paths.get("D:\\accounts");
            keyMap.put(path.register(service,StandardWatchEventKinds.ENTRY_CREATE), path);
            WatchKey watchKey;
            do {
                if (!arr.isEmpty()){
                    for (String s : arr){
                        array.put(s);
                    }
                }
                arr.clear();
                watchKey = service.take();
                Path eventDir = keyMap.get(watchKey);
                for (WatchEvent event : watchKey.pollEvents()){
                    WatchEvent.Kind kind = event.kind();
                    Path eventPath = (Path) event.context();
                    System.out.println(eventDir + ": " + kind + ": " + eventPath);
                    if (eventPath.toString().contains(".pdf"))
                        arr.add(eventPath.toString());
                }
            }while (watchKey.reset());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
