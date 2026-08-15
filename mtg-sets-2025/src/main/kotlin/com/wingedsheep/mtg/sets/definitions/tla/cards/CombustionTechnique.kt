package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.MarkExileOnDeathEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Combustion Technique
 * {1}{R}
 * Instant — Lesson
 *
 * Combustion Technique deals damage equal to 2 plus the number of Lesson cards in your graveyard
 * to target creature. If that creature would die this turn, exile it instead.
 *
 * Damage amount is evaluated on resolution as [DynamicAmount.Add] of 2 and the Lesson-card count in
 * your graveyard. The damage then marks the target with [MarkExileOnDeathEffect], a death-replacement
 * (CR 614) that exiles it instead of letting it go to the graveyard this turn — so even lethal damage
 * from another source sends it to exile. The marker expires at end of turn if it survives.
 */
val CombustionTechnique = card("Combustion Technique") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant — Lesson"
    oracleText = "Combustion Technique deals damage equal to 2 plus the number of Lesson cards in " +
        "your graveyard to target creature. If that creature would die this turn, exile it instead."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = DealDamageEffect(
            DynamicAmount.Add(
                DynamicAmount.Fixed(2),
                DynamicAmount.Count(
                    Player.You,
                    Zone.GRAVEYARD,
                    GameObjectFilter.Any.withSubtype(Subtype.LESSON),
                ),
            ),
            t,
        ) then MarkExileOnDeathEffect(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "128"
        artist = "Devin Elle Kurtz"
        flavorText = "\"Sokka, watch out! It's Sparky-Sparky Boom Man!\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdcca576-2ef2-44cc-9944-a92bd146444a.jpg?1774088518"
    }
}
