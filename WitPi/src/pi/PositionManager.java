package pi;

public class PositionManager {
	
	private int targetXPosition;
	private int targetYPosition;
	private int currentXPosition;
	private int currentYPosition;
	
	public PositionManager(int currentXPosition, int currentYPosition){
		this.currentXPosition = currentXPosition;
		this.currentYPosition = currentYPosition;
	}
	
	public void setTargetXPosition(int newXPos){
		targetXPosition = newXPos;		
	}
	
	public void setTargetYPosition(int newYPos){
		targetYPosition = newYPos;
	}
	
	public void setCurrentXPosition(int newXPos){
		currentXPosition = newXPos;		
	}
	
	public void setCurrentYPosition(int newYPos){
		currentYPosition = newYPos;
	}
}
