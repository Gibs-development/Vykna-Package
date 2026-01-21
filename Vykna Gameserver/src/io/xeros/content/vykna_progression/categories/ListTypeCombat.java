package io.xeros.content.vykna_progression.categories;

import io.xeros.content.vykna_progression.ProgressionEntry;
import io.xeros.content.vykna_progression.ProgressionListDefinition;
import io.xeros.content.vykna_progression.ProgressionListType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum ListTypeCombat {
    KBD_SLAYER(2000, "Black Dragon Slayer", "Defeat King Black Dragon 5 times.", "King Black Dragon",
            "kc:king black dragon", 5, 5, 1),
    KQ_CRUSHER(2001, "Kalphite Crusher", "Defeat Kalphite Queen 5 times.", "Kalphite Queen",
            "kc:kalphite queen", 5, 5, 2),
    GRAARDOR_BREAKER(2002, "Graardor Breaker", "Defeat General Graardor 10 times.", "General Graardor",
            "kc:general graardor", 10, 8, 3),
    TSUTSAROTH_TAMER(2003, "Tsutsaroth Tamer", "Defeat K'ril Tsutsaroth 10 times.", "K'ril Tsutsaroth",
            "kc:k'ril tsutsaroth", 10, 8, 4),
    KREEARRA_HUNTER(2004, "Kree'arra Hunter", "Defeat Kree'arra 10 times.", "Kree'arra",
            "kc:kree'arra", 10, 8, 1),
    ZILYANA_WARRIOR(2005, "Zilyana Warrior", "Defeat Commander Zilyana 10 times.", "Commander Zilyana",
            "kc:commander zilyana", 10, 8, 2),
    CORP_TITAN(2006, "Corp Titan", "Defeat Corporeal Beast 5 times.", "Corporeal Beast",
            "kc:corporeal beast", 5, 8, 3),
    KRAKEN_SMASHER(2007, "Kraken Smasher", "Defeat Kraken 10 times.", "Kraken",
            "kc:kraken", 10, 6, 4),
    CERBERUS_HUNTER(2008, "Hellhound Hunter", "Defeat Cerberus 10 times.", "Cerberus",
            "kc:cerberus", 10, 8, 1),
    ZULRAH_SLAYER(2009, "Zulrah Slayer", "Defeat Zulrah 10 times.", "Zulrah",
            "kc:zulrah", 10, 8, 2),
    VORKATH_VANQUISHER(2010, "Vorkath Vanquisher", "Defeat Vorkath 10 times.", "Vorkath",
            "kc:vorkath", 10, 8, 3),
    HYDRA_HUNTER(2011, "Hydra Hunter", "Defeat Alchemical Hydra 5 times.", "Alchemical Hydra",
            "kc:alchemical hydra", 5, 10, 4),
    NIGHTMARE_NEMESIS(2012, "Nightmare Nemesis", "Defeat The Nightmare 5 times.", "The Nightmare",
            "kc:the nightmare", 5, 10, 1),
    SARACHNIS_SLAYER(2013, "Sarachnis Slayer", "Defeat Sarachnis 10 times.", "Sarachnis",
            "kc:sarachnis", 10, 6, 2),
    GROTESQUE_GUARDIAN(2014, "Grotesque Guardian", "Defeat Grotesque Guardians 10 times.", "Grotesque Guardians",
            "kc:grotesque guardians", 10, 8, 3),
    DEMONIC_DESTROYER(2015, "Demonic Destroyer", "Defeat Demonic Gorillas 25 times.", "Demonic Gorilla",
            "kc:demonic gorilla", 25, 6, 4),
    SHAMAN_STOMPER(2016, "Shaman Stomper", "Defeat Lizardman Shamans 25 times.", "Lizardman Shaman",
            "kc:lizardman shaman", 25, 6, 1),
    BARRELCHEST_BUSTER(2017, "Barrelchest Buster", "Defeat Barrelchest 5 times.", "Barrelchest",
            "kc:barrelchest", 5, 5, 2),
    DAG_REX_HUNTER(2018, "Dagannoth Rex Hunter", "Defeat Dagannoth Rex 10 times.", "Dagannoth Rex",
            "kc:dagannoth rex", 10, 6, 3),
    DAG_PRIME_HUNTER(2019, "Dagannoth Prime Hunter", "Defeat Dagannoth Prime 10 times.", "Dagannoth Prime",
            "kc:dagannoth prime", 10, 6, 4),
    DAG_SUPREME_HUNTER(2020, "Dagannoth Supreme Hunter", "Defeat Dagannoth Supreme 10 times.", "Dagannoth Supreme",
            "kc:dagannoth supreme", 10, 6, 1),
    COX_CONQUEROR(2021, "Raids Conqueror", "Complete Chambers of Xeric 5 times.", "Chambers of Xeric",
            "kc:chambers of xeric", 5, 10, 2),
    TOB_CONQUEROR(2022, "Blood Theatre Conqueror", "Complete Theatre of Blood 5 times.", "Theatre of Blood",
            "kc:theatre of blood", 5, 10, 3),
    MIMIC_MENACE(2023, "Mimic Menace", "Defeat The Mimic 3 times.", "Mimic",
            "kc:the mimic", 3, 6, 4),
    HUNLLEF_HUNTER(2024, "Hunllef Hunter", "Defeat Crystalline Hunllef 5 times.", "Crystalline Hunllef",
            "kc:crystalline hunllef", 5, 8, 1),
    VOTE_BOSS_VICTOR(2025, "Vote Boss Victor", "Defeat Vote Boss 5 times.", "Vote Boss",
            "kc:vote boss", 5, 6, 2),
    SCORPIA_SCARER(2026, "Scorpia Scarer", "Defeat Scorpia 10 times.", "Scorpia",
            "kc:scorpia", 10, 6, 3),
    SKOTIZO_SLAYER(2027, "Skotizo Slayer", "Defeat Skotizo 5 times.", "Skotizo",
            "kc:skotizo", 5, 8, 4),
    OBOR_OBLITERATOR(2028, "Obor Obliterator", "Defeat Obor 5 times.", "Obor",
            "kc:obor", 5, 6, 1),
    BRYOPHYTA_BANE(2029, "Bryophyta Bane", "Defeat Bryophyta 5 times.", "Bryophyta",
            "kc:bryophyta", 5, 6, 2);

    private final int entryId;
    private final String name;
    private final String description;
    private final String subcategory;
    private final String requirementKey;
    private final int requirementTarget;
    private final int points;
    private final int spriteIndex;

    ListTypeCombat(int entryId, String name, String description, String subcategory,
                   String requirementKey, int requirementTarget, int points, int spriteIndex) {
        this.entryId = entryId;
        this.name = name;
        this.description = description;
        this.subcategory = subcategory;
        this.requirementKey = requirementKey;
        this.requirementTarget = requirementTarget;
        this.points = points;
        this.spriteIndex = spriteIndex;
    }

    public ProgressionEntry toEntry() {
        return new ProgressionEntry(entryId, name, description, ProgressionListType.COMBAT.getId(),
                subcategory, points, requirementKey, requirementTarget, spriteIndex);
    }

    public static ProgressionListDefinition getDefinition() {
        List<ProgressionEntry> entries = new ArrayList<>();
        Set<String> subcategories = new TreeSet<>();
        for (ListTypeCombat entry : values()) {
            entries.add(entry.toEntry());
            subcategories.add(entry.subcategory);
        }
        return new ProgressionListDefinition(
                ProgressionListType.COMBAT.getId(),
                ProgressionListType.COMBAT.getDisplayName(),
                new ArrayList<>(subcategories),
                entries
        );
    }
}
