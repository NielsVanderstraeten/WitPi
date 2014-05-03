package pi;

import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.*;

public class Listener implements Runnable
{
	private ServerSocket serverSocket;
	private Pi pi;
	private boolean listening;
	private LinkedList<String> queue = new LinkedList<String>();
	private Thread t;

	public Listener(int port, Pi pi) throws IOException
	{
		this.pi = pi;
		serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(10000);
		listening = true;
	}
	public synchronized void run(){
		while(listening){
			try{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				String inMsg = in.readUTF();
				OutputStream out = server.getOutputStream();
				DataOutputStream outData = new DataOutputStream(out); 
				if(inMsg.equals("takepicture")){
					pi.takePicture();
					File file = new File("picture.jpg");
					InputStream inFile = new FileInputStream(file);                        
					copy(inFile, out);
					inFile.close();
				}
				else {
				}
			
				server.close();
			}
			catch(SocketTimeoutException s){
				System.out.println("Socket timed out!");
				break;
			}
			catch(IOException e){
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
