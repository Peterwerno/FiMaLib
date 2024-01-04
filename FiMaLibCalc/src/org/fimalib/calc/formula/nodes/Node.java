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
package org.fimalib.calc.formula.nodes;

import org.fimalib.calc.formula.FormulaException;
import org.fimalib.calc.Number;
import org.fimalib.calc.FiMaLibCalcException;
import java.util.HashMap;

/**
 * This class is the base class for all formula node classes
 * 
 * @author Peter Werno
 */
public abstract class Node {
    int nodeCount;
    Node[] subNodes;
    
    /**
     * Creates a new instance of Node with a given number of subnodes
     * 
     * @param nodeCount (int) the number of subnodes
     */
    public Node(int nodeCount) {
        this.nodeCount = nodeCount;
        if(this.nodeCount > 0) {
            this.subNodes = new Node[nodeCount];
        }
    }
    
    /**
     * Returns the number of subnodes for the node
     * 
     * @return the number of subnodes (int)
     */
    public int getNodeCount() {
        return this.nodeCount;
    }
    
    /**
     * Returns the required subnode
     * 
     * @param num (int) the node-number
     * @return the subnode (Node)
     * @throws FormulaException 
     */
    public Node getNode(int num) throws FormulaException {
        if((num < 0) || (num >= this.nodeCount))
            throw new FormulaException("Node " + this.getName() + " has only " + nodeCount + " subnodes");
        
        return this.subNodes[num];
    }
    
    /**
     * Sets the subnode
     * 
     * @param num (int) the node-number
     * @param node (Node) the new subnode
     * @throws FormulaException 
     */
    public void setNode(int num, Node node) throws FormulaException {
        if((num < 0) || (num >= this.nodeCount))
            throw new FormulaException("Node " + this.getName() + " has only " + nodeCount + " subnodes");
        
        this.subNodes[num] = node;
    }
    
    /**
     * Returns all sub-nodes in an array.
     * By default, this will only be the left and right subnodes, but may be
     * overwritten by nodes that take more than two subnodes (e.g. functions
     * like min, max sum, etc.)
     * 
     * @return the list of subnodes (Node[])
     * @throws org.fimalib.calc.formula.FormulaException
     */
    public Node[] getSubNodes() throws FormulaException {
        return this.subNodes;
    }
    
    /**
     * Returns if the given node (including all the subnodes) is a "fixed"
     * number.
     * 
     * @return if node is fixed number (boolean)
     * @throws FormulaException 
     */
    public boolean isNumber() throws FormulaException {
        int numNodes = this.getNodeCount();
        for(int i=0; i<numNodes; i++) {
            if(!this.getNode(i).isNumber()) return false;
        }
        
        return true;
    }
    
    /**
     * Optimizes a formula tree by pre-calculating fixed numbers/constant values
     * 
     * @throws FormulaException
     * @throws FiMaLibCalcException 
     */
    public void optimize() throws FormulaException, FiMaLibCalcException {
        for(int i=0; i<this.nodeCount; i++) {
            Node subNode = this.getNode(i);
            if(subNode.isNumber()) {
                Number number = subNode.calculate(null);
                Constant constNode = new Constant(number);
                
                this.setNode(i, constNode);
            }
            else {
                subNode.optimize();
            }
        }
    }

    public abstract Number calculate(HashMap<String, Number> parameters) throws FiMaLibCalcException;
    
    public abstract Node derive(String parameterName) throws FormulaException;
    
    public abstract Node integrate(String parameterName) throws FormulaException;
    
    public abstract String getName();
    
    @Override
    public abstract String toString();
}
