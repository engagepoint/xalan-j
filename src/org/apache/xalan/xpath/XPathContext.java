/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xpath;

// Java lib imports
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Stack;

// Xalan imports
import org.apache.xalan.utils.IntStack;
import org.apache.xalan.utils.NSInfo;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.QName;

import org.apache.xalan.res.XSLMessages;

import org.apache.xalan.xpath.res.XPATHErrorResources;

// DOM Imports
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.Node;

// SAX2 imports
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Locator;

// TRaX imports
import trax.URIResolver;
import trax.TransformException;

// Temporary!!!
import org.apache.xalan.extensions.ExtensionsTable;

/**
 * <meta name="usage" content="advanced"/>
 * Default class for the execution context for XPath. Many 
 * of the functions in this class need to be overridden in order to 
 * perform correct execution of the XPath (for instance, variable 
 * execution).
 */
public class XPathContext
{
  /**
   * Create an XPathContext instance.
   */
  public XPathContext()
  {
  }

  /**
   * Create an XPathContext instance.
   * @param owner Value that can be retreaved via the getOwnerObject() method.
   * @see getOwnerObject
   */
  public XPathContext(Object owner)
  {
    m_owner = owner;
  }

  /**
   * Copy attributes from another liaison.
   */
  public void copyFromOtherLiaison(XPathContext from)
    throws SAXException
  {
  }

  /**
   * Reset for new run.
   */
  public void reset()
  {
  }
  
  Locator m_saxLocation;
  
  public void setSAXLocator(Locator location)
  {
    m_saxLocation = location;
  }
  
  public Locator getSAXLocator()
  {
    return m_saxLocation;
  }
  
  private Object m_owner;
  
  /**
   * Get the "owner" context of this context, which should be, 
   * in the case of XSLT, the Transformer object.  This is needed 
   * so that XSLT functions can get the Transformer.
   * @return The owner object passed into the constructor, or null.
   */
  public Object getOwnerObject()
  {
    return m_owner;
  }

  // ================ extensionsTable ===================
  
  /**
   * The table of Extension Handlers.
   */
  private ExtensionsTable m_extensionsTable = new ExtensionsTable();
  
  /**
   * Get the extensions table object.
   */
  public ExtensionsTable getExtensionsTable()
  {
    return m_extensionsTable;
  }
  
  void setExtensionsTable(ExtensionsTable table)
  {
    m_extensionsTable = table;
  }
  
  // ================ VarStack ===================

  /**
   * The stack of Variable stacks.  A VariableStack will be
   * pushed onto this stack for each template invocation.
   */
  private VariableStack m_variableStacks = new VariableStack();
  
  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   */
  public VariableStack getVarStack() { return m_variableStacks; }

  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   */
  public void setVarStack(VariableStack varStack) { m_variableStacks = varStack; }

  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   */
  public XObject getVariable(QName qname)
    throws org.xml.sax.SAXException
  {
    Object obj = getVarStack().getVariable(qname);
    if((null != obj) && !(obj instanceof XObject))
    {
      obj = new XObject(obj);
    }
    return (XObject)obj;
  }
  
  
  // ================ DOMHelper ===================

  private DOMHelper m_domHelper;
  
  /**
   * Get the DOMHelper associated with this execution context.
   */
  public DOMHelper getDOMHelper()
  {
    if(null == m_domHelper)
      m_domHelper = new DOMHelper();
    return m_domHelper;
  }
  
  /**
   * Set the DOMHelper associated with this execution context.
   */
  public void setDOMHelper(DOMHelper helper)
  {
    m_domHelper = helper;
  }
                                                
  // ================ SourceTreeManager ===================

  private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();
  
  /**
   * Get the DOMHelper associated with this execution context.
   */
  public SourceTreeManager getSourceTreeManager()
  {
    return m_sourceTreeManager;
  }
  
  /**
   * Set the DOMHelper associated with this execution context.
   */
  public void setSourceTreeManager(SourceTreeManager mgr)
  {
    m_sourceTreeManager = mgr;
  }
  
  // =================================================

  private URIResolver m_uriResolver;
  
  /**
   * Get the URIResolver associated with this execution context.
   */
  public URIResolver getURIResolver()
  {
    return m_uriResolver;
  }
  
  /**
   * Set the URIResolver associated with this execution context.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }
  
  // =================================================
   
  public XMLReader m_primaryReader;
  
  /**
   * Get primary XMLReader associated with this execution context.
   */
  public XMLReader getPrimaryReader()
  {
    return m_primaryReader;
  }
  
  /**
   * Set primary XMLReader associated with this execution context.
   */
  public void setPrimaryReader(XMLReader reader)
  {
    m_primaryReader = reader;
  }

  // =================================================

  /**
   * Get a factory to create XPaths.
   */
  public XPathFactory getDefaultXPathFactory()
  {
    return SimpleNodeLocator.factory();
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler()
  {
    return new SimpleNodeLocator();
  }

  /**
   * Take a user string (system ID) return the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide 
   * the error condition is severe enough to halt processing.
   */
  public URL getURLFromString(String urlString, String base)
    throws SAXException 
  {
    InputSource inputSource;
    try
    {
      inputSource = getSourceTreeManager().resolveURI(base, urlString);
    }
    catch(IOException ioe)
    {
      inputSource = null; // shutup compiler.
      throw new SAXException(ioe);
    }
    // System.out.println("url: "+url.toString());
    try
    {
      return new URL(inputSource.getSystemId());
    }
    catch(MalformedURLException mue)
    {
      throw new SAXException(mue);
    }
  }
  
  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Tell the user of an assertion error, and probably throw an 
   * exception.
   */
  private void assert(boolean b, String msg)
    throws org.xml.sax.SAXException
  {
    ErrorHandler errorHandler = getPrimaryReader().getErrorHandler();
    if (errorHandler != null) {
      errorHandler.fatalError(new TransformException(m_XSLMessages.createMessage(XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, new Object[] {msg})));
    }
  }
  
  //==========================================================
  // SECTION: Execution context state tracking
  //==========================================================
           
  /**
   * The current context node list.
   */
  private Stack m_contextNodeLists = new Stack();
        
  /**
   * Get the current context node list.
   */
  public NodeSet getContextNodeList()
  {
    if (m_contextNodeLists.size()>0)
      return (NodeSet)m_contextNodeLists.peek();
    else 
      return null;
  }
 
  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node list.
   * @param A nodelist that represents the current context 
   * list as defined by XPath.
   */
  public void pushContextNodeList(NodeSet nl)
  {
    m_contextNodeLists.push(nl);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Pop the current context node list.
   */
  public void popContextNodeList()
  {
    m_contextNodeLists.pop();
  }

  /**
   * Tells if FoundIndex should be thrown if index is found.
   * This is an optimization for match patterns.
   */
  private boolean m_throwFoundIndex = false;
  
  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown
   * if index is found.
   * This is an optimization for match patterns, and
   * is used internally by the XPath engine.
   */
  public boolean getThrowFoundIndex()
  {
    return m_throwFoundIndex;
  }

  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown
   * if index is found.
   * This is an optimization for match patterns, and
   * is used internally by the XPath engine.
   */
  public void setThrowFoundIndex(boolean b)
  {
    m_throwFoundIndex = b;
  }
  
  /**
   * The current node.
   */
  private Node m_currentNode = null;
  
  /**
   * The current prefixResolver for the execution context (not
   * the source tree context).
   * (Is this really needed?)
   */
  private PrefixResolver m_currentPrefixResolver = null;
  
  /**
   * Get the current context node.
   */
  public Node getCurrentNode()
  {
    return m_currentNode;
  }

  /**
   * Set the current context node.
   */
  public void setCurrentNode(Node n)
  {
    m_currentNode = n;
  }
  
  /**
   * Get the current namespace context for the xpath.
   */
  public PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Get the current namespace context for the xpath.
   */
  public void setNamespaceContext(PrefixResolver pr)
  {
    m_currentPrefixResolver = pr;
  } 
  
  //==========================================================
  // SECTION: Current TreeWalker contexts (for internal use)
  //==========================================================

  /**
   * Stack of AxesIterators.
   */
  private Stack m_axesIteratorStack = new Stack();
  
  /**
   * <meta name="usage" content="internal"/>
   * Push a TreeWalker on the stack.
   */
  public void pushTreeWalkerContext(TreeWalker iter)
  {
    m_axesIteratorStack.push(iter);
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Pop the last pushed axes iterator.
   */
  public void popTreeWalkerContext()
  {
    m_axesIteratorStack.pop();
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the current axes iterator, or return null if none.
   */
  public TreeWalker getTreeWalkerContext()
  {
    return m_axesIteratorStack.isEmpty() 
           ? null : (TreeWalker)m_axesIteratorStack.peek();
                                           
  }

}
