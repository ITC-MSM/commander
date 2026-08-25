package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jungle Delver
 * {G}
 * Creature — Merfolk Warrior
 * 1/1
 *
 * {3}{G}: Put a +1/+1 counter on this creature.
 */
val JungleDelver = card("Jungle Delver") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Warrior"
    oracleText = "{3}{G}: Put a +1/+1 counter on this creature."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Kieran Yanner"
        flavorText = "\"There is no power too great to be used in the defense of our ancestral lands.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be0e3547-d8cb-4b68-a396-8c8fbc3b2b1c.jpg"
    }
}
