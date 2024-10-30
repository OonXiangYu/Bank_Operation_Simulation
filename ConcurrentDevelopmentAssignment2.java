import java.util.Random;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;

class Jobs{

    private String id;
    private String branch;
    private int balance;
    private final Lock lock;
    private int jobType;
    Random rand = new Random();

    //construstor
    public Jobs(){
        this.lock = new ReentrantLock();
    }

    public Jobs(String id, String branch, int balance){
        this.id = id;
        this.branch = branch;
        this.balance = balance;
        this.lock = new ReentrantLock();
    }

    public Jobs(String id, String branch, int balance, int jobType){
        this.id = id;
        this.branch = branch;
        this.balance = balance;
        this.jobType = jobType;
        this.lock = new ReentrantLock();
    }

    //getter & setter
    public String getID(){
        return id;
    }

    public String getBranch(){
        return branch;
    }

    public int getBalance(){
        return balance;
    }

    public void setBalance(int balance){
        this.balance = balance;
    }

    public int getJobType(){
        return jobType;
    }

    //operation
    public void operation(Jobs c){

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
        }
    }

    public void deposit(Jobs c){
        lock.lock();

        try{
            c.setBalance(c.getBalance() + rand.nextInt(1000));
        }finally{
            lock.unlock();
        }
    }

    public void withdraw(Jobs c){
        lock.lock();

        try{
            c.setBalance(c.getBalance() - rand.nextInt(c.getBalance() + 1));
        }finally{
            lock.unlock();
        }
    }

    public int display(Jobs c){
        lock.lock();

        try{
            return c.getBalance();
        }finally{
            lock.unlock();
        }
    }
}

class Bank extends Thread{

    public Bank(){

    }

    public void run(){

    }

}

public class ConcurrentDevelopmentAssignment2{

    public static final int numCustomer = 20; //predefined amount of customers
    public static void main(String[] args){

        Random rand = new Random();
        String[] branches = {"Dublin", "Cork", "Galway"};
        Jobs[] c = new Jobs[numCustomer]; 

        for(int i = 0; i < numCustomer; i++){  //random generate customer details
            String id = new String("10" + rand.nextInt(900) + 100);
            String branch = branches[rand.nextInt(3)];
            int balance = rand.nextInt(1000);
            c[i] = new Jobs(id, branch, balance);
        }       
    }

}