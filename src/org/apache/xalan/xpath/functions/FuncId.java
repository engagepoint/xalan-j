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
package org.apache.xalan.xpath.functions; 

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Document;
import java.util.Vector;
import org.apache.xalan.xpath.XPathContext;
import org.apache.xalan.xpath.DOMHelper;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XNodeSet;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Id() function.
 */
public class FuncId extends Function
{
  /**
   * Execute the function.  The function must return 
   * a valid object.
   * @param path The executing xpath.
   * @param context The current context.
   * @param opPos The current op position.
   * @param args A list of XObject arguments.
   * @return A valid XObject.
   */
  public XObject execute(XPath path, XPathContext xctxt, Node context, int opPos, Vector args) 
    throws org.xml.sax.SAXException
  {    
    Document docContext = (Node.DOCUMENT_NODE == context.getNodeType()) 
                          ? (Document)context : context.getOwnerDocument();
    XObject arg = (XObject)args.elementAt(0);
    XNodeSet nodes = new XNodeSet();
    boolean argIsNodeset = (XObject.CLASS_NODESET != arg.getType());
    if((arg.getType() == arg.CLASS_NULL) || 
       (!argIsNodeset && arg.str().length() == 0))
      return nodes;
    Hashtable usedrefs = new Hashtable();
    NodeIterator ni = argIsNodeset ? arg.nodeset() : null;
    Node pos = null;
    while(!argIsNodeset || (null != (pos = ni.nextNode())))
    {
      String refval = argIsNodeset
                      ? DOMHelper.getNodeData(pos) : arg.str();
      if(null != refval)
      {
        StringTokenizer tokenizer = new StringTokenizer(refval);
        while(tokenizer.hasMoreTokens())
        {
          String ref = tokenizer.nextToken();
          if(null == ref)
            continue;
          if(usedrefs.get(ref) != null)
          {
            continue;
          }
          else
          {
            // m_currentPattern being used as a dummy value.
            usedrefs.put(ref, path.getPatternString());
          }
          if(null == docContext)
          {
            path.error(xctxt, context, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null); //"context does not have an owner document!");
          }
          Node node = xctxt.getDOMHelper().getElementByID(ref, docContext);
          // nodes.mutableNodeset().addNode(node);   
          if(null != node)
            nodes.mutableNodeset().addNodeInDocOrder(node, xctxt);
        }
      }
      if(!argIsNodeset)
        break;
    }
    return nodes;
  }
}
