package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.AttackEvent
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.events.AttackPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Aurelia, the Law Above
 * {3}{R}{W}
 * Legendary Creature — Angel
 * 4/4
 * Flying, vigilance, haste
 * Whenever a player attacks with three or more creatures, you draw a card.
 * Whenever a player attacks with five or more creatures, Aurelia deals 3 damage to each of your
 * opponents and you gain 3 life.
 *
 * Each ability observes the single declare-attackers event with an ANY binding. The attacker-count
 * predicate is evaluated from that event's declared-attacker snapshot, so removing an attacker in
 * response does not undo either trigger.
 */
val AureliaTheLawAbove = card("Aurelia, the Law Above") {
    manaCost = "{3}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Angel"
    oracleText = "Flying, vigilance, haste\n" +
        "Whenever a player attacks with three or more creatures, you draw a card.\n" +
        "Whenever a player attacks with five or more creatures, Aurelia deals 3 damage to each " +
        "of your opponents and you gain 3 life."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.HASTE)

    triggeredAbility {
        trigger = TriggerSpec(
            event = AttackEvent(
                requires = setOf(AttackPredicate.AttackerCountAtLeast(3))
            ),
            binding = TriggerBinding.ANY
        )
        effect = Effects.DrawCards(1)
        description = "Whenever a player attacks with three or more creatures, you draw a card."
    }

    triggeredAbility {
        trigger = TriggerSpec(
            event = AttackEvent(
                requires = setOf(AttackPredicate.AttackerCountAtLeast(5))
            ),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(
            Effects.DealDamage(
                3,
                EffectTarget.PlayerRef(Player.EachOpponent),
                damageSource = EffectTarget.Self
            ),
            Effects.GainLife(3)
        )
        description = "Whenever a player attacks with five or more creatures, Aurelia deals 3 " +
            "damage to each of your opponents and you gain 3 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "188"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f80c6e7-e9f9-4ca6-87f7-a52c96079e4a.jpg?1783912856"

        ruling(
            "2024-02-02",
            "For both triggered abilities, it doesn't matter what happens to the attacking " +
                "creatures in response. As long as a player attacked with at least the " +
                "appropriate number of creatures, the effects of Aurelia's triggered abilities " +
                "will still occur."
        )
    }
}
