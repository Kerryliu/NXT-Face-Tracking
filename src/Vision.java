import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Vision {

	private static final MatOfRect faceDetections = new MatOfRect();
	private static final VideoCapture camera = new VideoCapture(0);

	public static Mat rgbFrame = new Mat();
	public static Mat greyFrame = new Mat();

	public static int xCoord, yCoord, numFaces, faceSize, xOffSet, yOffSet;
	public static BufferedImage bufImage = null;
	
	private static int noiseReducingThing = 0;

	public static void runVision() {
		String asdf = Vision.class.getResource("lbpcascade_frontalface.xml").getPath();
		asdf = asdf.substring(1); // OpenCV is retarded.
		CascadeClassifier faceDetector = new CascadeClassifier(asdf);


		camera.read(rgbFrame);
		Imgproc.cvtColor(rgbFrame, greyFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(greyFrame, greyFrame);

		Core.rectangle(rgbFrame, new Point(320 - Main.xTol, 240 - Main.yTol), new Point(320 + Main.xTol, 240 + Main.yTol), new Scalar(255, 0, 0));

		faceDetector.detectMultiScale(greyFrame, faceDetections);

		numFaces = faceDetections.toArray().length;

		if (Vision.numFaces > 0) {
			noiseReducingThing++;
			if (noiseReducingThing > 5) {
				noiseReducingThing = 5;
			}
		}
		else if (noiseReducingThing > 0) {
			noiseReducingThing--;
			xCoord = 0;
			yCoord = 0;
			faceSize = 0;
			xOffSet = 0;
			yOffSet = 0;
		}
		
		if (noiseReducingThing > 2) {
			System.out.println("Detected " + numFaces + " faces");

			for (Rect rect : faceDetections.toArray()) {
				Core.rectangle(rgbFrame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));

				xCoord = rect.x + rect.width / 2;
				yCoord = rect.y + rect.height / 2;
				faceSize = rect.width;
				xOffSet = Math.abs(320-xCoord);
				yOffSet = Math.abs(240-yCoord);
				// Will center on the last face in array.

				Point point = new Point(xCoord, yCoord);
				Core.circle(rgbFrame, point, 5, new Scalar(0, 0, 255));
			}

			System.out.println("x-coord:" + xCoord);
			System.out.println("y-coord:" + yCoord);
			System.out.println("Face size: " + faceSize + "px");
			
			if(numFaces == 0) {
				xCoord = 0;
				yCoord = 0;
				faceSize = 0;
			}
		}

			// Convert from mat to jpg
			MatOfByte matOfByte = new MatOfByte();
			Highgui.imencode(".jpg", rgbFrame, matOfByte);
			byte[] byteArray = matOfByte.toArray();
			try {
				InputStream in = new ByteArrayInputStream(byteArray);
				bufImage = ImageIO.read(in);
			} catch (Exception e) {
				e.printStackTrace();
			}


	}
}
