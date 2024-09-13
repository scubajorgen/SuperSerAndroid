package net.studioblueplanet.superset.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * This class represents a polygon
 * @author jorgen
 *
 */
public class Polygon
{
	public static final int X=0;
	public static final int Y=1;
	
	private int[][]			points;
	
	public Polygon(int[][] points)
	{
		this.points=points;
	}
	
	private Polygon()
	{
		
	}
	
	/**
	 * This method draws the polygon on the canvas
	 * @param canvas The canvas to draw on
	 * @param x position of the center of the polygon, x-coordinage
	 * @param y position of the center of the polygon, y-coordinage
	 * @param outlinePaint The Paint to use for the outline
	 * @param fillPaint The Paint to use for the fill, or null if not filled
	 */
	public void draw(Canvas canvas, int x, int y, Paint outlinePaint, Paint fillPaint)
	{
    	int i;
    	int len;
    	
        // line at minimum...
        if (points.length < 2) 
        {
            return;
        }

        // path
        Path polyPath = new Path();
        polyPath.moveTo(points[0][X]+x, points[0][Y]+y);
        i=1;
        len = points.length;
        while (i<len) 
        {
            polyPath.lineTo(points[i][X]+x, points[i][Y]+y);
            i++;
        }
        polyPath.lineTo(points[0][X]+x, points[0][Y]+y);

        // draw
        if (fillPaint!=null)
        {
        	canvas.drawPath(polyPath, fillPaint);    	
        }
    	canvas.drawPath(polyPath, outlinePaint);    		
	}
	
	/**
	 * Create a scaled version of the polygon with indicated width and height
	 * @param sourcePolygon Source polygon, polygon to scale
	 * @param width Width the new polygon must have
	 * @param height Height the new polygon must have
	 * @return
	 */
	public static Polygon scalePolygon(Polygon sourcePolygon, int width, int height)
	{
		Polygon scaledPolygon;
		int     size;
    	int i;
    	int maxX;
    	int maxY;
    	int minX;
    	int minY;
		
		size=sourcePolygon.points.length;
		
		scaledPolygon=new Polygon();
		scaledPolygon.points=new int[size][2];
		


    	// Find the max. dimensions of the original polygon
    	i=0;
    	maxX=-1000000;
    	maxY=-1000000;
    	minX=1000000;
    	minY=1000000;
    	while (i<size)
    	{
    		if (sourcePolygon.points[i][X]>maxX)
    		{
    			maxX=sourcePolygon.points[i][X];
    		}
    		if (sourcePolygon.points[i][Y]>maxY)
    		{
    			maxY=sourcePolygon.points[i][Y];
    		}
    		if (sourcePolygon.points[i][X]<minX)
    		{
    			minX=sourcePolygon.points[i][X];
    		}
    		if (sourcePolygon.points[i][Y]<minY)
    		{
    			minY=sourcePolygon.points[i][Y];
    		}
    		i++;
    	}

    	// scale the polygon and write to destination list
    	i=0;
    	while (i<size)
    	{
    		scaledPolygon.points[i][X]=sourcePolygon.points[i][X]*width/(maxX-minX);
    		scaledPolygon.points[i][Y]=sourcePolygon.points[i][Y]*height/(maxY-minY);
    		i++;
    	}

		return scaledPolygon;
	}

}
