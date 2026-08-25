package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flowstone Channeler
 * {2}{R}
 * Creature — Human Spellshaper
 * 2/2
 * {1}{R}, {T}, Discard a card: Target creature gets +1/-1 and gains haste until end of turn.
 *
 * The Spellshaper shape: a mana-plus-tap-plus-discard cost buying the spell the card is named for
 * (Flowstone Slide's flowstone pump, here the Tempest-block +1/-1-and-haste). The two halves are
 * one composite on a single target slot.
 */
val FlowstoneChanneler = card("Flowstone Channeler") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Spellshaper"
    power = 2
    toughness = 2
    oracleText = "{1}{R}, {T}, Discard a card: Target creature gets +1/-1 and gains haste until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap, Costs.DiscardCard)
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, -1, creature),
            Effects.GrantKeyword(Keyword.HASTE, creature),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Alan Pollack"
        flavorText = "With the evincars gone, flowstone became erratic and wild, a source of power for mages more desperate than wise."
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f10fa1c-2c7b-49ba-87b6-3b652b65704d.jpg"
    }
}
