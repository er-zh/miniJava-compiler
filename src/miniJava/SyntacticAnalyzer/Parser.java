package miniJava.SyntacticAnalyzer;

import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Parser {
	private boolean trace = false;

	private Scanner scanner;
	private Token currentToken;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}

	public Package parse() {
		ClassDeclList classes = null;

		// load in the first token
		do {
			currentToken = scanner.getNextToken();
		} while (currentToken.getType() == TokenType.COMMENT);

		try {
			classes = parseProgram();

			return new Package(classes);
		} catch (SyntaxError e) {
			e.printErrorMsg();

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
				type = new BaseType(TypeKind.VOID);

				memberName = currentToken.getLexeme();
				accept(TokenType.ID);

				methods.add(parseMethodDecl(new FieldDecl(isPrivate, isStatic, type, memberName)));
			} else {
				type = parseType();

				memberName = currentToken.getLexeme();
				accept(TokenType.ID);

				if (currentToken.getType() == TokenType.SEMICOLON) {
					parseFieldDecl();
					fields.add(new FieldDecl(isPrivate, isStatic, type, memberName));
				} else {
					methods.add(parseMethodDecl(new FieldDecl(isPrivate, isStatic, type, memberName)));
				}
			}
		}

		accept(TokenType.RBRACE);

		return new ClassDecl(className, fields, methods);
	}

	void parseFieldDecl() {
		accept(TokenType.SEMICOLON);
	}

	MethodDecl parseMethodDecl(MemberDecl md) {
		accept(TokenType.LPAREN);

		ParameterDeclList params = null;
		StatementList body = null;
		if (currentToken.getType() != TokenType.RPAREN) {
			params = parseParamList();
		}

		accept(TokenType.RPAREN);
		accept(TokenType.LBRACE);

		body = new StatementList();
		while (currentToken.getType() != TokenType.RBRACE) {
			body.add(parseStatement());
		}

		accept(TokenType.RBRACE);

		return new MethodDecl(md, params, body);
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
			type = new BaseType(TypeKind.BOOLEAN);
			advance();
		} else {
			if (currentToken.getType() == TokenType.ID) {
				type = new ClassType(new Identifier(currentToken));
				advance();
			} else if (currentToken.getType() == TokenType.INT) {
				type = new BaseType(TypeKind.INT);
				advance();
			} else {
				throw new SyntaxError(
						"invalid typing: expected non-void typing, " + "but got " + currentToken.getType());
			}

			if (currentToken.getType() == TokenType.LSQUARE) {
				advance();
				accept(TokenType.RSQUARE);
				type = new ArrayType(type);
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

		paramList.add(new ParameterDecl(paramType, paramName));

		while (currentToken.getType() == TokenType.COMMA) {
			advance();

			paramType = parseType();
			paramName = currentToken.getLexeme();
			accept(TokenType.ID);

			paramList.add(new ParameterDecl(paramType, paramName));
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
			ref = (refType == TokenType.THIS) ? new ThisRef() : new IdRef(new Identifier(currentToken));
			advance();

			while (currentToken.getType() == TokenType.PERIOD) {
				advance();

				ref = new QualRef(ref, new Identifier(currentToken));
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

			statemt = new BlockStmt(statements);
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

			statemt = new IfStmt(ex, then, els);
			break;
		case WHILE:
			advance();

			Statement body;

			accept(TokenType.LPAREN);
			ex = parseExpr(); // loop condition
			accept(TokenType.RPAREN);

			body = parseStatement();

			statemt = new WhileStmt(ex, body);
			break;
		case RETURN:
			advance();

			ex = null; // the expression whose value is being returned

			if (currentToken.getType() != TokenType.SEMICOLON) {
				ex = parseExpr();
			}
			accept(TokenType.SEMICOLON);

			statemt = new ReturnStmt(ex);
			break;
		case ID:
			// need to decide between statements of the form:
			// id(type) id = expr; and ref (some kind of expr);
			// which requires left factoring of possible id
			Identifier startingId = new Identifier(currentToken);

			advance();

			switch (currentToken.getType()) {
			case ID:
				// 2 id's indicates
				// variable declaration of id with type id
				String varName = currentToken.getLexeme(); // name of var being declared

				advance();
				ex = parseStatementAssign(); // expr whose value var id is initialized with

				statemt = new VarDeclStmt(new VarDecl(new ClassType(startingId), varName), ex);
				break;
			case PERIOD:
				Reference ref = new IdRef(startingId);

				while (currentToken.getType() == TokenType.PERIOD) {
					advance();

					ref = new QualRef(ref, new Identifier(currentToken));
					accept(TokenType.ID);
				}

				statemt = parseStatementRef(ref);
				break;
			case ASSIGNMENT:
				// if the input is of the form
				// id =
				// then the id is a ref
				ref = new IdRef(startingId);
				ex = parseStatementAssign(); // expr value assigned to var id

				statemt = new AssignStmt(ref, ex);
				break;
			case LPAREN:
				advance();

				ref = new IdRef(startingId);
				ExprList methodArgs = null;
				if (currentToken.getType() != TokenType.RPAREN) {
					methodArgs = parseArgList();
				}

				accept(TokenType.RPAREN);
				accept(TokenType.SEMICOLON);

				statemt = new CallStmt(ref, methodArgs);
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

					statemt = new VarDeclStmt(new VarDecl(new ArrayType(new ClassType(startingId)), varName), ex);
				}
				else { // indexed assign
					ref = new IdRef(startingId);
					
					Expression indexExpr = parseExpr();
					
					accept(TokenType.RSQUARE);
					ex = parseStatementAssign(); // value assigned to ref[indexExpr]
					
					statemt = new IxAssignStmt(ref, indexExpr, ex);
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

			statemt = new VarDeclStmt(new VarDecl(type, varName), ex);
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

			statemt = new AssignStmt(ref, ex);
			break;
		case LSQUARE:
			advance();

			Expression indexExpr = parseExpr();
			accept(TokenType.RSQUARE);
			accept(TokenType.ASSIGNMENT);
			ex = parseExpr(); // assigned to ref[index]

			statemt = new IxAssignStmt(ref, indexExpr, ex);
			break;
		case LPAREN:
			advance();

			ExprList argList = null;
			if (currentToken.getType() != TokenType.RPAREN) {
				argList = parseArgList();
			}

			accept(TokenType.RPAREN);

			statemt = new CallStmt(ref, argList);
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
			Operator or = new Operator(currentToken);
			advance();

			leftExpr = new BinaryExpr(or, leftExpr, parseConj());
		}

		return leftExpr;
	}

	Expression parseConj() {
		Expression leftExpr = parseEq();

		while (currentToken.getType() == TokenType.BINOP && currentToken.getLexeme().equals("&&")) {
			Operator and = new Operator(currentToken);
			advance();
			leftExpr = new BinaryExpr(and, leftExpr, parseEq());
		}

		return leftExpr;
	}

	Expression parseEq() {
		Expression leftExpr = parseRel();

		while (currentToken.getType() == TokenType.BINOP
				&& (currentToken.getLexeme().equals("==") || currentToken.getLexeme().equals("!="))) {
			Operator equality = new Operator(currentToken);
			advance();
			leftExpr = new BinaryExpr(equality, leftExpr, parseRel());
		}

		return leftExpr;
	}

	Expression parseRel() {
		Expression leftExpr = parseAdd();

		while (currentToken.getType() == TokenType.BINOP
				&& (currentToken.getLexeme().equals("<") || currentToken.getLexeme().equals(">")
						|| currentToken.getLexeme().equals(">=") || currentToken.getLexeme().equals("<="))) {
			Operator relation = new Operator(currentToken);
			advance();
			leftExpr = new BinaryExpr(relation, leftExpr, parseAdd());
		}

		return leftExpr;
	}

	Expression parseAdd() {
		Expression leftExpr = parseMult();

		while (currentToken.getType() == TokenType.BINOP && currentToken.getLexeme().equals("+")
				|| currentToken.getType() == TokenType.UNOP && currentToken.getLexeme().equals("-")) {
			if (currentToken.getLexeme().equals("-"))
				currentToken.convUnop2Binop();

			Operator add = new Operator(currentToken);
			advance();
			leftExpr = new BinaryExpr(add, leftExpr, parseMult());
		}

		return leftExpr;
	}

	Expression parseMult() {
		Expression leftExpr = parseUnary();

		while (currentToken.getType() == TokenType.BINOP
				&& (currentToken.getLexeme().equals("*") || currentToken.getLexeme().equals("/"))) {
			Operator times = new Operator(currentToken);

			advance();
			leftExpr = new BinaryExpr(times, leftExpr, parseUnary());
		}

		return leftExpr;
	}

	Expression parseUnary() {
		if (currentToken.getType() == TokenType.UNOP) {
			Operator negate = new Operator(currentToken);
			advance();
			return new UnaryExpr(negate, parseUnary());
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

				expr = new IxExpr(ref, indexExpr);
			} else if (currentToken.getType() == TokenType.LPAREN) {
				advance();

				ExprList argsList = null; // TODO make sure empty inputs consistent
				if (currentToken.getType() != TokenType.RPAREN) {
					argsList = parseArgList();
				}
				accept(TokenType.RPAREN);

				expr = new CallExpr(ref, argsList);
			} else {
				expr = new RefExpr(ref);
			}
			break;
		case LPAREN:
			advance();
			expr = parseExpr();
			accept(TokenType.RPAREN);
			break;
		case NUM_LITERAL:
			expr = new LiteralExpr(new IntLiteral(currentToken));
			advance();
			break;
		case T:
		case F:
			expr = new LiteralExpr(new BooleanLiteral(currentToken));
			advance();
			break;
		case NEW:
			advance();

			if (currentToken.getType() == TokenType.ID) {
				ClassType newclass = new ClassType(new Identifier(currentToken));
				advance();

				if (currentToken.getType() == TokenType.LPAREN) {
					advance();
					accept(TokenType.RPAREN);

					expr = new NewObjectExpr(newclass);
				} else {
					accept(TokenType.LSQUARE);
					Expression sizeExpr = parseExpr();
					accept(TokenType.RSQUARE);

					expr = new NewArrayExpr(new ArrayType(newclass), sizeExpr);
				}
			} else if (currentToken.getType() == TokenType.INT) {
				advance();
				accept(TokenType.LSQUARE);
				Expression sizeExpr = parseExpr();
				accept(TokenType.RSQUARE);

				expr = new NewArrayExpr(new ArrayType(new BaseType(TypeKind.INT)), sizeExpr);
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
