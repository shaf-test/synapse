/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.mediators.transform.url;

import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.axis2.addressing.EndpointReference;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A mediator capable of rewriting URLs in messages. The input URL can be
 * extracted from the To header of the message or any of the message properties.
 * The rewritten URL can be set as the To header or a message property. URL
 * rewriting is carried out based on a user defined set of rules. This mediator
 * support rewriting entire URLs as well as URL fragments.
 */
public class URLRewriteMediator extends AbstractMediator {

    private List<RewriteRule> rules = new ArrayList<RewriteRule>();
    private String inputProperty;
    private String outputProperty;

    public boolean mediate(MessageContext messageContext) {
        URIFragments fragments;
        URI inputURI = getInputAddress(messageContext);
        if (inputURI != null) {
            fragments = new URIFragments(inputURI);
        } else {
            fragments = new URIFragments();
        }

        Map<String, String> headers = getHeaders(messageContext);

        try {
            for (RewriteRule r : rules) {
                r.rewrite(fragments, messageContext, headers);
            }

            if (outputProperty != null) {
                messageContext.setProperty(outputProperty, fragments.toURIString());
            } else {
                messageContext.setTo(new EndpointReference(fragments.toURIString()));
            }
        } catch (URISyntaxException e) {
            handleException("Error while constructing a URI from the fragments", e, messageContext);
        }
        return true;
    }

    private URI getInputAddress(MessageContext messageContext) {
        String uriString = null;
        if (inputProperty != null) {
            Object prop = messageContext.getProperty(inputProperty);
            if (prop != null && prop instanceof String) {
                uriString = (String) prop;
            }
        } else if (messageContext.getTo() != null) {
            uriString = messageContext.getTo().getAddress();
        }

        if (uriString != null) {
            try {
                return new URI(uriString);
            } catch (URISyntaxException e) {
                handleException("Malformed input URI: " + uriString, e, messageContext);
            }
        }
        return null;
    }

    private Map<String, String> getHeaders(MessageContext synCtx) {
        Axis2MessageContext axis2smc = (Axis2MessageContext) synCtx;
        org.apache.axis2.context.MessageContext axis2MessageCtx =
                axis2smc.getAxis2MessageContext();
        Object headers = axis2MessageCtx.getProperty(
                org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        Map<String, String> evaluatorHeaders = new HashMap<String, String>();

        if (headers != null && headers instanceof Map) {
            Map headersMap = (Map) headers;
            for (Object key : headersMap.keySet()) {
                if (key instanceof String && headersMap.get(key) instanceof String) {
                    evaluatorHeaders.put((String) key, (String) headersMap.get(key));
                }
            }
        }
        return evaluatorHeaders;
    }

    public void addRule(RewriteRule rule) {
        rules.add(rule);
    }

    public List<RewriteRule> getRules() {
        return rules;
    }

    public String getInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(String inputProperty) {
        this.inputProperty = inputProperty;
    }

    public String getOutputProperty() {
        return outputProperty;
    }

    public void setOutputProperty(String outputProperty) {
        this.outputProperty = outputProperty;
    }
}
