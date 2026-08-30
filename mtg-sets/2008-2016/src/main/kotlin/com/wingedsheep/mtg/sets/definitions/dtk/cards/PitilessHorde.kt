package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pitiless Horde
 * {2}{B}
 * Creature — Orc Berserker
 * 5 / 3
 *
 * At the beginning of your upkeep, you lose 2 life.
 * Dash {2}{B}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * The drawback is unconditional — no "unless", no sacrifice — so it is one [Triggers.YourUpkeep]
 * trigger over [Effects.LoseLife] aimed at the controller. `dash` is a builder property rather than
 * a `Keyword` constant, and setting it is what adds the `KeywordAbility.Dash` the cast enumerator
 * reads; dashing is how you get the 5/3 body without ever reaching an upkeep with it around.
 */
val PitilessHorde = card("Pitiless Horde") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Orc Berserker"
    power = 5
    toughness = 3
    oracleText = "At the beginning of your upkeep, you lose 2 life.\n" +
        "Dash {2}{B}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.LoseLife(2, EffectTarget.Controller)
        description = "At the beginning of your upkeep, you lose 2 life."
    }

    dash = "{2}{B}{B}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "112"
        artist = "Viktor Titov"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86263073-b585-4744-829d-5725e6a06cf2.jpg?1783938595"
    }
}
