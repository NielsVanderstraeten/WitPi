package pi;

import java.util.ArrayList;

public class PositionManager {
	
	int leftTime = 500;
	int rightTime = 1500;
	int forwardTime = 500;
	int backwardTime = 1500;
	
	private Vector targetPosition;
	private Vector currentPosition;
	private ArrayList<Vector> positions = new ArrayList<Vector>();
	private Pi pi;
	
	public PositionManager(Vector currentPosition, Pi pi){
		this.currentPosition = currentPosition;
		this.pi = pi;
		positions.add(currentPosition);
	}
	
	public void setTargetPosition(Vector newTargetPos){
		targetPosition = newTargetPos;		
	}

	public void setCurrentPosition(Vector newCurrentPos){
		currentPosition = newCurrentPos;		
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
			pi.left(leftTime);
		} else {
			//beweeg rechts (motor)
			pi.right(rightTime);
		}		
	}
	
	private void activateVerticalMotor() {
		if (verticalMovement() == 1) {
			//beweeg omhoog (motor)
			pi.forward(forwardTime);
		} else {
			//beweeg omlaag (motor)
			pi.backward(backwardTime);
		}		
	}

	//TODO: rotatione
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
				} else {
					//Beweeg de zeppelin verticaal
					activateVerticalMotor();
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
}
