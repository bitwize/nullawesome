package bitwize.nullawesome;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class StageClearUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private boolean hasRun = false;
    private int trinkCount;
    public StageClearUpdateAgent() {
        repo = EntityRepository.get();
    }
    private int countCollectedTrinkets(StageInfo info) {
        trinkCount = 0;
        repo.processEntitiesWithComponent
            (CollectibleInfo.class,
             (anEid) -> {
                CollectibleInfo ci =
                    (CollectibleInfo)repo.getComponent(anEid, CollectibleInfo.class);
                if(ci.type == CollectibleType.TRINKET &&
                   ci.state == CollectibleState.COLLECTED) {
                    trinkCount++;
                }
            });
        return trinkCount;
    }
    public void update(long time) {
        int stageEid;
        StageInfo info;
        if(hasRun) return;
        stageEid = repo.findEntityWithComponent(StageInfo.class);
        if(stageEid == EntityRepository.NO_ENTITY) {
            return;
        }
        info = (StageInfo)repo.getComponent(stageEid, StageInfo.class);
        if(info.clear) {
            try {
                JSONObject saveData = ContentRepository.get().getSaveData();
                JSONObject thisStageData = saveData.optJSONObject(info.name);
                if(thisStageData != null) {
                    int prevTrinketCount = thisStageData.getInt("trinket_count");
                    int currentTrinketCount = countCollectedTrinkets(info);
                    thisStageData.put("clear", true);
                    if(currentTrinketCount > prevTrinketCount) {
                        thisStageData.put("trinket_count", currentTrinketCount);
                    }
                } else {
                    thisStageData = new JSONObject();
                    thisStageData.put("trinket_count", countCollectedTrinkets(info));
                    saveData.put(info.name, thisStageData);
                }
                ContentRepository.get().saveSaveData();
            } catch(JSONException e) {
                throw new RuntimeException(e);
            }
            hasRun = true;
        }
    }
}
