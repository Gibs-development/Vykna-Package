package io.xeros.content.vykna_teleports.model;

import java.util.Collections;
import java.util.List;

/**
 * Data-only requirements. Keep logic (checking player stats/quests) out of here.
 *
 * skillId should match your server's skill constants/indexing (typically 0..20).
 */
public final class TeleportRequirement {

    public static final class SkillReq {
        private final int skillId;
        private final int level;

        public SkillReq(int skillId, int level) {
            this.skillId = skillId;
            this.level = level;
        }

        public int getSkillId() { return skillId; }
        public int getLevel() { return level; }
    }

    private final Integer combatLevel; // optional
    private final List<SkillReq> skills; // optional, can be empty

    private TeleportRequirement(Integer combatLevel, List<SkillReq> skills) {
        this.combatLevel = combatLevel;
        this.skills = skills == null ? Collections.emptyList() : Collections.unmodifiableList(skills);
    }

    public static TeleportRequirement none() {
        return new TeleportRequirement(null, Collections.emptyList());
    }

    public static TeleportRequirement combatLevel(int combatLevel) {
        return new TeleportRequirement(combatLevel, Collections.emptyList());
    }

    public static TeleportRequirement of(Integer combatLevel, List<SkillReq> skills) {
        return new TeleportRequirement(combatLevel, skills);
    }

    public Integer getCombatLevel() { return combatLevel; }
    public List<SkillReq> getSkills() { return skills; }
}
