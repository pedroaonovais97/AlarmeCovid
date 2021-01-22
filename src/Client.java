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

    public static boolean menuClient(String username,TaggedConnection c) throws IOException {
        Scanner sc = new Scanner(System.in);
        displayClient();
        int i = sc.nextInt();
        int x,y;
        TaggedConnection.DataFrame f;
        boolean sair = false;

        while(i != 0 && !sair){
            sc.nextLine();
            switch (i) {
                case 1 -> {
                    System.out.println("Coordenada X:");
                    x = sc.nextInt();
                    System.out.println("Coordenada Y:");
                    y = sc.nextInt();
                    c.sendUser(2, username, "", x, y, true, false, 0, 0);
                }
                case 2 -> {
                    System.out.println("Introduza a Localização a Procurar:");
                    System.out.println("Coordenada X:");
                    x = sc.nextInt();
                    System.out.println("Coordenada Y:");
                    y = sc.nextInt();
                    c.sendUser(3, username, "", x, y, true, false, 0, 0);
                    f = c.receiveUser();
                    assert f.tag == 3;
                    System.out.println("Nº de Pessoas: " + f.x);
                }
                case 3 -> {
                    System.out.println("Introduza a Localização a Procurar:");
                    System.out.println("Coordenada X:");
                    x = sc.nextInt();
                    System.out.println("Coordenada Y:");
                    y = sc.nextInt();
                    c.sendUser(4, username, "", x, y, false, false, x, y);
                    System.out.println("Waiting...");
                    f = c.receiveUser();
                    assert f.tag == 4;
                    if (f.loged)
                        System.out.println("(" + x + "," + y + ")" + " Disponível!");
                }
                case 4 -> {
                    c.sendUser(5, username, "", 0, 0, false, true, 0, 0);
                    sair = true;
                }
                case 5 -> sair = true;
                default -> System.out.println("Opção Inválida!");
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
        TaggedConnection c = new TaggedConnection(s);
        Scanner sc = new Scanner(System.in);
        displayMenu();
        int i = sc.nextInt();
        String username;
        String password;
        boolean sair = false;

        while (i != 0 && !sair) {
            sc.nextLine();
            switch (i) {
                case 1 -> {
                    System.out.println("Username:");
                    username = sc.nextLine();
                    System.out.println("Password");
                    password = sc.nextLine();
                    System.out.println("Coordenada X:");
                    int x = sc.nextInt();
                    System.out.println("Coordenada Y:");
                    int y = sc.nextInt();
                    c.sendUser(0, username, password, x, y, true, false, 0, 0);
                }
                case 2 -> {
                    System.out.println("Username:");
                    username = sc.nextLine();
                    System.out.println("Password:");
                    password = sc.nextLine();
                    c.sendUser(1, username, password, 0, 0, false, false, 0, 0);
                    TaggedConnection.DataFrame f = c.receiveUser();
                    assert f.tag == 1;
                    if (f.loged) {
                        System.out.println("Autenticado!");
                        sair = menuClient(username, c);
                    } else
                        System.out.println("Não Autenticado!");
                }
                default -> System.out.println("Opção Inválida!");
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
