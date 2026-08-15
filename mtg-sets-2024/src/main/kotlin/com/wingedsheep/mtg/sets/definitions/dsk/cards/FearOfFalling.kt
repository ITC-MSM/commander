package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect

/**
 * Fear of Falling
 * {3}{U}{U}
 * Enchantment Creature — Nightmare
 * 4/4
 * Flying
 * Whenever this creature attacks, target creature defending player controls gets -2/-0 and
 * loses flying until your next turn.
 *
 * "Defending player controls" is modeled as `Targets.CreatureOpponentControls` — the same
 * convention attack-trigger cards use for the defending player in the engine's 1v1 combat
 * (cf. Swooping Talon's Provoke). The -2/-0 and flying loss share the `UntilYourNextTurn`
 * duration, composed as two floating effects on the chosen creature.
 */
val FearOfFalling = card("Fear of Falling") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "Flying\nWhenever this creature attacks, target creature defending player controls " +
        "gets -2/-0 and loses flying until your next turn."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val targeted = target("target creature defending player controls", Targets.CreatureOpponentControls)
        effect = Effects.Composite(
            ModifyStatsEffect(-2, 0, targeted, Duration.UntilYourNextTurn),
            Effects.RemoveKeyword(Keyword.FLYING, targeted, Duration.UntilYourNextTurn)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Maxime Minard"
        flavorText = "It had been months since Jakob had started falling but only a few weeks since he'd run out of breath to scream."
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e814e48-cd9d-428f-90e2-74d97cb9c8f1.jpg?1726286065"
    }
}
