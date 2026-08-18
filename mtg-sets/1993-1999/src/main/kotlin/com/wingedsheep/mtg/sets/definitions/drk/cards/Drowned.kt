package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Drowned
 * {1}{U}
 * Creature — Zombie
 * 1/1
 * {B}: Regenerate this creature.
 */
val Drowned = card("Drowned") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 1
    oracleText = "{B}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Quinton Hoover"
        flavorText = "We asked Captain Soll what became of the Serafina, but all he said was, \"Ships that go down shouldn't come back up.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/5/951b6c10-cbba-44b6-aae2-2c386b7ebacb.jpg?1783947945"
    }
}
