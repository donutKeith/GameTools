package KeithTools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.io.File;
import java.util.Vector;

/**
 * Created by Keith on 11/5/2016.
 */
public class Collider {
    private Vector2[] verts;
    private Vector2 center;
    private Sprite sprite;

    private static final int START = 0;
    private static final int END = 1;
    private Vector2 newOrigin = new Vector2(50, 50);
    private ShapeRenderer debugLineRend;

    public Vector2 debug_shortest_dist_start;
    public Vector2 debug_shortest_dist_end;

    public int debugcounter = 0;
    public float radius;

    public Collider(Vector2 center, float radius, ShapeRenderer debugLineRend){
        this.center = center;
        this.radius = radius;
        this.debugLineRend = debugLineRend;
    }

    public Collider(Vector2 [] verts, String imagePath, Vector2 center, int width, int height, ShapeRenderer debugLineRend){
        this.debugLineRend = debugLineRend;
        this.debugLineRend.setColor(Color.BLUE);
        File f = new File(imagePath);
        if(f.exists() && !f.isDirectory()) {
            sprite = new Sprite(new Texture(Gdx.files.internal(imagePath)), width, height);
            sprite.setCenter(center.x, center.y);
        }
        else{
            System.err.println("Image File Does not exist: " + imagePath);
        }
        this.verts = verts;
        this.center = center;
        // Takes the verts which are expected to be the offset and updates them to be world
        //  coordinates around the center
        for (int i = 0; i < this.verts.length; i++){
            this.verts[i].add(center);
        }
        this.radius = -1;
    }

    public Sprite GetSprite(){
        return sprite;
    }

    public void DebugDraw(){
        int count = 0;
        int next = 1;
        if (radius >= 0){
            debugLineRend.setColor(Color.PINK);
            debugLineRend.circle(center.x, center.y, radius);
            debugLineRend.setColor(Color.BLUE);
        }
        else {
            while (count < this.verts.length) {
                debugLineRend.line(this.verts[count].x, this.verts[count].y, this.verts[next].x, this.verts[next].y);
                count++;
                if (count == this.verts.length - 1) {
                    next = 0;
                } else {
                    next++;
                }
            }
            //System.out.println("The line being drawn is from :" + debug_shortest_dist_start + " to " + debug_shortest_dist_end);

        }
        //System.out.println("END");
        //this.sprite.draw(sb);
    }

    public void Move(float x, float y){
        // Moves the ship to the position x, y
        // Get the change in position
        if (radius <= 0) {
            Vector2 delta = new Vector2(x - center.x, y - center.y);
            center = new Vector2(x, y);
            // Update all points to the new position
            for (int i = 0; i < this.verts.length; i++) {
                this.verts[i].add(delta);
            }

            this.sprite.setCenter(x, y);
        }
        else{
            this.center.x = x;
            this.center.y = y;
        }
    }

    public void Rotate( float degree ){
        System.out.println("Center:" + center);
        System.out.println("Rotate:" + degree + " degrees");
        float tempX ,tempY;
        for (int i = 0; i < verts.length; ++i){
            System.out.println("Vert [" + i + "]:" + verts[i]);
            // Transform points using rotational matrix
            // Move all points around 0, 0 NOT the Center
            verts[i].sub(center);
            // Rotate them
            tempX = verts[i].x;
            tempY = verts[i].y;
            verts[i].x = (float) ( ( Math.cos(Math.toRadians(degree)) * tempX ) - ( Math.sin(Math.toRadians(degree)) * tempY ));
            verts[i].y = (float) ( ( Math.sin(Math.toRadians(degree)) * tempX ) + ( Math.cos(Math.toRadians(degree)) * tempY ));
            // Move the points back around the Center point
            verts[i].add(center);
            System.out.println("Becomes:" + verts[i]);
        }
    }

    public void OldRotate( float degree ){
        float magnitude;
        System.out.println("OldRotate" + degree + " degrees");
        for (int i = 0; i < verts.length; ++i){
            System.out.println("Vert [" + i + "]:" + verts[i]);
            magnitude = (float) ( Math.sqrt( Math.pow(verts[i].x, 2) + Math.pow(verts[i].y , 2)) );
            verts[i].x = (float) ( Math.cos(degree) * magnitude );
            verts[i].y = (float) ( Math.sin(degree) * magnitude );
            System.out.println("Becomes:" + verts[i]);
        }
    }

    public Vector2[] GetVerts(){
        return this.verts;
    }
    public Vector2 Debug_GetCenter(){return center;}


    public boolean isCollidingWith( Collider obj) {
        // Create the vectors along which we will be projecting both shapes
        // If we find a space between the shapes along ANY of these projected lines we know they
        //  are NOT colliding
        Vector2[] vectorsToProjectOnto = CreateProjectionPlanes(this.verts, obj.GetVerts());

        //DEBUG
        int shapeCount = 0;
        float originX1, originY1, originX2, originY2;
        originX1 = this.center.x;
        originY1 = this.center.y;
        originX2 = obj.Debug_GetCenter().x;
        originY2 = obj.Debug_GetCenter().y;

        for (int i = 0; i < vectorsToProjectOnto.length; i++){

            if (i < vectorsToProjectOnto.length / 2f) {
                debugLineRend.line(originX1, originY1, originX1 + (vectorsToProjectOnto[i].x * 10), originY1 + (vectorsToProjectOnto[i].y * 10));

                shapeCount = (shapeCount + 1) % this.verts.length;
            }
            else{
                debugLineRend.line(originX2, originY2, originX2 + (vectorsToProjectOnto[i].x * 10), originY2 + (vectorsToProjectOnto[i].y * 10));

                shapeCount = (shapeCount + 1) % obj.GetVerts().length;
            }

        }
        //DEBUG

        // Vectors to hold the start and end of the shape projections on each vector in "vectorsToProjectOnto"
        Vector2[] projectionOfShape1_start_end;
        Vector2[] projectionOfShape2_start_end;

        float distBetweenShapes;
        for ( int i = 0; i < vectorsToProjectOnto.length; i++){
//            System.out.println("Projection of shape1 ["+ i +"]:   NUM VECTs to project onto:" + vectorsToProjectOnto.length);
            // Project Shape 1
            projectionOfShape1_start_end = ProjectShapeAlongVector(this.verts, vectorsToProjectOnto[i]);
//            System.out.println("Projection of shape2");
            debugcounter++;
            // Project Shape 2
            projectionOfShape2_start_end = ProjectShapeAlongVector(obj.GetVerts(), vectorsToProjectOnto[i]);
            debugcounter = 0;

            // Determine if the projections are overlapping
           // try {
                // Make sure the line segments have 2 points if not throw and exception and return false no collision
                distBetweenShapes =  GetDistanceBetweenLineSegments( projectionOfShape1_start_end, projectionOfShape2_start_end );

                //System.out.println("Dist["+i+"]:" + distBetweenShapes);
          //  }
          //  catch ( Exception e ){
         //       System.out.println(e.getMessage());
         //       return false;
         //   }

            // If the projections are NOT overlapping then we know these shapes are NOT colliding
            if ( distBetweenShapes > 0 ){
                return false;
            }
        }



        return true;
    }
    private Vector2[] CreateProjectionPlanes(Vector2[] shape1_vectors, Vector2[] shape2_vectors){
        // Vectors should be pointing at the corners of their corresponding shape
        Vector2[] projVectors = new Vector2[shape1_vectors.length + shape2_vectors.length];

        Vector2 tempVector;
        // Append all Vectors into 1 array
        int totalCount = 0;
        int next;
        for (int i = 0; i < shape1_vectors.length; i++){
            next = (i+1) % shape1_vectors.length;
            tempVector = new Vector2(shape1_vectors[next].x, shape1_vectors[next].y);
            projVectors[totalCount] = tempVector.sub(shape1_vectors[i]).rotate90(1).nor();
            if (projVectors[totalCount].x == -0){
                projVectors[totalCount].x = 0;
            }
            if (projVectors[totalCount].y == -0){
                projVectors[totalCount].y = 0;
            }
            totalCount++;
        }
        for (int i = 0; i < shape2_vectors.length; i++){
            next = (i+1) % shape2_vectors.length;
            tempVector = new Vector2(shape2_vectors[next].x, shape2_vectors[next].y);
            projVectors[totalCount] = tempVector.sub(shape2_vectors[i]).rotate90(1).nor();
            if (projVectors[totalCount].x == -0){
                projVectors[totalCount].x = 0;
            }
            if (projVectors[totalCount].y == -0){
                projVectors[totalCount].y = 0;
            }
            totalCount++;
        }

        // Filter out any vectors that have any duplicate slopes
        // projVectors = FilterParrallelVectors(projVectors);


        return projVectors;
    }

    // This will return the start and end points of a shape projected on a line
    private Vector2[] ProjectShapeAlongVector( Vector2[] cornersOfShape, Vector2 projectingOnThisVector){
        // cornersOfShape is a set of verticies that are the corners of the shape which we are projecting
        // projectingOnThisVector is the vector along which we are projecting this shape
        int debug_size;
        if (debugcounter == 0){
            //System.out.println("Projecting THIS shape on to all axies");
            debug_size = 5;
            debugLineRend.setColor(Color.GREEN);
        }
        else {
            //System.out.println("Projecting OTHER shape on to all axies");
            debug_size = 2;
            debugLineRend.setColor(Color.RED);
        }
        Vector2 projectionPoint;
        Vector2 min = new Vector2();
        Vector2 max = new Vector2();

        Vector2 [] debugPoints = new Vector2[cornersOfShape.length];
//        for (int j = 0;  j < cornersOfShape.length; j++){
//            System.out.println("  Corner [" + j + "]:" + cornersOfShape[j] + "\n   Projecting onto:" + projectingOnThisVector);
//        }

        // Loop through all the corners and find the smallest and largest projection vector along the vector "projectingOnThisVector"
        //  This will tell us the start and end points of the projection of the shape on the vector "projectingOnThisVector"
        // **Shape** is defined by the points in the "cornersOfShape" array

        for (int i = 0; i < cornersOfShape.length; i++) {
            // Projection of x on line v = (x dot v / v dot v ) * v   (if v is a unit vector though v dot v == 1 so we don't need to divide v dot v since we normalized "projectingOnThisVector")
            projectionPoint = new Vector2(projectingOnThisVector).scl(cornersOfShape[i].dot(projectingOnThisVector));
            debugPoints[i] = projectionPoint;

            if (i == 0){
                min = projectionPoint;
                max = projectionPoint;
            }
            else{
                if ( min.x >= projectionPoint.x && min.y >= projectionPoint.y ){
                    min = projectionPoint;
                }
                if (  max.x <= projectionPoint.x && max.y <= projectionPoint.y ){
                    max = projectionPoint;
                }
            }
        }
        debugLineRend.circle(min.x, min.y, debug_size);
        // Change color for max based on which shape this is for debug
        if (debugcounter == 0){
            // Black for this shape
            debugLineRend.setColor(Color.BLACK);
        }
        else {
            debugLineRend.setColor(Color.PURPLE);
        }
        debugLineRend.circle(max.x, max.y, debug_size);

        debugLineRend.setColor(Color.BLUE);

        Vector2[] startAndEndOfShape = new Vector2[2];
        startAndEndOfShape[START] = min;
        startAndEndOfShape[END] = max;

        return startAndEndOfShape;
    }

    // Returns the distance between line segments:
    // Line segments MUST BE on the same line
    // return value > 0 if there is no overlap, return value will represent the distance between segments
    // return value <= 0 if there is an overlap, return value will represent how much of an overlap there is
    private float GetDistanceBetweenLineSegments(Vector2[] segment1, Vector2[] segment2){//} throws Exception {
        //if (segment1.length != 2 || segment2.length != 2) {
           /* throw new Exception( "There was an invalid number of points in one or both of " +
                    "these line segments. There should only be 2 points!" );*/
        //}
        //else {
        // Make sure these segments are on the same line
            /*if ( !verifyLineSegmentsAreOnSameLine(segment1, segment2) ){
                throw new Exception( "Segments are not on the same line!" );
            }
            else {*/
        //System.out.println("Segment1 , Segment2: (" + segment1[START] + "," + segment1[END] + ") , (" + segment2[START] + ", " + segment2[END] + ")");
        Vector2 seg1 = new Vector2(segment1[END]);
        seg1.sub(segment1[START]);
        Vector2 seg2 = new Vector2(segment2[END]);
        seg2.sub(segment2[START]);

        // System.out.println("AFTER MATH:\n Segment1 , Segment2: (" + segment1[START] + "," + segment1[END] + ") , (" + segment2[START] + ", " + segment2[END] + ")");
        // System.out.println("(Segment1[END] - Segment1[START]) , (Segment2[END] - Segment2[START]): " + seg1 + ", " + seg2);
        // Check if vectors are parallel (cross product will be 0 if they are)
//        if (seg1.crs(seg2) == 0){
            // Check if they are pointing in the same direction
            //  This is important so we can know if they are overlapping (aka a collision)
            //  We need both of our projection vectors to be pointing in the same direction
            //  So rotate one if we need to do so then get the length of the difference and
            //  make it negative if the vector that is the difference of the two projection
            //  is pointing in the opposite direction vectors (aka the objects are colliding)
        if ( seg1.hasOppositeDirection(seg2) ){
            Vector2 temp = segment1[START];
            segment1[START] = segment1[END];
            segment1[END] = temp;
        }
        Vector2 diff;
        if (segment2[START].len2() > segment1[END].len2()) {
            diff = new Vector2(segment2[START]).sub(segment1[END]);
        }
        else{
            diff = new Vector2(segment1[START]).sub(segment2[END]);
        }

        if (diff.hasSameDirection(seg2)) {
            return diff.len2();
        }
        else{
            return -diff.len2();
        }
    }

    private Vector2[] FilterParrallelVectors(Vector2 vectors[]){
        // Takes in an array of vectors and removes vectors with duplicate slopes
        //  For examples vectors was an array of vectors from a rectangle only 2 vectors
        //  would be returned from the 4 passed into this method
        System.out.println("Vectors before filter");
        for (Vector2 v : vectors) {
            System.out.println(v);
        }
        Vector<Vector2> returnVectors = new Vector<Vector2>();
        // If the user passed in an empty array or an array with just 1 element
        //   just return that array. No filtering needs to take place.

        if (vectors.length > 1) {
            // Grab the first vector since it can't possibly be a duplicate
            returnVectors.add(vectors[0]);

            boolean addSlope;
            for (Vector2 v : vectors) {
                addSlope = true;
                for (Vector2 vectorWeAreKeeping : returnVectors) {
                    if ((vectorWeAreKeeping.y - vectorWeAreKeeping.x) == (v.y - v.x)) {
                        addSlope = false;
                    }
                }
                if (addSlope) {
                    returnVectors.add(v);
                }
            }

            Vector2 retArray[] = new Vector2[returnVectors.size()];

            // Put all the vectors we found in an array and return it
            for (int i =0; i <  retArray.length; i++){
                retArray[i] = returnVectors.get(i);
            }

            System.out.println("Vectors NOT FILTERED: ");

            for (int i =0; i <  retArray.length; i++){
                System.out.println(retArray[i]);
            }
            return retArray;
        }
        else{
            return vectors;
        }
    }

    public boolean isCollidingWithCircle(Vector2 centerOfCircle, float radius){
        Vector2 closestPoint = FindClosestPointTo(centerOfCircle);
        debug_shortest_dist_start = centerOfCircle;
        debug_shortest_dist_end = closestPoint;

        if (closestPoint.cpy().sub(centerOfCircle).len2() <= radius*radius){
            return true;
        }
        else {
            return false;
        }
    }

    private Vector2 FindClosestPointTo(Vector2 centerOfCircle){
        int next;
        Vector2 tempCenter;
        Vector2 start, end;
        Vector2 collisionPoint; // Origin to collision point
        Vector2 min = null;
        Vector2 curMin;
        for (int cur = 0; cur < this.verts.length; cur++) {

            tempCenter = centerOfCircle.cpy();
            System.out.println(cur);
            if (cur == this.verts.length - 1) {
                next = 0;
            } else {
                next = cur + 1;
            }
            start = this.verts[cur].cpy();
            end = this.verts[next].cpy();

            // Move start to origin and translate all the other points around it accordingly
            end.sub(start);
            tempCenter.sub(start);

            collisionPoint = end.cpy().scl((tempCenter.cpy().dot(end)) / (end.cpy().dot(end)));


            if (collisionPoint.hasSameDirection(end) && collisionPoint.len2() < end.len2()) {
                // This is the point on the line segment closest to the center of the circle
                // Move back to the original line since we moved it to origin earlier
                curMin = collisionPoint.cpy().add(this.verts[cur]);
            }
            else if (collisionPoint.hasOppositeDirection(end)){
                // This is the start of our line segment since the closest point is not on the line
                //  segment, but before it.
                curMin = this.verts[cur].cpy();
            }
            else {
                // This is the end of our line segment since the closest point is actually not on
                //  the line segment, but after it.
                curMin = this.verts[next].cpy();
            }

            // Check if our new min is smaller then smallest we have seen
            // If this is the first one we have seen no need to compare
            if (cur == 0){
                min = curMin.cpy();
            }
            else {
                // We need to compare the distance from the closest point on the shape (curMin) to the center of the circle to
                //  the old minimum (the distance from last closest point on the shape we saw to the center of the circle)
               if (curMin.cpy().sub(centerOfCircle).len2() < min.cpy().sub(centerOfCircle).len2()) {
                    min = curMin.cpy();
                }
            }
        }
        return min;
    }
}
