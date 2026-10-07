package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import db.DBConnection;
import java.awt.*;
import java.sql.*;


/*
 * Classe che permette di visualizzare la lista dei prodotti con
 * possibilità di filtrare solo quelli sotto la soglia di scorta.
 */
public class VisualizzaProdottiFrame extends JFrame 
{
    // Tabella per mostrare i prodotti
    private JTable table;
    private DefaultTableModel model;
    
    public VisualizzaProdottiFrame() throws Exception {
        setTitle("Visualizza Prodotti");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Pannello principale
        JLabel titleLabel = new JLabel("Elenco Prodotti Bar/Ristorante", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 118, 210));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        /*
         * Definizione colonne della tabella e modello non modificabile
         * dall'utente.
         */
        String[] columns = {"ID", "Nome", "Categoria", "Quantità", "Soglia Min.", "Scadenza", "Da Ordinare"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Bottone per mostrare solo prodotti sotto scorta
        JButton btnSottoScorta = new JButton("Mostra Sotto Scorta");
        btnSottoScorta.setBackground(new Color(255, 152, 0));
        btnSottoScorta.setForeground(Color.WHITE);
        btnSottoScorta.addActionListener(e -> {
            try {
                mostraSottoScorta();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // Bottone per mostrare tutti i prodotti senza filtro
        JButton btnTutti = new JButton("Mostra Tutti");
        btnTutti.setBackground(new Color(25, 118, 210));
        btnTutti.setForeground(Color.WHITE);
        btnTutti.addActionListener(e -> {
            try {
                caricaDati(false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // Bottone per chiudere la finestra
        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dispose());

        // aggiunta dei pulsanti al pannello
        buttonPanel.add(btnSottoScorta);
        buttonPanel.add(btnTutti);
        buttonPanel.add(btnChiudi);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // caricamento di tutti i prodotti
        caricaDati(false);
    }

    /*
     * Metodo che carica i dati nella tabella.
     * Se sottoScorta=true, carica solo i prodotti DaOrdinare
     */
    private void caricaDati(boolean soloSottoScorta) throws Exception {
        model.setRowCount(0);

        // Query principale per ottenere i prodotti
        String sql = "SELECT ID, Nome, Categoria, Quantità, Soglia_Minima, DataScadenza, DaOrdinare FROM Prodotto";

        /*
         * Query che, se richiesto, filtra solo i prodotti sotto scorta
         * In ogni caso li ordina in ordine alfabetico.
         */
        if (soloSottoScorta) {
            sql += " WHERE DaOrdinare = TRUE";
        }
        sql += " ORDER BY Nome";


        // Connessione al database e caricamento dei dati
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Inserimento righe nella tabella
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ID"),
                    rs.getString("Nome"),
                    rs.getString("Categoria"),
                    rs.getInt("Quantità"),
                    rs.getInt("Soglia_Minima"),
                    rs.getDate("DataScadenza"),
                    rs.getBoolean("DaOrdinare") ? "SI" : "NO"
                };
                model.addRow(row);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento: " + ex.getMessage(), 
                "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo che mostra solo i prodotti sotto scorta, richiamando caricaDati con filtro.
    private void mostraSottoScorta() throws Exception 
    {
        caricaDati(true);
    }
}