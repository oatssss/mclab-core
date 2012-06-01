package natlab.tame.valueanalysis.components.isComplex;

/**
 * indicates whether a value has complex information associated with it.
 */

import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.*;
public interface HasisComplexInfo<V extends Value<V>> extends Value<V> {
	
	/**
	 * returns the shape associated with this value
	 */
	public isComplexInfo<V> getisComplexInfo();	
	
}