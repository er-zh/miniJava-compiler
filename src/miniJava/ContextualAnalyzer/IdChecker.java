package miniJava.ContextualAnalyzer;

import java.io.File;
import java.util.HashMap;

import miniJava.ErrorReporter;
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
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.ExprList;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
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
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;

public class IdChecker implements Visitor<Object, Object>{
	private IdTable table;
	private ErrorReporter err;
	//used for identfying instances of this
	private ClassDecl currentClassDecl; 
	
	public IdChecker(ErrorReporter reporter) {
		table = new IdTable();
		err = reporter;
		currentClassDecl = null;
	}
	
	public IdChecker() { // TODO remove this
		table = new IdTable();
		err = null;
	} // should really only be used for testing if at all

	// TODO handle exceptions thrown by idTable
	public void check(AST ast) {
		ast.visit(this, null);
	}
	
	@Override
	public Object visitPackage(Package prog, Object arg) {
		table.openScope(); // level 1 --- class names
		
		ClassDeclList clDecList = prog.classDeclList;
		
		for(ClassDecl classDec : clDecList) table.enter(classDec);
		
		for(ClassDecl classDec : clDecList) {
			currentClassDecl = classDec;
			classDec.visit(this, null);
		}
		
		table.closeScope();
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		table.openScope(); // level 2 --- member names
		
		FieldDeclList fdl = cd.fieldDeclList;
		MethodDeclList mdl = cd.methodDeclList;
		
		for(FieldDecl fd : fdl) table.enter(fd);
		for(MethodDecl md : mdl) table.enter(md);
		
		for(FieldDecl fd : fdl) fd.visit(this, null);
		for(MethodDecl md : mdl) md.visit(this, null);
		
		table.closeScope();
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		fd.type.visit(this, null);
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		md.type.visit(this, null);
		
		table.openScope(); // level 3 -- params
		
		ParameterDeclList pdl = md.parameterDeclList;
		StatementList sl = md.statementList;
		
		for(ParameterDecl pd : pdl) table.enter(pd);
		
		table.openScope(); // level 4 --- inside of the method
		
		for(ParameterDecl pd : pdl) pd.visit(this, null);
		
		for(Statement s : sl) s.visit(this, null);
		
		
		table.closeScope();
		table.closeScope();
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg) {
		pd.type.visit(this, null);
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		decl.type.visit(this, null);
		return null;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg) {
		// don't need to do anything since the base type always exists in the lang
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg) {
		type.className.visit(this, null);
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg) {
		type.eltType.visit(this, null);
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, Object arg) {
		table.openScope();
		
		StatementList sl = stmt.sl;
		
		for(Statement s : sl) {
			s.visit(this, null);
		}
		
		table.closeScope();
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		VarDecl vd = stmt.varDecl;
		
		table.enter(vd);
		
		vd.visit(this, null);
		
		stmt.initExp.visit(this, null);
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		stmt.ref.visit(this, null);
		
		stmt.val.visit(this, null);
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg) {
		stmt.ref.visit(this, null);
		
		stmt.ix.visit(this, null);
		
		stmt.exp.visit(this, null);
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object argo) {
		stmt.methodRef.visit(this, null);
		
		ExprList args = stmt.argList;
		
		for(Expression arg : args) arg.visit(this, null);
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg) {
		Expression re = stmt.returnExpr;
		
		if(re != null) {
			re.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg) {
		stmt.cond.visit(this, null);
		
		stmt.thenStmt.visit(this, null);
		
		Statement es = stmt.elseStmt;
		
		if(es != null) {
			es.visit(this, null);
		}
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg) {
		stmt.cond.visit(this, null);
		
		stmt.body.visit(this, null);
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg) {
		expr.expr.visit(this, null);
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg) {
		expr.left.visit(this, null);
		expr.right.visit(this, null);
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg) {
		expr.ref.visit(this, null);
		return null;
	}

	@Override
	public Object visitIxExpr(IxExpr expr, Object arg) {
		expr.ref.visit(this, null);
		expr.ixExpr.visit(this, null);
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object argo) {
		expr.functionRef.visit(this, null);
		
		ExprList args = expr.argList;
		
		for(Expression arg : args) arg.visit(this, null);
		return null;
	}
	
	// TODO figure out if this is used/necessary
	// literals are predefined so they should be garuanteed
	// to be valid
	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg) {
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		expr.classtype.visit(this, null);
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		expr.eltType.visit(this, null);
		expr.sizeExpr.visit(this, null);
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg) {
		ref.linkDecl(currentClassDecl);
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg) {
		ref.id.visit(this, null);
		ref.linkDecl(ref.id.getDecl());
		return null;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg) {
		ref.ref.visit(this, null);
		Declaration conDecl = ref.ref.getDecl();
		// the qualifier must be an instance of a class
		// a().b or a[].b not allowed
		// for a.b, a must be a var, param, or field
		// then check their typeDenoter for the class type
		ClassDecl classdec = null;
		try {
			classdec = (ClassDecl)((ClassType)conDecl.type).className.getDecl();
		}
		catch(ClassCastException cce) {
			err.reportError(new SemanticError("identifier being dereferenced does not refer to an instance of a class",
					ref.posn, false));
			return null;
		}
		
		Declaration idDecl = findMemberDecl(ref.id, classdec);
		
		// manually link the decl of the qualifying id
		// since its decl, may not actually appear in the
		// scoped id table at the scope where the id itself
		// appears
		ref.id.linkDecl(idDecl);
		
		ref.linkDecl(idDecl);
		
		return null;
	}
	
	private Declaration findMemberDecl(Identifier id, ClassDecl classdec) {
		String idname = id.spelling;
		
		for(FieldDecl fd : classdec.fieldDeclList) {
			if(fd.name == idname) return fd;
		}
		
		for(MethodDecl md : classdec.methodDeclList) {
			if(md.name == idname) return md;
		}
		
		// if the correct decl is not found in the controlling
		// decl then throw an error
		err.reportError(new SemanticError("qualified identifier not found in / not a member of the qualifying class",
				id.posn, false));
		
		return null;
	}
	
	@Override
	public Object visitIdentifier(Identifier id, Object arg) {
		Declaration dec = table.retrieve(id.spelling);
		if(dec == null) {
			err.reportError(new SemanticError(id.spelling + " class type referenced but not previously declared",
					id.posn, false));
		}
		else {
			id.linkDecl(dec);
		}
		
		return null;
	}
	
	//*************************************//
	// predefined so nothing needs to be done
	
	@Override
	public Object visitOperator(Operator op, Object arg) {
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg) {
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nul, Object arg) {
		return null;
	}

}
