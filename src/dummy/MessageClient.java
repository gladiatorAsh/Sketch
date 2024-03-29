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
package gash.router.client;

import java.net.Authenticator.RequestorType;

import com.google.protobuf.ByteString;

import pipe.common.Common.*;
//import pipe.common.Common.Request;
import routing.Pipe.CommandMessage;

/**
 * front-end (proxy) to our service - functional-based
 * 
 * @author gash
 * 
 */
public class MessageClient {
	// track requests
	private long curID = 0;

	public MessageClient(String host, int port) {
		init(host, port);
	}

	private void init(String host, int port) {
		CommConnection.initConnection(host, port);
	}

	public void addListener(CommListener listener) {
		CommConnection.getInstance().addListener(listener);
	}

	public void ping() {
		// construct the message to send
		Header.Builder hb = Header.newBuilder();
		hb.setNodeId(999);
		hb.setTime(System.currentTimeMillis());
		hb.setDestination(-1);

		CommandMessage.Builder rb = CommandMessage.newBuilder();
		rb.setHeader(hb);
		rb.setPing(true);

		try {
			// direct no queue
			// CommConnection.getInstance().write(rb.build());

			// using queue
			CommConnection.getInstance().enqueue(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendWriteRequest(ByteString bs,String fileName,int chunkCount,int chunkId){
		
		Header.Builder header=Header.newBuilder();
		header.setNodeId(99);
		header.setTime(System.currentTimeMillis());
		header.setDestination(-1);
		
		Chunk.Builder chunk=Chunk.newBuilder();
		chunk.setChunkId(chunkId);
		chunk.setChunkData(bs);
		//chunk.setChunkSize(value);
		
		WriteBody.Builder body= WriteBody.newBuilder();
		body.setFilename(fileName);
		body.setNumOfChunks(chunkCount);
		//body.setFileExt(ext);
		body.setChunk(chunk);
		
		Request.Builder req=Request.newBuilder();
		req.setRequestType(Request.RequestType.WRITEFILE);
		
		CommandMessage.Builder comm=CommandMessage.newBuilder();
		comm.setHeader(header);
		//comm.setRepeatedField(field, index, value)
		comm.setMessage(fileName);
	}
	
	
	public void release() {
		CommConnection.getInstance().release();
	}

	/**
	 * Since the service/server is asychronous we need a unique ID to associate
	 * our requests with the server's reply
	 * 
	 * @return
	 */
	private synchronized long nextId() {
		return ++curID;
	}
}
