package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Erhnam Djinn
 * {3}{G}
 * Creature — Djinn
 * 4/5
 * At the beginning of your upkeep, target non-Wall creature an opponent controls
 * gains forestwalk until your next upkeep.
 */
val ErhnamDjinn = card("Erhnam Djinn") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Djinn"
    power = 4
    toughness = 5
    oracleText = "At the beginning of your upkeep, target non-Wall creature an opponent controls gains forestwalk until your next upkeep. (It can't be blocked as long as defending player controls a Forest.)"

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        val creature = target(
            "non-Wall creature an opponent controls",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.opponentControls().notSubtype(Subtype("Wall"))))
        )
        effect = Effects.GrantKeyword(Keyword.FORESTWALK, creature, Duration.UntilYourNextUpkeep)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "48"
        artist = "Ken Meyer, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42bc0c3f-0a52-4bdc-83da-6484bf3102f3.jpg?1562907201"
    }
}
