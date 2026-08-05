package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.PlayersCantCastSpells
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
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
 *   that much damage to any target. Do this only once each turn.
 *
 * A transforming double-faced creature ([CardDefinition.doubleFacedCreature]); the front owns the
 * sorcery-speed [TransformEffect] flip ([TimingRule.SorcerySpeed]). The back is reached only via
 * that flip, so it carries no castable mana cost — its G/W colors come from a color indicator
 * (CR 204).
 *
 *  - **Both faces** share the cast restriction, which is the reused [PlayersCantCastSpells]
 *    primitive (`EachOpponent`, `condition = IsYourTurn`) — Voice of Victory's exact shape, read
 *    at cast-legality time across every casting zone.
 *  - The back's payoff is a **recipient-side** damage trigger: `Triggers.dealsDamage` with
 *    [RecipientFilter.CreatureYouControl] and [TriggerBinding.ANY] reads as "whenever a creature
 *    you control is dealt damage" (any source, combat or not). She-Hulk is herself a creature you
 *    control, so damage dealt to her triggers it too.
 *  - "that much damage" is the damage amount carried on the trigger context
 *    ([ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT]); the "you may" is a resolution-time
 *    [MayEffect] over the targeted [DealDamageEffect], while "any target" is chosen when the
 *    ability goes on the stack (CR 603.3d).
 *  - "Do this only once each turn" is `oncePerTurn` on the triggered ability — the same modeling
 *    the set-mates and Ancient Cornucopia use for that wording.
 */

private val JenniferWaltersFront = card("Jennifer Walters") {
    manaCost = "{1}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Advisor Hero"
    power = 2
    toughness = 3
    oracleText = "Your opponents can't cast spells during your turn.\n" +
        "{3}{G}{W}{W}: Transform Jennifer Walters. Activate only as a sorcery."

    // Your opponents can't cast spells during your turn.
    staticAbility {
        ability = PlayersCantCastSpells(affected = Player.EachOpponent, condition = IsYourTurn)
    }

    // {3}{G}{W}{W}: Transform Jennifer Walters. Activate only as a sorcery.
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

    // Your opponents can't cast spells during your turn.
    staticAbility {
        ability = PlayersCantCastSpells(affected = Player.EachOpponent, condition = IsYourTurn)
    }

    // Whenever a creature you control is dealt damage, you may have The Sensational She-Hulk deal
    // that much damage to any target. Do this only once each turn.
    triggeredAbility {
        trigger = Triggers.dealsDamage(
            recipient = RecipientFilter.CreatureYouControl,
            binding = TriggerBinding.ANY,
        )
        oncePerTurn = true
        val victim = target("any target", Targets.Any)
        effect = MayEffect(
            DealDamageEffect(
                amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
                target = victim,
            )
        )
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
