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
