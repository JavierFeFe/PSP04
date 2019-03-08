# PSP04
Tarea para PSP04.

La tarea de la unidad esta dividida en 3 actividades.

* Actividad 4.1. Modifica el ejercicio 1 de la unidad 3 para el servidor permita trabajar de forma concurrente con varios clientes.  
```Java
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
```
*Clase Servidor*
```Java
package psp04_01;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Cliente {
    private final String URL = "127.0.0.1"; //DEFINO LA URL DEL SERVIDOR
    private final int PUERTO = 2000; //DEFINO EL PUERTO DEL SERVIDOR
    public static void main(String[] arg) {
        Cliente cliente = new Cliente();
    }
    public Cliente() {
        try (Socket sCliente = new Socket(URL, PUERTO)) {
            //CREO LOS SOCKETS DE ENTRADA Y SALIDA IGUAL QUE EN EL SERVIDOR
            InputStream in = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(in);
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);
            String entrada = flujo_entrada.readUTF();
            if (entrada.equals("Conectado")){
                System.out.println(entrada);
                while (!entrada.equals("Correcto")) { //BUCLE QUE SE REPETIRÁ MIENTRAS EL SERVIDOR NO DEVUELVA EL VALOR "Correcto"
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Introduce un número: ");
                    String texto = sc.nextLine();
                    flujo_salida.writeUTF(texto);
                    entrada = flujo_entrada.readUTF();
                    System.out.println(entrada);
                }
            }
            sCliente.close(); //CIERRO EL SOCKET
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```
*Clase cliente (Igual que la del ejercio 3.1)
* Actividad 4.2. Modifica el ejercicio 2 de la unidad 3 para el servidor permita trabajar de forma concurrente con varios clientes.
```Java
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
```
*Clase Servidor*
```Java
package psp04_02;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class Cliente {
    private final String URL = "127.0.0.1"; //DEFINO LA URL DEL SERVIDOR
    private final int PUERTO = 1500; //DEFINO EL PUERTO DEL SERVIDOR
    public static void main(String[] arg) {
        Cliente cliente = new Cliente();
    }
    public Cliente() {
        try (Socket sCliente = new Socket(URL, PUERTO)) {
            //CREO LOS SOCKETS DE ENTRADA Y SALIDA IGUAL QUE EN EL SERVIDOR
            InputStream in = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(in);
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);
            String entrada = flujo_entrada.readUTF();
            if (entrada.equals("Conectado")){
                Scanner sc = new Scanner(System.in);
                System.out.print("Introduce el nombre de un fichero: ");
                String texto = sc.nextLine();
                flujo_salida.writeUTF(texto);
                while (true){
                    try{
                        System.out.println(flujo_entrada.readUTF());
                    }catch (EOFException e) {
                        break;
                    }
                }
            }
            sCliente.close(); //CIERRO EL SOCKET
        }  catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
```
*Clase Cliente (Igual que la del ejercio 3.2)*
* Actividad 4.3. A partir del ejercicio anterior crea un servidor que una vez iniciada sesión a través de un nombre de usuario y contraseña específico (por ejemplo javier / secreta) el sistema permita Ver el contenido del directorio actual, mostrar el contenido de un determinado archivo y salir.
```Java

```
```Java

```
Para realizar el ejercicio primero debes crear un diagrama de estados que muestre el funcionamiento del servidor.
