package CrowdFunding.model;

public class Project {
    private String projectId;
    private String name;
    private double targetAmount;
    private String deadline; // เก็บเป็น String (yyyy-MM-dd) ตามไฟล์ CSV
    private double currentAmount;

    /* Project.java
     สำหรับเก็บข้อมูลโครงการ ใช้ควบคู่กับไฟล์ projects.csv
     ประกอบด้วย projectId,name,targetAmount,deadline,currentAmount  
    */
    public Project(String projectId, String name, double targetAmount, String deadline, double currentAmount) {
        this.projectId = projectId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.currentAmount = currentAmount;
    }

    // Getter & Setter
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    // Helper method
    public double getProgressPercentage() {
        return (targetAmount == 0) ? 0 : (currentAmount / targetAmount) * 100;
    }
}