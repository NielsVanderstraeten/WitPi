package pi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class PhotoSender implements Runnable {

	private ServerSocket serverSocket;
	private Pi pi;
	private boolean sending;
	private LinkedList<String> queue = new LinkedList<String>();
	private Thread t;

	public PhotoSender(int port, Pi pi) throws IOException
	{
		this.pi = pi;
		serverSocket = new ServerSocket(port);
		sending = true;
	}
	
	public void run(){
		try{ 
			sending = true;
			while(sending){
				long start = System.currentTimeMillis() - 500;
				if(System.currentTimeMillis() - start > 500){
					start = System.currentTimeMillis();
					Socket socket = serverSocket.accept();
					
					DataInputStream in = new DataInputStream(socket.getInputStream());
					OutputStream out = socket.getOutputStream();
					DataOutputStream outData = new DataOutputStream(out); 
					
					pi.takePicture();
					File file = new File("picture.jpg");
					long size = file.length();
					outData.writeBytes("IMG\n" + size + "\n");
					InputStream inFile = new FileInputStream(file);
					copy(inFile, outData);
					inFile.close();
	
					String inMsg = in.readUTF();
					if(!inMsg.equals("done"))
						System.out.println("something");
				}
			}
			serverSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
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
		sending = false;
	}

}
