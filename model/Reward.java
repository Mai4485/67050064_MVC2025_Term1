package CrowdFunding.model;
/**
 class สำหรับเก็บข้อมูล Reward Tier ของ Project
 * ใช้ควบคู่กับ reward_tiers.csv
    ประกอบด้วย
 *  - rewardName        
 *  - minAmount        
 *  - quotaOrRemaining 
 *  - projectId        
 */

//Constructor
public class Reward {
    private String rewardName;
    private double minAmount;
    private int quotaOrRemaining;
    private String projectId;

    public Reward(String rewardName, double minAmount, int quotaOrRemaining, String projectId) {
        this.rewardName = rewardName;
        this.minAmount = minAmount;
        this.quotaOrRemaining = quotaOrRemaining;
        this.projectId = projectId;
    }

    // Getter & Setter
    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public int getQuotaOrRemaining() {
        return quotaOrRemaining;
    }

    public void setQuotaOrRemaining(int quotaOrRemaining) {
        this.quotaOrRemaining = quotaOrRemaining;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
