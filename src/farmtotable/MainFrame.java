package farmtotable;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Farm-to-Table System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Header
        JLabel header = new JLabel("Farm-to-Table Distribution System", JLabel.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(60, 90, 130));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(900, 50));

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));
        tabs.addTab("Farms",       new FarmsPanel());
        RestaurantsPanel restaurantsPanel = new RestaurantsPanel();
        tabs.addTab("Orders",new OrdersPanel(restaurantsPanel));
        tabs.addTab("Restaurants", restaurantsPanel);
        tabs.addTab("Inquiries",   new InquiriesPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { /* default */ }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
