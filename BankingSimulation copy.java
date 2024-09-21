import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount {
    private int balance;
    private final Lock lock = new ReentrantLock();  //lock for thread sync

    public BankAccount(int balance) {
        this.balance = balance;
    }

    public void deposit(int amount) {
        lock.lock();  //lock before modifying balance
        try {
            System.out.println("Depositing $" + amount);
            int newBalance = balance + amount;
            Thread.sleep(1000);  //delay
            balance = newBalance;
            System.out.println("Deposit complete. New balance: $" + balance);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();  //lock released
        }
    }

    public void withdraw(int amount) {
        lock.lock();  //lock before modifying balance
        try {
            if (balance >= amount) {
                System.out.println("Withdrawing $" + amount);
                int newBalance = balance - amount;
                Thread.sleep(1000);
                balance = newBalance;
                System.out.println("Withdrawal complete. New balance: $" + balance);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();  //lock released
        }
    }

    public int getBalance() {
        return balance;
    }
}

class CustomerTransaction implements Runnable {
    private final BankAccount account;
    private final String transactionType;
    private final int amount;

    public CustomerTransaction(BankAccount account, String transactionType, int amount) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    @Override
    public void run() {
        if ("deposit".equalsIgnoreCase(transactionType)) {
            account.deposit(amount);
        } else if ("withdraw".equalsIgnoreCase(transactionType)) {
            account.withdraw(amount);
        }
    }
}

public class BankingSimulation {
    public static void main(String[] args) {
        //create bank account with initial balance
        BankAccount account = new BankAccount(10);

        //make transactions using threads
        Thread[] transactions = new Thread[] {
                new Thread(new CustomerTransaction(account, "deposit", 5)),
                new Thread(new CustomerTransaction(account, "withdraw", 3)),
                new Thread(new CustomerTransaction(account, "deposit", 10)),
                new Thread(new CustomerTransaction(account, "withdraw", 1)),
                new Thread(new CustomerTransaction(account, "deposit", 3))
        };

        //start threads
        for (Thread t : transactions) {
            t.start();
        }

        //wait for threads to finish before printing balance
        for (Thread t : transactions) {
            try {
                t.join();  //syncs the threads with main thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //print final balance
        System.out.println("Final balance: $" + account.getBalance());
    }
}
