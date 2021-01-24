package DL;

import User.Localizacao;
import User.User;

import java.util.*;
import java.sql.*;

public class UserDAO implements Map<String, User>
{
    private static UserDAO singleton = null;

    public static UserDAO getInstance() {
        if (UserDAO.singleton == null) {
            UserDAO.singleton = new UserDAO();
        }
        return UserDAO.singleton;
    }
    /*
    CREATE TABLE IF NOT EXISTS `mydb`.`Localizacoes` (
  `idLocalizacoes` VARCHAR(45) NOT NULL,
  `User_username` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`User_username`),
  INDEX `fk_Localizacoes_User_idx` (`User_username` ASC) VISIBLE,
  CONSTRAINT `fk_Localizacoes_User`
    FOREIGN KEY (`User_username`)
    REFERENCES `mydb`.`Users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
     */
    private UserDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Users (\n" +
                    "  `username` VARCHAR(45) NOT NULL,\n" +
                    "  `password` VARCHAR(45) NULL,\n" +
                    "  `Localizacao` VARCHAR(45) NULL,\n" +
                    "  `loged` Boolean NOT NULL,\n" +
                    "  `infetado` Boolean NOT NULL,\n" +
                    "  `LocDest` VARCHAR(45) NULL,\n" +
                    "  `tipo` INT NOT NULL,\n" +
                    "  PRIMARY KEY (`username`))";
            String locSql = "CREATE TABLE IF NOT EXISTS Localizacoes (\n" +
                    "  `idLocalizacoes` VARCHAR(45) NOT NULL,\n" +
                    "  `User_username` VARCHAR(45) NOT NULL PRIMARY KEY,\n" +
                    "FOREIGN KEY (User_username) REFERENCES Users(username))";
            stm.executeUpdate(sql);
            stm.executeUpdate(locSql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.
                getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Users")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return this.size()==0;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.
                getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.
                     executeQuery("SELECT username FROM Users WHERE username='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        User uti = (User) value;
        return this.containsKey(uti.getUsername());
    }


    @Override
    public User get(Object key) {
        User uti = null;
        List<Localizacao> locList = new ArrayList<>();
        try (Connection conn = DriverManager.
                getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Users WHERE username='"+key+"'")) {
            if (rs.next()) {  // A chave existe na tabela
                String[] coords = rs.getString("Localizacao").split(",");
                uti = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        new Localizacao(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));

                uti.setInfetado(rs.getBoolean("infetado"));
                uti.setLoged(rs.getBoolean("loged"));

                ResultSet locs = stm.executeQuery("SELECT * FROM Localizacoes WHERE User_username='"+ key +"'");
                while (locs.next())
                {
                    String[] coordenadas = locs.getString("idLocalizacoes").split(",");
                    locList.add(new Localizacao(Integer.parseInt(coordenadas[0]),Integer.parseInt(coordenadas[0])));
                }
                uti.setLocalizacoes(locList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return uti;
    }

    @Override
    public User put(String key, User value) {
        try (Connection conn = DriverManager.
                getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String dest = null;
            if(value.getLocalizacaoDest() != null)
                dest = value.getLocalizacaoDest().toStringtoDB();
            stm.executeUpdate(
                    "INSERT INTO Users VALUES ('" +
                            value.getUsername() + "', '" +
                            value.getPassword() + "', '" +
                            value.getLocalizacaoAtual().toStringtoDB() + "', '" +
                            (value.isLoged() ? 1 : 0) + "', '" +
                            (value.isInfetado() ? 1 : 0) + "', '" +
                            dest + "', '" +
                            "0" + "') " +
                            "ON DUPLICATE KEY UPDATE " +
                            "username=VALUES(username)," +
                            "password=VALUES(password)," +
                            "Localizacao=VALUES(Localizacao)," +
                            "loged=VALUES(loged)," +
                            "infetado = VALUES(infetado)," +
                            "LocDest = VALUES(LocDest)," +
                            "tipo = VALUES(tipo)");
            for(Localizacao l : value.getLocalizacoes())
            {
                stm.executeUpdate("INSERT INTO Localizacoes VALUES ('" +
                        l.getX() + "," + l.getY() + "' , '" + key + "')"+
                        "ON DUPLICATE KEY UPDATE " +
                        "idLocalizacoes=VALUES(idLocalizacoes)," +
                        "User_username=VALUES(User_username)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return value;
    }

    @Override
    public User remove(Object key) {
        User t = this.get(key);
        try (Connection conn = DriverManager.
                getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Users WHERE username='"+key+"'");
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends User> m) {
        for(User a : m.values()) {
            this.put(a.getUsername(), a);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.
                getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Users");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        throw new NullPointerException("Not implemented!");
    }

    @Override
    public Collection<User> values() {
        Collection<User> col = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT username FROM Users")) {
            while (rs.next()) {
                col.add(this.get(rs.getString("User")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return col;
    }

    @Override
    public Set<Entry<String, User>> entrySet() {
        throw new NullPointerException("Not implemented!");
    }
}
