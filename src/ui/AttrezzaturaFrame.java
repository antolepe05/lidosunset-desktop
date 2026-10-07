package ui;
import javax.swing.*;

import db.DBConnection;

import java.awt.*;
import java.sql.*;

// Classe per l'inserimento di una nuova attrezzatura
public class AttrezzaturaFrame extends JFrame 
{
    // Campi di input per l'inserimento dell'attrezzatura
    private JTextField txtCodice, txtNome;
    private JComboBox<String> cmbTipologia, cmbStato;
    private JTextField txtDataAcquisto;
    
    public AttrezzaturaFrame() {
        setTitle("Aggiungi Attrezzatura");
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Pannello principale
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Codice Identificativo
        panel.add(new JLabel("Codice Identificativo:"));
        txtCodice = new JTextField();
        panel.add(txtCodice);

        // Nome
        panel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        panel.add(txtNome);

        // Tipologia
        panel.add(new JLabel("Tipologia:"));
        cmbTipologia = new JComboBox<>(new String[]{"Canoa", "Pedalò", "Jet-Ski"});
        panel.add(cmbTipologia);

        // Data acquisto
        panel.add(new JLabel("Data Acquisto (YYYY-MM-DD):"));
        txtDataAcquisto = new JTextField();
        panel.add(txtDataAcquisto);

        // Stato dell'attrezzatura
        panel.add(new JLabel("Stato:"));
        cmbStato = new JComboBox<>(new String[]{"disponibile", "prenotata", "in manutenzione"});
        panel.add(cmbStato);

        // Pulsante per salvare l'attrezzatura nel databse
        JButton btnSalva = new JButton("Salva");
        btnSalva.setBackground(new Color(76, 175, 80));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.addActionListener(e -> {
            try {
                salvaAttrezzatura(); // Salvataggio nel database
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // Pulsante che chiude la finestra
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> dispose());
        
        panel.add(btnSalva);
        panel.add(btnAnnulla);
        
        add(panel);
    }

    // Metodo che salva l'attrezzatura nel database
    private void salvaAttrezzatura() throws Exception 
    {
        // Query principale che inserisce l'attrezzatura e DaOrdinare=False di default con il metodo pstmt.set...
        String sql = "INSERT INTO Attrezzatura (CodiceIdentificativo, Nome, DataAcquisto, Stato, DaOrdinare) VALUES (?, ?, ?, ?, FALSE)";

        // Connessione al database
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(txtCodice.getText()));
            pstmt.setString(2, txtNome.getText());
            pstmt.setDate(3, Date.valueOf(txtDataAcquisto.getText()));
            pstmt.setString(4, (String) cmbStato.getSelectedItem());
            
            pstmt.executeUpdate();
            
            // Inserimento nella tabella specifica per tipologia
            String tipologia = (String) cmbTipologia.getSelectedItem();
            String sqlTipo = "";
            if (tipologia.equals("Canoa")) {
                sqlTipo = "INSERT INTO Canoa (CodiceIdentificativoCanoa) VALUES (?)";
            } else if (tipologia.equals("Pedalò")) {
                sqlTipo = "INSERT INTO Pedalò (CodiceIdentificativoPedalò) VALUES (?)";
            } else if (tipologia.equals("Jet-Ski")) {
                sqlTipo = "INSERT INTO JetSki (CodiceIdentificativoJetSki) VALUES (?)";
            }
            if (!sqlTipo.isEmpty()) {
                try (PreparedStatement pstmtTipo = conn.prepareStatement(sqlTipo)) {
                    pstmtTipo.setInt(1, Integer.parseInt(txtCodice.getText()));
                    pstmtTipo.executeUpdate();
                }
            }

            // Messaggio di conferma
            JOptionPane.showMessageDialog(this, "Attrezzatura aggiunta con successo!");

            // Chiude la finestra
            dispose();
            
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}