/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package org.apache.training.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.training.ignite.runners.IgniteConfigUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 */
public abstract class AbstractLabIntegrationTest {
    private static Ignite ignite;

    @BeforeClass
    public static void connectToIgnite() {
        IgniteConfiguration cfg = new IgniteConfiguration();

        IgniteConfigUtil.commonConfig(cfg);

        cfg.setClientMode(true);

        ignite = Ignition.start(cfg);
    }

    @AfterClass
    public static void closeClient() {
        ignite.close();
    }
}
