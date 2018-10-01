/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2002 International Business Machines Corp. 2002. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.xml.rpc.processor.modeler.j2ee.xml;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * A simple DOM writer
 */
public class DOMWriter {
    protected PrintWriter out;
    protected int indent = 0;
    protected String encodingTag;
    protected String docTypeString;
    protected String prefix;

    /*
     * @param document document to print
     * @param writer  PrintWriter to print
     * @param encodingTag  encoding
     * @param docTypeString document type
     */
    public DOMWriter(
        Document document,
        PrintWriter writer,
        String encodingTag,
        String docTypeString,
        String prefix) {
        this.prefix = prefix;
        this.indent = indent;
        this.encodingTag = encodingTag;
        this.docTypeString = docTypeString;
        out = writer;
        print(document);
    }

    public DOMWriter(
        Document document,
        String outfile,
        String encoding,
        String encodingTag,
        String docTypeString) {
        this.encodingTag = encodingTag;
        this.docTypeString = docTypeString;

        try {
            OutputStreamWriter writer;
            if (encoding != null) {
                writer =
                    new OutputStreamWriter(
                        new FileOutputStream(outfile),
                        encoding);
            } else {
                // default to utf8
                writer =
                    new OutputStreamWriter(
                        new FileOutputStream(outfile),
                        "UTF8");
            }

            out = new PrintWriter(new BufferedWriter(writer));
            print(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printIndent() {
        out.print(prefix);
        for (int i = 0; i < indent; i++) {
            out.print(" ");
        }
    }

    public class XMLVisitor {
        public void visitNode(Node node) {
            switch (node.getNodeType()) {
                case Node.ATTRIBUTE_NODE :
                    {
                        visitAttr((Attr) node);
                        break;
                    }
                case Node.CDATA_SECTION_NODE :
                    {
                        visitCDATASection((CDATASection) node);
                        break;
                    }
                case Node.COMMENT_NODE :
                    {
                        visitComment((Comment) node);
                        break;
                    }
                case Node.DOCUMENT_NODE :
                    {
                        visitDocument((Document) node);
                        break;
                    }
                case Node.ELEMENT_NODE :
                    {
                        visitElement((Element) node);
                        break;
                    }
                case Node.PROCESSING_INSTRUCTION_NODE :
                    {
                        visitProcessingInstruction(
                            (ProcessingInstruction) node);
                        break;
                    }
                case Node.TEXT_NODE :
                    {
                        visitText((Text) node);
                        break;
                    }
            }
        }

        public void visitDocument(Document document) {
            if (encodingTag != null && !encodingTag.equals("")) {
                out.println(
                    "<?xml version=\"1.0\" encoding=\"" + encodingTag + "\"?>");
            }

            // Print the DOCTYPE if specified
            if (docTypeString != null && !docTypeString.equals("")) {
                out.println(docTypeString);
            }

            visitChildNodesHelper(document);
        }

        public void visitElement(Element element) {
            boolean currentElementHasChildElements = hasChildElements(element);

            printIndent();
            out.print('<' + element.getNodeName());
            visitAttributesHelper(element);
            out.print(">");

            if (currentElementHasChildElements) {
                out.print("\n");
            }

            indent += 2;
            visitChildNodesHelper(element);
            indent -= 2;

            if (currentElementHasChildElements) {
                printIndent();
            }

            out.println("</" + element.getNodeName() + ">");
        }

        public void visitAttr(Attr attr) {
            /*Don't print attribute value unless it was originally specified */
            if (attr.getSpecified()) {
                out.print(" ");
                out.print(attr.getNodeName() + "=\"" + attr.getValue() + '"');
            }
        }

        public void visitText(Text text) {
            out.print(normalize(text.getNodeValue()));
        }

        public void visitCDATASection(CDATASection cdataSection) {
        }

        public void visitComment(Comment comment) {
            printIndent();
            out.print("<!--");
            out.print(normalize(comment.getNodeValue()));
            out.println("-->");
        }

        public void visitProcessingInstruction(ProcessingInstruction pi) {
            printIndent();
            out.print("<?");
            out.print(pi.getNodeName());
            out.print(" ");
            out.print(normalize(pi.getNodeValue()));
            out.println("?>");
        }

        public boolean hasChildElements(Node node) {
            boolean result = false;
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public void visitChildNodesHelper(Node node) {
            NodeList children = node.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                visitNode(children.item(i));
            }
        }

        public void visitAttributesHelper(Node node) {
            NamedNodeMap map = node.getAttributes();
            for (int i = 0; i < map.getLength(); i++) {
                visitNode(map.item(i));
            }
        }
    }

    /**
     * Prints the specified node, recursively.
     */
    public void print(Node node) {
        // is there anything to do?
        if (node != null) {
            XMLVisitor visitor = new XMLVisitor();
            visitor.visitNode(node);
        }
        out.flush();
    }

    /**
     * Normalize the text string
     */
    protected String normalize(String s) {
        StringBuffer str = new StringBuffer();
        s = s.trim();

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<' :
                    {
                        str.append("&lt;");
                        break;
                    }
                case '>' :
                    {
                        str.append("&gt;");
                        break;
                    }
                case '&' :
                    {
                        str.append("&amp;");
                        break;
                    }
                case '"' :
                    {
                        str.append("&quot;");
                        break;
                    }
                case '\r' :
                case '\n' :
                    {
                        break;
                    }
                default :
                    {
                        str.append(ch);
                    }
            }
        }
        return (str.toString());
    }
}
