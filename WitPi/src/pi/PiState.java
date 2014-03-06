package pi;
/**
* Class to keep track of all states of the elements of the Pi:
*	- The motor used to control the propellor in order to descend and ascend: bottom motor.
*	- The motor used to control the propellor in order to turn left, move forward or backward: left motor.
*	- The motor used to control the propellor in order to turn right, move forward or backward: right motor.
*	- Last measured distance (blabedkbalbalbalbalbalbalblblablablablablabla)
*
*/
public class PiState{

	/**
	* Variable to keep track of the state of the bottom motor. Either 0 , off, 1, on/forward or 2 on/backward. 
	*/
	private int bottomMotorState = 0;
	
	/**
	* Variable to keep track of the power of the bottom motor. Value between 0 and 1024..
	*/
	private int bottomMotorPower = 1000;
	
	/**
	* Variable to keep track of the state of the left motor. Either 0 , off, 1, on/forward or 2 on/backward. 
	*/
	private int leftMotorState = 0;
	
	/**
	* Variable to keep track of the state of the right motor. Either 0 , off, 1, on/forward or 2 on/backward. 
	*/
	private int rightMotorState = 0;
	
	/**
	* Variable to keep track of the current height of the zeppelin.
	*/
	private float currentHeight = 0;
	
	private float targetHeight = 0;
	
	private double maxPower = 0;
	private double minPower = 0;
	
	public PiState(double minPower, double maxPower){
		this.minPower = minPower;
		this.maxPower = maxPower;
	}
	
	/**
	* Sets the state for the bottom motor to the given new state. Either 0 , off, 1, on/forward or 2 on/backward. 
	*/
	public void setBottomMotorState(int newState){
		bottomMotorState = newState;
	}
	
	/**
	 * Sets the power of the bottom motor to the given new power. A value >= 0 and <= 1024.
	 */
	public void setBottomMotorPower(int bottomMotorPower) {
		this.bottomMotorPower = bottomMotorPower;
	}  
	
	/**
	* Sets the state for the left motor to the given new state. Either 0 , off, 1, on/forward or 2 on/backward.
	*/
	public void setLeftMotorState(int newState){
		leftMotorState = newState;
	}
	
	public void setRightMotorState(int newState) {
		rightMotorState = newState;
	}
	
	/**
	* Sets the state for the right motor to the given new state. Either 0 , off, 1, on/forward or 2 on/backward.
	public void setRightMotorState(int newState){
		rightMotorState = newState;
	}
	
	/**
	* Sets the latest distance measured to the given distance.
	*/
	public void setCurrentHeight(float newCurrentHeight){
		currentHeight = newCurrentHeight;
	}
	
	public void setTargetHeight(float newTargetHeight){
		targetHeight = newTargetHeight;
	}
	
	/**
	* Returns the state of the bottom motor. Either 0 , off, 1, on/forward or 2 on/backward.
	*/
	public int getBottomMotorState() {
		return bottomMotorState;
	}

	/**
	 * Returns the power of the bottom motor. A value >= 0 and <= 1024.
	 */
	public int getBottomMotorPower() {
		return bottomMotorPower;
	}
	
	/**
	 * Returns the percentage of power of the bottom motor.
	 */
	public int getBottomMotorPowerPercentage(){
		int diff = (int) (maxPower-minPower);
		return (int) ((bottomMotorPower-minPower)/diff*100);
	}
	
	/**
	* Returns the state of the left motor. Either 0 , off, 1, on/forward or 2 on/backward.
	*/
	public int getLeftMotorState() {
		return leftMotorState;
	}

	/**
	* Returns the state of the right motor. Either 0 , off, 1, on/forward or 2 on/backward.
	*/
	public int getRightMotorState() {
		return rightMotorState;
	}

	/**
	* Returns the latest distance measured.
	*/
	public float getHeight() {
		return currentHeight;
	}
	
	public float getTargetHeight(){
		return targetHeight;
	}
	
	//TODO: tostring voor return-value
	/**
	 * Returns the current state of the PI: "bottomMotorState+' '+bottomMotorPowerPercentage+' '+rightMotorState+' '+leftMotorState+' '+height+' '+targetheight".
	 */
	public String toString(){
		return(getBottomMotorState() +" "+
				getBottomMotorPowerPercentage() +" "+
				getRightMotorState() +" "+
				getLeftMotorState() +" "+
				getHeight() +" "+
				getTargetHeight());
	}
	
}	
