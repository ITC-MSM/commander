package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ChooseActionEffect
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.FeasibilityCheck
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zoyowa Lava-Tongue {B}{R}
 * Legendary Creature — Goblin Warlock
 * 2/2
 *
 * Deathtouch
 * At the beginning of your end step, if you descended this turn, each opponent may
 * discard a card or sacrifice a permanent of their choice. Zoyowa deals
 * 3 damage to each opponent who didn't.
 * (You descended if a permanent card was put into your graveyard from anywhere.)
 *
 * "Descended this turn" is CR 700.11: at least one nontoken permanent card was put into
 * your graveyard from any zone this turn. The intervening-if gate fires only once per end
 * step regardless of how many times you descended, and cannot trigger if you have not yet
 * descended as the end step begins (Child of the Volcano / Broodrage Mycoid template).
 *
 * The per-opponent punisher is modeled with the Rottenmouth Viper / Thornplate Intimidator
 * template: [ForEachPlayerEffect] over [Player.EachOpponent] rebinds the controller to each
 * opponent in turn, then a [ChooseActionEffect] lets that opponent pick "Discard a card",
 * "Sacrifice a permanent", or the do-nothing option "Take 3 damage". Because the discard /
 * sacrifice are a "may", the damage option always remains available; infeasible discard /
 * sacrifice options are auto-filtered, so an opponent with no cards and no permanents simply
 * takes the 3 damage — exactly "each opponent who didn't." The damage source is Zoyowa
 * itself ([EffectTarget.Self], unaffected by the per-player controller rebind).
 */
val ZoyowaLavaTongue = card("Zoyowa Lava-Tongue") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Goblin Warlock"
    power = 2
    toughness = 2
    oracleText = "Deathtouch\n" +
        "At the beginning of your end step, if you descended this turn, each opponent may discard a card or " +
        "sacrifice a permanent of their choice. Zoyowa deals 3 damage to each opponent who didn't. " +
        "(You descended if a permanent card was put into your graveyard from anywhere.)"

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouDescendedThisTurn()
        effect = ForEachPlayerEffect(
            players = Player.EachOpponent,
            effects = listOf(
                ChooseActionEffect(
                    choices = listOf(
                        EffectChoice(
                            label = "Discard a card",
                            effect = Patterns.Hand.discardCards(1, EffectTarget.Controller),
                            feasibilityCheck = FeasibilityCheck.HasCardsInZone(Zone.HAND)
                        ),
                        EffectChoice(
                            label = "Sacrifice a permanent",
                            effect = ForceSacrificeEffect(
                                filter = GameObjectFilter.Permanent,
                                count = 1,
                                target = EffectTarget.Controller
                            ),
                            feasibilityCheck = FeasibilityCheck.ControlsPermanentMatching(GameObjectFilter.Permanent)
                        ),
                        EffectChoice(
                            label = "Take 3 damage from Zoyowa Lava-Tongue",
                            effect = Effects.DealDamage(
                                amount = 3,
                                target = EffectTarget.Controller,
                                damageSource = EffectTarget.Self
                            )
                        )
                    ),
                    player = EffectTarget.Controller
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Campbell White"
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c06f0ff2-d42f-4854-bbe5-4b022fb26d7d.jpg?1782694416"
        ruling("2023-11-10", "Some cards refer to a player who has \"descended this turn.\" This means that a permanent card has been put into that player's graveyard from anywhere this turn.")
        ruling("2023-11-10", "A permanent card is an artifact, battle, creature, enchantment, land, or planeswalker card. Tokens are not cards, and while tokens are put into the graveyard before ceasing to exist, that action doesn't count as a player having descended.")
        ruling("2023-11-10", "Abilities that begin with \"At the beginning of your end step, if you descended this turn\" will trigger only once during your end step, no matter how many times you descended this turn. However, if you haven't descended this turn as your end step begins, the ability won't trigger at all.")
        ruling("2023-11-10", "An opponent can always choose not to discard a card or sacrifice a permanent (and therefore be dealt 3 damage) even if they have cards in their hand or permanents on the battlefield.")
    }
}
