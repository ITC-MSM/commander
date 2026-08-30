package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lightning Berserker
 * {R}
 * Creature — Human Berserker
 * 1 / 1
 *
 * {R}: This creature gets +1/+0 until end of turn.
 * Dash {R} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * Firebreathing on a Berserker: the pump is a plain mana-cost activated ability over
 * [Effects.ModifyStats] aimed at [EffectTarget.Self], repeatable because nothing restricts it.
 * `dash` is a builder property rather than a `Keyword` constant, and setting it is what adds the
 * `KeywordAbility.Dash` the cast enumerator reads — a dashed Berserker gets haste, so every red
 * mana left over that turn is another point of damage.
 */
val LightningBerserker = card("Lightning Berserker") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Berserker"
    power = 1
    toughness = 1
    oracleText = "{R}: This creature gets +1/+0 until end of turn.\n" +
        "Dash {R} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: This creature gets +1/+0 until end of turn."
    }

    dash = "{R}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Joseph Meehan"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/3285cb6f-a9c0-4195-b6b2-3f33a16eaa01.jpg?1783938588"
    }
}
