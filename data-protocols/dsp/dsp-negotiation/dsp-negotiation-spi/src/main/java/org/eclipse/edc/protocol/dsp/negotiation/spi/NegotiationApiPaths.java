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

/**
 * Provides API paths used by negotiation controllers (api) and delegates (dispatcher).
 */
public interface NegotiationApiPaths {

    String BASE_PATH = "/negotiations/";
    String INITIAL_CONTRACT_REQUEST = "request";
    String CONTRACT_REQUEST = "/request";
    String CONTRACT_OFFER = "/offers";
    String EVENT = "/events";
    String AGREEMENT = "/agreement";
    String VERIFICATION = "/verification";
    String TERMINATION = "/termination";
}
