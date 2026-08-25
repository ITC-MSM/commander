package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Metallurgeon
 * {1}{W}
 * Artifact Creature — Human Artificer
 * 1 / 2
 * {W}, {T}: Regenerate target artifact.
 *
 * A repeatable regeneration on a body. The cost is the usual [Costs.Composite] of a coloured
 * [Costs.Mana] atom and [Costs.Tap]; the named target is [Targets.Artifact] (a battlefield
 * `TargetObject` over `TargetFilter.Artifact`) and the effect is the plain [RegenerateEffect]
 * pointed at that bound variable rather than at the source.
 */
val Metallurgeon = card("Metallurgeon") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Human Artificer"
    power = 1
    toughness = 2
    oracleText = "{W}, {T}: Regenerate target artifact."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val t = target("target", Targets.Artifact)
        effect = RegenerateEffect(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Warren Mahy"
        flavorText = "\"By the time I got there, the heart had stopped. Fortunately, I was able to replace it with something better.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ad9f9d5-62c1-4dae-a2c8-5552210a70a4.jpg"
    }
}
