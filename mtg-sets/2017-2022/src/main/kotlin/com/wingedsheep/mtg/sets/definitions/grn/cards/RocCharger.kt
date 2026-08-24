package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Roc Charger
 * {2}{W}
 * Creature — Bird
 * 1/3
 * Flying
 * Whenever this creature attacks, target attacking creature without flying gains flying until end of turn.
 */
val RocCharger = card("Roc Charger") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    oracleText = "Flying\n" +
        "Whenever this creature attacks, target attacking creature without flying gains flying until end of turn."
    power = 1
    toughness = 3

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.Attacks
        val flyer = target(
            "target",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING).attacking()
                )
            )
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, flyer)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Titus Lunter"
        flavorText = "Rocs' innate fearlessness makes them ideal mounts for emergency response."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/4932c635-b729-4348-92de-2c1b207ea460.jpg?1783934195"
    }
}
