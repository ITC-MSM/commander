package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Master Pakku
 * {1}{U}
 * Legendary Creature — Human Advisor Ally
 * 1/3
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Whenever Master Pakku becomes tapped, target player mills X cards, where X is the number of
 * Lesson cards in your graveyard. (They put the top X cards of their library into their graveyard.)
 */
val MasterPakku = card("Master Pakku") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Advisor Ally"
    power = 1
    toughness = 3
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Whenever Master Pakku becomes tapped, target player mills X cards, where X is the number of " +
        "Lesson cards in your graveyard. (They put the top X cards of their library into their graveyard.)"

    prowess()

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        val player = target("target player", Targets.Player)
        effect = Patterns.Library.mill(
            DynamicAmounts.zone(
                Player.You,
                Zone.GRAVEYARD,
                GameObjectFilter.Any.withSubtype(Subtype.LESSON)
            ).count(),
            player
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Olena Richards"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91e30df5-63f7-4281-8d64-8d522d663652.jpg?1764120366"
    }
}
