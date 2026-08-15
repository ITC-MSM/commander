package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeTurnedFaceUp
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern.YouAttackEvent
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Karlov Watchdog — Murders at Karlov Manor #20
 * {3}{W} · Creature — Dog · 3/2
 *
 * Vigilance
 * Permanents your opponents control can't be turned face up during your turn.
 * Whenever you attack with three or more creatures, creatures you control get +1/+1 until end of turn.
 *
 * The lock is white's answer to the disguise/cloak deck: on your turn an opponent can't flip a
 * face-down permanent up, so they can't ambush your attack with a surprise 4/5 in the declare-blockers
 * step. It's [CantBeTurnedFaceUp] over every permanent they control, wrapped in a
 * [ConditionalStaticAbility] gated by [Conditions.IsYourTurn] — the static is only *live* during your
 * turn, and the turn-face-up special action reads the projected flag and is rejected. "Your" resolves
 * to the Watchdog's projected controller, so a stolen Watchdog locks down its new controller's
 * opponents instead.
 *
 * `CantBeTurnedFaceUp` defaults its filter to the *attached* creature (it's normally an Aura's
 * ability, as on Unable to Scream), so the opponent-wide filter is passed explicitly.
 *
 * The attack payoff is `YouAttackEvent(minAttackers = 3)` bound ANY — the Meddling Youths shape. The
 * Watchdog need not be among the three attackers (it has vigilance, so it often attacks anyway), and
 * the trigger fires once per declare-attackers rather than once per attacker. The pump is a plain
 * Layer 7c modification over every creature you control, including creatures that didn't attack and
 * any that entered after attackers were declared but before the trigger resolved.
 */
val KarlovWatchdog = card("Karlov Watchdog") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    power = 3
    toughness = 2
    oracleText = "Vigilance\n" +
        "Permanents your opponents control can't be turned face up during your turn.\n" +
        "Whenever you attack with three or more creatures, creatures you control get +1/+1 until " +
        "end of turn."

    keywords(Keyword.VIGILANCE)

    // "Permanents your opponents control can't be turned face up during your turn."
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantBeTurnedFaceUp(GroupFilter.AllPermanents.opponentControls()),
            condition = Conditions.IsYourTurn,
        )
    }

    triggeredAbility {
        trigger = TriggerSpec(YouAttackEvent(minAttackers = 3), TriggerBinding.ANY)
        effect = Effects.ForEachInGroup(
            filter = GroupFilter.AllCreaturesYouControl,
            effect = Effects.ModifyStats(1, 1, EffectTarget.Self, Duration.EndOfTurn),
        )
        description = "Whenever you attack with three or more creatures, creatures you control " +
            "get +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "20"
        artist = "Craig J Spearing"
        flavorText = "Teysa trusts only those for whom money has no meaning."
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79cfb366-ae2a-4b3d-9a80-383a32db1509.jpg?1783912924"
    }
}
