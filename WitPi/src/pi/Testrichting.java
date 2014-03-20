package pi;


import java.io.IOException;
import java.util.Scanner;

public class Testrichting implements Runnable{
	
	Scanner reader1;
	private Pi pi;
	private PositionManager mypm;
	
	public Testrichting(Pi pi, PositionManager mypm) {
		reader1 = new Scanner(System.in);
		this.pi = pi;
		this.mypm = mypm;
	}
	
	public static void main(String[] args) {
		//int port = Integer.parseInt(args[0]);
		Pi pi = new Pi(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		try{
	 		Thread t = new Thread(new Testrichting(pi, pi.getMyPositionManager()));
			//Thread hm = new Thread(pi.getHeightManager());
			//t.setDaemon(true);
			//hm.setDaemon(true);
			//ex.setDaemon(true);
	 		t.start();
			//hm.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void run() {
			while (true) {
				System.out.println("setForward:");
				int inputx = reader1.nextInt();
				System.out.println("setBackward:");
				int inputy = reader1.nextInt();
				pi.setTargetPosition(inputx, inputy);
				pi.forward(inputx);
				pi.left(inputy);
			}
	}
}
