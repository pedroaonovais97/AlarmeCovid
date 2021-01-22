import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    final static int WORKERS_PER_CONNECTION = 10;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        UserList users = new UserList();
        List<Localizacao> buffer = new ArrayList<>();

        while (true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            Runnable worker = () -> {
                try (c) {
                    for (; ;) {
                        TaggedConnection.DataFrame frame = c.receiveUser();
                        int tag = frame.tag;
                        switch (frame.tag) {
                            case 0:
                                Localizacao loc = new Localizacao(frame.x, frame.y);
                                User u = new User(frame.user, frame.pass,loc);
                                users.addUser(u);
                                System.out.println("Users: ");
                                users.printUsers();
                                System.out.println();
                                break;
                            case 1:
                                if(users.autenticarUser(frame.user, frame.pass) && !users.getUser(frame.user).isInfetado())
                                    c.sendUser(1,"","",0,0,true,false,0,0);
                                else
                                    c.sendUser(1,"","",0,0,false,false,0,0);
                                break;
                            case 2:
                                System.out.println(frame.user + " vai para : " + "(" + frame.x + "," + frame.y + ")");
                                int xAtual = users.getUser(frame.user).getLocalizacaoAtual().getX();
                                int yAtual = users.getUser(frame.user).getLocalizacaoAtual().getY();
                                users.alterarLoc(frame.user, frame.x, frame.y);
                                List<Localizacao> aRemover = new ArrayList<>();
                                for(Localizacao l : buffer) {
                                    System.out.println(l);
                                    if (users.numPessoas(l.getX(), l.getY()) == 0) {
                                        for(Map.Entry<String,User> uz :  users.getMap().entrySet()) {
                                            System.out.println(uz);
                                            if(!uz.getValue().getUsername().equals(frame.user) &&
                                                    uz.getValue().getLocalizacaoDest().getX() == xAtual &&
                                                    uz.getValue().getLocalizacaoDest().getY() == yAtual){
                                                uz.getValue().setLocalizacaoDest(null);
                                                aRemover.add(new Localizacao(xAtual,yAtual));
                                            }
                                        }
                                    }
                                }
                                buffer.removeAll(aRemover);
                                System.out.println("Users: ");
                                users.printUsers();
                                for(Localizacao l : buffer)
                                    System.out.println(l);
                                System.out.println("------------------------\n");
                                break;
                            case 3:
                                int y = users.numPessoas(frame.x, frame.y);
                                c.sendUser(3,"","",y,0,false,false,0,0);
                                break;
                            case 4:
                                loc = new Localizacao(frame.x, frame.y);
                                if(users.numPessoas(frame.x, frame.y) == 0)
                                    c.sendUser(4,"","",0,0,true,false,0,0);
                                else {
                                    buffer.add(loc);
                                    users.getUser(frame.user).setLocalizacaoDest(new Localizacao(frame.x, frame.y));
                                    while(users.getUser(frame.user).getLocalizacaoDest() != null){

                                    }
                                    c.sendUser(4,"","",0,0,true,false,0,0);
                                    users.getUser(frame.user).setLocalizacaoDest(null);
                                }
                                break;
                            case 5:
                                users.getUser(frame.user).setInfetado(true);
                                users.printUsers();
                                break;
                        }
                    }
                } catch (Exception ignored) {
                }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(worker).start();
        }
    }
}