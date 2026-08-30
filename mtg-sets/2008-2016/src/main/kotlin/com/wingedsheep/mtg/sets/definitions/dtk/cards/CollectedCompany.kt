package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Collected Company
 * {3}{G}
 * Instant
 *
 * Look at the top six cards of your library. Put up to two creature cards with mana value 3 or less from among them onto the battlefield. Put the rest on the bottom of your library in any order.
 *
 * Three printed sentences, one recipe: [Patterns.Library.lookAtTopAndTakeMatching] is exactly this
 * shape — look at the top six privately, take *up to two* of the cards that match
 * ([SelectionMode.ChooseUpTo] over creatures capped at mana value 3; the recipe shows every
 * looked-at card and only the matching ones are selectable), put those onto the battlefield, and
 * bottom the remainder. "In any order" is the caster's choice, so the rest go back under
 * [CardOrder.ControllerChooses] — not [CardOrder.Random], which is the *other* printed wording
 * (Cartographer's Survey).
 */
val CollectedCompany = card("Collected Company") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Look at the top six cards of your library. Put up to two creature cards with mana value 3 or less from among them onto the battlefield. Put the rest on the bottom of your library in any order."

    spell {
        effect = Patterns.Library.lookAtTopAndTakeMatching(
            count = DynamicAmount.Fixed(6),
            filter = GameObjectFilter.Creature.manaValueAtMost(3),
            prompt = "Put up to two creature cards with mana value 3 or less from among them onto the battlefield",
            selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
            keepDestination = CardDestination.ToZone(Zone.BATTLEFIELD),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "177"
        artist = "Franz Vohwinkel"
        flavorText = "Many can stand where one would fall."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfa7b456-7e83-4587-a875-9b35fde318c2.jpg?1783938582"
    }
}
