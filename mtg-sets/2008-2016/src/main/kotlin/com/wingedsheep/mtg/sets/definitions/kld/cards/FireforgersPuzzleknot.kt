package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fireforger's Puzzleknot
 * {2}
 * Artifact
 *
 * When this artifact enters, it deals 1 damage to any target.
 * {2}{R}, Sacrifice this artifact: It deals 1 damage to any target.
 *
 * The same one-damage effect twice, reached two ways: once for free on arrival and once for
 * {2}{R} plus the artifact itself. The activated half composes [Costs.Mana] with
 * [Costs.SacrificeSelf], so the sacrifice is part of paying the cost — it happens on activation,
 * not on resolution, and the damage is still dealt after the Puzzleknot is gone.
 */
val FireforgersPuzzleknot = card("Fireforger's Puzzleknot") {
    manaCost = "{2}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, it deals 1 damage to any target.\n" +
        "{2}{R}, Sacrifice this artifact: It deals 1 damage to any target."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "213"
        artist = "Zezhou Chen"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a1c11ca-c81b-451e-a767-68865827e06d.jpg?1783937156"
    }
}
