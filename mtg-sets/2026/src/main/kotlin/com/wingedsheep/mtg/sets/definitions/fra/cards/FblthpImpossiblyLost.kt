package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fblthp, Impossibly Lost — Reality Fracture #213
 * {1}{U} · Legendary Creature — Homunculus · 1/1
 *
 * When one or more of your opponents are dealt combat damage during your turn, draw two cards.
 * If your library has no cards in it, you win the game. Fblthp's owner shuffles him into their
 * library. (If you draw from an empty library this way, you still win the game.)
 *
 * The trigger is the opponent-keyed combat-damage batch
 * ([Triggers.OneOrMoreOpponentsDealtCombatDamage]) — once per combat-damage step however many
 * opponents were hit — narrowed to your turn with a trigger restriction.
 *
 * The reminder text works because drawing from an empty library doesn't lose on the spot: the
 * attempt is recorded and the loss is the state-based action of CR 704.5b, checked only after the
 * whole ability has resolved. The win check (CR 104.2b) runs first and ends the game. The win check
 * precedes the shuffle, so Fblthp himself never refills the library he is judged on.
 */
val FblthpImpossiblyLost = card("Fblthp, Impossibly Lost") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Homunculus"
    power = 1
    toughness = 1
    oracleText = "When one or more of your opponents are dealt combat damage during your turn, draw two cards. " +
        "If your library has no cards in it, you win the game. Fblthp's owner shuffles him into their library. " +
        "(If you draw from an empty library this way, you still win the game.)"

    triggeredAbility {
        trigger = Triggers.OneOrMoreOpponentsDealtCombatDamage
        triggerRestriction = Conditions.IsYourTurn
        effect = Effects.Composite(
            Effects.DrawCards(2),
            ConditionalEffect(
                condition = Exists(player = Player.You, zone = Zone.LIBRARY, negate = true),
                effect = Effects.WinGame(message = "Fblthp, Impossibly Lost: your library has no cards in it.")
            ),
            Effects.ShuffleIntoLibrary(EffectTarget.Self)
        )
        description = "When one or more of your opponents are dealt combat damage during your turn, " +
            "draw two cards. If your library has no cards in it, you win the game. " +
            "Fblthp's owner shuffles him into their library."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Simon Dominic"
        flavorText = "Fblthp had *definitely* made a wrong turn."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3a2edbb-d144-4670-acad-17316cea98d2.jpg?1789127697"
        inBooster = false
    }
}
