package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Izzet Chronarch
 * {3}{U}{R}
 * Creature — Human Wizard
 * 2/2
 * When this creature enters, return target instant or sorcery card from your graveyard to your hand.
 */
val IzzetChronarch = card("Izzet Chronarch") {
    manaCost = "{3}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, return target instant or sorcery card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        // "from your graveyard" — restrict to instant/sorcery cards you own (oracle wording).
        val t = target("target", TargetObject(filter = TargetFilter.InstantOrSorceryInGraveyard.ownedByYou()))
        effect = Effects.Move(
            target = t,
            destination = Zone.HAND
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Nick Percival"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c82dc47-38db-4ee1-9031-f8ea68d05389.jpg?1593272708"
    }
}
