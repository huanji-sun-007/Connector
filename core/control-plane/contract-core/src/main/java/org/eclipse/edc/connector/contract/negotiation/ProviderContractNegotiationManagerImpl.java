/*
 *  Copyright (c) 2021 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - extended method implementation
 *       Daimler TSS GmbH - fixed contract dates to epoch seconds
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - refactor
 *       ZF Friedrichshafen AG - fixed contract validity issue
 *
 */

package org.eclipse.edc.connector.contract.negotiation;

import io.opentelemetry.extension.annotations.WithSpan;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.connector.contract.spi.negotiation.ProviderContractNegotiationManager;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreementMessage;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractNegotiationEventMessage;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationTerminationMessage;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequestMessage;
import org.eclipse.edc.connector.contract.spi.types.negotiation.command.ContractNegotiationCommand;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.statemachine.StateMachineManager;
import org.eclipse.edc.statemachine.StateProcessorImpl;

import java.util.function.Function;

import static java.lang.String.format;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.AGREEING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.FINALIZING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.OFFERING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.TERMINATING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.VERIFIED;

/**
 * Implementation of the {@link ProviderContractNegotiationManager}.
 */
public class ProviderContractNegotiationManagerImpl extends AbstractContractNegotiationManager implements ProviderContractNegotiationManager {

    private StateMachineManager stateMachineManager;

    private ProviderContractNegotiationManagerImpl() {
    }

    public void start() {
        stateMachineManager = StateMachineManager.Builder.newInstance("provider-contract-negotiation", monitor, executorInstrumentation, waitStrategy)
                .processor(processNegotiationsInState(OFFERING, this::processOffering))
                .processor(processNegotiationsInState(AGREEING, this::processAgreeing))
                .processor(processNegotiationsInState(VERIFIED, this::processVerified))
                .processor(processNegotiationsInState(FINALIZING, this::processFinalizing))
                .processor(processNegotiationsInState(TERMINATING, this::processTerminating))
                .processor(onCommands(this::processCommand))
                .build();

        stateMachineManager.start();
    }

    public void stop() {
        if (stateMachineManager != null) {
            stateMachineManager.stop();
        }
    }

    @Override
    public void enqueueCommand(ContractNegotiationCommand command) {
        commandQueue.enqueue(command);
    }

    private StateProcessorImpl<ContractNegotiation> processNegotiationsInState(ContractNegotiationStates state, Function<ContractNegotiation, Boolean> function) {
        return new StateProcessorImpl<>(() -> negotiationStore.nextForState(state.code(), batchSize), telemetry.contextPropagationMiddleware(function));
    }

    private StateProcessorImpl<ContractNegotiationCommand> onCommands(Function<ContractNegotiationCommand, Boolean> process) {
        return new StateProcessorImpl<>(() -> commandQueue.dequeue(5), process);
    }

    private boolean processCommand(ContractNegotiationCommand command) {
        return commandProcessor.processCommandQueue(command);
    }

    /**
     * Processes {@link ContractNegotiation} in state OFFERING. Tries to send the current offer to the
     * respective consumer. If this succeeds, the ContractNegotiation is transitioned to state OFFERED. Else,
     * it is transitioned to OFFERING for a retry.
     *
     * @return true if processed, false elsewhere
     */
    @WithSpan
    private boolean processOffering(ContractNegotiation negotiation) {
        var currentOffer = negotiation.getLastContractOffer();

        var contractOfferRequest = ContractRequestMessage.Builder.newInstance()
                .protocol(negotiation.getProtocol())
                .connectorId(negotiation.getCounterPartyId())
                .callbackAddress(negotiation.getCounterPartyAddress())
                .contractOffer(currentOffer)
                .processId(negotiation.getCorrelationId())
                .build();

        return entityRetryProcessFactory.doAsyncProcess(negotiation, () -> dispatcherRegistry.send(Object.class, contractOfferRequest))
                .entityRetrieve(negotiationStore::findById)
                .onDelay(this::breakLease)
                .onSuccess((n, result) -> transitionToOffered(n))
                .onFailure((n, throwable) -> transitionToOffering(n))
                .onRetryExhausted((n, throwable) -> transitionToTerminating(n, format("Failed to send %s to consumer: %s", contractOfferRequest.getClass().getSimpleName(), throwable.getMessage())))
                .execute("[Provider] send counter offer");
    }

    /**
     * Processes {@link ContractNegotiation} in state TERMINATING. Tries to send a contract rejection to the respective
     * consumer. If this succeeds, the ContractNegotiation is transitioned to state TERMINATED. Else, it is transitioned
     * to TERMINATING for a retry.
     *
     * @return true if processed, false elsewhere
     */
    @WithSpan
    private boolean processTerminating(ContractNegotiation negotiation) {
        var rejection = ContractNegotiationTerminationMessage.Builder.newInstance()
                .protocol(negotiation.getProtocol())
                .connectorId(negotiation.getCounterPartyId())
                .callbackAddress(negotiation.getCounterPartyAddress())
                .processId(negotiation.getCorrelationId())
                .rejectionReason(negotiation.getErrorDetail())
                .build();

        return entityRetryProcessFactory.doAsyncProcess(negotiation, () -> dispatcherRegistry.send(Object.class, rejection))
                .entityRetrieve(negotiationStore::findById)
                .onDelay(this::breakLease)
                .onSuccess((n, result) -> transitionToTerminated(n))
                .onFailure((n, throwable) -> transitionToTerminating(n))
                .onRetryExhausted((n, throwable) -> transitionToTerminated(n, format("Failed to send %s to consumer: %s", rejection.getClass().getSimpleName(), throwable.getMessage())))
                .execute("[Provider] send rejection");
    }

    /**
     * Processes {@link ContractNegotiation} in state CONFIRMING. Tries to send a contract agreement to the respective
     * consumer. If this succeeds, the ContractNegotiation is transitioned to state CONFIRMED. Else, it is transitioned
     * to CONFIRMING for a retry.
     *
     * @return true if processed, false elsewhere
     */
    @WithSpan
    private boolean processAgreeing(ContractNegotiation negotiation) {
        var retrievedAgreement = negotiation.getContractAgreement();

        ContractAgreement agreement;
        Policy policy;
        if (retrievedAgreement == null) {
            var lastOffer = negotiation.getLastContractOffer();

            var contractId = ContractId.parse(lastOffer.getId());
            if (!contractId.isValid()) {
                monitor.severe("ProviderContractNegotiationManagerImpl.checkConfirming(): Offer Id not correctly formatted.");
                return false;
            }
            var definitionId = contractId.definitionPart();

            policy = lastOffer.getPolicy();

            agreement = ContractAgreement.Builder.newInstance()
                    .id(ContractId.createContractId(definitionId))
                    .contractStartDate(lastOffer.getContractStart().toEpochSecond())
                    .contractEndDate(lastOffer.getContractEnd().toEpochSecond())
                    .contractSigningDate(clock.instant().getEpochSecond())
                    .providerAgentId(String.valueOf(lastOffer.getProvider()))
                    .consumerAgentId(String.valueOf(lastOffer.getConsumer()))
                    .policy(policy)
                    .assetId(lastOffer.getAsset().getId())
                    .build();
        } else {
            agreement = retrievedAgreement;
            policy = agreement.getPolicy();
        }

        var request = ContractAgreementMessage.Builder.newInstance()
                .protocol(negotiation.getProtocol())
                .connectorId(negotiation.getCounterPartyId())
                .callbackAddress(negotiation.getCounterPartyAddress())
                .contractAgreement(agreement)
                .processId(negotiation.getCorrelationId())
                .policy(policy)
                .build();

        return entityRetryProcessFactory.doAsyncProcess(negotiation, () -> dispatcherRegistry.send(Object.class, request))
                .entityRetrieve(negotiationStore::findById)
                .onDelay(this::breakLease)
                .onSuccess((n, result) -> transitionToAgreed(n, agreement))
                .onFailure((n, throwable) -> transitionToAgreeing(n))
                .onRetryExhausted((n, throwable) -> transitionToTerminating(n, format("Failed to send %s to consumer: %s", request.getClass().getSimpleName(), throwable.getMessage())))
                .execute("[Provider] send agreement");
    }

    /**
     * Processes {@link ContractNegotiation} in state AGREED. For the deprecated ids-protocol, it's transitioned
     * to FINALIZED, otherwise to VERIFYING to make the verification process start.
     *
     * @return true if processed, false otherwise
     */
    @WithSpan
    private boolean processVerified(ContractNegotiation negotiation) {
        transitionToFinalizing(negotiation);
        return true;
    }

    /**
     * Processes {@link ContractNegotiation} in state OFFERING. Tries to send the current offer to the
     * respective consumer. If this succeeds, the ContractNegotiation is transitioned to state OFFERED. Else,
     * it is transitioned to OFFERING for a retry.
     *
     * @return true if processed, false elsewhere
     */
    @WithSpan
    private boolean processFinalizing(ContractNegotiation negotiation) {
        var message = ContractNegotiationEventMessage.Builder.newInstance()
                .type(ContractNegotiationEventMessage.Type.FINALIZED)
                .protocol(negotiation.getProtocol())
                .callbackAddress(negotiation.getCounterPartyAddress())
                .processId(negotiation.getCorrelationId())
                .build();

        return entityRetryProcessFactory.doAsyncProcess(negotiation, () -> dispatcherRegistry.send(Object.class, message))
                .entityRetrieve(negotiationStore::findById)
                .onDelay(this::breakLease)
                .onSuccess((n, result) -> transitionToFinalized(n))
                .onFailure((n, throwable) -> transitionToFinalizing(n))
                .onRetryExhausted((n, throwable) -> transitionToTerminating(n, format("Failed to send %s to consumer: %s", message.getClass().getSimpleName(), throwable.getMessage())))
                .execute("[Provider] send finalization");
    }

    /**
     * Builder for ProviderContractNegotiationManagerImpl.
     */
    public static class Builder extends AbstractContractNegotiationManager.Builder<ProviderContractNegotiationManagerImpl> {


        private Builder() {
            super(new ProviderContractNegotiationManagerImpl());
        }

        public static Builder newInstance() {
            return new Builder();
        }
    }
}
