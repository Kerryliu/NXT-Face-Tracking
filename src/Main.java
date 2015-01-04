import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import lejos.nxt.Motor;

import org.opencv.core.Core;

public class Main {

	private static JLabel pic = new JLabel();
	private static JFrame jframe = new JFrame();
	public static int xTol = 60;
	public static int yTol = 35;
	
	private static int motorASpeed, motorBSpeed;
	
	public static void main(String[] args) {
		// Initialize
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


		while (true) {
			Vision.runVision();

			// Display feed:
			pic.setIcon(new ImageIcon(Vision.bufImage));
			jframe.setSize(Vision.rgbFrame.width(), Vision.rgbFrame.height());
			jframe.add(pic);
			jframe.repaint();
			jframe.setVisible(true);

			// Speed proportional to how close face is
			double motorASpeedDouble = Math.sqrt(Vision.faceSize)*(Vision.xOffSet*0.1);
			double motorBSpeedDouble = Math.sqrt(Vision.faceSize)*(Vision.yOffSet*0.05);

			motorASpeed = (int) Math.round(motorASpeedDouble);
			motorBSpeed = (int) Math.round(motorBSpeedDouble);

			Motor.A.setSpeed(motorASpeed);
			Motor.B.setSpeed(motorBSpeed);
			Motor.C.setSpeed(100);
		
				if (Vision.xCoord != 0 && Math.abs(Vision.xCoord - 320) > xTol) {
					if (Vision.xCoord < 320 && Motor.A.getTachoCount() < 400) {
						Motor.A.forward();
					} else if (Vision.xCoord > 320
							&& Motor.A.getTachoCount() > -400) {
						Motor.A.backward();
					} else {
						Motor.A.stop();
					}
				} else {
					Motor.A.stop();
				}

				if (Vision.yCoord != 0 && Math.abs(Vision.yCoord - 240) > yTol) {
					if (Vision.yCoord > 240 && Motor.B.getTachoCount() > -150) {
						Motor.B.backward();
					} else if (Vision.yCoord < 240
							&& Motor.B.getTachoCount() < 200) {
						Motor.B.forward();
					} else {
						Motor.B.stop();
					}
				} else {
					Motor.B.stop();
				}
				if (Vision.numFaces == 1 && Math.abs(Vision.xCoord - 320) < xTol && Math.abs(Vision.yCoord - 240) < yTol) {
					Motor.C.forward();
				} else {
					Motor.C.stop();
				}
		}
	}

}
