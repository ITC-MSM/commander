package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.MarkExileOnDeathEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wilt in the Heat — Secrets of Strixhaven #243
 * {2}{R}{W} · Instant
 *
 * This spell costs {2} less to cast if one or more cards left your graveyard this turn.
 * Wilt in the Heat deals 5 damage to target creature. If that creature would die this turn,
 * exile it instead.
 *
 * The cost reduction is a [ModifySpellCost] static on [SpellCostTarget.SelfCast], gated by
 * [CostGating.OnlyIf] with [Conditions.CardsLeftGraveyardThisTurn] (≥1) — evaluated at cast time
 * (the Essence Anchor / Primary Research graveyard-departure tracker). The spell deals 5 damage
 * and then marks the creature with [MarkExileOnDeathEffect], a death-replacement (CR 614) that
 * exiles it instead of letting it die this turn — so even lethal damage from another source
 * sends it to exile rather than the graveyard.
 */
val WiltInTheHeat = card("Wilt in the Heat") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Instant"
    oracleText = "This spell costs {2} less to cast if one or more cards left your graveyard " +
        "this turn.\nWilt in the Heat deals 5 damage to target creature. If that creature would " +
        "die this turn, exile it instead."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.OnlyIf(Conditions.CardsLeftGraveyardThisTurn(1)),
        )
    }

    spell {
        val t = target("target creature", TargetCreature())
        effect = DealDamageEffect(5, t) then MarkExileOnDeathEffect(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "243"
        artist = "Raluca Marinescu"
        flavorText = "\"'It'll be fun,' they said. 'The Fields are full of dead things,' they said.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f63f7209-fc0f-400c-8076-125f3131cb32.jpg?1775938697"
    }
}
