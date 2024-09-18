package com.himanshu.emailservice.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    
    public static List<String> readEmailsFromCSV(String fileName) {
        List<String> emails = new ArrayList<>();
        String line = "";
        String csvSplitBy = ",";  

        // Determine the path to the CSV file
        String filePath = Paths.get("").toAbsolutePath().resolve(fileName).toString();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();  // Skip header line
            
            while ((line = br.readLine()) != null) {
                // Split the line by commas
                String[] values = line.split(csvSplitBy);
                
                // Assuming the third column is the email
                if (values.length >= 3) {
                    emails.add(values[2].trim());  
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emails;
    }
}
