package components;
import java.io.File;
import java.io.IOException;

/**
* Class to control a camera attached to a Raspberry PI.
*/
public class Camera{

public Camera(){}

/**
* Takes a picture and saves it to the main file location of the PI under the name "picture.jpg".
*/
public void makePicture() throws IOException{
	try{
		File file = new File("picture.jpg");
		file.delete();
		/*
		 * -o <filename>	output naar <filename>
		 * -n				no preview
		 * -w <size>		width van de foto
		 * -h <size>		height van de foto
		 * -t <time>		betekent <time> seconden wachten voor foto genomen wordt
		 */
	    Process p = Runtime.getRuntime().exec("raspistill -o picture.jpg -n -w 750 -h 562 -t 0");
	    try {
			p.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	    long prevTime = System.currentTimeMillis();
//		long currTime = System.currentTimeMillis();
//		while(currTime - prevTime > 3000){
//			currTime = System.currentTimeMillis();
//		}
	}
	catch(IOException ieo){
		ieo.printStackTrace();
	}
}

}