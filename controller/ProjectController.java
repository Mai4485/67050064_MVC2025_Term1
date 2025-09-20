package CrowdFunding.controller;
import CrowdFunding.model.repository.DataStore;
import CrowdFunding.model.Project;
import CrowdFunding.model.Reward;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/* Controller สำหรับจัดการโครงการ ทำหน้าที่เชื่อมระหว่าง View กับ Model
  ทำหน้าที่ดึงรายการโครงการทั้งหมด,ค้นหา,เรียงลำดับตามเงื่อนไข,ดึงReward
*/
public class ProjectController {
    private final DataStore store;

    public ProjectController(DataStore store) { this.store = store; }

    public List<Project> listProjects() {
        return store.projects.all();
    }

    public List<Project> search(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        return store.projects.all().stream()
                .filter(p -> p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    //เรียงตามล่าสุด
    public List<Project> sortByLatest() {
        return store.projects.all().stream()
                .sorted(Comparator.comparing(Project::getProjectId).reversed())
                .collect(Collectors.toList());
    }

    //เรียงตามใกล้ปิด
    public List<Project> sortByEndingSoon() {
        return store.projects.all().stream()
                .sorted(Comparator.comparing(p -> LocalDate.parse(p.getDeadline())))
                .collect(Collectors.toList());
    }

    //เรียงตามการสนับสนุกมากสุด
    public List<Project> sortByTopFunded() {
        return store.projects.all().stream()
                .sorted(Comparator.comparing(Project::getCurrentAmount).reversed())
                .collect(Collectors.toList());
    }

    //หาโครงการตาม projectId
    public Project get(String projectId) {
        return store.projects.all().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst().orElse(null);
    }

    //ดึง reward ทั้งหมดของโครงการนั้น ๆ
    public List<Reward> rewardsOf(String projectId) {
        return store.rewards.all().stream()
                .filter(r -> r.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }
}
