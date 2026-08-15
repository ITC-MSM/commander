package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Drag to the Roots
 * {2}{B}{G}
 * Instant
 * Delirium — This spell costs {2} less to cast as long as there are four or more card types among
 * cards in your graveyard.
 * Destroy target nonland permanent.
 *
 * Delirium is an ability word (no rules meaning of its own). The fixed {2} reduction is a
 * [CostModification.ReduceGeneric] gated by [CostGating.OnlyIf] on the standard four-card-type
 * graveyard [Conditions.Delirium] threshold — the same pattern as Mental Modulation's
 * conditional reduction, just with a delirium condition instead of "during your turn".
 */
val DragToTheRoots = card("Drag to the Roots") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText = "Delirium — This spell costs {2} less to cast as long as there are four or more " +
        "card types among cards in your graveyard.\nDestroy target nonland permanent."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.OnlyIf(Conditions.Delirium()),
        )
    }

    spell {
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Destroy(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Deruchenko Alexander"
        flavorText = "\"Valgavoth commands us to feed the trees. Let our blood be their mulch, " +
            "our flesh their soil.\"\n—Victor, Valgavoth's seneschal"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46f46095-6479-46b0-9e59-194d83f86a46.jpg?1726286661"
    }
}
