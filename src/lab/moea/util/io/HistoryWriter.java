package lab.moea.util.io;

public class HistoryWriter {
	static private String SEP = ",";
	
	static private String path;
	
	static public void setPath(String path) {
		HistoryWriter.path = path;
	}
	
	static public String getPath() {
		return HistoryWriter.path;
	}	

}
