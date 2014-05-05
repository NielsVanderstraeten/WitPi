package pi;

import java.io.IOException;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class Pi {
	HeightManager3 myHeightManager;
	PositionManager myPositionManager;
	DistanceMonitor myDistance;
	Camera myCamera;
	MotorFixed myFrontMotor;
	MotorFixed mySideMotor;
	MotorPwm myHeightMotor;
	final double maxPower = 1024;
	final double minPower = 824;
	private PiRabbitClient client;
	private String host = "localhost";
	//private String host = "192.168.2.134";
	private String serverName = "server";
	
	private Vector middelpunt;
	
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
	private double rotation;
	private RabbitListener listener;
	private Thread hmThread, clientThread, listenerThread;
	private Listener photoListener;
	private Thread photoListenerThread;
	
	public Pi(int width, int height) throws SecurityException, IOException {
		myDistance = new DistanceMonitor();
		myCamera = new Camera();
		myHeightMotor = new MotorPwm(forw1, back1);
		myFrontMotor = new MotorFixed(forw4, back4);
		mySideMotor = new MotorFixed(forw2, back2);
		setMiddelpunt(width/2, height/2);
		
		myPositionManager = new PositionManager(new Vector(-1, -1), this);
		
		myHeightManager = new HeightManager3(myHeightMotor, myDistance, minPower, maxPower);
		listener = new RabbitListener(host, serverName, this);
		client = new PiRabbitClient(host, serverName, this);
		photoListener = new Listener(6066, this);
		
		hmThread = new Thread(this.getHeightManager());
		clientThread = new Thread(this.getClient());
		listenerThread = new Thread(this.getListener());
		photoListenerThread = new Thread(photoListener);
		
		hmThread.start();
		listenerThread.start();
		clientThread.start();
		photoListenerThread.start();
	}
	
	/**
	 * Een testconstructor omdat ik geen Pi had om motoren aan te sturen.
	 * @param width
	 * @param height
	 * @param test
	 */
	public Pi(int width, int height, boolean test){
		myCamera = new Camera();
	}
	
	public static void main(String [] args) throws NumberFormatException, SecurityException, IOException
	{
		int port = Integer.parseInt(args[0]);
		new Pi(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	}	
	
	public RabbitListener getListener() {
		return listener;
	}
	
	public PiRabbitClient getClient(){
		return client;
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
		myFrontMotor.triggerForwardOff();
		myFrontMotor.triggerBackwardOff();
		mySideMotor.triggerBackwardOff();
		mySideMotor.triggerForwardOff();
		myHeightManager.stopRunning();
		client.stopRunning();
		listener.stopRunning();
		photoListener.stopListening();
		System.exit(0);
	}
	
	public void forwardStart(){
		myFrontMotor.triggerForwardOn();
	}
	
	public void forwardStop() {		
		myFrontMotor.triggerForwardOff();
	}
	
	public void backwardStart(){
		myFrontMotor.triggerBackwardOn();
	}
	
	public void backwardStop() {
		myFrontMotor.triggerBackwardOff();
	}
	
	public void leftStart(){		
		mySideMotor.triggerForwardOn();
	}
	
	public void leftStop() {
		mySideMotor.triggerForwardOff();
	}
	
	public void rightStart(){
		mySideMotor.triggerBackwardOn();
	}
	
	public void rightStop() {
		mySideMotor.triggerBackwardOff();
	}
	
	public void goToHeight(double newTargetHeight) {
		myHeightManager.setTargetHeight(newTargetHeight);
	}
	
	public void terminate() {
		client.stopRunning();
		myHeightManager.stopRunning();	
		System.exit(0);
	}
	public double getTargetHeight() {
		return myHeightManager.getTargetHeight();
	}
	
	public MotorFixed getFrontMotor() {
		return myFrontMotor;
	}
	
	public MotorFixed getSideMotor() {
		return mySideMotor;
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
	
	public void forward(int amount) {
//		for (int i = 0; i < amount; i++) {
			forwardStart();
			waitForXMillis(amount);
			forwardStop();
//		}
	}
	
	public void backward(int amount) {
//		for (int i = 0; i < amount; i++) {
			backwardStart();
			waitForXMillis(amount);
			backwardStop();
//		}
	}
	
	public void left(int amount) {
//		for (int i = 0; i < amount; i++) {
			leftStart();
			waitForXMillis(amount);
			leftStop();
//		}
	}
	
	public void right(int amount) {
//		for (int i = 0; i < amount; i++) {
			rightStart();
			waitForXMillis(amount);
			rightStop();
//		}
	}
	
	private void waitForXMillis(int number) {
		long referentionTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < referentionTime + (long)number) {
			//no-op
		}
	}
	
	public void setMiddelpunt(int x, int y) {
		middelpunt = new Vector(x, y);
	}
	
	public Vector getMiddelpunt() {
		return middelpunt;
	}
	public PositionManager getMyPositionManager() {
		return myPositionManager;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
		myPositionManager.moveToNextPosition();
	}
	
	public double getRotation(){
		return rotation;
	}
}
