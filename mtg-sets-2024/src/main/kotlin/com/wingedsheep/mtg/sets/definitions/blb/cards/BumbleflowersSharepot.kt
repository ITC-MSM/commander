package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bumbleflower's Sharepot
 * {2}
 * Artifact
 *
 * When this artifact enters, create a Food token.
 * {5}, {T}, Sacrifice this artifact: Destroy target nonland permanent.
 * Activate only as a sorcery.
 */
val BumbleflowersSharepot = card("Bumbleflower's Sharepot") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "When this artifact enters, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")\n{5}, {T}, Sacrifice this artifact: Destroy target nonland permanent. Activate only as a sorcery."

    // ETB: Create a Food token
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    // {5}, {T}, Sacrifice: Destroy target nonland permanent. Sorcery speed.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        val permanent = target("nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Destroy(permanent)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "244"
        artist = "J.P. Targete"
        flavorText = "\"I may have overestimated the portions.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f0affd5-5dcd-4dd1-a694-37a9aedf4084.jpg?1721427267"
    }
}
