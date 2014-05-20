package teal.physics.em;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import teal.physics.GField;
import teal.physics.GeneratesG;
import teal.physics.physical.PhysicalObject;
import teal.sim.TSimElement;
import teal.sim.engine.AbstractEngine;
import teal.sim.engine.AbstractEngineImpl;

/**
 * Implementation for {@code TEngine#EM_ENGINE} type.
 * 
 * @author Stefan
 *
 */
public class EMEngineImpl extends AbstractEngineImpl {

  /**
   * 
   */
  private static final long serialVersionUID = 3873880276999899698L;
  /** 
   * List of all objects of type <code>PhysicalObject</code> that were added to the engine.
   * 
   * @see #addSimElement(TSimElement)
   * @see #addSimElements(Collection)
   * @see #removeSimElement(TSimElement)
   * @see #removeSimElements(Collection)
   */
  protected List<PhysicalObject> physObjs;
  /**
   * Composite gravitational field, which groups all gravitational field generating
   * objects within a wrapper class that simplifies field querying. Queries are
   * either total, or exclude a single object, which is useful when computing the
   * value of the field experienced by that object, due to all others.
   * 
   * @see teal.physics.GField
   */
  protected GField gField;
  /**
   * Composite magnetic field, which groups all magnetic field generating
   * objects within a wrapper class that simplifies field querying. Queries are
   * either total, or exclude a single object, which is useful when computing the
   * value of the field experienced by that object, due to all others.
   * 
   * @see teal.physics.em.BField
   */
  protected BField bField;
  /**
   * Composite electric field, which groups all electric field generating
   * objects within a wrapper class that simplifies field querying. Queries are
   * either total, or exclude a single object, which is useful when computing the
   * value of the field experienced by that object, due to all others.
   * 
   * @see teal.physics.em.EField
   */
  protected EField eField;
  /**
   * Composite Pauli field, which groups all Pauli field generating
   * objects within a wrapper class that simplifies field querying. Queries are
   * either total, or exclude a single object, which is useful when computing the
   * value of the field experienced by that object, due to all others.
   * 
   * @see teal.physics.em.PField
   */
  protected PField pField;

  public EMEngineImpl() {
    super();
    physObjs = new ArrayList<PhysicalObject>();
    gField = new GField();
    bField = new BField();
    eField = new EField();
    pField = new PField();

    collectionsByType.put(PhysicalObject.class, (Collection<PhysicalObject>) physObjs);
    elementsByType.put(GField.class, gField);
    elementsByType.put(BField.class, bField);
    elementsByType.put(EField.class, eField);
    elementsByType.put(PField.class, pField);

    typedElements.put(AbstractEngine.EngineElementType.PFIELD, pField);
    typedElements.put(AbstractEngine.EngineElementType.BFIELD, bField);
    typedElements.put(AbstractEngine.EngineElementType.EFIELD, eField);
    typedElements.put(AbstractEngine.EngineElementType.GFIELD, gField);
    typedElements.put(AbstractEngine.EngineElementType.PHYSICAL_OBJECTS, physObjs);
  }

  @Override
  public void addSimElement(TSimElement obj) {
    if (obj instanceof PhysicalObject) {
      physObjs.add((PhysicalObject) obj);
    }

    if (obj instanceof GeneratesG) {
      gField.add(obj);
    }

    if (obj instanceof GeneratesB) {
      bField.add(obj);
    }

    if (obj instanceof GeneratesE) {
      eField.add(obj);
    }

    if (obj instanceof GeneratesP) {
      pField.add(obj);
    }

  }

  @Override
  public void removeSimElement(TSimElement obj) {
    if (obj instanceof GeneratesG) {
      gField.remove(obj);
    }

    if (obj instanceof GeneratesB) {
      bField.remove(obj);
    }

    if (obj instanceof GeneratesE) {
      eField.remove(obj);
    }

    if (obj instanceof GeneratesP) {
      pField.remove(obj);
    }

    if (obj instanceof PhysicalObject) {
      physObjs.remove(obj);
    }

  }
}
