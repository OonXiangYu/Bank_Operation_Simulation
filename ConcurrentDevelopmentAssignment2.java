import java.util.Random;

class Customer{

    private String id;
    private String branch;
    private double balance;

    public Customer(){

    }

    public Customer(String id, String branch, double balance){
        this.id = id;
        this.branch = branch;
        this.balance = balance;
    }

    public String getID(){
        return id;
    }

    public String getBranch(){
        return branch;
    }

    public double getBalance(){
        return balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

}

public class ConcurrentDevelopmentAssignment2{
    public static void main(String[] args){

        Random rand = new Random();
        String[] branches = {"Dublin", "Cork", "Galway"};
        Customer[] c = new Customer[20]; //predefined we have 20 customer

        for(int i = 0; i < 20; i++){  //random generate customer details
            String id = new String("10" + rand.nextInt(900) + 100);
            String branch = branches[rand.nextInt(3)];
            double balance = Math.round(rand.nextDouble() * 1000);
            c[i] = new Customer(id, branch, balance);
        }       
    }

}