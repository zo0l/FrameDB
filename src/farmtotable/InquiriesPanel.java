package farmtotable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InquiriesPanel extends JPanel {

    private JTable resultTable;
    private JLabel lblTitle;

    public InquiriesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left: list of inquiries
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new TitledBorder("Select Inquiry"));
        left.setPreferredSize(new Dimension(280, 400));

        String[] inquiries = {
            "Crop Type with Max Orders",
            "Farms with No Sales Last Month",
            "Restaurants with No Orders Last month",
            "Revenue per Farm"
        };

        left.add(Box.createVerticalStrut(8));
        for (int i = 0; i < inquiries.length; i++) {
            final int idx = i + 1;
            JButton btn = new JButton(inquiries[i]);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(250, 32));
            btn.addActionListener(e -> showInquiry(idx));
            left.add(btn);
            left.add(Box.createVerticalStrut(6));
        }

        // Right: results
        JPanel right = new JPanel(new BorderLayout(5, 8));
        right.setBorder(new TitledBorder("Results"));

        lblTitle = new JLabel("Click an inquiry to see results");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setBorder(new EmptyBorder(5, 5, 5, 5));

        resultTable = new JTable();
        resultTable.setRowHeight(25);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        right.add(lblTitle, BorderLayout.NORTH);
        right.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.add(left,  BorderLayout.WEST);
        center.add(right, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void showInquiry(int num) {
        DefaultTableModel m;
         Object[][] rows;
        switch (num) {
            case 1:
                lblTitle.setText("Crop Type with Maximum Sales");

                m = new DefaultTableModel(
                    new String[]{"Crop Name", "Total Sold"}, 0);

                rows = DBHelper.getData(
                    "SELECT TOP 1 CT.CropName, SUM(OD.Quantity) AS TotalSold " +
                    "FROM CROPTYPE CT " +
                    "JOIN HARVESTBATCH HB ON CT.CropID = HB.CropID " +
                    "JOIN ORDERDETAILS OD ON HB.BatchID = OD.BatchID " +
                    "GROUP BY CT.CropName " +
                    "ORDER BY TotalSold DESC"
                );

                for (Object[] r : rows)
                    m.addRow(r);

                break;

            case 2:

               lblTitle.setText("Farms with No Sales Last Month");

               m = new DefaultTableModel(
                   new String[]{"Farm Name"}, 0);

               rows = DBHelper.getData(

                   "SELECT F.FarmName " +
                   "FROM FARM F " +
                   "WHERE F.FarmID NOT IN ( " +

                   "SELECT DISTINCT HB.FarmID " +
                   "FROM HARVESTBATCH HB " +
                   "JOIN ORDERDETAILS OD ON HB.BatchID = OD.BatchID " +
                   "JOIN ORDERS O ON OD.OrderID = O.OrderID " +

                   "WHERE MONTH(O.OrderDate) = MONTH(DATEADD(MONTH,-1,GETDATE())) " +
                   "AND YEAR(O.OrderDate) = YEAR(DATEADD(MONTH,-1,GETDATE())) )"
               );

               for (Object[] r : rows)
                   m.addRow(r);

               break;

            case 3:

                lblTitle.setText(
                    "Restaurants That Did Not Place Orders Last Month");

                m = new DefaultTableModel(
                    new String[]{"Restaurant Name"}, 0);

                rows = DBHelper.getData(

                    "SELECT RestaurantName " +
                    "FROM RESTAURANT " +
                    "WHERE RestaurantID NOT IN ( " +

                    "SELECT DISTINCT RestaurantID " +
                    "FROM ORDERS " +

                    "WHERE MONTH(OrderDate) = MONTH(DATEADD(MONTH,-1,GETDATE())) " +
                    "AND YEAR(OrderDate) = YEAR(DATEADD(MONTH,-1,GETDATE())) )"
                );

                for (Object[] r : rows)
                    m.addRow(r);

                break;

            case 4:

                lblTitle.setText("Revenue Per Farm");

                m = new DefaultTableModel(
                    new String[]{"Farm Name", "Total Revenue"}, 0);

                rows = DBHelper.getData(

                    "SELECT F.FarmName, " +
                    "SUM(OD.Quantity * HB.UnitPrice) AS TotalRevenue " +

                    "FROM FARM F " +

                    "JOIN HARVESTBATCH HB " +
                    "ON F.FarmID = HB.FarmID " +

                    "JOIN ORDERDETAILS OD " +
                    "ON HB.BatchID = OD.BatchID " +

                    "GROUP BY F.FarmName " +

                    "ORDER BY TotalRevenue DESC"
                );

                for (Object[] r : rows)
                    m.addRow(r);

                break;

            default:
                return;
        }
        resultTable.setModel(m);
    }
}