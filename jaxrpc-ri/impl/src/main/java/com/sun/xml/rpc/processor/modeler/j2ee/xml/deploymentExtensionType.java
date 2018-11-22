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

/**
* This class represents the complex type <deploymentExtensionType>
*/
public class deploymentExtensionType extends ComplexType {
    public deploymentExtensionType() {
    }

    public void setExtensionElement(
        int index,
        extensibleType extensionElement) {
        setElementValue(index, "extension-element", extensionElement);
    }

    public extensibleType getExtensionElement(int index) {
        return (extensibleType) getElementValue(
            "extension-element",
            "extensibleType",
            index);
    }

    public int getExtensionElementCount() {
        return sizeOfElement("extension-element");
    }

    public boolean removeExtensionElement(int index) {
        return removeElement(index, "extension-element");
    }

    public void setNamespace(String namespace) {
        setAttributeValue("namespace", namespace);
    }

    public String getNamespace() {
        return getAttributeValue("namespace");
    }

    public boolean removeNamespace() {
        return removeAttribute("namespace");
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        setAttributeValue("mustUnderstand", mustUnderstand);
    }

    public boolean getMustUnderstand() {
        return getAttributeBooleanValue("mustUnderstand");
    }

    public boolean removeMustUnderstand() {
        return removeAttribute("mustUnderstand");
    }

    public void setId(String id) {
        setAttributeValue("id", id);
    }

    public String getId() {
        return getAttributeValue("id");
    }

    public boolean removeId() {
        return removeAttribute("id");
    }

}