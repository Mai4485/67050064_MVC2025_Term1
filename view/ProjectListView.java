package CrowdFunding.view;
import CrowdFunding.controller.ProjectController;
import CrowdFunding.model.Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ProjectListView
 * หน้ารายการโครงการ 
 * - แสดงรายชื่อโครงการทั้งหมด
 * - มีแถบเครื่องมือให้ ค้นหา / เรียงตามล่าสุด / ใกล้หมดเขต / ยอดระดมสูงสุด
 * - กดคลิกที่แถวเพื่อเปิดรายละเอียดโครงการ
 */
public class ProjectListView extends JPanel {
    public interface OpenDetail { void run(String projectId); }

    private final ProjectController ctrl;
    private final OpenDetail openDetail;
    private final JTable table = new JTable();
    private final JTextField search = new JTextField(18);

    public ProjectListView(ProjectController ctrl, OpenDetail openDetail) {
        this.ctrl = ctrl;
        this.openDetail = openDetail;

        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH); // แถบเครื่องมือด้านบน
        add(new JScrollPane(table), BorderLayout.CENTER); 

        reload();  // เติมข้อมูลทันที
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ดับเบิลคลิกเพื่อเปิดรายละเอียด
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = table.getSelectedRow();
                    if (r >= 0) openDetail.run(table.getValueAt(r, 0).toString());
                }
            }
        });
    }

     // สร้างแถบเครื่องมือด้านบน  
    private JComponent buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bSearch = new JButton("Search");
        JButton bLatest = new JButton("Latest");
        JButton bEnding = new JButton("Ending Soon");
        JButton bTop = new JButton("Top Funded");

        bSearch.addActionListener(e -> fill(ctrl.search(search.getText())));
        bLatest.addActionListener(e -> fill(ctrl.sortByLatest()));
        bEnding.addActionListener(e -> fill(ctrl.sortByEndingSoon()));
        bTop.addActionListener(e -> fill(ctrl.sortByTopFunded()));

        p.add(new JLabel("Keyword:"));
        p.add(search);
        p.add(bSearch); p.add(bLatest); p.add(bEnding); p.add(bTop);
        return p;
    }

    public void reload() { fill(ctrl.sortByEndingSoon()); }

    //เติมรายการโครงการลงใน JTable
    private void fill(List<Project> items) {
        DefaultTableModel m = new DefaultTableModel(
            new Object[]{"projectId", "name", "target", "current", "deadline"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Project p : items) {
            m.addRow(new Object[]{
                p.getProjectId(), p.getName(),
                (long)p.getTargetAmount(), (long)p.getCurrentAmount(),
                p.getDeadline()
            });
        }
        table.setModel(m);
    }
}

