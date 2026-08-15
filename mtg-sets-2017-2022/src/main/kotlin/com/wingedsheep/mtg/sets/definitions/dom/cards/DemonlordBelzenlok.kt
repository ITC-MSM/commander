package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Demonlord Belzenlok
 * {4}{B}{B}
 * Legendary Creature — Elder Demon
 * 6/6
 * Flying, trample
 * When Demonlord Belzenlok enters, exile cards from the top of your library
 * until you exile a nonland card, then put that card into your hand. If the
 * card's mana value is 4 or greater, repeat this process. Demonlord Belzenlok
 * deals 1 damage to you for each card put into your hand this way.
 */
val DemonlordBelzenlok = card("Demonlord Belzenlok") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Elder Demon"
    power = 6
    toughness = 6
    oracleText = "Flying, trample\nWhen Demonlord Belzenlok enters, exile cards from the top of your library until you exile a nonland card, then put that card into your hand. If the card's mana value is 4 or greater, repeat this process. Demonlord Belzenlok deals 1 damage to you for each card put into your hand this way."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ExileFromTopRepeating(
            repeatIfManaValueAtLeast = 4,
            damagePerCard = 1
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "86"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ffb4dbe8-15fa-467f-9366-66382b192113.jpg?1562746442"
        ruling("2018-04-27", "Once the triggered ability resolves, the ability will continue until you either exile a nonland card with mana value 3 or less or fail to exile any nonland cards while performing the process.")
        ruling("2018-04-27", "Land cards exiled this way remain exiled.")
        ruling("2018-04-27", "Demonlord Belzenlok's ability causes it to deal an amount of damage to you all at once; it doesn't deal 1 damage multiple times.")
        ruling("2018-04-27", "If the mana cost of the nonland card includes {X}, X is considered to be 0.")
    }
}
