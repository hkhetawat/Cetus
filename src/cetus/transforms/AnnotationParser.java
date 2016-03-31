package cetus.transforms;

import cetus.analysis.CetusAnnotationParser;
import cetus.analysis.InlineAnnotationParser;
import cetus.analysis.OmpParser;
import cetus.hir.*;
import cetus.application.IPChainAnalysis;

import java.util.HashMap;
import java.util.LinkedList;

/**
* This pass is used to parse external annotations that might be
* present in the C source code input to Cetus and convert them to
* internal Cetus Annotations. 
*/
/* These Annotations are currently parsed into Cetus as Annotations inside of 
* DeclarationStatements, but are only strings. Each annotation
* specific pass can be used to extract the data within the annotations
* and store it in internal cetus annotations, or cetus appended annotations
* such as cetus_omp (input OpenMP)
*/
public class AnnotationParser extends TransformPass {


	public void recursivePrint(Traversable x)
	{
		for(Traversable y:x.getChildren())
		{
			if(y instanceof Traversable)
				recursivePrint((Traversable)y);
			System.out.println(y + " " + y.getClass());
		}
	}
    public AnnotationParser(Program program) {
        super(program);
        disable_protection = true;
    }

    public String getPassName() {
        return "[AnnotParser]";
    }
	
	private HashMap<String, String> parseCheckers(String[] tokenArray)
	{
		HashMap<String, String> checkers = new HashMap<String, String>();
		int i = 4;
		for(; !tokenArray[i + 1].equals("Recover"); i = i + 5)
		{
			String functionCall = tokenArray[i + 1] + tokenArray[i + 2] + tokenArray[i + 3] + tokenArray[i + 4];
			checkers.put(tokenArray[i + 3], functionCall);
		}
		return checkers;
	}
	
	private HashMap<String, LinkedList<String>> parseRecoverers(String[] tokenArray)
	{
		HashMap<String, LinkedList<String>> recoverers = new HashMap<String, LinkedList<String>>();
		int i = 4;
		for(; !tokenArray[i + 1].equals("Recover"); i = i + 5);
		i = i + 2;
		for(; !tokenArray[i].equals(")"); i = i + 5)
		{
			String functionCall = tokenArray[i + 1] + tokenArray[i + 2] + tokenArray[i + 3] + tokenArray[i + 4];
			if(recoverers.get(tokenArray[i + 3]) == null)
			{
				recoverers.put(tokenArray[i + 3], new LinkedList<String>());
			}
			recoverers.get(tokenArray[i + 3]).add(functionCall);
		}
		return recoverers;
	}

    public void start() {
        Annotation new_annot = null;
        boolean attach_to_next_annotatable = false;
        HashMap<String, Object> new_map = null;
        LinkedList<AnnotationStatement> stmts_to_be_attached =
                new LinkedList<AnnotationStatement>();
        LinkedList<Annotation> annots_to_be_attached =
                new LinkedList<Annotation>();
        FunctionCall persiDSCall = null;
        // Iterate over the program in Depth First order and search for
        // Annotations
        DFIterator<Traversable> iter = new DFIterator<Traversable>(program);
		IPChainAnalysis ipa = new IPChainAnalysis(program);
		//ipa.printUseDefChain();
		ipa.printDefUseChain();
        while(iter.hasNext()) {
            Traversable obj = iter.next();
            if (obj instanceof PreAnnotation) {
                PreAnnotation annot = (PreAnnotation)obj;
                Traversable curr_annot_parent = annot.getParent();
                String old_annot = annot.getText();
                // Convert the text of the old annotation to add spaces in order
                // for token creation
                old_annot = modifyAnnotationString(old_annot);
                // -------------------------------------------------------------
                // STEP 1:
                // Find the annotation type by parsing the text in the input
                // annotation and create a new Annotation of the corresponding
                // type
                // -------------------------------------------------------------
                String[] token_array = old_annot.split("\\s+");
                if (token_array[0].compareTo("#") == 0 &&
                    token_array[1].compareTo("pragma") == 0) {
                    String old_annot_key = token_array[2];
                    // Check for OpenMP annotations
                    if (old_annot_key.compareTo("omp") == 0) {
                        // -----------------------------------------------------
                        // Parse the contents:
                        // OmpParser puts the OpenMP directive parsing results
                        // into new_map
                        // -----------------------------------------------------
                        new_map = new HashMap<String, Object>();
                        attach_to_next_annotatable =
                            OmpParser.parse_omp_pragma(new_map, token_array);
                        // Create an OmpAnnotation and copy the parsed contents
                        // from new_map into a new OmpAnnotation
                        new_annot = new OmpAnnotation();
                        for (String key : new_map.keySet()) {
                            new_annot.put(key, new_map.get(key));
                        }
                    // Check if the pragma is a Cetus generated annotation
                    } else if (old_annot_key.compareTo("cetus") == 0) {
                        // -----------------------------------------------------
                        // Parse the contents:
                        // CetusAnnotationParser puts the Cetus annotation
                        // parsing results into new_map
                        // -----------------------------------------------------
                        new_map = new HashMap<String, Object>();
                        attach_to_next_annotatable =
                                CetusAnnotationParser.parse_pragma(
                                new_map, token_array);
                        // Create a CetusAnnotation and copy the parsed contents
                        // from new_map into a new CetusAnnotation
                        new_annot = new CetusAnnotation();
                        for (String key : new_map.keySet()) {
                            new_annot.put(key, new_map.get(key));
                        }
                    } else if(InlineAnnotationParser.parse_pragma(token_array)){
                        attach_to_next_annotatable = true;
                        new_annot = new InlineAnnotation(old_annot_key);
                    } else if(old_annot_key.compareTo("protect") == 0) {
						HashMap<String, String> checkers = parseCheckers(token_array);
						HashMap<String, LinkedList<String>> recoverers = parseRecoverers(token_array);
                        for (String key : checkers.keySet()) {
                            System.out.println(key + " -> " + checkers.get(key));
                        }
                        for (String key : recoverers.keySet()) {
                            System.out.print(key + " -> ");
							for(String calls:recoverers.get(key))
							{
								System.out.print(calls + " ");
							}
							System.out.println();
                        }
                        CompoundStatement parent_stmt =
                                (CompoundStatement)curr_annot_parent.getParent();
                        Statement curr_annot_parent_stmt = (Statement)curr_annot_parent;
						
						VariableDeclarator completedArrayDeclarator = new VariableDeclarator(new Identifier("completed"), new ArraySpecifier(new IntegerLiteral(1)));
						VariableDeclaration completedArrayDeclaration = new VariableDeclaration(Specifier.BOOL, completedArrayDeclarator);
						completedArrayDeclaration.setParent(null);
						parent_stmt.addDeclaration(completedArrayDeclaration);
						
						VariableDeclarator checkArrayDeclarator = new VariableDeclarator(new Identifier("check"), new ArraySpecifier(new IntegerLiteral(checkers.size())));
						VariableDeclaration checkArrayDeclaration = new VariableDeclaration(Specifier.BOOL, checkArrayDeclarator);
						checkArrayDeclaration.setParent(null);
						
						CompoundStatement whileBlock = new CompoundStatement();
						whileBlock.addDeclaration(checkArrayDeclaration);
						
						
						Traversable annotObj = iter.next();
						Statement annotStatement = (Statement)annotObj;
						parent_stmt.removeChild(annotStatement);
						annotStatement.setParent(null);
						
						CompoundStatement doWhileBlock = new CompoundStatement();
						doWhileBlock.addStatement(annotStatement);
						
						int i = 0;
                        for (String key : checkers.keySet()) 
						{
							String function = checkers.get(key);
							String parts[] = function.split("\\(");
							String functionName = parts[0];
							String arguements = parts[1].split("\\)")[0];
							
							FunctionCall checkCall = new FunctionCall(new Identifier(functionName), new Identifier(arguements));
							AssignmentExpression assignExpression = new AssignmentExpression(new ArrayAccess(new Identifier("check"), new IntegerLiteral(i)), AssignmentOperator.NORMAL, checkCall);
							ExpressionStatement assignStatement = new ExpressionStatement(assignExpression);
							doWhileBlock.addStatement(assignStatement);
							
							String recFunction = recoverers.get(key).get(0);
							String recParts[] = recFunction.split("\\(");
							String recFunctionName = recParts[0];
							String recArguements = recParts[1].split("\\)")[0];
							
							FunctionCall recCall = new FunctionCall(new Identifier(recFunctionName), new Identifier(recArguements));
							BinaryExpression recCondition = new BinaryExpression(recCall, BinaryOperator.COMPARE_EQ, new BooleanLiteral(false));
							CompoundStatement ifRecBlock = new CompoundStatement();
							ThrowExpression throwUnrecoverable = new ThrowExpression(new Identifier("unrecoverable"));
							ExpressionStatement throwStatement = new ExpressionStatement(throwUnrecoverable);
							ifRecBlock.addStatement(throwStatement);
							
							IfStatement recIf = new IfStatement(recCondition, ifRecBlock);
							
							BinaryExpression checkSingleCondition = new BinaryExpression(new ArrayAccess(new Identifier("check"), new IntegerLiteral(i)), BinaryOperator.COMPARE_EQ, new BooleanLiteral(false));
							CompoundStatement ifCheckBlock = new CompoundStatement();
							
							ifCheckBlock.addStatement(recIf);
							IfStatement checkIf = new IfStatement(checkSingleCondition, ifCheckBlock);
							
							doWhileBlock.addStatement(checkIf);
							
							i++;
                        }
						
						BinaryExpression checkCondition = new BinaryExpression(new ArrayAccess(new Identifier("check"), new IntegerLiteral(0)), BinaryOperator.COMPARE_EQ, new BooleanLiteral(false));
						for(i = 1; i < checkers.size(); i++)
						{
							BinaryExpression tempCondition = new BinaryExpression(new ArrayAccess(new Identifier("check"), new IntegerLiteral(i)), BinaryOperator.COMPARE_EQ, new BooleanLiteral(false));
							checkCondition = new BinaryExpression(checkCondition, BinaryOperator.LOGICAL_OR, tempCondition);
						}
						
						DoLoop checkLoop = new DoLoop(doWhileBlock, checkCondition);
						
						whileBlock.addStatement(checkLoop);
						
						BinaryExpression completeCondition = new BinaryExpression(new ArrayAccess(new Identifier("check"), new IntegerLiteral(0)), BinaryOperator.COMPARE_EQ, new BooleanLiteral(true));
						for(i = 1; i < checkers.size(); i++)
						{
							BinaryExpression tempCondition = new BinaryExpression(new ArrayAccess(new Identifier("check"), new IntegerLiteral(i)), BinaryOperator.COMPARE_EQ, new BooleanLiteral(true));
							completeCondition = new BinaryExpression(completeCondition, BinaryOperator.LOGICAL_AND, tempCondition);
						}
						
						CompoundStatement ifCompleteBlock = new CompoundStatement();
						AssignmentExpression assignExpression = new AssignmentExpression(new ArrayAccess(new Identifier("completed"), new IntegerLiteral(0)), AssignmentOperator.NORMAL, new BooleanLiteral(true));
						ExpressionStatement assignStatement = new ExpressionStatement(assignExpression);
						ifCompleteBlock.addStatement(assignStatement);
						IfStatement completedIf = new IfStatement(completeCondition, ifCompleteBlock);
						whileBlock.addStatement(completedIf);
						
						BinaryExpression protectCondition = new BinaryExpression(new ArrayAccess(new Identifier("completed"), new IntegerLiteral(0)), BinaryOperator.COMPARE_EQ, new BooleanLiteral(false));
						WhileLoop protectLoop = new WhileLoop(protectCondition, whileBlock);
						
                        parent_stmt.addStatementBefore(curr_annot_parent_stmt, protectLoop);
                        parent_stmt.removeChild(curr_annot_parent_stmt);
                    	System.out.println("Found persiDS pragma with parent - " + curr_annot_parent);
                    	continue;
                    } 
                    else {
                        // Found a Pragma Annotation that is neither OpenMP nor
                        // Cetus, insert it as a standalone PragmaAnnotation
                        // Restore the original text.
                        System.out.println("Found custom pragma");
                        old_annot = annot.getText().replace("#pragma ", "");
                        new_annot = PragmaAnnotation.parse(old_annot);
                        if (new_annot instanceof PragmaAnnotation.Range) {
                            attach_to_next_annotatable = true;
                        } else {
                            attach_to_next_annotatable = false;
                        }
                    }
                } else if ((token_array[0].compareTo("/")==0) &&
                           ((token_array[1].compareTo("*")==0) ||
                                (token_array[1].compareTo("/")==0))) {
                    // This is a comment annotation
                    String comment_annot_text = annot.getText();
                    comment_annot_text = comment_annot_text.replace("/* ", "");
                    comment_annot_text = comment_annot_text.replace(" */", "");
                    comment_annot_text = comment_annot_text.replace("// ", "");
                    comment_annot_text = comment_annot_text.replace("/*", "");
                    comment_annot_text = comment_annot_text.replace("*/", "");
                    comment_annot_text = comment_annot_text.replace("//", "");
                    new_annot = new CommentAnnotation(comment_annot_text);
                    if (!comment_annot_text.contains("\n")) {
                        ((CommentAnnotation)new_annot).setOneLiner(true);
                    }
                    attach_to_next_annotatable = false;
                }
                // -------------------------------------------------------------
                // STEP 2:
                // Based on whether the newly created annotation needs to be
                // attached to an Annotatable object or needs to be inserted as
                // a standalone Annotation contained within AnnotationStatement
                // or AnnotationDeclaration, perform the following IR insertion
                // and deletion operations
                // -------------------------------------------------------------
                // The annotation doesn't need to be attached to an existing
                // Annotatable object, create an Annotatable container for it
                // and insert it as a standalone annotation in the IR
                if (!attach_to_next_annotatable) {
                    // Dependending on the location of this annotation, the
                    // container should be AnnotationStatement or
                    // AnnotationDeclaration
                    if (curr_annot_parent instanceof DeclarationStatement) {
                        // The new standalone PragmaAnnotation must be attached
                        // to a container of type AnnotationStatement
                        AnnotationStatement annot_container =
                                new AnnotationStatement(new_annot);
                        // find the parent of the current annot parent that
                        // holds the annotation and insert the new Annotation
                        // container
                        DeclarationStatement curr_annot_parent_stmt = 
                                (DeclarationStatement)curr_annot_parent;
                        Statement parent_stmt =
                                (Statement)curr_annot_parent_stmt.getParent();
                        ((CompoundStatement)parent_stmt).addStatementBefore(
                                curr_annot_parent_stmt, annot_container);
                        // remove the old Annotation from the IR, we don't need
                        // it anymore
                        ((CompoundStatement)parent_stmt).removeChild(
                                curr_annot_parent_stmt);
                    } /*else if (persiDSCall != null)
                    {
                    	ExpressionStatement expr_container =
                                new ExpressionStatement(persiDSCall);
                        Statement parent_stmt =
                                (Statement)curr_annot_parent.getParent();
                        ((CompoundStatement)parent_stmt).addStatementBefore(
                                curr_annot_parent, expr_container);
                        ((CompoundStatement)parent_stmt).removeChild(
                                curr_annot_parent);
                    } */
                    else if (curr_annot_parent instanceof TranslationUnit) {
                        TranslationUnit tu_parent =
                                (TranslationUnit)curr_annot_parent;
                        // The new standalone PragmaAnnotation must be attached
                        // to a container of type AnnotationDeclaration
                        AnnotationDeclaration annot_container = 
                                new AnnotationDeclaration(new_annot);
                        // Insert the new AnnotationDeclaration into the
                        // TranslationUnit and remove the old Annotation
                        tu_parent.addDeclarationBefore(annot, annot_container);
                        tu_parent.removeChild(annot);
                    }
                    // In order to allow non-attached annotations mixed with
                    // attached annotations, check if the to_be_attached list
                    // is not empty. If it isn't, some annotations still exist
                    // that need to attached to the very next Annotatable.
                    // Hence, ...
                    if (!annots_to_be_attached.isEmpty()) {
                        attach_to_next_annotatable = true;
                    }
                } else {
                    // Add the newly created Annotation to a list of Annotations
                    // that will be attached to the required Annotatable object
                    // in the IR
                    annots_to_be_attached.add(new_annot);
                    // find the parent of the current annot parent that holds
                    // the annotation and remove the old annotation from the IR
                    if (curr_annot_parent instanceof DeclarationStatement) {
                        DeclarationStatement curr_annot_parent_stmt =
                                (DeclarationStatement)curr_annot_parent;
                        Statement parent_stmt =
                                (Statement)curr_annot_parent_stmt.getParent();
                        ((CompoundStatement)parent_stmt).removeChild(
                                curr_annot_parent_stmt);
                    } else if (curr_annot_parent instanceof TranslationUnit) {
                        ((TranslationUnit)curr_annot_parent).removeChild(annot);
                    }
                }
            // -----------------------------------------------------------------
            // STEP 3:
            // A list of newly created Annotations to be attached has been
            // created. Attach it to * the instance of Annotatable object that
            // does not already contain an input Annotation, this is encountered
            // next
            // -----------------------------------------------------------------
            } else if (obj instanceof DeclarationStatement &&
                       IRTools.containsClass(obj, PreAnnotation.class)) {
                continue;
            } else if (attach_to_next_annotatable &&
                       obj instanceof Annotatable) {
                Annotatable container = (Annotatable)obj;
                if (!annots_to_be_attached.isEmpty() && container != null) {
                    // Attach all the new annotations to this container
                    for (Annotation annot_to_be_attached:annots_to_be_attached){
                        container.annotate(annot_to_be_attached);
                    }
                } else {
                    System.out.println("Error");
                    Tools.exit(0);
                }
                // reset the flag to false, we've attached all annotations
                attach_to_next_annotatable = false;
                // Clear the list of annotations to be attached, we're done
                // with them
                annots_to_be_attached.clear();
            }
        }
    }
    
    private String modifyAnnotationString(String old_annotation_str) {
        String str = null;
        old_annotation_str = old_annotation_str.replace("#pragma", "# pragma");
        old_annotation_str = old_annotation_str.replace("/*", "/ * ");
        old_annotation_str = old_annotation_str.replace("//", "/ / ");
        if (old_annotation_str.contains("# pragma")) {
            // The delimiter for split operation is white space(\s).
            // Parenthesis, comma, and colon are delimiters, too. However, we
            // want to leave them in the pragma token array. Thus, we append a
            // space before and after the parenthesis and colons so that the
            // split operation can recognize them as independent tokens.
            old_annotation_str = old_annotation_str.replace("(", " ( ");
            old_annotation_str = old_annotation_str.replace(")", " ) ");
            old_annotation_str = old_annotation_str.replace(":", " : ");
            old_annotation_str = old_annotation_str.replace(",", " , ");
        }
        str = old_annotation_str;
        return str;
    }

}
