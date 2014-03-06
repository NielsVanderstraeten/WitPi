package Components;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
//import com.pi4j.io.gpio.GpioController;
//import com.pi4j.io.gpio.GpioFactory;
//import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class MotorPwm extends MotorFixed{
	private final GpioPinPwmOutput pwmPin = gpio.provisionPwmOutputPin(RaspiPin.GPIO_01);

	public MotorPwm(Pin trigPinForward, Pin trigPinBackward){
		super(trigPinForward, trigPinBackward);
		pwmPin.setPwm(0);
		this.trigPinForward.low();
		this.trigPinBackward.low();
	}
	
	public void setPower(int x){
		if(x >= 0  && x <=1024){	
		pwmPin.setPwm(x);
		}
		else{
			System.err.println("Tried to set the power of Pwm motor to a value out of range.");
		}
	}
	
	public int getPower(){
		return pwmPin.getPwm();
	}
	
	/*public static void main( String[] args ) {
		try{
			System.out.println("Hello");
			//Motor1
			Pin forw1 = RaspiPin.GPIO_07;
			Pin back1 = RaspiPin.GPIO_05;
			MotorPwm motor = new MotorPwm(forw, back);
			
			motor.triggerForwardOn();
			motor.setPower(1024);
			System.out.println("Forward with power = "+motor.getPower());
			Thread.sleep(3000);
			motor.setPower(800);
			System.out.println("Forward with power = "+motor.getPower());
			Thread.sleep(3000);
			motor.triggerForwardOff();
			System.out.println("Backward with power = "+motor.getPower());
			motor.triggerBackwardOn();
			Thread.sleep(3000);
			System.out.println("Backward with power = "+motor.getPower());
			motor.setPower(1200);
			Thread.sleep(3000);
			motor.setPower(0);
			motor.triggerForwardOff();
			System.out.println("Off with power = "+motor.getPower());
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		}
	}*/
}