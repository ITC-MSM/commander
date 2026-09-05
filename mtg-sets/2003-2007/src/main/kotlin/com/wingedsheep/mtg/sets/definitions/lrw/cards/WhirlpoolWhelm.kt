package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect

val WhirlpoolWhelm = card("Whirlpool Whelm") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Clash with an opponent, then return target creature to its owner's hand. If you win, you may put that creature on top of its owner's library instead. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        val bounce = Effects.ReturnToHand(creature)
        effect = Patterns.Mechanic.clash(
            ifYouWin = GatedEffect(
                gate = Gate.MayDecide(prompt = "Put that creature on top of its owner's library?"),
                then = Effects.PutOnTopOfLibrary(creature),
                otherwise = bounce
            ),
            otherwise = bounce
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Cyril Van Der Haegen"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/1934c34a-c163-4fac-a80d-8b17e8d64efa.jpg?1783942895"
        ruling("2007-10-01", "You choose the target when you play Whirlpool Whelm, not when you clash.")
        ruling("2007-10-01", "If you don't win the clash, the targeted creature always goes to its owner's hand. If you win the clash, you choose whether the creature goes to its owner's hand or the top of its owner's library.")
        ruling("2007-10-01", "If you clash with the owner of the targeted creature and win, the owner of the creature decides where to put to card revealed during the clash before you decide whether to put the creature on top of that library. You know the player's decision before you make your decision.")
    }
}
