package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Full Bore
 * {R}
 * Instant
 * Target creature you control gets +3/+2 until end of turn. If that creature was
 * cast for its warp cost, it also gains trample and haste until end of turn.
 */
val FullBore = card("Full Bore") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +3/+2 until end of turn. " +
        "If that creature was cast for its warp cost, it also gains trample and haste until end of turn."

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(3, 2, creature)
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(
                        GameObjectFilter.Creature.castForWarp(),
                        targetIndex = 0
                    ),
                    effect = Effects.GrantKeyword(Keyword.TRAMPLE, creature)
                        .then(Effects.GrantKeyword(Keyword.HASTE, creature))
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "135"
        artist = "Olivier Bernard"
        flavorText = "The mining gear that crumbled their planet could also deal terrible damage to flesh."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfee64ef-d22d-4024-bc65-59cbd1731d1c.jpg?1752947099"
    }
}
