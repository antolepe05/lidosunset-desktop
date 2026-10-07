package ui;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import java.util.Date;


// Classe per l'inserimento di una nuova prenotazione
public class PrenotazioneFrame extends JFrame
{
    // Campi di input per la prenotazione
    private JDateChooser dateChooser;
    private JTextField txtTariffa;
    private JTextField txtCodiceAttrezzatura, txtDocumentoCliente;
    private JComboBox<String> cmbStato;
    private JComboBox<String> comboFascia;

    public PrenotazioneFrame()
    {
        setTitle("Aggiungi Prenotazione");
        setSize(500, 500);
        setLocationRelativeTo(null);

        // Pannello principale
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Data con calendario
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Data:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setMinSelectableDate(new Date()); //Impedisce date pasate
        dateChooser.setPreferredSize(new Dimension(200, 25));
        panel.add(dateChooser, gbc);

        // Fascia oraria combo box (10:00-10:30, 10:30-11:00, etc.)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Fascia Oraria:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        comboFascia = new JComboBox<>();
        generaFasceOrarie(); // Popola la combo con intervalli orari
        comboFascia.setPreferredSize(new Dimension(200, 25));
        panel.add(comboFascia, gbc);

        // Codice Attrezzatura
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Codice Attrezzatura:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtCodiceAttrezzatura = new JTextField(15);
        panel.add(txtCodiceAttrezzatura, gbc);

        // Tariffa
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tariffa (€):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtTariffa = new JTextField(15);
        panel.add(txtTariffa, gbc);

        // Stato prenotazione
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Stato:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cmbStato = new JComboBox<>(new String[]{"attiva", "completata", "annullata"});
        cmbStato.setPreferredSize(new Dimension(200, 25));
        panel.add(cmbStato, gbc);

        // Documento Cliente
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Documento Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtDocumentoCliente = new JTextField(15);
        panel.add(txtDocumentoCliente, gbc);

        // Pulsanti
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));


        // Pulsante salva
        JButton btnSalva = new JButton("Salva");
        btnSalva.setBackground(new Color(76, 175, 80));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.setPreferredSize(new Dimension(100, 30));
        btnSalva.addActionListener(e -> {
            try {
                salvaPrenotazione();  // Salvataggio nel database
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // Pulsante annulla
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.setPreferredSize(new Dimension(100, 30));
        btnAnnulla.addActionListener(e -> dispose());

        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);
        panel.add(buttonPanel, gbc);

        add(panel);
    }


    // Genera fasce orarie dalle 10:00 alle 18:00 ogni 30 minuti
    private void generaFasceOrarie() {
        for (int ora = 10; ora < 18; ora++) {
            comboFascia.addItem(String.format("%02d:00-%02d:30", ora, ora));

            if (ora < 17) {
                comboFascia.addItem(String.format("%02d:30-%02d:00", ora, ora + 1));
            } else {
                comboFascia.addItem("17:30-18:00");
            }
        }
    }

    // Metodo che valida i dati nel database e salva la prenotazione nel database
    private void salvaPrenotazione() throws Exception {
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una data",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtCodiceAttrezzatura.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci il codice attrezzatura",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtTariffa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci la tariffa",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtDocumentoCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci il documento del cliente",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;

        // Connessione al database
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int codiceAttrezzatura = Integer.parseInt(txtCodiceAttrezzatura.getText().trim());
            java.sql.Date sqlDate = new java.sql.Date(dateChooser.getDate().getTime());
            String fasciaOraria = (String) comboFascia.getSelectedItem();

            // Query che verifica che l'attrezzatura esista
            String sqlCheckAttr = "SELECT Stato, DaOrdinare FROM Attrezzatura WHERE CodiceIdentificativo = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckAttr)) {
                pstmtCheck.setInt(1, codiceAttrezzatura);
                ResultSet rs = pstmtCheck.executeQuery();

                if (!rs.next()) {
                    throw new SQLException("Attrezzatura non trovata");
                }

                boolean daOrdinare = rs.getBoolean("DaOrdinare");
                if (daOrdinare) {
                    throw new SQLException("Impossibile prenotare: attrezzatura da ordinare");
                }
            }

            // Query che verifica se esiste una prenotazione per quella attrezzatura, nella stessa data e fascia oraria
            String sqlCheckPren = "SELECT COUNT(*) as count FROM Prenotazione " +
                    "WHERE CodiceIdentificativoAttrezzatura = ? " +
                    "AND Data = ? AND FasciaOraria = ? AND Stato != 'annullata'";

            try (PreparedStatement pstmtCheckPren = conn.prepareStatement(sqlCheckPren)) {
                pstmtCheckPren.setInt(1, codiceAttrezzatura);
                pstmtCheckPren.setDate(2, sqlDate);
                pstmtCheckPren.setString(3, fasciaOraria);

                ResultSet rs = pstmtCheckPren.executeQuery();
                if (rs.next() && rs.getInt("count") > 0) {
                    throw new SQLException("Attrezzatura già prenotata per questa data e fascia oraria");
                }
            }

            // Query che inserisce la prenotazione usando pstmtInsert.set...
            String sqlInsert = "INSERT INTO Prenotazione (Data, FasciaOraria, CodiceIdentificativoAttrezzatura, " +
                    "Tariffa, Stato, NumeroDocumentoRiconoscimento) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert))
            {
                pstmtInsert.setDate(1, sqlDate);
                pstmtInsert.setString(2, fasciaOraria);
                pstmtInsert.setInt(3, codiceAttrezzatura);
                pstmtInsert.setDouble(4, Double.parseDouble(txtTariffa.getText().trim().replace(",", ".")));
                pstmtInsert.setString(5, (String) cmbStato.getSelectedItem());
                pstmtInsert.setString(6, txtDocumentoCliente.getText().trim());

                pstmtInsert.executeUpdate();
            }

            // 4. Aggiorna lo stato dell'attrezzatura a 'prenotata'
            String statoPrenotazione = (String) cmbStato.getSelectedItem();
            if (statoPrenotazione.equals("attiva")) {
                String sqlUpdateAttr = "UPDATE Attrezzatura SET Stato = 'prenotata' WHERE CodiceIdentificativo = ?";
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateAttr)) {
                    pstmtUpdate.setInt(1, codiceAttrezzatura);
                    pstmtUpdate.executeUpdate();
                }
            }

            // Conferma dell'inserimento
            conn.commit();
            JOptionPane.showMessageDialog(this, "Prenotazione aggiunta con successo!");
            dispose();

        } catch (SQLException | NumberFormatException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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