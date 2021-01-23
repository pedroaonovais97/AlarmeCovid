import java.io.IOException;
import java.net.Socket;

public class Handler implements Runnable {
    private TaggedConnection m;
    String user;
    String pass;
    int x;
    int y;
    boolean loged;
    boolean inf;
    int x1;
    int y1;
    private int tag;
    private Socket s;

    public Handler(int tag, Socket s, String user,String pass,int x,int y,boolean loged,boolean inf,int x1,int y1)
            throws IOException {
        m = new TaggedConnection(s);
        this.user = user;
        this.pass = pass;
        this.x = x;
        this.y = y;
        this.loged = loged;
        this.inf = inf;
        this.x1 = x1;
        this.y1 = y1;
        this.tag = tag;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            m.sendUser(tag,user,pass,x,y,loged,inf,x1,y1);

            new Thread(() -> {
                try {
                    for (; ;) {
                        TaggedConnection.DataFrame data = m.receiveUser();
                        if(data.loged)
                            System.out.println("(" + x + "," + y + ")" + " Disponível!");
                    }
                } catch (Exception ignored) {
                }
            }).start();

        } catch (IOException ignored) {
        }
    }

}