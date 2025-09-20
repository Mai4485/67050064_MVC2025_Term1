package CrowdFunding.controller;

import CrowdFunding.model.Project;
import CrowdFunding.model.Reward;
import CrowdFunding.model.repository.DataStore;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

/* PledgeController ไว้จัดการการสนับสนุน
  ตรวจเงื่อนไขตามกติกา,อัปเดตยอด,บันทึกผลสำเร็จและปฏิเสธลง csv
 */
public class PledgeController {
    private final DataStore store;
    private static final String FAIL_LOG = "CrowdFunding/model/database/pledge_failures.csv";

    public PledgeController(DataStore store) {
        this.store = store;
    }

    public void submitPledge(String supporter, String projectId, double amount, String rewardName) throws Exception {
        //หาProject
        Project p = store.projects.all().stream()
                .filter(x -> x.getProjectId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> {
                    logFailure(supporter, projectId, amount, rewardName, "project_not_found");
                    return new IllegalArgumentException("Project not found");
                });

        //ตรวจ deadline
        if (LocalDate.parse(p.getDeadline()).isBefore(LocalDate.now())) {
            logFailure(supporter, projectId, amount, rewardName, "deadline_passed");
            throw new IllegalStateException("Project deadline passed");
        }

        Reward tier = null;
        if (rewardName != null && !rewardName.trim().isEmpty()) {
            String wanted = rewardName.trim();
            tier = store.rewards.all().stream()
                    .filter(r -> r.getProjectId().equals(projectId)
                            && r.getRewardName().equalsIgnoreCase(wanted))
                    .findFirst()
                    .orElseThrow(() -> {
                        logFailure(supporter, projectId, amount, rewardName, "reward_not_found");
                        return new IllegalStateException("Reward not found");
                    });

            if (amount < tier.getMinAmount()) {
                logFailure(supporter, projectId, amount, rewardName, "below_min");
                throw new IllegalStateException("Amount is below reward minimum");
            }

            if (tier.getQuotaOrRemaining() <= 0) {
                logFailure(supporter, projectId, amount, rewardName, "out_of_quota");
                throw new IllegalStateException("Reward out of stock");
            }
        }

        //บันทึก pledge
        store.pledges.add(new CrowdFunding.model.Pledge(
                supporter,
                projectId,
                LocalDateTime.now().toString(),
                amount,
                rewardName == null ? "" : rewardName
        ));

        //อัปเดตยอดใน Project
        p.setCurrentAmount(p.getCurrentAmount() + amount);

        //ลด quota ของ reward 
        if (tier != null) {
            tier.setQuotaOrRemaining(tier.getQuotaOrRemaining() - 1);
        }

        //Save กลับลงไฟล์
        store.saveAll();
    }

    //helper สำหรับ log failures
    private void logFailure(String supporter, String projectId, double amount, String rewardName, String reason) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FAIL_LOG, true))) {
            String when = LocalDateTime.now().toString();
            pw.printf("%s,%s,%s,%.2f,%s,%s%n",
                    when,
                    supporter == null ? "" : supporter.replace(",", " "),
                    projectId == null ? "" : projectId,
                    amount,
                    rewardName == null ? "" : rewardName.replace(",", " "),
                    reason);
        } catch (Exception ignore) {
        }
    }
}

