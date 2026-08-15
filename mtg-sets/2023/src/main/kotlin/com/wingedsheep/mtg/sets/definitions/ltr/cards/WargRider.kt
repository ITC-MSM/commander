package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Warg Rider
 * {4}{B}
 * Creature — Orc Warrior
 * 4/3
 *
 * Menace
 * Other Orcs and Goblins you control have menace.
 * At the beginning of combat on your turn, amass Orcs 2.
 */
val WargRider = card("Warg Rider") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Orc Warrior"
    power = 4
    toughness = 3
    oracleText = "Menace\n" +
        "Other Orcs and Goblins you control have menace.\n" +
        "At the beginning of combat on your turn, amass Orcs 2. (Put two +1/+1 counters on an Army you " +
        "control. It's also an Orc. If you don't control an Army, create a 0/0 black Orc Army creature token first.)"

    keywords(Keyword.MENACE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.MENACE,
            filter = GroupFilter(
                GameObjectFilter.Creature.withAnySubtype("Orc", "Goblin").youControl(),
                excludeSelf = true
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = Effects.Amass(2, "Orc")
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "826"
        artist = "Pascal Quidault"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18cc2bc6-12bb-4795-b2b2-9d414823b773.jpg?1719684212"
    }
}
