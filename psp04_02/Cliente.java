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
