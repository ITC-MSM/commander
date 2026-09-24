package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lyra, Tolarian Archangel.
 *
 * The end-step trigger is an intervening "if" (Resplendent Angel's shape): it checks your
 * cards-drawn-this-turn tally both when the end step begins and on resolution, and it fires at
 * *each* end step, so drawing three on an opponent's turn makes an Angel at their end step too.
 *
 * The activated ability's "until end of turn, whenever Lyra deals combat damage to a player" is
 * recorded as a turn-long triggered ability on Lyra herself. It ends with the turn, is lost if
 * Lyra leaves the battlefield (a returned Lyra is a new object), and a second activation stacks
 * a second instance — two activations draw four.
 */
val LyraTolarianArchangel = card("Lyra, Tolarian Archangel") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Angel Wizard"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "At the beginning of each end step, if you've drawn three or more cards this turn, create a 3/3 " +
        "blue Angel creature token with flying.\n" +
        "{3}{U}{U}: Until end of turn, whenever Lyra deals combat damage to a player, draw two cards."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        interveningIf = Conditions.YouDrewCardsThisTurn(3)
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Angel"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/5/1/5165055a-89e1-471b-ac96-732e593c910b.jpg?1789735744"
        )
    }

    activatedAbility {
        cost = Costs.Mana("{3}{U}{U}")
        effect = GrantTriggeredAbilityEffect(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = Effects.DrawCards(2)
            ),
            target = EffectTarget.Self,
            duration = Duration.EndOfTurn
        )
        description = "Until end of turn, whenever Lyra deals combat damage to a player, draw two cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "217"
        artist = "Victor Adame Minguez"
        flavorText = "Knowledge is radiance."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5183681-447b-4023-91f7-00e9338f4417.jpg?1789128032"
        inBooster = false
    }
}
