/*
 * Copyright (C) 2023 peter.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.fimalib.calc.formula;

import org.fimalib.calc.Boolean;
import org.fimalib.calc.Complex;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.functions.*;
import org.fimalib.calc.formula.nodes.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

/**
 * This class is the base class for all subclasses making up a formula tree.
 * The tree represents a mathematical formula 
 * @author peter
 */
public class Formula {
    public static ArrayList<Function> userDefinedFunctions = new ArrayList<>();
    
    public static Node parse(String formula) throws FormulaException {
        return parse(formula, NumberFormat.getInstance());
    }
    
    public static Node parse(String formula, NumberFormat format) throws FormulaException {
        int len = formula.length();
        StringBuilder leftstr = new StringBuilder("");
        Stack<String> brackets = new Stack<>();
        ArrayList<String> segments = new ArrayList<>();
        Stack<Node> nodeStack = new Stack<>();
        
        // First, parse for + or - and ensure correct handling of brackets
        for(int i=0; i<len; i++) {
            char c = formula.charAt(i);
            
            switch (c) {
                case '(':
                case '[':
                case '{':
                    leftstr.append(c);
                    brackets.push("" + c);
                    break;
                    
                case ')':
                case ']':
                case '}':
                    leftstr.append(c);
                    char open = brackets.peek().charAt(0);
                    if(((open == '(') && (c == ')')) || ((open == '[') && (c == ']')) ||
                       ((open == '{') && (c == '}'))) {
                        brackets.pop();
                    }
                    else
                        throw new FormulaException("Syntax error, bracket " + brackets.pop() + " cannot be closed with " + c);
                    break;
                    
                case '=':   // Equals is '=='
                    if(formula.charAt(i+1) == '=') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("==");
                        }
                        else
                            leftstr.append("==");
                    }
                    break;
                    
                case '!':   // Not is '!', Not equals is '!='
                    if(formula.charAt(i+1) == '=') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("!=");
                        }
                        else
                            leftstr.append("!=");
                    }
                    else {
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("!");
                        }
                    }
                    break;
                    
                case '>':   // Greater than is '>', greater or equal is '>='
                    if(formula.charAt(i+1) == '=') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add(">=");
                        }
                        else
                            leftstr.append(">=");
                    }
                    else {
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add(">");
                        }
                        else
                            leftstr.append(">");
                    }
                    break;
                    
                case '<':   // Less than is '<', less or equal is '<='
                    if(formula.charAt(i+1) == '=') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("<=");
                        }
                        else
                            leftstr.append("<=");
                    }
                    else {
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("<");
                        }
                        else
                            leftstr.append("<");
                    }
                    break;
                    
                case '&':   // And is '&&'
                    if(formula.charAt(i+1) == '&') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("&&");
                        }
                        else
                            leftstr.append("&&");
                    }
                    break;
                    
                case '|':   // Or is '||'
                    if(formula.charAt(i+1) == '|') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("||");
                        }
                        else
                            leftstr.append("||");
                    }
                    break;
                    
                case '#':   // Xor is '##'
                    if(formula.charAt(i+1) == '#') {
                        i++;
                        if(brackets.isEmpty()) {
                            if(leftstr.length() != 0) {
                                segments.add(leftstr.toString());
                                leftstr = new StringBuilder("");
                            }
                            segments.add("##");
                        }
                        else
                            leftstr.append("##");
                    }
                    break;
                    
                case ' ':
                case '\13':
                case '\10':
                    // Just ommit whitespace
                    break;
                        
                default:
                    leftstr.append(c);
                    break;
            }
        }
        
        if(leftstr.length() > 0) {
            segments.add(leftstr.toString());
        }
        
        if(segments.isEmpty())
            throw new FormulaException("Syntax error in Formula >" + formula + "<");
        
        int pos = 0;
        while(pos < segments.size()) {
            String segment = segments.get(pos++);
            
            switch (segment) {
                case "==":
                    Node equals = new Equals();
                    equals.setNode(0, nodeStack.pop());
                    String nextSegmentEquals = segments.get(pos++);
                    equals.setNode(1, parseAdd(nextSegmentEquals, format));
                    nodeStack.push(equals);
                    break;
                    
                case "!=":
                    Node notEquals = new NotEquals();
                    notEquals.setNode(0, nodeStack.pop());
                    String nextSegmentNotEquals = segments.get(pos++);
                    notEquals.setNode(1, parseAdd(nextSegmentNotEquals, format));
                    nodeStack.push(notEquals);
                    break;
                    
                case ">":
                    Node greater = new GreaterThan();
                    greater.setNode(0, nodeStack.pop());
                    String nextSegmentGreater = segments.get(pos++);
                    greater.setNode(1, parseAdd(nextSegmentGreater, format));
                    nodeStack.push(greater);
                    break;
                    
                case "<":
                    Node less = new LessThan();
                    less.setNode(0, nodeStack.pop());
                    String nextSegmentLess = segments.get(pos++);
                    less.setNode(1, parseAdd(nextSegmentLess, format));
                    nodeStack.push(less);
                    break;
                    
                case ">=":
                    Node greaterEquals = new GreaterEquals();
                    greaterEquals.setNode(0, nodeStack.pop());
                    String nextSegmentGreaterEquals = segments.get(pos++);
                    greaterEquals.setNode(1, parseAdd(nextSegmentGreaterEquals, format));
                    nodeStack.push(greaterEquals);
                    break;
                    
                case "<=":
                    Node lessEquals = new LessEquals();
                    lessEquals.setNode(0, nodeStack.pop());
                    String nextSegmentLessEquals = segments.get(pos++);
                    lessEquals.setNode(1, parseAdd(nextSegmentLessEquals, format));
                    nodeStack.push(lessEquals);
                    break;
                    
                case "!":
                    // TODO: Code this!
                    break;
                    
                case "&&":
                    Node and = new And();
                    and.setNode(0, nodeStack.pop());
                    String nextSegmentAnd = segments.get(pos++);
                    and.setNode(1, parseAdd(nextSegmentAnd, format));
                    nodeStack.push(and);
                    break;
                    
                case "||":
                    Node or = new Or();
                    or.setNode(0, nodeStack.pop());
                    String nextSegmentOr = segments.get(pos++);
                    or.setNode(1, parseAdd(nextSegmentOr, format));
                    nodeStack.push(or);
                    break;
                    
                case "##":
                    Node xor = new Xor();
                    xor.setNode(0, nodeStack.pop());
                    String nextSegmentXor = segments.get(pos++);
                    xor.setNode(1, parseAdd(nextSegmentXor, format));
                    nodeStack.push(xor);
                    break;
                    
                default:
                    Node lastNode = parseAdd(segment, format);
                    nodeStack.push(lastNode);
                    break;
            }
        }
        
        return nodeStack.pop();
    }
    
    protected static Node parseAdd(String formula, NumberFormat format) throws FormulaException {
        int len = formula.length();
        StringBuilder leftstr = new StringBuilder("");
        Stack<String> brackets = new Stack<>();
        ArrayList<String> segments = new ArrayList<>();
        Stack<Node> nodeStack = new Stack<>();
        
        // First, parse for + or - and ensure correct handling of brackets
        for(int i=0; i<len; i++) {
            char c = formula.charAt(i);
            
            switch (c) {
                case '(':
                case '[':
                case '{':
                    leftstr.append(c);
                    brackets.push("" + c);
                    break;
                    
                case ')':
                case ']':
                case '}':
                    leftstr.append(c);
                    char open = brackets.peek().charAt(0);
                    if(((open == '(') && (c == ')')) || ((open == '[') && (c == ']')) ||
                       ((open == '{') && (c == '}'))) {
                        brackets.pop();
                    }
                    else
                        throw new FormulaException("Syntax error, bracket " + brackets.pop() + " cannot be closed with " + c);
                    break;
                    
                case '+':
                case '-':
                    if(brackets.isEmpty()) {
                        if(leftstr.length() != 0) {
                            segments.add(leftstr.toString());
                            leftstr = new StringBuilder("");
                        }
                        segments.add(""+c);
                    }
                    else 
                        leftstr.append(c);
                    break;
                    
                case ' ':
                case '\13':
                case '\10':
                    // Just ommit whitespace
                    break;
                        
                default:
                    leftstr.append(c);
                    break;
            }
        }
        
        if(leftstr.length() > 0) {
            segments.add(leftstr.toString());
        }
        
        if(segments.isEmpty())
            throw new FormulaException("Syntax error in Formula >" + formula + "<");
        
        int pos = 0;
        if(segments.get(0).equals("-")) {
            segments.set(1, "neg(" + segments.get(1) + ")");
            pos = 1;
        }
        
        while(pos < segments.size()) {
            String segment = segments.get(pos++);
            
            switch (segment) {
                case "-":
                    Node sub = new Sub();
                    sub.setNode(0, nodeStack.pop());
                    String nextSegment = segments.get(pos++);
                    sub.setNode(1, parseMul(nextSegment, format));
                    nodeStack.push(sub);
                    break;
                    
                case "+":
                    Node add = new Add();
                    add.setNode(0, nodeStack.pop());
                    String nextSegmentAdd = segments.get(pos++);
                    add.setNode(1, parseMul(nextSegmentAdd, format));
                    nodeStack.push(add);
                    break;
                    
                default:
                    Node lastNode = parseMul(segment, format);
                    nodeStack.push(lastNode);
                    break;
            }
        }
        
        return nodeStack.pop();
    }
    
    protected static Node parseMul(String formula, NumberFormat format) throws FormulaException {
        int len = formula.length();
        StringBuilder leftstr = new StringBuilder("");
        Stack<String> brackets = new Stack<>();
        ArrayList<String> segments = new ArrayList<>();
        Stack<Node> nodeStack = new Stack<>();
        
        // First, parse for + or - and ensure correct handling of brackets
        for(int i=0; i<len; i++) {
            char c = formula.charAt(i);
            
            switch (c) {
                case '(':
                case '[':
                case '{':
                    leftstr.append(c);
                    brackets.push("" + c);
                    break;
                    
                case ')':
                case ']':
                case '}':
                    leftstr.append(c);
                    char open = brackets.peek().charAt(0);
                    if(((open == '(') && (c == ')')) || ((open == '[') && (c == ']')) ||
                       ((open == '{') && (c == '}'))) {
                        brackets.pop();
                    }
                    else
                        throw new FormulaException("Syntax error, bracket " + brackets.pop() + " cannot be closed with " + c);
                    break;
                    
                case '*':
                case '/':
                    if(brackets.isEmpty()) {
                        if(leftstr.length() != 0) {
                            segments.add(leftstr.toString());
                            leftstr = new StringBuilder("");
                        }
                        segments.add(""+c);
                    }
                    else 
                        leftstr.append(c);
                    break;
                    
                case ' ':
                case '\13':
                case '\10':
                    // Just ommit whitespace
                    break;
                        
                default:
                    leftstr.append(c);
                    break;
            }
        }
        
        if(leftstr.length() > 0) {
            segments.add(leftstr.toString());
        }
        
        if(segments.isEmpty())
            throw new FormulaException("Syntax error in Formula >" + formula + "<");
        
        int pos = 0;
        while(pos < segments.size()) {
            String segment = segments.get(pos++);
            
            switch (segment) {
                case "*":
                    Node mul = new Mul();
                    mul.setNode(0, nodeStack.pop());
                    String nextSegment = segments.get(pos++);
                    mul.setNode(1, parsePow(nextSegment, format));
                    nodeStack.push(mul);
                    break;
                    
                case "/":
                    Node add = new Div();
                    add.setNode(0, nodeStack.pop());
                    String nextSegmentDiv = segments.get(pos++);
                    add.setNode(1, parsePow(nextSegmentDiv, format));
                    nodeStack.push(add);
                    break;
                    
                default:
                    Node lastNode = parsePow(segment, format);
                    nodeStack.push(lastNode);
                    break;
            }
        }
        
        return nodeStack.pop();
    }
    
    public static Node parsePow(String formula, NumberFormat format) throws FormulaException {
        int len = formula.length();
        StringBuilder leftstr = new StringBuilder("");
        Stack<String> brackets = new Stack<>();
        ArrayList<String> segments = new ArrayList<>();
        Stack<Node> nodeStack = new Stack<>();
        
        // First, parse for + or - and ensure correct handling of brackets
        for(int i=0; i<len; i++) {
            char c = formula.charAt(i);
            
            switch (c) {
                case '(':
                case '[':
                case '{':
                    leftstr.append(c);
                    brackets.push("" + c);
                    break;
                    
                case ')':
                case ']':
                case '}':
                    leftstr.append(c);
                    char open = brackets.peek().charAt(0);
                    if(((open == '(') && (c == ')')) || ((open == '[') && (c == ']')) ||
                       ((open == '{') && (c == '}'))) {
                        brackets.pop();
                    }
                    else
                        throw new FormulaException("Syntax error, bracket " + brackets.pop() + " cannot be closed with " + c);
                    break;
                    
                case '^':
                    if(brackets.isEmpty()) {
                        if(leftstr.length() != 0) {
                            segments.add(leftstr.toString());
                            leftstr = new StringBuilder("");
                        }
                        segments.add(""+c);
                    }
                    else 
                        leftstr.append(c);
                    break;
                    
                case ' ':
                case '\13':
                case '\10':
                    // Just ommit whitespace
                    break;
                        
                default:
                    leftstr.append(c);
                    break;
            }
        }
        
        if(leftstr.length() > 0) {
            segments.add(leftstr.toString());
        }
        
        if(segments.isEmpty())
            throw new FormulaException("Syntax error in Formula >" + formula + "<");
        
        int pos = 0;
        while(pos < segments.size()) {
            String segment = segments.get(pos++);
            
            switch (segment) {
                case "^":
                    Node pow = new Pow();
                    pow.setNode(0, nodeStack.pop());
                    String nextSegment = segments.get(pos++);
                    pow.setNode(1, parseFunc(nextSegment, format));
                    nodeStack.push(pow);
                    break;
                    
                default:
                    Node lastNode = parseFunc(segment, format);
                    nodeStack.push(lastNode);
                    break;
            }
        }
        
        return nodeStack.pop();
    }
    
    public static Node parseFunc(String formula, NumberFormat format) throws FormulaException {
        char c1 = formula.charAt(0);
        char cl = formula.charAt(formula.length() - 1);
        
        if(((c1 == '(') && (cl == ')')) || ((c1 == '[') && (cl == ']')) ||
           ((c1 == '{') && (cl == '}'))) {
            String subStr = formula.substring(1, formula.length()-1);
            
            return parse(subStr, format);
        }
        
        if((c1 == '(') || (c1 == '[') || (c1 == '{'))
            throw new FormulaException("Error, bracket not closed: " + formula);
        
        // Now check for function names:
        // Start with the longest function names first, this avoids errors!
        
        if(formula.startsWith("arcsinh")) return new Arcsinh(parse(formula.substring(7), format));
        if(formula.startsWith("arccosh")) return new Arccosh(parse(formula.substring(7), format));
        if(formula.startsWith("arctanh")) return new Arctanh(parse(formula.substring(7), format));
        if(formula.startsWith("arccoth")) return new Arccoth(parse(formula.substring(7), format));
        if(formula.startsWith("arcsech")) return new Arcsech(parse(formula.substring(7), format));
        if(formula.startsWith("arccsch")) return new Arccsch(parse(formula.substring(7), format));

        if(formula.startsWith("arcsin")) return new Arcsin(parse(formula.substring(6), format));
        if(formula.startsWith("arccos")) return new Arccos(parse(formula.substring(6), format));
        if(formula.startsWith("arctan")) return new Arctan(parse(formula.substring(6), format));
        if(formula.startsWith("arccot")) return new Arccot(parse(formula.substring(6), format));
        if(formula.startsWith("arcsec")) return new Arcsec(parse(formula.substring(6), format));
        if(formula.startsWith("arccsc")) return new Arccsc(parse(formula.substring(6), format));
        
        if(formula.startsWith("sinh")) return new Sinh(parse(formula.substring(4), format));
        if(formula.startsWith("cosh")) return new Cosh(parse(formula.substring(4), format));
        if(formula.startsWith("tanh")) return new Tanh(parse(formula.substring(4), format));
        if(formula.startsWith("coth")) return new Coth(parse(formula.substring(4), format));
        if(formula.startsWith("sech")) return new Sech(parse(formula.substring(4), format));
        if(formula.startsWith("csch")) return new Csch(parse(formula.substring(4), format));

        if(formula.startsWith("sqrt")) return new Sqrt(parse(formula.substring(4), format));

        if(formula.startsWith("sin")) return new Sin(parse(formula.substring(3), format));
        if(formula.startsWith("cos")) return new Cos(parse(formula.substring(3), format));
        if(formula.startsWith("tan")) return new Tan(parse(formula.substring(3), format));
        if(formula.startsWith("cot")) return new Cot(parse(formula.substring(3), format));
        if(formula.startsWith("sec")) return new Sec(parse(formula.substring(3), format));
        if(formula.startsWith("csc")) return new Csc(parse(formula.substring(3), format));
        
        if(formula.startsWith("abs")) return new Abs(parse(formula.substring(3), format));
        if(formula.startsWith("sgn")) return new Sgn(parse(formula.substring(3), format));
        if(formula.startsWith("exp")) return new Exp(parse(formula.substring(3), format));
        if(formula.startsWith("log")) return new Log(parse(formula.substring(3), format));
        if(formula.startsWith("neg")) return new Neg(parse(formula.substring(3), format));

        if(formula.startsWith("ln")) return new Ln(parse(formula.substring(2), format));
        
        // Browse through all system defined functions
        Function systemFunc = Function.getFunction(formula, format);
        if(systemFunc != null) return systemFunc;
        
        // Browse through all user defined functions
        for(Function func : userDefinedFunctions) {
            if(formula.startsWith(func.getName())) {
                return func;
            }
        }
        
        // true or false?
        if(formula.equals("true") || formula.equals("false"))
            return new Constant(new Boolean(formula, format));
        
        // is it a variable?
        if(isVariable(formula)) {
            Variable var = new Variable(formula);
            return var;
        }
        
        
        // If nothing has matched so far, it must be a number!
        try {
            if(formula.endsWith("i")) {
                Complex number = new Complex(formula, format);
                return new Constant(number);
            }
            else {
                Double number = new Double(formula, format);
                return new Constant(number);
            }
        }
        catch (ParseException ex) {
            throw new FormulaException("Cannot parse number " + formula, ex);
        }
    }
    
    protected static boolean isVariable(String formula) {
        int len = formula.length();
        
        for(int i=0; i<len; i++) {
            char c = formula.charAt(i);
            
            if((c<'a') || (c>'z')) return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) throws Exception {
        //String formula = "(15-2+4^2*3+8+x^2+sin(1)^tan(x))/(-5)";
        String formula = "if(x<5,if(x>2,1,2),3)";
        Node node = parse(formula, NumberFormat.getInstance(Locale.US));
        
        HashMap<String,Number> parameters = new HashMap<>();
        parameters.put("x", new Complex(0,1, NumberFormat.getInstance(Locale.US)));
        System.out.println("result: " + node.calculate(parameters));
        System.out.println("back: " + node.toString());
    }
}
