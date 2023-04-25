/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
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

package org.eclipse.edc.spi.types.domain.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.util.Objects;

/**
 * A query sent to a remote system.
 *
 * @deprecated only used by ids multipart api/dispatcher. Can be removed with IDS modules.
 */
@Deprecated(forRemoval = true)
@JsonDeserialize(builder = QueryRequest.Builder.class)
public class QueryRequest implements RemoteMessage {
    private String protocol;
    private String connectorId;
    private String callbackAddress;
    private String queryLanguage;
    private String query;

    private QueryRequest() {
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public String getQueryLanguage() {
        return queryLanguage;
    }

    public String getQuery() {
        return query;
    }

    public String getConnectorId() {
        return connectorId;
    }

    @Override
    public String getCallbackAddress() {
        return callbackAddress;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final QueryRequest queryRequest;

        private Builder() {
            queryRequest = new QueryRequest();
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder protocol(String protocol) {
            queryRequest.protocol = protocol;
            return this;
        }

        public Builder connectorId(String connectorId) {
            queryRequest.connectorId = connectorId;
            return this;
        }

        public Builder callbackAddress(String callbackAddress) {
            queryRequest.callbackAddress = callbackAddress;
            return this;
        }

        public Builder queryLanguage(String language) {
            queryRequest.queryLanguage = language;
            return this;
        }

        public Builder query(String query) {
            queryRequest.query = query;
            return this;
        }

        public QueryRequest build() {
            Objects.requireNonNull(queryRequest.protocol, "protocol");
            Objects.requireNonNull(queryRequest.connectorId, "connectorId");
            Objects.requireNonNull(queryRequest.queryLanguage, "queryLanguage");
            Objects.requireNonNull(queryRequest.query, "query");
            Objects.requireNonNull(queryRequest.callbackAddress, "callbackAddress");
            return queryRequest;
        }
    }
}
