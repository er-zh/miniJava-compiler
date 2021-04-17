package miniJava.CodeGenerator;

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
import miniJava.AbstractSyntaxTrees.QualRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;

public class Encoder implements Visitor<Integer, Integer>{	
	public String startClass;
	
	public Encoder(String mainClassName) {
		startClass = mainClassName;
	}
	
	public void encode(AST dectree, String objfilename) {
		String ofname = objfilename+".mJAM";
		
		dectree.visit(this, null);
	}

	@Override
	public Integer visitPackage(Package prog, Integer arg) {
		ClassDeclList cldl = prog.classDeclList;
		
		// change the ordering of classes so that the class with the main function is visited first
		// then change the ordering of methods in that class so the main method is visited first
		for(int i = 0; i < cldl.size(); i++) {
			ClassDecl cd = cldl.get(i);
			if(cd.name.equals(startClass)) {
				MethodDeclList mdl = cd.methodDeclList;
				for(int j = 0; j < mdl.size(); j++) {
					MethodDecl md = mdl.get(i);
					if(md.name.equals("main")) {
						// main should be garunteed to be fond because of the identification step
						
						mdl.swapToFront(j);
					}
				}
				
				cldl.swapToFront(i);
			}
		}
		
		for(ClassDecl cd : cldl) {
			cd.visit(this, null);
		}
		
		Machine.emit(Op.HALT);
		return null;
	}

	@Override
	public Integer visitClassDecl(ClassDecl cd, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitFieldDecl(FieldDecl fd, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitMethodDecl(MethodDecl md, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitParameterDecl(ParameterDecl pd, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitVarDecl(VarDecl decl, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitBaseType(BaseType type, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitClassType(ClassType type, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitArrayType(ArrayType type, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitBlockStmt(BlockStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitVardeclStmt(VarDeclStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitAssignStmt(AssignStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIxAssignStmt(IxAssignStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitCallStmt(CallStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitReturnStmt(ReturnStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIfStmt(IfStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitWhileStmt(WhileStmt stmt, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitUnaryExpr(UnaryExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitBinaryExpr(BinaryExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitRefExpr(RefExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIxExpr(IxExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitCallExpr(CallExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitLiteralExpr(LiteralExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitNewObjectExpr(NewObjectExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitNewArrayExpr(NewArrayExpr expr, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitThisRef(ThisRef ref, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIdRef(IdRef ref, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitQRef(QualRef ref, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIdentifier(Identifier id, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitOperator(Operator op, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIntLiteral(IntLiteral num, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitBooleanLiteral(BooleanLiteral bool, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitNullLiteral(NullLiteral nul, Integer arg) {
		// TODO Auto-generated method stub
		return null;
	}
	// inherited -> stack frame size
	// synthesized -> decls only, size of var declared
	
	
}
