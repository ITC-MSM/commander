package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dreadmalkin — War of the Spark #87 (canonical printing)
 * {B}
 * Creature — Zombie Cat
 * 1/1
 * Menace (This creature can't be blocked except by two or more creatures.)
 * {2}{B}, Sacrifice another creature or planeswalker: Put two +1/+1 counters on this creature.
 *
 * [Costs.SacrificeAnother] is the "another" of the printed cost — it is the same sacrifice atom
 * as [Costs.Sacrifice] with `excludeSelf`, so the cat can never eat itself to grow. The fodder
 * filter is [GameObjectFilter.CreatureOrPlaneswalker], one Or-predicate rather than two costs.
 */
val Dreadmalkin = card("Dreadmalkin") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Cat"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "{2}{B}, Sacrifice another creature or planeswalker: Put two +1/+1 counters on this creature."
    power = 1
    toughness = 1

    keywords(Keyword.MENACE)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}"),
            Costs.SacrificeAnother(GameObjectFilter.CreatureOrPlaneswalker)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Aaron Miller"
        flavorText = "The feline Eternal felt Liliana's command, looked at her disdainfully, flicked its paw, and scampered away across the rooftops."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/7327cfac-a4a4-445e-9d04-51c1ca142140.jpg"
    }
}
