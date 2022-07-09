package lab.moea.history;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AOSHistory {
	private String path = "output/";
	private String SEP = ",";
	
	public void setPath(String path) {
		this.path = path;

		try {
			Path p = Paths.get(path);
			if (!Files.exists(p)) {
				Files.createDirectories(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public String getPath() {
		return path;
	}	
	
	public void WriteHistory(ArrayList<AOSHistoryInfo> historyinfo, Set<String> oNames, String fname) {
		try {
			BufferedWriter history = new BufferedWriter(new FileWriter(new File(path + fname), false));
			
			history.write("NFE");
			for (String oName : oNames) {
				history.write(SEP);
				history.write(oName);
			}
			history.newLine();
			
			for (AOSHistoryInfo entry : historyinfo) {
				history.write(Integer.toString(entry.nfe));
				for (String oName : oNames) {
					history.write(SEP);
					history.write(Double.toString(entry.value.get(oName)));
				}
				history.newLine();
			}
			
			history.flush();
			
		} catch (IOException e) {
			Logger.getLogger("WriteCreditHistory").log(Level.SEVERE, null, e);
		}
	}
}
