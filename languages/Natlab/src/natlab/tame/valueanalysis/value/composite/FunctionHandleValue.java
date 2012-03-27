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

package natlab.tame.valueanalysis.value.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import natlab.tame.classes.reference.FunctionHandleClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.constant.Constant;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.MatrixValue;
import natlab.tame.valueanalysis.value.Res;
import natlab.tame.valueanalysis.value.Shape;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.path.FunctionReference;

/**
 * This represents value which refers to a function handle.
 * Stores along with it the possible set of function hanldes that this can refer to.
 * A null element refers to a reference to a function which is unknown.
 * 
 * TODO - is it bad to have the null element?
 * TODO - this should enclose a workspace
 * @author adubra
 */

public class FunctionHandleValue<D extends MatrixValue<D>> implements Value<D> {
    HashSet<FunctionHandle<D>> functions = new HashSet<FunctionHandle<D>>();
    ValueFactory<D> factory;
    
    public FunctionHandleValue(ValueFactory<D> factory){
        this.factory = factory;
    }
    
    public FunctionHandleValue(ValueFactory<D> factory,FunctionReference f){
        this(factory);
        functions.add(FunctionHandle.<D>newInstance(f));
    }
    
    public FunctionHandleValue(FunctionHandleValue<D> other){
        this.factory = other.factory;
        this.functions.addAll(other.functions);
    }
    
    public FunctionHandleValue(ValueFactory<D> factory,FunctionReference f,
            List<ValueSet<D>> partialValues) {
        this(factory);
        functions.add(FunctionHandle.newInstance(f,partialValues));        
    }
    @Override
    public FunctionHandleClassReference getMatlabClass() {
        return FunctionHandleClassReference.getInstance();
    }

    @Override
    public Value<D> merge(Value<D> v) {
        if (v instanceof FunctionHandleValue<?>){
            FunctionHandleValue<D> result = new FunctionHandleValue<D>(this);
            result.functions.addAll(((FunctionHandleValue<D>)v).functions);
            return result;
        } else {
            return null;
        }
    }
    
    /**
     * returns the set of possible functions this function handle represents
     * @return
     */
    public Set<FunctionHandle<D>> getFunctionHandles(){
        return this.functions;
    }
    
    @Override
    public String toString() {
        String fs = functions.toString();
        return "(handle,"+fs.substring(1,fs.length()-1)+")";
    }
    
    @Override
    public ValueSet<D> arraySubsref(Args<D> indizes) {
        throw new UnsupportedOperationException("function handles do not support indexing");
        //TODO - or should they support it - this is the same as calling a function
    }
    
    @Override
    public ValueSet<D> dotSubsref(String field) {
        throw new UnsupportedOperationException("attempting to dot access function handle");
    }
    
    @Override
    public Value<D> arraySubsasgn(Args<D> indizes,Value<D> value) {
        throw new UnsupportedOperationException("function handles do not support indexing");
    }
    
    @Override
    public Value<D> dotSubsasgn(String field, Value<D> value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Res<D> cellSubsref(Args<D> indizes) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Value<D> cellSubsasgn(Args<D> indizes, Args<D> values) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Shape<D> getShape() {
        return factory.newScalarShape();
    }
    @Override
    public boolean hasShape() {
        return true;
    }
    
    
    public boolean isConstant(){
        return getConstant() != null;
    }
    
    public Constant getConstant(){
        return null; //TODO
    }

    @Override
    public Value<D> toFunctionArgument(boolean recursive) {
        return this; //todo if there's a workspace, simplify that ... or?
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionHandleValue<?>){
            return this.functions.equals(((FunctionHandleValue<?>)obj).functions);
        } else return false;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * this class represents a single funciton handle 
     * - it contains a function reference and partial values
     */
    public static class FunctionHandle<D extends MatrixValue<D>>{
        FunctionReference function;
        List<ValueSet<D>> partialValues;
        public static <D extends MatrixValue<D>> FunctionHandle<D> newInstance(FunctionReference function){
            FunctionHandle<D> result = new FunctionHandle<D>();
            result.function = function;
            result.partialValues = Collections.emptyList();
            return result;
        }
        public static <D extends MatrixValue<D>> FunctionHandle<D> newInstance(FunctionReference function,
                List<ValueSet<D>> partialValues){
            FunctionHandle<D> result = new FunctionHandle<D>();
            result.function = function;
            result.partialValues = new ArrayList<ValueSet<D>>(partialValues);
            return result;
        }
        public FunctionReference getFunction(){
            return function;
        }
        public List<ValueSet<D>> getPartialValues(){
            return partialValues;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FunctionHandle<?>) {
                FunctionHandle<?> aHandle = (FunctionHandle<?>) obj;
                return (this.function.equals(aHandle.function)) &&
                       (this.partialValues.equals(aHandle.partialValues));
            } else{
                return false;
            }
        }
        @Override
        public int hashCode() {
            return 0;//function.hashCode() + 1337*partialValues.hashCode();
        }
        @Override
        public String toString() {
            if (partialValues.size() == 0){
                return "@"+function.getname();
            } else {
                String s = partialValues.toString();
                return "@"+function.getname()+"("+s.substring(1,s.length()-1)+",..)";
            }
        }
    }
}