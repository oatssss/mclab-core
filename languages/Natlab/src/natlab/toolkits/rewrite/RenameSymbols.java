package natlab.toolkits.rewrite;

import java.util.*;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;


/**
 * This is a rewrite that will go through the AST and rename all Names,
 * according to a given Map. Any name expression in the tree whose name is a key
 * in the map will get transformed to name expression with the corresponding value
 * as a name.
 * 
 * @author ant6n
 */

public class RenameSymbols extends AbstractLocalRewrite {
    // *** Static helper funcitons *****************************************************
    /**
     * given a name, returns the same name with some numbers added at the end
     * such that the returned name does not exist within the given sets of names
     * (This may be slow - tries to find the numerical postfix with smallest positive value)
     * The names should be supplied as a Collection<String>
     */
    @SuppressWarnings("unchecked")
    public static String getNewName(String name, Collection ... names){
        if (names.length == 0) return name;

        int i = 1; //the postfix
        boolean containsName = false; //flag whether the name is in the sets
        
        //try to an i such that name+i is not in the set
        do{
            for (Collection<String> set : names){
                if (set.contains(name+i)){
                    containsName = true;
                    break;
                }
            }
            if (containsName) i++;
        } while(containsName);
        return name+i;
    }

    
    
    // *** Actual Simplification methods ***********************************************
	Map<String,String> map;
	
	/**
	 * Constructor taking the tree to be transformed, and the map
	 * 
	 * @param tree
	 * @param renameMap
	 */
	public RenameSymbols(ASTNode tree,Map<String, String> renameMap) {
		super(tree);
		map = renameMap;
	}
	

	//note that no rewriting of children is needed
	public void caseName(Name node) {
		if (map.containsKey(node.getID())){
			this.newNode = new TransformedNode(new Name(map.get(node.getID())));
		}
	}
	
}
