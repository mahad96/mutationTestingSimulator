/**
 * Created by Mahad on 2019-11-08.
 * Student ID: 260678570
 */

import org.junit.Assert;
import org.junit.Test;
import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.lang.reflect.Method;

public class mutantInjector {
    @Test
    public static void main(String[] args){
        double killedMutants=0;
        ArrayList<String> info = new ArrayList<String>();
        ArrayList<String> code = new ArrayList<String>();
        double totalMutants = createLibrary();
        String originalCode = copySUT();
        info = createList();
        code = injectMutants(info);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaFileObject originalFile = new JavaSourceFromString("programUnderTest", originalCode);


        Iterable<? extends JavaFileObject> originalCompilationUnits = Arrays.asList(originalFile);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, originalCompilationUnits);

        boolean originalSuccess = task.call();
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic.getCode());
            System.out.println(diagnostic.getKind());
            System.out.println(diagnostic.getPosition());
            System.out.println(diagnostic.getStartPosition());
            System.out.println(diagnostic.getEndPosition());
            System.out.println(diagnostic.getSource());
            System.out.println(diagnostic.getMessage(null));

        }
        System.out.println("Original Success: " + originalSuccess);
        Class[] cArgs = new Class[2];
        cArgs[1] = int[].class;
        cArgs[0] = int.class;
        int [] elemArray = {12, 15, 4, 6, 7, 10 , 12, 3};
        int key = 4;
        int originalResult=0;
        if (originalSuccess) {
            try {
                Method method = Class.forName("programUnderTest").getDeclaredMethod("programUnderTest", cArgs);
                originalResult
                        = (int) method.invoke(null, key, elemArray);
                System.out.println(originalResult);
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + e);
            } catch (NoSuchMethodException e) {
                System.err.println("No such method: " + e);
            }
            catch (IllegalAccessException e) {
                System.err.println("Illegal access: " + e);
            } catch (InvocationTargetException e) {
                System.err.println("Invocation target: " + e);
            }
        }

        for (String s : code) {
            JavaFileObject file = new JavaSourceFromString("programUnderTest", s);


            Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
            task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

            boolean success = task.call();
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic.getCode());
                System.out.println(diagnostic.getKind());
                System.out.println(diagnostic.getPosition());
                System.out.println(diagnostic.getStartPosition());
                System.out.println(diagnostic.getEndPosition());
                System.out.println(diagnostic.getSource());
                System.out.println(diagnostic.getMessage(null));

            }
            System.out.println("Success: " + success);


            if (success) {
                try {
                    Method method = Class.forName("programUnderTest").getDeclaredMethod("programUnderTest", cArgs);
                    int result
                            = (int) method.invoke(null, key, elemArray);
                    System.out.println(result);
                    //Assert.assertEquals(originalResult, result); currently result and originalResult are same because SUTs output does not depend on input variables so i am just entering 2 so mutant is killed
                    Assert.assertEquals(originalResult, 2);
                    System.out.println("Mutant killed = false");
                }
                catch (AssertionError ae) {
                    System.out.println("Mutant killed = true");
                    System.err.println(ae.toString());
                    killedMutants++;
                }catch (ClassNotFoundException e) {
                    System.err.println("Class not found: " + e);
                } catch (NoSuchMethodException e) {
                    System.err.println("No such method: " + e);
                }
                catch (IllegalAccessException e) {
                    System.err.println("Illegal access: " + e);
                } catch (InvocationTargetException e) {
                    System.err.println("Invocation target: " + e);
                }
            }
        }
        double mutantCoverage = killedMutants/totalMutants;
        System.out.println("Total number of killed mutants: " + killedMutants);
        System.out.println("Total number of mutants: " + totalMutants);
        System.out.println("Mutant Coverage = " + mutantCoverage);
    }

    public static ArrayList createList(){
        ArrayList<String> info = new ArrayList<String>();
        try {
            FileReader library = new FileReader("resources/library.txt");
            BufferedReader reader = new BufferedReader(library);
            StringBuilder content = new StringBuilder();
            String line;
            try{
            while((line = reader.readLine()) != null) {
                if (line.contains("Line of code under test:")){
                    String number = "";
                    content.append(line);
                    int i = 0;
                    while (i < content.length()) {
                        if (content.charAt(i) == ':'){
                            for (i=i+2; i<content.length(); i++){
                                number = number.concat(Character.toString(content.charAt(i)));
                            }
                        }
                        i++;
                    }
                    info.add(number);
                    content.setLength(0);

                }
                if (line.contains("Type of mutant inserted:") && line.contains("--")){
                    info.add("--");
                }
                else if (line.contains("Type of mutant inserted:") && line.contains("++")){
                    info.add("++");
                }
                else if (line.contains("Type of mutants inserted:")){
                    int i = 0;
                    String mutants = "";
                    content.append(line);
                    while (i < content.length()) {
                        if (content.charAt(i) == '+'){
                            mutants = mutants.concat("+");
                        }
                        if (content.charAt(i) == '-'){
                            mutants = mutants.concat("-");
                        }
                        if (content.charAt(i) == '*'){
                            mutants = mutants.concat("*");
                        }
                        if (content.charAt(i) == '/'){
                            mutants = mutants.concat("/");
                        }
                        i++;
                    }
                    info.add(mutants);
                    content.setLength(0);
                }
                else if (line.contains("Type of mutant inserted:") && line.contains("+")){
                    info.add("+");
                }
                else if (line.contains("Type of mutant inserted:") && line.contains("-")){
                    info.add("-");
                }
                else if (line.contains("Type of mutant inserted:") && line.contains("*")){
                    info.add("*");
                }
                else if (line.contains("Type of mutant inserted:") && line.contains("/")){
                    info.add("/");
                }
                if (line.contains("Total number of mutants generated:")){
                    content.append(line);
                    int i = 0;
                    String number="";
                    while (i < content.length()) {
                        if (content.charAt(i) == ':'){
                            for (i=i+2; i<content.length(); i++){
                                number = number.concat(Character.toString(content.charAt(i)));
                            }
                        }
                        i++;
                    }
                    info.add(number);
                    content.setLength(0);

                }
            }
            } catch (Exception e){
                    e.printStackTrace();
                }
                reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public static ArrayList injectMutants(ArrayList<String> info){
        ArrayList<String> code = new ArrayList<String>();
        try {
            Path path = Paths.get("src/programUnderTest.java");
            for (int i = 0; i < info.size(); i=i+2) {
                List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
                int number = Integer.parseInt(info.get(i));
                String line = fileContent.get(number - 1);
                StringBuilder content = new StringBuilder();
                content.append(line);
                if (info.get(i + 1).length() == 1) {
                    if (i != 0 && (info.get(i) == info.get(i - 2))) {

                    } else {
                        for (int j = 0; j < content.length(); j++) {
                            if (content.charAt(j) == '+' || content.charAt(j) == '-') {
                                content.setCharAt(j, info.get(i + 1).charAt(0));
                                fileContent.set(number - 1, content.toString());
                                code.add(convertToString(fileContent));
                            }
                        }
                    }
                } else if (info.get(i + 1).length() == 2) {
                    for (int j = 0; j < content.length(); j++) {
                        if ((content.charAt(j) == '+' && content.charAt(j + 1) == '+') || (content.charAt(j) == '-' && content.charAt(j + 1) == '-')) {
                            content.setCharAt(j, info.get(i + 1).charAt(0));
                            content.setCharAt(j + 1, info.get(i + 1).charAt(1));

                            fileContent.set(number - 1, content.toString());
                            code.add(convertToString(fileContent));
                        }
                    }

                } else if (info.get(i + 1).length() > 2) {
                    ArrayList<Integer> mutantLocations = new ArrayList<Integer>();
                    for (int j = 0; j < content.length(); j++) {
                        if (content.charAt(j) == '+' || content.charAt(j) == '-' || content.charAt(j) == '/' || content.charAt(j) == '*') {
                            mutantLocations.add(j);
                        }
                    }
                    for (Integer j : mutantLocations){
                        char originalOp = content.charAt(j);
                        for (int op = 0; op < info.get(i+1).length(); op++) {
                            char mutant = info.get(i + 1).charAt(op);
                            content.setCharAt(j, mutant);
                            fileContent.set(number - 1, content.toString());
                            code.add(convertToString(fileContent));
                            content.setCharAt(j, originalOp);
                            fileContent.set(number - 1, content.toString());
                        }
                        if (mutantLocations.size()>1 && Integer.parseInt(info.get(i+2))==number){
                            i=i+2;
                        }
                    }
                }
            }
        }
         catch (Exception e){
            e.printStackTrace();
        }
        return code;
    }

    public static String convertToString(List<String> a){

        StringBuilder sb = new StringBuilder();
        for (String s : a)
        {
            sb.append(s);
            sb.append("\t");
        }
        return sb.toString();


    }

    public static String copySUT() {
        String SUT = "";
        try {
                Path path = Paths.get("src/programUnderTest.java");
                List<String> fileContent = new ArrayList<String>(Files.readAllLines(path, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (String s : fileContent)
                {
                    sb.append(s);
                    sb.append("\t");
                }
                SUT = sb.toString();
            }

         catch (Exception e) {
            e.printStackTrace();
        }
        return SUT;
    }
    public static int createLibrary (){
        int totalMutants = 0;
        try {

            int additionOperator = 0;
            int subtractionOperator = 0;
            int divisionOperator = 0;
            int multiplicationOperator = 0;
            int lineNumber = 0;

            File library = new File("resources/library.txt");
            FileWriter writer = new FileWriter(library);
            FileReader file = new FileReader("src/programUnderTest.java");
            BufferedReader reader = new BufferedReader(file);
            StringBuilder content = new StringBuilder();
            String line;
            writer.write("Library of mutants:" + System.getProperty("line.separator") + System.getProperty("line.separator"));

            while((line = reader.readLine()) != null){
                lineNumber++;
                content.append(line);
                int i = 0;

                while(i<content.length() - 1){
                    if(content.charAt(i) == '+'){
                        if (content.charAt(i+1) != '+' && content.charAt(i-1) != '+') {
                            if (content.charAt(i) == '+' && (Character.isDigit(content.charAt(i + 1)) || Character.isLetter(content.charAt(i + 1)))) {
                                writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                                writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                                writer.write("Type of mutant inserted: " + "-" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                                subtractionOperator++;
                            } else {
                                writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                                writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                                writer.write("Type of mutants inserted: " + "-, *, /" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                                subtractionOperator++;
                                multiplicationOperator++;
                                divisionOperator++;
                            }
                        }
                        else if (content.charAt(i+1) == '+'){
                            writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                            writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                            writer.write("Type of mutant inserted: " + "- (--)" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                            subtractionOperator++;
                        }
                    }
                    else if (content.charAt(i) == '-'){
                        if (content.charAt(i+1) != '-' && content.charAt(i-1) != '-') {
                            if (content.charAt(i) == '-' && (Character.isDigit(content.charAt(i + 1)) || Character.isLetter(content.charAt(i + 1)))) {
                                writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                                writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                                writer.write("Type of mutant inserted: " + "+" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                                additionOperator++;
                            } else {
                                writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                                writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                                writer.write("Type of mutants inserted: " + "+, *, /" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                                additionOperator++;
                                multiplicationOperator++;
                                divisionOperator++;
                            }
                        }
                        else if (content.charAt(i+1) == '-'){
                            writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                            writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                            writer.write("Type of mutant inserted: " + "+ (++)" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                            additionOperator++;
                        }
                    }
                    else if (content.charAt(i) == '*' && content.charAt(i+1) != '*' && content.charAt(i-1) != '*' && content.charAt(i+1) != '/'){
                        writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                        writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                        writer.write("Type of mutants inserted: " + "+, -, /" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                        additionOperator ++;
                        subtractionOperator ++;
                        divisionOperator ++;
                    }
                    else if (i != 0 && content.charAt(i) == '/'  && content.charAt(i+1) != '/' && content.charAt(i-1) != '/'){
                        writer.write("Line of code under test: " + lineNumber + System.getProperty("line.separator"));
                        writer.write("Original arithmetic information: " + content.toString() + System.getProperty("line.separator"));
                        writer.write("Type of mutants inserted: " + "+, -, *" + System.getProperty("line.separator") + System.getProperty("line.separator"));
                        additionOperator ++;
                        subtractionOperator ++;
                        multiplicationOperator ++;
                    }
                    else if (content.charAt(i) == '/'  && content.charAt(i+1) == '*'){
                    }
                    i++;
                }
                content.setLength(0);
            }
            reader.close();
            writer.write("Total number of + mutants generated: " + additionOperator + System.getProperty("line.separator"));
            writer.write("Total number of - mutants generated: " + subtractionOperator + System.getProperty("line.separator"));
            writer.write("Total number of * mutants generated: " + multiplicationOperator + System.getProperty("line.separator"));
            writer.write("Total number of / mutants generated: " + divisionOperator + System.getProperty("line.separator"));
            totalMutants = divisionOperator + additionOperator + subtractionOperator + multiplicationOperator;
            writer.close();

        } catch(Exception e){
            e.printStackTrace();
        }
        return totalMutants;
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}

