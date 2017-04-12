package discover;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ashutosh Singh 
 * Code created with the help of from Michiel De Mey's blog
 * http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 * 1. Open a socket on the server that listens to the UDP requests.(I’ve chosen 8888) 
 * 2. Make a loop that handles the UDP requests and responses 
 * 3. Inside the loop, check the received UPD packet to see if it’s valid 
 * 4. Still inside the loop, send a response to the IP and Port of the received packet
 * 
 */
public class Server implements Runnable {
	Boolean isWrite=false;
	DatagramSocket socket;
	private volatile boolean exit = false;
	//static ArrayList<String> addresses = new ArrayList<String>();
	static Set<String> addresses = new HashSet<>();

	@Override
	public void run() {
		try {

			// Keep a socket open to listen to all the UDP traffic that is
			// destined for this port
			socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (!exit) {
				System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

				// Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				// Packet received
				System.out.println(getClass().getName() + ">>>Discovery packet received from: "
						+ packet.getAddress().getHostAddress());
				System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

				addresses.add(packet.getAddress().getHostAddress());
				
				// See if the packet holds the right command (message)
				String message = new String(packet.getData()).trim();
				if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
					byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

					// Send a response
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(),
							packet.getPort());
					socket.send(sendPacket);
					addresses.add(sendPacket.getAddress().getHostAddress());
					System.out.println(
							getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
				}
			}

		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException {
		try {

			Thread server = new Thread(Server.getInstance());
			server.start();
			Timer timer=new Timer();
			timer.schedule(new Writer(addresses), 20000);
			System.out.println("stopping Server thread");

			//System.out.println("Server >>>Stopped accepting incoming connections");
			server.interrupt();
			
			
		}
		finally{
			//
		}
	}

	public static Server getInstance() {
		return ServerThreadHolder.INSTANCE;
	}

	public void stop() {
		exit = true;
	}

	private static class ServerThreadHolder {
		private static final Server INSTANCE = new Server();
	}
}
