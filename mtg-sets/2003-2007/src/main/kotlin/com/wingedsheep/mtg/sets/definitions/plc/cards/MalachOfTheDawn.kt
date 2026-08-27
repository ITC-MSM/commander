package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Malach of the Dawn
 * {2}{W}{W}
 * Creature — Angel
 * 2/4
 * Flying
 * {W}{W}{W}: Regenerate this creature.
 */
val MalachOfTheDawn = card("Malach of the Dawn") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "{W}{W}{W}: Regenerate this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{W}{W}{W}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{W}{W}{W}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Steve Prescott"
        flavorText = "\"The sun rises, but the world still feels dark. Pray for the arrival of the malachim—they'll bring Dawn to the world and to our hearts.\"\n—Sister Betje, *Miracles of the Saints*"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f5df373-5dad-4e33-9e2b-351f1f4bdde4.jpg"
    }
}
