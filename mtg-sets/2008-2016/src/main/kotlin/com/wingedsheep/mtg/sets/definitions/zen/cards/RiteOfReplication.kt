package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rite of Replication
 * {2}{U}{U}
 * Sorcery
 *
 * Kicker {5} (You may pay an additional {5} as you cast this spell.)
 * Create a token that's a copy of target creature. If this spell was kicked, create five
 * of those tokens instead.
 *
 * "Create five … instead" is a mutually-exclusive branch, not additive: a kicked cast
 * makes five copies *and no more* (not five + the base one). Modeled as a
 * [ConditionalEffect] on [Conditions.WasKicked] — the kicked branch creates five copies,
 * the else branch creates one. Each token is a copy of the resolved target creature via
 * `Effects.CreateTokenCopyOfTarget` (copiable characteristics per Rule 707); the copies'
 * own enters-the-battlefield triggers fire as they are created.
 */
val RiteOfReplication = card("Rite of Replication") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Kicker {5} (You may pay an additional {5} as you cast this spell.)\n" +
        "Create a token that's a copy of target creature. If this spell was kicked, " +
        "create five of those tokens instead."

    keywordAbility(KeywordAbility.kicker("{5}"))

    spell {
        val t = target("target", TargetCreature())
        effect = ConditionalEffect(
            condition = Conditions.WasKicked,
            effect = Effects.CreateTokenCopyOfTarget(t, count = 5),
            elseEffect = Effects.CreateTokenCopyOfTarget(t, count = 1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "61"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/4530fe45-8a3d-48e9-a7a5-abf8fb1485e3.jpg?1783942162"
    }
}
