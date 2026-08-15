package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker

/**
 * Impractical Joke
 * {R}
 * Sorcery
 *
 * Damage can't be prevented this turn.
 * Impractical Joke deals 3 damage to up to one target creature or planeswalker.
 *
 * The prevention shield is a one-shot continuous effect created at resolution
 * ([Effects.DamageCantBePreventedThisTurn]); it applies for the rest of the turn regardless of
 * whether a target was chosen. The damage targets "up to one" creature or planeswalker, so it is
 * an optional target — the spell still resolves (lifting prevention) even with no target chosen.
 */
val ImpracticalJoke = card("Impractical Joke") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Damage can't be prevented this turn.\n" +
        "Impractical Joke deals 3 damage to up to one target creature or planeswalker."

    spell {
        val victim = target(
            "up to one target creature or planeswalker",
            TargetCreatureOrPlaneswalker(optional = true),
        )
        effect = Effects.DamageCantBePreventedThisTurn() then Effects.DealDamage(3, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Caroline Gariba"
        flavorText = "Strixhaven students have a sense of humor. The spirits of the Blood Age do not."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39a816b4-39b8-421c-b828-68db901d34b7.jpg?1775937777"
    }
}
