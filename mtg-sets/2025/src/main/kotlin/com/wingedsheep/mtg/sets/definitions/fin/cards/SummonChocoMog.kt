package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Summon: Choco/Mog
 * {2}{W}
 * Enchantment Creature — Saga Bird Moogle
 * 3/3
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)
 * I, II, III, IV — Stampede! — Other creatures you control get +1/+0 until end of turn.
 *
 * A "Summon Saga" (CR 714.1a): simultaneously a creature and a Saga. The saga machinery — lore
 * accrual (CR 714.3c), chapter triggers (CR 714.2b), and the final-chapter sacrifice (CR 714.4) —
 * runs while the permanent is a live 3/3 creature. No engine change is needed for the saga and
 * creature characteristics to co-exist (see CreatureSagaTest).
 */
val SummonChocoMog = card("Summon: Choco/Mog") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Saga Bird Moogle"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)\n" +
        "I, II, III, IV — Stampede! — Other creatures you control get +1/+0 until end of turn."
    power = 3
    toughness = 3

    // "Other creatures you control get +1/+0 until end of turn." Reused across all four chapters.
    val stampede = Patterns.Group.modifyStatsForAll(
        power = 1,
        toughness = 0,
        filter = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
    )

    sagaChapter(1) { effect = stampede }
    sagaChapter(2) { effect = stampede }
    sagaChapter(3) { effect = stampede }
    sagaChapter(4) { effect = stampede }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Tomohide Takano"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00546117-018a-4286-bc20-b5446c5be56f.jpg?1748705886"
    }
}
