package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ragged Recluse // Odious Witch (Innistrad: Crimson Vow #127 — the card's earliest printing)
 * {1}{B} · Creature — Human Peasant 2/1 // Creature — Human Warlock 3/3
 *
 * Front — Ragged Recluse ({1}{B}, Creature — Human Peasant, 2/1)
 *   At the beginning of your end step, if you discarded a card this turn, transform this creature.
 *
 * Back — Odious Witch (Creature — Human Warlock, 3/3, black color indicator)
 *   Whenever this creature attacks, defending player loses 1 life and you gain 1 life.
 *
 * The front is Panicked Bystander's shape with a different tally: an `interveningIf` on the end-step
 * trigger, which CR 603.4 checks twice — once when the ability would trigger, and again on
 * resolution. A discard that happens in response to the trigger therefore still turns it on, and a
 * card discarded earlier in the turn keeps it on even though the hand has since refilled.
 *
 * "You discarded a card this turn" is [Conditions.YouDiscardedACardThisTurn], named in this change
 * over the turn-scoped tally `TurnTracker.CARDS_DISCARDED` — the engine's
 * `CardsDiscardedThisTurnComponent`, written by every discard site (cost, effect, cycling, hand-size
 * cleanup) and reset each turn. It counts *cards*, not discard events, which is what the printed
 * "a card" asks for: one discard of two cards satisfies it just as two discards of one do. This is
 * not an ordinary flip trigger the opponent can play around, so it is not a "may".
 *
 * The back is a plain attack drain. "Defending player" is [Player.DefendingPlayer], resolved from
 * the attack the trigger fired on rather than from targeting, so the ability targets nothing and the
 * life loss is not damage — protection, prevention and damage replacement never see it. The two
 * halves are one [Effects.Composite] because they are one printed sentence: they resolve in printed
 * order, and the life gain happens whether or not the loss did anything.
 */
private val RaggedRecluseFront = card("Ragged Recluse") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Peasant"
    power = 2
    toughness = 1
    oracleText = "At the beginning of your end step, if you discarded a card this turn, " +
        "transform this creature."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.YouDiscardedACardThisTurn
        effect = TransformEffect(EffectTarget.Self)
        description = "At the beginning of your end step, if you discarded a card this turn, " +
            "transform this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Lie Setiawan"
        flavorText = "\"Forgive my manners. It's been so long since I had a guest for dinner.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fb728de-0d6e-4b32-b0c4-edd7382d1391.jpg?1783924862"
    }
}

private val OdiousWitch = card("Odious Witch") {
    manaCost = ""
    colorIdentity = "B"
    colorIndicator = "B" // Transformed back face, no mana cost (CR 204).
    typeLine = "Creature — Human Warlock"
    power = 3
    toughness = 3
    oracleText = "Whenever this creature attacks, defending player loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.DefendingPlayer)),
            Effects.GainLife(1),
        )
        description = "Whenever this creature attacks, defending player loses 1 life and you gain 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Lie Setiawan"
        flavorText = "Her cauldron refilled, she settled in to wait for the next unsuspecting " +
            "traveler to knock at her door."
        imageUri = "https://cards.scryfall.io/normal/back/7/f/7fb728de-0d6e-4b32-b0c4-edd7382d1391.jpg?1783924862"
    }
}

val RaggedRecluse: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = RaggedRecluseFront,
    backFace = OdiousWitch,
)
