package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Urza's Rage
 * {2}{R}
 * Instant
 * Kicker {8}{R}
 * This spell can't be countered.
 * Urza's Rage deals 3 damage to any target. If this spell was kicked, instead it
 * deals 10 damage to that permanent or player and the damage can't be prevented.
 */
val UrzasRage = card("Urza's Rage") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Kicker {8}{R} (You may pay an additional {8}{R} as you cast this spell.)\n" +
        "This spell can't be countered.\n" +
        "Urza's Rage deals 3 damage to any target. If this spell was kicked, instead it deals " +
        "10 damage to that permanent or player and the damage can't be prevented."

    cantBeCountered = true
    keywordAbility(KeywordAbility.kicker("{8}{R}"))

    spell {
        val t = target("any target", Targets.Any)
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = DealDamageEffect(10, t, cantBePrevented = true),
            elseEffect = Effects.DealDamage(3, t),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "178"
        artist = "Matthew D. Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61a25a35-3ae4-471e-adcd-d8baf2f77b68.jpg?1562914759"
    }
}
