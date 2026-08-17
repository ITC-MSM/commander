package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCantLoseGame
import com.wingedsheep.sdk.scripting.GrantOpponentsCantWinGame

/**
 * Platinum Angel — Mirrodin #228
 * {7} · Artifact Creature — Angel · 4/4 · Rare
 *
 * Flying
 * You can't lose the game and your opponents can't win the game.
 *
 * The printed sentence is two independent restrictions, so it is two static abilities — the same
 * pair Herald of Eternal Dawn (FDN) and Lich's Mastery (DOM) use. [GrantCantLoseGame] suppresses
 * every loss condition for the controller (0-or-less life, poison, drawing from an empty library,
 * and "you lose the game" effects), while [GrantOpponentsCantWinGame] makes any effect that would
 * hand an opponent the win do nothing at all.
 *
 * Both are anchored to the permanent, so the lock ends the instant the Angel leaves the battlefield
 * — a player at -20 life loses to the state-based action on the very next check.
 */
val PlatinumAngel = card("Platinum Angel") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Angel"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "You can't lose the game and your opponents can't win the game."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = GrantCantLoseGame
    }
    staticAbility {
        ability = GrantOpponentsCantWinGame
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Brom"
        flavorText = "In its heart lies the secret of immortality."
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59bb5aee-b334-4c24-875b-56751d4add02.jpg?1783944508"

        ruling(
            "2009-10-01",
            "No game effect can cause you to lose the game or cause any opponent to win the game " +
                "while you control Platinum Angel. It doesn't matter whether you have 0 or less life, " +
                "you're forced to draw a card while your library is empty, or you have ten or more " +
                "poison counters. You keep playing."
        )
        ruling(
            "2004-12-01",
            "Effects that say the game is a draw are not affected by Platinum Angel. They'll still work."
        )
        ruling(
            "2004-12-01",
            "You can concede a game while Platinum Angel is on the battlefield. A concession causes " +
                "you to leave the game, which then causes you to lose the game."
        )
    }
}
