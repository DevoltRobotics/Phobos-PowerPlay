package org.firstinspires.ftc.phoboscode.vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import org.firstinspires.ftc.robotcore.external.Telemetry;

//import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;
import java.util.List;

//@Config
public class ConeTrackingPipeline extends OpenCvPipeline {

    public Scalar lowerRed = new Scalar(0, 0, 0);
    public Scalar upperRed = new Scalar(255, 255, 255);

    public Scalar lowerBlue = new Scalar(112, 96, 53);
    public Scalar upperBlue = new Scalar(119, 212, 255);

    public static double blurValue = 31;

    public static double erosionValue = 1;
    public static double dilationValue = 3;

    public boolean displayThreshold = false;

    private Mat blur = new Mat();

    private Mat threshold = new Mat();

    int framesWithLastRect = 0;
    private Rect lastRect = null;

    int framesDiscardingDelta = 0;
	
	public int maxFramesHistoricalData = 10;

    public double weightingDifferenceThreshold = 10;
    public double discardingDifferenceThreshold = 20;
  
    public double historicWeight = 0.3;
    public double currentWeight = 0.7;
	
	public double minRectRatio = 0.1;
	public double maxRectRatio = 0.9;

	public static double centerXOffset = 0d;

	private double lastErrorFromCenter = 0d;

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.GaussianBlur(input, blur, new Size(blurValue, blurValue), 0);

        List<MatOfPoint> contours = thresholdAndCountors(blur, threshold, Imgproc.COLOR_RGB2HSV, lowerBlue, upperBlue, erosionValue, dilationValue);

        Imgproc.drawContours(input, contours, -1, new Scalar(200, 0, 255), 3, 8);

        Rect biggestRect = null;
		
		double rectRatio;
		
		double imageXCenter = input.cols() / 2d + centerXOffset;

        for(MatOfPoint points : contours) {
            Rect rect = Imgproc.boundingRect(points);

			if(biggestRect == null || (rect.area() > biggestRect.area() && rect.height >= rect.width)) {
				double xDistanceFromCenter = Math.abs((rect.x + rect.width / 2d) - imageXCenter);
				
				if(biggestRect != null) {
					double currentXDistanceFromCenter = Math.abs((biggestRect.x + biggestRect.width / 2d) - imageXCenter);
					
					if(currentXDistanceFromCenter < xDistanceFromCenter) {
						continue;
					}

					if(lastRect != null) {
						int xDelta = Math.abs(rect.x - lastRect.x);
						int biggestXDelta = Math.abs(rect.x - biggestRect.x);

						if(rect.area() < lastRect.area() && biggestXDelta >= xDelta) {
							continue;
						}
					}
				}
				
				rectRatio = (double)rect.width / rect.height;
				
				if(rectRatio >= minRectRatio && rectRatio <= maxRectRatio) {
					biggestRect = rect;
				}
			}
        }
	
        if(biggestRect != null) {
			if(lastRect != null) {
				int deltaSums = 0;
				
				int xDelta = Math.abs(biggestRect.x - lastRect.x);
				if(xDelta >= discardingDifferenceThreshold && framesDiscardingDelta <= maxFramesHistoricalData) {
					biggestRect.x = lastRect.x;
					deltaSums += xDelta;
				}
				
				int yDelta = Math.abs(biggestRect.y - lastRect.y);
				if(yDelta >= discardingDifferenceThreshold && framesDiscardingDelta <= maxFramesHistoricalData) {
					biggestRect.y = lastRect.y;
					deltaSums += yDelta;
				}
				
				int widthDelta = Math.abs(biggestRect.width - lastRect.width);
				if(widthDelta >= discardingDifferenceThreshold && framesDiscardingDelta <= maxFramesHistoricalData) {
					biggestRect.width = lastRect.width;
					deltaSums = widthDelta;
				}
				
				int heightDelta = Math.abs(biggestRect.height - lastRect.height);
				if(heightDelta >= discardingDifferenceThreshold && framesDiscardingDelta <= maxFramesHistoricalData) {
					biggestRect.height = lastRect.height;
					deltaSums += heightDelta;
				}
				
				if(deltaSums >= discardingDifferenceThreshold * 4 && framesDiscardingDelta <= maxFramesHistoricalData) {
					biggestRect = lastRect;
				} else {
					if(xDelta >= weightingDifferenceThreshold) {
						biggestRect.x = calcAvgHistCurr(lastRect.x, biggestRect.x);
					}
					if(yDelta >= weightingDifferenceThreshold) {
						biggestRect.y = calcAvgHistCurr(lastRect.y, biggestRect.y);
					}

					if(widthDelta >= weightingDifferenceThreshold) {
						biggestRect.width = calcAvgHistCurr(lastRect.width, biggestRect.width);
					}
					if(heightDelta >= weightingDifferenceThreshold) {
						biggestRect.height = calcAvgHistCurr(lastRect.height, biggestRect.height);
					}
					
					if(deltaSums == 0) {
						framesDiscardingDelta = 0;
					}
				}
			}

			lastRect = biggestRect;
			framesWithLastRect = 0;
		} else if(lastRect != null) {
			biggestRect = lastRect;
		}

        if(biggestRect != null) {
            Imgproc.rectangle(input, biggestRect, new Scalar(0, 100, 255), 5);
        }

		if(framesWithLastRect >= maxFramesHistoricalData) {
			lastRect = null;
		}
		
		framesWithLastRect++;
		framesDiscardingDelta++;
		
		if(biggestRect != null) {
			double rectXCenter = biggestRect.x + biggestRect.width / 2d;

			setLastErrorFromCenter(rectXCenter - imageXCenter);
		}
		
		//telemetry.update();

		if(displayThreshold) {
			return threshold;
		} else {
			return input;
		}
    }

	private synchronized void setLastErrorFromCenter(double error) {
		lastErrorFromCenter = error;
	}

	public synchronized double getLastErrorFromCenter() {
		return lastErrorFromCenter;
	}

    private int calcAvgHistCurr(int historic, int current) {
		return (int) (( (historic * historicWeight) + (current * currentWeight) ) / (historicWeight + currentWeight) );
    }

    private Mat cvtMat = new Mat();

    public List<MatOfPoint> thresholdAndCountors(Mat input, Mat thresholdOutput, int conversion, Scalar min, Scalar max, double erosion, double dilation) {
        Imgproc.cvtColor(input, cvtMat, conversion);
        Core.inRange(cvtMat, min, max, thresholdOutput);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erosion + 1, erosion+ 1));
        Imgproc.erode(thresholdOutput, thresholdOutput, element);
        Imgproc.erode(thresholdOutput, thresholdOutput, element);
		element.release();

		element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilation + 1, dilation+ 1));
        Imgproc.dilate(thresholdOutput, thresholdOutput, element);
        Imgproc.dilate(thresholdOutput, thresholdOutput, element);
		element.release();

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdOutput, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        return contours;
    }
}
