package Components;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
//import com.pi4j.io.gpio.RaspiPin;

/**
 * 
 * @author Team Wit
 *
 */
public class MotorFixed {
	protected final static GpioController gpio = GpioFactory.getInstance();
	// twee pinnen: 1 om voorwaarts te draaien, 1 om achterwaarts te draaien
	protected final GpioPinDigitalOutput trigPinForward;
	protected final GpioPinDigitalOutput trigPinBackward;
	protected boolean forwardOn = false;
	protected boolean backwardOn = false;

	public MotorFixed(Pin trigPinForward, Pin trigPinBackward){
		this.trigPinForward = gpio.provisionDigitalOutputPin(trigPinForward);
		this.trigPinBackward = gpio.provisionDigitalOutputPin(trigPinBackward);
		this.trigPinForward.low();
		this.trigPinBackward.low();
	}		
	
	public void triggerForwardOn(){
		if(backwardOn){
			trigPinBackward.low();
			backwardOn = false;
		}
		trigPinForward.high();
                forwardOn = true;
	}
	
	public void triggerForwardOff(){
		trigPinForward.low();
		forwardOn = false;
	}

	public void triggerBackwardOn(){
		if(forwardOn){
			trigPinForward.low();
			forwardOn = false;
		}
		trigPinBackward.high();
		backwardOn = true;
	}

	public void triggerBackwardOff(){
		trigPinBackward.low();
		backwardOn = false;
	}

	/*public static void main( String[] args ) {
		//Motor1
		Pin forw = RaspiPin.GPIO_05;
		Pin back = RaspiPin.GPIO_07;
		//Motor2
		//Pin forw = RaspiPin.GPIO_04;
		//Pin back = RaspiPin.GPIO_00;
		//Motor3
		//Pin forw = RaspiPin.GPIO_13;
		//Pin back = RaspiPin.GPIO_11;
		//Motor4
		//Pin forw = RaspiPin.GPIO_12;
		//Pin back = RaspiPin.GPIO_14;
		try{
			System.out.println("Hello");
			MotorFixed fixedMotor = new MotorFixed(forw, back);
			fixedMotor.triggerForwardOn();
			System.out.println("Forward spin on");
			Thread.sleep(3000);
			fixedMotor.triggerForwardOff();
			System.out.println("Forward spin off");
			Thread.sleep(3000);
			fixedMotor.triggerBackwardOn();
			System.out.println("Backward spin on");
			Thread.sleep(3000);
			fixedMotor.triggerBackwardOff();
			System.out.println("Backward spin off");
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		}

	}*/
}
