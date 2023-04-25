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

package org.eclipse.edc.protocol.dsp.transferprocess.api;

import org.eclipse.edc.connector.spi.transferprocess.TransferProcessProtocolService;
import org.eclipse.edc.jsonld.spi.transformer.JsonLdTransformerRegistry;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.protocol.dsp.transferprocess.api.controller.DspTransferProcessApiController;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebService;

import static org.eclipse.edc.jsonld.JsonLdExtension.TYPE_MANAGER_CONTEXT_JSON_LD;

/**
 * Creates and registers the controller for dataspace protocol transfer process requests.
 */
@Extension(value = DspTransferProcessApiExtension.NAME)
public class DspTransferProcessApiExtension implements ServiceExtension {

    @Inject
    private DspApiConfiguration config;
    @Inject
    private TypeManager typeManager;
    @Inject
    private Monitor monitor;
    @Inject
    private WebService webService;
    @Inject
    private TransferProcessProtocolService transferProcessProtocolService;
    @Inject
    private JsonLdTransformerRegistry registry;
    @Inject
    private IdentityService identityService;

    public static final String NAME = "Dataspace Protocol: TransferProcess API Extension";

    @Override
    public void initialize(ServiceExtensionContext context) {
        var controller = new DspTransferProcessApiController(monitor, typeManager.getMapper(TYPE_MANAGER_CONTEXT_JSON_LD), registry, transferProcessProtocolService, identityService, config.getDspCallbackAddress());

        webService.registerResource(config.getContextAlias(), controller);
    }
}
