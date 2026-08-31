package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Seed Spark — Ravnica: City of Guilds #30
 * {3}{W} · Instant
 *
 * Destroy target artifact or enchantment. If {G} was spent to cast this spell, create two 1/1 green
 * Saproling creature tokens.
 *
 * The Selesnya half of Ravnica's "if {X} was spent" rider cycle. The green clause asks only what was
 * *paid*, so any source of {G} — a Forest, a Temple Garden, Birds of Paradise — turns the Saprolings
 * on; it is not a second colour requirement on the spell, which stays mono-white.
 *
 * The rider is a plain `ConditionalEffect` **after** the destruction rather than a branch around it,
 * because the two halves are independent: the tokens arrive even when the destroy half does nothing
 * (a target that regenerated or is indestructible), and the spell fizzles entirely — tokens included
 * — only when its single target is illegal on resolution (CR 608.2b).
 *
 * Note the earliest printing: Scryfall lists a `psal` (Salvat 2005) box printing three weeks before
 * Ravnica, but that is not a real expansion, so the canonical definition belongs here.
 */
val SeedSpark = card("Seed Spark") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment. " +
        "If {G} was spent to cast this spell, create two 1/1 green Saproling creature tokens."

    spell {
        val permanent = target("artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Move(permanent, Zone.GRAVEYARD, byDestruction = true)
            .then(
                ConditionalEffect(
                    condition = Conditions.ManaSpentToCastIncludes(requiredGreen = 1),
                    effect = Effects.CreateToken(
                        power = 1,
                        toughness = 1,
                        colors = setOf(Color.GREEN),
                        creatureTypes = setOf("Saproling"),
                        count = 2,
                    ),
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "30"
        artist = "Jeff Miracola"
        flavorText = "\"If you ask me, those root-lovers value mindless dogma over progress.\"\n" +
            "—Trivaz, Izzet mage"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33af31cf-d730-4f54-a0be-3229cdccf60c.jpg?1783943695"
    }
}
