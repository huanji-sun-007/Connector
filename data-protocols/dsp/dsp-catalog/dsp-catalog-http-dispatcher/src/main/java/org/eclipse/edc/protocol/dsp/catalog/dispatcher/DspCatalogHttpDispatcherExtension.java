/*
 *  Copyright (c) 2023 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *
 */

package org.eclipse.edc.protocol.dsp.catalog.dispatcher;

import org.eclipse.edc.jsonld.spi.transformer.JsonLdTransformerRegistry;
import org.eclipse.edc.protocol.dsp.catalog.dispatcher.delegate.CatalogRequestHttpDelegate;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRemoteMessageDispatcher;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

import static org.eclipse.edc.jsonld.JsonLdExtension.TYPE_MANAGER_CONTEXT_JSON_LD;

/**
 * Creates and registers the HTTP dispatcher delegate for sending a catalog request as defined in
 * the dataspace protocol specification.
 */
@Extension(value = DspCatalogHttpDispatcherExtension.NAME)
public class DspCatalogHttpDispatcherExtension implements ServiceExtension {
    
    public static final String NAME = "Dataspace Protocol Catalog HTTP Dispatcher Extension";
    
    @Inject
    private DspHttpRemoteMessageDispatcher messageDispatcher;
    @Inject
    private TypeManager typeManager;
    @Inject
    private JsonLdTransformerRegistry transformerRegistry;
    
    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public void initialize(ServiceExtensionContext context) {
        messageDispatcher.registerDelegate(new CatalogRequestHttpDelegate(typeManager.getMapper(TYPE_MANAGER_CONTEXT_JSON_LD), transformerRegistry));
    }
    
}
