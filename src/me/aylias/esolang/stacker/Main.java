package me.aylias.esolang.stacker;

public class Main {

    public static StackerFunction mainFunc;
    public static void main(String[] args) {
        if (args.length > 0)
            mainFunc = new StackerFunction("main", String.join(" ", args));
        else
            mainFunc = new StackerFunction("main");

        StackerFunction.main = mainFunc;
        mainFunc.start();
    }
}