package natlab.Static.mc4;

import java.io.File;
import java.util.*;

import natlab.Static.ir.transform.ThreeAddressToIR;
import natlab.Static.mc4.symbolTable.*;
import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.inline.*;
import natlab.toolkits.rewrite.simplification.FullSimplification;
import natlab.toolkits.rewrite.threeaddress.*;

import ast.*;

/**
 * 
 * This object represents a function with mc4.
 * It stores the AST of a function, as well as a symbol table.
 * 
 */
public class Mc4Function {
    Function function;
    SymbolTable symbolTable;
    File source;
    String name;
    HashSet<String> siblings;
    
    /**
     * Creates a Mc4 Function, given an ast and the source file.
     * This transforms the function to 3 address code and builds the symbol table.
     * 
     * @param function the AST of the function
     * @param source the file where it is from
     * @param a function finder, to resolve IDs into Function references
     */
    public Mc4Function(Function function, File source) {
        this.function = function.fullCopy();
        this.source = source;
        this.symbolTable = new SymbolTable();
        this.name = function.getName();
        
        
        //set siblings
        siblings = new HashSet<String>(function.getSiblings().keySet());
        
        //transform to 3 address code
        transformTo3Addr();
        
        //create first symbol table
        createSymbolTable();
    }
    
    //creates and puts first order approximation of the symbol table - variable or function
    private void createSymbolTable(){
        //perform variable or function analysis on function and get result
        VFPreorderAnalysis functionAnalysis = new VFPreorderAnalysis(this.function,
                Mc4.functionFinder.getFunctionOrScriptQuery());
        functionAnalysis.analyze();        
        VFFlowset flowset; 
        flowset = functionAnalysis.getFlowSets().get(function);
        Mc4.debug("create symbol table - function "+name+" variable or function analysis result:\n"+flowset);
        
        //go through all symbols, and put them in the symbol table
        //TODO the var analysis only captures variables - get those first...
        for (ValueDatumPair<String, ? extends VFDatum> pair : flowset.toList()){
            VFDatum vf = pair.getDatum();
            String name = pair.getValue();
                        
            if (vf.isFunction()){
                symbolTable.put(name, new UnknownFunction());
            } else if (vf.isVariable()){
                symbolTable.put(name, new Variable());
            } else {
                System.err.println("symbol table: "+name+" in "+this.name+" cannot be resolved as a function or variable. vf:"+vf);
            }
        }        
        //...the rest are assumed to be functions for now
        for (String name : function.getSymbols()){
            if (!symbolTable.containsKey(name)){
                symbolTable.put(name, new UnknownFunction());
            }
        }
        
        Mc4.debug(this.toString());
        Mc4.debug("first symbol table for "+name+":"+symbolTable);
        //TODO deal with errors
    }
    
    
    //transforms the underlying AST to 3 address code
    private void transformTo3Addr(){
        System.out.println("\n\nbefore 3 addr trasformation:");
        System.out.println(this.function.getPrettyPrinted());
        
    	//so far 3 addr only works on Program nodes
    	FunctionList fList = new FunctionList();
    	fList.addFunction(function);
    	
    	//do transform - first left, then right
    	//System.out.println("lr:");
    	//LeftThreeAddressRewrite lr = new LeftThreeAddressRewrite(fList);
    	//System.out.println("rr:");
    	//RightThreeAddressRewrite rr = new RightThreeAddressRewrite(lr.transform());
    	
    	Simplifier simplifier =
    	    new Simplifier(fList, ThreeAddressToIR.class);
    	fList = ((FunctionList)simplifier.simplify());
    	
    	//transform into IR 
    	//TODO - is this the right place?
        //System.out.println(rr.transform().getPrettyPrinted());
    	//System.out.println("after 3 addr trasformation:");
        //System.out.println(this.function.getPrettyPrinted());
        //ThreeAddressToIR irTransform = new ThreeAddressToIR(fList);
    	//fList = (FunctionList)irTransform.transform();

        System.out.println("after IR  trasformation:");
        System.out.println(this.function.getPrettyPrinted());
    	
    	this.function = fList.getFunction(0);
    	
    }
    
    
    //getter methods
    public SymbolTable getSymbolTable(){ return symbolTable; }
    public String getName(){ return name; }
    public Function getAst(){ return function; }
    public File getFile(){ return source; }
    public Set<String> getSiblings(){ return siblings; }
    
    
    
    /**
     * inlines a copy of all functions that are in the given map,
     * and which are called from this function
     */
    public void inline(Map<FunctionReference, Mc4Function> map){
    	HashMap<String, Function> inlinerMap = new HashMap<String,Function>();
    	
    	//get all function references in this' symbol table
    	for (String name : symbolTable.getSymbols(SymbolTable.NON_BUILTIN_FUNCTIONS)){
    		FunctionReference ref = ((FunctionReferenceType)symbolTable.get(name)).getFunctionReference();
    		if (map.containsKey(ref)){
    			//ref is in the map and is called from this - we need to inline it
    			Mc4Function otherFunction = map.get(ref);
    			
    			//merge symbol tables
    			Map<String, String> renameMap = symbolTable.merge(
    					otherFunction.getSymbolTable(), otherFunction.name.substring(0,4)+"_");
    	    	
    			Mc4.debug("rename map to inline "+ref+" in "+this.name+"\n"+renameMap);
    			
    			//rename symbols in other function
    	    	RenameSymbols rename = new RenameSymbols(otherFunction.getAst().copy(), renameMap);
    	    	Function otherAst = (Function)rename.transform();
    			    			
    			//put it in the inline list
    	    	inlinerMap.put(name, otherAst);
    		}
    	}
    	
    	//actually inline
    	Inliner<Function,Function> inliner = new Inliner<Function,Function>(this.function,inlinerMap);
    	this.function = (Function)inliner.transform();
    }
    
        
    @Override
    public String toString() {
        return 
            "Function: "+name+
            "\nfile: "+source+
            "\nSymbol table:\n"+symbolTable+
            "\ncode:\n"+function.getPrettyPrinted();
    }
}


