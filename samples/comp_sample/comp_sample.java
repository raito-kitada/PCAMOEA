package comp_sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class comp_sample {
    private static ObjectMapper mapper = new ObjectMapper();

    public static double objective(Object x) {
        // Encode x to the json format
        String json_x;
      	try {
	          json_x = mapper.writeValueAsString(x);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Double.NaN;
        }

        // Submit a solution
        String path = "solution.json";
        try{
            File file = new File(path);
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(json_x);
            filewriter.close();
        } catch (IOException e) {
            System.out.println(e);
            return Double.NaN;
        }
        
//        String cmd = "opt submit --match=1 --solution=" + path;
        String cmd = "./dummy.sh";
        String json = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                json += line + "\n";
            }
      	    proc.waitFor();   
      	} catch (RuntimeException | IOException | InterruptedException e) {
	          e.printStackTrace();
      	}
        
        // Decode the json file to a java object
        double y;
        try {
            y = mapper.readTree(json).get("objective").doubleValue();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Double.NaN;
        }
    	
        return y;
    }

    public static void main(String[] args) {
        double f = objective("[1.5, 2.3]");
        System.out.println("f(x) = " + f);
    }
}
