package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Plasma Bolt
 * {R}
 * Sorcery
 * Plasma Bolt deals 2 damage to any target.
 * Void — Plasma Bolt deals 3 damage instead if a nonland permanent left the battlefield this turn
 * or a spell was warped this turn.
 */
val PlasmaBolt = card("Plasma Bolt") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Plasma Bolt deals 2 damage to any target.\n" +
        "Void — Plasma Bolt deals 3 damage instead if a nonland permanent left the battlefield " +
        "this turn or a spell was warped this turn."

    spell {
        target = Targets.Any
        effect = Effects.DealDamage(
            amount = DynamicAmount.Conditional(
                condition = Conditions.Void,
                ifTrue = DynamicAmount.Fixed(3),
                ifFalse = DynamicAmount.Fixed(2)
            ),
            target = EffectTarget.ContextTarget(0)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Viko Menezes"
        flavorText = "Excess energy from the moxite core is focused into a destructive ray of plasma."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1a1834b-76c2-4496-b8c5-18b69ab34c4c.jpg?1752947168"
    }
}
