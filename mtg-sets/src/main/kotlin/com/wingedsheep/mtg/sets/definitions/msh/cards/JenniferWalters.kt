package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.DealsDamageEvent
import com.wingedsheep.sdk.scripting.PlayersCantCastSpells
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Jennifer Walters // The Sensational She-Hulk — Marvel Super Heroes #18 (mythic)
 *
 * Front — Jennifer Walters · {1}{W} · Legendary Creature — Human Advisor Hero · 2/3
 *   Your opponents can't cast spells during your turn.
 *   {3}{G}{W}{W}: Transform Jennifer Walters. Activate only as a sorcery.
 *
 * Back — The Sensational She-Hulk · Legendary Creature — Gamma Hero · 6/6
 *   Reach, trample
 *   Your opponents can't cast spells during your turn.
 *   Whenever a creature you control is dealt damage, you may have The Sensational She-Hulk deal
 *     that much damage to any target. Do this only once each turn.
 *
 * A transforming double-faced creature ([CardDefinition.doubleFacedCreature]), the Bruce Banner
 * shape: the front owns the sorcery-speed [TransformEffect] flip, and the back is reached only
 * through it, so it carries no castable mana cost — its G/W colors come from a color indicator
 * (CR 204).
 *
 *  - **"Your opponents can't cast spells during your turn"** (both faces) is the reusable
 *    [PlayersCantCastSpells] static scoped to [Player.EachOpponent] with `condition = IsYourTurn` —
 *    the Voice of Victory precedent. The engine reads it at cast-legality time, so it covers every
 *    casting zone with no per-zone wiring.
 *
 *  - **The damage mirror** is a `DealsDamageEvent` observer ([TriggerBinding.ANY]) filtered to
 *    [RecipientFilter.CreatureYouControl] — the Kazarov, Sengir Pureblood shape read from the
 *    recipient's side. It is deliberately *not* a batch trigger: CR 603.2 makes it fire once per
 *    damaged creature, so a multi-block puts one instance on the stack per creature that was dealt
 *    damage. "That much damage" reads the triggering event's amount via
 *    [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT].
 *
 *  - **"Do this only once each turn" is an *effect* cap, not a trigger cap** — `effectOncePerTurn =
 *    true`, never `oncePerTurn = true`. That is the whole card: in a multi-block every damaged
 *    creature offers its own trigger and the controller declines down the line until the instance
 *    carrying the biggest damage number, then applies that one. The trigger cap would fire only for
 *    the first creature dealt damage and make the rest unreachable. Declining costs nothing — the
 *    engine lowers the flag into a [com.wingedsheep.sdk.scripting.effects.Gate.OnceEachTurn] budget
 *    gate placed *inside* the [MayEffect] consent gate, so only an application that actually happens
 *    spends the turn's single use.
 */

private val JenniferWaltersFront = card("Jennifer Walters") {
    manaCost = "{1}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Advisor Hero"
    power = 2
    toughness = 3
    oracleText = "Your opponents can't cast spells during your turn.\n" +
        "{3}{G}{W}{W}: Transform Jennifer Walters. Activate only as a sorcery."

    staticAbility {
        ability = PlayersCantCastSpells(affected = Player.EachOpponent, condition = IsYourTurn)
    }

    activatedAbility {
        cost = Costs.Mana("{3}{G}{W}{W}")
        effect = TransformEffect(EffectTarget.Self)
        timing = TimingRule.SorcerySpeed
        description = "Transform Jennifer Walters. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "18"
        artist = "Taurin Clarke"
        flavorText = "\"I will remind the witness that I have permission to treat her as hostile.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61237530-ad49-469c-a952-67c92315708e.jpg?1783902978"
    }
}

private val TheSensationalSheHulkBack = card("The Sensational She-Hulk") {
    manaCost = ""
    colorIdentity = "GW"
    colorIndicator = "GW" // Transformed back face, no mana cost (CR 204).
    typeLine = "Legendary Creature — Gamma Hero"
    power = 6
    toughness = 6
    oracleText = "Reach, trample\n" +
        "Your opponents can't cast spells during your turn.\n" +
        "Whenever a creature you control is dealt damage, you may have The Sensational She-Hulk " +
        "deal that much damage to any target. Do this only once each turn."

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    staticAbility {
        ability = PlayersCantCastSpells(affected = Player.EachOpponent, condition = IsYourTurn)
    }

    triggeredAbility {
        trigger = TriggerSpec(
            DealsDamageEvent(recipient = RecipientFilter.CreatureYouControl),
            TriggerBinding.ANY,
        )
        val victim = target("any target", Targets.Any)
        effect = MayEffect(
            Effects.DealDamage(
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
                victim,
            )
        )
        effectOncePerTurn = true
        description = "Whenever a creature you control is dealt damage, you may have The " +
            "Sensational She-Hulk deal that much damage to any target. Do this only once each turn."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "18"
        artist = "Taurin Clarke"
        imageUri = "https://cards.scryfall.io/normal/back/6/1/61237530-ad49-469c-a952-67c92315708e.jpg?1783902978"
    }
}

val JenniferWalters: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = JenniferWaltersFront,
    backFace = TheSensationalSheHulkBack,
)
