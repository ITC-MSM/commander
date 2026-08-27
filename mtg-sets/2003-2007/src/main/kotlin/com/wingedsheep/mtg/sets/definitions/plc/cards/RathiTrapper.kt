package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rathi Trapper
 * {1}{B}
 * Creature — Human Rebel Rogue
 * 1/2
 * {B}, {T}: Tap target creature.
 */
val RathiTrapper = card("Rathi Trapper") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rebel Rogue"
    power = 1
    toughness = 2
    oracleText = "{B}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.Tap(t)
        description = "{B}, {T}: Tap target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Pete Venters"
        flavorText = "Tangling vines, fetid murk, paralyzing poisons, and crawling dead. The swamp is nature's trap waiting to be exploited by unnatural minds."
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2968e01-2d04-4591-98de-24688bb087df.jpg"
    }
}
