package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Servant of Volrath
 * {2}{B}
 * Creature — Minion
 * 3/3
 * When this creature leaves the battlefield, sacrifice a creature.
 */
val ServantOfVolrath = card("Servant of Volrath") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Minion"
    power = 3
    toughness = 3
    oracleText = "When this creature leaves the battlefield, sacrifice a creature."

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
        description = "When this creature leaves the battlefield, sacrifice a creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Brian Snõddy"
        flavorText = "\"Volrath is a harsh master. My reflection is a constant reminder of that.\"\n" +
            "—Greven *il*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/691afabb-f266-45fd-b5a3-577be4f10f86.jpg"
    }
}
