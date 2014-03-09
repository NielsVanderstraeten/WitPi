package pi;

import java.io.IOException;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class Pi {
	HeightManager3 myHeightManager;
	PositionManager myPositionManager;
	DistanceMonitor myDistance;
	Camera myCamera;
	MotorFixed myTopMotor;
	MotorFixed myLowMotor;
	MotorPwm myHeightMotor;
	final double maxPower = 1024;
	final double minPower = 824;
	
	//Motor1
	Pin forw1 = RaspiPin.GPIO_07;
	Pin back1 = RaspiPin.GPIO_05;
	//Motor2
	Pin forw2 = RaspiPin.GPIO_00;
	Pin back2 = RaspiPin.GPIO_04;
	//Distance Monitor pins:.
	//Pin 1 = RaspiPin.GPIO_13;
	//Pin 2 = RaspiPin.GPIO_11;
	//Motor4
	Pin forw4 = RaspiPin.GPIO_12;
	Pin back4 = RaspiPin.GPIO_14;
	
	public Pi() {
		myDistance = new DistanceMonitor();
		myCamera = new Camera();
		myHeightMotor = new MotorPwm(forw1, back1);
		myTopMotor = new MotorFixed(forw4, back4);
		myLowMotor = new MotorFixed(forw2, back2);
		myHeightManager = new HeightManager3(myHeightMotor, myDistance, minPower, maxPower);
		myPositionManager = new PositionManager(new Vector(-1, -1));
	}
	
	public static void main(String [] args)
	{
		int port = Integer.parseInt(args[0]);
		Pi pi = new Pi();
		try{
	 		Thread t = new Thread(new Listener(port, pi));
			Thread hm = new Thread(pi.getHeightManager());
			//t.setDaemon(true);
			//hm.setDaemon(true);
			//ex.setDaemon(true);
	 		t.start();
			hm.start();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}	
	
	public HeightManager3 getHeightManager(){
		return myHeightManager;
	}
	
	/*public float getHeight() {
		float returnHeight = myDistance.getDistance();
		myPiState.setCurrentHeight(returnHeight);
		return returnHeight;
		
	}*/
	
	public void takePicture() {
		try {
			myCamera.makePicture();
		} catch (IOException e) {
			System.out.println("Couldn't make a picture. (IOException)");
		}
	}
	
	public void stop() { //deze methode laat de zeppelin ogenblikkelijk stoppen met commandos uit te voeren, waarnaa hij terug bestuurbaar is door de pijltjestoetsen.
		myTopMotor.triggerForwardOff();
		myTopMotor.triggerBackwardOff();
		myLowMotor.triggerBackwardOff();
		myLowMotor.triggerForwardOff();
	}
	public void forwardStart(){
		myTopMotor.triggerForwardOn();
		myLowMotor.triggerForwardOn();
	}
	
	public void forwardStop() {		
		myTopMotor.triggerForwardOff();
		myLowMotor.triggerForwardOff();
	}
	
	public void backwardStart(){
		myTopMotor.triggerBackwardOn();
		myLowMotor.triggerBackwardOn();
	}
	
	public void backwardStop() {
		myTopMotor.triggerBackwardOff();
		myLowMotor.triggerBackwardOff();
	}
	
	public void turnLeftStart(){		
		myTopMotor.triggerBackwardOn();
		myLowMotor.triggerForwardOn();
	}
	
	public void turnLeftStop() {
		myTopMotor.triggerBackwardOff();
		myLowMotor.triggerForwardOff();
	}
	
	public void turnRightStart(){
		myTopMotor.triggerForwardOn();
		myLowMotor.triggerBackwardOn();
	}
	
	public void turnRightStop() {
		myTopMotor.triggerForwardOff();
		myLowMotor.triggerBackwardOff();
	}
	
	
	/*public void climbStart(){
		myHeightManager.setRunning(false);
		myBottomMotor.setPower(1024);
		myPiState.setBottomMotorPower(1024);
		myBottomMotor.triggerForwardOn();
		myPiState.setBottomMotorState(1);
	}
	
	public void climbStop() {
		myBottomMotor.setPower(0);
		myPiState.setBottomMotorPower(0);
		myBottomMotor.triggerForwardOff();
		myPiState.setBottomMotorState(0);
		myHeightManager.setTargetHeight(myDistance.getDistance());
		myHeightManager.setRunning(true);
		
	}
	
	public void descendStart(){
		myHeightManager.setRunning(false);
		myBottomMotor.setPower(1024);
		myPiState.setBottomMotorPower(1024);
		myBottomMotor.triggerBackwardOn();
		myPiState.setBottomMotorState(2);
	}
	
	public void descendStop() {
		myBottomMotor.setPower(0);
		myPiState.setBottomMotorPower(0);
		myBottomMotor.triggerBackwardOff();
		myPiState.setBottomMotorState(0);
		myHeightManager.setTargetHeight(myDistance.getDistance());
		myHeightManager.setRunning(true);
	}*/
	
	public void goToHeight(double newTargetHeight) {
		myHeightManager.setTargetHeight(newTargetHeight);
	}
	
	public void terminate() {
		myHeightManager.stopRunning();		
		System.exit(0);
	}
	public double getTargetHeight() {
		return myHeightManager.getTargetHeight();
	}
	
	public MotorFixed getLeftMotor() {
		return myTopMotor;
	}
	
	public MotorFixed getRightMotor() {
		return myLowMotor;
	}
	
	public float getLastCalculatedHeight(){
		return myHeightManager.getLastCalculatedHeight();
	}
	
	public void setPosition(int newXPos, int newYPos) {
		myPositionManager.setCurrentPosition(new Vector(newXPos, newYPos));
	}
	
	public void setTargetPosition(int xPos, int yPos){ 
		myPositionManager.setTargetPosition(new Vector(xPos, yPos));
	}

	
}
