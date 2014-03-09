package pi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class Executor implements Runnable{
	
	private String command;
	private Pi pi;
	private int forwardOn;
	private int forwardOff;
	private int backwardOn;
	private int backwardOff;
	private int turnforwardOn;
	private int turnbackwardOnExtraTime;
	private int turnOff;
	private final double forwardTreshold = 150; // = afstand vanaf waar snelheid constant blijft.
	private double forwardStopParam; // = parameter om tot stilstand te komen, voorwaarts vliegen
	private final double backwardTreshold = 100; // = afstand vanaf waar snelheid constant blijft.
	private double backwardStopParam; // = parameter om tot stilstand te komen, achterwaarts vliegen
	private final double turnTreshold = 80; // = hoek vanaf waar hoeksnelheid constant blijft.
	private double turnStopParam; // = parameter om tot stilstand te komen, draaien.
	private double extraBackwardParam = 0.8;
	

	public Executor(Pi pi, String command) {
		this.command = command;
		this.pi = pi;
		
		forwardOn = 100;
		forwardOff = 200;
		backwardOn = 600;
		backwardOff = 100;
		turnforwardOn = 80;
		turnbackwardOnExtraTime = 50;
		turnOff = 200;
		forwardStopParam = 30;
		backwardStopParam = 8;
		turnStopParam = 12;
	}
	
	public synchronized void run(){
		if (command.contains("goforward ")) {
			List<String> strings = Arrays.asList(command.split("\\s+"));
			double distance = Integer.parseInt(strings.get(1).toString());
			//nieuwe manier:
			//omrekenen naar aantal pulsen:
			double a = -0.04; // = ???
			double b = 43;
			double c = 1535;
			if (distance > 100) { // if volledig weg indien faalt,inclusief body -> TODO
				distance = distance/2;
				double amount = a * Math.pow(distance, 2) + b * distance + c;
				forwardPulse((int)((amount-getForwardStopTime(distance))/300));
				backward(getForwardStopTime(distance));
				forwardPulse((int)((amount-getForwardStopTime(distance))/300));
				backward(getForwardStopTime(distance));
			}
			else { // "else weg, body blijft -> TODO
				double amount = a * Math.pow(Integer.parseInt(strings.get(1).toString()), 2) + b * Integer.parseInt(strings.get(1).toString()) + c;
				forwardPulse((int)((amount-getBackwardStopTime(distance))/300));
				backward(getForwardStopTime(distance));
			}
		}
		else if (command.contains("gobackward ")) {
			List<String> strings = Arrays.asList(command.split("\\s+"));
			double distance = Double.parseDouble(strings.get(1).toString());
			//nieuwe manier:
			//omrekenen naar aantal pulsen:
			double a = -0.04; // = ???
			double b = 43;
			double c = 1535;
			if (distance > 100) { // if volledig weg indien faalt, inclusief body -> TODO
				distance = distance/2;
				double amount = a * Math.pow(distance, 2) + b * distance + c;
				backwardPulse((int)((amount-getBackwardStopTime(distance))/475));
				forward(getBackwardStopTime(distance));
				backwardPulse((int)((amount-getBackwardStopTime(distance))/475));
				forward(getBackwardStopTime(distance));
			}
			else { // "else weg, body blijft -> TODO
				double amount = a * Math.pow(Integer.parseInt(strings.get(1).toString()), 2) + b * Integer.parseInt(strings.get(1).toString()) + c;
				backwardPulse((int)((amount-getBackwardStopTime(distance))/475));
				forward(getBackwardStopTime(distance));
			}
			
		}
		else if (command.contains("turnleft ")) {
			List<String> strings = Arrays.asList(command.split("\\s+"));
			double angle = Integer.parseInt(strings.get(1).toString());
			//nieuwe manier:
			//omrekenen naar aantal pulsen:
			double a = -0.04; // = ???
			double b = 43;
			double c = 1535;
			double amount = a * Math.pow(Integer.parseInt(strings.get(1).toString()), 2) + b * Integer.parseInt(strings.get(1).toString()) + c;
			leftPulse((int)((amount-getTurnStopTime(angle))/380*1.2));
			turnRight(getTurnStopTime(angle));
		}
		else if (command.contains("turnright ")) {
			List<String> strings = Arrays.asList(command.split("\\s+"));
			double angle = Integer.parseInt(strings.get(1).toString());
			//nieuwe manier:
			//omrekenen naar aantal pulsen:
			double a = -0.04; // = ???
			double b = 43;
			double c = 1535;
			double amount = a * Math.pow(Integer.parseInt(strings.get(1).toString()), 2) + b * Integer.parseInt(strings.get(1).toString()) + c;
			rightPulse((int)((amount-getTurnStopTime(angle))/380*1.2));
			turnLeft(getTurnStopTime(angle));
		}

		//vanaf hier: wordt allemaal opgelost in Listener!!! (oude code)

		/*else if (command.contains("climb ")) {
						List<String> strings = Arrays.asList(command.split("\\s+"));
						pi.climbStart();
						try {
							Thread.sleep(Integer.parseInt(strings.get(1)));
						} catch(InterruptedException ex) {
						    Thread.currentThread().interrupt();
						}
						pi.climbStop();
					}
					else if ( command.contains("descend ")) {
						List<String> strings = Arrays.asList(command.split("\\s+"));
						pi.descendStart();
						try {
							Thread.sleep(Integer.parseInt(strings.get(1)));
						} catch(InterruptedException ex) {
						    Thread.currentThread().interrupt();
						}
						pi.descendStop();
					}
					else if(command.equals("forwardstart"))
						pi.forwardStart();
					else if(command.equals("forwardstop"))
						pi.forwardStop();
					else if(command.equals("backwardstart"))
						pi.backwardStart();
					else if(command.equals("backwardstop"))
						pi.backwardStop();
					else if(command.equals("climbstart"))
						pi.climbStart();
					else if(command.equals("climbstop"))
						pi.climbStop();
					else if(command.equals("descendstart"))
						pi.descendStart();
					else if(command.equals("descendstop"))
						pi.descendStop();
					else if(command.equals("turnrightstart"))
						pi.turnRightStart();
					else if(command.equals("turnrightstop"))
						pi.turnRightStop();
					else if(command.equals("turnleftstart"))
						pi.turnLeftStart();
					else if(command.equals("turnleftstop"))
						pi.turnLeftStop();
					else if(command.contains("stayatheight")){
						String[] split = command.split("\\s+");
						if(split.length == 2 ){
							System.out.println("stayatheight "+split[1]);
							double height = Double.parseDouble(split[1]);
							pi.goToHeight(height);
						}
						else{
							//onbekend commando
						}
					}*/
		else {
			// commando niet gevonden/verwerkt!
		}
	}
	
	public void forwardPulse(int amount) {
		for (int i = 0; i < amount; i++) {
			pi.forwardStart();
			waitForXMillis(forwardOn);
			pi.forwardStop();
			waitForXMillis(forwardOff);
		}
	}
	
	public void backwardPulse(int amount) {
		for (int i = 0; i < amount; i++) {
			pi.backwardStart();
			waitForXMillis(backwardOn);
			pi.backwardStop();
			waitForXMillis(backwardOff);
		}
	}
	
	public void rightPulse(int amount) {
		for (int i = 0; i < amount; i++) {
			pi.rightStart();
			waitForXMillis(backwardOn);
			pi.rightStop();
			waitForXMillis(backwardOff);
		}
	}
	
	public void leftPulse(int amount) {
		for (int i = 0; i < amount; i++) {
			pi.leftStart();
			waitForXMillis(backwardOn);
			pi.leftStop();
			waitForXMillis(backwardOff);
		}
	}
	
	private void waitForXMillis(int number) {
		long referentionTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < referentionTime + (long)number) {
			
		}
	}
	
	public int getForwardStopTime(double distance) {
		if (distance > forwardTreshold) {
			return (int)(forwardTreshold * forwardStopParam);
		}
		else {
			return (int)(distance * forwardStopParam);
		}		
	}
	
	public int getBackwardStopTime(double distance) {
		if (distance > backwardTreshold) {
			return (int)(backwardTreshold * backwardStopParam);
		}
		else {
			return (int)(distance * backwardStopParam);
		}
	}
	
	public int getTurnStopTime(double angle) {
		if (angle > turnTreshold) {
			return (int)(turnTreshold * turnStopParam);
		}
		else {
			return (int)(angle * turnStopParam);
		}
	}
	
	public void forward(int time) {
		pi.forwardStart();
		waitForXMillis(time);
		pi.forwardStop();
	}
	
	public void backward(int time) {
		pi.backwardStart();
		waitForXMillis(time);
		pi.backwardStop();
	}
	
//	public void turnLeft(int time) {
//		pi.getFrontMotor().triggerBackwardOn();
//		waitForXMillis((int)(time * extraBackwardParam));
//		pi.getSideMotor().triggerForwardOn();
//		waitForXMillis(time);
//		pi.getSideMotor().triggerForwardOff();
//		waitForXMillis((int)(time * extraBackwardParam));
//		pi.getFrontMotor().triggerBackwardOff();
//	}
//	
//	public void turnRight(int time) {
//		pi.getSideMotor().triggerBackwardOn();
//		waitForXMillis((int)(time * extraBackwardParam));
//		pi.getFrontMotor().triggerForwardOn();
//		waitForXMillis(time);
//		pi.getFrontMotor().triggerForwardOff();
//		waitForXMillis((int)(time * extraBackwardParam));
//		pi.getSideMotor().triggerBackwardOff();
//	}
}