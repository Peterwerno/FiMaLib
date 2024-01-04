/*
 * Copyright (C) 2024 peter.
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
package org.fimalib.calc.formula.functions;

import java.text.NumberFormat;
import org.fimalib.calc.formula.FormulaException;
import org.fimalib.calc.formula.nodes.Node;
import java.util.ArrayList;
import java.util.HashMap;
import org.fimalib.calc.formula.Formula;

/**
 * This class is used as the base class for all pre-defined functions.
 * It provides a generic framework for functions taking up multiple parameters
 * 
 * @author Peter Werno
 */
public abstract class Function extends Node {
    String[] parameterNames;
    Node[] parameterNodes;
    
    public Function(int numNodes) {
        super(numNodes);
        
        this.parameterNames = new String[numNodes];
        this.parameterNodes = new Node[numNodes];
    }
    
    public Function() {
        super(0);
    }
    
    public void setParameterNode(int num, Node node) {
        this.parameterNodes[num] = node;
    }
    
    public Node getParameterNode(int num) {
        return this.parameterNodes[num];
    }
    
    public Function(String[] parameterNames, Node[] parameterNodes) throws FormulaException {
        super(parameterNames.length);
        
        if(parameterNames.length != parameterNodes.length) 
            throw new FormulaException("Number of parameter names and nodes do not match");
        
        this.parameterNodes = parameterNodes;
    }
    
    @Override
    public void optimize() {
        // Do nothing here
    }
    
    public static Function getFunction(String functionName, NumberFormat format) throws FormulaException {
        try {
            if(functionName.startsWith("if(")) {
                Function ifFunc = new If();
                parseFunction(functionName.substring(2), ifFunc, format);
                return ifFunc;
            }
        }
        catch (Exception ex) {
            return null;
        }
        
        return null;
    }
    
    public static void parseFunction(String functionParameters, Function function) throws FormulaException {
        parseFunction(functionParameters, function, NumberFormat.getInstance());
    }
    
    public static void parseFunction(String functionParameters, Function function, NumberFormat format) throws FormulaException {
        if(functionParameters.startsWith("(") && functionParameters.endsWith(")")) {
            functionParameters = functionParameters.substring(1, functionParameters.length()-1);
        }
        
        int len = functionParameters.length();
        StringBuilder leftstr = new StringBuilder("");
        int brackets = 0;
        int paramCount = 0;
        
        for(int i=0; i<len; i++) {
            char c = functionParameters.charAt(i);
            
            switch (c) {
                case '(':
                case '[':
                case '{':
                    brackets++;
                    leftstr.append(c);
                    break;
                    
                case ')':
                case ']':
                case '}':
                    brackets--;
                    leftstr.append(c);
                    break;
                    
                case ',':
                    if(brackets == 0) {
                        Node paramNode = Formula.parse(leftstr.toString(), format);
                        leftstr = new StringBuilder("");
                        
                        function.setParameterNode(paramCount++, paramNode);
                    }
                    else
                        leftstr.append(c);
                    break;
                    
                default:
                    leftstr.append(c);
                    break;
            }
        }
        
        if(leftstr.length() != 0) {
            Node paramNode = Formula.parse(leftstr.toString(), format);
            function.setParameterNode(paramCount++, paramNode);
        }
    }
    
    public abstract int getMinimumParameters();
    
    public abstract int getMaximumParameters();
    
}
