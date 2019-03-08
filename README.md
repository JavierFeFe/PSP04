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
*Clase cliente (Igual que la del ejercio 3.1)*
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
```
*Clase Servidor*
```Java
package psp04_03;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class Cliente {

    private final String URL = "127.0.0.1"; //DEFINO LA URL DEL SERVIDOR
    private final int PUERTO = 1500; //DEFINO EL PUERTO DEL SERVIDOR
    private final String MENU = "\nIntroduzca una opcion:\n" //MENÚ CON LAS POSIBLES OPCIONES
            + "1 - Ver el contenido del directorio actual.\n"
            + "2 - Mostrar el contenido de un determinado archivo.\n"
            + "3 - Salir.\n"
            + "Opción:-> ";

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
            if (entrada.equals("Conectado")) {
                Scanner sc = new Scanner(System.in);
                String texto = "";
                String msgServer = "";
                do { //FUERZO LA INTRODUCCIÓN DE USUARIO Y CONTRASEÑA CORRECTO
                    System.out.print("Usuario: ");
                    texto = sc.nextLine();
                    flujo_salida.writeUTF(texto);
                    System.out.print("Contraseña: ");
                    texto = sc.nextLine();
                    flujo_salida.writeUTF(texto);
                    msgServer = flujo_entrada.readUTF();
                    System.out.println(msgServer);
                } while (!msgServer.equals("Login Correcto"));
                while (true) { //BUCLE Q SE REPITE MIENTRAS LA CONEXIÓN ESTÉ ACTIVA
                    System.out.print(MENU); //MUESTRO EL MENÚ
                    texto = sc.nextLine(); //ESPERO ENTRADA DE TEXTO
                    System.out.println("");
                    flujo_salida.writeUTF(texto); //ENVIO OPCIÓN AL SERVIDOR
                    String opcionMenu = "";
                    try {opcionMenu = flujo_entrada.readUTF();}catch (EOFException e) { //ESPERO RESPUESTA DEL SERVIDOR
                            break;
                        }
                    switch (opcionMenu) { //INTERPRETO LAS OPCIONES EN FUNCIÓN DEL MENSAJE DEL SERVIDOR
                        case "Introduce el nombre de el archivo: ": {
                            System.out.print(opcionMenu);
                            texto = sc.nextLine();
                            flujo_salida.writeUTF(texto);
                        }
                        break;
                    }
                    String salida ="";
                    while (!salida.equals("EOF!")) { //MIENTRAS NO RECIBE EL TEXTO EOF! SEGUIRÁ LEYENDO EL ARCHIVO
                        try {
                            System.out.println(salida);
                            salida = flujo_entrada.readUTF();
                        } catch (EOFException e) {
                            break;
                        }
                    }
                }

            }
            sCliente.close(); //CIERRO EL SOCKET
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
```
*Clase Cliente*  
![image](https://user-images.githubusercontent.com/44543081/54058993-cf2bea00-41f7-11e9-9ed0-c1342133438e.png)  
![image](https://user-images.githubusercontent.com/44543081/54059017-e539aa80-41f7-11e9-8ea5-3e6ca760defc.png)  
Para realizar el ejercicio primero debes crear un diagrama de estados que muestre el funcionamiento del servidor.
