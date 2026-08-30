package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sell-Sword Brute
 * {1}{R}
 * Creature — Human Mercenary
 * 2/2
 *
 * When this creature dies, it deals 2 damage to you.
 *
 * "You" is the controller of the triggered ability — the player who controlled Sell-Sword
 * Brute as it left the battlefield, per last-known information.
 */
val SellSwordBrute = card("Sell-Sword Brute") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Mercenary"
    oracleText = "When this creature dies, it deals 2 damage to you."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.You))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Jeff Miracola"
        flavorText = "\"Killing is easy. Just wrap your hand around the haft, and wrap your enemy around the blade.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db513835-af5a-4ff3-8cf8-31936732a4db.jpg?1783943647"
    }
}
