package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tuknir Deathlock
 * {R}{R}{G}{G}
 * Legendary Creature — Human Wizard
 * 2/2
 *
 * Flying
 * {R}{G}, {T}: Target creature gets +2/+2 until end of turn.
 */
val TuknirDeathlock = card("Tuknir Deathlock") {
    manaCost = "{R}{R}{G}{G}"
    colorIdentity = "GR"
    typeLine = "Legendary Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "Flying\n{R}{G}, {T}: Target creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}{G}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "267"
        artist = "Liz Danforth"
        flavorText = "An explorer of the Æther, Tuknir often discovers himself in the most unusual physical " +
            "realms."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9dfbcb4d-a9ae-4d76-8dde-7312fbad56b0.jpg?1783948030"
    }
}
