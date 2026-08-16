package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ulcerate
 * {B}
 * Instant
 * Target creature gets -3/-3 until end of turn. You lose 3 life.
 */
val Ulcerate = card("Ulcerate") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-3 until end of turn. You lose 3 life."

    spell {
        val t = target("target creature", Targets.Creature)
        // The life loss isn't a cost — it only happens if the spell resolves.
        effect = Effects.ModifyStats(-3, -3, t)
            .then(Effects.LoseLife(3, EffectTarget.Controller))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Johann Bodin"
        flavorText = "\"If it were merely lethal, that would be sufficient. The art, however, is in maximizing the suffering it causes.\" —Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e06e6c8-05c0-4d87-9961-605b888bc794.jpg?1783939179"
        ruling(
            "2017-11-17",
            "The loss of life isn't a cost. If the target creature is an illegal target when Ulcerate tries to resolve, it won't resolve and none of its effects will happen. You won't lose any life.",
        )
    }
}
