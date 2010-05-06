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

package org.apache.synapse.config.xml;

public class RMSequenceMediatorSerializationTest extends AbstractTestCase {

    private RMSequenceMediatorFactory rmSequenceMediatorFactory;
    private RMSequenceMediatorSerializer rmSequenceMediatorSerializer;

    public RMSequenceMediatorSerializationTest() {
        rmSequenceMediatorFactory = new RMSequenceMediatorFactory();
        rmSequenceMediatorSerializer = new RMSequenceMediatorSerializer();
    }

    public void testRMSequenceSerializationTestScenarioOne() {
        String inputXml = "<RMSequence xmlns=\"http://synapse.apache.org/ns/2010/04/configuration\" " +
                          "single=\"true\" version=\"1.0\"/>";
        assertTrue(serialization(inputXml, rmSequenceMediatorFactory, rmSequenceMediatorSerializer));
        assertTrue(serialization(inputXml, rmSequenceMediatorSerializer));
    }

    public void testRMSequenceSerializationTestScenarioTwo() {
        String inputXml = "<RMSequence xmlns=\"http://synapse.apache.org/ns/2010/04/configuration\" " +
                          "correlation=\"get-property('To')\" last-message=\"get-property('To')\" version=\"1.1\"/>";
        assertTrue(serialization(inputXml, rmSequenceMediatorFactory, rmSequenceMediatorSerializer));
        assertTrue(serialization(inputXml, rmSequenceMediatorSerializer));
    }
}
