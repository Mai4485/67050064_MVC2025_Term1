package CrowdFunding.model.repository;

import CrowdFunding.model.Reward;
import java.io.*;
import java.util.*;

public class RewardRepository {
    private final String path;
    private final List<Reward> all = new ArrayList<>();

    public RewardRepository(String path) {
        this.path = path;
    }

    public List<Reward> all() {
        return all;
    }

    public List<Reward> byProject(String projectId) {
        List<Reward> out = new ArrayList<>();
        for (Reward r : all) {
            if (r.getProjectId().equals(projectId))
                out.add(r);
        }
        return out;
    }

    public Optional<Reward> byProjectAndName(String projectId, String rewardName) {
        for (Reward r : all) {
            if (r.getProjectId().equals(projectId)
                    && r.getRewardName().equalsIgnoreCase(rewardName)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    // ---------------------------
    // โหลดข้อมูลจาก CSV (ไม่ใช้ NIO)
    // ---------------------------
    public void load() throws IOException {
        all.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                } // skip header
                String[] t = line.split(",", -1);
                all.add(new Reward(
                        t[0], // reward_name
                        Double.parseDouble(t[1]), // min_amount
                        Integer.parseInt(t[2]), // quota_or_remaining
                        t[3] // project_id
                ));
            }
        }
    }

    // ---------------------------
    // เขียนข้อมูลกลับ CSV
    // ---------------------------
    public void save() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("reward_name,min_amount,quota_or_remaining,project_id");
            for (Reward r : all) {
                pw.println(String.join(",",
                        esc(r.getRewardName()),
                        String.valueOf((long) r.getMinAmount()),
                        String.valueOf(r.getQuotaOrRemaining()),
                        r.getProjectId()));
            }
        }
    }

    private String esc(String s) {
        return s == null ? "" : s.replace(",", " ");
    }
}
