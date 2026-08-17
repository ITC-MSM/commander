package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mindslaver
 * {6}
 * Legendary Artifact
 *
 * {4}, {T}, Sacrifice Mindslaver: You control target player during that player's next turn.
 */
val Mindslaver = card("Mindslaver") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "{4}, {T}, Sacrifice Mindslaver: You control target player during that player's " +
        "next turn. (You see all cards that player could see and make all decisions for them.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap, Costs.SacrificeSelf)
        val player = target("target player", Targets.Player)
        effect = Effects.HijackNextTurn(player)
        description = "{4}, {T}, Sacrifice Mindslaver: You control target player during that " +
            "player's next turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "206"
        artist = "Glen Angus"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98fb1eaa-2871-491a-a4f5-3e358778ba40.jpg?1783944513"

        ruling("2024-04-12", "The player you're controlling is still the active player during that turn.")
        ruling("2024-04-12", "You only control the player. You don't control any of that player's permanents, spells, or abilities.")
        ruling("2024-04-12", "You can use only the affected player's resources (cards, mana, and so on) to pay costs for that player; you can't use your own. Similarly, you can use the affected player's resources only to pay that player's costs; you can't spend them on your costs.")
        ruling("2024-04-12", "If the targeted player skips their next turn, you'll control the next turn the affected player actually takes.")
        ruling("2024-04-12", "Multiple player-controlling effects that affect the same player overwrite each other. The last one to be created is the one that works.")
    }
}
