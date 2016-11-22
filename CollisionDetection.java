package KeithTools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

/**
 * Created by Keith on 8/27/2016.
 */
public class CollisionDetection {

    private static final int START = 0;
    private static final int END = 1;

    private Vector2 newOrigin = new Vector2(50, 50);

    private ShapeRenderer debugLineRend;
    public boolean isColliding(Vector2 shape1_vectors[] , Vector2 shape2_vectors[], ShapeRenderer debug) {
        debugLineRend = debug;
        // Create the vectors along which we will be projecting both shapes
        // If we find a space between the shapes along ANY of these projected lines we know they
        //  are NOT colliding
        Vector2[] vectorsToProjectOnto = CreateProjectionPlanes(shape1_vectors, shape2_vectors);

        //DEBUG
        int shapeCount = 0;
        float originX1, originY1, originX2, originY2;
        originX1 = shape1_vectors[0].x + 5;
        originY1 = shape1_vectors[0].y + 5;
        originX2 = shape2_vectors[0].x + 5;
        originY2 = shape2_vectors[0].y + 5;

        for (int i = 0; i < vectorsToProjectOnto.length; i++){

            if (i < vectorsToProjectOnto.length / 2f) {
                debugLineRend.line(originX1, originY1, originX1 + (vectorsToProjectOnto[i].x * 10), originY1 + (vectorsToProjectOnto[i].y * 10));

                shapeCount = (shapeCount + 1) % shape1_vectors.length;
            }
            else{
                debugLineRend.line(originX2, originY2, originX2 + (vectorsToProjectOnto[i].x * 10), originY2 + (vectorsToProjectOnto[i].y * 10));

                shapeCount = (shapeCount + 1) % shape1_vectors.length;
            }

        }
        //DEBUG

        // Vectors to hold the start and end of the shape projections on each vector in "vectorsToProjectOnto"
        Vector2[] projectionOfShape1;
        Vector2[] projectionOfShape2;

        float distBetweenShapes;

        for ( int i = 0; i < vectorsToProjectOnto.length; i++){
//            System.out.println("Projection of shape1 ["+ i +"]:   NUM VECTs to project onto:" + vectorsToProjectOnto.length);
            // Project Shape 1
            projectionOfShape1 = ProjectShapeAlongVector(shape1_vectors, vectorsToProjectOnto[i]);
//            System.out.println("Projection of shape2");
            // Project Shape 2
            projectionOfShape2 = ProjectShapeAlongVector(shape2_vectors, vectorsToProjectOnto[i]);


            // Determine if the projections are overlapping
            try {
                // Make sure the line segments have 2 points if not throw and exception and return false no collision
                distBetweenShapes =  GetDistanceBetweenLineSegments( projectionOfShape1, projectionOfShape2 );
            }
            catch ( Exception e ){
                System.out.println(e.getMessage());
                return false;
            }

            // If the projections are NOT overlapping then we know these shapes are NOT colliding
            if ( distBetweenShapes > 0 ){
                return false;
            }
      }



        return true;
    }

    // This will return the start and end points of a shape projected on a line
    private Vector2[] ProjectShapeAlongVector( Vector2[] cornersOfShape, Vector2 projectingOnThisVector){
        // cornersOfShape is a set of verticies that are the corners of the shape which we are projecting
        // projectingOnThisVector is the vector along which we are projecting this shape

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
        projectingOnThisVector.nor();
        for (int i = 0; i < cornersOfShape.length; i++) {

            // Since we normalized projectionVector we do not need to divide by  "projectingOnThisVector.dot(projectingOnThisVector))" as this just == 1
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

            //System.out.println("["+ i + "]:" + projectionPoint.x + " " + projectionPoint.y);
            //System.out.println("MIN:" + min.x + " " + min.y);
            //System.out.println("MAX:" + max.x + " " + max.y);
        }
        for (int i = 0; i < debugPoints.length ; i++) {
            if ( debugPoints[i].x == min.x &&  debugPoints[i].y == min.y  ){
                debugLineRend.setColor(Color.PURPLE);
            }
            else if ( debugPoints[i].x == max.x &&  debugPoints[i].y == max.y  ){
                debugLineRend.setColor(Color.RED);
            }
            else{
                debugLineRend.setColor(Color.GREEN);
            }

            debugLineRend.circle(debugPoints[i].x, debugPoints[i].y, 2);
        }
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
    private float GetDistanceBetweenLineSegments(Vector2[] segment1, Vector2[] segment2) throws Exception {
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
               // System.out.println("Segment1 , Segment2: (" + segment1[START] + "," + segment1[END] + ") , (" + segment2[START] + ", " + segment2[END] + ")");
                Vector2 seg1 = new Vector2(segment1[END]).sub(segment1[START]);
                Vector2 seg2 = new Vector2(segment2[END]).sub(segment2[START]);
               // System.out.println("AFTER MATH:\n Segment1 , Segment2: (" + segment1[START] + "," + segment1[END] + ") , (" + segment2[START] + ", " + segment2[END] + ")");
               // System.out.println("(Segment1[END] - Segment1[START]) , (Segment2[END] - Segment2[START]): " + seg1 + ", " + seg2);
                // Check if vectors are parallel (cross product will be 0 if they are)
                if (seg1.crs(seg2) == 0){
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
                    Vector2 intersect;
                    if (segment2[START].len2() > segment1[START].len2()) {
                        intersect = new Vector2(segment2[START]).sub(segment1[END]);//seg1.sub(seg2);
                    }
                    else{
                        intersect = new Vector2(segment1[START]).sub(segment2[END]);
                    }
                    float distSqr = intersect.len2();

                    if (intersect.hasOppositeDirection(seg1)){
                        // Intersection
                        return distSqr * -1;
                    }
                    else{
                        // No intersection
                        return distSqr;
                    }
                }
                else{
                    throw new Exception("");//("Vectors are not parallel");
                }

            //}
        //}
    }

    private boolean verifyLineSegmentsAreOnSameLine(Vector2[] segment1, Vector2[] segment2){
        float changeInXForLine1 = segment1[END].x - segment1[START].x;
        float changeInXForLine2 = segment2[END].x - segment2[START].x;
        if ( changeInXForLine1 == 0 || changeInXForLine2 == 0 ){
            if (  changeInXForLine1 == 0 && changeInXForLine2 == 0 ){
                // We know these segments are vertical because of their slope
                //  So if their x coordinates are the same then they must be on the same line
                //  else they are not (We could use START or END it does not matter since they are
                //  vertical)
                if ( segment1[START].x == segment2[START].x ){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                // If only one of these has an undefined slope and not the other then their slopes
                //  are different; therefore, they cannot be on the same line
                return false;
            }
        }
        else {
            float slopeLine1 = segment1[END].y - segment1[START].y / changeInXForLine1;
            float slopeLine2 = segment2[END].y - segment2[START].y / changeInXForLine2;
            if ( slopeLine1 == slopeLine2 ){
                // Check if y intecepts are equal. If so they are on the same line else they are not
                if ( (segment1[START].y - (slopeLine1 * segment1[START].x)) == (segment2[START].y - (slopeLine2 * segment2[START].x)) ) {
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                // Slopes are not equal so they are not on the same line
                return false;
            }
        }
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

        // Replace all vectors with an orthogonal counter part
        /*for (int i = 0; i < projVectors.length; i++) {

            if(i == projVectors.length - 1){
                projVectors[i] = projVectors[i].sub(projVectors[0]);
            }
            else{
                projVectors[i] = projVectors[i].sub(projVectors[i+1]);
            }
            projVectors[i].rotate(90);

        }*/

        return projVectors;
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
}
