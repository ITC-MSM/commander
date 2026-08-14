package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Synod Sanctum — Mirrodin #252
 * {1} · Artifact · Uncommon
 *
 * {2}, {T}: Exile target permanent you control.
 * {2}, Sacrifice this artifact: Return all cards exiled with this artifact to the battlefield
 * under your control.
 *
 * Modelling notes:
 * - The two abilities are *linked* (CR 607): the second one can only find what the first one
 *   exiled. That link is the exile's `linkToSource = true` plus the matching
 *   [CardSource.FromLinkedExile] gather — the same pairing Northampton Farm uses — not a
 *   "cards in exile" scan, which would wrongly scoop up other players' exiled cards.
 * - The return is "all cards", so there is no selection step: gather the linked exile and move
 *   the whole collection. An empty linked exile makes the ability a legal but empty activation.
 * - `CardDestination.ToZone(Zone.BATTLEFIELD, Player.You)` is what makes it "under **your**
 *   control" — the exiled permanents may have been targets you owned but a returned card comes
 *   back under the activating player's control regardless of owner.
 * - Sacrificing the Sanctum is a *cost*, so it is already gone when the ability resolves. The
 *   linked exile is keyed to the object that exiled the cards, which is why the ability can
 *   still find them (CR 607.2 last-known-information; the same shape as Northampton Farm).
 */
val SynodSanctum = card("Synod Sanctum") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}, {T}: Exile target permanent you control.\n" +
        "{2}, Sacrifice this artifact: Return all cards exiled with this artifact to the " +
        "battlefield under your control."

    activatedAbility {
        val permanent = target(
            "target permanent you control",
            TargetPermanent(filter = TargetFilter.PermanentYouControl)
        )
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Effects.Move(permanent, Zone.EXILE, linkToSource = true)
        description = "{2}, {T}: Exile target permanent you control."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(source = CardSource.FromLinkedExile(), storeAs = "exiled"),
                MoveCollectionEffect(
                    from = "exiled",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, Player.You)
                )
            )
        )
        description = "{2}, Sacrifice this artifact: Return all cards exiled with this artifact " +
            "to the battlefield under your control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "252"
        artist = "Dana Knutson"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45f2ab6e-019e-4e72-be06-9c8cd97d54d4.jpg?1783944501"
    }
}
