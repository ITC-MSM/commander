package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Pearl Shard — Mirrodin #225 (canonical printing)
 * {3} · Artifact
 *
 * {3}, {T} or {W}, {T}: Prevent the next 2 damage that would be dealt to any target this turn.
 *
 * The white member of the Mirrodin Shard cycle, and modelled exactly like [GraniteShard]: the printed
 * "{3}, {T} or {W}, {T}" is one ability with two alternative cost sets (2004-10-04 ruling: pay one or
 * the other, never both), which two `activatedAbility` blocks with identical effects express directly.
 * Both include the shard's own `{T}`, so they compete for the same tap and only one can be activated
 * per untap — the same play pattern as the single printed ability.
 *
 * `colorIdentity = "W"` because the {W} alternative puts white in the card's identity even though the
 * card itself is colorless (CR 903.4).
 */
val PearlShard = card("Pearl Shard") {
    manaCost = "{3}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "{3}, {T} or {W}, {T}: Prevent the next 2 damage that would be dealt to any target this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val recipient = target("any target", AnyTarget())
        effect = Effects.PreventNextDamage(2, recipient)
        description = "{3}, {T}: Prevent the next 2 damage that would be dealt to any target this turn."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val recipient = target("any target", AnyTarget())
        effect = Effects.PreventNextDamage(2, recipient)
        description = "{W}, {T}: Prevent the next 2 damage that would be dealt to any target this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Doug Chaffee"
        flavorText = "Leonin folktales claim it was brought from beyond the sky by the first kha."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abde295d-af41-4b9c-af48-3067befe9383.jpg?1783944508"
        ruling("2004-10-04", "You can pay either of the two costs (but not both at the same time) to activate the ability.")
    }
}
