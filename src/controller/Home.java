package controller;

import models.Trabajador;
import views.Menu;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
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
                case 6:
                    System.out.print("ID del trabajador a modificar: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Escriba que campo quiere cambiar [nombre|departamento|salario]");
                    String respuesta = sc.nextLine();
                    System.out.print("Diga su nuevo valor: ");
                    String valor = sc.nextLine();
                    modificarTrabajador(id, respuesta, file, valor);
                    break;
                case 7:
                    borradoFisico(file);
                    break;
                default:
                    System.out.println("¿Un saludo? Pues un saludo.");
            }
        } while (opcion != 0);

    }

    private static void borradoFisico(File file) {
        ArrayList<Trabajador> trabajadores = new ArrayList<>();
        Trabajador trabajador;
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            while (raf.getFilePointer() != raf.length()) {
                int id = raf.readInt();
                if (id == -1) {
                    raf.skipBytes(52);
                } else {
                    trabajador = new Trabajador();
                    escribiendoTrabajadorMientrasBorro(trabajadores, trabajador, raf);

                }
            }
            raf.seek(0);
            leerConBorrado(trabajadores, raf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escribiendoTrabajadorMientrasBorro(ArrayList<Trabajador> trabajadores, Trabajador trabajador, RandomAccessFile raf) throws IOException {
        trabajador.setId(trabajadores.size() + 1);
        byte[] b = new byte[40];
        raf.readFully(b);
        String name = new String(b);
        trabajador.setNombre(name);
        trabajador.setDepartamento(raf.readInt());
        trabajador.setSalario(raf.readDouble());
        trabajadores.add(trabajador);
    }

    private static void leerConBorrado(ArrayList<Trabajador> trabajadores, RandomAccessFile raf) throws IOException {
        StringBuilder sb;
        for (Trabajador trabajadore : trabajadores) {
            raf.writeInt(trabajadore.getId());
            sb = new StringBuilder(trabajadore.getNombre());
            sb.setLength(20);
            raf.writeChars(sb.toString());
            raf.writeInt(trabajadore.getDepartamento());
            raf.writeDouble(trabajadore.getSalario());
        }
        raf.setLength(trabajadores.size() * 56);
    }

    private static void modificarTrabajador(int id, String respuesta, File file, String valor) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if ((id - 1) * 56 > raf.length()) {
                System.out.println("Ese usuario no existe");
            } else {
                if (raf.readInt() == id) {

                    if (respuesta.equalsIgnoreCase("nombre")) {
                        StringBuilder sb = new StringBuilder(valor);
                        sb.setLength(20);
                        raf.writeChars(sb.toString());
                        System.out.println("Valor modificado");
                    }
                    if (respuesta.equalsIgnoreCase("departamento")) {
                        raf.seek(raf.skipBytes(40));
                        raf.writeInt(Integer.parseInt(valor));
                        System.out.println("Valor modificado");
                    }
                    if (respuesta.equalsIgnoreCase("salario")) {
                        raf.seek(raf.skipBytes(44));
                        raf.writeDouble(Double.parseDouble(valor));
                        System.out.println("Valor modificado");
                    }
                } else {
                    raf.skipBytes(52);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void borrarEmpleado(File file) {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID a borrar: ");
        int respuesta = sc.nextInt();
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if (raf.length() > (respuesta - 1) * 56) {
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
                escribe(trabajador, id, raf);
            } else {
                System.out.println(raf.length());
                raf.seek(raf.length() - 56);
                id = raf.readInt();
                raf.seek(raf.length());
                escribe(trabajador, id + 1, raf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escribe(Trabajador trabajador, int id, RandomAccessFile raf) throws IOException {
        raf.writeInt(id);
        StringBuilder buffer = new StringBuilder(trabajador.getNombre());
        buffer.setLength(20);
        raf.writeChars(buffer.toString());
        raf.writeInt(trabajador.getDepartamento());
        raf.writeDouble(trabajador.getSalario());
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
