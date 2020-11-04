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
                case 5:
                    borrarEmpleadoSecuencial(file);
                    break;
                default:
                    System.out.println("¿Un saludo? Pues un saludo.");
            }
        } while (opcion != 0);

    }

    private static void borrarEmpleadoSecuencial(File file) {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID a borrar: ");
        int respuesta = sc.nextInt();
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            int id;
            while (raf.getFilePointer() != raf.length()) {
                id = raf.readInt();
                if (id == respuesta) {
                    raf.seek(raf.getFilePointer() - 4);
                    raf.writeInt(-1);
                } else {
                    raf.skipBytes(52);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void borrarEmpleado(File file) {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID a borrar: ");
        int respuesta = sc.nextInt();
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if (raf.length() > (respuesta-1) * 56) {
                raf.seek((respuesta - 1) * 56);

                if (raf.readInt() != -1) {
                    raf.seek(raf.getFilePointer() - 4);
                    raf.writeInt(-1);
                } else {
                    System.out.println("Parece que ya ha sido dado de baja ese trabajador");

                }
            } else {
                System.out.println("Ese ID no existe");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void leerEmpleadoPorId(File file) {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID: ");
        int id = sc.nextInt();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek((id - 1) * 56);
            if (raf.readInt() == -1) {
                System.out.println("El usuario ha sido borrado");
            } else {

                System.out.println(id);
                byte[] b = new byte[40];
                raf.readFully(b);
                String name = new String(b);
                System.out.println(name);
                System.out.println(raf.readInt());
                System.out.println(raf.readDouble());
            }
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
        sc.nextLine();
        System.out.println("Salario: ");
        trabajador.setSalario(sc.nextDouble());
        sc.nextLine();
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if (raf.length() == 0) {
                raf.writeInt(id);
                StringBuilder buffer = new StringBuilder(trabajador.getNombre());
                buffer.setLength(20);
                raf.writeChars(buffer.toString());
                raf.writeInt(trabajador.getDepartamento());
                raf.writeDouble(trabajador.getSalario());
            } else {
                System.out.println(raf.length());
                raf.seek(raf.length() - 56);
                id = raf.readInt();
                raf.seek(raf.length());
                raf.writeInt(id + 1);
                StringBuilder buffer = new StringBuilder(trabajador.getNombre());
                buffer.setLength(20);
                raf.writeChars(buffer.toString());
                raf.writeInt(trabajador.getDepartamento());
                raf.writeDouble(trabajador.getSalario());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void leerTrabajadores(File file) {
        int id, departamento;
        double salario;
        String nombre;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            while (raf.getFilePointer() != raf.length()) {
                id = raf.readInt();
                if (id == -1) {
                    System.out.println("Este usuario ha sido dado de baja");
                    raf.skipBytes(52);
                } else {
                    byte[] b = new byte[40];
                    raf.readFully(b);
                    nombre = new String(b);
                    departamento = raf.readInt();
                    salario = raf.readDouble();
                    System.out.println(id + "\t" + nombre + "\t" + departamento + "\t" + salario);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
