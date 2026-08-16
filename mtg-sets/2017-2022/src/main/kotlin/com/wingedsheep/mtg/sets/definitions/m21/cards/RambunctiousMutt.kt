package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Rambunctious Mutt
 * {3}{W}{W}
 * Creature — Dog
 * 3/4
 * When this creature enters, destroy target artifact or enchantment an opponent controls.
 *
 * Not optional and not "up to one" — the trigger needs a legal target to go on the stack at all.
 */
val RambunctiousMutt = card("Rambunctious Mutt") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, destroy target artifact or enchantment an opponent controls."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter.ArtifactOrEnchantment.opponentControls())
        )
        effect = Effects.Destroy(t)
        description = "When this creature enters, destroy target artifact or enchantment an opponent controls."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Campbell White"
        flavorText = "Emphatic words with powerful gestures. Clearly this was playtime."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f602ecc-c264-4f3e-adeb-d0186668653e.jpg?1783930737"
    }
}
