package test;


import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MyCallable implements Callable<Long>{

	long id=0;
	ArrayList<Long> chunks= InvokeAllDemo.chunks;
	
	public MyCallable(long i){
		this.id=i;
	}
	
	@Override
	public Long call(){
		System.out.println(chunks.get((int) id));
		return id;
	}
}
