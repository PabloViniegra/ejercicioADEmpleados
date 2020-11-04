package controller;

import models.Trabajador;
import views.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Home {

    /*
    Codigo
    Nombre (20)
    Departamento
    Salario
    * */
    public static void main(String[] args) throws IOException {
        Menu menu = new Menu();
        Scanner sc = new Scanner(System.in);
        File file = new File("trabajadores.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        int opcion;
        do {
            menu.menu();
            System.out.print("Introduce opcion: ");
            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    insertarTrabajador(file);
                    break;
                case 2:
                    leerTrabajadores(file);
                    break;
                case 3:
                    leerEmpleadoPorId(file);
                    break;
                case 4:
                    borrarEmpleado(file);
                    break;
                default:
                    System.out.println("¿Un saludo? Pues un saludo.");
            }
        } while (opcion != 0);

    }

    private static void borrarEmpleado(File file) {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID a borrar: ");
        int respuesta = sc.nextInt();
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek((respuesta-1) * 56);
            if ( raf.readInt() != -1 && raf.readInt()!=respuesta) {

                raf.writeInt(-1);
            } else {
                System.out.println("Parece que ya ha sido dado de baja ese trabajador");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void leerEmpleadoPorId(File file) {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID: ");
        int id = sc.nextInt();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")){
            raf.seek((id-1)*56);
            System.out.println(id);
            byte[] b = new byte[40];
            raf.readFully(b);
            String name = new String(b);
            System.out.println(name);
            System.out.println(raf.readInt());
            System.out.println(raf.readDouble());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void insertarTrabajador(File file) {
        Trabajador trabajador = new Trabajador();
        Scanner sc = new Scanner(System.in);
        int id = 1;
        System.out.print("Nombre: ");
        trabajador.setNombre(sc.nextLine());
        System.out.print("Departamento: ");
        trabajador.setDepartamento(sc.nextInt());
        System.out.println("Salario: ");
        trabajador.setSalario(sc.nextDouble());
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if (file.length() == 0) {
                raf.seek(0);
                raf.write(id);
                StringBuilder buffer = new StringBuilder(trabajador.getNombre());
                buffer.setLength(20);
                raf.writeChars(buffer.toString());
                raf.write(trabajador.getDepartamento());
                raf.writeDouble(trabajador.getSalario());
            } else {
                System.out.println(raf.length());
                raf.seek(raf.length() - 50);
                id = (int) (raf.length()/56);
                raf.seek(raf.length());
                raf.write(id + 1);
                StringBuilder buffer = new StringBuilder(trabajador.getNombre());
                buffer.setLength(20);
                raf.writeChars(buffer.toString());
                raf.write(trabajador.getDepartamento());
                raf.writeDouble(trabajador.getSalario());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void leerTrabajadores(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(0);
            while (raf.getFilePointer() != raf.length()) {
                System.out.println(raf.readInt());
                byte[] b = new byte[40];
                raf.readFully(b);
                String name = new String(b);
                System.out.println(name);
                System.out.println(raf.readInt());
                System.out.println(raf.readDouble());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
