package psp04_03;

import java.io.*;
import java.net.*;

class Servidor extends Thread {

    private static final int PUERTO = 1500; //DEFINO EL PUERTO DEL SERVIDOR
    Socket skCliente;
     //ESTABLEZCO EL NOMBRE DE USUARIO Y CONTRASEÑA
    private static final String USER = "javier";
    private static final String PASSWORD = "secreta";

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
            while (true) {
                String usertmp = flujo_entrada.readUTF();
                String passtmp = flujo_entrada.readUTF();
                if (usertmp.toLowerCase().equals(USER.toLowerCase()) && passtmp.equals(PASSWORD)) {
                    System.out.println("Login Correcto");
                    flujo_salida.writeUTF("Login Correcto"); //ENVÍO EL MENSAJE DE CONEXIÓN AL CLIENTE
                    break;
                } else {
                    System.out.println("Login Incorrecto");
                    flujo_salida.writeUTF("Login Incorrecto!!"); //ENVÍO EL MENSAJE DE CONEXIÓN AL CLIENTE
                }
            }
            
            while (skCliente.isConnected()){ //MIENTRAS
                String opcion = flujo_entrada.readUTF();
                switch (opcion) {
                    case "1": {
                        File[] files = new File(".").listFiles();//CAPTURO UN ARRAY CON LOS FICHEROS DE LA CARPETA ACTUAL
                        for (File file : files) { //RECORRO EL ARRAY
                            if (file.isFile()) { //SI NO ES UNA CARPETA
                                flujo_salida.writeUTF(file.getName()); //LEO EL FICHERO LÍNEA POR LÍNEA
                            }
                        }
                        flujo_salida.writeUTF("EOF!"); //ENVÍO ORDEN DE FIN DE LECTURA
                    }
                    break;
                    case "2": {
                        flujo_salida.writeUTF("Introduce el nombre de el archivo: ");
                        File archivo = new File(flujo_entrada.readUTF());
                        if (archivo.exists()) {
                            InputStream ArchivoIn = new FileInputStream(archivo);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(ArchivoIn)); //CREO EL BUFFER DE LECTURA DEL FICHERO
                            String line;
                            while ((line = reader.readLine()) != null) {
                                flujo_salida.writeUTF(line); //LEO EL FICHERO LÍNEA POR LÍNEA
                            }
                            flujo_salida.writeUTF("EOF!"); //ENVÍO ORDEN DE FIN DE LECTURA
                        } else {
                            flujo_salida.writeUTF("El archivo no existe");
                        }
                    }
                    break;
                    case "3": {
                        flujo_salida.close();//CIERRO EL FLUJO DE SALIDA DEL CLIENTE
                        flujo_entrada.close();//CIERRO EL FLUJO DE ENTRADA DEL CLIENTE
                    }
                    break;

                }
            }

            // Se cierra la conexión
            skCliente.close();
            System.out.println("Cliente desconectado");

        } catch (Exception e) {
            System.out.println(e.getMessage()); //NORMALMENTE CAPTURO EL CIERRE DE CONEXIÓN DEL CLIENTE ("socket closed")
        }
    }
}
