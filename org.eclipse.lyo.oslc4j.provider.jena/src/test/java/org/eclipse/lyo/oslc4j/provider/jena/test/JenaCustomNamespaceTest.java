/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *	   Daniel Figueiredo Caetano - initial implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.provider.jena.test;

import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.CUSTOM_PREFIX;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.CUSTOM_URL;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.GLOBAL_PREFIX;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.GLOBAL_URL;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.TEST1_PREFIX;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.TEST1_URL;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.TEST2_PREFIX;
import static org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceConstants.TEST2_URL;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.eclipse.lyo.oslc4j.core.OslcGlobalNamespaceProvider;
import org.eclipse.lyo.oslc4j.core.annotation.OslcSchema;
import org.eclipse.lyo.oslc4j.core.test.customnamespace.CustomNamespaceResource;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Tests the Jena working with custom and global namespace mappings.
 * 
 * @author Daniel Figueiredo Caetano
 *
 */
public class JenaCustomNamespaceTest {

	private final String NAME_LOCAL_PART		= "name";
	private final String DESCRIPTION_LOCAL_PART = "description";
	
	/**
	 * Creates an instance of {@link CustomNamespaceResource} and checks if 
	 * the custom namespaces returned by the class passed to the 
	 * {@link OslcSchema#customNamespaceProvider()} are in the 
	 * {@link Model#getNsPrefixMap()} that Jena creates.   
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJenaCustomNamespaceProvider() throws Exception {
		CustomNamespaceResource namespaceResource = createCustomNamespaceResource();
		Model model = JenaModelHelper.createJenaModel(new Object[]{namespaceResource});
		assertCustomNamespaces(model, TEST1_PREFIX, TEST1_URL);
		assertCustomNamespaces(model, TEST2_PREFIX, TEST2_URL);
		assertCustomNamespaces(model, CUSTOM_PREFIX, CUSTOM_URL);
	}

	/**
	 * Adds custom prefix to the {@link OslcGlobalNamespaceProvider#getPrefixDefinitionMap()} 
	 * and checks if it is in the {@link Model#getNsPrefixMap()} created by Jena. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJenaGlobalNamespaceProvider() throws Exception {
		Map<String, String> globalNamespaceMappings = new HashMap<String, String>(1);
		globalNamespaceMappings.put(GLOBAL_PREFIX, GLOBAL_URL);
		OslcGlobalNamespaceProvider.getInstance().setPrefixDefinitionMap(globalNamespaceMappings);
		CustomNamespaceResource namespaceResource = createCustomNamespaceResource();
		Model model = JenaModelHelper.createJenaModel(new Object[]{namespaceResource});
		assertCustomNamespaces(model, GLOBAL_PREFIX, GLOBAL_URL);
	}
	
	/**
	 * Creates a new instance adding some test values.
	 * 
	 * @return new instance
	 */
	private CustomNamespaceResource createCustomNamespaceResource() {
		CustomNamespaceResource namespaceResource = new CustomNamespaceResource();
		URI aboutNamespaceResource = URI.create("http://about.custom.oslc.namespace/001");
		namespaceResource.setAbout(aboutNamespaceResource);
		URI typeNamespaceResource = URI.create(CustomNamespaceResource.CUSTOM_RESOURCE_TYPE);
		namespaceResource.getTypes().add(typeNamespaceResource);
		QName qName = new QName(TEST1_URL, NAME_LOCAL_PART);
		namespaceResource.getExtendedProperties().put(qName, "Some Name");
		qName = new QName(TEST2_URL, DESCRIPTION_LOCAL_PART);
		namespaceResource.getExtendedProperties().put(qName, "Any Description");
		return namespaceResource;
	}
	
	/**
	 * Checks if the model contains the prefix and its mapped namespace.
	 * @param model to be checked.
	 * @param prefix that model should have.
	 * @param namespaceURI mapped related to the prefix.
	 */
	private void assertCustomNamespaces(Model model, String prefix, String namespaceURI) {
		Assert.assertTrue(
				"The prefix map should contain the custom namespace mapping", 
				model.getNsPrefixMap().containsKey(prefix)
		);
		Assert.assertEquals(
				"The prefix mapped should has a different namespaceURI.", 
				model.getNsPrefixMap().get(prefix),
				namespaceURI
		);
	}
	
}