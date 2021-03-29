package miniJava.ContextualAnalyzer;

import miniJava.ErrorReporter;
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
import miniJava.AbstractSyntaxTrees.TypeDenoter;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;

public class TypeChecker implements Visitor<Object, TypeDenoter>{
	
	private ErrorReporter err;
	private MethodDecl currentmd;
	
	public TypeChecker(ErrorReporter reporter) {
		err = reporter;
		currentmd = null;
	}
	
	private boolean checkEquals(TypeDenoter type1, TypeDenoter type2) {
		TypeKind t1 = type1.typeKind;
		TypeKind t2 = type2.typeKind;
		
		if(t1 == TypeKind.ERROR || t2 == TypeKind.ERROR) {
			return true;
		}
		else if(t1 == TypeKind.UNSUPPORTED) { 
			err.reportError(new SemanticError("cannot reference unsupported type", type1.posn, true));
			return false;
		}
		else if(t2 == TypeKind.UNSUPPORTED) {
			err.reportError(new SemanticError("cannot reference unsupported type", type2.posn, true));
			return false;
		}
		else if(t1 == TypeKind.VOID) {
			err.reportError(new SemanticError("expressions may not have void typing",
					type1.posn, true));
			return false;
		}
		else if(t2 == TypeKind.VOID) {
			err.reportError(new SemanticError("expressions may not have void typing",
					type2.posn, true));
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
				
				return checkEquals(a1.eltType, a2.eltType);
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
		// TODO don't need to check field Decls because they only
		// consist of a type and an id
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
		// TODO does the existence of a return statement (when required)
		// need to be checked for?
		
		boolean reStmt = false;
		currentmd = md;
		
		// the parameter decls are also unvisited since they are like field decls
		//ParameterDeclList pdl = md.parameterDeclList;
		StatementList sl = md.statementList;
		
		//for(ParameterDecl pd : pdl) pd.visit(this, null);
		
		for(Statement s : sl) {
			s.visit(this, null);
		}
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
		
		if(!checkEquals(vtype, exptype)) {
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
		
		if(!checkEquals(vtype, valtype)) {
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
		if(!checkEquals(a.eltType, exptype)) {
			err.reportError(new SemanticError("the element type of the array and the type of "
					+ "the value assigned to it do not agree",
					stmt.posn, true));
		}
		
		return null;
	}

	@Override
	public TypeDenoter visitCallStmt(CallStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitReturnStmt(ReturnStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitIfStmt(IfStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitWhileStmt(WhileStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitUnaryExpr(UnaryExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitBinaryExpr(BinaryExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitRefExpr(RefExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitIxExpr(IxExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitCallExpr(CallExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitLiteralExpr(LiteralExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitThisRef(ThisRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitIdRef(IdRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitQRef(QualRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitIdentifier(Identifier id, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitOperator(Operator op, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitIntLiteral(IntLiteral num, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDenoter visitNullLiteral(NullLiteral nul, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
