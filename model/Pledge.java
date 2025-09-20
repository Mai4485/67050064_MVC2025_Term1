package CrowdFunding.model;

public class Pledge {
    private String supporter;
    private String projectId;
    private String datetime; 
    private double amount;
    private String rewardName; 

    /* Pledge.java สำหรับเก็บข้อมูลการสนับสนุน ใช้ควบคู่กับไฟล์ pledges.csv
    ประกอบด้วย ชื่อผู้ใช้ที่สนับสนุน,projectId,datetime,amount,rewardName  
    */
    public Pledge(String supporter, String projectId, String datetime, double amount, String rewardName) {
        this.supporter = supporter;
        this.projectId = projectId;
        this.datetime = datetime;
        this.amount = amount;
        this.rewardName = rewardName;
    }

    // Getter & Setter
    public String getSupporter() {
        return supporter;
    }

    public void setSupporter(String supporter) {
        this.supporter = supporter;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }
}
