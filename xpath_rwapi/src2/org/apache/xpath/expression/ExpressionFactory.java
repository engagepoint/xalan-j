/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.expression;

import org.apache.xpath.XPathException;
import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.impl.parser.NodeFactory;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Allows the creation of XPath expression either from string representation or
 * by composing expressions.
 */
public interface ExpressionFactory
{
    /**
     * Create a new XPath expression from a string representation
     *
     * @return A XPath expression
     *
     * @throws XPathException whenever the given expression isn't a valid XPath
     *         expression
     */
    public Expr createExpr(String expr) throws XPathException;

	/**
	 * Creates a new XPath sequence
	 * @return A sequence
	 */
	public OperatorExpr createSequence();

    /**
     * Create a new relative or absolute path expression
     *
     * @return An XPath expression of type 'path'
     */
    public PathExpr createPathExpr(boolean isAbsolute);

    /**
     * Create a new step. This step contains no qualifier.
     *
     * @return A step
     */
    public StepExpr createStepExpr(short axisType, NodeTest nodeTest);

    /**
     * Create a name test.
     *
     * @return A name test
     */
    public NodeTest createNameTest(String namespace, String name);

    /**
     * Create a new combining expression of the given type
     *
     * @param type The type of the combining expression to create.
     *
     * @return An XPath expression of type combine
     *
     * @see OperatorExpr#UNION_COMBINE
     * @see OperatorExpr#EXCEPT_COMBINE
     * @see OperatorExpr#INTERSECT_COMBINE
     */
    public OperatorExpr createCombineExpr(short type);

    /**
     * Create a new quantifier expression of type 'some' with one clause
     *
     * @param clause First clause of the new quantifier expression
     *
     * @return An XPath expression of type 'some'
     */
    public ForAndQuantifiedExpr createSomeExpr(Expr clause);

    /**
     * Create a quantifier expression of type 'every'  with one clause
     *
     * @return An XPath expression of type 'every'
     */
    public ForAndQuantifiedExpr createEveryExpr(Expr clause);

    /**
     * Create a logical expression of type 'and' with at least two operands
     *
     * @param operand1 XPath expression to compose
     * @param operand2 XPath expression to compose
     *
     * @return An XPath expression of type 'and'
     */
    public OperatorExpr createAndExpr(Expr operand1, Expr operand2);

    /**
     * Create a new logical expression of type 'or' with at least two operands
     *
     * @param operand1 XPath expression to compose
     * @param operand2 XPath expression to compose
     *
     * @return An XPath expression of type 'or'
     */
    public OperatorExpr createOrExpr(Expr operand1, Expr operand2);

    /**
     * Create a new conditional expression
     *
     * @param test The boolean XPath expression
     * @param thenExpr The XPath expression uses in the then clause
     * @param elseExpr The XPath expression uses in the else clause
     *
     * @return An XPath expression of type 'if'
     */
    public ConditionalExpr createIfExpr(Expr test, Expr thenExpr, Expr elseExpr);

    /**
     * Create a new for expression
     *
     * @param varName The name of the binding variable
     * @param clauseExpr The for clause expression
     * @param quantifiedExpr The content of the for expression
     *
     * @return An XPath expression of type for
     */
    public ForAndQuantifiedExpr createForExpr(String varName, Expr clauseExpr,
        Expr quantifiedExpr);

    /**
     * Create a new cast as expression
     *
     * @param seqType The cast as type
     * @param parExpr The XPath expression to cast as
     *
     * @return An XPath expression of type cast as
     */
    public CastOrTreatAsExpr createCastAsExpr(SequenceType seqType,
        OperatorExpr parExpr);

    /**
     * Create a new treat as expression
     *
     * @param seqType The treat as type
     * @param parExpr The XPath expression to treat as
     *
     * @return An XPath expression of type treat as
     */
    public CastOrTreatAsExpr createTreatAsExpr(SequenceType seqType,
        OperatorExpr parExpr);

    /**
     * Create a new integer literal
     *
     * @return A literal of type integer
     */
    public Literal createIntegerLiteralExpr(int value);

    /**
     * Create a new 'big' integer literal
     *
     * @param value The big integer
     *
     * @return A literal of type integer
     */
    public Literal createIntegerLiteralExpr(BigInteger value);

    /**
     * Create a new decimal literal
     *
     * @return A literal of type decimal
     */
    public Literal createDecimalLiteralExpr(double value);

    /**
     * Create a new 'big' decimal literal
     *
     * @return A literal of type decimal
     */
    public Literal createDecimalLiteralExpr(BigDecimal value);

    /**
     * Create a new string literal
     *
     * @return A literal of type string
     */
    public Literal createStringLiteralExpr(String value);

    /**
     * Create a new double literal
     *
     * @return A literal of type double
     */
    public Literal createDoubleLiteralExpr(double value);
    
    /**
     * Sets the node factory for creating AST nodes
     */
    void setNodeFactory(NodeFactory factory);
}