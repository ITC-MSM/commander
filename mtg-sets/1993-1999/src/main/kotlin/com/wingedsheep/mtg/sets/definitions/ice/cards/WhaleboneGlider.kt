package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Whalebone Glider
 * {2}
 * Artifact
 *
 * {2}, {T}: Target creature with power 3 or less gains flying until end of turn.
 *
 * The keyword-granting artifact shape with a narrowed target: [Targets.CreatureWithPowerAtMost]
 * adds the power predicate on top of the creature filter, so only the target requirement differs
 * from Fyndhorn Bow.
 */
val WhaleboneGlider = card("Whalebone Glider") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}, {T}: Target creature with power 3 or less gains flying until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val t = target("target", Targets.CreatureWithPowerAtMost(3))
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "349"
        artist = "Amy Weber"
        flavorText = "\"It's no Ornithopter, but then I'm no Urza.\"\n—Arcum Dagsson, Soldevi Machinist"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b75adf0-9501-4776-a213-456c2b821070.jpg"
    }
}
