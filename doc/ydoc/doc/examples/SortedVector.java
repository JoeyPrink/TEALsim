package examples;

import java.util.Collection;
import java.util.Comparator;
import java.util.Vector;

/**
 * yDoc example class representing a sorted container structure
 * using {@link java.util.Vector} as a base class.<br>
 * (The source code contains only method stubs, i.e. no implementation.)
 *
 * @author Thomas Behr
 */
public class SortedVector extends Vector implements java.io.Serializable
{

  /**
   * Demonstrating yDoc's filtering capabilites
   *
   * @y.exclude
   */
  public Comparator comparator;
  
  /**
   * Constructor
   *
   * @param comparator   the <i>Comparator</i> to use with binary
   *                     search algorithm
   */
  public SortedVector(Comparator comparator) 
  {
  }
  
  /**
   * Constructor<br>
   * Constructs a vector containing the elements of the specified
   * collection. The quick sort algorithm is used to ensure
   * ascending order according to the <i>Comparator</i>.
   *
   * @y.complexity Average case:
   *               <code>O(n log n)</code>, where <code>n</code>
   *               is this container's size (QuickSort complexity)
   * @param collection   the Collection whose elements are to be
   *                     placed into this SortedVector
   * @param comparator   the <i>Comparator</i> to use with binary
   *                     search algorithm
   */
  public SortedVector(Collection collection,
                      Comparator comparator) 
  {
  }
  
  /**
   * Insert objects into the vector, sorted in ascending
   * order according to the <i>Comparator</i>.
   * The binary search algorithm is used to determine
   * the position, where the object should be inserted.
   *
   * @y.postcondition The underlying container is still sorted
   *                  in ascending order according to the <i>Comparator</i>
   *                  after the specified Object has been inserted.
   * @y.complexity Average/worst case:
   *               <code>O(log n)</code>, where <code>n</code>
   *               is this container's size (BinarySearch complexity)
   * @param o   the Object to insert
   *
   * @return
   *   TRUE, iff the object was successfully inserted.
   *   FALSE, if the object could not be inserted, e.g.
   *   if the vector was already holding an <i>equal</i>
   *   object according to the <i>Comparator</i>
   */
  public boolean insertSorted(Object o)
  {
    return false;
  }
  
  /**
   * Standard implementation of binary search algorithm.
   *
   * @y.precondition The underlying container has to be sorted
   *                 in ascending order according to the <i>Comparator</i>
   *                 for binary search to work correctly.
   * @y.complexity Average/worst case:
   *               <code>O(log n)</code>, where <code>n</code>
   *               is this container's size
   * @param o   the Object to look for
   */
  public int binarySearch(Object o)
  {
    return -1;
  }

  /**
   * Demonstrating yDoc's filtering capabilites
   *
   * @y.exclude
   * @param comparator   the Comparator to use
   */
  public void setComparator(Comparator comparator) 
  {
  }

}
