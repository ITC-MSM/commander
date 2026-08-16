package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Hawkeye, Master Marksman — Marvel Super Heroes #130
 * {1}{R} · Legendary Creature — Human Archer Hero · Rare · 2/2
 *
 * Reach, first strike
 * Trick Arrows — Whenever Hawkeye becomes tapped, you may pay {1} up to three times. When you do,
 * choose up to that many —
 * • Net — Target creature can't block this turn.
 * • Explosive — Hawkeye deals 2 damage to target player.
 * • Boomerang — Discard a card, then draw a card.
 *
 * Modeling notes:
 *  - "Trick Arrows" is a flavor ability word (CR 207.2c) with no rules meaning; it lives in the
 *    oracle text and the ability's description only.
 *  - The whole ability is one [Triggers.BecomesTapped] trigger — cause-agnostic, so tapping him to
 *    attack, to crew, to pay a teamwork cost, or an opponent's Twiddle all trigger it. First strike
 *    plus a tap payoff is deliberate: attacking is the usual way to turn it on, and vigilance would
 *    turn it *off*.
 *  - "You may pay {1} up to three times. **When you do**, …" is the CR 603.12 reflexive shape:
 *    [ReflexiveTriggerEffect] with the repeated payment as the `action` and the modal as the
 *    `reflexiveEffect`, so the modes are announced on a second, separately-responded-to stack
 *    object *after* the payment is known — which is exactly what "up to that many" needs. The
 *    reflexive half never triggers when the controller declines or cannot pay.
 *  - The payment itself is [Effects.PayRepeatedly]: the count offered is capped at three *and* at
 *    what the controller can actually afford, and the number of repetitions is published to the
 *    resolution pipeline, where the modal reads it as [DynamicAmounts.timesPaid] for its
 *    `dynamicChooseCount`. "Up to that many" is a ceiling with a floor of zero, which is what a
 *    dynamic choose-count already means at the trigger-time evaluation site — so no
 *    `dynamicMinChooseCount`: paying twice and then taking one mode (or none) is legal.
 *  - Each mode carries its own target requirement and reads it as `ContextTarget(0)`, the standard
 *    per-mode targeting shape; the modes are picked and targeted as the reflexive ability goes on
 *    the stack (CR 603.3c/603.3d). `allowRepeat` stays false — CR 700.2d, no mode twice — so
 *    paying three times means all three arrows, not one arrow thrice.
 *  - "Hawkeye deals 2 damage" names *Hawkeye*, not the ability, so the damage source is the
 *    permanent — which is already the reflexive ability's source, hence no explicit `damageSource`.
 *    His first strike is irrelevant to it (this is not combat damage).
 */
val HawkeyeMasterMarksman = card("Hawkeye, Master Marksman") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Archer Hero"
    power = 2
    toughness = 2
    oracleText = "Reach, first strike\n" +
        "Trick Arrows — Whenever Hawkeye becomes tapped, you may pay {1} up to three times. " +
        "When you do, choose up to that many —\n" +
        "• Net — Target creature can't block this turn.\n" +
        "• Explosive — Hawkeye deals 2 damage to target player.\n" +
        "• Boomerang — Discard a card, then draw a card."

    keywords(Keyword.REACH, Keyword.FIRST_STRIKE)

    // Trick Arrows — Whenever Hawkeye becomes tapped, you may pay {1} up to three times.
    // When you do, choose up to that many —
    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = ReflexiveTriggerEffect(
            action = Effects.PayRepeatedly("{1}", upTo = 3),
            optional = true,
            reflexiveEffect = ModalEffect(
                modes = listOf(
                    Mode.withTarget(
                        effect = Effects.CantBlock(EffectTarget.ContextTarget(0)),
                        target = TargetCreature(),
                        description = "Net — Target creature can't block this turn."
                    ),
                    Mode.withTarget(
                        effect = Effects.DealDamage(2, EffectTarget.ContextTarget(0)),
                        target = TargetPlayer(),
                        description = "Explosive — Hawkeye deals 2 damage to target player."
                    ),
                    Mode.noTarget(
                        effect = Effects.Composite(
                            Patterns.Hand.discardCards(1),
                            Effects.DrawCards(1)
                        ),
                        description = "Boomerang — Discard a card, then draw a card."
                    )
                ),
                dynamicChooseCount = DynamicAmounts.timesPaid()
            ),
            descriptionOverride = "You may pay {1} up to three times. When you do, choose up to " +
                "that many —\n" +
                "• Net — Target creature can't block this turn.\n" +
                "• Explosive — Hawkeye deals 2 damage to target player.\n" +
                "• Boomerang — Discard a card, then draw a card."
        )
        description = "Trick Arrows — Whenever Hawkeye becomes tapped, you may pay {1} up to " +
            "three times. When you do, choose up to that many — Net — Target creature can't " +
            "block this turn.; Explosive — Hawkeye deals 2 damage to target player.; " +
            "Boomerang — Discard a card, then draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "130"
        artist = "Bachzim"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/9991b684-0ae0-4aa4-8f22-a9c473f5d69c.jpg?1783902931"
    }
}
