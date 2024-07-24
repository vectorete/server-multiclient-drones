package MAIN;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/** 
* <h1> Clientes drones </h1>
* Esta clase se encarga de crear clientes y hacer que se conecten uno a uno al servidor
* y que se guarden la id. Para luego enviar sus coordenadas y recibir las respuestas
* de que han recibido los mensajes.
* 
* @author   Jesus Goyena Bleda
* @author   Victor Kravchuk Vorekvych
* @author   Victor Crusat Delgado
* @author   Andreu Soler Roig
* 
* @version  1.0 
* @since    2023-10-12
*/
public class Clientes{
    // Declaracion de variables
    private static ArrayList<Cliente> clientes = new ArrayList<>();
    protected final static int SERVER_PORT = 3000;
    private static int nclientes = 10000, v = 100, iter = 4; 
    /** 
    * Este es el metodo principal que crea clientes
    *  
    * @param args sin uso.
    *  
    * @return vacio. 
    */
    public static void main (String[] args) throws IOException
    {   
        // Creamos sockets que seran cada cliente y realizamos la conexion con el servidor
        for(int i = 0; i < nclientes; i++){
            Socket sockliente = new Socket("192.168.1.39", SERVER_PORT);
            //Socket sockliente = new Socket("127.0.0.1", SERVER_PORT);
            sockliente.setSoTimeout(0);
            try {
                DataInputStream in = new DataInputStream(sockliente.getInputStream());
                int id = in.readInt();
                System.out.println("Id recibido: " + id);
                clientes.add(new Cliente(id, sockliente));
            } catch (IOException e) {
                System.err.println("Error en la aceptacion de socket: " + e.getMessage());
            }
        }
        // Creamos los hilos para cada cliente y los iniciamos
        ArrayList<ManejaCliente> mClientes = new ArrayList<>();
        for (Cliente cl : clientes)
            mClientes.add(new ManejaCliente(cl, v - 1, iter));
        
        for(Thread x : mClientes)
            x.start();
        // Esperamos a que termine cada hilo
        for(Thread x : mClientes)
            try {
                x.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
    /** 
    * <h1> Clase ManejaCliente </h1> 
    * Clase de ManejaCliente que gestiona un cliente con la lista de sockets de sus vecinos
    * que sera ejecutado como un thread
    */
    public static class Cliente{
        // Declaracion de variables
        private Socket sockliente;
        private int idCliente, maxx, minx, maxy, miny;
        private String coordenadas;
        /** 
        * Constructor del Cliente
        *  
        * @param id int del id del cliente
        * @param sockliente Objeto SocketCliente con informacion del cliente: socket, id
        */
        public Cliente(int idCliente, Socket sockliente){
            this.idCliente = idCliente;
            this.sockliente = sockliente;
            this.maxx = 1237;
            this.minx = 0;
            this.maxy = 562;
            this.miny = 0;
        }
        /** 
        * Este metodo devuelve el id del cliente
        *
        * @return id del cliente
        */
        public int getId(){
            return idCliente;
        }
        /** 
        * Este metodo devuelve el socket del cliente
        *
        * @return Socket del cliente
        */
        public Socket getSocket(){
            return sockliente;
        }
        /** 
        * Este metodo cierra la conexion con el servidor
        */
        public void cerrarConexion(){
            try {
                this.sockliente.close();
            } catch (IOException ex) {
                Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
        /** 
        * Este metodo devuelve las coordenadas(aleatorias) del 
        *
        * @return String de las coordenadas
        */        
        public String getCoordenadas(){
            int posX = (int) ((Math.random() * (maxx - minx)) + minx);
            int posY = (int) ((Math.random() * (maxy - miny)) + miny);
            this.coordenadas = ("Pos X: " + String.valueOf(posX)+", Pos Y: " + String.valueOf(posY));

            return this.coordenadas;
        }
    }
    /** 
    * <h1> Clase ManejaCliente </h1> 
    * Clase de ManejaCliente que gestiona un cliente que sera ejecutado como un thread
    */
    public static class ManejaCliente extends Thread{
        // Declaracion de variables
        private Cliente cliente;
        private int msjfaltan;
        private int iteraciones;
        /** 
        * Constructor de ManejaCliente
        *  
        * @param cliente Cliente Objeto cliente con informacion de cliente: socket, id
        * @param sockliente Objeto SocketCliente con informacion del cliente: socket, id
        * @param vecinos int entero que indica la cantidad de vecinos en el grupo
        */
        public ManejaCliente(Cliente cliente, int msjfaltan, int iteraciones){
            this.cliente = cliente;
            this.msjfaltan = msjfaltan;
            this.iteraciones = iteraciones;
        }
        // Funcion principal que ejecutara el thread
        @Override
        public void run(){
            try {
                // Variables que vamos a usar
                int mensajesesperando = 0, ackesperando = 0;
                long tini = 0, tfin = 0;
                float ttotal = 0;
                // Bucle donde enviamos coordenadas y esperamos a recibir respuestas en menos de 20 segundos y pasamos de ciclo
                for(int i = 0; i < iteraciones; i++){
                    // Abrimos flujo de entrada y salida
                    DataOutputStream out = new DataOutputStream(cliente.getSocket().getOutputStream());
                    DataInputStream in = new DataInputStream(cliente.getSocket().getInputStream());
                    // Le enviamos las coordenadas al servidor
                    out.writeUTF("[" + cliente.getId() + "]:" + cliente.getCoordenadas() + " Iteracion: " + i);
                    // Cada ciclo esperaremos ack y coordenadas
                    mensajesesperando += msjfaltan;
                    ackesperando += msjfaltan;
                    // Comenzamos a medir el tiempo
                    tini = System.currentTimeMillis();
                    // Esperamos a recibir acks o si pasan mas de 20 segundos salimos
                    while(ackesperando > 0 && ((tfin - tini) < 20000)) {
                        String messg = "";
                        messg = in.readUTF();
                        if(!messg.equals("")){
                            //System.out.println(messg);
                            if (messg.charAt(0) == 'A'){
                                ackesperando--;
                            }
                            else{
                                String stringit = messg.substring(messg.lastIndexOf(':')+2);
                                out.writeUTF("(" + messg.substring(1, messg.indexOf("]")) +")ACK recibido Iteracion: " + stringit);
                                mensajesesperando--;
                            }
                        }
                        tfin = System.currentTimeMillis(); // Tiempo final que medimos para calcular el tiempo que llevamos
                    } 
                    // Guardamos el tiempo del ciclo
                    ttotal += (tfin - tini);
                } 
                // Terminamos los ciclos sin importar el tiempo
                DataInputStream in = new DataInputStream(cliente.getSocket().getInputStream());
                while(ackesperando > 0 || mensajesesperando > 0) {
                    String messg = "";
                    messg = in.readUTF();
                    if(!messg.equals("")){
                        //System.out.println(messg);
                        if (messg.charAt(0) == 'A'){
                            ackesperando--;
                        }
                        else{
                            DataOutputStream out = new DataOutputStream(cliente.getSocket().getOutputStream());
                            String stringit = messg.substring(messg.lastIndexOf(':')+2);
                            out.writeUTF("(" + messg.substring(1, messg.indexOf("]")) +")ACK recibido Iteracion: " + stringit);
                            mensajesesperando--;
                        }
                    }
                } 
                // procedemos a comenzar la desconexion enviando un mensaje de FIN y el tiempo de media de los ciclos
                DataOutputStream out = new DataOutputStream(cliente.getSocket().getOutputStream());
                ttotal /= iteraciones;
                out.writeUTF("FIN");
                out.writeFloat(ttotal);
                String entrada = "";
                do {
                    entrada = in.readUTF();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!entrada.equals("DC"));
                cliente.cerrarConexion();
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}