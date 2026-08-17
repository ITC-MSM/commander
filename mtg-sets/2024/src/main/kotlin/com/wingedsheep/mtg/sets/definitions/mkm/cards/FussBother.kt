package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fuss // Bother — Murders at Karlov Manor #248
 * Split-layout spell (CR 709). Cast either half from hand; only the chosen half goes on the stack.
 *
 * Fuss {2}{R/W} — Instant
 *   Put a +1/+1 counter on each attacking creature you control.
 *
 * Bother {4}{W/U}{W/U} — Sorcery
 *   Create three 1/1 colorless Thopter artifact creature tokens with flying. Surveil 2.
 *
 * Neither half targets, so Fuss is [Effects.ForEachInGroup] over the attacking creatures you
 * control rather than a target list — it hits whatever is attacking as it resolves, and does
 * nothing at all outside combat. `EffectTarget.Self` inside the group body is the *iterated*
 * permanent, not the spell.
 *
 * Bother's Thopters take their art from the MKM `tokenArt` layer, so no `imageUri` is baked in.
 * Surveil comes last, matching printed order — the tokens exist before the surveil's graveyard
 * decision, which matters for anything watching token creation.
 */
val FussBother = card("Fuss // Bother") {
    layout = CardLayout.SPLIT
    colorIdentity = "WUR"

    face("Fuss") {
        manaCost = "{2}{R/W}"
        typeLine = "Instant"
        oracleText = "Put a +1/+1 counter on each attacking creature you control."

        spell {
            effect = Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.youControl().attacking()),
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
        }
    }

    face("Bother") {
        manaCost = "{4}{W/U}{W/U}"
        typeLine = "Sorcery"
        oracleText = "Create three 1/1 colorless Thopter artifact creature tokens with flying. " +
            "Surveil 2."

        spell {
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = emptySet(),
                creatureTypes = setOf("Thopter"),
                keywords = setOf(Keyword.FLYING),
                count = 3,
                artifactToken = true,
                name = "Thopter",
            ) then Patterns.Library.surveil(2)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "248"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/269a031e-0b89-40e1-b11b-ae870d72161c.jpg?1783912831"
    }
}
