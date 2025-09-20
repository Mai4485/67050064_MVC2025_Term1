package CrowdFunding.view;

import CrowdFunding.controller.ProjectController;
import CrowdFunding.controller.PledgeController;
import CrowdFunding.controller.AuthController;
import CrowdFunding.model.Project;
import CrowdFunding.model.repository.DataStore;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private final ProjectController projectCtrl; //logic project
    private final PledgeController  pledgeCtrl; //logic pledge
    private final DataStore         store; //จัดเก็บข้อมูลจาก csv
    private final AuthController    auth; //จัดการหน้า login

    // Layout หลัก
    private final CardLayout card = new CardLayout();
    private final JPanel container = new JPanel(card);

    private ProjectListView listView;
    private final JLabel userLabel = new JLabel("Guest");

    /**
     * Constructor: รับ Controllers และ DataStore มาเชื่อมต่อกับ View
     */
    public MainView(ProjectController projectCtrl,
                    PledgeController pledgeCtrl,
                    DataStore store,
                    AuthController auth) {
        this.projectCtrl = projectCtrl;
        this.pledgeCtrl  = pledgeCtrl;
        this.store       = store;
        this.auth        = auth;

        // ตั้งค่าหน้าต่างหลัก
        setTitle("CrowdFunding (MVC)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- Toolbar ----------
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        
        // ปุ่ม Projects กลับไปหน้า list
        JButton btnProjects = new JButton("Projects");
        btnProjects.addActionListener(e -> {
            if (listView != null) listView.reload();
            card.show(container, "list");
        });

        // ปุ่ม Summary
        JButton btnSummary = new JButton("Summary");
        btnSummary.addActionListener(e -> {
            SummaryView summary = new SummaryView(store,
                    () -> card.show(container, "list"));
            container.add(summary, "summary");  
            card.show(container, "summary");
        });

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> openLoginDialog());

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> { auth.logout(); updateUserLabel(); });

        // เพิ่มปุ่มต่าง ๆ ลงใน toolbar
        bar.add(btnProjects);
        bar.add(btnSummary);
        bar.addSeparator();
        bar.add(new JLabel("User: "));
        bar.add(userLabel);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(btnLogin);
        bar.add(btnLogout);

        add(bar, BorderLayout.NORTH);

        //  Default page Project list
        listView = new ProjectListView(projectCtrl, this::openDetail);
        container.add(listView, "list");
        add(container, BorderLayout.CENTER);
        card.show(container, "list");

        updateUserLabel();
    }

    //Login dialog 
    private void openLoginDialog() {
        JDialog dlg = new JDialog(this, "Login", true);
        dlg.setContentPane(new LoginView(auth, new LoginView.OnLogin() {
            @Override public void success() {
                updateUserLabel();
                dlg.dispose();
            }
        }));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void updateUserLabel() {
        userLabel.setText(auth.isLoggedIn() ? auth.getCurrentUser() : "Guest");
    }

    // Open detail
    private void openDetail(String projectId) {
        Project p = projectCtrl.get(projectId);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Project not found: " + projectId,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProjectDetailView detail = new ProjectDetailView(
                p,
                projectCtrl,
                pledgeCtrl,
                auth,
                new Runnable() { // onBack
                    @Override public void run() {
                        listView.reload();
                        card.show(container, "list");
                    }
                }
        );

        container.add(detail, "detail");
        card.show(container, "detail");
    }
}

