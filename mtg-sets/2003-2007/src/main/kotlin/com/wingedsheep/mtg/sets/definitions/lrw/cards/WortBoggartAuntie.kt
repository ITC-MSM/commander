package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Wort, Boggart Auntie
 * {2}{B}{R}
 * Legendary Creature — Goblin Shaman
 * 3/3
 * Fear
 * At the beginning of your upkeep, you may return target Goblin card from your graveyard to your hand.
 *
 * The target is a *card* in the graveyard, so the filter is any owned Goblin card — Kindred cards
 * such as Tarfire come back too, not only Goblin creatures.
 */
val WortBoggartAuntie = card("Wort, Boggart Auntie") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Goblin Shaman"
    power = 3
    toughness = 3
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or black " +
        "creatures.)\nAt the beginning of your upkeep, you may return target Goblin card from your " +
        "graveyard to your hand."

    keywords(Keyword.FEAR)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        optional = true
        val goblinCard = target(
            "target Goblin card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Any.withSubtype(Subtype.GOBLIN).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.ReturnToHand(goblinCard)
        description = "At the beginning of your upkeep, you may return target Goblin card from " +
            "your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "252"
        artist = "Larry MacDougall"
        flavorText = "Auntie always knows which berries to lick, which kithkin to trick, and what to do when either goes wrong."
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a08a1377-dede-47c8-8447-c9df125f3b14.jpg?1783942852"
    }
}
