package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Subterranean Schooner
 * {1}{U}
 * Artifact — Vehicle
 * 3/4
 *
 * Whenever this Vehicle attacks, target creature that crewed it this turn explores. (Reveal the top
 * card of your library. Put that card into your hand if it's a land. Otherwise, put a +1/+1 counter
 * on that creature, then put the card back or put it into your graveyard.)
 * Crew 1
 *
 * The attack trigger targets a creature restricted to the ones that crewed this Vehicle this turn
 * via the source-relative `crewedOrSaddledSourceThisTurn` filter (backed by the engine's
 * CrewSaddleContributorsComponent). That same targeted creature explores (CR 701.44), so the
 * potential +1/+1 counter lands on the crewer, matching the oracle "put a +1/+1 counter on that
 * creature".
 */
val SubterraneanSchooner = card("Subterranean Schooner") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Vehicle"
    power = 3
    toughness = 4
    oracleText = "Whenever this Vehicle attacks, target creature that crewed it this turn explores. " +
        "(Reveal the top card of your library. Put that card into your hand if it's a land. " +
        "Otherwise, put a +1/+1 counter on that creature, then put the card back or put it into " +
        "your graveyard.)\n" +
        "Crew 1"

    keywordAbility(KeywordAbility.crew(1))

    triggeredAbility {
        trigger = Triggers.Attacks
        val crewer = target(
            "target creature that crewed it this turn",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.crewedOrSaddledSourceThisTurn()))
        )
        effect = Effects.Explore(crewer)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "80"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94b6881a-b00e-4e90-92e6-602ed8e0e090.jpg?1782694545"
    }
}
