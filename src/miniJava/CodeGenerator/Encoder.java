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
import miniJava.AbstractSyntaxTrees.Declaration;
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
import miniJava.AbstractSyntaxTrees.QualRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Prim;
import mJAM.Machine.Reg;

public class Encoder implements Visitor<Integer, Integer>{	
	private String startClass;
	private int main_code_addr;
	private int staticSeg;
	private ClassDecl currentcd;
	private int offset;
	
	// used set to true prior to any ref visit
	// this way, if an unqualified field identifier is visited
	// it knows to reference the OB directly, rather than expecting
	// a heap address value to be already preplaced onto the stack
	// for it to use
	private boolean implicitthis;
	
	
	public Encoder(String mainClassName) {
		Machine.initCodeGen();
		startClass = mainClassName;
		
		main_code_addr = -1;
		staticSeg = 0; // equiv to static seg ends at stack base (Reg.SB)
	}
	
	public void encode(AST dectree) {
		// prior to generating any code
		// emit preamble code for starting the program
		
		// set up the static segment of the stack
		int patch_static_seg = Machine.nextInstrAddr();
		Machine.emit(Op.PUSH, 0); 
		
		// set up call to main
		Machine.emit(Op.LOADL,0);            // array length 0
		Machine.emit(Prim.newarr);           // empty String array argument
		int patch_main_call_addr = Machine.nextInstrAddr();  // record instr addr where main is called                                                
		Machine.emit(Op.CALL,Reg.CB,-1);     // static call main (address to be patched)
		Machine.emit(Op.HALT,0,0,0);         // end execution
		
		// generate code for the other methods
		dectree.visit(this, null); 
		
		Machine.patch(patch_main_call_addr, main_code_addr);
		Machine.patch(patch_static_seg, staticSeg);
	}
	
	// implementation of visitor methods
	// inherited -> stack frame size
	// synthesized -> decls only, size of var declared
		
	@Override
	public Integer visitPackage(Package prog, Integer arg) {
		ClassDeclList cldl = prog.classDeclList;
		
		// attach REDs to each class decl before generating
		// mJAM code for methods
		for(ClassDecl cd : cldl) {
			FieldDeclList fdl = cd.fieldDeclList;
			int classSize = 0; // == # of nonstatic fields on a class
			
			for(FieldDecl fd : fdl) {
				offset = classSize;
				classSize += fd.visit(this, arg);
			}
			
			// TODO maybe use a more appropriate RED
			cd.setRED(new KnownValue(classSize, -3)); 
		}
		
		for(ClassDecl cd : cldl) {
			cd.visit(this, null);
		}
		
		return null;
	}
	
	@Override
	public Integer visitClassDecl(ClassDecl cd, Integer arg) {
		currentcd = cd;
		
		MethodDeclList mdl = cd.methodDeclList;
		
		for(MethodDecl md : mdl) md.visit(this, arg);
		
		return cd.getRED().size;
	}

	@Override
	public Integer visitFieldDecl(FieldDecl fd, Integer arg) {
		// arg recieved is the current size of the class
		// used to find the displacement for the value on the heap
		
		// regardless of the type of the field, the field size should be 1
		// since it is either a base value or an int address 
		// both have size of 1 word
		int fieldsize = fd.type.visit(this, arg);
		
		if(fd.isStatic) {
			// static fields should be added to the static segment of the 
			// stack rather than being instantiated as part of a class
			fd.setRED(new Field(fieldsize, staticSeg));
			staticSeg += fieldsize;
			
			fieldsize = 0;
			
			// exception is _PrintStream out static field of System
			// it has size 0 and can have its representation reside within static memory
		}
		else {
			fd.setRED(new Field(fieldsize, offset));
		}
		
		return fieldsize;
	}
	
	private int nArgs;
	@Override
	public Integer visitMethodDecl(MethodDecl md, Integer arg) {
		int codeStartAddr = Machine.nextInstrAddr();
		// frames always have
		// 0. OB value
		// 1. DL (caller's LB)
		// 2. RA (return address)
		int frame = Machine.linkDataSize;
		nArgs = md.parameterDeclList.size();
		
		if(currentcd.name.equals(startClass) && md.name.equals("main")) main_code_addr = codeStartAddr;
		
		for(int i = 0; i < nArgs; i++) {
			offset = -(i + 1);
			md.parameterDeclList.get(i).visit(this, frame);
		}
		
		// special instructions for predefined println method
		if(currentcd.name.equals("_PrintStream") && md.name.equals("println")) {
			Machine.emit(Op.LOAD, Reg.LB, -1);
			Machine.emit(Prim.putintnl);
			Machine.emit(Op.RETURN, 0, 0, 1);
		}
		else {
			// for normal methods just visit the instructions directly
			for(Statement s : md.statementList) {
				if(s instanceof VarDeclStmt) {
					frame += s.visit(this, frame);
				}
				else {
					s.visit(this, frame);
				}
			}
			
			// if the method's return type is void, add an extra final return statement
			// if the last statement of the method body is not already a return statement
			// might still be redundant, but just in case
			// its ok if this is extra, because only on return statement is called per method
			// invocation, no danger of popping too many values from the stack
			if(md.type.typeKind == TypeKind.VOID 
					&& !(md.statementList.get(md.statementList.size()-1) instanceof ReturnStmt)) {
				Machine.emit(Op.RETURN, 0, 0, nArgs);
			}
		}
		
		if(md.getRED() != null) {
			((UnknownRoutine)md.getRED()).patchUnknownCalls(codeStartAddr);
		}
		
		md.setRED(new KnownRoutine(frame, codeStartAddr));
		
		return null;
	}

	@Override
	public Integer visitParameterDecl(ParameterDecl pd, Integer arg) {
		int paramsize = pd.type.visit(this, arg);
		
		if(pd.type.typeKind == TypeKind.CLASS || pd.type.typeKind == TypeKind.ARRAY) {
			pd.setRED(new UnknownAddress(paramsize, offset));
		}
		else {
			pd.setRED(new UnknownValue(paramsize, offset));
		}
		
		return paramsize;
	}

	@Override
	public Integer visitVarDecl(VarDecl decl, Integer arg) {
		int varsize = decl.type.visit(this, arg);
		
		if(decl.type.typeKind == TypeKind.CLASS || decl.type.typeKind == TypeKind.ARRAY) {
			decl.setRED(new UnknownAddress(varsize, offset));
		}
		else {
			decl.setRED(new UnknownValue(varsize, arg));
		}
		
		return varsize;
	}

	@Override
	public Integer visitBaseType(BaseType type, Integer arg) {
		return 1;
	}

	@Override
	public Integer visitClassType(ClassType type, Integer arg) {
		return 1; // address on the heap
	}

	@Override
	public Integer visitArrayType(ArrayType type, Integer arg) {
		return 1; // address on the heap
	}

	@Override
	public Integer visitBlockStmt(BlockStmt stmt, Integer arg) {
		int block = 0;
		
		for(Statement s : stmt.sl) {
			if(s instanceof VarDeclStmt) {
				 block += s.visit(this, arg + block);
			}
			else {
				s.visit(this, arg + block);
			}
		}
		
		// values declared within the block statement need to fall
		// out of scope after leaving the block
		// pop any var decls from the stack to clean up the frame
		Machine.emit(Op.POP, block);
		
		return null;
	}

	@Override
	public Integer visitVardeclStmt(VarDeclStmt stmt, Integer arg) {
		int varsize = stmt.varDecl.visit(this, arg);
		
		// evaluate the initializing expression 
		// result should be left on top of the stack
		// at the same location specified by the var RED
		stmt.initExp.visit(this, arg);
		
		/*
		// store the result of the expression inside of the variable
		RuntimeEntityDescriptor red = stmt.varDecl.getRED();
		if(red instanceof UnknownValue) {
			Machine.emit(Op.STORE, Reg.LB, ((UnknownValue)red).offset);
		}
		else {
			Machine.emit(Op.STORE, Reg.LB, ((UnknownAddress)red).offset);
		}*/
		
		return varsize;
	}
	
	private void refVisitHelper(Reference ref, Integer arg) {
		implicitthis = true;
		ref.visit(this, arg);
	}
	
	private void getRefVal(Declaration dec) {
		RuntimeEntityDescriptor red = dec.getRED();
		if(red instanceof Field) {
			if(((FieldDecl)dec).isStatic) {
				//Machine.emit(Op.LOADI);
			}
			else {
				Machine.emit(Prim.fieldref);
			}
		}
		else if(!(red instanceof KnownValue)) {
			Machine.emit(Op.LOADI);
		}
	}
	
	@Override
	public Integer visitAssignStmt(AssignStmt stmt, Integer arg) {
		if(stmt.ref.getDecl() instanceof FieldDecl) { // field
			refVisitHelper(stmt.ref, arg);
			
			stmt.val.visit(this, arg);
			
			Machine.emit(Prim.fieldupd);
		}
		else { // local var or param
			// eval the value expr
			// result at stack top
			stmt.val.visit(this, arg);
			
			refVisitHelper(stmt.ref, arg);
	
			// now address of var being assigned to is at stack top with
			// the value right below it	
			Machine.emit(Op.STOREI);
		}
		return null;
	}
	
	@Override
	public Integer visitIxAssignStmt(IxAssignStmt stmt, Integer arg) {
		refVisitHelper(stmt.ref, arg);
		
		getRefVal(stmt.ref.getDecl());
		
		stmt.ix.visit(this, arg);
		
		stmt.exp.visit(this, arg);
		
		Machine.emit(Prim.arrayupd);
		return null;
	}

	@Override
	public Integer visitCallStmt(CallStmt stmt, Integer arg) {
		// args are pushed onto the stack in reverse order
		for(int i = stmt.argList.size()-1; i > -1; i--) {
			stmt.argList.get(i).visit(this, arg);
		}		
		
		// this method does not actually issue the call opcode
		// done so indirectly by visiting method ref
		refVisitHelper(stmt.methodRef, arg);
		
		// return value of the call, if any, should be disregarded
		
		return null;
	}

	@Override
	public Integer visitReturnStmt(ReturnStmt stmt, Integer arg) {
		if(stmt.returnExpr != null) {
			stmt.returnExpr.visit(this, arg);
			Machine.emit(Op.RETURN, 1, 0, nArgs);
		}
		else {
			Machine.emit(Op.RETURN, 0, 0, nArgs);
		}
		
		return null;
	}

	@Override
	public Integer visitIfStmt(IfStmt stmt, Integer arg) {
		int condjump, elsejump, then, endstmt;
		stmt.cond.visit(this, arg);
		
		condjump = Machine.nextInstrAddr();
		Machine.emit(Op.JUMPIF, Machine.trueRep, Reg.CB, -1);
		
		elsejump = -1;
		if(stmt.elseStmt != null) {
			stmt.elseStmt.visit(this, arg);
			elsejump = Machine.nextInstrAddr();
			Machine.emit(Op.JUMP, Reg.CB, -1);
		}
		
		then = Machine.nextInstrAddr();
		stmt.thenStmt.visit(this, arg);
		
		endstmt = Machine.nextInstrAddr();
		Machine.patch(condjump, then);
		if(elsejump != -1) Machine.patch(elsejump, endstmt);
		
		return null;
	}

	@Override
	public Integer visitWhileStmt(WhileStmt stmt, Integer arg) {
		int start, body, cond;
		start = Machine.nextInstrAddr();
		Machine.emit(Op.JUMP, Reg.CB, -1);
		
		body = Machine.nextInstrAddr();
		stmt.body.visit(this, arg);
		
		cond = Machine.nextInstrAddr();
		Machine.patch(start, cond);
		
		stmt.cond.visit(this, arg);
		Machine.emit(Op.JUMPIF, Machine.trueRep, Reg.CB, body);
		
		return null;
	}

	@Override
	public Integer visitUnaryExpr(UnaryExpr expr, Integer arg) {
		// doesn't delegate to visitOp, just call the primitive directly
		expr.expr.visit(this, arg);
		
		if(expr.operator.spelling.equals("-")) {
			Machine.emit(Prim.neg);
		}
		else { // only other possible unary op is "!"
			Machine.emit(Prim.not);
		}
		return null;
	}

	@Override
	public Integer visitBinaryExpr(BinaryExpr expr, Integer arg) {		
		expr.left.visit(this, arg);
		expr.right.visit(this, arg);
		
//		switch(expr.operator.spelling) {
//		// equals and not equals operators require an extra size argument
//		case "==":
//		case "!=":
//			Machine.emit(Op.LOADL, 1);
//		}
		
		expr.operator.visit(this, arg);
		
		return null;
	}

	@Override
	public Integer visitRefExpr(RefExpr expr, Integer arg) {
		// ref visit should leave the correct address for the data
		// at stack top
		refVisitHelper(expr.ref, arg);
		
		getRefVal(expr.ref.getDecl());
		
		return null;
	}

	@Override
	public Integer visitIxExpr(IxExpr expr, Integer arg) {
		refVisitHelper(expr.ref, arg);
		
		getRefVal(expr.ref.getDecl());
		
		expr.ixExpr.visit(this, arg);
		
		Machine.emit(Prim.arrayref);
		return null;
	}

	@Override
	public Integer visitCallExpr(CallExpr expr, Integer arg) {
		// args are pushed onto the stack in reverse order
		for(int i = expr.argList.size()-1; i > -1; i--) {
			expr.argList.get(i).visit(this, arg);
		}
		
		// this method does not actually issue the call opcode
		// instead issues it indirectly by visiting method ref
		refVisitHelper(expr.methodRef, arg);
		
		return null;
	}

	@Override
	public Integer visitLiteralExpr(LiteralExpr expr, Integer arg) {
		expr.lit.visit(this, arg);
		return null;
	}

	@Override
	public Integer visitNewObjectExpr(NewObjectExpr expr, Integer arg) {
		Machine.emit(Op.LOADL, -1); // parent object addr, no inheritance so is unused
		
		Machine.emit(Op.LOADL, expr.classtype.className.getDecl().getRED().size);
		
		Machine.emit(Prim.newobj);
		return null;
	}

	@Override
	public Integer visitNewArrayExpr(NewArrayExpr expr, Integer arg) {
		// all miniJava values are size 1, so this is enough
		// might have to multiply by object size otherwise
		expr.sizeExpr.visit(this, arg);
		
		Machine.emit(Prim.newarr);
		return null;
	}

	@Override
	public Integer visitThisRef(ThisRef ref, Integer arg) {
		// this ref appears within non-static methods, so the OB should be set to the
		// correct location in the heap by the surrounding method call already
		Machine.emit(Op.LOADA, Reg.OB, 0);
		return null;
	}

	@Override
	public Integer visitIdRef(IdRef ref, Integer arg) {
		ref.id.visit(this, arg);
		return null;
	}

	@Override
	public Integer visitQRef(QualRef ref, Integer arg) {
		refVisitHelper(ref.ref, arg);
		
		getRefVal(ref.ref.getDecl());
		
		ref.id.visit(this, arg);
		return null;
	}
	
	// if the identifier being visited is a method, then this method issues a call op
	// and the result of the call is left at the top of the stack
	// otherwise, the address that corresponds to the identifier in question is left at
	// stack top
	@Override
	public Integer visitIdentifier(Identifier id, Integer arg) {
		Declaration dec = id.getDecl();
		RuntimeEntityDescriptor red = dec.getRED();
		
		Reg relTo;
		int disp;
		
		if(red instanceof Field) {
			disp = ((Field)red).offset;
			
			if(((FieldDecl)dec).isStatic) {
				relTo = Reg.SB;
				
				Machine.emit(Op.LOADA, relTo, disp);
			}
			else {
				if(implicitthis) {
					Machine.emit(Op.LOADA, Reg.OB, 0);
				}
				// if a nonstatic field is being visited, then its corresponding instance addr
				// in the heap should already be loaded
				Machine.emit(Op.LOADL, disp);
			}
			
		}
		else if(red instanceof UnknownValue) {
			relTo = Reg.LB;
			disp = ((UnknownValue)red).offset;
			
			Machine.emit(Op.LOADA, relTo, disp);
		}
		else if(red instanceof UnknownAddress) {
			relTo = Reg.LB;
			disp = ((UnknownAddress)red).offset;
			
			// address of heap address is pushed onto the stack
			Machine.emit(Op.LOADA, relTo, disp);
		}
		else if(red instanceof KnownRoutine) {
			// assumption is that if an id representing a method is being visited, 
			// then that method is being called
			relTo = Reg.CB;
			disp = ((KnownRoutine)red).codeaddr;
			
			if(!((MethodDecl)dec).isStatic) { // non-static method call
				Machine.emit(Op.CALLI, relTo, disp);
			}
			else { // static method call
				Machine.emit(Op.CALL, relTo, disp);
			}
			
		}// code for called method not yet generated
		else if(red == null && dec instanceof MethodDecl) { 
			relTo = Reg.CB;
			disp = -1;
			int patchaddr = Machine.nextInstrAddr();
			
			if(!((MethodDecl)dec).isStatic) { // non-static method call
				Machine.emit(Op.CALLI, relTo, disp);
			}
			else { // static method call
				Machine.emit(Op.CALL, relTo, disp);
			}
			
			UnknownRoutine mred = new UnknownRoutine(-1);
			
			mred.patchCalls.push(patchaddr);
			
			dec.setRED(mred);
		}
		else if(red instanceof UnknownRoutine) {
			relTo = Reg.CB;
			disp = -1;
			int patchaddr = Machine.nextInstrAddr();
			
			if(!((MethodDecl)dec).isStatic) { // non-static method call
				Machine.emit(Op.CALLI, relTo, disp);
			}
			else { // static method call
				Machine.emit(Op.CALL, relTo, disp);
			}
			
			((UnknownRoutine)red).patchCalls.push(patchaddr);
		}
		//else if(red instanceof KnownValue) {}
			// this RED is only being used for classes atm
			// just do nothing
			// next access is a static address
		//else if(red instanceof KnownAddress) {}
		
		// immediately set to false after the first visit to an identifier of any sort
		implicitthis = false;
		
		return null;
	}

	@Override
	public Integer visitOperator(Operator op, Integer arg) {
		switch(op.spelling) {
		case "||":
			Machine.emit(Prim.or);
			break;
		case "&&":
			Machine.emit(Prim.and);
			break;
		case "==":
			Machine.emit(Prim.eq);
			break;
		case "!=":
			Machine.emit(Prim.ne);
			break;
		case "<":
			Machine.emit(Prim.lt);
			break;
		case "<=":
			Machine.emit(Prim.le);
			break;
		case ">=":
			Machine.emit(Prim.ge);
			break;
		case ">":
			Machine.emit(Prim.gt);
			break;
		case "-":
			Machine.emit(Prim.sub);
			break;
		case "+":
			Machine.emit(Prim.add);
			break;
		case "*":
			Machine.emit(Prim.mult);
			break;
		case "/":
			Machine.emit(Prim.div);
			break;
		}
		
		return null;
	}
	
	@Override
	public Integer visitIntLiteral(IntLiteral num, Integer arg) {
		Machine.emit(Op.LOADL, Integer.parseInt(num.spelling));
		
		return null;
	}

	@Override
	public Integer visitBooleanLiteral(BooleanLiteral bool, Integer arg) {
		// 0 for false
		// 1 for true
		
		if(bool.spelling.equals("true")) {
			Machine.emit(Op.LOADL, Machine.trueRep);
		}
		else {
			Machine.emit(Op.LOADL, Machine.falseRep);
		}
		
		return null;
	}

	@Override
	public Integer visitNullLiteral(NullLiteral nul, Integer arg) {
		Machine.emit(Op.LOADL, Machine.nullRep);
		return null;
	}
	
}
