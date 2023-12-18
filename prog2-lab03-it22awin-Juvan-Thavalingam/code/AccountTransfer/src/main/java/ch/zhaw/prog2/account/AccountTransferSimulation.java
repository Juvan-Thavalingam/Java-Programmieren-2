package ch.zhaw.prog2.account;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AccountTransferSimulation {

    private static final int ITERATIONS = 10000;

    public static void main(String[] args) throws InterruptedException {

        Account account1 = new Account(1, 10);
        Account account2 = new Account(2, 10);
        Account account3 = new Account(3, 999999);

        System.out.println("Start Balance:");
        System.out.println("- Account1 = " + account1.getBalance());
        System.out.println("- Account2 = " + account2.getBalance());
        System.out.println("- Account3 = " + account3.getBalance());
        System.out.println("Summed up balances of all accounts: " +
                (account1.getBalance() + account2.getBalance() + account3.getBalance()));
        System.out.println("Start of Transaction");

        AccountTransferTask task1 = new AccountTransferTask(account3, account1, 2);
        AccountTransferTask task2 = new AccountTransferTask(account3, account2, 1);
        AccountTransferTask task3 = new AccountTransferTask(account2, account1, 2);

        // create ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(8);

        // execute transactions on accounts
        System.out.println("Submitting...");
        for (int count = 0; count < ITERATIONS; count++) {
            executor.execute(task1);
            executor.execute(task2);
            executor.execute(task3);
        }
        System.out.println("Working...");

        executor.shutdown();
        // wait for completion
        boolean completed = executor.awaitTermination(20, TimeUnit.SECONDS);

        if (completed) {
            System.out.println("Transactions completed!");
        } else {
            System.out.println("Transactions timed out!");
            executor.shutdownNow();

        }

        // Print end statistics
        System.out.println("Amount transferred (when enough on fromAccount):");
        System.out.println("- Task 1 = " + task1.getTotalTransfer());
        System.out.println("- Task 2 = " + task2.getTotalTransfer());
        System.out.println("- Task 3 = " + task3.getTotalTransfer());
        System.out.println("End Balance:");
        System.out.println("- Account1 = " + account1.getBalance());
        System.out.println("- Account2 = " + account2.getBalance());
        System.out.println("- Account3 = " + account3.getBalance());
        System.out.println("Summed up balances of all accounts (should be the same as at start): " +
                (account1.getBalance() + account2.getBalance() + account3.getBalance()));

    }
}
