package CrowdFunding.model.repository;

import CrowdFunding.model.Pledge;
import java.io.*;
import java.util.*;

public class PledgeRepository {
    private final String path;
    private final List<Pledge> all = new ArrayList<>();

    public PledgeRepository(String path) {
        this.path = path;
    }

    public List<Pledge> all() {
        return all;
    }

    public void add(Pledge p) {
        all.add(p);
    }

    public List<Pledge> byProject(String projectId) {
        List<Pledge> out = new ArrayList<>();
        for (Pledge p : all) {
            if (p.getProjectId().equals(projectId))
                out.add(p);
        }
        return out;
    }

    // โหลดข้อมูลจาก CSV

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
                all.add(new Pledge(
                        t[0], // supporter
                        t[1], // project_id
                        t[2], // datetime
                        Double.parseDouble(t[3]), // amount
                        t[4] // reward_name (อาจว่าง)
                ));
            }
        }
    }

    // เขียนข้อมูลกลับ CSV

    public void save() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("supporter,project_id,datetime,amount,reward_name");
            for (Pledge p : all) {
                pw.println(String.join(",",
                        p.getSupporter(),
                        p.getProjectId(),
                        p.getDatetime(),
                        String.valueOf((long) p.getAmount()),
                        esc(p.getRewardName())));
            }
        }
    }

    private String esc(String s) {
        return s == null ? "" : s.replace(",", " ");
    }
}
