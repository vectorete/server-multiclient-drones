package MAIN;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
* <h1> Servidor drones </h1>
* Esta clase se encarga de gestionar las conexiones de los clientes
* que son drones los cuales le asignaremos un grupo y una id, que 
* si mandan su posicion la reenviaremos a los drones de su mismo grupo
* para que tengan en constancia la posicion de sus vecinos
* 
* @author   Jesus Goyena Bleda
* @author   Victor Kravchuk Vorekvych
* @author   Victor Crusat Delgado
* @author   Andreu Soler Roig
* 
* @version  1.0 
* @since    2023-10-12
*/
public class Servidor {
    // Declaracion de variables
    private static int SERVER_PORT = 3000;
    private static int V, N, S, grupo, clientesact, G;
    private static ArrayList<ArrayList<SocketCliente>> grupos = new ArrayList<>();
    private static ServerSocket svsocket;
    /** 
    * Este es el metodo principal que gestiona el servidor
    *  
    * @param args sin uso.
    *  
    * @return vacio. 
    */
    public static void main(String[] args) throws IOException {
        Scanner entrada = new Scanner(System.in);
        // Pedimos num. drones por grupo, clientes e iteraciones
        do{
            System.out.print("Introduce el numero de clientes (N): ");
            N = entrada.nextInt();
            System.out.print("Introduce los miembros en cada grupo (V): ");
            V = entrada.nextInt();
            System.out.print("Introduce el numero de iteraciones (S): ");
            S = entrada.nextInt();
            if (N % V != 0)
                System.out.println("V no es divisor de N, introduce los datos de nuevo.");
            else if (N <= 0 || S <= 0 || V <= 0)
                System.out.println("Valores negativos no aceptados, introduce los datos de nuevo.");
        } while (N % V != 0 || N <= 0 || S <= 0 || V <= 0);
        entrada.close();

        // Intentamos iniciar el servidor
        System.out.print("Inicializando servidor... ");
        try{
            InetAddress privateIP = InetAddress.getByName("192.168.1.39");
            //InetAddress privateIP = InetAddress.getByName("127.0.0.1");
            svsocket = new ServerSocket(SERVER_PORT, 0, privateIP);
            svsocket.setSoTimeout(0);
            System.out.println("\t[OK]");
        } catch (IOException e){
            System.err.println("Error en la inicializacion del servidor: " + e.getMessage());
            return;
        }

        // Calculamos numero de grupos que necesitamos
        G = N / V;
        for (int i = 0; i < G; i++){
            grupos.add(new ArrayList<>());
        }
        // Contador de cuantos clientes tenemos, que actuara como id
        clientesact = 0;
        // Establecemos con clientes hasta aceptar los clientes que indicamos
        while(clientesact < N){
            try {
                Socket socket = svsocket.accept();
                socket.setSoTimeout(0);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeInt(clientesact); //mandar ID
                System.out.println("Envio id: " + clientesact);
                grupo = clientesact / V;
                SocketCliente sockliente = new SocketCliente(clientesact, socket);
                grupos.get(grupo).add(sockliente);
                clientesact++;
            } catch (IOException e){
                System.err.println("Error en la aceptacion del cliente: " + e.getMessage());
            }
        }
        ArrayList<Thread> manejadores = new ArrayList<>();
        // Asignamos un hilo distinto a cada cliente
        for (ArrayList<SocketCliente> g : grupos) {
            for (SocketCliente s : g){
                ManejaCliente manejahilo = new ManejaCliente(g, s, V);
                manejadores.add(manejahilo);
            }
        }
        // Comenzamos la simulacion iniciando cada hilo para cada cliente
        System.out.println("Empieza la simulacion.");
        for(Thread t : manejadores){
            t.start();
        }
        // Extraemos el tiempo de cada hilo
        double t_media = 0;
        for(Thread t : manejadores){
            try {
                t.join();
                if (t instanceof ManejaCliente) {
                    t_media += ((ManejaCliente) t).getTiempo();
                }
            } catch (InterruptedException e) {
                System.err.println("Error de espera del hilo: " + e.getMessage());
            }
        }
        // Calculamos la media entre todos los clientes
        t_media /= N;
        if (t_media > 20000){
            t_media = 20000.0;
        }
        System.out.println("Tiempo de respuesta: " + t_media);
        System.out.println("Cerrando server...");
        svsocket.close();
    }
    /** 
    * <h1> Clase ManejaCliente </h1> 
    * Clase de ManejaCliente que gestiona un cliente con la lista de sockets de sus vecinos
    * que sera ejecutado como un thread
    */
    public static class ManejaCliente extends Thread {
        // Declaracion de variables
        private ArrayList<SocketCliente> socketsCliente;
        private SocketCliente sockcliente;
        private int vecinos;
        private float tiempo;
        /** 
        * Constructor de ManejaCliente
        *  
        * @param socketsCliente ArrayList del objeto SocketCliente con todos los sockets de los vecinos
        * @param sockliente Objeto SocketCliente con informacion del cliente: socket, id
        * @param vecinos int entero que indica la cantidad de vecinos en el grupo
        */
        public ManejaCliente(ArrayList<SocketCliente> socketsCliente, SocketCliente sockcliente, int vecinos){
            this.sockcliente = sockcliente;
            this.socketsCliente = socketsCliente;
            this.tiempo = 0;
            this.vecinos = vecinos;
        }
        /** 
        * Este metodo devuelve el tiempo de media del cliente
        *  
        * @return tiempo de media del cliente 
        */
        public double getTiempo(){
            return this.tiempo;
        }
        // Funcion principal que ejecutara el thread
        @Override
        public void run(){
            // Booleano que sera true cuando cliente envie FIN
            boolean finaliza = false;
            try {
                // Abrimos un flujo de entrada para revisar si hay mensajes
                DataInputStream in = new DataInputStream(sockcliente.getSocketCliente().getInputStream());
                // Bucle sobre el que se iterara hasta que cliente envie fin
                while(!finaliza){
                    // Leemos mensaje
                    String entrada = in.readUTF();
                    // Si mensaje es fin de cliente(FIN), saldremos del bucle ya que no esperamos nada mas
                    if (entrada.equals("FIN")) {
                        this.tiempo = in.readFloat();
                        //System.out.println("Recibido final de cliente [" + sockcliente.getIdCliente() + "]");
                        sockcliente.setInactivo();
                        finaliza = true;
                        DataOutputStream out =  new DataOutputStream(sockcliente.getSocketCliente().getOutputStream());
                        out.writeUTF("DC"); // Escribimos DC para que haya una desconexion acordada
                    } // Si mensaje no vacio
                    else if (!entrada.equals("")) {
                        // Si es un mensaje de coordenadas --> [id] coordenadas
                        if (entrada.charAt(0) == '['){
                            // Recorremos desde id+1 hasta el ultimo vecino, y desde 0 hasta id - 1, para que no se atasquen tanto en el synchronized
                            for (int i = ((sockcliente.idCliente % vecinos) + 1); i < socketsCliente.size(); i++){
                                if (socketsCliente.get(i).isActivo()){
                                    try {
                                        DataOutputStream vecinout = new DataOutputStream(socketsCliente.get(i).getSocketCliente().getOutputStream());
                                        synchronized (vecinout){
                                            vecinout.writeUTF(entrada);
                                        }
                                        //System.out.println(entrada);
                                    } catch (IOException e){
                                        System.err.println("Error en el envio a un vecino de coordenadas: " + e.getMessage());
                                    }
                                }
                            }
                            for (int i = 0; i < (sockcliente.idCliente % vecinos); i++){
                                if (socketsCliente.get(i).isActivo()){
                                    try {
                                        DataOutputStream vecinout = new DataOutputStream(socketsCliente.get(i).getSocketCliente().getOutputStream());
                                        synchronized (vecinout){
                                            vecinout.writeUTF(entrada);
                                        }
                                        //System.out.println(entrada);
                                    } catch (IOException e){
                                        System.err.println("Error en el envio a un vecino de coordenadas: " + e.getMessage());
                                    }
                                }
                            }
                        } // Si el mensaje es una respuesta --> (ID) ACK
                        else if (entrada.charAt(0) == '('){
                            // Obtenemos el rango en el que esta contenido el id
                            int indaux = entrada.indexOf(')');
                            int indice = Integer.parseInt(entrada.substring(1, indaux)) % vecinos;
                            try {
                                // Accedemos al indice mediante id % vecinos ya que esta ordenado por orden de lista
                                SocketCliente socketdestino = socketsCliente.get(indice);
                                DataOutputStream vecout = new DataOutputStream(socketdestino.getSocketCliente().getOutputStream());
                                synchronized(vecout){
                                    vecout.writeUTF(entrada.substring(indaux+1));
                                }
                                //System.out.println(entrada.substring(indaux+1));
                            } catch (IOException e) {
                                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Error sending ACK response", e);
                            }                            
                        }
                    }
                }
            } catch (IOException e){
                System.err.println("Error abriendo el flujo de entrada: " + e.getMessage());
            } 
        }
    }
    //Clase para tener almacenados datos de los clientes incluidos sus sockets
    public static class SocketCliente {
        // Declaracion de variables
        private Socket sockliente;
        private int idCliente;
        private boolean activo;
        
        /** 
        * Constructor de SocketCliente
        *  
        * @param idCliente int que indica el id de cliente
        * @param sockliente Objeto SocketCliente con informacion del cliente: socket, id
        *
        */
        public SocketCliente(int idCliente, Socket sockliente){
            this.sockliente = sockliente;
            this.idCliente = idCliente;
            this.activo = true;
        }

        /** 
        * Este metodo devuelve el socket del cliente
        *
        * @return Socket del cliente
        */
        public Socket getSocketCliente(){
            return sockliente;
        }

        /** 
        * Este metodo devuelve el id del cliente
        *
        * @return id del cliente
        */
        public int getIdCliente(){
            return idCliente;
        }

        /** 
        * Este metodo hace al socket inactivo como que termino ya
        */
        public void setInactivo(){
            this.activo = false;
        }

        /** 
        * Este metodo devuelve si esta activo el cliente
        *
        * @return si el cliente esta activo
        */
        public boolean isActivo(){
            return this.activo;
        }

        /** 
        * Este metodo cierra la conexion con el cliente
        */
        public void close(){
            try {
                DataOutputStream out = new DataOutputStream(sockliente.getOutputStream());
                out.writeUTF("FIN");
            } catch (IOException e){
                System.err.println("Error en el cierre: " + e.getMessage());
            }
            try {
                this.sockliente.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
    }
}