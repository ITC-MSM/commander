package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Neurok Replica — Scars of Mirrodin #186
 * {3} · Artifact Creature — Wizard · 1 / 4
 *
 * {1}{U}, Sacrifice this creature: Return target creature to its owner's hand.
 *
 * The Replica can bounce itself: the sacrifice is a cost, so it is already in the graveyard when
 * the ability resolves and the target must be some other creature (CR 601.2h).
 */
val NeurokReplica = card("Neurok Replica") {
    manaCost = "{3}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Wizard"
    power = 1
    toughness = 4
    oracleText = "{1}{U}, Sacrifice this creature: Return target creature to its owner's hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Creature)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "All the curiosity of the Neurok with only a trace of their duplicity."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e32d5a8-0916-4728-9cb2-3903262bf873.jpg?1783941701"
    }
}
