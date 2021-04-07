package week6;

import java.io.*;
import java.net.*;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class client {

	static class ServerList {
		public String type;
		public int limit;
		public int bootupTime;
		public float hourlyRate;
		public int coreCount;
		public int memory;
		public int disk;

		public ServerList(String type, int limit, int bootupTime, float hourlyRate, int coreCount, int memory, int disk) {
			this.type = type;
			this.limit = limit;
			this.bootupTime = bootupTime;
			this.hourlyRate = hourlyRate;
			this.coreCount = coreCount;
			this.memory = memory;
			this.disk = disk;
		}

		public void print() {
			System.out.println("type: " + type + " / limit: " + limit + " / bootupTime: " + bootupTime + " / hourlyRate: " + hourlyRate + " / coreCount: " + coreCount + " / memory: " + memory + " / disk: " + disk);
		}

		@Override
    public String toString() {
        return ("type: " + type + " / limit: " + limit + " / bootupTime: " + bootupTime + " / hourlyRate: " + hourlyRate + " / coreCount: " + coreCount + " / memory: " + memory + " / disk: " + disk);
    }
	}

	public static ServerList[] importXML(String fileLocation) {

		ServerList[] returning = new ServerList[]{};

		try {
			File dssystemxml = new File(fileLocation);

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(dssystemxml);

			doc.getDocumentElement().normalize();

			NodeList servers = doc.getElementsByTagName("server");

			ServerList[] serverList = new ServerList[servers.getLength()];
			returning = new ServerList[servers.getLength()];

			for(int i = 0; i < servers.getLength(); i++) {
				Element server = (Element) servers.item(i);

				String t = server.getAttribute("type");
				int l = Integer.parseInt(server.getAttribute("limit"));
				int b = Integer.parseInt(server.getAttribute("bootupTime"));
				float hr = Float.parseFloat(server.getAttribute("hourlyRate"));
				int c = Integer.parseInt(server.getAttribute("coreCount"));
				int m = Integer.parseInt(server.getAttribute("memory"));
				int d = Integer.parseInt(server.getAttribute("disk"));

				serverList[i] = new ServerList(t, l, b, hr, c, m, d);
			}

			returning = serverList.clone();

		} catch (Exception e) {
			System.out.println(e);
		}

		return returning;
	}

	public static void sortServerList(ServerList[] serverList) {
		Arrays.sort(serverList, (a, b) -> Integer.compare(a.memory, b.memory));
	}

	public static void printServerList(ServerList[] serverList) {
		for(int i = 0; i < serverList.length; i++) {
			serverList[i].print();
		}
	}

	public static String readServerOutput(BufferedReader input) {
		System.out.println("Recieved from Server...");
		return input.lines().collect(Collectors.joining());
	}
	public static void main(String[] args) {  
		try{      
			Socket s = new Socket("localhost",50000);
			
			BufferedReader recieveFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
      DataOutputStream sendToServer = new DataOutputStream(s.getOutputStream());

			// Write
			sendToServer.write("HELO".getBytes());

			// Write
			sendToServer.write("AUTHLucas".getBytes());

			// Import ds-system.xml for tasks
			ServerList[] serverList = importXML("./ds-system.xml");
			
			// Sort in ascending order ServerList
			sortServerList(serverList);
			printServerList(serverList);
			System.out.println("Largest Server: " + serverList[serverList.length - 1].toString());

			// REDY
			sendToServer.write("REDY".getBytes());

			// Send Jobs
			/*
			for(int i = 0; i < serverList.length; i++) {
				String toSend = "JOBN " + i + " " + i + " 100 " + serverList[i].coreCount + " " + serverList[i].memory + " " + serverList[i].disk;
				System.out.println("Sending Command: " + toSend);
				sendToServer.write(toSend.getBytes());
			}
			*/

			// Schedule First Job
			//String toSend = "SCHD 8 16xlarge 0";
			//sendToServer.write(toSend.getBytes());

			int value;
			while ((value = recieveFromServer.read()) != -1) {
        System.out.println((char) value);
   	 }

			sendToServer.flush();
			sendToServer.close();

			s.close();
		} catch(Exception e) {
			System.out.println(e);
		}  
	}  
}