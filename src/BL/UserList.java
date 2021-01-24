package BL;

import DL.UserDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserList {
    private Map<String,User> users;

    public UserList() {
        //this.users = new HashMap<>();
        this.users = UserDAO.getInstance();
        Localizacao l1 = new Localizacao(1,1);
        Localizacao l2 = new Localizacao(2,2);
        Localizacao l3 = new Localizacao(3,3);

        User ana = new User("Ana","ana",null,new ArrayList<>(),
                false,false);

        User pedro = new User("Pedro","pedro",null,new ArrayList<>(),
                false,false);

        User goncalo = new User("Goncalo","goncalo",null,new ArrayList<>(),
                false,false);

        ana.addLocalizacao(l1);
        pedro.addLocalizacao(l2);
        goncalo.addLocalizacao(l3);

        users.put(ana.getUsername(),ana);
        users.put(pedro.getUsername(),pedro);
        users.put(goncalo.getUsername(), goncalo);

        System.out.println(users.get("Ana"));
        System.out.println(users.get("Pedro"));
        System.out.println(users.get("Goncalo"));
    }

    public void addUser(User u){
        users.put(u.getUsername(),u);
    }

    public void printUsers(){
        for(Map.Entry<String,User> e : this.users.entrySet()){
            /*
            System.out.println("BL.User: " + e.getValue().getUsername() + "\nLoc: " + e.getValue().getLocalizacaoAtual()
                    + "\nAutenticado: " + e.getValue().isLoged() + "\nLocalizações: " + e.getValue().getLocalizacoes());*/
            System.out.println(e);
        }
    }

    public boolean autenticarUser(String user,String pass){
        boolean r = false;
        for(Map.Entry<String,User> e : this.users.entrySet()){
            if(e.getValue().getUsername().equals(user) && e.getValue().getPassword().equals(pass)){
                r = true;
                e.getValue().setLoged(true);
            }
        }
        return r;
    }

    public void alterarLoc(String user,int x,int y){
        for(Map.Entry<String,User> e : this.users.entrySet()){
            if(e.getValue().getUsername().equals(user)){
                Localizacao loc = new Localizacao(x,y);
                e.getValue().addLocalizacao(loc);
            }
        }
    }

    public int numPessoas(int x,int y){
        int count = 0;
        for(Map.Entry<String,User> e : this.users.entrySet()){
            if(e.getValue().getLocalizacaoAtual().getX() == x && e.getValue().getLocalizacaoAtual().getY() == y)
                count ++;
        }
        return count;
    }

    public User getUser(String user){
        return this.users.get(user);
    }

    public Map<String,User> getMap(){
        return this.users;
    }
}
