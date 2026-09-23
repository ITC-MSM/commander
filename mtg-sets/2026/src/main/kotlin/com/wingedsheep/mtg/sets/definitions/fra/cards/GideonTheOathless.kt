package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gideon the Oathless.
 *
 * "That player" differs between the two triggers: for the entering creature it is the creature's
 * controller as it enters ([EffectTarget.ControllerOfTriggeringEntity] — not its owner, which is
 * who a stolen creature's zone change would name), for the loyalty ability it is the opponent who
 * activated it.
 */
val GideonTheOathless = card("Gideon the Oathless") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Mercenary"
    power = 3
    toughness = 3
    oracleText = "Ward—Discard a card.\n" +
        "Whenever a creature an opponent controls enters, Gideon deals 1 damage to that player.\n" +
        "Whenever an opponent activates a loyalty ability, Gideon deals 1 damage to that player."

    keywordAbility(KeywordAbility.wardDiscard())

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DealDamage(1, EffectTarget.ControllerOfTriggeringEntity)
    }

    triggeredAbility {
        trigger = Triggers.OpponentActivatesLoyaltyAbility
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Manuel Castañón"
        flavorText = "\"The man who was Kytheon Iora, hero of the people, was lost long ago. I serve no one now.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c985b0d1-25bd-4069-aab7-a566ff27a8f6.jpg?1789127990"
        inBooster = false
    }
}
