package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skyshroud Troll
 * {2}{G}{G}
 * Creature — Troll Giant
 * 3/3
 * {1}{G}: Regenerate this creature.
 */
val SkyshroudTroll = card("Skyshroud Troll") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Troll Giant"
    power = 3
    toughness = 3
    oracleText = "{1}{G}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "257"
        artist = "Matthew D. Wilson"
        flavorText = "The elves and merfolk have nothing but bitterness for each other. The trolls, however, find them both rather tasty."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/925c488d-79db-47d1-b7be-851f31732026.jpg"
    }
}
