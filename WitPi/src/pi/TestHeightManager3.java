package pi;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class TestHeightManager3 {
	public static void main(String[] args) throws InterruptedException {
		Pin forw1 = RaspiPin.GPIO_07;
		Pin back1 = RaspiPin.GPIO_05;
		MotorPwm myMotor = new MotorPwm(forw1, back1);
		DistanceMonitor myDistance = new DistanceMonitor();
		HeightManager3 myHM = new HeightManager3(myMotor, myDistance, 820, 1024);
		Thread tmyHM = new Thread(myHM);
		tmyHM.start();
		//Thread.sleep(200000);
		//myHM.terminate();
	}
}

