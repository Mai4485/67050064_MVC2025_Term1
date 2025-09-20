import CrowdFunding.model.repository.DataStore;
import CrowdFunding.controller.ProjectController;
import CrowdFunding.controller.PledgeController;
import CrowdFunding.controller.AuthController;
import CrowdFunding.view.MainView;

public class App {
    public static void main(String[] args) throws Exception {
        // base path ที่เก็บไฟล์ CSV ของระบบ 
        String base = "CrowdFunding/model/database";
        // โหลดข้อมูลจากไฟล์ CSV ทั้งหมดมาเก็บไว้ใน DataStore
        DataStore ds = new DataStore(base);

        // สร้าง Controller ต่าง ๆ โดยส่ง DataStore ให้ใช้งานร่วมกัน
        ProjectController pc = new ProjectController(ds);
        PledgeController  plc = new PledgeController(ds);
        AuthController    ac  = new AuthController();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainView(pc, plc, ds, ac).setVisible(true);
        });
    }
}
