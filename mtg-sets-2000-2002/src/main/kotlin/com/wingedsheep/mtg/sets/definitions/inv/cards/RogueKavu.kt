package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.AttackPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rogue Kavu
 * {1}{R}
 * Creature — Kavu
 * 1/1
 * Whenever this creature attacks alone, it gets +2/+0 until end of turn.
 */
val RogueKavu = card("Rogue Kavu") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature attacks alone, it gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.attacks(requires = setOf(AttackPredicate.Alone))
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61e1a445-129d-4bb9-a8b0-3f55e3e0bc58.jpg?1562914814"
    }
}
