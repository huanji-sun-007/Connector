/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.edc.connector.api.management.transferprocess.transform;

import org.eclipse.edc.api.transformer.DtoTransformer;
import org.eclipse.edc.connector.api.management.transferprocess.model.TransferRequestDto;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransferRequestDtoToTransferRequestTransformer implements DtoTransformer<TransferRequestDto, TransferRequest> {

    @Override
    public Class<TransferRequestDto> getInputType() {
        return TransferRequestDto.class;
    }

    @Override
    public Class<TransferRequest> getOutputType() {
        return TransferRequest.class;
    }

    @Override
    public @Nullable TransferRequest transform(@NotNull TransferRequestDto object, @NotNull TransformerContext context) {
        // Generate a DataRequest ID if none is provided (used for idempotency)
        String id = Objects.requireNonNullElseGet(object.getId(), () -> UUID.randomUUID().toString());

        var dataRequest = DataRequest.Builder.newInstance()
                .id(id)
                .assetId(object.getAssetId())
                .connectorId(object.getConnectorId())
                .dataDestination(object.getDataDestination())
                .connectorAddress(object.getConnectorAddress())
                .contractId(object.getContractId())
                .transferType(object.getTransferType())
                .destinationType(object.getDataDestination().getType())
                .properties(object.getProperties())
                .managedResources(object.isManagedResources())
                .protocol(object.getProtocol())
                .dataDestination(object.getDataDestination())
                .build();

        var callbacks = object.getCallbackAddresses().stream().map(c -> context.transform(c, CallbackAddress.class)).collect(Collectors.toList());

        return TransferRequest.Builder.newInstance()
                .dataRequest(dataRequest)
                .callbackAddresses(callbacks)
                .build();
    }
}
