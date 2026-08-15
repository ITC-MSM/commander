package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect

/**
 * Stalking Assassin
 * {1}{U}{B}
 * Creature — Human Assassin
 * 1/1
 * {3}{U}, {T}: Tap target creature.
 * {3}{B}, {T}: Destroy target tapped creature.
 */
val StalkingAssassin = card("Stalking Assassin") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Human Assassin"
    power = 1
    toughness = 1
    oracleText = "{3}{U}, {T}: Tap target creature.\n" +
        "{3}{B}, {T}: Destroy target tapped creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = TapUntapEffect(target = t, tap = true)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{B}"), Costs.Tap)
        val t = target("target", Targets.TappedCreature)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "277"
        artist = "Dana Knutson"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff8cc71f-3070-497f-908f-35aa13a8a857.jpg?1562946631"
    }
}
