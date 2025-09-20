package CrowdFunding.controller;
/*AuthController สำหรับจัดการการยืนยันตัวตน
  จัดเก็บสถานะผู้ใช้ที่กำลัง Login อยู่ 
  ให้ View / Controller อื่นเรียกเช็คได้ว่าใครกำลังใช้งาน
 */
public class AuthController {
    private String currentUser;

    //ตรวจสอบว่ามีผู้ใช้ล็อกอินอยู่หรือไม่
    public boolean isLoggedIn() {
        return currentUser != null && !currentUser.trim().isEmpty();
    }

    public String getCurrentUser() {
        return currentUser;
    }

    //ออกจากระบบ
    public void logout() {
        currentUser = null;
    }

    //login
    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("กรุณากรอกชื่อผู้ใช้");
        }
        currentUser = username.trim();
    }
}
