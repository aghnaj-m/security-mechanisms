
class ServerThread extends Thread
{
    public void run()
    {
        // instancier un serveur
        Server s = new Server();
    }
}
class ClientThread1 extends Thread
{
    public void run()
    {
        // instancier un client
        Client c = new Client();
    }
}
class ClientThread2 extends Thread
{
    public void run()
    {
        // instancier un client
    	Client c = new Client();
    }
}




public class Firewall {

    public static void main(String[] args) {
       
        new ServerThread().start();
        new ClientThread1().start();
        new ClientThread2().start();

    }
}
    

