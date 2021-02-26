/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class IfStmt extends Statement
{
	public Expression cond;
    public Statement thenStmt;
    public Statement elseStmt;
    
    public IfStmt(Expression b, Statement t, Statement e){
        super();
        cond = b;
        thenStmt = t;
        elseStmt = e;
    }
    
    public IfStmt(Expression b, Statement t){
        super();
        cond = b;
        thenStmt = t;
        elseStmt = null;
    }
        
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitIfStmt(this, o);
    }
    
    
}