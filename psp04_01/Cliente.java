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
