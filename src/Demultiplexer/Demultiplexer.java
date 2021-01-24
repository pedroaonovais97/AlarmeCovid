package Demultiplexer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable{

    private final TaggedConnection conn;
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, Entry> buf = new HashMap<>();
    private IOException exception = null;

    private class Entry{
        int waiters = 0;
        final Condition cond = lock.newCondition();
        final ArrayDeque<TaggedConnection.DataFrame> queue = new ArrayDeque<>();
    }

    private Entry get(int tag){
        Entry e = buf.get(tag);
        if(e == null){
            e = new Entry();
            buf.put(tag, e);
        }
        return e;
    }

    public Demultiplexer(TaggedConnection conn) {
        this.conn = conn;
    }

    public void start(){
        new Thread(() -> {
            try {
                for(;;){
                    TaggedConnection.DataFrame frame = conn.receiveUser();
                    lock.lock();
                    try{
                        //recebe uma Entry, coloca no mapa e dá signal para avisar que algo ocorreu
                        Entry e = get(frame.tag);
                        e.queue.add(frame);
                        e.cond.signal();
                    }finally {
                        lock.unlock();
                    }
                }
            } catch (IOException e) {
                lock.lock();
                try {
                    exception = e;
                    buf.forEach((k,v) -> v.cond.signalAll());
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }
    public void sendUser(int tag,String user,String pass,int x,int y,boolean loged,boolean inf,int x1,int y1) throws IOException{
        conn.sendUser(tag, user, pass, x, y, loged, inf, x1, y1);
    }

    public TaggedConnection.DataFrame receiveUser(int tag) throws IOException, InterruptedException{
        lock.lock();
        try{
            Entry e = get(tag);
            e.waiters++;
            for(;;) {
                if(!e.queue.isEmpty()){
                    TaggedConnection.DataFrame res= e.queue.poll();
                    //System.out.println(res.tag);
                    e.waiters--;
                    if(e.queue.isEmpty() && e.waiters == 0)
                        buf.remove(tag);
                    return res;
                }
                if(exception != null)
                    throw exception;
                e.cond.await();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
