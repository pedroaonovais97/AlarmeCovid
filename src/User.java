import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private Localizacao localizacaoAtual;
    private List<Localizacao> localizacoes;
    private boolean loged;
    private boolean infetado;

    public User(String username, String password, Localizacao loc){
        this.username = username;
        this.password = password;
        this.localizacaoAtual = loc;
        this.localizacoes = new ArrayList<>();
        localizacoes.add(loc);
        this.loged = true;
        this.infetado = false;
    }

    public User(String username, String password, Localizacao localizacaoAtual, List<Localizacao> localizacoes,
                boolean loged,boolean infetado) {
        this.username = username;
        this.password = password;
        this.localizacaoAtual = localizacaoAtual;
        this.localizacoes = localizacoes;
        this.loged = loged;
        this.infetado = infetado;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Localizacao getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public void setLocalizacaoAtual(Localizacao localizacaoAtual) {
        this.localizacaoAtual = localizacaoAtual;
    }

    public List<Localizacao> getLocalizacoes() {
        return localizacoes;
    }

    public void setLocalizacoes(List<Localizacao> localizacoes) {
        this.localizacoes = localizacoes;
    }

    public void addLocalizacao(Localizacao loc){
        this.localizacoes.add(loc);
        this.localizacaoAtual = loc;
    }

    public boolean isLoged() {
        return loged;
    }

    public void setLoged(boolean loged) {
        this.loged = loged;
    }

    public boolean isInfetado() {
        return infetado;
    }

    public void setInfetado(boolean infetado) {
        this.infetado = infetado;
    }
}
