import javax.swing.*;
import ui.AttrezzaturaFrame;
import ui.PrenotazioneFrame;
import ui.ProdottoFrame;
import ui.VisualizzaAttrezzatureFrame;
import ui.VisualizzaPrenotazioniFrame;
import ui.VisualizzaProdottiFrame;
import java.awt.*;

import javax.swing.*;
import java.awt.*;

/* Classe che rappresenta la finestra pincipale del sistema*/

public class MainFrame extends JFrame {

    public MainFrame() {

        //Impostazione titolo, dimensioni
        setTitle("Lido Sunset - Sistema Gestionale");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color blueAcceso = new Color(25, 118, 210);
        Color white = Color.WHITE;
        Color blueChiaroHover = new Color(100, 149, 237);

        //Pannello contenitore
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(white);

        // Etichetta con il nome dell'azienda centrata
        JLabel headerLabel = new JLabel("LIDO SUNSET S.R.L.S", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(blueAcceso); // Testo blu acceso
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Menu centrale con griglia 3x2 per i pulsanti del menu
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        menuPanel.setBackground(white);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));


        //Pulsanti del menu creati con metodo dedicato
        JButton btnAttrezzature = createMenuButton("Aggiungi Attrezzatura", "➕ 🏄", white, blueAcceso, blueChiaroHover);
        JButton btnVisualizzaAttrezzature = createMenuButton("Visualizza Attrezzature", "📋 🏄", white, blueAcceso, blueChiaroHover);
        JButton btnProdotti = createMenuButton("Aggiungi Prodotto", "➕ 🍹", white, blueAcceso, blueChiaroHover);
        JButton btnVisualizzaProdotti = createMenuButton("Visualizza Prodotti", "📋 🍹", white, blueAcceso, blueChiaroHover);
        JButton btnPrenotazioni = createMenuButton("Aggiungi Prenotazione", "➕ 📅", white, blueAcceso, blueChiaroHover);
        JButton btnVisualizzaPrenotazioni = createMenuButton("Visualizza Prenotazioni", "📋 📅", white, blueAcceso, blueChiaroHover);


        //Apertura delle finestre corrispondenti al click dei pulsanti
        btnAttrezzature.addActionListener(e -> new AttrezzaturaFrame().setVisible(true));

        btnVisualizzaAttrezzature.addActionListener(e -> {
            try {
                new VisualizzaAttrezzatureFrame().setVisible(true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        btnProdotti.addActionListener(e -> new ProdottoFrame().setVisible(true));

        btnVisualizzaProdotti.addActionListener(e -> {
            try {
                new VisualizzaProdottiFrame().setVisible(true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        btnPrenotazioni.addActionListener(e -> new PrenotazioneFrame().setVisible(true));
        btnVisualizzaPrenotazioni.addActionListener(e -> {
            try {
                new VisualizzaPrenotazioniFrame().setVisible(true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        //Inserimento dei pulsanti nel menu centrale
        menuPanel.add(btnAttrezzature);
        menuPanel.add(btnVisualizzaAttrezzature);
        menuPanel.add(btnProdotti);
        menuPanel.add(btnVisualizzaProdotti);
        menuPanel.add(btnPrenotazioni);
        menuPanel.add(btnVisualizzaPrenotazioni);

        //Inserimento menu nel centro del pannello principlae
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        //Aggiunta poannello principale al frame
        add(mainPanel);
    }

    //Metodo che crea pulsanti
    private JButton createMenuButton(String text, String emoji, Color fg, Color bg, Color hoverBg) {
        JButton button = new JButton("<html><center>" + emoji + "<br>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));


        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBg);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    //Metodo principale
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Avvio dell'interfaccia grafica
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}

