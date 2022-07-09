package threadpool_sample;

import java.time.LocalTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class test implements Callable {
	private String message;
	private int sleepTime;
	
	public test(String msg, int sleepTime) {
		message = msg;
		this.sleepTime = sleepTime; 
	}
	
	@Override
    public String call() throws Exception {
        System.out.println(LocalTime.now()+" start thread (" + sleepTime +")");
        TimeUnit.SECONDS.sleep(sleepTime);
        System.out.println(LocalTime.now()+" end thread");
        return message;
    }

}
