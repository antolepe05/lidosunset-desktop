package db;

import java.sql.Connection;
import java.sql.DriverManager;

// Classe per gestire la connessione al database
public class DBConnection
{
    // URL di connessione al database MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/lido_sunset?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    //  USername del database
    private static final String USER = "lido_user";

    // Password del database
    private static final String PASSWORD = "lido123";


    // Viene eseguito UNA SOLA VOLTA quando la classe viene caricata in memoria
    static {
        try {
            // Serve per far comunicare java con MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL non trovato", e);
        }
    }

    // Metodo che restituisce una nuova connessione al database. Ogni chiamata crea una nuova connection
    public static Connection getConnection() throws Exception {

        //DriverManager utilizza l'URL, l'utente e la password per aprire la connessione al database.
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}