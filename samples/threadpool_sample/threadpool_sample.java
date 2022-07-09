package threadpool_sample;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class threadpool_sample {
	
	public static void main(String[] args) {
		String[] messages = {"1st", "2nd", "3rd", "4th", "5th", "6th"};
		
		ExecutorService pool;
	    ArrayList<Future<String>> futures;

		int ncpu = Runtime.getRuntime().availableProcessors();
		System.out.println(ncpu);

		int nthread = 2; // ncpu - 1;
		
		pool = Executors.newFixedThreadPool(nthread);
		
		futures = new ArrayList<>();
		
		int sleepTime = messages.length * 2 + 5;
		for (String msg : messages) {
			test t = new test(msg, sleepTime);
			futures.add(pool.submit(t));
			sleepTime -= 2;
		}
		
        for (Future<String> run : futures) {
            try {
                System.out.println(run.get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println(e.getMessage());
            }
        }
        
        pool.shutdown();
	}

}
