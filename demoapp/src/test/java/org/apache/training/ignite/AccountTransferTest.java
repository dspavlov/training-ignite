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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.training.ignite.model.Account;
import org.apache.training.ignite.model.Client;
import org.apache.training.ignite.runners.StartServerNodeRunner;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <b>Note:</b> Before running test start server node by running {@link StartServerNodeRunner}.
 */
public class AccountTransferTest extends AbstractLabIntegrationTest {
    /** Storage. */
    private ClientStorage clients = new ClientStorage();

    /** Storage. */
    private AccountStorage accounts = new AccountStorage();

    /** Account. */
    private AccountTransfer transfer = new AccountTransfer();

    /**
     * Simple straightforward account transfer
     */
    @Test
    public void testAcntTransfer() {
        int srcId = 10001;
        Client src = new Client(srcId, "Initiator", "init@test.test", "+7931332112321");
        String phoneNum = "+79217502321";
        int destId = 10002;
        Client dest = new Client(destId, "Destionation", "dest@test.test", phoneNum);

        clients.saveAll(Lists.newArrayList(src, dest));

        int initBalanceSrc = 10000;
        Account srcAcnt = new Account()
            .ownerClientId(srcId)
            .balance(initBalanceSrc);

        int initBalanceDest = 320;
        Account destAcnt = new Account()
            .ownerClientId(destId)
            .balance(initBalanceDest);

        accounts.saveAll(Lists.newArrayList(srcAcnt, destAcnt));

        int amount = 9999;
        transfer.transferToAccountByPhoneNumber(src, srcAcnt, phoneNum, amount);

        assertEquals(initBalanceDest + amount, accounts.load(destAcnt.id()).balance());
        assertEquals(initBalanceSrc - amount, accounts.load(srcAcnt.id()).balance());

        long resultingBalance = accounts.load(destAcnt.id()).balance() + accounts.load(srcAcnt.id()).balance();

        assertEquals(initBalanceDest + initBalanceSrc, resultingBalance);
    }


    @Test
    public void testMultithreadAcntTrasnfer() throws InterruptedException {
        int srcId = 702301;
        Client src = new Client(srcId, "Initiator Multithread", "init@test.test", "+7931332112321");

        String destPhoneNum = "+79217533321";
        int destId = 704302;
        Client dest = new Client(destId, "Destionation Multithread", "dest@test.test", destPhoneNum);

        clients.saveAll(Lists.newArrayList(src, dest));

        int initBalanceSrc = 20000;
        Account srcAcnt = new Account()
            .id(23432590432L) // using fixed ID to overwrite same entry in case of test restart
            .ownerClientId(srcId)
            .balance(initBalanceSrc);

        int initBalanceDest = 20000;
        Account destAcnt = new Account()
            .id(324234234L) // using fixed ID to overwrite same entry in case of test restart
            .ownerClientId(destId)
            .balance(initBalanceDest);

        accounts.saveAll(Lists.newArrayList(srcAcnt, destAcnt));

        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        int daemonTx = 1000;
        for (int i = 0; i < daemonTx; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                latch.countDown();

                int i1 = ThreadLocalRandom.current().nextInt(10000);
                return i1 % 7 > 3
                    ? transfer.tryTransfer(srcAcnt.id(), destAcnt.id(), 1)
                    : transfer.tryTransfer(destAcnt.id(), srcAcnt.id(), 1);

            }));
        }

        for (int j = 0; j < 10; j++) {
            System.out.println("Running account transfer " + j +
                " Dest before: " + accounts.load(destAcnt.id()).balance() +
                " Source before:" + accounts.load(srcAcnt.id()).balance());

            latch.await();
            transfer.transferToAccountByPhoneNumber(src, srcAcnt, destPhoneNum, 71);

            System.out.println("Completed account transfer " + j +
                " Dest after: " + accounts.load(destAcnt.id()).balance() +
                " Source after:" + accounts.load(srcAcnt.id()).balance());

        }

        AtomicInteger cnt = new AtomicInteger();
        futures.forEach(f -> {
            try {
                Boolean res = f.get();
                if(Boolean.TRUE.equals(res))
                    cnt.incrementAndGet();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new RuntimeException(e);
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Successfully applied [" + cnt.get() + "] Transactions from [" + daemonTx + "]");

        long resultingBalance = accounts.load(destAcnt.id()).balance() + accounts.load(srcAcnt.id()).balance();

        assertEquals(initBalanceDest + initBalanceSrc, resultingBalance);
    }
}
