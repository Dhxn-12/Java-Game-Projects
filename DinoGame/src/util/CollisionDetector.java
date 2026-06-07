package util;

import java.awt.Rectangle;

/**
 * AABB (Axis-Aligned Bounding Box) collision detection.
 * DSA: O(1) per pair check; O(n) total per frame for n obstacles.
 * Used by GameEngine each frame.
 */
public class CollisionDetector {

    private CollisionDetector() {}

    /**
     * Returns true if rectangles a and b overlap.
     * Standard AABB test: check non-overlapping on each axis.
     * Time Complexity: O(1)
     */
    public static boolean overlaps(Rectangle a, Rectangle b) {
        return a.x < b.x + b.width
            && a.x + a.width  > b.x
            && a.y < b.y + b.height
            && a.y + a.height > b.y;
    }
}
