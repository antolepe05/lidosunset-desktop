package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import db.DBConnection;
import java.awt.*;
import java.sql.*;


/*
 * Classe che permette la visualizzazione, modifica ed eliminazione delle prenotazioni
 * presenti nel database
 */
public class VisualizzaPrenotazioniFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    //Costruttore della finestra di visualizzazione delle prenotazioni

    public VisualizzaPrenotazioniFrame() throws Exception {
        setTitle("Visualizza Prenotazioni");
        setSize(1100, 500);
        setLocationRelativeTo(null);

        //Pannello principale
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo della finestra
        JLabel titleLabel = new JLabel("Elenco Prenotazioni", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 118, 210));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Definizione colonne della tabella
        String[] columns = {"Data", "Fascia Oraria", "Cod. Attrezzatura",
                           "Tariffa (€)", "Stato", "Doc. Cliente"};

        //Modello tabella non modificabile dall'utente
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //Creazionr JTable con impostazioni grafiche
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Pannello filtri e pulsanti
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Sezione per filtrare le prenotazioni
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtra per stato:"));

        //Combo box per filtrare le prenotazioni per stato
        JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Tutte", "attiva", "completata", "annullata"});
        filterPanel.add(cmbFiltro);

        //Pulsante per applicare il filtro
        JButton btnFiltra = new JButton("Applica Filtro");
        btnFiltra.setBackground(new Color(25, 118, 210));
        btnFiltra.setForeground(Color.WHITE);
        btnFiltra.addActionListener(e -> {
            String filtro = (String) cmbFiltro.getSelectedItem();
            try {
                caricaDati(filtro);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        filterPanel.add(btnFiltra);

        bottomPanel.add(filterPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Pulsante modifica prenotazione selezionata
        JButton btnModifica = new JButton("Modifica Selezionata");
        btnModifica.setBackground(new Color(255, 152, 0));
        btnModifica.setForeground(Color.WHITE);
        btnModifica.addActionListener(e -> {
            try {
                modificaPrenotazione();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });


        // Pulsante eliminazione prenotazione selezionata
        JButton btnElimina = new JButton("Elimina Selezionata");
        btnElimina.setBackground(new Color(244, 67, 54));
        btnElimina.setForeground(Color.WHITE);
        btnElimina.addActionListener(e -> {
            try {
                eliminaPrenotazione();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });


        // Pulsante chiusura finestra
        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dispose());

        buttonPanel.add(btnModifica);
        buttonPanel.add(btnElimina);
        buttonPanel.add(btnChiudi);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        caricaDati("Tutte");
    }

    // Caricamento dati tabella
    private void caricaDati(String filtroStato) throws Exception {
        model.setRowCount(0);

        // Query principale
        String sql = "SELECT Data, FasciaOraria, CodiceIdentificativoAttrezzatura, " +
                    "Tariffa, Stato, NumeroDocumentoRiconoscimento FROM Prenotazione";


        /*
        Si applica il filtro dello stato se richiesto, inoltre, si ordina sempre in senso
        decresente e per fascia oraria
        */
        if (!filtroStato.equals("Tutte")) {
            sql += " WHERE Stato = '" + filtroStato + "'";
        }
        sql += " ORDER BY Data DESC, FasciaOraria";


        // Connessione e lettura dati dal database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getDate("Data"),
                    rs.getString("FasciaOraria"),
                    rs.getInt("CodiceIdentificativoAttrezzatura"),
                    String.format("%.2f", rs.getDouble("Tariffa")),
                    rs.getString("Stato"),
                    rs.getString("NumeroDocumentoRiconoscimento")
                };
                model.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento: " + ex.getMessage(),
                "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificaPrenotazione() throws Exception {

        //Controllo selezione riga
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una prenotazione da modificare",
                "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recupero dati della prenotazione selezionata
        Date data = (Date) model.getValueAt(selectedRow, 0);
        String fasciaOraria = (String) model.getValueAt(selectedRow, 1);
        int codiceAttrezzatura = (int) model.getValueAt(selectedRow, 2);
        String tariffaStr = (String) model.getValueAt(selectedRow, 3);
        double tariffa = Double.parseDouble(tariffaStr.replace(",", "."));
        String stato = (String) model.getValueAt(selectedRow, 4);
        String documentoCliente = (String) model.getValueAt(selectedRow, 5);

        // Apertura dialog di modifica
        ModificaPrenotazioneDialog dialog = new ModificaPrenotazioneDialog(
            this, data, fasciaOraria, codiceAttrezzatura, tariffa, stato, documentoCliente
        );
        dialog.setVisible(true);

        // Ricarica dati se la modifica è andata a buon fine
        if (dialog.isModificaEffettuata()) {
            caricaDati("Tutte");
        }
    }

    private void eliminaPrenotazione() throws Exception {

        // Controllo selezione riga
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una prenotazione da eliminare",
                "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recupero dati prenotazione selezionata
        Date data = (Date) model.getValueAt(selectedRow, 0);
        String fasciaOraria = (String) model.getValueAt(selectedRow, 1);
        int codiceAttrezzatura = (int) model.getValueAt(selectedRow, 2);
        String stato = (String) model.getValueAt(selectedRow, 4);

        // Conferma eliminazione
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sei sicuro di voler eliminare la prenotazione:\n" +
            "Data: " + data + "\n" +
            "Fascia Oraria: " + fasciaOraria + "\n" +
            "Attrezzatura: " + codiceAttrezzatura + "\n" +
            "Stato: " + stato + "?",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Query che elimina la prenotazione
            String sqlDelete = "DELETE FROM Prenotazione WHERE Data = ? AND FasciaOraria = ? " +
                              "AND CodiceIdentificativoAttrezzatura = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
                pstmt.setDate(1, data);
                pstmt.setString(2, fasciaOraria);
                pstmt.setInt(3, codiceAttrezzatura);
                pstmt.executeUpdate();
            }

            // Se era l'unica prenotazione attiva per quell'attrezzatura, rimetti disponibile
            String sqlCheckOtherPren = "SELECT COUNT(*) as count FROM Prenotazione " +
                                      "WHERE CodiceIdentificativoAttrezzatura = ? AND Stato = 'attiva'";

            boolean hasActiveBookings = false;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheckOtherPren)) {
                pstmt.setInt(1, codiceAttrezzatura);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    hasActiveBookings = rs.getInt("count") > 0;
                }
            }

            // Se non ci sono altre prenotazioni attive, rimetti l'attrezzatura disponibile
            if (!hasActiveBookings && stato.equals("attiva")) {
                String sqlUpdateAttr = "UPDATE Attrezzatura SET Stato = 'disponibile' " +
                                      "WHERE CodiceIdentificativo = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateAttr)) {
                    pstmt.setInt(1, codiceAttrezzatura);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Prenotazione eliminata con successo!");
            caricaDati("Tutte");

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Errore nell'eliminazione: " + ex.getMessage(),
                "Errore", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}