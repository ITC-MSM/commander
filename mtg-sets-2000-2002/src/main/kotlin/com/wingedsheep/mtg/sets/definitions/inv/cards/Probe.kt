package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Probe
 * {2}{U}
 * Sorcery
 * Kicker {1}{B}
 * Draw three cards, then discard two cards. If this spell was kicked, target player
 * discards two cards.
 *
 * The target player is only relevant when the spell is kicked, so it is declared as an
 * optional target (minCount 0). When unkicked, no target need be chosen; the kicked
 * discard simply never resolves.
 *
 * Approximation: strictly, a *kicked* Probe requires a target player. Kicker is chosen
 * while casting (CR 601.2b) and targets are then announced (CR 601.2c), so the targeted
 * clause is only mandatory once the spell is kicked. The SDK has no kicker-conditional
 * target requirement, so a kicked cast could legally be made with no target — in which
 * case the discard fizzles. A dedicated card-specific primitive isn't worth it for one
 * card; if a second kicked-only-target card appears, add a general "required-iff-kicked"
 * target flag.
 */
val Probe = card("Probe") {
    manaCost = "{2}{U}"
    colorIdentity = "UB"
    typeLine = "Sorcery"
    oracleText = "Kicker {1}{B} (You may pay an additional {1}{B} as you cast this spell.)\n" +
        "Draw three cards, then discard two cards. If this spell was kicked, target player discards two cards."

    keywordAbility(KeywordAbility.kicker("{1}{B}"))

    spell {
        val targetPlayer = target("target player", TargetPlayer(optional = true))
        effect = Effects.DrawCards(3) then
            Effects.Discard(2) then
            ConditionalEffect(
                condition = WasKicked,
                effect = Effects.Discard(2, targetPlayer)
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Eric Peterson"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2a58d18-3d52-4178-86b2-7590d4164e76.jpg?1562927868"
    }
}
