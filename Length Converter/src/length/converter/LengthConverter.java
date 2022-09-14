/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package length.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Wonarche 
 */
public class LengthConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // display a welcome message
        System.out.println("Welcome to the Length Converter\n");

        // continue until the user enters 4
        int menuNumber = 0;
        while (menuNumber != 4) {
            // display menu
            System.out.println("1 - Convert a length");
            System.out.println("2 - Add a type of conversion");
            System.out.println("3 - Delete a type of conversion");
            System.out.println("4 - Exit\n");

            // get input from user
            menuNumber = Console.getInt("Enter menu number: ", 0, 5);
            System.out.println();

            switch (menuNumber) {
                case 1: {
                    // read the conversion list from the file
                    ArrayList<Conversion> conversions
                            = ConversionIO.getConversions();

                    // display the list of conversions
                    displayConversions(conversions);

                    // get menu choice from user
                    int conversionNumber = Console.getInt(
                            "Enter conversion number: ", 
                            0, conversions.size() + 1);
                    System.out.println();

                    // get the Conversion object
                    int index = conversionNumber - 1;
                    Conversion c = conversions.get(index);

                    // get from value, calculate to value, and display result
                    double fromValue = Console.getDouble(
                            "Enter " + c.getFromUnit() + ": ");
                    c.setFromValue(fromValue);
                    System.out.println(c.getCalculationString());
                    System.out.println();

                    break;
                }
                case 2: {
                    // get data from user
                    String fromUnit = Console.getString(
                            "Enter 'From' unit: ");
                    String toUnit = Console.getString(
                            "Enter 'To' unit: ");
                    double ratio = Console.getDouble(
                            "Enter the conversion ratio: ");

                    // read the conversion types list from the file
                    ArrayList<Conversion> conversions
                            = ConversionIO.getConversions();

                    // create Conversion object and fill it with data
                    Conversion c = new Conversion(fromUnit, toUnit, ratio);

                    // add the Conversion objec to the array list
                    conversions.add(c);

                    // save the array list and display confirmation message
                    ConversionIO.saveConversions(conversions);
                    System.out.println();
                    System.out.println("This entry has been saved.\n");

                    break;
                }
                case 3: {
                    // read the conversion types list from the file
                    ArrayList<Conversion> conversions
                            = ConversionIO.getConversions();

                    // display the list with numbers
                    displayConversions(conversions);

                    // get data from user
                    int conversionNumber = Console.getInt(
                            "Enter number of conversion to delete: ", 
                            0, conversions.size() + 1);
                    int index = conversionNumber - 1;

                    // get the Conversion object
                    Conversion c = conversions.get(index);

                    // delete the Conversion objec
                    conversions.remove(index);

                    // save the array list and display confirmation message
                    ConversionIO.saveConversions(conversions);
                    System.out.println();
                    System.out.println(
                            c.getConversionString() + " has been deleted.\n");

                    break;
                }
                case 4: {
                    System.out.println("Goodbye.\n");
                    break;
                }
            }
        }
    }

    // display the conversions list with numbers
    private static void displayConversions(ArrayList<Conversion> conversions) {
        int i = 1;
        for (Conversion c : conversions) {
            System.out.println(i + " -  " + c.getConversionString());
            i++;
        }
        System.out.println();
    }
}
 class ConversionIO {

    private static final Path filename = Paths.get("conversion_types.txt");
    private static File file = filename.toFile();
    private static final String FIELD_SEP = "\t";

    public static ArrayList<Conversion> getConversions() {
        checkFile();

        try (BufferedReader in = new BufferedReader(
                                 new FileReader(file))) {

            // read each line in the file
            ArrayList<Conversion> typesList = new ArrayList<>();
            String line = in.readLine();
            while (line != null) {
                String[] columns = line.split(FIELD_SEP);
                String toUnit = columns[0];
                String fromUnit = columns[1];
                String ratio = columns[2];

                Conversion c = new Conversion(
                        toUnit, fromUnit, Double.parseDouble(ratio));
                typesList.add(c);

                line = in.readLine();
            }
            return typesList;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public static void saveConversions(ArrayList<Conversion> conversions) {
        checkFile();
        try (PrintWriter out = new PrintWriter(
                               new BufferedWriter(
                               new FileWriter(file)))) {

            for (Conversion c : conversions) {
                out.print(c.getFromUnit() + FIELD_SEP);
                out.print(c.getToUnit() + FIELD_SEP);
                out.print(c.getRatio());
                out.println();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void checkFile() {
        try {
            if (Files.notExists(filename)) {
                Files.createFile(filename);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
 class Conversion {

    private String fromUnit;
    private double fromValue;
    private String toUnit;
    private double toValue;
    private double ratio;

    public Conversion(String fromUnit, String toUnit, double ratio) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
        this.ratio = ratio;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public double getRatio() {
        return ratio;
    }

    public String getConversionString() {
        return fromUnit + " to " + toUnit + ": " + ratio;
    }
    
    public void setFromValue(double fromValue) {
        this.fromValue = fromValue;
    }

    public String getCalculationString() {
        this.toValue = fromValue * ratio;
        NumberFormat number = NumberFormat.getNumberInstance();
        number.setMaximumFractionDigits(4);
        String cs = fromValue + " " + fromUnit + " = "
                + number.format(toValue) + " " + toUnit;
        return cs;
    }
}

class Console {
    
    private static Scanner sc = new Scanner(System.in);

    public static String getString(String prompt) {
        String s = "";
        boolean isValid = false;
        while (!isValid) {
            System.out.print(prompt);
            s = sc.nextLine();
            if (s.isEmpty()) {
                System.out.println("Error! Required entry. Try again.");
            } else {
                isValid = true;
            }
        }
        return s;
    }

    public static int getInt(String prompt) {
        int i = 0;
        boolean isValid = false;
        while (!isValid) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                i = sc.nextInt();
                isValid = true;
            } else {
                System.out.println("Error! Invalid integer. Try again.");
            }
            sc.nextLine();  // discard any other data entered on the line
        }
        return i;
    }

    public static int getInt(String prompt, int min, int max) {
        int i = 0;
        boolean isValid = false;
        while (!isValid) {
            i = getInt(prompt);
            if (i <= min) {
                System.out.println(
                        "Error! Number must be greater than " + min + ".");
            } else if (i >= max) {
                System.out.println(
                        "Error! Number must be less than " + max + ".");
            } else {
                isValid = true;
            }
        }
        return i;
    }

    public static double getDouble(String prompt) {
        double d = 0;
        boolean isValid = false;
        while (!isValid) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                d = sc.nextDouble();
                isValid = true;
            } else {
                System.out.println("Error! Invalid number. Try again.");
            }
            sc.nextLine();  // discard any other data entered on the line
        }
        return d;
    }

    public static double getDouble(String prompt, double min, double max) {
        double d = 0;
        boolean isValid = false;
        while (!isValid) {
            d = getDouble(prompt);
            if (d <= min) {
                System.out.println(
                        "Error! Number must be greater than " + min + ".");
            } else if (d >= max) {
                System.out.println(
                        "Error! Number must be less than " + max + ".");
            } else {
                isValid = true;
            }
        }
        return d;
    }
}