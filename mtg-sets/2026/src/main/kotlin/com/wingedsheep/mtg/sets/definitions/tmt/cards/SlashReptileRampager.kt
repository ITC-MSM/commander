package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Slash, Reptile Rampager
 * {3}{R}{R}
 * Legendary Creature — Mutant Berserker Turtle
 * 7/5
 *
 * Alliance — Whenever another creature you control enters, Slash
 * deals 2 damage to each opponent.
 * Whenever Slash attacks, create a 2/2 red Mutant creature token.
 */
val SlashReptileRampager = card("Slash, Reptile Rampager") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Mutant Berserker Turtle"
    oracleText = "Alliance — Whenever another creature you control enters, Slash deals 2 damage to each opponent.\nWhenever Slash attacks, create a 2/2 red Mutant creature token."
    power = 7
    toughness = 5

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
        description = "Alliance — Whenever another creature you control enters, Slash deals 2 damage to each opponent."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Mutant"),
            imageUri = "https://cards.scryfall.io/normal/front/5/1/51e33613-7a24-461c-8d9f-12680af4b92a.jpg?1771590526"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "108"
        artist = "Andrew Mar"
        flavorText = "\"Tell 'em you've got a new partner. One who knows the true meaning of being a warrior.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/2/025aafbc-5b24-4f5b-9b4a-81f6b9cb5ef3.jpg?1769006188"
    }
}
