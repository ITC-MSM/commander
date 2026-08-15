package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Prohibit
 * {1}{U}
 * Instant
 * Kicker {2}
 * Counter target spell if its mana value is 2 or less. If this spell was kicked,
 * counter that spell if its mana value is 4 or less instead.
 */
val Prohibit = card("Prohibit") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Kicker {2} (You may pay an additional {2} as you cast this spell.)\n" +
        "Counter target spell if its mana value is 2 or less. If this spell was kicked, " +
        "counter that spell if its mana value is 4 or less instead."

    keywordAbility(KeywordAbility.kicker("{2}"))

    spell {
        target = Targets.Spell
        // If kicked, counter when the target's mana value is 4 or less; otherwise 2 or less.
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = ConditionalEffect(
                condition = Conditions.TargetSpellManaValueAtMost(DynamicAmount.Fixed(4)),
                effect = Effects.CounterSpell()
            ),
            elseEffect = ConditionalEffect(
                condition = Conditions.TargetSpellManaValueAtMost(DynamicAmount.Fixed(2)),
                effect = Effects.CounterSpell()
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0daa5458-2a97-40d0-b18d-2381a7a68ee1.jpg?1562897807"
    }
}
