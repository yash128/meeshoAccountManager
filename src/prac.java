import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class prac {
    public static void main(String[] args) {
        int quantity = 0;
        String orderNo = "";
        float amnt = 0;
        String SKU = "";ArrayList<String> awb = new ArrayList<>();
        PDFManager manager = new PDFManager();
        String location = "C:\\Users\\sanjiv\\Downloads\\3a354c.pdf";
        manager.setFilePath(location);
        try {
            ArrayList<String> list = manager.toText();
            int a = 0;
            for (String str : list) {
                Scanner scan = new Scanner(str);
                String line;
                a++;
                while (scan.hasNext()) {
                    line = scan.nextLine().replaceAll(" ", "");
                    if (checkNum(line)){
                        awb.add(line);
                        continue;
                    }
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
                if (!SKU.isEmpty()){
                System.out.println(a + " =>" + orderNo + "  " + amnt  + "  " + quantity + " " + SKU + "  " + awb);
                awb.clear();}
                SKU = "";
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static boolean checkNum(String s){
        try {
            Long.parseLong(s);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
