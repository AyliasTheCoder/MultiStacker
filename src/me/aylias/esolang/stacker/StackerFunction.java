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
        if (line.startsWith("stacks")) {
            stacks(firstArg(line, "stacks"));
            return;
        }

        if (line.startsWith("stacki")) {
            stacki(toInt(
                    firstArg(line, "stacki")));
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

        if (line.equals("revs")) {
            revs();
        }
    }

    private void prints() {
        System.out.println(strStack.pop());
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

    public void revs() {
        Collections.reverse(strStack);
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
}
