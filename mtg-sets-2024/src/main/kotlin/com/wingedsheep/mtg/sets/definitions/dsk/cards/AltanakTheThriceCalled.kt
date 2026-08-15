package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Altanak, the Thrice-Called
 * {5}{G}{G}
 * Legendary Creature — Insect Beast
 * 9/9
 *
 * Trample
 * Whenever Altanak becomes the target of a spell or ability an opponent controls, draw a card.
 * {1}{G}, Discard this card: Return target land card from your graveyard to the battlefield tapped.
 *
 * The last ability functions from hand: its cost discards this card ([Costs.DiscardSelf]) and it's
 * activated from the hand zone ([activateFromZone] = [Zone.HAND]), so you can ramp by pitching the
 * fatty when it's stranded in hand.
 */
val AltanakTheThriceCalled = card("Altanak, the Thrice-Called") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Insect Beast"
    power = 9
    toughness = 9
    oracleText = "Trample\nWhenever Altanak becomes the target of a spell or ability an opponent " +
        "controls, draw a card.\n{1}{G}, Discard this card: Return target land card from your " +
        "graveyard to the battlefield tapped."

    keywords(Keyword.TRAMPLE)

    // Whenever Altanak becomes the target of a spell or ability an opponent controls, draw a card.
    triggeredAbility {
        trigger = Triggers.BecomesTargetByOpponent
        effect = Effects.DrawCards(1)
    }

    // {1}{G}, Discard this card: Return target land card from your graveyard to the battlefield tapped.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        val t = target(
            "land",
            TargetObject(filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.PutOntoBattlefield(t, tapped = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "166"
        artist = "Sam Wolfe Connelly"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/807b0674-8eba-4204-b6b6-fa2b785a79e9.jpg?1726286473"
    }
}
