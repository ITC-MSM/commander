package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dictate of Kruphix
 * {1}{U}{U}
 * Enchantment
 * Flash
 * At the beginning of each player's draw step, that player draws an additional card.
 *
 * The additional draw is a triggered ability (not a replacement), fired on every player's
 * draw step. [Player.TriggeringPlayer] resolves to the player whose draw step it is (the
 * active player of that step), matching the "that player" wording — see Collapsing Borders
 * for the same each-player step-trigger shape.
 */
val DictateOfKruphix = card("Dictate of Kruphix") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "At the beginning of each player's draw step, that player draws an additional card."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.phase(Step.DRAW, Player.Each)
        effect = Effects.DrawCards(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Daarken"
        flavorText = "\"Knowledge is cruel. It will break your heart and test your allegiances. " +
            "Are you certain you want this curse?\""
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8e7916c-f39a-48a0-a47d-7e83ebf028fa.jpg?1782713429"
    }
}
