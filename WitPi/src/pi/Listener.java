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
		serverSocket.setSoTimeout(10000);
		listening = true;
	}
	public synchronized void run()
	{
		while(listening){

			if  ( t == null || (! queue.isEmpty() && ! t.isAlive())) {
				String c = queue.poll();
				t = new Thread(new Executor(pi, c));
				t.start();
			}

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
				else if(inMsg.contains("stop")) {
					queue.clear();
					pi.stop();
				}
				else if(inMsg.equals("forwardstart")) 
					pi.forwardStart();
				//				else if(inMsg.equals("forwardstop"))
				//					pi.forwardStop();
				else if(inMsg.equals("backwardstart"))
					pi.backwardStart();				
				//				else if(inMsg.equals("backwardstop"))
				//					pi.backwardStop();
				//				else if(inMsg.equals("climbstart"))
				//					pi.climbStart();
				//				else if(inMsg.equals("climbstop"))
				//					pi.climbStop();
				//else if(inMsg.equals("descendstart"))
				//	pi.descendStart();
				//				else if(inMsg.equals("descendstop"))
				//					pi.descendStop();
				//else if(inMsg.equals("turnrightstart"))
				//	pi.turnRightStart();
				//				else if(inMsg.equals("turnrightstop"))
				//					pi.turnRightStop();
				//else if(inMsg.equals("turnleftstart"))
				//	pi.turnLeftStart();
				//				else if(inMsg.equals("turnleftstop"))
				//					pi.turnLeftStop();
				//else if(inMsg.contains("stayatheight")) {
				else if(inMsg.contains("setgoalheight")) {
					List<String> strings = Arrays.asList(inMsg.split("\\s+"));
					pi.goToHeight(Double.parseDouble(strings.get(1)));
				}
				else if(inMsg.equals("getheight")){
					outData.writeUTF(pi.getLastCalculatedHeight()+"");
				}
				else if(inMsg.contains("setposition")){
					List<String> strings = Arrays.asList(inMsg.split("\\s+"));
					pi.setPosition(Integer.parseInt(strings.get(1)), Integer.parseInt(strings.get(2)));
				}
				else if(inMsg.contains("setgoalposition")){
					List<String> strings = Arrays.asList(inMsg.split("\\s+"));
					pi.setTargetPosition(Integer.parseInt(strings.get(1)), Integer.parseInt(strings.get(2)));
				}
				//else if (inMsg.contains("climb ")) {
				//	List<String> strings = Arrays.asList(inMsg.split("\\s+"));
				//	pi.goToHeight(pi.getTargetHeight() + Double.parseDouble(strings.get(1)));
				//}
				//else if (inMsg.contains("descend ")) {
				//	List<String> strings = Arrays.asList(inMsg.split("\\s+"));
				//	pi.goToHeight(pi.getTargetHeight() - Double.parseDouble(strings.get(1)));
				//}
				else if(inMsg.equals("terminate")) {
					stopListening();
					pi.terminate();	
				}
				// nieuwe manier, via Thread!
				else {
					queue.add(inMsg);
				}
				// voorlopig in command -> vorige code!
				/*
				else if (inMsg.contains("forward ")) {
					List<String> strings = Arrays.asList(inMsg.split("\\s+"));
					pi.forward(Integer.parseInt(strings.get(1)));
				}
				else if (inMsg.contains("backward ")) {
					List<String> strings = Arrays.asList(inMsg.split("\\s+"));
					pi.backward(Integer.parseInt(strings.get(1)));
				}
				else if (inMsg.contains("climb ") || inMsg.contains("descend ")) {
					List<String> strings = Arrays.asList(inMsg.split("\\s+"));
					pi.goToHeight(Integer.parseInt(strings.get(1)));
				}
				//TODO: links en rechts met graden
				else if(inMsg.equals("forwardstart"))
					pi.forwardStart();
				else if(inMsg.equals("forwardstop"))
					pi.forwardStop();
				else if(inMsg.equals("backwardstart"))
					pi.backwardStart();
				else if(inMsg.equals("backwardstop"))
					pi.backwardStop();
				else if(inMsg.equals("climbstart"))
					pi.climbStart();
				else if(inMsg.equals("climbstop"))
					pi.climbStop();
				else if(inMsg.equals("descendstart"))
					pi.descendStart();
				else if(inMsg.equals("descendstop"))
					pi.descendStop();
				else if(inMsg.equals("turnrightstart"))
					pi.turnRightStart();
				else if(inMsg.equals("turnrightstop"))
					pi.turnRightStop();
				else if(inMsg.equals("turnleftstart")){
					pi.turnLeftStart();
					System.out.println("turnleft start received");
				}
				else if(inMsg.equals("turnleftstop")){
					pi.turnLeftStop();
					System.out.println("turnleft start received");
				}
				else if(inMsg.contains("stayatheight")){
					String[] split = inMsg.split("\\s+");
					if(split.length == 2 ){
						System.out.println("stayatheight "+split[1]);
						double height = Double.parseDouble(split[1]);
						pi.goToHeight(height);
					}
					else{
						//onbekend commando
					}
				}
				else{
					//out.writeUTF("error: unknown command");				
				}
				 */
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
