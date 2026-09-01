package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Arbiter of Knollridge
 * {6}{W}
 * Creature — Giant Wizard
 * 5/5
 *
 * Vigilance
 * When this creature enters, each player's life total becomes the highest life total among
 * all players.
 *
 * "The highest life total among all players" is a per-player maximum, not a sum, so it reads
 * [DynamicAmount.GreatestAmongPlayers] — that combinator rebinds the measured player for each
 * seat, letting the inner `LifeTotal(Player.You)` mean "that player's life". The amount is
 * re-evaluated per player as the effect walks the seats, which is safe here precisely because
 * setting everyone to the maximum can only ever raise a life total: the maximum is a fixed
 * point of the operation, so every player lands on the same number regardless of order.
 */
val ArbiterOfKnollridge = card("Arbiter of Knollridge") {
    manaCost = "{6}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant Wizard"
    power = 5
    toughness = 5
    oracleText = "Vigilance\n" +
        "When this creature enters, each player's life total becomes the highest life total " +
        "among all players."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.SetLifeTotal(
            amount = DynamicAmount.GreatestAmongPlayers(
                players = Player.Each,
                inner = DynamicAmount.LifeTotal(Player.You)
            ),
            target = EffectTarget.PlayerRef(Player.Each)
        )
        description = "each player's life total becomes the highest life total among all players."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "2"
        artist = "Brandon Dorman"
        flavorText = "Though giants are mortal, they live so long and on such a grand scale " +
            "that many small folk don't believe they ever truly die."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b598cf2-c327-422b-9527-1b28645ba70c.jpg?1783942919"
        ruling("2010-06-15", "In a Two-Headed Giant game, each team chooses one person on that team to gain the appropriate amount of life. This basically means that the triggered ability will set the life total of each team to that of the team with the highest life total.")
        ruling("2007-10-01", "Abilities that trigger when a player gains life may trigger as a result of this ability.")
        ruling("2007-10-01", "In other multiplayer formats, each player's life total is set to the highest life total within their range of influence. These changes happen at the same time. For example, if each player has a range of influence of one, and the players in the game are Alice (4 life), Barry (8 life), Carrie (11 life), and Doug (7 life), in that order, then Alice's life total will become 8 and Barry and Doug's life totals will each become 11.")
    }
}
