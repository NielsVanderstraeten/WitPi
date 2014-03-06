package pi;

/**
 * Class to monitor distance measured by a HC-SR04 distance sensor on a
 * Raspberry Pi.
 *
 * The main method assumes the trig pin is connected to the pin # 7 and the echo
 * pin is connected to pin # 11.  Output of the program are comma separated lines
 * where the first value is the number of milliseconds since unix epoch, and the
 * second value is the measured distance in centimeters.
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Arrays;


/**
 * DistanceMonitor class to monitor distance measured by sensor
 *
 * @author Team Wit
 */
public class DistanceMonitor {

	private final static float SOUND_SPEED = 343.00f;  // speed of sound in m/s
	private final static int TRIG_DURATION_IN_MICROS = 10; // trigger duration of 10 micro s
	private final static Pin trig = RaspiPin.GPIO_13; // GPIO 9 /// fysiek: 21
	private final static Pin echo = RaspiPin.GPIO_11; // GPIO 7 /// fysiek: 26

	private final static int TIMEOUT = 2100;

	private final static GpioController gpio = GpioFactory.getInstance();

	private final GpioPinDigitalInput echoPin = gpio.provisionDigitalInputPin(echo);
	private final GpioPinDigitalOutput trigPin = gpio.provisionDigitalOutputPin(trig);

	public DistanceMonitor() {
		this.trigPin.low();
	}
	/*
	 * This method returns the median of measured distances.
	 *
	 */
	public float getDistance() {
		// N is the number of measurements.
		long prevTime = 0;
		int amount = 0;
		int N = 10;
		float[] measurements = new float[N];
		while(amount < N){
			long currTime = System.currentTimeMillis();
			if((currTime - prevTime ) > 30){
				measurements[amount] = measureDistance();
				prevTime = currTime;
				amount++;
			}
		}
		// Sort the measurements and return the median from the sorted array.
				Arrays.sort(measurements);
		float dist = 0;
		if(measurements.length%2 == 0){
			dist =((float)measurements[N/2-1] + (float)measurements[N/2])/2;
		}
		else{
			dist =(float) measurements[N/2];
		}
		return dist;

	}
	/*
	 * This method returns the distance measured by the sensor in cm
	 *
	 * @throws TimeoutException if a timeout occurs
	 */
	public float measureDistance() {
		try{
			this.triggerSensor();
			this.waitForSignal();
			long duration = this.measureSignal();
			return duration * SOUND_SPEED / ( 2.0f * 10000 );
		}
		catch(TimeoutException e){
			return -1;
		}
	}
	/**
	 * Put a high on the trig pin for TRIG_DURATION_IN_MICROS
	 */
	/*private void triggerSensor() {
		try {
			this.trigPin.high();
			Thread.sleep( 0, TRIG_DURATION_IN_MICROS * 1000 );
			this.trigPin.low();
		} catch (InterruptedException ex) {
			System.err.println( "Interrupt during trigger" );
		}
	}*/
	private void triggerSensor() {
		this.trigPin.high();
		long start = System.nanoTime();
		while((System.nanoTime() - start)/1000 < TRIG_DURATION_IN_MICROS){
		}
		this.trigPin.low();
	}

	/**
	 * Wait for a high on the echo pin
	 *
	 * @throws DistanceMonitor.TimeoutException if no high appears in time
	 */
	private void waitForSignal() throws TimeoutException {
		int countdown = TIMEOUT;

		while( this.echoPin.isLow() && countdown > 0 ) {
			countdown--;
		}

		if( countdown <= 0 ) {
			throw new TimeoutException( "Timeout waiting for signal start" );
		}
	}
	/**
	 * @return the duration of the signal in micro seconds
	 * @throws DistanceMonitor.TimeoutException if no low appears in time
	 */
	private long measureSignal() throws TimeoutException {
		int countdown = TIMEOUT;
		long start = System.nanoTime();
		while( this.echoPin.isHigh() && countdown > 0 ) {
			countdown--;
		}
		long end = System.nanoTime();

		if( countdown <= 0 ) {
			throw new TimeoutException( "Timeout waiting for signal end" );
		}

		return (long)Math.ceil( ( end - start ) / 1000.0 );  // Return micro seconds
	}

	/**
	 * Exception thrown when timeout occurs
	 */
	public static class TimeoutException extends Exception {

		private final String reason;

		public TimeoutException( String reason ) {
			this.reason = reason;
		}

		@Override
		public String toString() {
			return this.reason;
		}
	}
}