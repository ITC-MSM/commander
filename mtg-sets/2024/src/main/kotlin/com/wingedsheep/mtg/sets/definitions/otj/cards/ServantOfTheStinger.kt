package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Servant of the Stinger
 * {1}{B}
 * Creature — Human Warlock
 * 1/3
 * Deathtouch
 * Whenever this creature deals combat damage to a player, if you've committed a crime this turn,
 * you may sacrifice this creature. If you do, search your library for a card, put it into your
 * hand, then shuffle.
 *
 * The "if you've committed a crime this turn" clause is an intervening-if (CR 603.4): it's
 * checked both when the ability would trigger and again as it resolves. Modeled via
 * [triggerCondition]. The "you may sacrifice this creature. If you do, search..." pay-then-payoff
 * is a [GatedEffect] with a [Gate.MayPay] sacrificing this creature.
 */
val ServantOfTheStinger = card("Servant of the Stinger") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warlock"
    power = 1
    toughness = 3
    oracleText = "Deathtouch\n" +
        "Whenever this creature deals combat damage to a player, if you've committed a crime this turn, " +
        "you may sacrifice this creature. If you do, search your library for a card, put it into your hand, then shuffle."

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        triggerCondition = Conditions.YouCommittedCrimeThisTurn
        effect = GatedEffect(
            gate = Gate.MayPay(SacrificeSelfEffect),
            then = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any,
                count = 1,
                destination = SearchDestination.HAND,
                shuffleAfter = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Steven Russell Black"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5c98650-b195-4071-8e02-4df35fddddc7.jpg?1712355666"

        ruling("2024-04-12", "A player commits a crime as they cast a spell, activate an ability, or put a triggered ability on the stack that targets at least one opponent, at least one permanent, spell, or ability an opponent controls, and/or at least one card in an opponent's graveyard.")
        ruling("2024-04-12", "Whether you've committed a crime this turn is checked as Servant of the Stinger's triggered ability would trigger and again as it resolves. If you haven't committed a crime by the time it resolves, the ability does nothing.")
    }
}
