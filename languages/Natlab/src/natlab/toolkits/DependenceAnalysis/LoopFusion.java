/*.........................
 * Loop Transformations
 * Author:Amina Aslam.
 * Creation Date:Mar15,2009
 * Whenever a For node with "LoopFusion" transformation annotated is encountered or visited
 * an object of this class is instantiated.
 */
package natlab.toolkits.DependenceAnalysis;

import java.math.BigDecimal;
import java.math.BigInteger;

import ast.ASTNode;
import ast.AssignStmt;
import ast.FPLiteralExpr;
import ast.NameExpr;
import ast.Opt;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.RangeExpr;
import ast.Stmt;

public class LoopFusion {
	
private ForStmt forStmt1;
private ForStmt forStmt2;
	
public LoopFusion(ForStmt fStmt1,ForStmt fStmt2){	
			forStmt1=fStmt1;		
			forStmt2=fStmt2;		
		
}
/*
 * This method does the following.
 * 1.Check the loop bounds of the two loops that need to be fused.
		1.If the upper,lower bounds and no of iterations are the same for the two loops then copy the statements of second loop into first loop.
		2.Else return an error �Two loops cannot be fused�.
	2.Once two loops are combined into one loop,remove the second loop from the AST and send the transformed AST to pretty printer for generating new MATLAB code from the transformed AST.
 */
public void ApplyLoopFusion(){
		
		if(TestLoopLimits()){
			ast.List<Stmt> forList1=forStmt1.getStmtList();
			forList1.removeChild(0);
			ast.List<Stmt> forList2=forStmt2.getStmtList();
			forList2.removeChild(0);
			
			AssignStmt assStmt1= forStmt1.getAssignStmt();//This gives the assignment statement of the loop
			AssignStmt assStmt2= forStmt2.getAssignStmt();//This gives the assignment statement of the loop			
			
			//int no= assStmt2.getNumChild();
			int no=forStmt2.getNumStmt();
			//System.out.println("No of statements in loop2 " + no);
			for(int i=0;i<no;i++){
			    forStmt1.addStmt(forStmt2.getStmt(i));
			}	
				
			ASTNode parent = forStmt2.getParent(); //get the parent of the second loop 
			int loc = parent.getIndexOfChild(forStmt2);
				
			if (loc>=0) parent.removeChild(loc); //remove the second loop from the AST
		}
		else{
			System.out.println("Two loops cannot be fused because their loop limits are different");
		}
		
		
	}
	/*
	 * This method does the following.
	 * 1.Check the loop bounds of the two loops that need to be fused.
	 */	
  private boolean TestLoopLimits(){
		
		AssignStmt assStmt1= forStmt1.getAssignStmt();//This gives the assignment statement of the loop
		AssignStmt assStmt2= forStmt2.getAssignStmt();//This gives the assignment statement of the loop
	    if(assStmt1.getRHS() instanceof RangeExpr && assStmt2.getRHS() instanceof RangeExpr){	
	    	
	    	RangeExpr expr1=(RangeExpr) assStmt1.getRHS();
	    	RangeExpr expr2=(RangeExpr) assStmt2.getRHS();
	    	int LIndex1;
    		int LIndex2;
    		int UIndex1;
    		int UIndex2;
    		
        	float fLIndex1;
    		float fLIndex2;
    		float fUIndex1;
    		float fUIndex2;
    		boolean exprOptFlag=false;
	    	if(expr1.getLower() instanceof IntLiteralExpr && expr2.getLower() instanceof IntLiteralExpr){
	    		
				
				IntLiteralExpr fExprLower1=(IntLiteralExpr) expr1.getLower();				
				LIndex1 = fExprLower1.getValue().getValue().intValue();				
				//System.out.println("Lower Index of loop 1  " + LIndex1);
				
				IntLiteralExpr fExprLower2=(IntLiteralExpr) expr2.getLower();				
				LIndex2 = fExprLower2.getValue().getValue().intValue();				
				//System.out.println("Lower Index of loop 2  " + LIndex2);
			
				IntLiteralExpr fExprUpper1=(IntLiteralExpr) expr1.getUpper();
				UIndex1 = fExprUpper1.getValue().getValue().intValue();
			//	System.out.println("Upper Index of loop1  " + UIndex1);
				
				
				
				IntLiteralExpr fExprUpper2=(IntLiteralExpr) expr2.getUpper();
				UIndex2 = fExprUpper2.getValue().getValue().intValue();
			//	System.out.println("Upper Index of loop2  " + UIndex2);
				
				exprOptFlag=checkExprOpt(expr1,expr2);
				if((LIndex1==LIndex2 && UIndex1==UIndex2)&& exprOptFlag){
			    	return true;
			    }

			}
	    	
	    	if(expr1.getLower() instanceof FPLiteralExpr && expr2.getLower() instanceof FPLiteralExpr)
			{
	    		
				
				FPLiteralExpr fExprLower1=(FPLiteralExpr) expr1.getLower();				
				fLIndex1 = fExprLower1.getValue().getValue().floatValue();				
				//System.out.println("Lower Index of loop 1  " + LIndex1);
				
				FPLiteralExpr fExprLower2=(FPLiteralExpr) expr2.getLower();				
				fLIndex2 = fExprLower2.getValue().getValue().floatValue();				
				//System.out.println("Lower Index of loop 2  " + LIndex2);
			
				FPLiteralExpr fExprUpper1=(FPLiteralExpr) expr1.getUpper();
				fUIndex1 = fExprUpper1.getValue().getValue().floatValue();
			//	System.out.println("Upper Index of loop1  " + UIndex1);
				
				FPLiteralExpr fExprUpper2=(FPLiteralExpr) expr2.getUpper();
				fUIndex2 = fExprUpper2.getValue().getValue().floatValue();
			//	System.out.println("Upper Index of loop2  " + UIndex2);
				
				exprOptFlag=checkExprOpt(expr1,expr2);
				if((fLIndex1==fLIndex2 && fUIndex1==fUIndex2) && exprOptFlag) 
			    {
			    	return true;
			    }
			}			
		
	    	
			
	    }
	    
	    return false;
		
		
	}
	
	private boolean checkExprOpt(RangeExpr expr1,RangeExpr expr2){
		boolean flag=false;
		if(expr1.hasIncr() && expr2.hasIncr())
		{						
			if(expr1.getIncr() instanceof IntLiteralExpr && expr2.getIncr() instanceof IntLiteralExpr)
			{
				IntLiteralExpr incExpr1=(IntLiteralExpr)expr1.getIncr();					
				int incValue1=incExpr1.getValue().getValue().intValue();				
				
				IntLiteralExpr incExpr2=(IntLiteralExpr)expr2.getIncr();					
				int incValue2=incExpr2.getValue().getValue().intValue();
				
				if(incValue1==incValue2)
				{
				  flag=true;	
				}
				return flag;
			}
			
			else if(expr1.getIncr() instanceof FPLiteralExpr && expr2.getIncr() instanceof FPLiteralExpr)
			{
				FPLiteralExpr incExpr1=(FPLiteralExpr)expr1.getIncr();					
				float incValue1=incExpr1.getValue().getValue().floatValue();
				
				FPLiteralExpr incExpr2=(FPLiteralExpr)expr2.getIncr();					
				float incValue2=incExpr2.getValue().getValue().floatValue();
				if(incValue1==incValue2)
				{
				  flag=true;	
				}
				return flag;
			}
		}
		else if(!expr1.hasIncr() && !expr2.hasIncr())
		{
				flag=true;
				return flag;
			
		}
		return flag;

		
	}

}