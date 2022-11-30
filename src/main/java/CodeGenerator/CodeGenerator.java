package CodeGenerator;


import Parser.CodeGeneratorInterface;
import Scanner.Token;
import ast.block.BlockLabel;
import ast.block.ForLoop;
import ast.block.IfCases;
import ast.block.repeatLoop;
import ast.expr.binary.arithmatic.BinaryAnd;
import ast.expr.binary.arithmatic.BinaryNot;
import ast.expr.binary.arithmatic.BinaryOr;
import ast.expr.binary.arithmatic.BinaryXor;
import ast.expr.constant.*;
import ast.program.function.FunctionArguments;
import ast.type.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import symtab.SymbolTable;
import symtab.TableStack;
import symtab.dscp.AbstractDescriptor;
import symtab.dscp.array.ArrayDescriptor;
import symtab.dscp.function.FunctionDescriptor;
import symtab.dscp.function.Functions;
import symtab.dscp.struct.StructureDescriptor;
import symtab.dscp.struct.Structures;
import symtab.dscp.variable.GlobalVar;
import symtab.dscp.variable.VariableDescriptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static ast.type.Type.DOUBLE;
import static ast.type.Type.FLOAT;
import static ast.type.Type.LONG;
import static ast.type.Type.*;
import static org.objectweb.asm.Opcodes.*;

public class CodeGenerator implements CodeGeneratorInterface {
    private static final String OUTPUT_FILE = "Compiled.class";
    public static final String SUPER_CLASS = "java/lang/Object";
    public static final String GENERATED_CLASS = "Compiled";

    public static ClassWriter mainClw;
    public static ClassWriter structClw;
    public static MethodVisitor mVisit;

    public static Stack<Object> semanticStack = new Stack<>();
    public static Stack<BlockLabel> blockLabels = new Stack<>();
    private boolean isNeg = false;
    private String currentAssignId;

    private boolean inFunction;
    private boolean isConst = false;
    private boolean cast = false;
    FunctionDescriptor functionDescriptor;

    private boolean inRecord = false;
    private boolean inFunctionCall = false;
    public static ArrayList<Type> functionParameters;
    private String currentID;
    private int constOp, strOp, ldrOp, opcode;

    public static void initFunctionParameters() {
        functionParameters = new ArrayList<>();
    }
    AbstractDescriptor currentRecord;

    public static Object getPopSemanticStack() {
        return semanticStack.pop();
    }

    public static void initClass() {
        mainClw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        mainClw.visit(V1_8, ACC_PUBLIC, GENERATED_CLASS, null, SUPER_CLASS, null);
        mVisit = mainClw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mVisit.visitCode();
        mVisit.visitVarInsn(ALOAD, 0);
        mVisit.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS, "<init>", "()V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(1, 1);
        mVisit.visitEnd();

        mVisit = mainClw.visitMethod(ACC_PUBLIC | ACC_STATIC, "print", "(I)V", null, null);
        mVisit.visitCode();
        mVisit.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mVisit.visitVarInsn(ILOAD, 0);
        mVisit.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(2, 2);
        mVisit.visitEnd();


        mVisit = mainClw.visitMethod(ACC_PUBLIC | ACC_STATIC, "print", "(J)V", null, null);
        mVisit.visitCode();
        mVisit.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mVisit.visitVarInsn(LLOAD, 0);
        mVisit.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(1, 1);
        mVisit.visitEnd();

        mVisit = mainClw.visitMethod(ACC_PUBLIC | ACC_STATIC, "print", "(F)V", null, null);
        mVisit.visitCode();
        mVisit.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mVisit.visitVarInsn(FLOAD, 0);
        mVisit.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(1, 1);
        mVisit.visitEnd();

        mVisit = mainClw.visitMethod(ACC_PUBLIC | ACC_STATIC, "print", "(D)V", null, null);
        mVisit.visitCode();
        mVisit.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mVisit.visitVarInsn(DLOAD, 0);
        mVisit.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(1, 1);
        mVisit.visitEnd();

        mVisit = mainClw.visitMethod(ACC_PUBLIC | ACC_STATIC, "print", "(Ljava/lang/String;)V", null, null);
        mVisit.visitCode();
        mVisit.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mVisit.visitVarInsn(ALOAD, 0);
        mVisit.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(1, 1);
        mVisit.visitEnd();

        mVisit = mainClw.visitMethod(ACC_PUBLIC | ACC_STATIC, "len", "(Ljava/lang/String;)I", null, null);
        mVisit.visitCode();
        mVisit.visitVarInsn(ALOAD, 0);
        mVisit.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisit.visitInsn(IRETURN);
        mVisit.visitMaxs(1, 1);
        mVisit.visitEnd();
        ///TODO scanner

    }

    public static void writeFinalClassCode() {
        Logger.log("Writing the generated code into the executable output file");
        mainClw.visitEnd();
        try (OutputStream out = new FileOutputStream(OUTPUT_FILE)) {
            out.write(mainClw.toByteArray());
            Logger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doSemantic(String sem) {
        System.out.println(sem + " :" + Token.getCurrentToken());
        switch (sem) {
            case "push":
                semanticStack.push(Token.getCurrentToken());
                break;
            case "MFDSC":
                MFDSC();
                break;
            case "addArg":
                addArg();
                break;
            case "CFDSC":
                CFDSC();
                break;
            case "CBLOCK":
                CBlock();
                break;
            case "MSDSCc":
                MSDSC(true);
                break;
            case "MSDSCf":
                MSDSC(false);
                break;
            case "MRDSC":
                MRDSC();
                break;
            case "CRDSC":
                CRDSC();
                break;
            case "CONST":
                isConst = true;
                break;
            case "ASSIGNc":
                ASSIGN(true);
                break;
            case "ASSIGNf":
                ASSIGN(false);
                break;
            case "CVALUE":
                CVALUE();
                break;
            case "MADSCc":
                MADSC(true, false);
                break;
            case "MADSCf":
                MADSC(false, false);
                break;
            case "MADSCcnew":
                MADSC(true, true);
                break;
            case "MADSCfnew":
                MADSC(false, true);
                break;
            case "CAST":
                cast = true;
                break;
            case "BAND":
                BETypeCheck();
                BinaryAnd.and();
                break;
            case "MAND":
                METypeCheck();
                BinaryAnd.and();
                break;
            case "BOR":
                BETypeCheck();
                BinaryOr.or();
                break;
            case "BNOT":
                BinaryNot.not();
                break;
            case "MOR":
                METypeCheck();
                BinaryOr.or();
                break;
            case "BXOR":
                BETypeCheck();
                BinaryXor.xor();
                break;
            case "MXOR":
                METypeCheck();
                BinaryXor.xor();
                break;
            case "ADD":
            case "SUB":
            case "MULT":
            case "DIV":
            case "MOD":
                Type t1 = Type.getType((String) semanticStack.pop());
                Type t2 = Type.getType((String) semanticStack.pop());
                mVisit.visitInsn(determineArithmeticType(t1, t2, sem));
                break;
            case "beginIf":
                startIf();
                break;
            case "endIf":
                endIf();
                break;
            case "endElse":
                endElse();
                break;
            case "beginRepeat":
                repeatLoop.startRepeat();
                break;
            case "endRepeat":
                repeatLoop.endRepeat();
                break;
            case "pushID":
                pushID(false);
                break;
            case "pushIDAssign":
                pushID(true);
                break;
            case "pushAssignment":
                currentAssignId = currentID;
                semanticStack.push(Token.getCurrentToken());
                break;
            case "CASSIGN":
                CASSIGN();
                break;
            case "forBegin":
                ForLoop.initForLoop();
                break;
            case "forCondition":
                ForLoop.forConditions();
                break;
            case "forUpdate":
                ForLoop.forUpdate();
                break;
            case "forEndBlock":
                ForLoop.completeFor();
                break;
            case "FCs":
                inFunctionCall = true;
                functionParameters = new ArrayList<>();
                break;
            case "FCe":
                FC();
                break;
            case "FR":
                Type type = Type.getType((String) semanticStack.pop());
                mVisit.visitInsn(determineFunctionReturnOp(type));
                break;
            case "VFR":
                mVisit.visitInsn(determineFunctionReturnOp(VOID));
                break;
            case "break":
                Logger.log("break");
                try {
                    CodeGenerator.mVisit.visitJumpInsn(Opcodes.GOTO, blockLabels.peek().getEndLabel());
                } catch (Exception e) {
                    Logger.error("break operation should only be used in loops");
                }
                break;
            case "continue":
                Logger.log("continue");
                try {
                    CodeGenerator.mVisit.visitJumpInsn(Opcodes.GOTO, blockLabels.peek().getStartLabel());
                } catch (Exception e) {
                    Logger.error("continue operation should only be used in loops");
                }
                break;
            case "accessArray":
                arrayAccess();
                break;
            case "loadArray":
                loadAccess();
                break;
            case "IDPP":
                AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
                if (dsc instanceof GlobalVar)
                    CodeGenerator.mVisit.visitFieldInsn(Opcodes.GETSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
                else
                    CodeGenerator.mVisit.visitVarInsn(determineLoadOp(dsc.getType()), dsc.getStackIndex());
                semanticStack.pop();
                IDPP();
                break;
            case "PPID":
                dsc = TableStack.getInstance().find(currentAssignId);
                if (dsc instanceof GlobalVar)
                    CodeGenerator.mVisit.visitFieldInsn(Opcodes.GETSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
                else
                    CodeGenerator.mVisit.visitVarInsn(determineLoadOp(dsc.getType()), dsc.getStackIndex());
                PPID();
                break;
            case "IDMM":
                dsc = TableStack.getInstance().find(currentAssignId);
                if (dsc instanceof GlobalVar)
                    CodeGenerator.mVisit.visitFieldInsn(Opcodes.GETSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
                else
                    CodeGenerator.mVisit.visitVarInsn(determineLoadOp(dsc.getType()), dsc.getStackIndex());
                semanticStack.pop();
                IDMM();
                break;
            case "MMID":
                dsc = TableStack.getInstance().find(currentAssignId);
                if (dsc instanceof GlobalVar)
                    CodeGenerator.mVisit.visitFieldInsn(Opcodes.GETSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
                else
                    CodeGenerator.mVisit.visitVarInsn(determineLoadOp(dsc.getType()), dsc.getStackIndex());
                MMID();
                break;
            default:
                throw new RuntimeException("sem (" + sem + ") doesn't exist.");
        }
    }

    private void FC() {
        inFunctionCall = false;
        Logger.log("function call");
        Type[] types = new Type[functionParameters.size()];

        for (int i = 0; i < functionParameters.size(); i++) {
            types[i] = functionParameters.get(i);
        }

        String id = (String) semanticStack.pop();

        if (!Functions.getInstance().contains(id, types)) {
            Logger.error("no function definition found");
        }
        FunctionDescriptor descriptor = Functions.getInstance().get(id, types);
        CodeGenerator.mVisit.visitMethodInsn(Opcodes.INVOKESTATIC, CodeGenerator.GENERATED_CLASS, descriptor.getName(), descriptor.getDescriptor(), false);

        Type returnType = descriptor.getReturnType();
        semanticStack.push(Type.getTypeName(returnType));
    }

    private int determineFunctionReturnOp(Type type) {
        Type currentFuncReturnType = TableStack.getInstance().currentFunction().getReturnType();
        if (type == DOUBLE && currentFuncReturnType == DOUBLE)
            return Opcodes.DRETURN;
        else if (type == FLOAT && currentFuncReturnType == FLOAT)
            return Opcodes.FRETURN;
        else if (type == LONG && currentFuncReturnType == LONG)
            return Opcodes.LRETURN;
        else if (type == INT && (currentFuncReturnType == INT || currentFuncReturnType == CHAR || currentFuncReturnType == BOOL))
            return Opcodes.IRETURN;
        else if (type == VOID && currentFuncReturnType == VOID)
            return RETURN;
        else if (type == STRING && currentFuncReturnType == STRING)
            return ARETURN;
        else
            Logger.error("invalid function return type");
        return 0;
    }

    AbstractDescriptor ardsc;

    private void loadAccess() {
//        beforeArray.add(Token.tokens.get(Token.id - 2).getValue());
        String currentID = (String) semanticStack.pop();
        ardsc = TableStack.getInstance().find(currentID);

        semanticStack.push(Type.getTypeName(Type.toSimple(ardsc.getType())));

        if (inFunctionCall)
            functionParameters.add(ardsc.getType());
        CodeGenerator.mVisit.visitVarInsn(Opcodes.ALOAD, ardsc.getStackIndex());
    }

//    Stack<String> beforeArray = new Stack<>();

    private void arrayAccess() {

        //because of var dcl like int i = arr[3] type shouldn't be poped
        //so it should be handled in assignment
        String accessType = (String) semanticStack.pop();

        if (!accessType.equals("INT"))
            Logger.error("arrays can only be accessed using integer types");
//        String before = null;
//        if (beforeArray.size() != 0) {
//            before = beforeArray.pop();
//            if (!(before.equals("++") || before.equals("--")))
//                before = null;
//        }
//        if (!Token.nextOfCurrentToken().equals("=")
//                && !Token.nextOfCurrentToken().equals("++") && !Token.nextOfCurrentToken().equals("--") && before == null) {
        CodeGenerator.mVisit.visitInsn(determineArrayOp(Type.toSimple(ardsc.getType())));
//        }

    }

    private int determineArrayOp(Type type) {
        if (type == DOUBLE)
            return Opcodes.DALOAD;
        else if (type == FLOAT)
            return Opcodes.FALOAD;
        else if (type == LONG)
            return Opcodes.LALOAD;
        else if (type == INT)
            return Opcodes.IALOAD;
        else
            Logger.error("unable to create non primitive array");
        return 0;

    }

    private void METypeCheck() {
        String first = ((String) semanticStack.pop()).toUpperCase();
        String second = ((String) semanticStack.pop()).toUpperCase();
        if ((first.equals("LONG") || first.equals("INT")) && (second.equals("LONG") || second.equals("INT"))) {
            semanticStack.push(first);
            semanticStack.push(second);
        } else
            Logger.error("type mismatch");
    }

    private void BETypeCheck() {
        String first = ((String) semanticStack.pop()).toUpperCase();
        String second = ((String) semanticStack.pop()).toUpperCase();
        if (!first.equals(second) || !first.equals("BOOL"))
            Logger.error("type mismatch");
        else {
            semanticStack.push(first);
            semanticStack.push(second);
        }
    }

    AbstractDescriptor descriptor;
    AbstractDescriptor simpleDs;


    private void MFDSC() {
        inFunction = true;
        FunctionDescriptor fdsc = new FunctionDescriptor();
        fdsc.setName(Token.getCurrentToken());
        String typeTxt = (String) semanticStack.pop();
        Type type = Type.getType(typeTxt.toUpperCase());
        fdsc.setReturnType(type);
        FunctionArguments.getInstance().init();
        functionDescriptor = fdsc;
    }

    private void addArg() {
        FunctionArguments fa = FunctionArguments.getInstance();
        String typeTxt = (String) semanticStack.pop();
        Type type = Type.getType(typeTxt.toUpperCase());
        fa.addArgument(Token.getCurrentToken(), type);
    }

    private void CFDSC() {
        functionDescriptor.setParameters(FunctionArguments.getInstance().getArguments());
        if (checkOperation(functionDescriptor)) {
            Functions.getInstance().addFunction(functionDescriptor);
        } else {
            if (!Functions.getInstance().contains(functionDescriptor.getName(), functionDescriptor.getParameterTypes()))
                Functions.getInstance().addFunction(functionDescriptor);
            else
                functionDescriptor = Functions.getInstance().get(functionDescriptor.getName(), functionDescriptor.getParameterTypes());
            functionDescriptor.setCompleteDCL(true);
        }
        System.out.println("in write function");
        boolean isMain = functionDescriptor.getName().equals("start") && functionDescriptor.getReturnType() == VOID && functionDescriptor.getParameterTypes().length == 0;
        String funcName;
        if (isMain)
            funcName = "main";
        else
            funcName = functionDescriptor.getName();
        mVisit = mainClw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, funcName,
                isMain ? "([Ljava/lang/String;)V" : functionDescriptor.getDescriptor(), null, null);
        mVisit.visitCode();
        TableStack.getInstance().pushSymbolTable(new SymbolTable(new HashMap<>()));
        TableStack.getInstance().newFunction(functionDescriptor, isMain);
        CodeGenerator.mVisit.visitLabel(new Label());
    }

    private boolean checkOperation(FunctionDescriptor descriptor) {
        if (Functions.getInstance().contains(descriptor.getName(), descriptor.getParameterTypes())) {
            descriptor = Functions.getInstance().get(descriptor.getName(), descriptor.getParameterTypes());
            if (descriptor.isCompleteDCL())
                Logger.error("invalid function declaration");
            return false;
        }
        return true;
    }

    private void CBlock() {
        CodeGenerator.mVisit.visitLabel(new Label());
        TableStack.getInstance().popSymbolTable();
        System.out.println(TableStack.getInstance().getStackSize());
        boolean isMain = functionDescriptor.getName().equals("start") &&
                functionDescriptor.getReturnType() == VOID &&
                functionDescriptor.getParameterTypes().length == 0;
        if (functionDescriptor.getReturnType() == Type.VOID || isMain) {
            mVisit.visitInsn(Opcodes.RETURN);
        }
        mVisit.visitEnd();
        inFunction = false;
    }

    private void MSDSC(boolean existOtherVar) {

        String name = (String) semanticStack.pop();
        System.out.println("////" + name);
        String typeTxt = (String) semanticStack.pop();
        if (existOtherVar) {
            semanticStack.push(typeTxt);
        }
        Type type = Type.getType(typeTxt.toUpperCase());
        System.out.println(typeTxt);
        AbstractDescriptor sdsc = new VariableDescriptor();
        sdsc.setType(type);
        sdsc.setName(name);
        sdsc.setConst(isConst);
        if (!existOtherVar)
            isConst = false;

        descriptor = sdsc;
        if (inFunction) {
            TableStack.getInstance().addVariable(sdsc);
            simpleDs = sdsc;
        } else if (inRecord) {
            TableStack.getInstance().addVariable(sdsc);
            CodeGenerator.structClw.visitField
                    (Opcodes.ACC_PUBLIC | ACC_STATIC, sdsc.getName(), sdsc.getType().typeName(), null, null).visitEnd();

        } else {
            TableStack.getInstance().addGlobal(sdsc);
            mainClw.visitField(ACC_PUBLIC | ACC_STATIC, name,
                    type.typeName(), null, null).visitEnd();
        }
    }

    private int determineOp(Type type) {
        if (type == DOUBLE)
            return Opcodes.DSTORE;
        else if (type == FLOAT)
            return Opcodes.FSTORE;
        else if (type == LONG)
            return Opcodes.LSTORE;
        else if (type == INT)
            return Opcodes.ISTORE;
        else if (type == DOUBLE_ARRAY)
            return Opcodes.DASTORE;
        else if (type == FLOAT_ARRAY)
            return FASTORE;
        else if (type == LONG_ARRAY)
            return LASTORE;
        else if (type == INT_ARRAY)
            return IASTORE;
        else
            return Opcodes.ASTORE;
    }

    private int determineArithmeticType(Type t1, Type t2, String arithmetic) {
        if (!t1.typeName().equals(t2.typeName()))
            Logger.error("type mismatch");
        if (t1.equals(INT)) {
            semanticStack.push("INT");
            switch (arithmetic) {
                case "ADD":
                    return IADD;
                case "SUB":
                    return ISUB;
                case "MULT":
                    return IMUL;
                case "DIV":
                    return IDIV;
                case "MOD":
                    return IREM;
            }
        } else if (t1.equals(FLOAT)) {
            semanticStack.push("FLOAT");
            switch (arithmetic) {
                case "ADD":
                    return FADD;
                case "SUB":
                    return FSUB;
                case "MULT":
                    return FMUL;
                case "DIV":
                    return FDIV;
                case "MOD":
                    return FREM;
            }
        } else if (t1.equals(DOUBLE)) {
            semanticStack.push("DOUBLE");
            switch (arithmetic) {
                case "ADD":
                    return DADD;
                case "SUB":
                    return DSUB;
                case "MULT":
                    return DMUL;
                case "DIV":
                    return DDIV;
                case "MOD":
                    return DREM;
            }
        } else if (t1.equals(LONG)) {
            semanticStack.push("LONG");
            switch (arithmetic) {
                case "ADD":
                    return LADD;
                case "SUB":
                    return LSUB;
                case "MULT":
                    return LMUL;
                case "DIV":
                    return LDIV;
                case "MOD":
                    return LREM;
            }
        } else Logger.error("type mismatch");
        return -1;
    }

    private void ASSIGN(boolean existOtherVar) {
//        semanticStack.pop();
        String typeTxt = (String) semanticStack.pop();
        Type type = Type.getType(typeTxt);
        if (existOtherVar) {
            semanticStack.push(Type.getTypeName(simpleDs.getType()));
        }
        if (simpleDs.getType() == AUTO) {
            simpleDs.setType(type);
        }
        typeCheckAndImplicitCast(typeTxt.toUpperCase());
        mVisit.visitVarInsn(determineOp(simpleDs.getType()), simpleDs.getStackIndex());
    }

    private void typeCheckAndImplicitCast(String typeTxt) {
        if (typeTxt.equals(Type.getTypeName(simpleDs.getType())) && !typeTxt.equals("CHAR"))
            return;
        if (typeTxt.equals("INT")) {
            if (Type.getTypeName(simpleDs.getType()).equals("LONG"))
                CodeGenerator.mVisit.visitInsn(Opcodes.I2L);
            else
                Logger.error("INT can't assign to " + Type.getTypeName(simpleDs.getType()));
        } else if (typeTxt.equals("FLOAT")) {
            if (Type.getTypeName(simpleDs.getType()).equals("DOUBLE"))
                CodeGenerator.mVisit.visitInsn(Opcodes.F2D);
            else
                Logger.error("FLOAT can't assign to " + Type.getTypeName(simpleDs.getType()));
        } else if (typeTxt.equals("CHAR")) {
            if (Type.getTypeName(simpleDs.getType()).equals("LONG"))
                CodeGenerator.mVisit.visitInsn(Opcodes.I2D);
            else if (Type.getTypeName(simpleDs.getType()).equals("CHAR"))
                CodeGenerator.mVisit.visitInsn(Opcodes.I2C);
            else if (!Type.getTypeName(simpleDs.getType()).equals("INT"))
                Logger.error("CHAR can't assign to " + Type.getTypeName(simpleDs.getType()));
        } else {
            Logger.error(typeTxt + " can't assign to " + Type.getTypeName(simpleDs.getType()));
        }
    }

    private void explicitCast() {
        String from = ((String) semanticStack.pop()).toUpperCase();
//        Type from = Type.getType(typeFromTxt);
        String to = ((String) semanticStack.pop()).toUpperCase();
//        Type to = Type.getType(typeToTxt);
        if (from.equals(to))
            return;
        if (from.equals("INT")) {
            if (to.equals("LONG")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.I2L);
                semanticStack.push(to);
            } else
                Logger.error("INT can't cast to " + to);
        } else if (from.equals("FLOAT")) {
            if (to.equals("DOUBLE")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.F2D);
                semanticStack.push(to);
            } else if (to.equals("INT")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.F2I);
                semanticStack.push(to);
            } else if (to.equals("LONG")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.F2L);
                semanticStack.push(to);
            } else
                Logger.error("FLOAT can't cast to " + to);
        } else if (from.equals("DOUBLE")) {
            if (to.equals("INT")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.D2I);
                semanticStack.push(to);
            } else if (to.equals("LONG")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.D2L);
                semanticStack.push(to);
            } else
                Logger.error("FLOAT can't cast to " + to);
        } else if (from.equals("CHAR")) {
            if (to.equals("LONG")) {
                CodeGenerator.mVisit.visitInsn(Opcodes.I2D);
                semanticStack.push(to);
            } else if (!to.equals("INT"))
                Logger.error("CHAR can't cast to " + to);
        } else {
            Logger.error(from + " can't cast to " + Type.getTypeName(simpleDs.getType()));
        }
    }

    private void CVALUE() {
        Constant value = null;
        if (inFunctionCall) {
            switch (Token.getCurrentTokenID()) {
                case Token.REAL_NUMBER:
                    if (Token.getCurrentToken().contains("F"))
                        functionParameters.add(FLOAT);
                    else
                        functionParameters.add(DOUBLE);
                    break;
                case Token.DECIMAL_INTEGER:
                    if (Token.getCurrentToken().contains("L"))
                        functionParameters.add(LONG);
                    else
                        functionParameters.add(INT);
                    break;
                case Token.CHARACTER:
                    functionParameters.add(CHAR);
                    break;
                case Token.STRING:
                    functionParameters.add(STRING);
                    break;
                default:
                    functionParameters.add(BOOL);
                    break;
            }
        }
        switch (Token.getCurrentTokenID()) {
            case Token.REAL_NUMBER:
                if (Token.getCurrentToken().contains("F")) {
                    float value1 = Float.parseFloat(Token.getCurrentToken().substring(0, Token.getCurrentToken().length() - 1));
                    value = new FloatConstant(value1);
                    semanticStack.push("FLOAT");
                } else {
                    double value1 = Double.parseDouble(Token.getCurrentToken());
                    value = new DoubleConstant(value1);
                    semanticStack.push("DOUBLE");
                }
                break;
            case Token.DECIMAL_INTEGER:
                if (Token.getCurrentToken().contains("L")) {
                    long value1 = Long.parseLong(Token.getCurrentToken().substring(0, Token.getCurrentToken().length() - 1));
                    value = new LongConstant(value1);
                    semanticStack.push("LONG");
                } else {
                    int value1 = Integer.parseInt(Token.getCurrentToken());
                    value = new IntegerConstant(value1);
                    semanticStack.push("INT");
                }
                break;
            case Token.HEXADECIMAL:
                break;
            case Token.SCIENTIFIC_NOTATION:
                break;
            case Token.CHARACTER:
                value = new CharConstant(Token.getCurrentToken());
                semanticStack.push("CHAR");
                break;
            case Token.STRING:
                value = new StringConstant(Token.getCurrentToken());
                semanticStack.push("STRING");
                break;
            default:
                boolean value1 = Boolean.parseBoolean(Token.getCurrentToken());
                value = new BooleanConstant(value1);
                semanticStack.push("BOOL");
        }
        if (inFunctionCall)
            semanticStack.pop();
        value.compile();
        if (cast) {
//            semanticStack.pop();
            explicitCast();
            cast = false;
        }
    }

    private void MADSC(boolean existOtherVar, boolean assignedArr) {
        String typeArr = null;
        if (assignedArr)
        {
            String typeOfExpr = (String) semanticStack.pop();
            if (!(typeOfExpr.equals("INT") || typeOfExpr.equals("int")))
                Logger.error("type of expr not valid");
            typeArr = (String) semanticStack.pop();
        }
        String name = (String) semanticStack.pop();
        String typeTxt = (String) semanticStack.pop();
        if (assignedArr)
            if (!typeArr.equals(typeTxt))
                Logger.error("You can't initialize array like this!");
        if (existOtherVar)
            semanticStack.push(typeTxt);
        Type type = Type.getType(typeTxt.toUpperCase());
        AbstractDescriptor aDsc = new ArrayDescriptor();
        aDsc.setType(toArray(type));
        aDsc.setName(name);
        aDsc.setConst(false);

        if (inFunction) {
            TableStack.getInstance().addVariable(aDsc);
            mVisit.visitVarInsn(NEWARRAY, determinePrimitiveType(toArray(type)));
            mVisit.visitVarInsn(ASTORE, aDsc.getStackIndex());
        } else if (inRecord) {

            TableStack.getInstance().addVariable(aDsc);
            structClw.visitField(ACC_PUBLIC, name, toArray(type).typeName(), null, null).visitEnd();

        } else {
            TableStack.getInstance().addGlobal(aDsc);
            mainClw.visitField(ACC_PUBLIC | ACC_STATIC, name,
                    Type.toArray(type).toString(), null, null).visitEnd();
        }
    }

    private int determinePrimitiveType(Type type) {
        if (type == DOUBLE_ARRAY)
            return Opcodes.T_DOUBLE;
        else if (type == FLOAT_ARRAY)
            return Opcodes.T_FLOAT;
        else if (type == LONG_ARRAY)
            return Opcodes.T_LONG;
        else if (type == INT_ARRAY)
            return Opcodes.T_INT;
        else
            Logger.log("unsupported array type");
        return 0;
    }


    private void endElse() {
        CodeGenerator.mVisit.visitLabel(IfCases.endElseLabel.pop());
        TableStack.getInstance().popSymbolTable();
    }


    private void startIf() {
        Label endIfLabel = new Label();
        Label startIfLabel = new Label();
        IfCases.startIfLabel.push(startIfLabel);
        IfCases.endIfLabel.push(endIfLabel);
        CodeGenerator.mVisit.visitJumpInsn(Opcodes.IFEQ, endIfLabel);
        //in case we have E pop semantic stack
        semanticStack.pop();
        TableStack.getInstance().pushSymbolTable(new SymbolTable(new HashMap<>()));
        CodeGenerator.mVisit.visitLabel(startIfLabel);
    }

    private void endIf() {
        Label endIfLabel = IfCases.endIfLabel.pop();
        if (!Token.getCurrentToken().equals("else")) {
            CodeGenerator.mVisit.visitLabel(endIfLabel);
            TableStack.getInstance().popSymbolTable();
            System.out.println(TableStack.getInstance().getStackSize());
        } else {
            Label endelseBlock = new Label();
            Label staratelseBlock = new Label();
            IfCases.startElseLabel.push(staratelseBlock);
            IfCases.endElseLabel.push(endelseBlock);
            CodeGenerator.mVisit.visitJumpInsn(Opcodes.GOTO, endelseBlock);
            CodeGenerator.mVisit.visitLabel(endIfLabel);
            TableStack.getInstance().popSymbolTable();
            TableStack.getInstance().pushSymbolTable(new SymbolTable(new HashMap<>()));
            CodeGenerator.mVisit.visitLabel(staratelseBlock);
        }
    }

    private void pushID(boolean inAssign) {
        String id = Token.getCurrentToken();
        currentID = id;
        if (Functions.getInstance().containsName(id)) {
            semanticStack.push(id);
            return;
        }
        AbstractDescriptor dsc = TableStack.getInstance().find(id);
        if (inFunctionCall) {
            functionParameters.add(dsc.getType());
        }

        if (dsc instanceof ArrayDescriptor || dsc instanceof StructureDescriptor) {
            semanticStack.push(id);
            return;
        }

        if (!(dsc instanceof VariableDescriptor)) {
            Logger.error("variable not declared");
            return;
        }
        semanticStack.push(Type.getTypeName(dsc.getType()));
        if (inAssign)
            return;
        Logger.log("accessing variable");
        if (dsc instanceof GlobalVar) {
            CodeGenerator.mVisit.visitFieldInsn(Opcodes.GETSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
        } else
            CodeGenerator.mVisit.visitVarInsn(determineLoadOp(dsc.getType()), dsc.getStackIndex());

        if (inFunctionCall)
            semanticStack.pop();
    }


    private int determineLoadOp(Type type) {
        if (type == DOUBLE)
            return Opcodes.DLOAD;
        else if (type == FLOAT)
            return Opcodes.FLOAD;
        else if (type == LONG)
            return Opcodes.LLOAD;
        else if (type == INT)
            return Opcodes.ILOAD;
        else
            return Opcodes.ALOAD;
    }

    private void CASSIGN() {
        String type = (String) semanticStack.pop();
        String assignKind = (String) semanticStack.pop();

        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);

        if (dsc.isConst())
            Logger.error("can't assign const variable");
        if (dsc.getType().equals(AUTO))
            dsc.setType(Type.getType(type));
        if (assignKind.equals("="))
            directAssign(changeType(dsc, type));
        else if (assignKind.equals("/") || assignKind.equals("*") || assignKind.equals("+") || assignKind.equals("-"))
            indirectAssign(changeType(dsc, type), assignKind);
        semanticStack.pop();
    }

    private void indirectAssign(String type, String assignKind) {
        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
        if (!Type.getTypeName(dsc.getType()).equals(type)) {
            Logger.error("type mismatch");
            return;
        }
        if (dsc instanceof GlobalVar)
            CodeGenerator.mVisit.visitFieldInsn(Opcodes.GETSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
        else
            CodeGenerator.mVisit.visitVarInsn(determineLoadOp(dsc.getType()), dsc.getStackIndex());
        int strCode = determineAssignOp(Type.getType(type), assignKind);
        CodeGenerator.mVisit.visitInsn(strCode);
        if (dsc instanceof GlobalVar)
            mVisit.visitFieldInsn(Opcodes.PUTSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
        else {
            if (dsc instanceof ArrayDescriptor)
                mVisit.visitInsn(determineOp(Type.getType(type)));
            else
                mVisit.visitVarInsn(determineOp(dsc.getType()), dsc.getStackIndex());
        }
    }

    private int determineAssignOp(Type type, String assignKind) {
        if (type == DOUBLE || type == DOUBLE_ARRAY) {
            switch (assignKind) {
                case "/":
                    return DDIV;
                case "*":
                    return DMUL;
                case "+":
                    return DADD;
                case "-":
                    return DSUB;
            }
        } else if (type == FLOAT || type == FLOAT_ARRAY) {
            switch (assignKind) {
                case "/":
                    return FDIV;
                case "*":
                    return FMUL;
                case "+":
                    return FADD;
                case "-":
                    return FSUB;
            }
        } else if (type == LONG || type == LONG_ARRAY) {
            switch (assignKind) {
                case "/":
                    return LDIV;
                case "*":
                    return LMUL;
                case "+":
                    return LADD;
                case "-":
                    return LSUB;
            }
        } else if (type == INT || type == INT_ARRAY) {
            switch (assignKind) {
                case "/":
                    return IDIV;
                case "*":
                    return IMUL;
                case "+":
                    return IADD;
                case "-":
                    return ISUB;
            }
        } else
            Logger.error("type mismatch");
        return 0;
    }

    private static String changeType(AbstractDescriptor dsc, String varKind) {
        Type type = Type.getType(varKind);
        if (dsc instanceof ArrayDescriptor)
            return Type.getTypeName(Type.toArray(type));
        else
            return varKind;
    }

    private void directAssign(String varkKind) {
        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
        if (!Type.getTypeName(dsc.getType()).equals(varkKind)) {
            Logger.error("type mismatch: " + varkKind + " != " + Type.getTypeName(dsc.getType()));
            return;
        }
        if (dsc instanceof GlobalVar)
            mVisit.visitFieldInsn(Opcodes.PUTSTATIC, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
        else {
            if (dsc instanceof ArrayDescriptor)
                mVisit.visitInsn(determineOp(Type.getType(varkKind)));
            else
                mVisit.visitVarInsn(determineOp(Type.getType(varkKind)), dsc.getStackIndex());
        }
    }

    private void IDPP() {
        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
        Type type = Type.getType((String) semanticStack.peek());
        determineDualOp(type);
        opcode = determineAssignOp(type, "+");

        if (dsc instanceof ArrayDescriptor) {
            mVisit.visitInsn(DUP2);
            mVisit.visitInsn(determineArrayOp(Type.toSimple(ardsc.getType())));
            mVisit.visitInsn(DUP_X2);
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            mVisit.visitInsn(determineOp(dsc.getType()));

        } else {
            mVisit.visitInsn(ldrOp);
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            if (descriptor instanceof GlobalVar)
                mVisit.visitFieldInsn(Opcodes.PUTFIELD, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
            else {
                mVisit.visitVarInsn(strOp, dsc.getStackIndex());
            }
        }


    }

    private void IDMM() {
        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
        Type type = Type.getType((String) semanticStack.peek());
        determineDualOp(type);
        opcode = determineAssignOp(type, "-");

        if (dsc instanceof ArrayDescriptor) {
            mVisit.visitInsn(DUP2);
            mVisit.visitInsn(determineArrayOp(Type.toSimple(ardsc.getType())));
            mVisit.visitInsn(DUP_X2);
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            mVisit.visitInsn(determineOp(dsc.getType()));

        } else {
            mVisit.visitInsn(ldrOp);
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            if (descriptor instanceof GlobalVar)
                mVisit.visitFieldInsn(Opcodes.PUTFIELD, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
            else {
                mVisit.visitVarInsn(strOp, dsc.getStackIndex());
            }
        }
    }

    private void PPID() {
        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
        Type type = Type.getType((String) semanticStack.peek());
        determineDualOp(type);
        opcode = determineAssignOp(type, "+");

        if (dsc instanceof ArrayDescriptor) {
            mVisit.visitInsn(DUP2);
            mVisit.visitInsn(determineArrayOp(Type.toSimple(ardsc.getType())));
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            mVisit.visitInsn(DUP_X2);
            mVisit.visitInsn(determineOp(Type.getType(Type.getTypeName(dsc.getType()))));
        } else {
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            mVisit.visitInsn(ldrOp);
            if (dsc instanceof GlobalVar)
                mVisit.visitFieldInsn(Opcodes.PUTFIELD, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
            else {
                mVisit.visitVarInsn(strOp, dsc.getStackIndex());
            }
        }
    }

    private void MMID() {
        AbstractDescriptor dsc = TableStack.getInstance().find(currentAssignId);
        Type type = Type.getType((String) semanticStack.peek());
        determineDualOp(type);
        opcode = determineAssignOp(type, "-");

        if (dsc instanceof ArrayDescriptor) {
            mVisit.visitInsn(DUP2);
            mVisit.visitInsn(determineArrayOp(Type.toSimple(ardsc.getType())));
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            mVisit.visitInsn(DUP_X2);
            mVisit.visitInsn(determineOp(Type.getType(Type.getTypeName(dsc.getType()))));
        } else {
            mVisit.visitInsn(constOp);
            mVisit.visitInsn(opcode);
            mVisit.visitInsn(ldrOp);
            if (dsc instanceof GlobalVar)
                mVisit.visitFieldInsn(Opcodes.PUTFIELD, CodeGenerator.GENERATED_CLASS, dsc.getName(), dsc.getType().typeName());
            else {
                mVisit.visitVarInsn(strOp, dsc.getStackIndex());
            }
        }

    }

    private int determineDualOp(Type type) {
        if (type == DOUBLE) {
            constOp = Opcodes.DCONST_1;
            strOp = Opcodes.DSTORE;
            ldrOp = DUP2;
        } else if (type == FLOAT) {
            constOp = Opcodes.FCONST_1;
            strOp = Opcodes.FSTORE;
            ldrOp = DUP;
        } else if (type == LONG) {
            constOp = Opcodes.LCONST_1;
            strOp = Opcodes.LSTORE;
            ldrOp = DUP2;
        } else if (type == INT) {
            constOp = Opcodes.ICONST_1;
            strOp = Opcodes.ISTORE;
            ldrOp = DUP;
        } else
            Logger.error("invalid operation");
        return 0;
    }

    private void CRDSC() {
        inRecord = false;
        mVisit = structClw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mVisit.visitCode();
        mVisit.visitVarInsn(ALOAD, 0);
        mVisit.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS, "<init>", "()V", false);
        mVisit.visitInsn(RETURN);
        mVisit.visitMaxs(1, 100);
        mVisit.visitEnd();

        CodeGenerator.writeStructureClassCode(currentRecord.getName() + ".class");

    }


    private void MRDSC() {
        inRecord = true;
        StructureDescriptor adsc = new StructureDescriptor();
        adsc.setName(Token.getCurrentToken());
        adsc.setType(new Type(Token.getCurrentToken()));
        Structures.getInstance().addStructure(adsc);
        currentRecord = adsc;

        structClw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        structClw.visit(V1_8, ACC_PUBLIC, GENERATED_CLASS, null, SUPER_CLASS, null);

    }

    public static void writeStructureClassCode(String outputFile) {
        Logger.log("writing structures class code to the output file");
        try (OutputStream out = new FileOutputStream(outputFile)) {
            out.write(structClw.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
