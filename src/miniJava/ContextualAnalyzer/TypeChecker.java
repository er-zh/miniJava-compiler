package miniJava.ContextualAnalyzer;

import java.util.Stack;

import miniJava.ErrorReporter;
import miniJava.SourcePosition;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.AssignStmt;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallExpr;
import miniJava.AbstractSyntaxTrees.CallStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassDeclList;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.ExprList;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IfStmt;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.IxAssignStmt;
import miniJava.AbstractSyntaxTrees.IxExpr;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.NullLiteral;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.QualRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.TypeDenoter;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class TypeChecker implements Visitor<Object, TypeDenoter>{	
	private ErrorReporter err;
	
	// type denoter for visiting this refs
	private ClassType currentct;
	// internal variables used for type checking inside of methods
	// ensures that return types are consistent with method decls
	private TypeDenoter currentmdtype;
	private boolean promisesReturn;
	private Stack<Boolean> doesReturn;
	
	public TypeChecker(ErrorReporter reporter) {
		err = reporter;
		currentmdtype = null;
		currentct = null;
	}
	
	public void check(AST ast) {// should not be called multiple times
		ast.visit(this, null);
	}
	
	private boolean checkEquals(TypeDenoter type1, TypeDenoter type2, SourcePosition checkposn) {
		TypeKind t1 = type1.typeKind;
		TypeKind t2 = type2.typeKind;
		
		if(t1 == TypeKind.ERROR || t2 == TypeKind.ERROR) {
			return true;
		}
		else if(t1 == TypeKind.UNSUPPORTED) { 
			err.reportError(new SemanticError("cannot reference unsupported type", checkposn, true));
			return false;
		}
		else if(t2 == TypeKind.UNSUPPORTED) {
			err.reportError(new SemanticError("cannot reference unsupported type", checkposn, true));
			return false;
		}
		else if(t1 == TypeKind.NULL && t2 == TypeKind.CLASS
				|| t2 == TypeKind.NULL && t1 == TypeKind.CLASS) {
			return true;
		}
		else if(t1 == TypeKind.VOID) {
			err.reportError(new SemanticError("expressions may not have void typing",
					checkposn, true));
			return false;
		}
		else if(t2 == TypeKind.VOID) {
			err.reportError(new SemanticError("expressions may not have void typing",
					checkposn, true));
			return false;
		}
		else if(t1 != t2) {
			return false;
		}
		else {
			if(t1 == TypeKind.CLASS) {
				ClassType c1 = (ClassType)type1;
				ClassType c2 = (ClassType)type2;
				
				return c1.className.spelling.equals(c2.className.spelling);
			}
			else if(t1 == TypeKind.ARRAY) {
				ArrayType a1 = (ArrayType)type1;
				ArrayType a2 = (ArrayType)type2;
				
				return checkEquals(a1.eltType, a2.eltType, checkposn);
			}
			else {
				// types must be a base type of some kind
				// int, boolean
				return true;
			}
		}
	}

	@Override
	public TypeDenoter visitPackage(Package prog, Object arg) {
		ClassDeclList cldl = prog.classDeclList;
		
		for(ClassDecl cd : cldl) {
			cd.visit(this, null);
		}
		
		return null;
	}

	@Override
	public TypeDenoter visitClassDecl(ClassDecl cd, Object arg) {
		Identifier cid = new Identifier(new Token(TokenType.ID, cd.name), cd.posn);
		cid.linkDecl(cd);
		currentct = new ClassType(cid, cd.posn);
		
		// don't need to check field Decls because they only consist of a 
		// type and an id
		//FieldDeclList fdl = cd.fieldDeclList;
		MethodDeclList mdl = cd.methodDeclList;
		
		//for(FieldDecl fd : fdl) fd.visit(this, null);
		for(MethodDecl md : mdl) {
			md.visit(this, null);
		}
		
		return null;
	}

	@Override
	public TypeDenoter visitFieldDecl(FieldDecl fd, Object arg) {
		// unused
		return null;
	}
	
	@Override
	public TypeDenoter visitMethodDecl(MethodDecl md, Object arg) {
		// TODO does a return statement exist when asked for?
		
		currentmdtype = md.type;
		promisesReturn = currentmdtype.typeKind != TypeKind.VOID ? true : false;
		doesReturn = new Stack<Boolean>();
				
		// the parameter decls are also unvisited since they are like field decls
		//ParameterDeclList pdl = md.parameterDeclList;
		StatementList sl = md.statementList;
		
		//for(ParameterDecl pd : pdl) pd.visit(this, null);
		
		for(Statement s : sl) {
			s.visit(this, null);
		}
		
		if(promisesReturn) {
			if(doesReturn.isEmpty() || !doesReturn.peek()) {
				err.reportError(new SemanticError("method is declared to return a value but does not always return a value", 
					md.posn, true));
			}
		}

		currentmdtype = null;
		return null;
	}

	@Override
	public TypeDenoter visitParameterDecl(ParameterDecl pd, Object arg) {
		// unused
		return null;
	}

	@Override
	public TypeDenoter visitVarDecl(VarDecl decl, Object arg) {
		return decl.type.visit(this, null);
	}

	@Override
	public TypeDenoter visitBaseType(BaseType type, Object arg) {
		return type;
	}

	@Override
	public TypeDenoter visitClassType(ClassType type, Object arg) {
		if(type.className.spelling.equals("String")) {
			return new BaseType(TypeKind.UNSUPPORTED, type.posn);
		}
		return type;
	}

	@Override
	public TypeDenoter visitArrayType(ArrayType type, Object arg) {
		return type;
	}

	@Override
	public TypeDenoter visitBlockStmt(BlockStmt stmt, Object arg) {
		StatementList sl = stmt.sl;
		
		for(Statement s : sl) {
			s.visit(this, null);
		}
		return null;
	}

	@Override
	public TypeDenoter visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		TypeDenoter vtype = stmt.varDecl.visit(this, null);
		TypeDenoter exptype = stmt.initExp.visit(this, null);
		
		if(!checkEquals(vtype, exptype, stmt.posn)) {
			err.reportError(new SemanticError("the type of the variable and the type of "
					+ "its initializing expression do not agree",
					stmt.posn, true));
		}
		return null;
	}

	@Override
	public TypeDenoter visitAssignStmt(AssignStmt stmt, Object arg) {
		TypeDenoter vtype = stmt.ref.visit(this, null);
		TypeDenoter valtype = stmt.val.visit(this, null);
		
		if(!checkEquals(vtype, valtype, stmt.posn)) {
			err.reportError(new SemanticError("the type of the variable and the type of "
					+ "the value assigned to it do not agree",
					stmt.posn, true));
		}
		return null;
	}

	@Override
	public TypeDenoter visitIxAssignStmt(IxAssignStmt stmt, Object arg) {
		TypeDenoter arrtype = stmt.ref.visit(this, null);
		TypeDenoter ixtype = stmt.ix.visit(this, null);
		TypeDenoter exptype = stmt.exp.visit(this, null);
		
		if(arrtype.typeKind != TypeKind.ARRAY) {
			err.reportError(new SemanticError("cannot index a variable that is not an array",
					stmt.posn, true));
			return null;
		}
		
		if(ixtype.typeKind != TypeKind.INT) {
			err.reportError(new SemanticError("array indices must be integer values",
					stmt.posn, true));
			return null;
		}
		
		// this cast should be valid as a result of the first if check
		ArrayType a = (ArrayType)arrtype;
		if(!checkEquals(a.eltType, exptype, stmt.posn)) {
			err.reportError(new SemanticError("the element type of the array and the type of "
					+ "the value assigned to it do not agree",
					stmt.posn, true));
		}
		
		return null;
	}

	@Override
	public TypeDenoter visitCallStmt(CallStmt stmt, Object arg) {
		//TypeDenoter mrettype = stmt.methodRef.visit(this, null);
		// this ref --> err
		// id ref / qual ref --> need to check that whats being ref'd is a method
		// need the types of its params as well
		MethodDecl refdec;
		try {
			refdec = (MethodDecl)stmt.methodRef.getDecl();
		}
		catch(ClassCastException cce) {
			err.reportError(new SemanticError("cannot call an identifier that does not refer to a method",
					stmt.posn, true));
			return null;
		}
		
		ParameterDeclList pdl = refdec.parameterDeclList;
		ExprList args = stmt.argList;
		
		if(pdl.size() != args.size()) {
			err.reportError(new SemanticError("number of arguments supplied to the method is incorrect",
					stmt.posn, true));
			return null;
		}
		
		for(int i = 0; i < pdl.size(); i++) {
			if(!checkEquals(pdl.get(0).type, args.get(i).visit(this, null), stmt.posn)) {
				err.reportError(new SemanticError("arg " + (i+1) + " of method call does not match the expected type",
						stmt.posn, true));
				return null;
			}
		}
		return null;
	}

	@Override
	public TypeDenoter visitReturnStmt(ReturnStmt stmt, Object arg) {
		if(stmt.returnExpr == null) {
			if(currentmdtype.typeKind != TypeKind.VOID) {
				err.reportError(new SemanticError("return statement of a void method must not return a value",
					stmt.posn, true));
			}
		}
		else {
			if(!checkEquals(currentmdtype, stmt.returnExpr.visit(this, null), stmt.posn)) {
				err.reportError(new SemanticError("type of the return expression must match that of the declared "
						+ "method type", stmt.posn, true));
			}
		}
		
		doesReturn.push(true);
		
		return null;
	}

	@Override
	public TypeDenoter visitIfStmt(IfStmt stmt, Object arg) {
		TypeDenoter condtype = stmt.cond.visit(this, null);
		boolean thenret, elret = false;
		
		if(condtype.typeKind != TypeKind.BOOLEAN) {
			err.reportError(new SemanticError("loop condition must evaluate to a boolean type", stmt.posn, true));
			return null;
		}
		
		doesReturn.push(false);
		stmt.thenStmt.visit(this, null);
		thenret = doesReturn.peek();
		while(doesReturn.pop()) {} // must eventually run into the false value that it pushed
		
		doesReturn.push(false);
		if(stmt.elseStmt != null) {
			stmt.elseStmt.visit(this, null);
		}
		elret = doesReturn.peek();
		while(doesReturn.pop()) {}
		
		if(thenret && elret) {
			doesReturn.push(true);
		}
		
		return null;
	}

	@Override
	public TypeDenoter visitWhileStmt(WhileStmt stmt, Object arg) {
		TypeDenoter condtype = stmt.cond.visit(this, null);
		
		if(condtype.typeKind != TypeKind.BOOLEAN) {
			err.reportError(new SemanticError("loop condition must evaluate to a boolean type", stmt.posn, true));
			return new BaseType(TypeKind.ERROR, stmt.posn);
		}
		
		stmt.body.visit(this, null);
		
		return null;
	}

	@Override
	public TypeDenoter visitUnaryExpr(UnaryExpr expr, Object arg) {
		TypeDenoter exptype = expr.expr.visit(this, null);
		String rator = expr.operator.spelling;
		if(rator.equals("-") && exptype.typeKind == TypeKind.INT
				|| rator.equals("!") && exptype.typeKind == TypeKind.BOOLEAN) {
			return exptype;
		}
		
		err.reportError(new SemanticError(rator + " operator may not be applied to expressions "
				+ "that are not of type " + (rator.equals("-")?"INT":"BOOLEAN"), expr.posn, true));
		
		return new BaseType(TypeKind.ERROR, expr.posn);
	}

	@Override
	public TypeDenoter visitBinaryExpr(BinaryExpr expr, Object arg) {
		TypeDenoter lefttype = expr.left.visit(this, null);
		TypeDenoter righttype = expr.right.visit(this, null);
		String rator = expr.operator.spelling;
		
		switch(rator) {
		case "+":
		case "-":
		case "*":
		case "/":
			if(lefttype.typeKind == TypeKind.INT && righttype.typeKind == TypeKind.INT) {
				return lefttype;
			}
			break;
		case "<=":
		case "<":
		case ">":
		case ">=":
			if(lefttype.typeKind == TypeKind.INT && righttype.typeKind == TypeKind.INT) {
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
			}
			break;
		case "&&":
		case "||":
			if(lefttype.typeKind == TypeKind.BOOLEAN && righttype.typeKind == TypeKind.BOOLEAN) {
				return lefttype;
			}
			break;
		case "==":
		case "!=":
			if(checkEquals(lefttype, righttype, expr.posn)) {
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
			}
			break;
		}
		// either the types for left and right don't match
		// or the rator string isn't matched
		// i have no idea how the latter case could happen
		
		err.reportError(new SemanticError("left and right expression types of the binop do not match",
				expr.posn, true));
		
		return new BaseType(TypeKind.ERROR, expr.posn);
	}

	@Override
	public TypeDenoter visitRefExpr(RefExpr expr, Object arg) {
		TypeDenoter reftype = expr.ref.visit(this, null);
		if(reftype.typeKind == TypeKind.UNSUPPORTED) {
			err.reportError(new SemanticError("references may not be made to unsupported types",
					expr.posn, true));
			return new BaseType(TypeKind.UNSUPPORTED, expr.posn);
		}
		else {
			return reftype;
		}
	}

	@Override
	public TypeDenoter visitIxExpr(IxExpr expr, Object arg) {
		TypeDenoter arrtype = expr.ref.visit(this, null);
		TypeDenoter ixtype = expr.ixExpr.visit(this, null);
		
		if(arrtype.typeKind != TypeKind.ARRAY) {
			err.reportError(new SemanticError("cannot index a variable that is not an array",
					expr.posn, true));
			return new BaseType(TypeKind.ERROR, expr.posn);
		}
		
		if(ixtype.typeKind != TypeKind.INT) {
			err.reportError(new SemanticError("array indices must be integer values",
					expr.posn, true));
			return new BaseType(TypeKind.ERROR, expr.posn);
		}
		
		return ((ArrayType)arrtype).eltType.visit(this, null);
	}

	@Override
	public TypeDenoter visitCallExpr(CallExpr expr, Object arg) {
		TypeDenoter mrettype = expr.methodRef.visit(this, null);
		// this ref --> err
		// id ref / qual ref --> need to check that whats being ref'd is a method
		// need the types of its params as well
		MethodDecl refdec;
		try {
			refdec = (MethodDecl)expr.methodRef.getDecl();
		}
		catch(ClassCastException cce) {
			err.reportError(new SemanticError("cannot call an identifier that does not refer to a method",
					expr.posn, true));
			return new BaseType(TypeKind.ERROR, expr.posn);
		}
		
		ParameterDeclList pdl = refdec.parameterDeclList;
		ExprList args = expr.argList;
		
		if(pdl.size() != args.size()) {
			err.reportError(new SemanticError("number of arguments supplied to the method is incorrect",
					expr.posn, true));
			return new BaseType(TypeKind.ERROR, expr.posn);
		}
		
		for(int i = 0; i < pdl.size(); i++) {
			if(!checkEquals(pdl.get(0).type, args.get(i).visit(this, null), args.get(i).posn)) {
				err.reportError(new SemanticError("arg " + (i+1) + " of method call does not match the expected type",
						expr.posn, true));
				return new BaseType(TypeKind.ERROR, expr.posn);
			}
		}
		return mrettype;
	}

	@Override
	public TypeDenoter visitLiteralExpr(LiteralExpr expr, Object arg) {
		return expr.lit.visit(this, null);
	}

	@Override
	public TypeDenoter visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		return expr.classtype.visit(this, null);
	}

	@Override
	public TypeDenoter visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		TypeDenoter sizetype = expr.sizeExpr.visit(this, null);
		
		if(sizetype.typeKind != TypeKind.INT) {
			err.reportError(new SemanticError("arrays must be defined with integer sizes", expr.posn, true));
			return new BaseType(TypeKind.ERROR, expr.posn);
		}
		return new ArrayType(expr.eltType.visit(this, null), expr.posn);
	}

	@Override
	public TypeDenoter visitThisRef(ThisRef ref, Object arg) {
		return currentct;
	}

	@Override // TODO id type might not exist if id points to a class
	public TypeDenoter visitIdRef(IdRef ref, Object arg) {
		try {
			return ref.id.getDecl().type.visit(this, null);
		}
		catch(NullPointerException npe) {
			err.reportError(new SemanticError("may not use class name as a value", ref.posn, true));
			return new BaseType(TypeKind.ERROR, ref.posn);
		}
	}

	@Override
	public TypeDenoter visitQRef(QualRef ref, Object arg) {
		return ref.id.getDecl().type.visit(this, null);
	}

	@Override
	public TypeDenoter visitIdentifier(Identifier id, Object arg) {
		return id.getDecl().type.visit(this, null);
	}

	@Override
	public TypeDenoter visitOperator(Operator op, Object arg) {
		// unused
		return null;
	}

	@Override
	public TypeDenoter visitIntLiteral(IntLiteral num, Object arg) {
		return new BaseType(TypeKind.INT, num.posn);
	}

	@Override
	public TypeDenoter visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		return new BaseType(TypeKind.BOOLEAN, bool.posn);
	}

	@Override
	public TypeDenoter visitNullLiteral(NullLiteral nul, Object arg) {
		return new BaseType(TypeKind.NULL, nul.posn);
	}

}
