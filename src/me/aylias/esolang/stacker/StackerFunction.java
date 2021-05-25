package me.aylias.esolang.stacker;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class StackerFunction {

    final String name;
    List<String> lines = new ArrayList<>();

    Stack<String> strStack = new Stack<>();
    Stack<Integer> intStack = new Stack<>();
    Stack<Float> floatStack = new Stack<>();
    Stack<Boolean> boolStack = new Stack<>();
    int lineNumber = 0;

    public StackerFunction(String name) {
        this.name = name;
        read();
        for (String line : lines) executeLine(line);
    }

    public void read() {
        try {
            File file = new File(name + ".stkr");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (! (line.equalsIgnoreCase("")))
                    lines.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void executeLine(String line) {
        lineNumber++;
        if (line.startsWith("stacks")) {
            stacks(firstArg(line, "stacks"));
            return;
        }

        // explicit stack functions

        if (line.startsWith("stacki")) {
            stacki(toInt(
                    firstArg(line, "stacki")));
            return;
        }

        if (line.startsWith("stackf")) {
            stackf(toFloat(
                    firstArg(line, "stackf")));
            return;
        }

        if (line.startsWith("stackb")) {
            stackb(toBool(
                    firstArg(line, "stackf")));
            return;
        }

        if (line.equals("merge")) {
            merge();
            return;
        }

        if (line.equals("prints")) {
            prints();
            return;
        }

        if (line.equals("printi")) {
            printi();
            return;
        }

        if (line.equals("printf")) {
            printf();
            return;
        }

        if (line.equals("printb")) {
            printb();
            return;
        }

        if (line.equals("revs")) {
            revs();
            return;
        }

        if (line.equals("revi")) {
            revi();
            return;
        }

        if (line.equals("revf")) {
            revf();
            return;
        }

        if (line.equals("revb")) {
            revb();
            return;
        }

        if (line.equals("inputs")) {
            inputs();
            return;
        }

        if (line.equals("inputi")) {
            inputi();
            return;
        }

        if (line.equals("inputf")) {
            inputf();
            return;
        }

        if (line.equals("inputb")) {
            inputs();
            return;
        }

        if (line.equals("clones")) {
            clones();
            return;
        }

        if (line.equals("clonei")) {
            clonei();
            return;
        }

        if (line.equals("clonef")) {
            clonef();
            return;
        }

        if (line.equals("cloneb")) {
            cloneb();
            return;
        }

        if (line.equals("opi")) {
            opi();
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
        System.out.println(strStack.pop());
    }

    private void printi() {
        System.out.println(intStack.pop());
    }

    private void printf() {
        System.out.println(floatStack.pop());
    }

    private void printb() {
        System.out.println(boolStack.pop());
    }

    private void merge() {
        List<String> toMerge = new ArrayList<>();

        int count = intStack.pop();
        for (int i = 0; i < count; i++) {
            toMerge.add(strStack.pop());
        }

        strStack.push(String.join(" ", toMerge));
    }

    private void stacki(int value) {
        intStack.push(value);
    }

    public void stacks(String value) {
        strStack.push(value);
    }

    public void stackf(float value) {
        floatStack.push(value);
    }

    public void stackb(boolean value) {
        boolStack.push(value);
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

    public void opi() {
        int a = intStack.pop();
        int b = intStack.pop();
        String operation = strStack.pop();
        switch (operation) {
            case "+":
                intStack.push(a + b);
                break;
            case "-":
                intStack.push(a - b);
                break;
            case "x":
                intStack.push(a * b);
                break;
            case "/":
                intStack.push(a / b);
                break;
        }
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
