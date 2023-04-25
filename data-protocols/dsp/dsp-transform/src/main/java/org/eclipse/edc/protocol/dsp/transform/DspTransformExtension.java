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

package org.eclipse.edc.protocol.dsp.transform;

import jakarta.json.Json;
import org.eclipse.edc.jsonld.spi.transformer.JsonLdTransformerRegistry;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromCatalogTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromDataServiceTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromDatasetTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromDistributionTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromPolicyTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToActionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToCatalogTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToConstraintTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDataServiceTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDatasetTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDistributionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDutyTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToPermissionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToPolicyTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToProhibitionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonValueToGenericTypeTransformer;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.Map;

import static org.eclipse.edc.jsonld.JsonLdExtension.TYPE_MANAGER_CONTEXT_JSON_LD;

/**
 * Provides support for transforming DCAT catalog and ODRL policy types to and from JSON-LD. The
 * respective transformers are registered with the {@link JsonLdTransformerRegistry}.
 */
@Extension(value = DspTransformExtension.NAME)
public class DspTransformExtension implements ServiceExtension {

    public static final String NAME = "Dataspace Protocol Transform Extension";

    @Inject
    private TypeManager typeManager;

    @Inject
    private JsonLdTransformerRegistry registry;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var mapper = typeManager.getMapper(TYPE_MANAGER_CONTEXT_JSON_LD);
        mapper.registerSubtypes(AtomicConstraint.class, LiteralExpression.class);

        var jsonBuilderFactory = Json.createBuilderFactory(Map.of());

        // EDC model to JSON-LD transformers
        registry.register(new JsonObjectFromCatalogTransformer(jsonBuilderFactory, mapper));
        registry.register(new JsonObjectFromDatasetTransformer(jsonBuilderFactory, mapper));
        registry.register(new JsonObjectFromPolicyTransformer(jsonBuilderFactory));
        registry.register(new JsonObjectFromDistributionTransformer(jsonBuilderFactory));
        registry.register(new JsonObjectFromDataServiceTransformer(jsonBuilderFactory));

        // JSON-LD to EDC model transformers
        registry.register(new JsonObjectToCatalogTransformer());
        registry.register(new JsonObjectToDataServiceTransformer());
        registry.register(new JsonObjectToDatasetTransformer());
        registry.register(new JsonObjectToDistributionTransformer());
        registry.register(new JsonObjectToPolicyTransformer());
        registry.register(new JsonObjectToPermissionTransformer());
        registry.register(new JsonObjectToProhibitionTransformer());
        registry.register(new JsonObjectToDutyTransformer());
        registry.register(new JsonObjectToActionTransformer());
        registry.register(new JsonObjectToConstraintTransformer());
        registry.register(new JsonValueToGenericTypeTransformer(mapper));
    }
}
