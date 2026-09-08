package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rebellion of the Flamekin
 * {3}{R}
 * Kindred Enchantment — Elemental
 * Whenever you clash, you may pay {1}. If you do, create a 3/1 red Elemental Shaman creature token.
 * If you won, that token gains haste until end of turn. (This ability triggers after the clash ends.)
 *
 * Three nested riders, each a different primitive:
 *
 *  - **"Whenever you clash"** — [Triggers.WheneverYouClash], not the "and win" variant: the {1} is
 *    offered whether or not you won, and per its ruling it is offered on a clash an opponent's spell
 *    started too.
 *  - **"you may pay {1}. If you do, …"** — a [Gate.MayPay] over [PayManaCostEffect]; declining or
 *    being unable to pay makes the whole payoff, token and haste alike, not happen.
 *  - **"If you won, that token …"** — [Conditions.YouWonTheClash], the clash outcome carried on the
 *    trigger. It has to be read *after* the mana payment pauses and resumes, which is why the
 *    outcome lives on the resolving ability rather than in a pipeline collection.
 *
 * "That token" is [EffectTarget.PipelineTarget] over [CREATED_TOKENS], the slot
 * [Effects.CreateToken] publishes — so the haste lands on the token this resolution just made and
 * not on any other Elemental. It is a *granted* keyword for [Duration.EndOfTurn] rather than an
 * intrinsic one on the token, because the token outlives the turn and haste must not.
 */
val RebellionOfTheFlamekin = card("Rebellion of the Flamekin") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Kindred Enchantment — Elemental"
    oracleText = "Whenever you clash, you may pay {1}. If you do, create a 3/1 red Elemental " +
        "Shaman creature token. If you won, that token gains haste until end of turn. " +
        "(This ability triggers after the clash ends.)"

    triggeredAbility {
        trigger = Triggers.WheneverYouClash
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{1}"))),
            then = Effects.Composite(
                Effects.CreateToken(
                    power = 3,
                    toughness = 1,
                    colors = setOf(Color.RED),
                    creatureTypes = setOf("Elemental", "Shaman"),
                    imageUri = "https://cards.scryfall.io/normal/front/a/2/a280aee2-e15a-4625-b429-4032eae08a41.jpg?1783942839"
                ),
                ConditionalEffect(
                    Conditions.YouWonTheClash,
                    Effects.GrantKeyword(
                        Keyword.HASTE,
                        EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
                        Duration.EndOfTurn
                    )
                )
            )
        )
        description = "you may pay {1}. If you do, create a 3/1 red Elemental Shaman creature " +
            "token. If you won, that token gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Dan Dos Santos"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abd2c8cb-4852-4bde-b2cf-72ceb9b0dd3e.jpg?1783942871"
    }
}
