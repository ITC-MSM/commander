package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mire Boa
 * {1}{G}
 * Creature — Snake
 * 2/1
 * Swampwalk
 * {G}: Regenerate this creature.
 */
val MireBoa = card("Mire Boa") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 2
    toughness = 1
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)\n" +
        "{G}: Regenerate this creature. (The next time this creature would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    keywords(Keyword.SWAMPWALK)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{G}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Greg Hildebrandt"
        flavorText = "Mire slime courses through its veins in place of blood. No sooner does it bleed than it opens its mouth to replace the loss."
        imageUri = "https://cards.scryfall.io/normal/front/1/9/190dffdc-9d85-430d-9aea-b75084104840.jpg"
    }
}
