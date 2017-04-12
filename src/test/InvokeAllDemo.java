package test;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InvokeAllDemo {
	public static ArrayList<Long> chunks = new ArrayList<Long>();

	public static void runTask() throws InterruptedException {
		FillList();

		ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		List<MyCallable> futuresList = new ArrayList<MyCallable>();
		for (int i = 0; i < chunks.size(); i++) {
			MyCallable myCallable = new MyCallable(i);
			futuresList.add(myCallable);

		}
		System.out.println("Start send");

		List<Future<Long>> futures = service.invokeAll(futuresList);
		System.out.println("Completed tasks");

		service.shutdown();
	}

	private static void FillList() {
		// TODO Auto-generated method stub
		
		for(int i=0;i<10000;i++){
			chunks.add((long)i);
		}
	}

}
