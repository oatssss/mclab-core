package natlab.toolkits.analysis;

import ast.*;
import java.util.*;

/**
 * Abstract implementation of a structural analysis. 
 */
public abstract class AbstractStructuralAnalysis<A > extends AbstractNodeCaseHandler implements StructuralAnalysis<A>
{

    protected A currentOutSet;
    protected A currentInSet;
    protected Map<ASTNode,A> outFlowSets, inFlowSets;
    protected ASTNode tree;
    protected AnalysisHelper helper;

    protected boolean analyzed = false;

    public AbstractStructuralAnalysis(ASTNode tree){
        this.tree = tree;

        //setup maps
        outFlowSets = new HashMap<ASTNode, A>();
        inFlowSets = new HashMap<ASTNode, A>();

        //setup helper visitor
        helper = new AnalysisHelper<A>( this );
    }
    
    @Override
    public void setCallback(NodeCaseHandler handler) {
        helper.setCallback(handler);
    }

    public ASTNode getTree()
    {
        return tree;
    }

    public Map<ASTNode, A> getOutFlowSets(){
        return outFlowSets;
    }

    /**
     * Associates the given out set with the given node. 
     * @return the outsed previously associated with the node.
     */
    protected A associateOutSet(ASTNode node, A outSet){
        return outFlowSets.put(node, outSet);
    }
    
    public Map<ASTNode, A> getInFlowSets(){
        return inFlowSets;
    }

    /**
     * Associates the given out set with the given node. 
     * @return the outsed previously associated with the node.
     */
    protected A associateInSet(ASTNode node, A inSet){
        return inFlowSets.put(node, inSet);
    }

    
    /**
     * Returns a boolean signifying whether or not the analysis has
     * been performed on a given AST. This does not take into account
     * changes to the AST.
     *
     * @return Whether or not the analysis has been performed.
     */
    public boolean isAnalyzed()
    {
        return analyzed;
    }

    /**
     * Perform the analysis on the entire AST. Also has the effect of
     * setting the is analyzed status to true.
     *
     * Note, as of this time the analyzed status has no effect on
     * whether or not the analysis is performed. This results in the
     * analysis being able to be run multiple times on the same
     * AST. This could be desirable if the AST has been modified in
     * some way. If you wish to prevent the analysis from being
     * performed you should check the is analyzed status with the
     * isAnalyzed method. The is analyzed method does not however take
     * into account any changes made to the AST since the status was
     * changed. 
     *
     * @see isAnalyzed()
     */
    public void analyze()
    {
        //initialize currentOutSet
        currentOutSet = newInitialFlow();
        analyze( tree );

        analyzed = true;
    }
    protected void analyze( ASTNode node )
    {
        node.analyze( helper );
    }
    public abstract void merge(A in1, A in2, A out);
    public abstract void copy(A source, A dest);

    public A getCurrentOutSet()
    {
        return currentOutSet;
    }
    public void setCurrentOutSet( A outSet )
    {
        currentOutSet = outSet;
    }
    public A getCurrentInSet()
    {
        return currentInSet;
    }
    public void setCurrentInSet( A inSet )
    {
        currentInSet = inSet;
    }

    public void caseLoopVar( AssignStmt loopVar )
    {
        caseAssignStmt( loopVar );
    }

    public void caseLoopVarAsInit( AssignStmt loopVar )
    {
        caseLoopVar( loopVar );
    }
    //process the loop variable in the initialization part of a for loop
    //eg  for i=start:step:stop
    //treat as i = start
    public void caseLoopVarAsUpdate( AssignStmt loopVar )
    {
        caseLoopVar( loopVar );
    }
    //prosess in the update 
    //treat as i=i+step
    public void caseLoopVarAsCondition( AssignStmt loopVar )
    {
        caseLoopVar( loopVar );
    }
    //process as checking the condition
    //treat as i<=stop or i*sign(step)<=stop*sign(step)

    public void caseIfCondition( Expr condExpr )
    {
        caseCondition( condExpr );
    }
    public void caseWhileCondition( Expr condExpr )
    {
        caseCondition( condExpr );
    }
    /**
     * A simple implementation of caseCondition. It simply treats it
     * as an expression.
     */
    public void caseCondition( Expr condExpr )
    {
        caseExpr( condExpr );
    }

    public abstract A newInitialFlow();

    public void caseSwitchExpr( Expr switchExpr )
    {
        caseExpr( switchExpr );
    }
}