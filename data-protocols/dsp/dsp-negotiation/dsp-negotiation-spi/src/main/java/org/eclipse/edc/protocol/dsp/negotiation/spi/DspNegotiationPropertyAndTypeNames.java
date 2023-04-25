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

package org.eclipse.edc.protocol.dsp.negotiation.spi;

public interface DspNegotiationPropertyAndTypeNames {

    // namespace

    String DSPACE_SCHEMA = "https://w3id.org/dspace/v0.8/";
    String DSPACE_PREFIX = "dspace";

    // types

    String DSPACE_CONTRACT_NEGOTIATION = DSPACE_SCHEMA + "ContractNegotiation";
    String DSPACE_CONTRACT_NEGOTIATION_ERROR = DSPACE_SCHEMA + "ContractNegotiationError";

    // messages

    String DSPACE_NEGOTIATION_CONTRACT_REQUEST_MESSAGE = DSPACE_SCHEMA + "ContractRequestMessage";
    String DSPACE_NEGOTIATION_CONTRACT_OFFER_MESSAGE = DSPACE_SCHEMA + "ContractOfferMessage";
    String DSPACE_NEGOTIATION_EVENT_MESSAGE = DSPACE_SCHEMA + "ContractNegotiationEventMessage";
    String DSPACE_NEGOTIATION_AGREEMENT_MESSAGE = DSPACE_SCHEMA + "ContractAgreementMessage";
    String DSPACE_NEGOTIATION_AGREEMENT_VERIFICATION_MESSAGE = DSPACE_SCHEMA + "ContractAgreementVerificationMessage";
    String DSPACE_NEGOTIATION_TERMINATION_MESSAGE = DSPACE_SCHEMA + "ContractNegotiationTerminationMessage";

    // properties

    String DSPACE_NEGOTIATION_PROPERTY_PROCESS_ID = DSPACE_SCHEMA + "processId";
    String DSPACE_NEGOTIATION_PROPERTY_EVENT_TYPE = DSPACE_SCHEMA + "eventType";
    String DSPACE_NEGOTIATION_PROPERTY_CODE = DSPACE_SCHEMA + "code";
    String DSPACE_NEGOTIATION_PROPERTY_REASON = DSPACE_SCHEMA + "reason";
    String DSPACE_NEGOTIATION_PROPERTY_CALLBACK_ADDRESS = DSPACE_SCHEMA + "callbackAddress";
    String DSPACE_NEGOTIATION_PROPERTY_STATE = DSPACE_SCHEMA + "state";
    String DSPACE_NEGOTIATION_PROPERTY_CHECKSUM = DSPACE_SCHEMA + "checksum";
    String DSPACE_NEGOTIATION_PROPERTY_AGREEMENT = DSPACE_SCHEMA + "agreement";
    String DSPACE_NEGOTIATION_PROPERTY_OFFER = DSPACE_SCHEMA + "offer";
    String DSPACE_NEGOTIATION_PROPERTY_DATASET = DSPACE_SCHEMA + "dataSet";
    String DSPACE_NEGOTIATION_PROPERTY_TIMESTAMP = DSPACE_SCHEMA + "timestamp";

    // event types

    String DSPACE_NEGOTIATION_PROPERTY_EVENT_TYPE_ACCEPTED = DSPACE_SCHEMA + "ACCEPTED";
    String DSPACE_NEGOTIATION_PROPERTY_EVENT_TYPE_FINALIZED = DSPACE_SCHEMA + "FINALIZED";

    // negotiation states

    String DSPACE_NEGOTIATION_STATE_REQUESTED = DSPACE_SCHEMA + "REQUESTED";
    String DSPACE_NEGOTIATION_STATE_OFFERED = DSPACE_SCHEMA + "OFFERED";
    String DSPACE_NEGOTIATION_STATE_ACCEPTED = DSPACE_SCHEMA + "ACCEPTED";
    String DSPACE_NEGOTIATION_STATE_AGREED = DSPACE_SCHEMA + "AGREED";
    String DSPACE_NEGOTIATION_STATE_VERIFIED = DSPACE_SCHEMA + "VERIFIED";
    String DSPACE_NEGOTIATION_STATE_FINALIZED = DSPACE_SCHEMA + "FINALIZED";
    String DSPACE_NEGOTIATION_STATE_TERMINATED = DSPACE_SCHEMA + "TERMINATED";

    // other

    String CRED_CREDENTIAL_SUBJECT = "cred:credentialSubject";
    String SEC_PROOF = "sec:proof";
}
