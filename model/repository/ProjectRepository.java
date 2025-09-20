package CrowdFunding.model.repository;

import CrowdFunding.model.Project;
import java.io.*;
import java.util.*;

public class ProjectRepository {
    private final String path;
    private final List<Project> all = new ArrayList<>();

    public ProjectRepository(String path) {
        this.path = path;
    }

    public List<Project> all() {
        return all;
    }

    public Optional<Project> byId(String id) {
        return all.stream().filter(p -> p.getProjectId().equals(id)).findFirst();
    }

    // โหลดข้อมูลจาก CSV

    public void load() throws IOException {
        all.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { // ข้าม header
                    firstLine = false;
                    continue;
                }
                String[] t = line.split(",", -1);
                all.add(new Project(
                        t[0], t[1],
                        Double.parseDouble(t[2]),
                        t[3],
                        Double.parseDouble(t[4])));
            }
        }
    }

    private Project mapToProject(String line) {
        String[] t = line.split(",", -1);
        return new Project(
                t[0],
                t[1],
                Double.parseDouble(t[2]),
                t[3],
                Double.parseDouble(t[4]));
    }

    // เขียนข้อมูลกลับ CSV

    public void save() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("project_id,name,target_amount,deadline,current_amount");
            for (Project p : all) {
                pw.println(String.join(",",
                        p.getProjectId(),
                        p.getName().replace(",", " "),
                        String.valueOf((long) p.getTargetAmount()),
                        p.getDeadline(),
                        String.valueOf((long) p.getCurrentAmount())));
            }
        }
    }

}
