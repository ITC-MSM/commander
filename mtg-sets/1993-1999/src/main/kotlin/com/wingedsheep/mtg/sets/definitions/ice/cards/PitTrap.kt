package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Pit Trap
 * {2}
 * Artifact
 *
 * {2}, {T}, Sacrifice this artifact: Destroy target attacking creature without flying. It can't be regenerated.
 *
 * [Effects.Destroy] with `noRegenerate = true` is the facade that composes the can't-be-regenerated
 * marker ahead of the destroy, so the rider is never hand-rolled; the target filter is the same
 * "attacking creature without flying" shape Quicksand uses.
 */
val PitTrap = card("Pit Trap") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}, {T}, Sacrifice this artifact: Destroy target attacking creature without flying. It can't be regenerated."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING).attacking())
        )
        effect = Effects.Destroy(t, noRegenerate = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "333"
        artist = "Anson Maddocks"
        flavorText = "\"These traps are truly a symbol of great cruelty and sinister cunning.\"\n—Sorine Relicbane, Soldevi Heretic"
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c588fe7f-945d-4459-904c-67442f88b4e1.jpg"
    }
}
