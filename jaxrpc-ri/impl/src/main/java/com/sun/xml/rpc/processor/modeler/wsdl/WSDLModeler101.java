/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.BindingFault;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.OperationStyle;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.wsdl.document.soap.SOAPHeader;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * WSDLModeler for JAXRPC version 1.0.1
 * @author JAX-RPC Development Team
 */
public class WSDLModeler101 extends WSDLModelerBase {

    /**
     * @param modelInfo
     * @param options
     */
    public WSDLModeler101(WSDLModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
    }

    /**
     * JAXRPC 1.0.1 doesn't support RPC/Literal
     * @return null
     */
    @Override
    protected Operation processSOAPOperationRPCLiteralStyle() {
        return null;
    }

    @Override
    protected void setUnwrapped(LiteralStructuredType type) {
    }

    /**
     * JAXRPC 1.0.1 doesn't support optional parts in a WSDL document
     * @param body request or response body, represents soap:body
     * @param message Input or output message, equivalent to wsdl:message
     * @return iterator over MessagePart
     */
    @Override
    protected List getMessageParts(
        SOAPBody body,
        com.sun.xml.rpc.wsdl.document.Message message, 
        boolean isInput) {
        
        if (body.getParts() != null) {
            // right now, we only support body parts
            // TODO - fix this to include the case of <soap:body parts="..."/>
            warn(
                "wsdlmodeler.warning.ignoringOperation.cannotHandleBodyPartsAttribute",
                info.portTypeOperation.getName());
            return null;
        }
        ArrayList parts = new ArrayList();
        for(Iterator iter = message.parts();iter.hasNext();) {
            parts.add(iter.next());
        }
        return parts;
    }

    @Override
    protected java.util.List processParameterOrder(
        Set inputParameterNames,
        Set outputParameterNames,
        StringBuffer resultParameterName) {
        //process doc/lit params differently
        if(isOperationDocumentLiteral()){
            return processDocLitParameterOrder(inputParameterNames,
                    outputParameterNames, resultParameterName);            
        }
        if (resultParameterName == null)
            resultParameterName = new StringBuffer();
        com.sun.xml.rpc.wsdl.document.Message inputMessage = getInputMessage();
        com.sun.xml.rpc.wsdl.document.Message outputMessage =
            getOutputMessage();
        SOAPBody soapRequestBody = getSOAPRequestBody();
        SOAPBody soapResponseBody = getSOAPResponseBody();
        String parameterOrder = info.portTypeOperation.getParameterOrder();
        java.util.List parameterList = null;

        boolean buildParameterList = false;

        if (parameterOrder != null) {
            parameterList = XmlUtil.parseTokenList(parameterOrder);
        } else {
            parameterList = new ArrayList();
            buildParameterList = true;
        }

        Set partNames = new HashSet();
        Iterator partsIter = getMessageParts(soapRequestBody, inputMessage, true).iterator();
        while (partsIter.hasNext()) {
            MessagePart part = (MessagePart) partsIter.next();
            if (part.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
                throw new ModelerException(
                    "wsdlmodeler.invalid.message.partMustHaveTypeDescriptor",
                    new Object[] { inputMessage.getName(), part.getName()});
            }
            partNames.add(part.getName());
            inputParameterNames.add(part.getName());
            if (buildParameterList) {
                parameterList.add(part.getName());
            }
        }
        for (Iterator iter = outputMessage.parts(); iter.hasNext();) {
            MessagePart part = (MessagePart) iter.next();
            if (part.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
                throw new ModelerException(
                    "wsdlmodeler.invalid.message.partMustHaveTypeDescriptor",
                    new Object[] { outputMessage.getName(), part.getName()});
            }
            partNames.add(part.getName());
            if (buildParameterList && resultParameterName.length() == 0) {
                // pick the first output argument as the result
                resultParameterName.append(part.getName());
            } else {
                outputParameterNames.add(part.getName());
                if (buildParameterList) {
                    if (!inputParameterNames.contains(part.getName())) {
                        parameterList.add(part.getName());
                    }
                }
            }
        }

        if (!buildParameterList) {
            // do some validation of the given parameter order
            for (Iterator iter = parameterList.iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                if (!partNames.contains(name)) {
                    throw new ModelerException(
                        "wsdlmodeler.invalid.parameterorder.parameter",
                        new Object[] {
                            name,
                            info.operation.getName().getLocalPart()});
                }
                partNames.remove(name);
            }

            // now we should be left with at most one part
            if (partNames.size() > 1) {
                throw new ModelerException(
                    "wsdlmodeler.invalid.parameterOrder.tooManyUnmentionedParts",
                    new Object[] { info.operation.getName().getLocalPart()});
            }
            if (partNames.size() == 1) {
                resultParameterName.append(
                    (String) partNames.iterator().next());
            }
        }
        return parameterList;
    }

    /**
     * @return
     */
    private List processDocLitParameterOrder(
            Set inputParameterNames,
            Set outputParameterNames,
            StringBuffer resultParameterName) {
        Set partNames = new HashSet();
        java.util.List parameterList = new ArrayList();
        boolean gotOne = false;
        boolean isRequestResponse =
            info.portTypeOperation.getStyle()
            == OperationStyle.REQUEST_RESPONSE;

        // vivekp, optional parts handling 
        Iterator partsIter = getMessageParts(getSOAPRequestBody(), getInputMessage(), true).iterator();

        while (partsIter.hasNext()) {
            if (gotOne) {
                warn(
                        "wsdlmodeler.warning.ignoringOperation.cannotHandleMoreThanOnePartInInputMessage",
                        info.portTypeOperation.getName());
                return null;
            }

            MessagePart part = (MessagePart)partsIter.next();
            if (part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
                // right now, we only support "element" message parts
                // TODO - fix this
                warn(
                        "wsdlmodeler.warning.ignoringOperation.cannotHandleTypeMessagePart",
                        info.portTypeOperation.getName());
                return null;
            }

            partNames.add(part.getName());
            inputParameterNames.add(part.getName());
            parameterList.add(part.getName());
            gotOne = true;
        }

        boolean inputIsEmpty = !gotOne;

        if (isRequestResponse) {
            gotOne = false;
            /** vivekp, optional parts handling */
            partsIter = getMessageParts(getSOAPResponseBody(), getOutputMessage(), false).iterator();

            while (partsIter.hasNext()) {
                if (gotOne) {
                    warn(
                            "wsdlmodeler.warning.ignoringOperation.cannotHandleMoreThanOnePartInOutputMessage",
                            info.portTypeOperation.getName());
                    return null;
                }

                MessagePart part = (MessagePart)partsIter.next();
                if (part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
                    // right now, we only support "element" message parts
                    // TODO - fix this
                    warn(
                            "wsdlmodeler.warning.ignoringOperation.cannotHandleTypeMessagePart",
                            info.portTypeOperation.getName());
                    return null;
                }
                partNames.add(part.getName());
                //outputParameterNames.add(part.getName());
                if (!inputParameterNames.contains(part.getName())) {
                    //parameterList.add(part.getName());
                    outputParameterNames.add(part.getName());
                }
                resultParameterName.append(part.getName());    
            }
        }
        return parameterList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleLiteralSOAPFault(
        Response response,
        Set duplicateNames) {
        //      handle faults
        for (Iterator iter = info.bindingOperation.faults(); iter.hasNext();) {
            BindingFault bindingFault = (BindingFault) iter.next();

            warn(
                "wsdlmodeler.warning.ignoringFault.documentOperation",
                new Object[] {
                    bindingFault.getName(),
                    info.bindingOperation.getName()});
        }
    }

    /**
     * bug fix: 4884736 
     * Returns soapbinding:fault name. If null then gives warning for wsi R2721 and uses 
     * wsdl:fault name.
     * 
     * @param faultPartName - to be used by versions &lt; 1.1
     * @param soapFaultName
     * @param bindFaultName
     * @param faultMessageName 
     * @return faultPartName
     */
    @Override
    protected String getFaultName(
        String faultPartName,
        String soapFaultName,
        String bindFaultName,
        String faultMessageName) {
            
        return faultPartName;
    }

    /* 
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getSchemaAnalyzerInstance(com.sun.xml.rpc.wsdl.document.WSDLDocument, com.sun.xml.rpc.processor.config.WSDLModelInfo, java.util.Properties, java.util.Set, com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator)
     */
    protected SchemaAnalyzerBase getSchemaAnalyzerInstance(
        WSDLDocument document,
        WSDLModelInfo _modelInfo,
        Properties _options,
        Set _conflictingClassNames,
        JavaSimpleTypeCreator _javaTypes) {
        return new SchemaAnalyzer101(
            document,
            _modelInfo,
            _options,
            _conflictingClassNames,
            _javaTypes);
    }

    /* Fix for bug: 4913508, basically reverts bug 4847438 fix
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#createJavaException(com.sun.xml.rpc.processor.model.Fault, com.sun.xml.rpc.processor.model.Port, java.lang.String)
     */
    @Override
    protected boolean createJavaException(
        Fault fault,
        Port port,
        String operationName) {
        String exceptionName = null;
        String propertyName =
            getEnvironment().getNames().validJavaMemberName(fault.getName());
        SOAPType faultType = (SOAPType) fault.getBlock().getType();
        SOAPStructureType soapStruct;
        if (faultType instanceof SOAPStructureType) {
            exceptionName =
                makePackageQualified(
                    getEnvironment().getNames().validJavaClassName(
                        faultType.getName().getLocalPart()),
                    faultType.getName());
            soapStruct =
                (SOAPStructureType) _faultTypeToStructureMap.get(
                    faultType.getName());
            if (soapStruct == null) {
                soapStruct = new SOAPOrderedStructureType(faultType.getName());
                SOAPStructureType temp = (SOAPStructureType) faultType;
                Iterator members = temp.getMembers();
                while (members.hasNext()) {
                    soapStruct.add((SOAPStructureMember) members.next());
                }
                _faultTypeToStructureMap.put(faultType.getName(), soapStruct);
            }
        } else {
            exceptionName =
                makePackageQualified(
                    getEnvironment().getNames().validJavaClassName(
                        fault.getName()),
                    port.getName());
            soapStruct =
                new SOAPOrderedStructureType(
                    new QName(
                        faultType.getName().getNamespaceURI(),
                        fault.getName()));
            QName memberName =
                new QName(
                    fault.getBlock().getName().getNamespaceURI(),
                    StringUtils.capitalize(faultType.getName().getLocalPart()));
            SOAPStructureMember soapMember =
                new SOAPStructureMember(memberName, faultType);
            JavaStructureMember javaMember =
                new JavaStructureMember(
                    fault.getBlock().getName().getLocalPart(),
                    faultType.getJavaType(),
                    soapMember);
            soapMember.setJavaStructureMember(javaMember);
            javaMember.setConstructorPos(0);
            javaMember.setReadMethod("get" + memberName.getLocalPart());
            javaMember.setInherited(soapMember.isInherited());
            soapMember.setJavaStructureMember(javaMember);
            soapStruct.add(soapMember);
        }
        if (isConflictingClassName(exceptionName)) {
            exceptionName += "_Exception";
        }

        JavaException existingJavaException =
            (JavaException) _javaExceptions.get(exceptionName);
        if (existingJavaException != null) {
            if (existingJavaException.getName().equals(exceptionName)) {
                if (((SOAPType) existingJavaException.getOwner())
                    .getName()
                    .equals(soapStruct.getName())) {
                    // we have mapped this fault already
                    if (faultType instanceof SOAPStructureType) {
                        fault.getBlock().setType(
                            (SOAPType) existingJavaException.getOwner());
                    }
                    fault.setJavaException(existingJavaException);
                    createRelativeJavaExceptions(fault, port, operationName);
                    return false;
                }
            }
        }

        JavaException javaException =
            new JavaException(exceptionName, false, soapStruct);
        soapStruct.setJavaType(javaException);

        _javaExceptions.put(javaException.getName(), javaException);

        Iterator members = soapStruct.getMembers();
        SOAPStructureMember member = null;
        JavaStructureMember javaMember;
        for (int i = 0; members.hasNext(); i++) {
            member = (SOAPStructureMember) members.next();
            javaMember = member.getJavaStructureMember();
            javaMember.setConstructorPos(i);
            javaException.add(javaMember);
        }
        if (faultType instanceof SOAPStructureType) {
            fault.getBlock().setType(soapStruct);
        }
        fault.setJavaException(javaException);

        createRelativeJavaExceptions(fault, port, operationName);
        return true;
    }
    
    /**
     * Overridden this method, 1.0.1 does not process header fault
     * {@inheritDoc}
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#processHeaderFaults(com.sun.xml.rpc.wsdl.document.soap.SOAPHeader, 
     * com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase.ProcessSOAPOperationInfo, com.sun.xml.rpc.processor.model.Response, java.util.Set)
     */
    @Override
    protected void processHeaderFaults(
        SOAPHeader header,
        ProcessSOAPOperationInfo info,
        Response response,
        Set duplicateNames) {
    }
    
    /**
     * {@inheritDoc}
     * Only JAXRPC SI 1.1.2 and onwards support wsdl mime extension and swaref.
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getAnyExtensionOfType(com.sun.xml.rpc.wsdl.framework.Extensible, java.lang.Class)
     */
    @Override
    protected Extension getAnyExtensionOfType(
            Extensible extensible,
            Class type) {
        return getExtensionOfType(extensible, type);
    }       

    /**
     * {@inheritDoc}
     * @return true
     */
    @Override
    protected boolean isBoundToSOAPBody(MessagePart part) {
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     * @return false
     */
    @Override
    protected boolean isBoundToMimeContent(MessagePart part) {        
        return false;
    }

}
