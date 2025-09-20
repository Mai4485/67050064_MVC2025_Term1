package CrowdFunding.model.repository;

import java.io.File;

public class DataStore {
    public final ProjectRepository projects;
    public final RewardRepository  rewards;
    public final PledgeRepository  pledges;

    //basePath 
    public DataStore(String basePath) throws Exception {
        File base = new File(basePath);

        String projectsPath = new File(base, "projects.csv").getPath();
        String rewardsPath  = new File(base, "reward_tiers.csv").getPath();
        String pledgesPath  = new File(base, "pledges.csv").getPath();

       
        projects = new ProjectRepository(projectsPath);
        rewards  = new RewardRepository(rewardsPath);
        pledges  = new PledgeRepository(pledgesPath);

        projects.load();
        rewards.load();
        pledges.load();
    }

    public void saveAll() throws Exception {
        projects.save();
        rewards.save();
        pledges.save();
    }
}
