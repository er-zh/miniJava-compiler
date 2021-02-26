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

	// TODO actually fill out the argument list with the exprs from parseExpr
	// can be empty
	ExprList parseArgList() {
		ExprList args = new ExprList();

		parseExpr();

		while (currentToken.getType() == TokenType.COMMA) {
			advance();
			parseExpr();
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
			parseExpr();
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

			ex = null; //loop condition
			Statement body;

			accept(TokenType.LPAREN);
			parseExpr();
			accept(TokenType.RPAREN);

			body = parseStatement();

			statemt = new WhileStmt(ex, body);
			break;
		case RETURN:
			advance();

			ex = null; // the expression whose value is being returned

			if (currentToken.getType() != TokenType.SEMICOLON) {
				parseExpr(); // TODO put the exprValue in the retExpr
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
				ex = null; // expr whose value var id is initialized with
				
				advance();
				parseStatementAssign(); // TODO put actual exprValue in varValue

				statemt = new VarDeclStmt(new VarDecl(new ClassType(startingId),
						varName), ex);
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
				ex = null; // expr value assigned to var id
				
				parseStatementAssign(); // TODO put actual exprValue in assignVal
				
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

					ex = null; // val array is initialized with
					varName = currentToken.getLexeme(); // name of the array
					accept(TokenType.ID);

					parseStatementAssign(); // TODO put actual exprValue in arrayVal

					statemt = new VarDeclStmt(new VarDecl(new ArrayType(new ClassType(startingId)), 
							varName), ex);
				} else { // indexed assign
					ref = new IdRef(startingId);
					Expression indexExpr = null;
					ex = null; // value assigned to ref[indexExpr]
					
					parseExpr(); // TODO put actual exprValue in indexExpr
					accept(TokenType.RSQUARE);
					parseStatementAssign(); // TODO put actual exprValue in assignValue
					
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
			ex = null; // value that var is initialized to

			accept(TokenType.ID);
			parseStatementAssign(); // TODO put actual exprValue in varValue

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
			
			Expression ex = null; 
			parseExpr(); // TODO put exprvalue into ex
			
			statemt = new AssignStmt(ref, ex);
			break;
		case LSQUARE:
			advance();
			
			ex = null; 
			Expression indexExpr = null;
			parseExpr(); // TODO indexExpr
			accept(TokenType.RSQUARE);
			accept(TokenType.ASSIGNMENT);
			parseExpr(); // TODO assigned to ref[index]
			
			statemt = new IxAssignStmt(ref, indexExpr, ex);
			break;
		case LPAREN:
			advance();
			
			ExprList argList = null;
			if (currentToken.getType() != TokenType.RPAREN) {
				parseArgList(); // TODO get the actual list of exprs
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

	// TODO return expr associated with assignment
	private void parseStatementAssign() {
		Expression expr;
		accept(TokenType.ASSIGNMENT);
		parseExpr();
		accept(TokenType.SEMICOLON);
	}

	void parseExpr() {
		switch (currentToken.getType()) {
		case ID:
		case THIS:
			parseRef();

			if (currentToken.getType() == TokenType.LSQUARE) {
				advance();
				parseExpr();
				accept(TokenType.RSQUARE);
			} else if (currentToken.getType() == TokenType.LPAREN) {
				advance();
				if (currentToken.getType() != TokenType.RPAREN) {
					parseArgList();
				}
				accept(TokenType.RPAREN);
			}
			break;
		case LPAREN:
			advance();
			parseExpr();
			accept(TokenType.RPAREN);
			break;
		case NUM_LITERAL:
		case T:
		case F:
			advance();
			break;
		case NEW:
			advance();

			if (currentToken.getType() == TokenType.ID) {
				advance();
				if (currentToken.getType() == TokenType.LPAREN) {
					advance();
					accept(TokenType.RPAREN);
				} else {
					accept(TokenType.LSQUARE);
					parseExpr();
					accept(TokenType.RSQUARE);
				}
			} else if (currentToken.getType() == TokenType.INT) {
				advance();
				accept(TokenType.LSQUARE);
				parseExpr();
				accept(TokenType.RSQUARE);
			} else {
				throw new SyntaxError("invalid use of new in expression");
			}
			break;
		case UNOP:
			advance();
			parseExpr();
			break;
		default:
			throw new SyntaxError("invalid expression");
		}

		if (currentToken.getType() == TokenType.BINOP) {
			advance();
			parseExpr();
		} else if (currentToken.getLexeme().equals("-")) {
			currentToken.convUnop2Binop();
			advance();
			parseExpr();
		}
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
