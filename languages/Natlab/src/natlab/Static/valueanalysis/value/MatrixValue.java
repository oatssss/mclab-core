// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.tame.valueanalysis.value;

import java.util.List;

import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.ValueSet;

/**
 * represents a primitive value, i.e. a value that has a primitive type.
 * At this level this class stores just a matlab class.
 * Objects of this type are immutable.
 * Use SimpleMatrixValue for an implementation that is non-abstract, and 
 * which adds a constant.
 * 
 * Any actual implementation of this class has to be generic in itself.
 * Implementations have to provide a MatrixValueFactory. The convention
 * is that every implementation of MatrixValue provides a factory as a public
 * static final variable FACTORY (see SimpleMatrixValue).
 * Note that any Value has to implement an equals method.
 * @author adubra
 */
public abstract class MatrixValue<D extends MatrixValue<D>> implements Value<D> {
    protected PrimitiveClassReference classRef;
    
    @Override
    public PrimitiveClassReference getMatlabClass() {
        return classRef;
    }
    
    /**
     * creates a non constant matrix value with the given matlab class
     * @param class reference
     */
    public MatrixValue(PrimitiveClassReference classRef){
        this.classRef = classRef;
    }

    
    @Override
    abstract public D merge(Value<D> other);
    
    
    public boolean isConstant(){
        return getConstant() != null;
    }
        
    
    @Override
    abstract public ValueSet<D> arraySubsref(Args<D> indizes);  
    
    
    /**
     * returns a Matrix Value representing the index i
     * in the loop header
     * for i = <this>:<inc>:<upper>
     * inc is optional, and may be null, in which case inc=1
     */
    abstract public D forRange(D upper, D inc);
    
}





