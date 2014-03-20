package pi;

import java.io.IOException;
import java.util.Scanner;

public class TestPosition implements Runnable{
	
	Scanner reader1;
	private Pi pi;
	private PositionManager mypm;
	
	public TestPosition(Pi pi, PositionManager mypm) {
		reader1 = new Scanner(System.in);
		this.pi = pi;
		this.mypm = mypm;
	}
	
	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		Pi pi = new Pi(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		try{
	 		Thread t = new Thread(new TestPosition(pi, pi.getMyPositionManager()));
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
			System.out.println("setGoalPositionX:");
			int inputx = reader1.nextInt();
			System.out.println("setGoalPositionY:");
			int inputy = reader1.nextInt();
			pi.setTargetPosition(inputx, inputy);
			while (true) {
				System.out.println("setCurrentPositionX:");
				int currx = reader1.nextInt();
				System.out.println("setCurrentPositionY:");
				int curry = reader1.nextInt();
				System.out.println("setCurrentRotation:");
				int rotation = reader1.nextInt();
				pi.setRotation(rotation);
				pi.setPosition(currx, curry);
				mypm.moveToNextPosition();
			}		
	}
}
