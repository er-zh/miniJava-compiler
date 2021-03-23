package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;
import miniJava.SourcePosition;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Parser {
	private boolean trace = false;

	private Scanner scanner;
	private Token currentToken;
	
	private ErrorReporter err;

	public Parser(Scanner scanner, ErrorReporter e) {
		this.scanner = scanner;
		this.err = e;
	}
	
	/*should only be used for testing*/
	public Parser(Scanner scanner) {
		this.scanner = scanner;
		this.err = null;
	}

	public Package parse() {
		// load in the first token
		do {
			currentToken = scanner.getNextToken();
		} while (currentToken.getType() == TokenType.COMMENT);

		try {
			ClassDeclList classes = parseProgram();

			return new Package(classes, new SourcePosition(scanner.getLineNum()));
		} catch (SyntaxError e) {
			if (err != null) err.reportError(e);

			return null;
		}

	}

	ClassDeclList parseProgram() {
		ClassDeclList classes = new ClassDeclList();

		while (currentToken.getType() == TokenType.CLASS) {
			classes.add(parseClassDecl());
		}

		accept(TokenType.EOT);
		return classes;
	}

	ClassDecl parseClassDecl() {
		String className;
		FieldDeclList fields = new FieldDeclList();
		MethodDeclList methods = new MethodDeclList();

		accept(TokenType.CLASS);

		className = currentToken.getLexeme();
		accept(TokenType.ID);

		accept(TokenType.LBRACE);

		while (currentToken.getType() != TokenType.RBRACE) {
			boolean isPrivate = parseVis();
			boolean isStatic = parseAccess();
			String memberName;
			TypeDenoter type;

			if (currentToken.getType() == TokenType.VOID) {
				accept(TokenType.VOID);
				type = new BaseType(TypeKind.VOID, new SourcePosition(scanner.getLineNum()));

				memberName = currentToken.getLexeme();
				accept(TokenType.ID);

				methods.add(parseMethodDecl(new FieldDecl(isPrivate, isStatic, type, memberName, new SourcePosition(scanner.getLineNum()))));
			} else {
				type = parseType();

				memberName = currentToken.getLexeme();
				accept(TokenType.ID);

				if (currentToken.getType() == TokenType.SEMICOLON) {
					parseFieldDecl();
					fields.add(new FieldDecl(isPrivate, isStatic, type, memberName, new SourcePosition(scanner.getLineNum())));
				} else {
					methods.add(parseMethodDecl(new FieldDecl(isPrivate, isStatic, type, memberName, new SourcePosition(scanner.getLineNum()))));
				}
			}
		}

		accept(TokenType.RBRACE);

		return new ClassDecl(className, fields, methods, new SourcePosition(scanner.getLineNum()));
	}

	void parseFieldDecl() {
		accept(TokenType.SEMICOLON);
	}

	MethodDecl parseMethodDecl(MemberDecl md) {
		accept(TokenType.LPAREN);

		ParameterDeclList params = new ParameterDeclList();
		StatementList body = new StatementList();
		
		if (currentToken.getType() != TokenType.RPAREN) {
			params = parseParamList();
		}

		accept(TokenType.RPAREN);
		accept(TokenType.LBRACE);

		while (currentToken.getType() != TokenType.RBRACE) {
			body.add(parseStatement());
		}

		accept(TokenType.RBRACE);

		return new MethodDecl(md, params, body, new SourcePosition(scanner.getLineNum()));
	}

	// can be empty
	// TODO resolve ambiguity around public / package private false state
	boolean parseVis() {
		if (currentToken.getType() == TokenType.PRIVATE) {
			advance();
			return true;
		} else if (currentToken.getType() == TokenType.PUBLIC) {
			advance();
		}
		return false;
	}

	// can be empty
	boolean parseAccess() {
		if (currentToken.getType() == TokenType.STATIC) {
			advance();
			return true;
		}
		return false;
	}

	TypeDenoter parseType() {
		TypeDenoter type = null;

		if (currentToken.getType() == TokenType.BOOLEAN) {
			type = new BaseType(TypeKind.BOOLEAN, new SourcePosition(scanner.getLineNum()));
			advance();
		} else {
			if (currentToken.getType() == TokenType.ID) {
				type = new ClassType(new Identifier(currentToken, new SourcePosition(scanner.getLineNum())), new SourcePosition(scanner.getLineNum()));
				advance();
			} else if (currentToken.getType() == TokenType.INT) {
				type = new BaseType(TypeKind.INT, new SourcePosition(scanner.getLineNum()));
				advance();
			} else {
				throw new SyntaxError("invalid typing: expected non-void typing, " 
						+ "but got " + currentToken.getType());
			}

			if (currentToken.getType() == TokenType.LSQUARE) {
				advance();
				accept(TokenType.RSQUARE);
				type = new ArrayType(type, new SourcePosition(scanner.getLineNum()));
			}
		}

		return type;
	}

	// can be empty
	ParameterDeclList parseParamList() {
		ParameterDeclList paramList = new ParameterDeclList();

		TypeDenoter paramType = parseType();
		String paramName = currentToken.getLexeme();
		accept(TokenType.ID);

		paramList.add(new ParameterDecl(paramType, paramName, new SourcePosition(scanner.getLineNum())));

		while (currentToken.getType() == TokenType.COMMA) {
			advance();

			paramType = parseType();
			paramName = currentToken.getLexeme();
			accept(TokenType.ID);

			paramList.add(new ParameterDecl(paramType, paramName, new SourcePosition(scanner.getLineNum())));
		}

		return paramList;
	}

	// can be empty
	// however if the method is called it is expected that 
	// there is at least one argument provided
	ExprList parseArgList() {
		ExprList args = new ExprList();

		args.add(parseExpr());

		while (currentToken.getType() == TokenType.COMMA) {
			advance();
			args.add(parseExpr());
		}

		return args;
	}

	Reference parseRef() {
		Reference ref = null;
		TokenType refType = currentToken.getType();

		if (refType == TokenType.ID || refType == TokenType.THIS) {
			ref = (refType == TokenType.THIS) ? new ThisRef(new SourcePosition(scanner.getLineNum())) 
					: new IdRef(new Identifier(currentToken, new SourcePosition(scanner.getLineNum())),
							new SourcePosition(scanner.getLineNum()));
			advance();

			while (currentToken.getType() == TokenType.PERIOD) {
				advance();

				ref = new QualRef(ref, new Identifier(currentToken, new SourcePosition(scanner.getLineNum())),
						new SourcePosition(scanner.getLineNum()));
				accept(TokenType.ID);
			}
		} else {
			// TODO add possible syntax error for missing name/ref?
		}

		return ref;
	}

	Statement parseStatement() {
		Statement statemt = null;
		switch (currentToken.getType()) {
		case LBRACE:
			advance();

			StatementList statements = new StatementList();

			while (currentToken.getType() != TokenType.RBRACE) {
				statements.add(parseStatement());
			}
			accept(TokenType.RBRACE);

			statemt = new BlockStmt(statements, new SourcePosition(scanner.getLineNum()));
			break;
		case IF:
			advance();

			Expression ex = null; // conditional expr
			Statement then;
			Statement els = null;

			accept(TokenType.LPAREN);
			ex = parseExpr();
			accept(TokenType.RPAREN);

			then = parseStatement();

			if (currentToken.getType() == TokenType.ELSE) {
				advance();
				els = parseStatement();
			}

			statemt = new IfStmt(ex, then, els, new SourcePosition(scanner.getLineNum()));
			break;
		case WHILE:
			advance();

			Statement body;

			accept(TokenType.LPAREN);
			ex = parseExpr(); // loop condition
			accept(TokenType.RPAREN);

			body = parseStatement();

			statemt = new WhileStmt(ex, body, new SourcePosition(scanner.getLineNum()));
			break;
		case RETURN:
			advance();

			ex = null; // the expression whose value is being returned

			if (currentToken.getType() != TokenType.SEMICOLON) {
				ex = parseExpr();
			}
			accept(TokenType.SEMICOLON);

			statemt = new ReturnStmt(ex, new SourcePosition(scanner.getLineNum()));
			break;
		case ID:
			// need to decide between statements of the form:
			// id(type) id = expr; and ref (some kind of expr);
			// which requires left factoring of possible id
			Identifier startingId = new Identifier(currentToken, new SourcePosition(scanner.getLineNum()));

			advance();

			switch (currentToken.getType()) {
			case ID:
				// 2 id's indicates
				// variable declaration of id with type id
				String varName = currentToken.getLexeme(); // name of var being declared

				advance();
				ex = parseStatementAssign(); // expr whose value var id is initialized with

				statemt = new VarDeclStmt(new VarDecl(new ClassType(startingId, new SourcePosition(scanner.getLineNum())), varName, new SourcePosition(scanner.getLineNum())), 
						ex, new SourcePosition(scanner.getLineNum()));
				break;
			case PERIOD:
				Reference ref = new IdRef(startingId, new SourcePosition(scanner.getLineNum()));

				while (currentToken.getType() == TokenType.PERIOD) {
					advance();

					ref = new QualRef(ref, new Identifier(currentToken, new SourcePosition(scanner.getLineNum())), new SourcePosition(scanner.getLineNum()));
					accept(TokenType.ID);
				}

				statemt = parseStatementRef(ref);
				break;
			case ASSIGNMENT:
				// if the input is of the form
				// id =
				// then the id is a ref
				ref = new IdRef(startingId, new SourcePosition(scanner.getLineNum()));
				ex = parseStatementAssign(); // expr value assigned to var id

				statemt = new AssignStmt(ref, ex, new SourcePosition(scanner.getLineNum()));
				break;
			case LPAREN:
				advance();

				ref = new IdRef(startingId, new SourcePosition(scanner.getLineNum()));
				ExprList methodArgs = new ExprList();
				if (currentToken.getType() != TokenType.RPAREN) {
					methodArgs = parseArgList();
				}

				accept(TokenType.RPAREN);
				accept(TokenType.SEMICOLON);

				statemt = new CallStmt(ref, methodArgs, new SourcePosition(scanner.getLineNum()));
				break;
			case LSQUARE:
				// still need to decide between an array variable decl
				// or an indexed assignment
				advance();

				if (currentToken.getType() == TokenType.RSQUARE) { // array var decl
					advance();

					varName = currentToken.getLexeme(); // name of the array
					accept(TokenType.ID);

					ex = parseStatementAssign(); // val array is initialized with

					statemt = new VarDeclStmt(new VarDecl(new ArrayType(new ClassType(startingId, new SourcePosition(scanner.getLineNum())), 
							new SourcePosition(scanner.getLineNum())), 
							varName, new SourcePosition(scanner.getLineNum())), 
							ex, new SourcePosition(scanner.getLineNum()));
				}
				else { // indexed assign
					ref = new IdRef(startingId, new SourcePosition(scanner.getLineNum()));
					
					Expression indexExpr = parseExpr();
					
					accept(TokenType.RSQUARE);
					ex = parseStatementAssign(); // value assigned to ref[indexExpr]
					
					statemt = new IxAssignStmt(ref, indexExpr, ex, new SourcePosition(scanner.getLineNum()));
				}
				break;
			default:
				throw new SyntaxError("failed to parse statement");
			}
			break;
		case THIS:
			Reference thisref = parseRef();

			statemt = parseStatementRef(thisref);
			break;
		case BOOLEAN:
		case INT:
			TypeDenoter type = parseType();
			String varName = currentToken.getLexeme();

			accept(TokenType.ID);
			ex = parseStatementAssign();

			statemt = new VarDeclStmt(new VarDecl(type, varName, new SourcePosition(scanner.getLineNum())),
					ex, new SourcePosition(scanner.getLineNum()));
			break;
		default:
			throw new SyntaxError("failed to parse statement");
		}

		return statemt;
	}

	private Statement parseStatementRef(Reference ref) {
		Statement statemt = null;

		switch (currentToken.getType()) {
		case ASSIGNMENT:
			advance();

			Expression ex = parseExpr();

			statemt = new AssignStmt(ref, ex, new SourcePosition(scanner.getLineNum()));
			break;
		case LSQUARE:
			advance();

			Expression indexExpr = parseExpr();
			accept(TokenType.RSQUARE);
			accept(TokenType.ASSIGNMENT);
			ex = parseExpr(); // assigned to ref[index]

			statemt = new IxAssignStmt(ref, indexExpr, ex, new SourcePosition(scanner.getLineNum()));
			break;
		case LPAREN:
			advance();

			ExprList argList = new ExprList();
			if (currentToken.getType() != TokenType.RPAREN) {
				argList = parseArgList();
			}

			accept(TokenType.RPAREN);

			statemt = new CallStmt(ref, argList, new SourcePosition(scanner.getLineNum()));
			break;
		default:
			throw new SyntaxError("failed to parse ref statement");
		}
		accept(TokenType.SEMICOLON);

		return statemt;
	}

	private Expression parseStatementAssign() {
		accept(TokenType.ASSIGNMENT);
		Expression expr = parseExpr();
		accept(TokenType.SEMICOLON);
		
		return expr;
	}

	Expression parseExpr() {
		Expression leftExpr = parseConj();

		while (currentToken.getType() == TokenType.BINOP && currentToken.getLexeme().equals("||")) {
			Operator or = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));
			advance();

			leftExpr = new BinaryExpr(or, leftExpr, parseConj(), new SourcePosition(scanner.getLineNum()));
		}

		return leftExpr;
	}

	Expression parseConj() {
		Expression leftExpr = parseEq();

		while (currentToken.getType() == TokenType.BINOP && currentToken.getLexeme().equals("&&")) {
			Operator and = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));
			advance();
			leftExpr = new BinaryExpr(and, leftExpr, parseEq(), new SourcePosition(scanner.getLineNum()));
		}

		return leftExpr;
	}

	Expression parseEq() {
		Expression leftExpr = parseRel();

		while (currentToken.getType() == TokenType.BINOP
				&& (currentToken.getLexeme().equals("==") || currentToken.getLexeme().equals("!="))) {
			Operator equality = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));
			advance();
			leftExpr = new BinaryExpr(equality, leftExpr, parseRel(), new SourcePosition(scanner.getLineNum()));
		}

		return leftExpr;
	}

	Expression parseRel() {
		Expression leftExpr = parseAdd();

		while (currentToken.getType() == TokenType.BINOP
				&& (currentToken.getLexeme().equals("<") || currentToken.getLexeme().equals(">")
						|| currentToken.getLexeme().equals(">=") || currentToken.getLexeme().equals("<="))) {
			Operator relation = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));
			advance();
			leftExpr = new BinaryExpr(relation, leftExpr, parseAdd(), new SourcePosition(scanner.getLineNum()));
		}

		return leftExpr;
	}

	Expression parseAdd() {
		Expression leftExpr = parseMult();

		while (currentToken.getType() == TokenType.BINOP && currentToken.getLexeme().equals("+")
				|| currentToken.getType() == TokenType.UNOP && currentToken.getLexeme().equals("-")) {
			if (currentToken.getLexeme().equals("-"))
				currentToken.convUnop2Binop();

			Operator add = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));
			advance();
			leftExpr = new BinaryExpr(add, leftExpr, parseMult(), new SourcePosition(scanner.getLineNum()));
		}

		return leftExpr;
	}

	Expression parseMult() {
		Expression leftExpr = parseUnary();

		while (currentToken.getType() == TokenType.BINOP
				&& (currentToken.getLexeme().equals("*") || currentToken.getLexeme().equals("/"))) {
			Operator times = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));

			advance();
			leftExpr = new BinaryExpr(times, leftExpr, parseUnary(), new SourcePosition(scanner.getLineNum()));
		}

		return leftExpr;
	}

	Expression parseUnary() {
		if (currentToken.getType() == TokenType.UNOP) {
			Operator negate = new Operator(currentToken, new SourcePosition(scanner.getLineNum()));
			advance();
			return new UnaryExpr(negate, parseUnary(), new SourcePosition(scanner.getLineNum()));
		} else {
			return parseVal();
		}
	}

	Expression parseVal() {
		Expression expr = null;
		
		switch (currentToken.getType()) {
		case ID:
		case THIS:
			Reference ref = parseRef();

			if (currentToken.getType() == TokenType.LSQUARE) {
				advance();
				Expression indexExpr = parseExpr();
				accept(TokenType.RSQUARE);

				expr = new IxExpr(ref, indexExpr, new SourcePosition(scanner.getLineNum()));
			} else if (currentToken.getType() == TokenType.LPAREN) {
				advance();

				ExprList argsList = new ExprList();
				if (currentToken.getType() != TokenType.RPAREN) {
					argsList = parseArgList();
				}
				accept(TokenType.RPAREN);

				expr = new CallExpr(ref, argsList, new SourcePosition(scanner.getLineNum()));
			} else {
				expr = new RefExpr(ref, new SourcePosition(scanner.getLineNum()));
			}
			break;
		case LPAREN:
			advance();
			expr = parseExpr();
			accept(TokenType.RPAREN);
			break;
		case NUM_LITERAL:
			expr = new LiteralExpr(new IntLiteral(currentToken, new SourcePosition(scanner.getLineNum())),
					new SourcePosition(scanner.getLineNum()));
			advance();
			break;
		case NULL:
			expr = new LiteralExpr(new NullLiteral(currentToken, new SourcePosition(scanner.getLineNum())),
					new SourcePosition(scanner.getLineNum()));
			advance();
			break;
		case T:
		case F:
			expr = new LiteralExpr(new BooleanLiteral(currentToken, new SourcePosition(scanner.getLineNum())),
					new SourcePosition(scanner.getLineNum()));
			advance();
			break;
		case NEW:
			advance();

			if (currentToken.getType() == TokenType.ID) {
				ClassType newclass = new ClassType(new Identifier(currentToken, new SourcePosition(scanner.getLineNum())),
						new SourcePosition(scanner.getLineNum()));
				advance();

				if (currentToken.getType() == TokenType.LPAREN) {
					advance();
					accept(TokenType.RPAREN);

					expr = new NewObjectExpr(newclass, new SourcePosition(scanner.getLineNum()));
				} else {
					accept(TokenType.LSQUARE);
					Expression sizeExpr = parseExpr();
					accept(TokenType.RSQUARE);

					expr = new NewArrayExpr(newclass, sizeExpr, new SourcePosition(scanner.getLineNum()));
				}
			} else if (currentToken.getType() == TokenType.INT) {
				advance();
				accept(TokenType.LSQUARE);
				Expression sizeExpr = parseExpr();
				accept(TokenType.RSQUARE);

				expr = new NewArrayExpr(new BaseType(TypeKind.INT, new SourcePosition(scanner.getLineNum())), 
						sizeExpr, new SourcePosition(scanner.getLineNum()));
			} else {
				throw new SyntaxError("invalid use of new in expression");
			}
			break;
		default:
			throw new SyntaxError("invalid expression");
		}

		return expr;
	}

	private void accept(TokenType expectedToken) throws SyntaxError {
		if (currentToken.getType() == expectedToken) {
			advance();
		} else {
			throw new SyntaxError(expectedToken, currentToken.getType());
		}
	}

	private void advance() {
		if (trace)
			pTrace();

		do {
			currentToken = scanner.getNextToken();
		} while (currentToken.getType() == TokenType.COMMENT);

		if (currentToken.getType() == TokenType.ERROR) {
			throw new SyntaxError("Lexical error, " + currentToken.getLexeme());
		}
	}

	private void pTrace() {
		StackTraceElement[] stl = Thread.currentThread().getStackTrace();
		for (int i = stl.length - 1; i > 0; i--) {
			if (stl[i].toString().contains("parse"))
				System.out.println(stl[i]);
		}
		System.out.println("accepting: " + currentToken.getType() + " (\"" + currentToken.getLexeme() + "\")");
		System.out.println();
	}

}
