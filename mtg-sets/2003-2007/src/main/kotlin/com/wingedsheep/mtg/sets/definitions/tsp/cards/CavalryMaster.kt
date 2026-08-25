package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Cavalry Master
 * {2}{W}{W}
 * Creature — Human Knight
 * 3/3
 * Flanking (Whenever a creature without flanking blocks this creature, the blocking creature
 * gets -1/-1 until end of turn.)
 * Other creatures you control with flanking have flanking. (Each instance of flanking triggers
 * separately.)
 *
 * A lord that grants the keyword it selects on: the filter already requires flanking, so the
 * grant only ever lands a *second* instance. That works because flanking's triggered ability is
 * derived from the projected keyword rather than authored — `TriggerAbilityResolver` supplies one
 * trigger per instance, which is exactly what the reminder text promises.
 */
val CavalryMaster = card("Cavalry Master") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 3
    oracleText = "Flanking (Whenever a creature without flanking blocks this creature, the blocking creature gets -1/-1 until end of turn.)\n" +
        "Other creatures you control with flanking have flanking. (Each instance of flanking triggers separately.)"

    keywords(Keyword.FLANKING)

    staticAbility {
        ability = GrantKeyword(
            Keyword.FLANKING,
            GroupFilter(
                GameObjectFilter.Creature.withKeyword(Keyword.FLANKING).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Thomas M. Baxa"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7b19194-87bf-432c-8d34-91dd9520cbd2.jpg"
    }
}
