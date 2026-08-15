package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Charging Troll
 * {2}{G}{W}
 * Creature — Troll
 * 3/3
 * Vigilance
 * {G}: Regenerate this creature.
 */
val ChargingTroll = card("Charging Troll") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Troll"
    power = 3
    toughness = 3
    oracleText = "Vigilance\n{G}: Regenerate this creature."

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "239"
        artist = "Dave Dorman"
        flavorText = "They stop for nothing, not even the end of a battle."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58956099-6b97-4c7b-ab23-9f9b4d50ef95.jpg?1562913006"
    }
}
