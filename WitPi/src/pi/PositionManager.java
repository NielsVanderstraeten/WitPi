package pi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PositionManager {
	
	int leftTime = 200;
	int rightTime = 600;
	int forwardTime = 200;
	int backwardTime = 600;
	
	private Vector targetPosition;
	private Vector currentPosition;
	private Vector realTarget, realCurrent;
	private ArrayList<Vector> positions = new ArrayList<Vector>();
	private Pi pi;
	
	public PositionManager(Vector currentPosition, Pi pi){
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
	}
		
	//1 = links, 2 = rechts, 0 = blijf
	private int horizontalMovement() {
		if (currentPosition.getX() > targetPosition.getX() + 200)
			return 1;
		else if (currentPosition.getX() < targetPosition.getX() - 200)
			return 2;
		else
			return 0;
		//TODO draaien
	}
	
	//1 = omhoog, 2 = omlaag, 0 = blijf
	private int verticalMovement() {
		if (currentPosition.getY() > targetPosition.getY() + 200)
			return 1;
		else if (currentPosition.getY() < targetPosition.getY() - 200)
			return 2;
		else
			return 0;
	}
	
	public boolean isAtCorrectPosition() {
		return (verticalMovement() == 0 && horizontalMovement() == 0);
	}
	
	private void activateHorizontalMotor() {
		if (horizontalMovement() == 1) {
			//beweeg links (motor)
			pi.right(rightTime);
			System.out.println("Achteruit");
		} else {
			//beweeg rechts (motor)
			pi.left(leftTime);
			System.out.println("Vooruit");
		}		
	}
	
	private void activateVerticalMotor() {
		if (verticalMovement() == 1) {
			//beweeg omhoog (motor)
			pi.forward(forwardTime);
			System.out.println("Rechts");
		} else {
			//beweeg omlaag (motor)
			pi.backward(backwardTime);
			System.out.println("Links");
		}		
	}

	public void moveToNextPosition() {			

		if (! isAtCorrectPosition()) {
			Vector middelpunt = pi.getMiddelpunt();

			boolean juisteXAs = (horizontalMovement() == 0);
			boolean juisteYAs = (verticalMovement() == 0);

			if (juisteXAs) {
				//Beweeg enkel nog op de y-as
				activateVerticalMotor();
			} else if (juisteYAs) {
				//Beweeg enkel nog op de x-as
				activateHorizontalMotor();
			} else {
				//Beweeg in richting die het dichts bij middelpunt ligt.
				double horDistance = calculateHorizontalDistanceToMid(middelpunt);			
				double verDistance = calculateVerticalDistanceToMid(middelpunt);

				if (horDistance < verDistance) {
					//Beweeg de zeppelin horizontaal
					activateHorizontalMotor();
					activateVerticalMotor();
				} else {
					//Beweeg de zeppelin verticaal
					activateVerticalMotor();
					activateHorizontalMotor();
				}
			}	
		}
	}

	private double calculateHorizontalDistanceToMid(Vector middelpunt) {
		if (horizontalMovement() == 1) { //Richting links
			return middelpunt.getDistance(new Vector(currentPosition.getX() - 100, currentPosition.getY()));
		} else { //Richting rechts
			return middelpunt.getDistance(new Vector(currentPosition.getX() + 100, currentPosition.getY()));
		}
	}

	private double calculateVerticalDistanceToMid(Vector middelpunt) {
		if (verticalMovement() == 1) { //Richting omhoog
			return middelpunt.getDistance(new Vector(currentPosition.getX(), currentPosition.getY() - 100));
		} else { //Richting omlaag
			return middelpunt.getDistance(new Vector(currentPosition.getX(), currentPosition.getY() + 100));
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
