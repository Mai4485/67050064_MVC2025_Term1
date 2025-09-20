package CrowdFunding.view;

import CrowdFunding.model.Pledge;
import CrowdFunding.model.Project;
import CrowdFunding.model.repository.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 SummaryView หน้าสรุปสถิติของระบบ
 *  ตารางสรุปต่อโครงการ: จำนวน Pledge สำเร็จ , จำนวนที่ถูกปฏิเสธ และยอดรวม
 successอ่านจาก DataStore (pledges.csv) , failed อ่านจากไฟล์ log  pledge_failures.csv
 */
public class SummaryView extends JPanel {
    public interface Back { void run(); }

    private final DataStore store;
    private static final String FAIL_LOG = "CrowdFunding/model/database/pledge_failures.csv";

    public SummaryView(DataStore store, Back onBack) {
        this.store = store;
        setLayout(new BorderLayout());

        // header + back
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("← Back");
        back.addActionListener(e -> onBack.run());
        top.add(back);
        top.add(new JLabel("Summary"));
        add(top, BorderLayout.NORTH);

        //สรุปรวมจาก success
        long totalSuccess = store.pledges.all().size();
        long totalAmount  = 0;
        for (Pledge p : store.pledges.all()) totalAmount += (long) p.getAmount();

        //โหลด FAILURE LOG 
        Map<String, Long> failByProject = new HashMap<>();
        long totalFailed = 0;
        File f = new File(FAIL_LOG);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] t = line.split(",", -1);
                    if (t.length >= 3) {
                        String projectId = t[2].trim();
                        if (!projectId.isEmpty()) {
                            failByProject.put(projectId, failByProject.getOrDefault(projectId, 0L) + 1);
                            totalFailed++;
                        }
                    }
                }
            } catch (Exception ignore) {}
        }

        
        DefaultTableModel m = new DefaultTableModel(
                new Object[]{"projectId", "name", "pledges", "failed", "amount"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        //success ต่อโครงการ
        Map<String, Long> succCount = new HashMap<>();
        Map<String, Long> succAmount = new HashMap<>();
        for (Pledge p : store.pledges.all()) {
            succCount.put(p.getProjectId(), succCount.getOrDefault(p.getProjectId(), 0L) + 1);
            succAmount.put(p.getProjectId(), succAmount.getOrDefault(p.getProjectId(), 0L) + (long) p.getAmount());
        }

        //เติมแถวในตาราง
        for (Project pr : store.projects.all()) {
            String id = pr.getProjectId();
            long c = succCount.getOrDefault(id, 0L);
            long a = succAmount.getOrDefault(id, 0L);
            long fcount = failByProject.getOrDefault(id, 0L);
            if (c > 0 || a > 0 || fcount > 0) {
                m.addRow(new Object[]{id, pr.getName(), c, fcount, a});
            }
        }

        JTable table = new JTable(m);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // สรุปรวม 
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 12));
        stats.add(card("จำนวนสำเร็จ", String.valueOf(totalSuccess)));
        stats.add(card("จำนวนถูกปฏิเสธ", String.valueOf(totalFailed)));
        stats.add(card("ยอดรวมทั้งหมด", String.valueOf(totalAmount)));
        add(stats, BorderLayout.SOUTH);
    }

    private JComponent card(String title, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel h = new JLabel(title);
        h.setFont(h.getFont().deriveFont(Font.BOLD));
        JLabel v = new JLabel(value);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 18f));
        p.add(h); p.add(v);
        return p;
    }
}

