package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import db.DBConnection;
import java.awt.*;
import java.sql.*;

// Classe che permette di visualizzare le attrezzature presenti

public class VisualizzaAttrezzatureFrame extends JFrame 
{

    // Tabella per mostrare le attrezzature
    private JTable table;
    private DefaultTableModel model;
    
    public VisualizzaAttrezzatureFrame() throws Exception
    {
        setTitle("Visualizza Attrezzature");
        setSize(900, 500);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pannello principale
        JLabel titleLabel = new JLabel("Elenco Attrezzature", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 118, 210));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        /*
         * Definizione colonne della tabella e modello non modificabile
         * dall'utente.
         */
        String[] columns = {"Codice", "Nome", "Data Acquisto", "Stato", "Da Ordinare"};
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

        // Bottone per aggiornare i dati nella tabella richiamando caricaDati()
        JButton btnAggiorna = new JButton("Aggiorna");
        btnAggiorna.setBackground(new Color(25, 118, 210));
        btnAggiorna.setForeground(Color.WHITE);
        btnAggiorna.addActionListener(e -> {
            try {
                caricaDati();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        // Bottone per chiudere la finestra
        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dispose());
        
        buttonPanel.add(btnAggiorna);
        buttonPanel.add(btnChiudi);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        caricaDati();
    }

    // Metodo che carica i dati nella tabella
    private void caricaDati() throws Exception
    {
        model.setRowCount(0);

        // Query principale per ottenere le attrezzature in ordine del codice identificativo
        String sql = "SELECT CodiceIdentificativo, Nome, DataAcquisto, Stato, DaOrdinare FROM Attrezzatura ORDER BY CodiceIdentificativo";


        // Connessione al databse e caricamento dei dati
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            // Inserimento righe nella tabella
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("CodiceIdentificativo"),
                    rs.getString("Nome"),
                    rs.getDate("DataAcquisto"),
                    rs.getString("Stato"),
                    rs.getBoolean("DaOrdinare") ? "SI" : "NO"
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento: " + ex.getMessage(), 
                "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}