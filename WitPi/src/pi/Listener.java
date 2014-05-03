package pi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Listener implements Runnable
{
	private ServerSocket serverSocket;
	private Pi pi;
	private boolean listening;
	private LinkedList<String> queue = new LinkedList<String>();
	private Thread t;
	
	private final boolean logging = true;
	private Logger logger;
	private FileHandler fh;

	public Listener(int port, Pi pi) throws IOException
	{
		this.pi = pi;
		serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(10000);
		listening = true;
		
		if (logging) {
			logger = Logger.getLogger("photoclientlogger");  
			fh = new FileHandler("/photoclient.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter); 
		}
	}
	public synchronized void run(){
		while(listening){
			try{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				if (logging)
					logger.info("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				String inMsg = in.readUTF();
				if (logging)
					logger.info("Read inMsg: " + inMsg);
				OutputStream out = server.getOutputStream();
				DataOutputStream outData = new DataOutputStream(out); 
				
				if (logging)
					logger.info("OutputStream initialised");
				
				if(inMsg.equals("takepicture")){
					if (logging)
						logger.info("Taking picture...");
					pi.takePicture();
					
					if (logging)
						logger.info("Taken picture.");
					File file = new File("picture.jpg");
					InputStream inFile = new FileInputStream(file);  
					if (logging)
						logger.info("Starting copy");
					copy(inFile, out);
					if (logging)
						logger.info("Finished copy");
					inFile.close();
				}
				else {
				}
			
				server.close();
			}
			catch(SocketTimeoutException s){
				System.out.println("Socket timed out!");
				if (logging)
					logger.info("Socket timed out!");
				break;
			}
			catch(IOException e){
				if (logging)
					logger.info("IOException");
				e.printStackTrace();
				break;
			}
		}
	}

	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
	}

	public void stopListening() {
		listening = false;
	}

}
