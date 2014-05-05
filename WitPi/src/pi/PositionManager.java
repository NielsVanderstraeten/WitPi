package pi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PositionManager {
	
	int leftTime = 500;
	int rightTime = 1500;
	int forwardTime = 500;
	int backwardTime = 1500;
	
	private Vector targetPosition;
	private Vector currentPosition;
	private Vector realTarget, realCurrent;
	private ArrayList<Vector> positions = new ArrayList<Vector>();
	private Pi pi;
	
	private Logger logger;
	private FileHandler fh;
	private final boolean logging = true;
	
	public PositionManager(Vector currentPosition, Pi pi) throws SecurityException, IOException{
		if (logging) {
			logger = Logger.getLogger("positionlogger");  
			fh = new FileHandler("/positionmanager.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter); 
		}
		
		this.pi = pi;
		realTarget = pi.getMiddelpunt();
		setCurrentPosition(currentPosition);
		positions.add(currentPosition);
	}
	
	public void setTargetPosition(Vector newTargetPos){
		realTarget = newTargetPos;		
	}

	public void setCurrentPosition(Vector newCurrentPos){
		realCurrent = newCurrentPos;
		double rotation = pi.getRotation();
		double x = newCurrentPos.getX();
		double y = newCurrentPos.getY();
		double tarX = realTarget.getX();
		double tarY = realTarget.getY();
		
		targetPosition = changeBase(tarX,tarY,rotation);
		currentPosition = changeBase(x,y,rotation);

		System.out.println("targetPosition changed base from:" + tarX +", " + tarY + "to: " + targetPosition.getX() + ", " + targetPosition.getY());
		System.out.println("currentPosition changed base from:" + x +", " + y + "to: " + currentPosition.getX() + ", " + currentPosition.getY());
		
		if (logging) {
			logger.info("targetPosition changed base from:" + tarX +", " + tarY + "to: " + targetPosition.getX() + ", " + targetPosition.getY());
			logger.info("currentPosition changed base from:" + x +", " + y + "to: " + currentPosition.getX() + ", " + currentPosition.getY());
		}
	}
		
	//1 = vooruit, -1 = achteruit, 0 = blijf
	private int horizontalMovement() {
		System.out.println("HorzMove: " + (targetPosition.getY() - currentPosition.getY()) + "");
		if (targetPosition.getX() - currentPosition.getX() > 500)
			return 1;
		else if (targetPosition.getX() - currentPosition.getX() < -500)
			return -1;
		else
			return 0;
		//draaien
	}
	
	//1 = rechts, 2 = links, 0 = blijf
	private int verticalMovement() {
		System.out.println("VertMove: " + (targetPosition.getY() - currentPosition.getY()));
		if (targetPosition.getY() - currentPosition.getY() > 500)
			return 1;
		else if (targetPosition.getY() - currentPosition.getY() < -500)
			return -1;
		else
			return 0;
	}
	
	public boolean isAtCorrectPosition() {
		if (logging) {
			logger.info("isAtCorrectPosition, vertmov: " + verticalMovement());
			logger.info("isAtCorrectPosition, hormov: " + horizontalMovement());
		}
		
		return (verticalMovement() == 0 && horizontalMovement() == 0);
	}
	
	private void activateHorizontalMotor() {
		if (horizontalMovement() == 1) {
			//beweeg vooruit (motor)
			pi.forward(forwardTime);
			System.out.println("Vooruit");
		} else if (horizontalMovement() == -1){
			//beweeg achteruit (motor)
			pi.backward(backwardTime);
			System.out.println("Achteruit");
		} else
			;
	}
	
	private void activateVerticalMotor() {
		if (verticalMovement() == 1) {
			//beweeg rechts (motor)
			pi.right(rightTime);
			System.out.println("Rechts");
		} else if (verticalMovement() == -1){
			//beweeg links (motor)
			pi.left(leftTime);
			System.out.println("Links");
		}		
	}

	public void moveToNextPosition() {			

		if (! isAtCorrectPosition()) {
			if(logging){
				logger.info("moveToNextPos: horizontal: "+ horizontalMovement());
				logger.info("moveToNextPos: Vertical: "+ verticalMovement());
			}
			activateHorizontalMotor();
			activateVerticalMotor();
		}	
	
	}

	@Deprecated
	private double calculateHorizontalDistanceToMid(Vector middelpunt) {
		if (horizontalMovement() == 1) { //Richting links
			return middelpunt.getDistance(new Vector(currentPosition.getX() - 500, currentPosition.getY()));
		} else { //Richting rechts
			return middelpunt.getDistance(new Vector(currentPosition.getX() + 500, currentPosition.getY()));
		}
	}
	
	@Deprecated
	private double calculateVerticalDistanceToMid(Vector middelpunt) {
		if (verticalMovement() == 1) { //Richting omhoog
			return middelpunt.getDistance(new Vector(currentPosition.getX(), currentPosition.getY() - 500));
		} else { //Richting omlaag
			return middelpunt.getDistance(new Vector(currentPosition.getX(), currentPosition.getY() + 500));
		}
	}
	
	private Vector changeBase(double x, double y, double rotation){
		x = x - pi.getMiddelpunt().getX();
		y = y - pi.getMiddelpunt().getY();
		double newX = (x*Math.cos(rotation) + y*Math.sin(rotation)) + pi.getMiddelpunt().getX();
		double newY = (-x*Math.sin(rotation) + y*Math.cos(rotation)) + pi.getMiddelpunt().getY();
		return new Vector(newX, newY);
	}
}
