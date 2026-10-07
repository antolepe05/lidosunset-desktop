package ui;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.sql.*;
import db.DBConnection;


// Classe che permette di modificare una prenotazione esistente. Permette di modificare data, fascia oraria, tariffa e stato
public class ModificaPrenotazioneDialog extends JDialog
{
    // Campi di input per la modifica della prenotazione
    private JDateChooser dateChooser;
    private JComboBox<String> cmbFasciaOraria;
    private JTextField txtTariffa;
    private JComboBox<String> cmbStato;
    private boolean modificaEffettuata = false;

    // Campi originali della prenotazione
    private Date dataOriginale;
    private String fasciaOrariaOriginale;
    private int codiceAttrezzaturaOriginale;
    private String statoOriginale;
    private String documentoCliente;


    // Riceve i dati della prenotazione da modificare
    public ModificaPrenotazioneDialog(JFrame parent, Date data, String fasciaOraria,
                                      int codiceAttrezzatura, double tariffa,
                                      String stato, String documentoCliente) {
        super(parent, "Modifica Prenotazione", true);

        this.dataOriginale = data;
        this.fasciaOrariaOriginale = fasciaOraria;
        this.codiceAttrezzaturaOriginale = codiceAttrezzatura;
        this.statoOriginale = stato;
        this.documentoCliente = documentoCliente;

        setSize(500, 450);
        setLocationRelativeTo(parent);

        // Pannello principale
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel titleLabel = new JLabel("Modifica Prenotazione", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 118, 210));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Data con calendario
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Data:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new java.util.Date(data.getTime())); // Data originale
        dateChooser.setMinSelectableDate(new java.util.Date()); // Impossibile selezionare date precedenti
        dateChooser.setPreferredSize(new Dimension(200, 25));
        formPanel.add(dateChooser, gbc);

        // Fascia Oraria con combo box
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Fascia Oraria:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cmbFasciaOraria = new JComboBox<>();
        generaFasceOrarie();
        cmbFasciaOraria.setSelectedItem(fasciaOraria);
        cmbFasciaOraria.setPreferredSize(new Dimension(200, 25));
        formPanel.add(cmbFasciaOraria, gbc);

        // Codice Attrezzatura (non modificabile)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Codice Attrezzatura:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblCodice = new JLabel(String.valueOf(codiceAttrezzatura));
        lblCodice.setForeground(Color.GRAY);
        formPanel.add(lblCodice, gbc);

        // Documento Cliente (non modificabile)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Documento Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblDoc = new JLabel(documentoCliente);
        lblDoc.setForeground(Color.GRAY);
        formPanel.add(lblDoc, gbc);

        // Tariffa
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Tariffa (€):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtTariffa = new JTextField(String.format("%.2f", tariffa), 15);
        formPanel.add(txtTariffa, gbc);

        // Stato
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Stato:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cmbStato = new JComboBox<>(new String[]{"attiva", "completata", "annullata"});
        cmbStato.setSelectedItem(stato);
        cmbStato.setPreferredSize(new Dimension(200, 25));
        formPanel.add(cmbStato, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Pulsante per salvare la prenotazione nel database
        JButton btnSalva = new JButton("Salva Modifiche");
        btnSalva.setBackground(new Color(76, 175, 80));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.addActionListener(e -> {
            try {
                salvaModifiche(); // Salvataggio nel database
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });


        // Pulsante per chiude la finestra
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> dispose());

        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Metodo che genera le fasce orarie dalle 10:00 alle 18:00 ogni 30 minuti

    private void generaFasceOrarie() {
        for (int ora = 10; ora < 18; ora++) {
            cmbFasciaOraria.addItem(String.format("%02d:00-%02d:30", ora, ora));

            if (ora < 17) {
                cmbFasciaOraria.addItem(String.format("%02d:30-%02d:00", ora, ora + 1));
            } else {
                cmbFasciaOraria.addItem("17:30-18:00");
            }
        }
    }

    /*
     * Metodo che salva le modifiche. Se cambiano data o fascia oraria -> delete + insert;
     * Altrimenti -> semplice update.
     */
    private void salvaModifiche() throws Exception {
        // Validazione campi
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una data",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtTariffa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci la tariffa",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;

        // Connessione al database
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Date nuovaData = new Date(dateChooser.getDate().getTime());
            String nuovaFasciaOraria = (String) cmbFasciaOraria.getSelectedItem();
            double nuovaTariffa = Double.parseDouble(txtTariffa.getText().trim().replace(",", "."));
            String nuovoStato = (String) cmbStato.getSelectedItem();

            // Verifica se data o fascia oraria sono cambiate
            boolean dataOFasciaCambiata = !nuovaData.equals(dataOriginale) ||
                    !nuovaFasciaOraria.equals(fasciaOrariaOriginale);

            // Query che verifica se c'è un'altra prenotazione per quella stessa attrezzatura nella stessa data e fascia oraria.
            if (dataOFasciaCambiata) {
                String sqlCheck = "SELECT COUNT(*) as count FROM Prenotazione " +
                        "WHERE CodiceIdentificativoAttrezzatura = ? " +
                        "AND Data = ? AND FasciaOraria = ? AND Stato != 'annullata'";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
                    pstmt.setInt(1, codiceAttrezzaturaOriginale);
                    pstmt.setDate(2, nuovaData);
                    pstmt.setString(3, nuovaFasciaOraria);

                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && rs.getInt("count") > 0) {
                        throw new SQLException("Esiste già una prenotazione per questa attrezzatura " +
                                "nella data e fascia oraria selezionate");
                    }
                }

                // Query che elimina la vecchia prenotazione
                String sqlDelete = "DELETE FROM Prenotazione WHERE Data = ? AND FasciaOraria = ? " +
                        "AND CodiceIdentificativoAttrezzatura = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
                    pstmt.setDate(1, dataOriginale);
                    pstmt.setString(2, fasciaOrariaOriginale);
                    pstmt.setInt(3, codiceAttrezzaturaOriginale);
                    pstmt.executeUpdate();
                }

                // Query che inserisce la prenotazione con le modifiche
                String sqlInsert = "INSERT INTO Prenotazione " +
                        "(Data, FasciaOraria, CodiceIdentificativoAttrezzatura, Tariffa, Stato, NumeroDocumentoRiconoscimento) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setDate(1, nuovaData);
                    pstmt.setString(2, nuovaFasciaOraria);
                    pstmt.setInt(3, codiceAttrezzaturaOriginale);
                    pstmt.setDouble(4, nuovaTariffa);
                    pstmt.setString(5, nuovoStato);
                    pstmt.setString(6, documentoCliente);
                    pstmt.executeUpdate();
                }
            } else {
                // Query che viene eseguita se non è stata modificata ne data ne fascia oraria
                String sqlUpdate = "UPDATE Prenotazione SET Tariffa = ?, Stato = ? " +
                        "WHERE Data = ? AND FasciaOraria = ? AND CodiceIdentificativoAttrezzatura = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                    pstmt.setDouble(1, nuovaTariffa);
                    pstmt.setString(2, nuovoStato);
                    pstmt.setDate(3, dataOriginale);
                    pstmt.setString(4, fasciaOrariaOriginale);
                    pstmt.setInt(5, codiceAttrezzaturaOriginale);
                    pstmt.executeUpdate();
                }
            }

            // Aggiornamento stato attrezzatura
            if (statoOriginale.equals("attiva") && !nuovoStato.equals("attiva")) {

                // Query che conta quante prenotazioni esistono per la stessa attrezzatura
                String sqlCheck = "SELECT COUNT(*) as count FROM Prenotazione " +
                        "WHERE CodiceIdentificativoAttrezzatura = ? AND Stato = 'attiva'";

                boolean hasOtherActive = false;
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
                    pstmt.setInt(1, codiceAttrezzaturaOriginale);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        hasOtherActive = rs.getInt("count") > 0;
                    }
                }

                if (!hasOtherActive) {

                    // Query che aggiorna lo stato a disponibile se non esistono prenotazioni attive
                    String sqlUpdateAttr = "UPDATE Attrezzatura SET Stato = 'disponibile' " +
                            "WHERE CodiceIdentificativo = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateAttr)) {
                        pstmt.setInt(1, codiceAttrezzaturaOriginale);
                        pstmt.executeUpdate();
                    }
                }
            } else if (!statoOriginale.equals("attiva") && nuovoStato.equals("attiva")) {
                // Query che aggiorna lo stato a prenotata se il nuovo stato è attivo
                String sqlUpdateAttr = "UPDATE Attrezzatura SET Stato = 'prenotata' " +
                        "WHERE CodiceIdentificativo = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateAttr)) {
                    pstmt.setInt(1, codiceAttrezzaturaOriginale);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            modificaEffettuata = true;
            // Messaggio di conferma
            JOptionPane.showMessageDialog(this, "Prenotazione modificata con successo!");
            // Chiusura finestra
            dispose();

        } catch (SQLException | NumberFormatException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(),
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

    // Ritorna true se la modifica è andata a buon fine
    public boolean isModificaEffettuata() {
        return modificaEffettuata;
    }
}