package CrowdFunding.view;

import CrowdFunding.controller.ProjectController;
import CrowdFunding.controller.PledgeController;
import CrowdFunding.controller.AuthController;
import CrowdFunding.model.Project;
import CrowdFunding.model.Reward;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 ProjectDetailView
  หน้ารายละเอียดของ Project
 แสดงข้อมูลโครงการ , แสดง Reward tiers ที่เลือกได้ , ต้อง Login ก่อนถึงจะ Pledge ได้
 */
public class ProjectDetailView extends JPanel {

    //Dependencies 
    private final Project project;
    private final ProjectController projectCtrl;
    private final PledgeController pledgeCtrl;
    private final AuthController auth;
    private final Runnable onBack;

    //UI 
    private final JLabel lName = new JLabel(); // ชื่อ Project
    private final JLabel lTarget = new JLabel(); // Target amount
    private final JLabel lCurrent = new JLabel(); // Current amount
    private final JLabel lDeadline = new JLabel(); // Deadline
    private final JProgressBar progress = new JProgressBar(0, 100); // Progress bar

    private final JComboBox<String> rewardBox = new JComboBox<>();
    private final JLabel lRewardHint = new JLabel(" "); // โชว์ minAmount/Quota
    private final JTextField amountField = new JTextField(10);

    //Constructor
    public ProjectDetailView(Project project,
                             ProjectController projectCtrl,
                             PledgeController pledgeCtrl,
                             AuthController auth,
                             Runnable onBack) {
        this.project = project;
        this.projectCtrl = projectCtrl;
        this.pledgeCtrl = pledgeCtrl;
        this.auth = auth;
        this.onBack = onBack;

        initUI();
        refreshInfo();
    }

    // สร้าง UI 

    private void initUI() {
        setLayout(new BorderLayout());

        // Header + Back
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("← Back");
        back.addActionListener(e -> { if (onBack != null) onBack.run(); });
        top.add(back);
        add(top, BorderLayout.NORTH);

        // Body 
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        lName.setFont(lName.getFont().deriveFont(Font.BOLD, 18f));
        lName.setAlignmentX(LEFT_ALIGNMENT);
        body.add(centerRow(lName));
        body.add(Box.createVerticalStrut(6));

        body.add(row("Target:  ", lTarget));
        body.add(row("Current: ", lCurrent));
        body.add(row("Deadline:", lDeadline));

        progress.setStringPainted(true);
        body.add(Box.createVerticalStrut(8));
        body.add(progress);

        body.add(Box.createVerticalStrut(12));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(8));

        // Pledge form
        JPanel pledgePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pledgePane.add(new JLabel("Reward:"));
        rewardBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
        pledgePane.add(rewardBox);

        pledgePane.add(new JLabel("  Amount:"));
        amountField.setPreferredSize(new Dimension(110, amountField.getPreferredSize().height));
        pledgePane.add(amountField);

        JButton pledgeBtn = new JButton("Pledge");
        pledgePane.add(pledgeBtn);

        // ข้อความแสดง minAmount/Quota
        lRewardHint.setForeground(new Color(90, 90, 90));
        pledgePane.add(new JLabel("  "));
        pledgePane.add(lRewardHint);

        body.add(pledgePane);

        // เติม rewards จาก Project
        rewardBox.addItem(""); // allow no reward
        List<Reward> tiers = projectCtrl.rewardsOf(project.getProjectId());
        for (Reward r : tiers) rewardBox.addItem(r.getRewardName());

        rewardBox.addActionListener(e -> updateRewardHint());

        pledgeBtn.addActionListener(e -> doPledge());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(body, BorderLayout.NORTH);
        add(new JScrollPane(wrap), BorderLayout.CENTER);

        updateRewardHint();
    }

    private JPanel row(String label, JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(label));
        p.add(comp);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private JPanel centerRow(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(comp);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }

     /**
     * โหลดข้อมูลโครงการมาแสดง (ชื่อ, target, current, deadline, progress)
     */
    private void refreshInfo() {
        lName.setText(project.getName());
        lTarget.setText(String.valueOf((long) project.getTargetAmount()));
        lCurrent.setText(String.valueOf((long) project.getCurrentAmount()));
        lDeadline.setText(project.getDeadline());

        double target = project.getTargetAmount() <= 0 ? 1.0 : project.getTargetAmount();
        int pct = (int) Math.min(100, Math.round(project.getCurrentAmount() * 100.0 / target));
        progress.setValue(pct);
        progress.setString(pct + "%");
    }

     /**
     * แสดง reward ที่เลือก (ขั้นต่ำ + quota)
     */
    private void updateRewardHint() {
        String selected = (String) rewardBox.getSelectedItem();
        if (selected == null || selected.trim().isEmpty()) {
            lRewardHint.setText("เลือกแบบไม่รับรางวัลได้");
            return;
        }
        // หา tier เพื่อโชว์ minAmount/Quota
        List<Reward> tiers = projectCtrl.rewardsOf(project.getProjectId());
        for (Reward r : tiers) {
            if (r.getRewardName().equalsIgnoreCase(selected)) {
                lRewardHint.setText("(ขั้นต่ำ " + (long) r.getMinAmount()
                        + ", quota เหลือ " + r.getQuotaOrRemaining() + ")");
                return;
            }
        }
        lRewardHint.setText(" ");
    }

    private void doPledge() {
        // ต้องล็อกอินก่อน
        if (!auth.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "กรุณา Login ก่อนทำรายการ",
                    "Need Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String supporter = auth.getCurrentUser();  // ชื่อผู้ใช้ที่ล็อกอิน

        String reward = (String) rewardBox.getSelectedItem();
        if (reward != null && reward.trim().isEmpty()) reward = "";

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Amount ไม่ถูกต้อง",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ส่งไปยัง PledgeController
        try {
            pledgeCtrl.submitPledge(supporter, project.getProjectId(), amount, reward);
            JOptionPane.showMessageDialog(this, "Pledge สำเร็จ!");
            amountField.setText("");
            // refresh ข้อมูลปัจจุบัน (current/progress)
            refreshInfo();
            // อัปเดต hint quota ทันที
            updateRewardHint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

