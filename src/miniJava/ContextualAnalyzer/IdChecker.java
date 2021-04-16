package miniJava.ContextualAnalyzer;

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
import miniJava.AbstractSyntaxTrees.MemberDecl;
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
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class IdChecker implements Visitor<Object, Object>{
	
	private IdTable table;
	private ErrorReporter err;
	//used for identfying instances of this
	private ClassDecl currentClassDecl; 
	// lets checker know when it is w/in static methods
	private boolean isStaticContext; 
	
	public IdChecker(ErrorReporter reporter) {
		err = reporter;
		table = new IdTable(err);
		currentClassDecl = null;
	}

	public void check(AST ast) {// should not be called multiple times
		ast.visit(this, null);
	}
	
	@Override
	public Object visitPackage(Package prog, Object arg) {
		table.openScope(); // level 1 --- class names
		
		ClassDeclList clDecList = prog.classDeclList;
		
		addPredecIds(clDecList);
		
		for(ClassDecl classDec : clDecList) table.enter(classDec);
		
		for(ClassDecl classDec : clDecList) {
			currentClassDecl = classDec;
			classDec.visit(this, null);
		}
		
		table.closeScope();
		return null;
	}
	
	private void addPredecIds(ClassDeclList cdl) {
		// predeclared identifiers used in miniJava
		// these identifiers will have 0 as their sourceposition as
		// they are not found within the source file
		SourcePosition predecl = new SourcePosition(0);

		// extra definitions for the println method of _PrintStream
		MethodDeclList psmdl = new MethodDeclList();
		ParameterDeclList printpl = new ParameterDeclList();
		printpl.add(new ParameterDecl(new BaseType(TypeKind.INT, predecl), "n", predecl));
		psmdl.add(new MethodDecl(new FieldDecl(false, false, new BaseType(TypeKind.VOID, predecl), 
				"println", predecl),
				printpl, new StatementList(), predecl));
		ClassDecl ps = new ClassDecl("_PrintStream", new FieldDeclList(), psmdl, predecl);

		// extra definitions for the out field of System
		FieldDeclList Systemfdl = new FieldDeclList();
		Identifier outTypeid = new Identifier(new Token(TokenType.ID, "_PrintStream"), predecl);
		outTypeid.linkDecl(ps);
		Systemfdl.add(new FieldDecl(false, true, new ClassType(outTypeid, predecl), "out", predecl));

		
		cdl.add(new ClassDecl("System", Systemfdl, new MethodDeclList(), predecl)); // predef'd System class
		cdl.add(ps); // predef'd _PrintStream class
		cdl.add(new ClassDecl("String", new FieldDeclList(), new MethodDeclList(), predecl)); // predef'd String class
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		table.openScope(); // level 2 --- member names
		isStaticContext = false;
		
		FieldDeclList fdl = cd.fieldDeclList;
		MethodDeclList mdl = cd.methodDeclList;
		
		for(FieldDecl fd : fdl) table.enter(fd);
		for(MethodDecl md : mdl) table.enter(md);
		
		for(FieldDecl fd : fdl) fd.visit(this, null);
		for(MethodDecl md : mdl) {
			isStaticContext = md.isStatic;
			md.visit(this, null);
		}
		
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
		md.type.visit(this, null); // return type of the method
		
		table.openScope(); // level 3 -- params
		
		ParameterDeclList pdl = md.parameterDeclList;
		StatementList sl = md.statementList;
		
		for(ParameterDecl pd : pdl) table.enter(pd);
		
		table.openScope(); // level 4 --- local method vars
		
		for(ParameterDecl pd : pdl) pd.visit(this, null);
		
		for(Statement s : sl) {
			s.visit(this, null);
		}
		
		
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
		
		// any name not predifined is parsed as a ClassType when used as a type
		// this does not necessarily mean that the name refers to a class
		if(!(type.className.getDecl() instanceof ClassDecl)) {
			// check that the actual class isn't being shadowed
			Declaration cd = table.retrieve("__"+type.className.spelling);
			
			if(cd instanceof ClassDecl) {
				// class is being shadowed, need to link the correct decl now
				type.className.linkDecl(cd);
			}
			else {
				err.reportError(new SemanticError("\"" + type.className.spelling 
						+ "\" is not a defined class", type.posn, false));
			}
		}
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
		
		table.promiseEnter(vd);
		
		vd.visit(this, null);
		
		stmt.initExp.visit(this, null);
		
		table.fulfillEnter(vd);
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
		checkNotSingleVarDecl(stmt.thenStmt);
		
		Statement es = stmt.elseStmt;
		
		if(es != null) {
			es.visit(this, null);
			checkNotSingleVarDecl(es);
		}
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg) {
		stmt.cond.visit(this, null);
		
		stmt.body.visit(this, null);
		
		checkNotSingleVarDecl(stmt.body);
		return null;
	}
	
	private void checkNotSingleVarDecl(Statement stmt) {
		if(stmt instanceof VarDeclStmt) {
			err.reportError(new SemanticError("solitary variable declaration not allowed within a conditional statement", 
					stmt.posn, false));
		}
		// TODO check if solitary decls in block statments also need to fail
		//else if(stmt instanceof BlockStmt) {}
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
		
		if(expr.ref.getDecl() instanceof MethodDecl) {
			err.reportError(new SemanticError("reference does not denote a variable, instead references a method", 
					expr.posn, false));
		}
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
		expr.methodRef.visit(this, null);
		
		ExprList args = expr.argList;
		
		for(Expression arg : args) arg.visit(this, null);
		return null;
	}
	
	// literals are predefined so they are garuanteed to be valid
	// if parsed properly
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
		if(isStaticContext) {
			err.reportError(new SemanticError("cannot use \"this\" within a static context",
					ref.posn, false));
		}
		
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
		if(conDecl == null) {
			err.reportError(new SemanticError("reference to erroneous declaration made",
					ref.posn, false));
			return null;
		}
		else if(conDecl instanceof MethodDecl) {
			err.reportError(new SemanticError("methods may not be used as qual refs in miniJava",
					ref.posn, false));
		}
		
		//check for this or recursive class
		boolean withinClass;

		// the qualifier must be an instance of a class
		// since it has members that are being referenced
		
		// note a().b or a[].b not allowed
		
		// for a.b, if a is a var, param, or field
		// then check their typeDenoter for the class type
		// if there is no typeDenoter then a is a direct ref
		// to a class
		ClassDecl classdec = null;
		MemberDecl idDecl = null;
		if(conDecl.type != null) {
			try {
				Identifier cti = ((ClassType)conDecl.type).className;
				if(cti.getDecl() == null) {
					// identifier has not yet been linked to its decl
					// or it doesn't have one
					cti.visit(this, null); // try to find its class decl
				}
				
				classdec = (ClassDecl)cti.getDecl();
			}
			catch(ClassCastException cce) {
				err.reportError(new SemanticError("identifier being dereferenced does not refer to an instance of a class",
						ref.posn, false));
				return null;
			}
			
			
			
			idDecl = findMemberDecl(ref.id, classdec);
		}
		else { 
			// conDecl.type is null meaing the controlling decl is already a class decl
			// thus the qref is a direct reference to a class 
			// 		qualified id must be static
			// or is a "this" kw
			// 		qualified id can be anything
			try {
			classdec = (ClassDecl) conDecl;
			}
			catch(ClassCastException cce) {
				err.reportError(new SemanticError("identifier being dereferenced is not a valid class",
						ref.posn, false));
				return null;
			}
			
			idDecl = findMemberDecl(ref.id, classdec);
			if(!idDecl.isStatic && isStaticContext) {
				err.reportError(new SemanticError("cannot reference a non-static field", 
						ref.posn, false));
			}
		}
		withinClass = classdec.name.equals(currentClassDecl.name);
		
		if(idDecl.isPrivate && !withinClass) {
			err.reportError(new SemanticError("cannot access a private field from outside of the declaring class",
					ref.posn, false));
		}
		
		// manually link the decl of the qualifying id
		// since its decl does not actually appear in the
		// scoped id table at the scope where the qualifier appears
		ref.id.linkDecl(idDecl);
		
		ref.linkDecl(idDecl);
		
		return null;
	}
	
	private MemberDecl findMemberDecl(Identifier id, ClassDecl classdec) {
		String idname = id.spelling;
		
		for(FieldDecl fd : classdec.fieldDeclList) {
			if(idname.equals(fd.name)) return fd;
		}
		
		for(MethodDecl md : classdec.methodDeclList) {
			if(idname.equals(md.name)) return md;
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
			err.reportError(new SemanticError(id.spelling + " referenced but not previously declared",
					id.posn, false));
		}
		else {
			if(isStaticContext) {
				// if within a static context, need to check if the memberDecl
				// is static
				if(dec instanceof MemberDecl) {
					MemberDecl cd = (MemberDecl) dec;
					
					if(!cd.isStatic) {
						err.reportError(new SemanticError(id.spelling + " is a declared to be non-static and cannot be used in a static context",
								id.posn, false));
					}
				}
			}
			
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
