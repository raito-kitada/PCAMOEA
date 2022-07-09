package smile_sample;

import java.io.IOException;
import java.net.URISyntaxException;

import smile.data.DataFrame;
import smile.io.*;

public class smile_sample1 {

	public static void main(String[] args) throws IOException, URISyntaxException {
		CSV csv = new CSV();
		DataFrame dat = csv.read("samples/smile_sample/pca_sample.csv");
		System.out.println(dat.toString());
		System.out.println(dat.get(1)); // 2nd row
		System.out.println(dat.get(0, 0)); // 0,0 element
		System.out.println(dat.column(0)); // 1st column
		
		DataFrame dat2 = dat.drop(0);
		System.out.println(dat2.toString());
		
		System.out.println(dat.nrows());
		DataFrame dat3 = dat.slice(0, 5);
		System.out.println(dat3.toString());
		
	}
}
