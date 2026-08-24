package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ranger en-Vec
 * {1}{G}{W}
 * Creature — Human Soldier Archer Ranger
 * 2/2
 * First strike
 * {G}: Regenerate this creature.
 */
val RangerEnVec = card("Ranger en-Vec") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Human Soldier Archer Ranger"
    power = 2
    toughness = 2
    oracleText = "First strike\n" +
        "{G}: Regenerate this creature."

    keywords(Keyword.FIRST_STRIKE)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{G}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "268"
        artist = "Randy Elliott"
        flavorText = "\"The path of least resistance will seldom lead you beyond your doorstep.\"\n" +
            "—Oracle *en*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a89e82c-7206-4d74-95c6-ad3627e5a9ce.jpg"
    }
}
