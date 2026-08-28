package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Norwood Warrior
 * {2}{G}
 * Creature — Elf Warrior
 * 2 / 2
 *
 * Whenever this creature becomes blocked, it gets +1/+1 until end of turn.
 */
val NorwoodWarrior = card("Norwood Warrior") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "Whenever this creature becomes blocked, it gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Rebecca Guay"
        flavorText = "\"The woods hold peace for those who desire it, but those who seek battle will find me.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f7ddf0a-1081-4ae0-ab91-b052153bab4c.jpg"
    }
}
