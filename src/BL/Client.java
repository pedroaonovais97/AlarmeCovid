package BL;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void displayMenu(){
        System.out.println("########################################");
        System.out.println("#.............Alarme Covid.............#");
        System.out.println("#......................................#");
        System.out.println("#.............1 - Registar.............#");
        System.out.println("#.............2 - Autenticar...........#");
        System.out.println("#.............0 - Sair.................#");
        System.out.println("#......................................#");
        System.out.println("########################################");
    }

    public static void displayClient(){
        System.out.println("########################################");
        System.out.println("#.............Alarme Covid.............#");
        System.out.println("#......................................#");
        System.out.println("#.............1 - Localizar............#");
        System.out.println("#.............2 - Nº Pessoas em (X,Y)..#");
        System.out.println("#.............3 - Ir Para (X,Y)........#");
        System.out.println("#.............4 - Declarar Infeção.....#");
        System.out.println("#.............5 - Sair.................#");
        System.out.println("#......................................#");
        System.out.println("########################################");
    }

    public static boolean menuClient(String username,Demultiplexer c) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        displayClient();
        int i = sc.nextInt();
        int x,y;
        TaggedConnection.DataFrame f;
        boolean sair = false;
        Thread[] threads = new Thread[10];

        while(i != 0 && !sair){
            sc.nextLine();
            switch (i) {
                case 1:
                    System.out.println("Coordenada X:");
                    x = sc.nextInt();
                    System.out.println("Coordenada Y:");
                    y = sc.nextInt();
                    c.sendUser(2, username, "", x, y, true, false, 0, 0);
                    break;
                case 2:
                    try {
                        System.out.println("Introduza a Localização a Procurar:");
                        System.out.println("Coordenada X:");
                        x = sc.nextInt();
                        System.out.println("Coordenada Y:");
                        y = sc.nextInt();
                        c.sendUser(3, username, "", x, y, true, false, 0, 0);
                        f = c.receiveUser(3);
                        System.out.println("Nº de Pessoas: " + f.x);
                    }catch (Exception ignored) {}
                    break;
                case 3:
                        System.out.println("Introduza a Localização a Procurar:");
                        System.out.println("Coordenada X:");
                        final int x4;
                        final int y4;
                        x4 = sc.nextInt();
                        System.out.println("Coordenada Y:");
                        y4 = sc.nextInt();
                        /*
                        threads[0] = new Thread(() -> {
                            try {
                                c.sendUser(4, username, "", x4, y4, false, false, x4, y4);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });*/
                        c.sendUser(4, username, "", x4, y4, false, false, x4, y4);
                        System.out.println("Pedido Registado!");
                        new Thread(() -> {
                            try {
                                TaggedConnection.DataFrame as = c.receiveUser(4);
                                if (as.loged)
                                    System.out.println("(" + as.x + "," + as.y + ")" + " Disponível!");

                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        break;                
                case 4:
                    c.sendUser(5, username, "", 0, 0, false, true, 0, 0);
                    sair = true;
                break;
                case 5: sair = true; break;
                default: 
                    System.out.println("Opção Inválida!");
                    break;
            }
            if(!sair) {
                displayClient();
                i = sc.nextInt();
            }
        }
        return sair;
    }

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        Demultiplexer c = new Demultiplexer(new TaggedConnection(s));
        Scanner sc = new Scanner(System.in);
        displayMenu();
        int i = sc.nextInt();
        String username;
        String password;
        boolean sair = false;
        c.start();

        while (i != 0 && !sair) {
            sc.nextLine();
            switch (i) {
                case 1:
                    System.out.println("Username:");
                    username = sc.nextLine();
                    System.out.println("Password");
                    password = sc.nextLine();
                    System.out.println("Coordenada X:");
                    int x = sc.nextInt();
                    System.out.println("Coordenada Y:");
                    int y = sc.nextInt();
                    c.sendUser(0, username, password, x, y, true, false, 0, 0);
                break;
                case 2:
                    System.out.println("Username:");
                    username = sc.nextLine();
                    System.out.println("Password:");
                    password = sc.nextLine();
                    c.sendUser(1, username, password, 0, 0, false, false, 0, 0);
                    System.out.println("Enviou");
                    TaggedConnection.DataFrame f = c.receiveUser(1);
                    System.out.println("ok");
                    if (f.loged) {
                        System.out.println("Autenticado!");
                        sair = menuClient(username, c);
                    } else
                        System.out.println("Não Autenticado!");
                break;
                default:
                    System.out.println("Opção Inválida!");
                    break;
            }
            if(!sair) {
                displayMenu();
                i = sc.nextInt();
            }
        }
        c.close();
        System.out.println("Fechou");
    }
}
