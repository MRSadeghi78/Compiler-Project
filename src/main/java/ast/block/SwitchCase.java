//package ast.block;
//
//import CodeGenerator.CodeGenerator;
//import javafx.util.Pair;
//import org.objectweb.asm.Label;
//import Scanner.Token;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import static CodeGenerator.CodeGenerator.*;
//
//public class SwitchCase {
//
//    static ArrayList<Pair<Integer,String>> allTokens = Token.tokens;
//    public static Label[] labels;
//    static ArrayList<Integer> icValues = new ArrayList<>();
//    private static int switchNum = 0;
//    private static boolean inSwitchCase = true;
//
//    public static void initSwitch(){
//        CodeGenerator.defaultLabel = new Label();
//        CodeGenerator.endCase = new Label();
//        iterateTokens();
//        initArray();
//        mVisit.visitTableSwitchInsn(minSwitch, maxSwitch, defaultLabel, labels);
//    }
//
//    private static void initArray(){
//        labels = new Label[CodeGenerator.maxSwitch - minSwitch + 1];
//        Arrays.fill(labels, defaultLabel);
//
//        for (int i = minSwitch; i <= maxSwitch; i++) {
//            if (icValues.contains(i)) {
//                labels[i - minSwitch] = new Label();
//            }
//        }
//
//
//    }
//
//    private static void iterateTokens(){
//        int n = 0;
//        for (int i = 0; i < allTokens.size(); i++) {
//
//            if (allTokens.get(i).getValue().equals("case") && inSwitchCase) {
//                maxSwitch = Math.max(maxSwitch, Integer.parseInt(allTokens.get(i + 1).getValue()));
//                minSwitch = Math.min(minSwitch, Integer.parseInt(allTokens.get(i + 1).getValue()));
//                icValues.add(Integer.valueOf(allTokens.get(i + 1).getValue()));
//            }
//        }
//    }
//}
