package farmtotable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RestaurantsPanel extends JPanel {
    private JTable tblBatches;
    public RestaurantsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(220);
        split.setBorder(null);

        // Restaurants
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(new TitledBorder("All Restaurants"));

        String[] rCols = {"ID", "Name", "Address", "Contact"};
        Object[][] rData = DBHelper.getData(
    "SELECT RestaurantID, RestaurantName, DeliveryAddress, ContactNumber FROM RESTAURANT"
);
        JTable tblRest = new JTable(new DefaultTableModel(rData, rCols) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        tblRest.setRowHeight(25);
        tblRest.setFont(new Font("Arial", Font.PLAIN, 12));
        tblRest.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));



        top.add(new JScrollPane(tblRest), BorderLayout.CENTER);
       

        // Batches with JOIN
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(new TitledBorder("Harvest Batches (Farm + Crop — JOIN)"));

        String[] bCols = {"Batch ID", "Farm", "Crop", "Harvest Date", "Quantity","Unit Price"};
            Object[][] bData = DBHelper.getData(
                "SELECT HB.BatchID, F.FarmName, CT.CropName, HB.HarvestDate, HB.AvailableQuantity, HB.UnitPrice " +
                "FROM HARVESTBATCH HB " +
                "JOIN FARM F ON HB.FarmID = F.FarmID " +
                "JOIN CROPTYPE CT ON HB.CropID = CT.CropID"
            ); 
      tblBatches = new JTable(new DefaultTableModel(bData, bCols) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        tblBatches.setRowHeight(25);
        tblBatches.setFont(new Font("Arial", Font.PLAIN, 12));
        tblBatches.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));


        bottom.add(new JScrollPane(tblBatches), BorderLayout.CENTER);

        split.setTopComponent(top);
        split.setBottomComponent(bottom);
        add(split, BorderLayout.CENTER);
    }
        public void refreshBatches() {

    Object[][] bData = DBHelper.getData(
        "SELECT HB.BatchID, F.FarmName, CT.CropName, HB.HarvestDate, HB.AvailableQuantity, HB.UnitPrice " +
        "FROM HARVESTBATCH HB " +
        "JOIN FARM F ON HB.FarmID = F.FarmID " +
        "JOIN CROPTYPE CT ON HB.CropID = CT.CropID"
    );

    DefaultTableModel m =
        (DefaultTableModel) tblBatches.getModel();

    m.setRowCount(0);

    for (Object[] row : bData) {
        m.addRow(row);
    }
}
}
