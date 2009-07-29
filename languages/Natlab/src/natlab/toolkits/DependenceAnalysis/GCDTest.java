package natlab.toolkits.DependenceAnalysis;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import natlab.ast.NameExpr;
import natlab.ast.MTimesExpr;
import natlab.ast.PlusExpr;
import natlab.ast.MinusExpr;
import natlab.ast.IntLiteralExpr;

/*
 * Author:Amina Aslam
 * Date:24 Jul,2009
 * GCD Test class calculates the GCD of the Constraints' Equations.
 */

public class GCDTest {
	private long u;
	private long v;
	private long gcd;
	private long c=0;
	private boolean isSolution;
	
	public GCDTest()
	{
		u=0;
		v=0;
		gcd=0;
		c=0;
		isSolution=false;
	}
	
	public void calculateGcd (ConstraintsGraph cGraph) 
	{
		
		AffineExpression aExpr1=null,aExpr2=null;		
		Map cMap=cGraph.getGraphData();
		//Get Map in Set interface to get key and value
		Set s=cMap.entrySet();
		
	      //Move next key and value of Map by iterator
        Iterator it=s.iterator();      
        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
        	Map.Entry m =(Map.Entry)it.next();        	
        	String key=(String)m.getKey();      
            ConstraintsList cList1=(ConstraintsList)m.getValue();
            if(cList1.getListNode()!=null)
            {
            	 aExpr1=cList1.getListNode().getData();
            }
            if(cList1.getListNode().getNext()!=null)
            {
            	 aExpr2=cList1.getListNode().getNext().getData();
            }                     
		//long gcd = 0;
	    long r = 0;
	    assignValues(aExpr1,aExpr2);
	    while (true) {
	      if (v == 0) {
	        gcd = u;
	        break;
	      }//end of if 
	      else {
	        r = u % v;
	        System.out.println ("u " + u + ", v " + v + ", u % v " + r);
	        u = v;
	        v = r;
	      }//end of else 
	    }//end of 2nd while
	    isSolution();  
      }//end of 1st while
	 

	}//end of calculateGCD function.
	
	/*
	 * This function calculates whether there is a solution to the system of equations or not.
	 * If the constant is divided by gcd then there is a solution to the system of equations.
	 * 
	 */
	private void isSolution()
	{
		if(c%gcd==0)
		{   isSolution=true;
			System.out.println("Gcd Result" + c%gcd);
		}
		else 
		{isSolution=false;
		 System.out.println("Gcd Result" + c%gcd);
		 System.out.println("There is no integer solution for the system of equations");
		}
	}//end of isSolution function
	
	public boolean getIsSolution()
	{
	    return isSolution;	
	}
	
	
	
	
	
	/*
	 * Assign values to u and v.
	 */
	private void assignValues(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		if(aExpr1.getIndexExpr() instanceof NameExpr)//e.g.i=j
	    {  	u = 1;
	    	if(aExpr2.getIndexExpr() instanceof NameExpr)
	    		{ v = 1;
	    		  if(aExpr1.getC() > 0) c=Math.abs(aExpr2.getC()-aExpr1.getC());
	    		  else if(aExpr1.getC() < 0) c=Math.abs(aExpr2.getC()+aExpr1.getC());
	    		  else if(aExpr1.getC()==0) c=Math.abs(aExpr2.getC());
	    		}	    	
	    }//end of if
		else if(aExpr1.getIndexExpr() instanceof MTimesExpr) //e.g. 2i=2j+10  2i=2j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			
			   if(aExpr2.getIndexExpr() instanceof MTimesExpr) //this check is to handle these types of equations 2j+10
			   {		  
				    MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
				    v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
	    		    if(aExpr1.getC() > 0) c=Math.abs(aExpr2.getC()-aExpr1.getC());
	    		    else if(aExpr1.getC() < 0) c=Math.abs(aExpr2.getC()+aExpr1.getC());
	    		    else if(aExpr1.getC()==0) c=Math.abs(aExpr2.getC());
	    				    
	    	   }//end of 1st if			   		
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof NameExpr) //e.g. i=2j+10  i=2j-10
	    {  	u = 1;
	    	if(aExpr2.getIndexExpr() instanceof MTimesExpr)//aExpr2.getIndexExpr() returns 2j part of the equation.
	    		{			  
	    		    MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
				    v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
	    		    if(aExpr1.getC() > 0) c=Math.abs(aExpr2.getC()-aExpr1.getC());
	    		    else if(aExpr1.getC() < 0) c=Math.abs(aExpr2.getC()+aExpr1.getC());
	    		    else if(aExpr1.getC()==0) c=Math.abs(aExpr2.getC());	    		  
			   }//end of 2nd if			  	    	
	    }//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MTimesExpr) //e.g. 2i=j+10 or 2i=j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
     		if(aExpr2.getIndexExpr() instanceof NameExpr) //this check is to handle these types of equations 2j+10
			   {    v = 1;
				    NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();				    
	    		    if(aExpr1.getC() > 0) c=Math.abs(aExpr2.getC()-aExpr1.getC());
	    		    else if(aExpr1.getC() < 0) c=Math.abs(aExpr2.getC()+aExpr1.getC());
	    		    else if(aExpr1.getC()==0) c=Math.abs(aExpr2.getC());				  
	    		}//end of 1st if
		}//end of else if

		
		/*else if(aExpr1.getIndexExpr() instanceof MTimesExpr) //e.g.2i=2j
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			if(aExpr2.getIndexExpr() instanceof MTimesExpr)
			{			   
				  MTimesExpr mExpr2=(MTimesExpr)((PlusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
				  c=0;
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MTimesExpr) //e.g. 2i=2j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			if(aExpr2.getIndexExpr() instanceof MinusExpr)
			{
			   if(((MinusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-10
			   {
				  MTimesExpr mExpr2=(MTimesExpr)((MinusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
				  c=Math.abs(aExpr2.getC());
			   }//end of 2nd if
			   else v=1;c=Math.abs(aExpr2.getC());				
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof PlusExpr) //e.g. i+10=j+11
		{
			PlusExpr mExpr1=(PlusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i+10=2j+11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()-aExpr1.getC());
				
			}
			else u=1;c=Math.abs(aExpr2.getC()-aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof PlusExpr)
			{
			   if(((PlusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-10
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((PlusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
				  c=Math.abs(aExpr2.getC()-aExpr1.getC());
			   }//end of 2nd if
			   else v=1;		c=Math.abs(aExpr2.getC()-aExpr1.getC());		
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MinusExpr) //e.g. i-10=j-11
		{
			MinusExpr mExpr1=(MinusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i-10=2j-11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()+aExpr1.getC());
			}
			else u=1;	c=Math.abs(aExpr2.getC()+aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof MinusExpr)
			{
			   if(((MinusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-10
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((MinusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
				  c=Math.abs(aExpr2.getC()+aExpr1.getC());
			   }//end of 2nd if
			   else v=1;c=Math.abs(aExpr2.getC()+aExpr1.getC());				
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MinusExpr) //e.g. i-10=j+11
		{
			MinusExpr mExpr1=(MinusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i-10=2j+11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()+aExpr1.getC());
			}
			else u=1;	c=Math.abs(aExpr2.getC()+aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof PlusExpr)
			{
			   if(((PlusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j+11
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((PlusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
					c=Math.abs(aExpr2.getC()+aExpr1.getC());
			   }//end of 2nd if
			   else v=1;	c=Math.abs(aExpr2.getC()+aExpr1.getC());				
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof PlusExpr) //e.g. i+10=j-11
		{
			PlusExpr mExpr1=(PlusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i+10=2j-11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()-aExpr1.getC());
			}
			else u=1;	c=Math.abs(aExpr2.getC()-aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof MinusExpr)
			{
			   if(((MinusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-11
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((MinusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
					c=Math.abs(aExpr2.getC()-aExpr1.getC());
			   }//end of 2nd if
			   else v=1;	c=Math.abs(aExpr2.getC()-aExpr1.getC());				
			}//end of 1st if
		}//end of else if*/

}//end of assign values function 



}
