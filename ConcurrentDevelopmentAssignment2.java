import java.util.Random;
import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Jobs {
    private String id;
    private String branch;
    private int balance;
    private int jobType;
    Random rand = new Random();

    // construstor
    public Jobs() {
    }

    public Jobs(String id, String branch, int balance) {
        this.id = id;
        this.branch = branch;
        this.balance = balance;

    }

    public Jobs(String id, String branch, int balance, int jobType) {
        this.id = id;
        this.branch = branch;
        this.balance = balance;
        this.jobType = jobType;
    }

    // getter & setter
    public String getID() {
        return id;
    }

    public int getLockID() {
        return Integer.parseInt(id) - 10000;
    }

    public String getBranch() {
        return branch;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

}

class Bank implements Runnable {

    private Jobs c1;
    private Jobs c2;
    private static final int numCustomer = 20; // predefined num of customer
    private static final Lock[] acclock = new ReentrantLock[numCustomer];
    Random rand = new Random();

    static { // make sure the lock is only create once
        for (int i = 0; i < numCustomer; i++) {
            acclock[i] = new ReentrantLock();
        }
    }

    public Bank(Jobs c1, Jobs c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public void run() {
        System.out.println("Clerk " + Thread.currentThread().getId() + " : " + Thread.currentThread().getState());
        c1.setJobType(rand.nextInt(4) + 1); // random choose one operation
        operation(c1, c2);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    // operation
    public void operation(Jobs c, Jobs c2) {
        if (acclock[c.getLockID()].tryLock()) { // if first account lock is free, then operate them except transfer
            try {
                switch (c.getJobType()) {
                    case 1:
                        deposit(c);
                        break;

                    case 2:
                        withdraw(c);
                        break;

                    case 3:
                        display(c);
                        break;
                    case 4:
                        transfer(c, c2);
                        break;
                }
            } finally {
                acclock[c.getLockID()].unlock();
            }
        }
    }

    public void deposit(Jobs c) {
        int money = rand.nextInt(1000) + 1; // random money below 1000
        c.setBalance(c.getBalance() + money);
        System.out.println("Customer " + c.getID() + " successfully deposited " + money + " by Clerk "
                + Thread.currentThread().getId());
    }

    public void withdraw(Jobs c) {
        int money = rand.nextInt(c.getBalance()) + 1;
        if (money <= c.getBalance()) {
            c.setBalance(c.getBalance() - money);
            System.out.println("Customer " + c.getID() + " successfully withdrew " + money + " by Clerk "
                    + Thread.currentThread().getId());
        } else {
            System.out.println("Customer " + c.getID() + " have not enough money to withdrew " + money + "by Clerk "
                    + Thread.currentThread().getId());
        }
    }

    public int display(Jobs c) {
        System.out.println("Customer " + c.getID() + " has " + c.getBalance()
                + " in his/her bank account and displayed by Clerk " + Thread.currentThread().getId());
        return c.getBalance();
    }

    public void transfer(Jobs c1, Jobs c2) {
        if (acclock[c2.getLockID()].tryLock()) {
            try {
                int money = rand.nextInt(c1.getBalance()) + 1;
                if (money <= c1.getBalance()) {
                    c1.setBalance(c1.getBalance() - money);
                    c2.setBalance(c2.getBalance() + money);
                    System.out.println("Customer " + c1.getID() + " transferred " + money + " to customer " + c2.getID()
                            + " by Clerk " + Thread.currentThread().getId());
                } else {
                    System.out.println("Customer " + c1.getID() + " is not enough money to transferred to customer "
                            + c2.getID() + " for " + money + " by Clerk " + Thread.currentThread().getId());
                }
            } finally {
                acclock[c2.getLockID()].unlock();
            }
        }
    }
}

public class ConcurrentDevelopmentAssignment2 {

    public static final int numCustomer = 20; // predefined amount of customers

    public static void main(String[] args) {

        Random rand = new Random();
        String[] branches = { "Dublin", "Cork", "Galway" };
        Jobs[] c = new Jobs[numCustomer];

        for (int i = 0; i < numCustomer; i++) { // random generate customer details
            String id = String.valueOf(10000 + i);
            String branch = branches[rand.nextInt(3)];
            int balance = rand.nextInt(1000);
            c[i] = new Jobs(id, branch, balance);
            // System.out.println(c[i].getID() + c[i].getBranch()); //code for me to debug
        }

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService clerkPool = Executors.newFixedThreadPool(numThreads);
        ArrayList<Jobs> customerInBranch = new ArrayList<Jobs>();

        String currentBranch = branches[rand.nextInt(3)];
        System.out.println("********************");
        System.out.println(currentBranch + " Bank is now open.");
        System.out.println("********************");

        customerInBranch.clear(); // clear previous list

        for (int i = 0; i < numCustomer; i++) {
            if (c[i].getBranch().equals(currentBranch)) { // this is figure out those customer who in the active branch
                customerInBranch.add(c[i]);
                // System.out.println(c[i].getID() + c[i].getBranch()); //code for me to debug
            }
        }

        for (int i = 0; i < numThreads; i++) {
            int first = rand.nextInt(customerInBranch.size());
            int sec = 0;
            do {
                sec = rand.nextInt(customerInBranch.size()); // ensure two accs are different
            } while (sec == first);
            Bank bankTask = new Bank(customerInBranch.get(first), customerInBranch.get(sec));
            clerkPool.submit(bankTask);
        }

        clerkPool.shutdown();

        try {
            if (!clerkPool.awaitTermination(60, TimeUnit.SECONDS)) { // wait all task complete
                clerkPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            clerkPool.shutdownNow();
            ex.printStackTrace();
        }

    }

}