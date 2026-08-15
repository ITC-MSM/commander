package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect

/**
 * Firebending Lesson — {R}
 * Instant — Lesson
 * Kicker {4} (You may pay an additional {4} as you cast this spell.)
 * Firebending Lesson deals 2 damage to target creature. If this spell was kicked,
 * it deals 5 damage to that creature instead.
 *
 * Note: despite the name, this spell does NOT have the firebending keyword — it is
 * a plain Kicker + damage spell.
 */
val FirebendingLesson = card("Firebending Lesson") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant — Lesson"
    oracleText = "Kicker {4} (You may pay an additional {4} as you cast this spell.)\n" +
        "Firebending Lesson deals 2 damage to target creature. If this spell was kicked, " +
        "it deals 5 damage to that creature instead."

    keywordAbility(KeywordAbility.kicker("{4}"))

    spell {
        val t = target("target creature", Targets.Creature)
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = DealDamageEffect(5, t),
            elseEffect = Effects.DealDamage(2, t),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Toni Infante"
        flavorText = "\"Firebending is not something to fear, but if you don't respect it, " +
            "it'll chew you up and spit you out.\"\n—Zuko"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa940e68-010e-4b68-be8a-555d7068f7b4.jpg?1764120947"
    }
}
