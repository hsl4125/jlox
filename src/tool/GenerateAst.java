package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class GenerateAst {
    private static final String Tab = "   ";
    private static final String Tab2 = Tab + Tab;
    private static final String Tab3 = Tab2 + Tab;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal : Object value",
                "Unary : Token operator, Expr right"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Expression : Expr expression",
                "Print : Expr expression"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException{
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // AST
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // the base accept() method
        writer.println();
        writer.println(Tab + "abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println(Tab + "interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(Tab2 + "R visit" + typeName + baseName + "(" +
                            typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println(Tab + "}");
        writer.println();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(Tab + "static class " + className + " extends " +
                baseName + " {");

        // Constructor
        writer.println(Tab2 + className + "(" + fieldList + ") {");

        // Store parameter in fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1].trim();
            writer.println(Tab3 + "this." + name + " = " + name + ";");
        }

        writer.println(Tab2 + "}");

        // Visitor pattern
        writer.println();
        writer.println(Tab2 + "@Override");
        writer.println(Tab2 + "<R> R accept(Visitor<R> visitor) {");
        writer.println(Tab3 + "return visitor.visit" +
                className + baseName + "(this);");
        writer.println(Tab2 + "}");

        // Fields
        writer.println();
        for (String field : fields) {
            writer.println(Tab2 + "final " + field + ";");
        }

        writer.println(Tab + "}");
        writer.println();
    }
}
