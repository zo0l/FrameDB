package farmtotable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FarmsPanel extends JPanel {

    private JTable table;
    private JTextField txtName, txtLocation, txtContact;
    private JTextField txtId, txtNewContact;

    public FarmsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        //  get Table
        String[] cols = {"Farm ID", "Farm Name", "Location", "Contact"};
        Object[][] data = DBHelper.getData("SELECT FarmID, FarmName, Location, ContactNumber FROM FARM");
        table = new JTable(new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Bottom controls
        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 0));
        bottom.add(buildAddBox());
        bottom.add(buildDeleteBox());
        bottom.add(buildUpdateBox());
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildAddBox() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Add New Farm"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        txtName     = new JTextField(12);
        txtLocation = new JTextField(12);
        txtContact  = new JTextField(12);

        g.gridx = 0; g.gridy = 0; p.add(new JLabel("Farm Name:"), g);
        g.gridx = 1; p.add(txtName, g);
        g.gridx = 0; g.gridy = 1; p.add(new JLabel("Location:"), g);
        g.gridx = 1; p.add(txtLocation, g);
        g.gridx = 0; g.gridy = 2; p.add(new JLabel("Contact:"), g);
        g.gridx = 1; p.add(txtContact, g);

        
        //insert to table
        JButton btn = new JButton("Add");
        btn.addActionListener(e -> {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Farm Name");
                return;
            }
            String sql = "INSERT INTO FARM (FarmName, Location, ContactNumber) VALUES ('"
                            + txtName.getText() + "', '"
                            + txtLocation.getText() + "', '"
                            + txtContact.getText() + "')";
                        
                         if (DBHelper.runUpdate(sql)) {
                            refreshTable();
                            txtName.setText(""); txtLocation.setText(""); txtContact.setText("");
                            JOptionPane.showMessageDialog(this, "Farm added");
            }

        });
        g.gridx = 1; g.gridy = 3; p.add(btn, g);

        return p;
    }

    private JPanel buildDeleteBox() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Delete Farm"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        txtId = new JTextField(8);
        g.gridx = 0; g.gridy = 0; p.add(new JLabel("Farm ID:"), g);
        g.gridx = 1; p.add(txtId, g);
        
        //delete from table
        JButton btnDel = new JButton("Delete");
        btnDel.setForeground(new Color(180, 30, 30));
        btnDel.addActionListener(e -> {
            String idTxt = txtId.getText().trim();
           if (idTxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter ID"); return; }
            int c = JOptionPane.showConfirmDialog(this,
                "Delete farm with ID " + idTxt + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM FARM WHERE FarmID = " + idTxt;
                if (DBHelper.runUpdate(sql)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Deleted");
                }
            }
        });



        g.gridx = 1; g.gridy = 1; p.add(btnDel, g);

        return p;
    }

    private JPanel buildUpdateBox() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Update Contact"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        JTextField txtUpdateId = new JTextField(8);
        txtNewContact = new JTextField(12);

        g.gridx = 0; g.gridy = 0; p.add(new JLabel("Farm ID:"), g);
        g.gridx = 1; p.add(txtUpdateId, g);
        g.gridx = 0; g.gridy = 1; p.add(new JLabel("New Contact:"), g);
        g.gridx = 1; p.add(txtNewContact, g);
        
        //update from table
        JButton btn = new JButton("Update");
        btn.addActionListener(e -> {
      if (txtUpdateId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Farm ID");
                return;
            }
            String sql = "UPDATE FARM SET ContactNumber = '"
                + txtNewContact.getText() + "' WHERE FarmID = "
                + txtUpdateId.getText();
            if (DBHelper.runUpdate(sql)) {
                refreshTable();
                txtUpdateId.setText(""); txtNewContact.setText("");
                JOptionPane.showMessageDialog(this, "Updated");
            }
        });
        g.gridx = 1; g.gridy = 2; p.add(btn, g);

        return p;
    }
    private void refreshTable() {
        Object[][] data = DBHelper.getData(
            "SELECT FarmID, FarmName, Location, ContactNumber FROM FARM"
        );
        DefaultTableModel m = (DefaultTableModel) 
        table.getModel();
        m.setRowCount(0);
        for (Object[] row : data) m.addRow(row);
    }
}
