package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Scheming Silvertongue // Sign in Blood — Secrets of Strixhaven #99
 * {1}{B} · Creature — Vampire Warlock · 1/3
 *
 * Flying, lifelink
 * At the beginning of your second main phase, if you gained 2 or more life this turn, this
 * creature becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so
 * unprepares it.)
 * //
 * Sign in Blood — {B}{B} · Sorcery: Target player draws two cards and loses 2 life.
 *
 * Prepare (Secrets of Strixhaven): Scheming Silvertongue does NOT enter prepared (no PREPARED
 * keyword). It becomes prepared via its postcombat-main intervening-if trigger (gained 2+ life
 * this turn) through [Effects.BecomePrepared]. Becoming prepared creates a copy of its prepare
 * spell ("Sign in Blood") in exile that its controller may cast for {B}{B}; casting that copy
 * unprepares the creature. Modeled via `CardLayout.PREPARE` + the `prepare(name) { }` DSL.
 */
val SchemingSilvertongue = card("Scheming Silvertongue") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warlock"
    power = 1
    toughness = 3
    oracleText = "Flying, lifelink\n" +
        "At the beginning of your second main phase, if you gained 2 or more life this turn, " +
        "this creature becomes prepared. (While it's prepared, you may cast a copy of its spell. " +
        "Doing so unprepares it.)"

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.YouGainedLifeThisTurnAtLeast(2)
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Sign in Blood — the prepare spell.
    prepare("Sign in Blood") {
        manaCost = "{B}{B}"
        typeLine = "Sorcery"
        oracleText = "Target player draws two cards and loses 2 life."
        spell {
            val player = target("target player", TargetPlayer())
            effect = Effects.Composite(
                Effects.DrawCards(2, player),
                Effects.LoseLife(2, player),
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "99"
        artist = "Anna Steinbauer"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe85a124-0d8b-4a29-8df1-65888a39147f.jpg?1778165124"
    }
}
