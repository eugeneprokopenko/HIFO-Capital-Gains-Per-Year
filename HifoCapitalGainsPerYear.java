


/* In summary, this program reads stock purchases and sales from two CSV files, 
"purchases.csv" and "sales.csv". It then calculates the capital gains/loss per 
year using the HIFO (highest-in, first-out) method. If the total sold stock units 
are more than the total purchased stock units, the program prints a message and 
terminates. The program uses a PriorityQueue to maintain the order of purchases 
by price in descending order, and it processes each sale by selling the 
highest-priced stock first. The capital gains/loss per year is stored in a HashMap 
and printed at the end. */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class HifoCapitalGainsPerYear {

    // Define a static class to represent a stock purchase with timestamp, amount, and price.
    static class StockPurchase {
        LocalDateTime timestamp;
        double amount;
        double price;

        StockPurchase(LocalDateTime timestamp, double amount, double price) {
            this.timestamp = timestamp;
            this.amount = amount;
            this.price = price;
        }
    }

    // Define a static class to represent a stock sale with timestamp, amount, and price.
    static class StockSale {
        LocalDateTime timestamp;
        double amount;
        double price;

        StockSale(LocalDateTime timestamp, double amount, double price) {
            this.timestamp = timestamp;
            this.amount = amount;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        List<StockPurchase> purchases = readPurchases("purchases.csv");
        List<StockSale> sales = readSales("sales.csv");

        // Check if the total sold stock units are more than the total purchased stock units.
        double totalSoldUnits = sales.stream().mapToDouble(sale -> sale.amount).sum();
        double totalPurchasedUnits = purchases.stream().mapToDouble(purchase -> purchase.amount).sum();
        

        if (totalSoldUnits > totalPurchasedUnits) {
            System.out.println("Total sold stock units (" + totalSoldUnits + ") are more than the total purchased stock units (" + totalPurchasedUnits + ")");
            return;
        }

        Map<Integer, Double> hifoGainsPerYear = calculateHifoCapitalGainsPerYear(purchases, sales);
        for (Map.Entry<Integer, Double> entry : hifoGainsPerYear.entrySet()) {
            System.out.printf("Year: %d, Capital gains/losses: $%.2f%n", entry.getKey(), entry.getValue());
        }
    }

    // Read the purchase records from a CSV file and return a list of StockPurchase objects.
    public static List<StockPurchase> readPurchases(String fileName) {
        List<StockPurchase> purchases = new ArrayList<>();
        
        // Define the DateTimeFormatter for parsing the timestamp strings.
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yy H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                LocalDateTime timestamp = LocalDateTime.parse(data[0], dateFormatter);
                double amount = Double.parseDouble(data[1]);
                double price = Double.parseDouble(data[2]);
                purchases.add(new StockPurchase(timestamp, amount, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    // Read the sale records from a CSV file and return a list of StockSale objects.
    public static List<StockSale> readSales(String fileName) {
        List<StockSale> sales = new ArrayList<>();
        
        // Define the DateTimeFormatter for parsing the timestamp strings.
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yy H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                LocalDateTime timestamp = LocalDateTime.parse(data[0], dateFormatter);
                double amount = Double.parseDouble(data[1]);
                double price = Double.parseDouble(data[2]);
                sales.add(new StockSale(timestamp, amount, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }

    
    // Calculate the HIFO capital gains/loss per year for the provided purchases and sales.
    public static Map<Integer, Double> calculateHifoCapitalGainsPerYear(List<StockPurchase> purchases, List<StockSale> sales) {
        
        // Sort purchases by price in descending order.
        PriorityQueue<StockPurchase> purchaseQueue = new PriorityQueue<>(
                (a, b) -> Double.compare(b.price, a.price)
        );
        purchaseQueue.addAll(purchases);

        Map<Integer, Double> gainsPerYear = new HashMap<>();
        for (StockSale sale : sales) {
            double remainingAmount = sale.amount;
            while (remainingAmount > 0) {
                if (purchaseQueue.isEmpty()) {
                    throw new RuntimeException("Not enough purchase records to cover sale");
                }

                StockPurchase highestPurchase = purchaseQueue.peek();
                double quantityToSell = Math.min(highestPurchase.amount, remainingAmount);
                double gain = (sale.price - highestPurchase.price) * quantityToSell;
                int year = sale.timestamp.getYear();

                gainsPerYear.put(year, gainsPerYear.getOrDefault(year, 0.0) + gain);

                remainingAmount -= quantityToSell;
                highestPurchase.amount -= quantityToSell;

                if (highestPurchase.amount == 0) {
                    purchaseQueue.poll();
                }
            }
        }

        return gainsPerYear;
    }
}

