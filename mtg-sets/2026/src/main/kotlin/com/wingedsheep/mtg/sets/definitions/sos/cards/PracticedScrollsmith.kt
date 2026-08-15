package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Practiced Scrollsmith
 * {R}{R/W}{W}
 * Creature — Dwarf Cleric
 * 3/2
 * First strike
 * When this creature enters, exile target noncreature, nonland card from your graveyard.
 * Until the end of your next turn, you may cast that card.
 */
val PracticedScrollsmith = card("Practiced Scrollsmith") {
    manaCost = "{R}{R/W}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Dwarf Cleric"
    power = 3
    toughness = 2
    oracleText = "First strike\n" +
        "When this creature enters, exile target noncreature, nonland card from your graveyard. " +
        "Until the end of your next turn, you may cast that card."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target(
            "target noncreature, nonland card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = (GameObjectFilter.Noncreature and GameObjectFilter.Nonland).ownedByYou(),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.ChosenTargets,
                    storeAs = "exiledCard",
                ),
                MoveCollectionEffect(
                    from = "exiledCard",
                    destination = CardDestination.ToZone(Zone.EXILE),
                ),
                GrantMayPlayFromExileEffect("exiledCard", MayPlayExpiry.UntilEndOfNextTurn),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Loïc Canavaggia"
        flavorText = "\"Every weapon has a story. Let me tell you this one's.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40075e3f-58b3-47fd-8fbe-4b301e9ce7a1.jpg?1775938459"
    }
}
