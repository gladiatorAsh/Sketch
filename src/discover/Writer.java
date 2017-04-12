package discover;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Writer extends TimerTask {

	static Set<String> addresses = new HashSet<>();
	String filePath="C:\\netty-pipe3.tar\\netty-pipe3\\runtime\\route-6.conf";
	
	public Writer(Set<String> addresses2) {
		// TODO Auto-generated constructor stub
		addresses = addresses2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedWriter out = null;
		System.out.println("Inside Write to file");

		try {
			System.out.println("Making JSON object");
			System.out.println("SD");
			JSONObject obj = new JSONObject();
			System.out.println("1");
			LocalIP.getLocalHostLANAddress();
			String local=InetAddress.getLocalHost().toString();
			obj.put("nodeId", local.split("\\.")[3]);
			obj.put("internalNode", "false");
			obj.put("heartBeatDt", 3000);
			System.out.println("2");
			obj.put("workPort", 4567);
			obj.put("commandPort", 4568);
			JSONArray arr = new JSONArray();
			for (String address : addresses) {
				System.out.println("3");
				System.out.println(address);
				JSONObject route = new JSONObject();

				route.put("id", address.split("\\.")[3]);
				route.put("host", address);
				route.put("port", 4567);
				System.out.println("Iterated");
				arr.add(route);
				System.out.println("Added to arr");
			}
			obj.put("routing", arr);
			System.out.println("4");
			System.out.println("Opening file");

			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file, true);
			// data.
			out = new BufferedWriter(fstream);
			System.out.println("Writing to file");

			out.write(obj.toJSONString());
			System.out.println("Connection details written to file");
			System.out.println("JSON Object " + obj);

		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (Exception e) {

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
