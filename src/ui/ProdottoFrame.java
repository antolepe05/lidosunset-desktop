package ui;
import javax.swing.*;

import db.DBConnection;

import java.awt.*;
import java.sql.*;

// Classe per l'inserimento di un prodotto nel database
public class ProdottoFrame extends JFrame 
{
    // Campi di input testuali per i dati del prodotto
    private JTextField txtId, txtNome, txtDescrizione, txtQuantita, txtSoglia, txtDataScadenza;

    // ComboBox per selezionare la categoria del prodotto
    private JComboBox<String> cmbCategoria;
    
    public ProdottoFrame() {
        setTitle("Aggiungi Prodotto");
        setSize(500, 450);
        setLocationRelativeTo(null);

        //Pannello principale
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campo ID prodotto
        panel.add(new JLabel("ID Prodotto:"));
        txtId = new JTextField();
        panel.add(txtId);

        // Campo nome prodotto
        panel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        panel.add(txtNome);

        // Selezione categoria tramite menu a tendina
        panel.add(new JLabel("Categoria:"));
        cmbCategoria = new JComboBox<>(new String[]{
            "frutta e verdura", "carne", "pesce", "pasta", 
            "alcolici e non alcolici", "snack"
        });
        panel.add(cmbCategoria);

        // Campo descrizione prodotto
        panel.add(new JLabel("Descrizione:"));
        txtDescrizione = new JTextField();
        panel.add(txtDescrizione);

        // Campo quantità disponibile
        panel.add(new JLabel("Quantità:"));
        txtQuantita = new JTextField();
        panel.add(txtQuantita);

        // Campo soglia minima
        panel.add(new JLabel("Soglia Minima:"));
        txtSoglia = new JTextField();
        panel.add(txtSoglia);

        // Campo data di scadenza
        panel.add(new JLabel("Data Scadenza (YYYY-MM-DD):"));
        txtDataScadenza = new JTextField();
        panel.add(txtDataScadenza);

        // Pulsante di salvataggio del prodotto
        JButton btnSalva = new JButton("Salva");
        btnSalva.setBackground(new Color(76, 175, 80));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.addActionListener(e -> {
            try {
                // chiamata al metodo di inserimento nel database
                salvaProdotto();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // Pulsante di annullamento (chiude la finestra)
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> dispose());
        
        panel.add(btnSalva);
        panel.add(btnAnnulla);
        
        add(panel);
    }

    // Metodo che salva i prodotti nel database
    private void salvaProdotto() throws Exception 
    {

        // Query che inserisce un prodotto nella tabella usando successivamente pstmt.setInt/String
        String sql = "INSERT INTO Prodotto (ID, Nome, Categoria, Descrizione, Quantità, Soglia_Minima, DataScadenza, DaOrdinare) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        // Connessione al database
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int quantita = Integer.parseInt(txtQuantita.getText());
            int soglia = Integer.parseInt(txtSoglia.getText());
            boolean daOrdinare = quantita < soglia;
            
            pstmt.setInt(1, Integer.parseInt(txtId.getText()));
            pstmt.setString(2, txtNome.getText());
            pstmt.setString(3, (String) cmbCategoria.getSelectedItem());
            pstmt.setString(4, txtDescrizione.getText());
            pstmt.setInt(5, quantita);
            pstmt.setInt(6, soglia);
            pstmt.setDate(7, Date.valueOf(txtDataScadenza.getText()));
            pstmt.setBoolean(8, daOrdinare);

            // Esecuzione dell'inserimento
            pstmt.executeUpdate();

            //Messaggio di conferma
            JOptionPane.showMessageDialog(this, "Prodotto aggiunto con successo!");

            // Chiusura della finestra dopo il salvataggio
            dispose();
            
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}