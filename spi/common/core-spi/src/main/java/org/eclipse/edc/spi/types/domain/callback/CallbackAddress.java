/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.edc.spi.types.domain.callback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The {@link CallbackAddress} contains information about users configured callbacks
 * that can be invoked in various state of the requests processing according to the filter provided
 * in {@link CallbackAddress#events}
 */
@JsonDeserialize(builder = CallbackAddress.Builder.class)
public class CallbackAddress {

    private String uri;

    private Set<String> events = new HashSet<>();

    private boolean transactional;


    public Set<String> getEvents() {
        return events;
    }

    public String getUri() {
        return uri;
    }

    public boolean isTransactional() {
        return transactional;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {


        private final CallbackAddress callbackAddress;

        protected Builder() {
            callbackAddress = new CallbackAddress();
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder uri(String url) {
            callbackAddress.uri = url;
            return this;
        }

        public Builder events(Set<String> events) {
            callbackAddress.events = events;
            return this;
        }

        public Builder transactional(boolean transactional) {
            callbackAddress.transactional = transactional;
            return this;
        }


        public CallbackAddress build() {
            Objects.requireNonNull(callbackAddress.uri, "URI should not be null");
            Objects.requireNonNull(callbackAddress.events, "Events should not be null");
            return callbackAddress;
        }
    }

}
