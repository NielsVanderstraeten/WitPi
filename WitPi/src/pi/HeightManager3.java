package pi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;


public class HeightManager3 implements Runnable {
	
	private double targetHeight;
	private DistanceMonitor myDistance;
	private boolean running = true;
	private double maxPower;
	private double minPower;
	private MotorPwm heightmotor;
	private int direction; // 0 = uit; 1 = forward; 2 = backward
	private double accumulator;
	private float lastCalculatedHeight = 0;
	
	private double [] error;
	
	public HeightManager3(MotorPwm heightMotor, DistanceMonitor distanceMonitor, double minPower, double maxPower){
		myDistance = distanceMonitor;
		this.maxPower = maxPower;
		this.minPower = minPower;
		this.heightmotor = heightMotor;
		this.direction = 0;
		this.error = new double[10];
		this.accumulator = 0;
		setTargetHeight(80);
	}
	
	private double pTerm = 2;
	private double iTerm = 0;
	private double dTerm = 2;
	
	public synchronized void run() {
		while(running){
			double pid;
			calcError();
			
			pid = pTerm * error[0];
			accumulator += error[0];
			pid += iTerm * accumulator;
			pid += dTerm * (error[0] - error[3]);
			if(Math.abs(pid) > (maxPower - minPower)){
				pid = maxPower - minPower;
				accumulator -= error[0];
			}
			int power = (int) Math.round(Math.abs(pid)+minPower);
			if(pid < 0){
				if(!(direction==2))
					startDownward(power);
				else
					setPower(power);
			}
			else{
				if(!(direction==1))
					startUpward(power);
				else
					setPower(power);
			}
		}
	}

	private void calcError(){
		for(int i=(error.length-1); i > 0; i--)
			error[i] = error[i-1];
		//float newDistance = myDistance.getDistance();
		lastCalculatedHeight = myDistance.getDistance();
//		try {
//			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
//			out.write((int) newDistance);
//			out.close();
//		} catch(Exception e) {}
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		error[0] = targetHeight - (double)lastCalculatedHeight;
		//System.out.println("newDistance: "+lastCalculatedHeight);
	}

	public void terminate(){
		running = false;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		heightmotor.setPower(0);
	}
	
	private void startUpward(int power){
		heightmotor.triggerBackwardOff();
		heightmotor.triggerForwardOn();
		direction = 1;
		setPower(power);
	}
	
	private void startUpward(){
		startUpward((int)minPower);
	}
	
	private void startDownward(){
		startDownward((int)minPower);
	}
	
	private void startDownward(int power){
		heightmotor.triggerForwardOff();
		heightmotor.triggerBackwardOn();
		direction = 2;
		setPower(power);
	}
	
	public void setTargetHeight(double newTargetHeight){
		targetHeight = newTargetHeight;
	}
	
	private void setPower(double voltage) {
		if(voltage>maxPower)
			voltage = maxPower;
		if(voltage<minPower)
			voltage = minPower;
		heightmotor.setPower((int)voltage);
	}
	public void stopRunning() {
		running = false;
		
		long prevTime = System.currentTimeMillis();
		long currTime = System.currentTimeMillis();
		while(currTime - prevTime > 1000){
			currTime = System.currentTimeMillis();
		}
		heightmotor.setPower(0);
	}

	public double getTargetHeight() {
		return targetHeight;
	}
	
	public float getLastCalculatedHeight(){
		return lastCalculatedHeight;
	}
}