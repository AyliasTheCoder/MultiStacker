package me.aylias.esolang.stacker;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StackerFunction {

    public static StackerFunction main;
    final String name;
    String dir = "";
    StackerFunction caller = null;
    List<String> lines = new ArrayList<>();

    Stack<String> strStack = new Stack<>();
    Stack<Integer> intStack = new Stack<>();
    Stack<Float> floatStack = new Stack<>();
    Stack<Boolean> boolStack = new Stack<>();
    int lineNumber = 0;

    Map<String, Method> arglessFunctions = new HashMap<>();

    public StackerFunction(String name, String dir) {
        this.dir = dir;
        this.name = name;
        read();
        doRegister();
    }

    public StackerFunction(String name) {
        this.name = name;
        read();
        doRegister();
    }

    void doRegister() {
        registerAll4("print");
        registerAll4("rev");
        registerAll4("input");
        registerAll4("clone");
        registerAll4("and");
        registerAll4("pop");
        registerAll4("flip");

        registerNumeric("op");

        registerSingle("for", "myFor");
        registerSingle("while", "myWhile");
        registerSingle("if", "myIf");
        registerSingle("run");
        registerSingle("afor", "arglessMyFor");
        registerSingle("awhile", "arglessMyFor");
        registerSingle("aif", "arglessMyFor");
        registerSingle("arun");
        registerSingle("merge");
        registerSingle("not");
    }

    public void start() {
        for (String line : lines) executeLine(line);
    }

    public void read() {
        try {

            File file;
            if (!dir.equals("")) {
                file = new File(dir + "/" + name + ".stkr");
            } else {
                file = new File(name + ".stkr");
            }
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (! (line.equalsIgnoreCase("")))
                    lines.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            if (name.equals("main"))
                System.out.println("Main file (main.stkr) not found!");
            else
                System.out.println("File " + name + ".stkr not found!");
        }
    }

    void registerAll4(String name) {
        String ends = "ifsb";
        for (char end : ends.toCharArray()) {
            try {
                arglessFunctions.put(name + end, this.getClass().getDeclaredMethod(name + end));
            } catch (NoSuchMethodException e) {
                System.out.println("No method with name " + name + end);
            }
        }
    }

    void registerNumeric(String name) {
        String ends = "if";
        for (char end : ends.toCharArray()) {
            try {
                arglessFunctions.put(name + end, this.getClass().getDeclaredMethod(name + end));
            } catch (NoSuchMethodException e) {
                System.out.println("No method with name " + name + end);
            }
        }
    }

    void registerSingle(String name) {
        try {
            arglessFunctions.put(name, this.getClass().getDeclaredMethod(name));
        } catch (NoSuchMethodException e) {
            System.out.println("No method with name " + name);
        }
    }

    void registerSingle(String name, String method) {
        try {
            arglessFunctions.put(name, this.getClass().getDeclaredMethod(method));
        } catch (NoSuchMethodException e) {
            System.out.println("No method with name " + method);
        }
    }

    public void executeLine(String line) {
        lineNumber++;

        if (line.startsWith("#") || line.equals("")) return;

        if (line.startsWith("if_")) {
            if (!boolStack.pop()) return;
            line = line.replace("if_", "");
        }

        if (line.equals("stop")) {
            System.exit(0);
        }

        // explicit stack functions
        if (line.startsWith("pushs")) {
            pushs(line.replace("pushs ", ""));
            return;
        }

        if (line.startsWith("pushi")) {
            pushi(toInt(
                    firstArg(line, "pushi")));
            return;
        }

        if (line.startsWith("pushf")) {
            pushf(toFloat(
                    firstArg(line, "pushf")));
            return;
        }

        if (line.startsWith("pushb")) {
            pushb(toBool(
                    firstArg(line, "pushb")));
            return;
        }

        if (line.startsWith("s_pushs")) {
            caller.pushs(line.replace("s_pushs ", ""));
            return;
        }

        if (line.startsWith("s_pushi")) {
            caller.pushi(toInt(
                    firstArg(line, "s_pushi")));
            return;
        }

        if (line.startsWith("s_pushf")) {
            caller.pushf(toFloat(
                    firstArg(line, "s_pushf")));
            return;
        }

        if (line.startsWith("s_pushb")) {
            caller.pushb(toBool(
                    firstArg(line, "s_pushb")));
            return;
        }

        if (line.startsWith("m_pushs")) {
            main.pushs(line.replace("m_pushs ", ""));
            return;
        }

        if (line.startsWith("m_pushi")) {
            main.pushi(toInt(
                    firstArg(line, "m_pushi")));
            return;
        }

        if (line.startsWith("m_pushf")) {
            main.pushf(toFloat(
                    firstArg(line, "m_pushf")));
            return;
        }

        if (line.startsWith("m_pushb")) {
            main.pushb(toBool(
                    firstArg(line, "m_pushb")));
            return;
        }

        if (line.equals("printn")) {
            System.out.println();
        }

        if (arglessFunctions.containsKey(line)) {
            try {
                arglessFunctions.get(line).invoke(this);
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
            return;
        }

        if (arglessFunctions.containsKey(line.replace("s_", "")) &&
                caller != null) {
            try {
                arglessFunctions.get(line).invoke(caller);
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
            return;
        }

        if (arglessFunctions.containsKey(line.replace("m_", ""))) {
            try {
                arglessFunctions.get(line).invoke(main);
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }

    }

    private void cloneb() {
        boolStack.push(boolStack.peek());
    }

    private void clonef() {
        floatStack.push(floatStack.peek());
    }

    private void clonei() {
        intStack.push(intStack.peek());
    }

    private void clones() {
        strStack.push(strStack.peek());
    }

    private void prints() {
        System.out.print(strStack.pop());
    }

    private void printi() {
        System.out.print(intStack.pop());
    }

    private void printf() {
        System.out.print(floatStack.pop());
    }

    private void printb() {
        System.out.print(boolStack.pop());
    }

    private void merge() {
        List<String> toMerge = new ArrayList<>();

        int count = intStack.pop();
        for (int i = 0; i < count; i++) {
            toMerge.add(strStack.pop());
        }

        strStack.push(String.join(" ", toMerge));
    }

    private void pushi(int value) {
        intStack.push(value);
    }

    public void pushs(String value) {
        strStack.push(value);
    }

    public void pushf(float value) {
        floatStack.push(value);
    }

    public void pushb(boolean value) {
        boolStack.push(value);
    }

    public void popi() {
        intStack.pop();
    }

    public void popf() {
        floatStack.pop();
    }

    public void pops() {
        strStack.pop();
    }

    public void popb() {
        boolStack.pop();
    }

    public void revs() {
        Collections.reverse(strStack);
    }

    public void revi() {
        Collections.reverse(intStack);
    }

    public void revf() {
        Collections.reverse(floatStack);
    }

    public void revb() {
        Collections.reverse(boolStack);
    }

    public void inputs() {
        System.out.print(strStack.pop() + " \u001B[32m");
        Scanner scanner = new Scanner(System.in);
        strStack.push(scanner.nextLine());
        System.out.print("\u001B[37m");
    }

    public void inputi() {
        String prompt = strStack.pop() + " \u001B[32m";
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String next = scanner.nextLine();
            try {
                int val = Integer.parseInt(next);
                intStack.push(val);
                break;
            } catch (Exception e) {
                System.out.println("\u001B[37m" + next + " isn't a valid integer");
                System.out.print(prompt);
            }
        }
        System.out.print("\u001B[37m");
    }

    public void inputf() {
        String prompt = strStack.pop() + " \u001B[32m";
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String next = scanner.nextLine();
            try {
                float val = Float.parseFloat(next);
                floatStack.push(val);
                break;
            } catch (Exception e) {
                System.out.println("\u001B[37m" + next + " isn't a valid value");
                System.out.print(prompt);
            }
        }
        System.out.print("\u001B[37m");
    }

    public void inputb() {
        String prompt = strStack.pop() + " \u001B[32m";
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String next = scanner.nextLine();

            if (!next.equals("true") && !next.equals("false")) {
                System.out.println("\u001B[37m" + next + " isn't a valid integer");
                System.out.print(prompt);
            } else {
                boolean val = Boolean.parseBoolean(next);
                boolStack.push(val);
                break;
            }
        }
        System.out.print("\u001B[37m");
    }

    public void opi() {
        int a = intStack.pop();
        int b = intStack.pop();
        String operation = strStack.pop();
        switch (operation) {
            case "+":
                boolStack.push(true);
                intStack.push(a + b);
                break;
            case "-":
                boolStack.push(true);
                intStack.push(a - b);
                break;
            case "*":
            case "x":
                boolStack.push(true);
                intStack.push(a * b);
                break;
            case "/":
                boolStack.push(true);
                intStack.push(a / b);
                break;
            case "%":
                boolStack.push(true);
                intStack.push(a % b);
                break;
            case "^":
                boolStack.push(true);
                intStack.push(a ^ b);
                break;
            case ">":
                boolStack.push(false);
                boolStack.push(a > b);
                break;
            case "<":
                boolStack.push(false);
                boolStack.push(a < b);
                break;
            case ">=":
                boolStack.push(false);
                boolStack.push(a >= b);
                break;
            case "<=":
                boolStack.push(false);
                boolStack.push(a <= b);
                break;
        }
    }

    public void opf() {
        float a = floatStack.pop();
        float b = floatStack.pop();
        String operation = strStack.pop();
        switch (operation) {
            case "+":
                boolStack.push(true);
                floatStack.push(a + b);
                break;
            case "-":
                boolStack.push(true);
                floatStack.push(a - b);
                break;
            case "*":
            case "x":
                boolStack.push(true);
                floatStack.push(a * b);
                break;
            case "/":
                boolStack.push(true);
                floatStack.push(a / b);
                break;
            case "%":
                boolStack.push(true);
                floatStack.push(a % b);
                break;
            case "^":
                boolStack.push(true);
                floatStack.push((float) Math.pow(a, b));
                break;
            case ">":
                boolStack.push(false);
                boolStack.push(a > b);
                break;
            case "<":
                boolStack.push(false);
                boolStack.push(a < b);
                break;
            case ">=":
                boolStack.push(false);
                boolStack.push(a >= b);
                break;
            case "<=":
                boolStack.push(false);
                boolStack.push(a <= b);
                break;
        }
    }

    private void run() {
        int ints = intStack.pop();
        int flts = intStack.pop();
        int strs = intStack.pop();
        int bools = intStack.pop();

        Stack<Integer> integers = new Stack<>();
        Stack<Float> floats = new Stack<>();
        Stack<String> strings = new Stack<>();
        Stack<Boolean> booleans = new Stack<>();

        for (int i = 0; i < ints; i++) {
            integers.push(intStack.pop());
        }

        for (int i = 0; i < flts; i++) {
            floats.push(floatStack.pop());
        }

        for (int i = 0; i < strs; i++) {
            strings.push(strStack.pop());
        }

        for (int i = 0; i < bools; i++) {
            booleans.push(boolStack.pop());
        }

        StackerFunction toRun;
        if (!dir.equals("")) {
            toRun = new StackerFunction(strStack.pop(), dir);
        } else {
            toRun = new StackerFunction(strStack.pop());
        }

        toRun.intStack = integers;
        toRun.floatStack = floats;
        toRun.strStack = strings;
        toRun.boolStack = booleans;

        toRun.caller = this;
        toRun.start();
    }

    private void arun() {
        StackerFunction toRun;
        if (!dir.equals("")) {
            toRun = new StackerFunction(strStack.pop(), dir);
        } else {
            toRun = new StackerFunction(strStack.pop());
        }
        toRun.caller = this;
        toRun.start();
    }

    public void myWhile() {
        int ints = intStack.pop();
        int flts = intStack.pop();
        int strs = intStack.pop();
        int bools = intStack.pop();

        Stack<Integer> integers = new Stack<>();
        Stack<Float> floats = new Stack<>();
        Stack<String> strings = new Stack<>();
        Stack<Boolean> booleans = new Stack<>();

        for (int i = 0; i < ints; i++) {
            integers.push(intStack.pop());
        }

        for (int i = 0; i < flts; i++) {
            floats.push(floatStack.pop());
        }

        for (int i = 0; i < strs; i++) {
            strings.push(strStack.pop());
        }

        for (int i = 0; i < bools; i++) {
            booleans.push(boolStack.pop());
        }

        StackerFunction toRun;
        if (!dir.equals("")) {
            toRun = new StackerFunction(strStack.pop(), dir);
        } else {
            toRun = new StackerFunction(strStack.pop());
        }
        toRun.caller = this;
        while (boolStack.pop()) {
            toRun.intStack = integers;
            toRun.floatStack = floats;
            toRun.strStack = strings;
            toRun.boolStack = booleans;
            toRun.start();
        }
    }

    public void myIf() {
        if (!boolStack.pop()) return;
        
        int ints = intStack.pop();
        int flts = intStack.pop();
        int strs = intStack.pop();
        int bools = intStack.pop();

        Stack<Integer> integers = new Stack<>();
        Stack<Float> floats = new Stack<>();
        Stack<String> strings = new Stack<>();
        Stack<Boolean> booleans = new Stack<>();

        for (int i = 0; i < ints; i++) {
            integers.push(intStack.pop());
        }

        for (int i = 0; i < flts; i++) {
            floats.push(floatStack.pop());
        }

        for (int i = 0; i < strs; i++) {
            strings.push(strStack.pop());
        }

        for (int i = 0; i < bools; i++) {
            booleans.push(boolStack.pop());
        }

        StackerFunction toRun;
        if (!dir.equals("")) {
            toRun = new StackerFunction(strStack.pop(), dir);
        } else {
            toRun = new StackerFunction(strStack.pop());
        }

        toRun.intStack = integers;
        toRun.floatStack = floats;
        toRun.strStack = strings;
        toRun.boolStack = booleans;
        toRun.caller = this;
        toRun.start();
    }

    public void myFor() {
        int count = intStack.pop();
        
        int ints = intStack.pop();
        int flts = intStack.pop();
        int strs = intStack.pop();
        int bools = intStack.pop();

        Stack<Integer> integers = new Stack<>();
        Stack<Float> floats = new Stack<>();
        Stack<String> strings = new Stack<>();
        Stack<Boolean> booleans = new Stack<>();

        for (int i = 0; i < ints; i++) {
            integers.push(intStack.pop());
        }

        for (int i = 0; i < flts; i++) {
            floats.push(floatStack.pop());
        }

        for (int i = 0; i < strs; i++) {
            strings.push(strStack.pop());
        }

        for (int i = 0; i < bools; i++) {
            booleans.push(boolStack.pop());
        }

        StackerFunction toRun;
        if (!dir.equals("")) {
            toRun = new StackerFunction(strStack.pop(), dir);
        } else {
            toRun = new StackerFunction(strStack.pop());
        }
        toRun.caller = this;
        for (int i = 0; i < count; i++) {
            toRun.intStack = integers;
            toRun.floatStack = floats;
            toRun.strStack = strings;
            toRun.boolStack = booleans;
            toRun.intStack.push(i);
            toRun.start();
        }
    }
    
    public void arglessMyWhile() {
        String funcName = strStack.pop();
        StackerFunction toRun;
        while (boolStack.pop()) {
            if (!dir.equals("")) {
                toRun = new StackerFunction(funcName, dir);
            } else {
                toRun = new StackerFunction(funcName);
            }
            toRun.start();
        }
    }
    
    public void arglessMyFor() {
        String funcName = strStack.pop();
        StackerFunction toRun;
        int count = intStack.pop();
        for (int i = 0; i < count; i++) {
            if (!dir.equals("")) {
                toRun = new StackerFunction(funcName, dir);
            } else {
                toRun = new StackerFunction(funcName);
            }
            toRun.intStack.push(i);
            toRun.start();
        }
    }
    
    public void arglessMyIf() {
        String funcName = strStack.pop();

        if (!boolStack.pop()) return;
        
        StackerFunction toRun;
        if (!dir.equals("")) {
            toRun = new StackerFunction(funcName, dir);
        } else {
            toRun = new StackerFunction(funcName);
        }
        toRun.start();
    }
    
    public void andi() {
        boolStack.push(
                intStack.pop().equals(intStack.pop())
        );
    }

    public void andf() {
        boolStack.push(
                floatStack.pop().equals(floatStack.pop())
        );
    }

    public void ands() {
        boolStack.push(
                strStack.pop().equals(strStack.pop())
        );
    }
    
    public void andb() {
        boolStack.push(
                boolStack.pop().equals(boolStack.pop())
        );
    }

    public void not() {
        boolStack.push(!boolStack.pop());
    }
    
    public void flipi() {
        int a = intStack.pop();
        int b = intStack.pop();
        intStack.push(a);
        intStack.push(b);
    }
    
    public void flipf() {
        float a = floatStack.pop();
        float b = floatStack.pop();
        floatStack.push(a);
        floatStack.push(b);
    }
    
    public void flips() {
        String a = strStack.pop();
        String b = strStack.pop();
        strStack.push(a);
        strStack.push(b);
    }

    public void flipb() {
        boolean a = boolStack.pop();
        boolean b = boolStack.pop();
        boolStack.push(a);
        boolStack.push(b);
    }

    String[] getArgs(String line, String command) {
        return line.replace(command + " ", "").split(" ");
    }

    String firstArg(String line, String command) {
        return getArgs(line, command)[0];
    }

    int toInt(String value) {
        return Integer.parseInt(value);
    }

    float toFloat(String value) {
        return Float.parseFloat(value);
    }

    boolean toBool(String value) {
        return Boolean.parseBoolean(value);
    }
}