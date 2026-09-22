package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val TinybonesPocketNuisance = card("Tinybones, Pocket Nuisance") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Skeleton Rogue"
    oracleText = "When Tinybones enters, each opponent discards a card.\n" +
        "Whenever a player discards one or more cards, Tinybones deals 1 damage to each opponent."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.eachOpponentDiscards(1)
        description = "When Tinybones enters, each opponent discards a card."
    }

    triggeredAbility {
        trigger = Triggers.discards(player = Player.Each, batch = true)
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
        description = "Whenever a player discards one or more cards, Tinybones deals 1 damage to each opponent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Jesper Ejsing"
        flavorText = "No score is too small, no coin purse too big."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f47ddf7-35b6-4205-8045-f057914c5f64.jpg?1788329294"
        inBooster = false
    }
}
