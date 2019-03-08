package psp04_02;

import java.io.*;
import java.net.*;

class Servidor extends Thread {

    private static final int PUERTO = 1500; //DEFINO EL PUERTO DEL SERVIDOR
    Socket skCliente;


    public Servidor(Socket sCliente) {
        skCliente = sCliente;
    }

    public static void main(String[] arg) {
        try {
            
            // Inicio el servidor en el puerto
            ServerSocket skServidor = new ServerSocket(PUERTO);
            System.out.println("Escucho el puerto " + PUERTO);

            while (true) { //se queda a la espera de la entrada de nuevos clientes
                // Se conecta un cliente
                Socket skCliente = skServidor.accept();
                System.out.println("Cliente conectado");
                // Atiendo al cliente mediante un thread
                new Servidor(skCliente).start(); //crea un hilo para este cliente
            }
        } catch (Exception e) {;
        }
    }

    public void run() { //ESTABLEZCO EL HILO PARA EL CLIENTE CONECTADO
        try {
            // Creo los flujos de entrada y salida
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            flujo_salida.writeUTF("Conectado"); //ENVÍO EL MENSAJE DE CONEXIÓN AL CLIENTE
            File archivo = new File(flujo_entrada.readUTF());
            if (archivo.exists()){
                InputStream ArchivoIn = new FileInputStream(archivo);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ArchivoIn)); //CREO EL BUFFER DE LECTURA DEL FICHERO
                        String line;
                        while ((line = reader.readLine()) != null) {
                            flujo_salida.writeUTF(line); //LEO EL FICHERO LÍNEA POR LÍNEA
                        }
            }else{
                flujo_salida.writeUTF("El archivo no existe");
            }

            // Se cierra la conexión
            skCliente.close();
            System.out.println("Cliente desconectado");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
