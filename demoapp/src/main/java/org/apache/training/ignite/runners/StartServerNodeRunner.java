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
package org.apache.training.ignite.runners;

import java.io.File;
import java.io.IOException;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ConnectorConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.training.ignite.AccountStorage;
import org.apache.training.ignite.ClientStorage;

/**
 *
 */
public class StartServerNodeRunner {
    /**
     * @param args Args.
     */
    public static void main(String[] args) throws IOException {
        CacheConfiguration ccfg = ClientStorage.cacheConfig();

        CacheConfiguration acntCfg = AccountStorage.cacheConfig();

        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setCacheConfiguration(ccfg, acntCfg);

        cfg.setConnectorConfiguration(new ConnectorConfiguration());

        IgniteConfigUtil.commonConfig(cfg);

        // Todo (Lab 6) Create new DataStorageConfiguration() and provide it to Ignite config
        DataStorageConfiguration dsCfg = new DataStorageConfiguration();

        // Todo (Lab 6) Create new DataRegionConfiguration() and provide it to data storage as default region
        DataRegionConfiguration regConf = new DataRegionConfiguration();

        // Todo (Lab 6) Enable persistence for region, set Max size, e.g. to 256Mbytes
        regConf.setPersistenceEnabled(true).setMaxSize(256 * 1024L * 1024);

        dsCfg.setDefaultDataRegionConfiguration(regConf);

        cfg.setDataStorageConfiguration(dsCfg);

        // Todo (Lab 6) Configure Ignite Work Directory to any local folder
        cfg.setWorkDirectory(new File(".").getAbsolutePath());

        try (Ignite ignite = Ignition.start(cfg)) {
            System.out.print("Press any key to stop server.");
            System.in.read();
        }
    }

}
