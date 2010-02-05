package natlab.toolkits.analysis;

import ast.*;
import java.util.*;

/**
 * A simple abstract implementation of a
 * AbstractStructuralForwardAnalysis. This class provides some simple
 * implementations of methods such as processBreaks and caseBreakStmt.
 *
 * @see AbstractStructuralForwardAnalysis
 */
public abstract class AbstractSimpleStructuralForwardAnalysis<A extends FlowSet> extends AbstractStructuralForwardAnalysis<A>
{

    public AbstractSimpleStructuralForwardAnalysis(ASTNode tree){
        super( tree );
    }
    /**
     * A simple implementation of the caseBreakStmt. It copies the
     * in set to the out sets and adds it to the break list.
     */
    public void caseBreakStmt( BreakStmt node )
    {
        currentOutSet = newInitialFlow();
        copy( currentInSet, currentOutSet );
        loopStack.peek().addBreakSet( currentOutSet );
    }

    /**
     * A simple implementation of the caseContinueStmt. It copies the
     * in set to the out set and adds it to the continue list.
     */
    public void caseContinueStmt( ContinueStmt node )
    {
        currentOutSet = newInitialFlow();
        copy( currentInSet, currentOutSet );
        loopStack.peek().addBreakSet( currentOutSet );
    }

    /**
     * A simple implementation of caseCondition. It simply treats it
     * as an expression.
     */
    public void caseCondition( Expr condExpr )
    {
        caseExpr( condExpr );
    }
    public void caseLoopVarAsCondition( AssignStmt node )
    {
        caseAssignStmt( node );
    }
    public void caseLoopVarAsInit( AssignStmt node )
    {
        caseAssignStmt( node );
    }
    public void caseLoopVarAsUpdate( AssignStmt node )
    {
        caseAssignStmt( node );
    }


    /**
     * A simple implementation of processBreaks. It merges all break
     * sets into a single set and returns it.
     */
    public A processBreaks()
    {
        A mergedSets = null;

        for( A set : loopStack.peek().getBreakOutSets() ){
            if( mergedSets == null ){
                mergedSets = newInitialFlow();
                copy(set, mergedSets);
            }
            else
                merge( set, mergedSets, mergedSets );
        }

        return mergedSets;
    }

    /**
     * A simple implementation of processContinues. It merges all
     * break sets into a single set and returns it.
     */
    public A processContinues()
    {
        A mergedSets = null;

        for( A set : loopStack.peek().getContinueOutSets() ){
            if( mergedSets == null ){
                mergedSets = newInitialFlow();
                copy(set, mergedSets);
            }
            else
                merge( set, mergedSets, mergedSets );
        }
        return mergedSets;
    }

}
