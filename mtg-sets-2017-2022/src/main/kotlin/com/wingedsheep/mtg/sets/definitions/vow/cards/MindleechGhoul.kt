package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.exploit
import com.wingedsheep.sdk.model.Rarity

/**
 * Mindleech Ghoul
 * {1}{B}
 * Creature — Zombie
 * 2/2
 * Exploit (When this creature enters, you may sacrifice a creature.)
 * When this creature exploits a creature, each opponent exiles a card from their hand.
 *
 * The payoff is untargeted (an each-opponent effect resolved at resolution time), so it's baked
 * straight into the exploit reflexive ([exploit]'s `onExploit`). [Effects.EachOpponentExilesFromHand]
 * iterates each opponent, who chooses one card from their own hand to exile.
 */
val MindleechGhoul = card("Mindleech Ghoul") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 2
    oracleText = "Exploit (When this creature enters, you may sacrifice a creature.)\n" +
        "When this creature exploits a creature, each opponent exiles a card from their hand."

    exploit(
        onExploit = Effects.EachOpponentExilesFromHand(1)
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Alex Brock"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5c4e00d-128a-4ddb-9e1b-3ee93121b262.jpg?1783924857"
    }
}
