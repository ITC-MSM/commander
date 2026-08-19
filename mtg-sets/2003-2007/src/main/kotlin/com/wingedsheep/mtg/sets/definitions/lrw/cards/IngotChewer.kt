package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ingot Chewer
 * {4}{R}
 * Creature — Elemental
 * 3/3
 * When this creature enters, destroy target artifact.
 * Evoke {R}
 */
val IngotChewer = card("Ingot Chewer") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, destroy target artifact.\n" +
        "Evoke {R} (You may cast this spell for its evoke cost. If you do, it's sacrificed when " +
        "it enters.)"

    evoke = "{R}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val artifact = target("target artifact", Targets.Artifact)
        effect = Effects.Destroy(artifact)
        description = "destroy target artifact."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "Kev Walker"
        flavorText = "Elementals are ideas given form. This one is the idea of \"smashitude.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9ceb9ae9-d153-446f-8835-1c53672fc9ca.jpg?1783942873"
    }
}
