package farmtotable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrdersPanel extends JPanel {

    private JTable table;
    private JTextField txtRestId, txtBatchId, txtQuantity;
    private JSpinner spnDate;
    private JComboBox<String> cmbStatus;
    private JTextField txtOrderId;
    private JComboBox<String> cmbNewStatus;
    private JTextField txtDeleteId;
    private RestaurantsPanel restaurantsPanel;

    public OrdersPanel(RestaurantsPanel restaurantsPanel) {

        this.restaurantsPanel = restaurantsPanel;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {
            "Order ID",
            "Restaurant",
            "Farm",
            "Crop",
            "Quantity",
            "Date",
            "Status",
            "Amount"
        };

        Object[][] data = DBHelper.getData(
            "SELECT O.OrderID, R.RestaurantName, F.FarmName, C.CropName, " +
            "OD.Quantity, O.OrderDate, O.OrderStatus, O.TotalAmount " +
            "FROM ORDERS O " +
            "JOIN RESTAURANT R ON O.RestaurantID = R.RestaurantID " +
            "JOIN ORDERDETAILS OD ON O.OrderID = OD.OrderID " +
            "JOIN HARVESTBATCH HB ON OD.BatchID = HB.BatchID " +
            "JOIN FARM F ON HB.FarmID = F.FarmID " +
            "JOIN CROPTYPE C ON HB.CropID = C.CropID"
        );

        table = new JTable(new DefaultTableModel(data, cols) {

            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });

        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 0));

        bottom.add(buildAddBox());
        bottom.add(buildDeleteBox());
        bottom.add(buildUpdateBox());

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildAddBox() {

        JPanel p = new JPanel(new GridBagLayout());

        p.setBorder(new TitledBorder("Add New Order"));

        GridBagConstraints g = new GridBagConstraints();

        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        txtRestId = new JTextField(10);

        spnDate = new JSpinner(new SpinnerDateModel());

        spnDate.setEditor(
            new JSpinner.DateEditor(spnDate, "yyyy-MM-dd")
        );

        spnDate.setPreferredSize(
            new Dimension(120, 22)
        );

        cmbStatus = new JComboBox<>(
            new String[]{
                "Pending",
                "Confirmed",
                "Delivered",
                "Cancelled"
            }
        );

        txtBatchId = new JTextField(10);

        txtQuantity = new JTextField(10);

        DefaultTableModel batchModel =
            new DefaultTableModel(
                new String[]{
                    "BatchID",
                    "Quantity"
                },
                0
            );

        JTable batchTable = new JTable(batchModel);

        JScrollPane batchScroll =
            new JScrollPane(batchTable);

        batchScroll.setPreferredSize(
            new Dimension(180, 90)
        );

        JButton btnAddBatch =
            new JButton("Add Batch");

        JButton btn =
            new JButton("Add");

        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("Rest. ID:"), g);

        g.gridx = 1;
        p.add(txtRestId, g);

        g.gridx = 0;
        g.gridy = 1;
        p.add(new JLabel("Date:"), g);

        g.gridx = 1;
        p.add(spnDate, g);

        g.gridx = 0;
        g.gridy = 2;
        p.add(new JLabel("Status:"), g);

        g.gridx = 1;
        p.add(cmbStatus, g);

        g.gridx = 0;
        g.gridy = 3;
        p.add(new JLabel("Batch ID:"), g);

        g.gridx = 1;
        p.add(txtBatchId, g);

        g.gridx = 0;
        g.gridy = 4;
        p.add(new JLabel("Quantity:"), g);

        g.gridx = 1;
        p.add(txtQuantity, g);

        g.gridx = 0;
        g.gridy = 5;
        p.add(btnAddBatch, g);

        g.gridx = 1;
        p.add(btn, g);

        g.gridx = 0;
        g.gridy = 6;
        g.gridwidth = 2;

        p.add(batchScroll, g);

        btnAddBatch.addActionListener(e -> {

            if (txtBatchId.getText().trim().isEmpty() || txtQuantity.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Enter Batch ID and Quantity");

                return;
            }

            batchModel.addRow(new Object[]{
                txtBatchId.getText(),
                txtQuantity.getText()
            });

            txtBatchId.setText("");
            txtQuantity.setText("");
        });

        btn.addActionListener(e -> {

            double total = 0;

            for (int i = 0;i < batchModel.getRowCount();i++) {

                String batchId =batchModel.getValueAt(i, 0).toString();

                double qty =Double.parseDouble(batchModel.getValueAt(i, 1).toString() );

                String getBatchSql =
                    "SELECT UnitPrice, AvailableQuantity " +
                    "FROM HARVESTBATCH " +
                    "WHERE BatchID = " +
                    batchId;

                Object[][] batchData =DBHelper.getData(getBatchSql);

                double unitPrice =Double.parseDouble(batchData[0][0].toString());

                double availableQty =Double.parseDouble(batchData[0][1].toString());

                if (qty > availableQty) {

                    JOptionPane.showMessageDialog( this,"Not enough quantity for Batch "+ batchId);

                    return;
                }

                total += unitPrice * qty;
            }

            String dateStr =new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.util.Date)spnDate.getValue());

            String sql =
                "INSERT INTO ORDERS " +
                "(RestaurantID, OrderDate, OrderStatus, TotalAmount) VALUES ("
                + txtRestId.getText()
                + ", '"
                + dateStr
                + "', '"
                + cmbStatus.getSelectedItem()
                + "', "
                + total
                + ")";

            if (DBHelper.runUpdate(sql)) {

                Object[][] lastOrder =DBHelper.getData("SELECT MAX(OrderID) FROM ORDERS");

                int orderId =Integer.parseInt(lastOrder[0][0].toString());

                for (int i = 0;i < batchModel.getRowCount();i++) {

                    String batchId =batchModel.getValueAt(i, 0).toString();

                    String qty =batchModel.getValueAt(i, 1).toString();

                    String detailSql =
                        "INSERT INTO ORDERDETAILS " +
                        "(OrderID, BatchID, Quantity) VALUES ("
                        + orderId
                        + ", "
                        + batchId
                        + ", "
                        + qty
                        + ")";

                    DBHelper.runUpdate(detailSql);

                    String updateBatchSql =
                        "UPDATE HARVESTBATCH " +
                        "SET AvailableQuantity = AvailableQuantity - "
                        + qty +
                        " WHERE BatchID = " +
                        batchId;

                    DBHelper.runUpdate(updateBatchSql);
                }

                refreshTable();

                restaurantsPanel.refreshBatches();

                txtRestId.setText("");
                txtBatchId.setText("");
                txtQuantity.setText("");

                batchModel.setRowCount(0);

                JOptionPane.showMessageDialog(
                    this,
                    "Order added"
                );
            }
        });

        return p;
    }

    private JPanel buildDeleteBox() {

        JPanel p =
            new JPanel(new GridBagLayout());

        p.setBorder(
            new TitledBorder("Delete Order")
        );

        GridBagConstraints g =
            new GridBagConstraints();

        g.insets =
            new Insets(4, 4, 4, 4);

        g.anchor =
            GridBagConstraints.WEST;

        txtDeleteId =
            new JTextField(8);

        g.gridx = 0;
        g.gridy = 0;

        p.add(
            new JLabel("Order ID:"),
            g
        );

        g.gridx = 1;

        p.add(txtDeleteId, g);

        JButton btnDel =
            new JButton("Delete Order");

        btnDel.setForeground(
            new Color(180, 30, 30)
        );

        btnDel.addActionListener(e -> {

            String idTxt =
                txtDeleteId
                .getText()
                .trim();

            if (idTxt.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Enter Order ID"
                );

                return;
            }

            int c =
                JOptionPane.showConfirmDialog(
                    this,
                    "Delete order with ID "
                    + idTxt + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
                );

            if (
                c == JOptionPane.YES_OPTION
            ) {

                Object[][] orderData =
                    DBHelper.getData(
                        "SELECT BatchID, Quantity " +
                        "FROM ORDERDETAILS " +
                        "WHERE OrderID = "
                        + idTxt
                    );

                if (
                    orderData.length > 0
                ) {

                    String updateQty =
                        "UPDATE HARVESTBATCH " +
                        "SET AvailableQuantity = AvailableQuantity + "
                        + orderData[0][1]
                        + " WHERE BatchID = "
                        + orderData[0][0];

                    DBHelper.runUpdate(
                        updateQty
                    );
                }

                DBHelper.runUpdate(
                    "DELETE FROM ORDERDETAILS " +
                    "WHERE OrderID = "
                    + idTxt
                );

                String sql =
                    "DELETE FROM ORDERS " +
                    "WHERE OrderID = "
                    + idTxt;

                if (
                    DBHelper.runUpdate(sql)
                ) {

                    refreshTable();

                    restaurantsPanel
                    .refreshBatches();

                    txtDeleteId.setText("");

                    JOptionPane.showMessageDialog(
                        this,
                        "Deleted"
                    );
                }
            }
        });

        g.gridx = 1;
        g.gridy = 1;

        p.add(btnDel, g);

        return p;
    }

    private JPanel buildUpdateBox() {

        JPanel p =
            new JPanel(new GridBagLayout());

        p.setBorder(
            new TitledBorder("Update Status")
        );

        GridBagConstraints g =
            new GridBagConstraints();

        g.insets =
            new Insets(4, 4, 4, 4);

        g.anchor =
            GridBagConstraints.WEST;

        txtOrderId =
            new JTextField(8);

        cmbNewStatus =
            new JComboBox<>(
                new String[]{
                    "Pending",
                    "Confirmed",
                    "Delivered",
                    "Cancelled"
                }
            );

        g.gridx = 0;
        g.gridy = 0;

        p.add(
            new JLabel("Order ID:"),
            g
        );

        g.gridx = 1;

        p.add(txtOrderId, g);

        g.gridx = 0;
        g.gridy = 1;

        p.add(
            new JLabel("New Status:"),
            g
        );

        g.gridx = 1;

        p.add(cmbNewStatus, g);

        JButton btn =
            new JButton("Update");

        btn.addActionListener(e -> {

            if (
                txtOrderId
                .getText()
                .trim()
                .isEmpty()
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Enter Order ID"
                );

                return;
            }

            Object[][] orderCheck =
                DBHelper.getData(
                    "SELECT OrderID " +
                    "FROM ORDERS " +
                    "WHERE OrderID = "
                    + txtOrderId.getText()
                );

            if (
                orderCheck.length == 0
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Order ID not found"
                );

                return;
            }

            String sql =
                "UPDATE ORDERS " +
                "SET OrderStatus = '"
                + cmbNewStatus
                .getSelectedItem()
                + "' WHERE OrderID = "
                + txtOrderId.getText();

            if (
                DBHelper.runUpdate(sql)
            ) {

                refreshTable();

                txtOrderId.setText("");

                JOptionPane.showMessageDialog(
                    this,
                    "Updated"
                );
            }
        });

        g.gridx = 1;
        g.gridy = 2;

        p.add(btn, g);

        return p;
    }

    private void refreshTable() {

        Object[][] data =
            DBHelper.getData(
                "SELECT O.OrderID, " +
                "R.RestaurantName, " +
                "F.FarmName, " +
                "C.CropName, " +
                "OD.Quantity, " +
                "O.OrderDate, " +
                "O.OrderStatus, " +
                "O.TotalAmount " +
                "FROM ORDERS O " +
                "JOIN RESTAURANT R " +
                "ON O.RestaurantID = R.RestaurantID " +
                "JOIN ORDERDETAILS OD " +
                "ON O.OrderID = OD.OrderID " +
                "JOIN HARVESTBATCH HB " +
                "ON OD.BatchID = HB.BatchID " +
                "JOIN FARM F " +
                "ON HB.FarmID = F.FarmID " +
                "JOIN CROPTYPE C " +
                "ON HB.CropID = C.CropID"
            );

        DefaultTableModel m =
            (DefaultTableModel)
            table.getModel();

        m.setRowCount(0);

        for (Object[] row : data) {

            m.addRow(row);
        }
    }
}