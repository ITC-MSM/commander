package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ruinous Gremlin
 * {R}
 * Creature — Gremlin
 * 1/1
 * {2}{R}, Sacrifice this creature: Destroy target artifact.
 *
 * A plain activated ability: the mana and the self-sacrifice are two atoms of one composite cost,
 * and [Effects.Destroy] is the destruction-flavoured move to the graveyard (so indestructible and
 * regeneration are honoured).
 */
val RuinousGremlin = card("Ruinous Gremlin") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Gremlin"
    oracleText = "{2}{R}, Sacrifice this creature: Destroy target artifact."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}"), Costs.SacrificeSelf)
        val t = target("target", TargetPermanent(filter = TargetFilter.Artifact))
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Steve Prescott"
        flavorText = "City officials once sent twenty gleaming automatons to exterminate a nest of gremlins. They soon saw the error of their ways."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88067bc3-6ec9-4a96-8077-817c57e032d0.jpg?1783937191"
    }
}
