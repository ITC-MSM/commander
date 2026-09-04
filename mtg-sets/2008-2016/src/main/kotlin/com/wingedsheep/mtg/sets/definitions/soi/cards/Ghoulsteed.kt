package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghoulsteed (Shadows over Innistrad #113)
 * {4}{B}
 * Creature — Zombie Horse
 * 4 / 4
 *
 * {2}{B}, Discard two cards: Return this card from your graveyard to the battlefield tapped.
 *
 * Modeling notes:
 *  - [DrownyardTemple]'s recursion shape with a discard rider on the cost: the composite is
 *    mana + [Costs.Discard], and the return keeps the graveyard guard and the `tapped` axis.
 */
val Ghoulsteed = card("Ghoulsteed") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Horse"
    power = 4
    toughness = 4
    oracleText = "{2}{B}, Discard two cards: Return this card from your graveyard to the battlefield tapped."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Discard(count = 2))
        effect = Effects.PutOntoBattlefieldFromGraveyard(EffectTarget.Self, tapped = true)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "113"
        artist = "Jason Kang"
        flavorText = "It once served a cathar roadwatcher, patrolling the crossways between villages. Its hooves still carry it along the same path."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed5ba8dd-d2cd-4ee6-bdb4-390968f1ff54.jpg?1783937774"
    }
}
