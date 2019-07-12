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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.apache.ignite.transactions.TransactionOptimisticException;
import org.apache.training.ignite.model.Account;
import org.apache.training.ignite.model.Client;

public class AccountTransfer {
    ClientStorage clients = new ClientStorage();
    AccountStorage accounts = new AccountStorage();
    Ignite ignite = Ignition.ignite();

    /**
     * Transfers defined amount to some (default) acount by phone number, ignoring if it a card account or not.
     *
     * @param initiator Initiator.
     * @param from From.
     * @param toPhoneNum To phone number.
     * @param amount Amount.
     */
    public void transferToAccountByPhoneNumber(Client initiator, Account from, String toPhoneNum, long amount) {
        Client dest = clients.findClientByPhone(toPhoneNum);
        A.ensure(dest != null, "Destination account not found by phone [" + toPhoneNum + "]");

        A.ensure(initiator.id() == from.ownerClientId(), "Initiator is not an owner, power of attorney is not implemented");

        Client clientByPhone = clients.findClientByPhone(toPhoneNum);
        if (clientByPhone == null)
            throw new IllegalStateException("Can't find client by [" + toPhoneNum + "]");

        List<Account> allDestAccounts = this.accounts.findAccounts(clientByPhone.id());
        if(allDestAccounts.isEmpty())
            throw new IllegalStateException("Destination customer does not have accounts");

        Optional<Account> freshAcntSameCurrency = allDestAccounts.stream()
            .filter(acnt -> Objects.equals(acnt.currencyCode(), from.currencyCode()))
            .max(Comparator.comparing(Account::createdTs));

        if(!freshAcntSameCurrency.isPresent())
            throw new IllegalStateException("Destination customer does not have accounts in same currency, cross currency transfer is not implemented");

        Account to = freshAcntSameCurrency.get();

        Supplier<Boolean> riskSys = () -> {
            //emulatin some 3rd party system approval.
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(150));
            return true;
        };

        //TODO (Lab 3) Start and commit transaction, pessimistic, repeatable read, optionally set timeout
        // call doAccountTransfer in transcation context
        try (Transaction tx = ignite.transactions().txStart(
            TransactionConcurrency.PESSIMISTIC,
            TransactionIsolation.REPEATABLE_READ)) {

            doAccountTransfer(from.id(), to.id(), amount, riskSys);
            tx.commit();
        }
    }

    boolean tryTransfer(long fromAcntId, long toAcntId, long amount) {
        try (Transaction tx = ignite.transactions().txStart(
            TransactionConcurrency.OPTIMISTIC,
            TransactionIsolation.SERIALIZABLE)) {
            doAccountTransfer(fromAcntId, toAcntId, amount, () -> true);

            tx.commit();
        } catch (TransactionOptimisticException exception) {

            //Caused by: class org.apache.ignite.transactions.TransactionOptimisticException: Failed to prepare transaction, read/write conflict [key=8944184219396797440, keyCls=java.lang.Long, val=org.apache.training.ignite.model.Account@6682a148, valCls=org.apache.training.ignite.model.Account, cache=accounts, thread=IgniteThread [compositeRwLockIdx=15, stripe=7, plc=-1, executingEntryProcessor=false, holdsTopLock=false, name=sys-stripe-7-#8]]
            //Caused by: class org.apache.ignite.internal.transactions.IgniteTxOptimisticCheckedException: Failed to prepare transaction, read/write conflict [key=8944184219396797440, keyCls=java.lang.Long, val=org.apache.training.ignite.model.Account@6682a148, valCls=org.apache.training.ignite.model.Account, cache=accounts, thread=IgniteThread [compositeRwLockIdx=15, stripe=7, plc=-1, executingEntryProcessor=false, holdsTopLock=false, name=sys-stripe-7-#8]]
            return false;
        }

        return true;
    }

    private void doAccountTransfer(long fromAcntId, long toAcntId, long amount, Supplier<Boolean> riskSys) {
        Account srcAcnt;
        Account destAcnt;

        if (fromAcntId > toAcntId) {
            srcAcnt = accounts.load(fromAcntId);
            destAcnt = accounts.load(toAcntId);
        }
        else {
            destAcnt = accounts.load(toAcntId);
            srcAcnt = accounts.load(fromAcntId);
        }

        A.ensure(srcAcnt != null, "Source account not found");

        A.ensure(destAcnt != null, "Destination account not found");

        if (srcAcnt.balance() < amount)
            throw new IllegalStateException("Unable to debit account [" + srcAcnt.id() + "]. Not enought funds");

        Boolean approved = riskSys.get();
        if(!Boolean.TRUE.equals(approved))
            throw new IllegalStateException("Unable perform transfer, not approved by risk system");

        srcAcnt.balance(srcAcnt.balance() - amount);
        destAcnt.balance(destAcnt.balance() + amount);

        // Store updated account in cache.
        this.accounts.save(srcAcnt);
        this.accounts.save(destAcnt);
    }
}
