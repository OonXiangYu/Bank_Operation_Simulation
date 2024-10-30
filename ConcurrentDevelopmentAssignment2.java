import java.util.Random;
import java.util.concurrent.locks.*;
import java.util.ArrayList;

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

    public void setJobType(int jobType){
        this.jobType = jobType;
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
        int money = rand.nextInt(1000);

        try{
            c.setBalance(c.getBalance() + money);
            System.out.println("Customer " + c.getID() + " successfully deposited " + money + " by Clerk " + Thread.currentThread().getId());
        }finally{
            lock.unlock();
        }
    }

    public void withdraw(Jobs c){
        lock.lock();
        int money = rand.nextInt(c.getBalance() + 1);

        try{
            c.setBalance(c.getBalance() - money);
            System.out.println("Customer " + c.getID() + " successfully withdrew " + money + " by Clerk " + Thread.currentThread().getId());
        }finally{
            lock.unlock();
        }
    }

    public int display(Jobs c){
        lock.lock();

        try{
            System.out.println("Customer " + c.getID() + " has " + c.getBalance() + " in his/her bank account and displayed by Clerk " + Thread.currentThread().getId());
            return c.getBalance();
        }finally{
            lock.unlock();
        }
    }
}

class Bank extends Thread{

    private Jobs c;
    Random rand = new Random();

    public Bank(Jobs c){
        this.c = c;
    }

    public void run(){
        c.setJobType(rand.nextInt(3) + 1); //random choose one operation
        c.operation(c);
        try{
            Thread.sleep(1000);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }

}

public class ConcurrentDevelopmentAssignment2{

    public static final int numCustomer = 20; //predefined amount of customers
    public static void main(String[] args){

        Random rand = new Random();
        String[] branches = {"Dublin", "Cork", "Galway"};
        Jobs[] c = new Jobs[numCustomer]; 

        for(int i = 0; i < numCustomer; i++){  //random generate customer details
            String id = String.valueOf(10000 + i);
            String branch = branches[rand.nextInt(3)];
            int balance = rand.nextInt(1000);
            c[i] = new Jobs(id, branch, balance);
            //System.out.println(c[i].getID() + c[i].getBranch());
        }
        
        int numThreads = Runtime.getRuntime().availableProcessors();
        Bank[] t = new Bank[numThreads];
        ArrayList<Jobs> customerInBranch = new ArrayList<Jobs>();

        for(int j = 0; j < 3; j++){ //total count you want to simulate
            String currentBranch = branches[rand.nextInt(3)];
            System.out.println("********************");
            System.out.println(currentBranch + " Bank is now open.");
            System.out.println("********************");
            for(int i = 0; i < numCustomer; i++){
                if(c[i].getBranch().equals(currentBranch)){ //this is figure out those customer who in the active branch
                    customerInBranch.add(c[i]);
                    //System.out.println(c[i].getID() + c[i].getBranch());
                }
            }
             
            for(int i = 0; i < numThreads; i++){
                t[i] = new Bank(customerInBranch.get(rand.nextInt(customerInBranch.size()))); //random chose one customer to operate
                t[i].start();         
            }

            try{
                for(int i = 0; i < numThreads; i++){
                    t[i].join();
                }
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            
        }
    }

}