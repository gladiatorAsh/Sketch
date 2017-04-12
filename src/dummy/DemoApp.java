/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import gash.router.client.CommConnection;
import gash.router.client.CommListener;
import gash.router.client.Constants;
import gash.router.client.MessageClient;
import pipe.common.Common.Body;
import pipe.common.Common.Body.BodyType;
import pipe.common.Common.Header;
import pipe.work.Work.Task;
import routing.Pipe.CommandMessage;
import com.google.protobuf.ByteString;


public class DemoApp implements CommListener {
	private MessageClient mc;
	private long msgId;
	public DemoApp(MessageClient mc) {
		init(mc);
	}

	private void init(MessageClient mc) {
		this.mc = mc;
		this.mc.addListener(this);
	}

	private synchronized long nextId(){
		return ++msgId;
	}
	
	private void ping(int N) {
		// test round-trip overhead (note overhead for initial connection)
		final int maxN = 10;
		long[] dt = new long[N];
		long st = System.currentTimeMillis(), ft = 0;
		for (int n = 0; n < N; n++) {
			mc.ping();
			ft = System.currentTimeMillis();
			dt[n] = ft - st;
			st = ft;
		}

		System.out.println("Round-trip ping times (msec)");
		for (int n = 0; n < N; n++)
			System.out.print(dt[n] + " ");
		System.out.println("");
	}

	@Override
	public String getListenerID() {
		return "demo";
	}

	@Override
	public void onMessage(CommandMessage msg) {
		System.out.println("---> " + msg);
		
	}

	/**
	 * sample application (client) use of our messaging service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {	
		String host = "127.0.0.1";
		int port = 4167;

		try {
			MessageClient mc = new MessageClient(host, port);
			DemoApp da = new DemoApp(mc);
			
			// do stuff w/ the connection
			//da.ping(2);
			
			String path="C:\\1\\rprogramming.pdf";
			File file=new File(path);
			da.sendFile(file);

			System.out.println("\n** exiting in 10 seconds. **");
			System.out.flush();
			Thread.sleep(10 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommConnection.getInstance().release();
		}
	}

	private void sendFile(File file) throws IOException {
		// TODO Auto-generated method stub
		
		/*
		 * Task 1: Send Read Command
		 * Task 2: Receive Ack
		 * Task 3: Split into chunks 
		 * Task 4: Send chunks
		 * Task 5: Recv Ack
		 * Task 6: Resend chunks not ack
		 * */
		
		sendReadCommand(file);
		
		
	}

	private void sendReadCommand(File ufile) throws IOException {
		// TODO Auto-generated method stub

		//String name=filePath.getName();
		//File ufile=new File(filePath);
		String name=ufile.getName();
		
		ArrayList<ByteString> chunks=new ArrayList<ByteString>();
		int chunkCount=0;
		byte[] buffer=new byte[Constants.sizeOfChunk];
		BufferedInputStream bs=new BufferedInputStream(new FileInputStream(ufile));
		
		int got;
		while ((got = bs.read(buffer)) != -1) {

			ByteString bstr = ByteString.copyFrom(buffer, 0, got);
        	chunks.add(bstr);
        	chunkCount++;
		}

		 //ExecutorService es=Executors.newWorkStealingPool()
		
		
		Header.Builder header=Header.newBuilder();
		header.setNodeId(99);
		header.setTime(System.currentTimeMillis());
		header.setDestination(-1);
		
		CommandMessage.Builder command= CommandMessage.newBuilder();
		command.setHeader(header);
		
		Body.Builder body= Body.newBuilder();
		//body.setFilename(fileName);
		body.setBodyType(BodyType.READFILE);
		body.setSender(InetAddress.getLocalHost().getHostAddress());
		
		body.setRequestId(Long.toString(nextId()));
		
		//command.setMessage(fileName);
		
		//ToDo:setBody
		//command.se(body.build());
		
		try {
			CommConnection.getInstance().enqueue(command.build());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
