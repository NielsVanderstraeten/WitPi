package pi;

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
		Pi pi = new Pi(3000, 3000, true);
		try{
	 		(new TestPosition(pi, pi.getMyPositionManager())).run();
			//Thread hm = new Thread(pi.getHeightManager());
			//t.setDaemon(true);
			//hm.setDaemon(true);
			//ex.setDaemon(true);
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
				double rotation = reader1.nextDouble();
				pi.setPosition(currx, curry);
				pi.setRotation(rotation);
			}		
	}
}
