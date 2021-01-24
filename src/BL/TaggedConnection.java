package BL;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable{

    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private ReentrantLock wlock = new ReentrantLock();
    private ReentrantLock rlock = new ReentrantLock();

    public static class DataFrame{
        public final int tag;
        public String user;
        public String pass;
        public int x;
        public int y;
        public boolean loged;
        public boolean infetado;
        public int x1;
        public int y1;

        public DataFrame(int tag,String user,String pass,int x,int y,boolean loged,boolean infetado,int x1,int y1){
            this.tag = tag;
            this.user = user;
            this.pass = pass;
            this.x = x;
            this.y = y;
            this.loged = loged;
            this.infetado = infetado;
            this.x1 = x1;
            this.y1 = y1;
        }
    }

    public TaggedConnection(Socket socket) throws IOException{
        this.s = socket;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void sendUser(int tag,String user,String pass,int x,int y,boolean loged,boolean inf,int x1,int y1)throws IOException{
        try{
            wlock.lock();
            out.writeInt(tag);
            out.writeUTF(user);
            out.writeUTF(pass);
            out.writeInt(x);
            out.writeInt(y);
            out.writeBoolean(loged);
            out.writeBoolean(inf);
            out.writeInt(x1);
            out.writeInt(y1);
            out.flush();
        }finally{
            wlock.unlock();
        }
    }

    public DataFrame receiveUser() throws IOException{
        DataFrame df;
        int tag;
        String user;
        String pass;
        int x;
        int y;
        boolean log;
        boolean inf;
        int x1;
        int y1;

        try{
            rlock.lock();
            System.out.println("oi");
            tag = in.readInt();
            System.out.println("io");
            user = in.readUTF();
            pass = in.readUTF();
            x = in.readInt();
            y = in.readInt();
            log = in.readBoolean();
            inf = in.readBoolean();
            x1 = in.readInt();
            y1 = in.readInt();

            df = new DataFrame(tag,user,pass,x,y,log,inf,x1,y1);
            System.out.println(tag + " " + user + " " + pass + " " + log);

        }finally{
            rlock.lock();
        }
        return df;
    }

    @Override
    public void close() throws Exception{
        this.s.close();
    }
}

