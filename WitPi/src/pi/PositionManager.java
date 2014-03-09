package pi;

import java.util.ArrayList;

public class PositionManager {
	
	private Vector targetPosition;
	private Vector currentPosition;
	private ArrayList<Vector> positions = new ArrayList<Vector>();
	
	public PositionManager(Vector currentPosition){
		this.currentPosition = currentPosition;
		positions.add(currentPosition);
	}
	
	public void setTargetPosition(Vector newTargetPos){
		targetPosition = newTargetPos;		
	}

	
	public void setCurrentPosition(Vector newCurrentPos){
		currentPosition = newCurrentPos;		
	}
}
