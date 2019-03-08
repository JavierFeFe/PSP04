package psp04_01;

import java.io.*;
import java.net.*;
import java.util.Random;

class Servidor extends Thread {
    private static final Random rmd = new Random(); //INICIALIZO RANDOM PARA GENERAR EL NÚMERO ALEATORIO
    private static final int PUERTO = 2000; //DEFINO EL PUERTO DEL SERVIDOR
    Socket skCliente;
    private static int numero = -2;

    public Servidor(Socket sCliente) {
        this.numero = rmd.nextInt(100); //GENERO UN NÚMERO ALEATORIO ENTRE 1 Y 100
        System.out.println("Número secreto: " + numero); //MUESTRO EN LA CONSOLA DEL SERVIDOR EL NÚMERO GENERADO
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
            int numeroIntroducido = -1; //ESTABLEZCO UN VALOR INICIAL PARA EL NÚMERO INTRODUCIDO
            // Creo los flujos de entrada y salida
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            flujo_salida.writeUTF("Conectado"); //ENVÍO EL MENSAJE DE CONEXIÓN AL CLIENTE
            // ATENDER PETICIÓN DEL CLIENTE
            while (numeroIntroducido != numero) { //BUCLE QUE COMPRUEBA EL NÚMERO INTRODUCIDO Y ENVIA MENSAJES DE MAYOR O MENOR
                String entrada = flujo_entrada.readUTF();
                System.out.println("Valor introducido: " + entrada);
                if (entrada.matches("\\d+")) { //COMPRUEBO QUE EL VALOR INTRODUCIDO DESA DE TIPO NUMÉRICO
                       int numeroNuevo = Integer.parseInt(entrada);
                    if (numeroNuevo == numero) {
                        System.out.println("Valor correcto!!");
                        flujo_salida.writeUTF("Correcto");
                        break;
                    } else if (numeroNuevo > numero) {
                        System.out.println("El número es menor");
                        flujo_salida.writeUTF("El número es menor");
                    } else {
                        System.out.println("El número es mayor");
                        flujo_salida.writeUTF("El número es mayor");
                    }
                } else {
                    System.out.println("No es de tipo numérico");
                    flujo_salida.writeUTF("Valor inválido");
                }
            }

            // Se cierra la conexión
            skCliente.close();
            System.out.println("Cliente desconectado");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
