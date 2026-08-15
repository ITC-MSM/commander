package com.wingedsheep.mtg.sets.definitions.c13.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Angel of Finality
 * {3}{W}
 * Creature — Angel
 * Flying
 * When this creature enters, exile target player's graveyard.
 *
 * The whole-graveyard exile is a gather-then-move over the target player's graveyard
 * (Cranial Archive / Lantern of the Lost shape), so it correctly handles an empty
 * graveyard and cards owned by any player.
 */
val AngelOfFinality = card("Angel of Finality") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 4
    oracleText =
        "Flying\n" +
        "When this creature enters, exile target player's graveyard."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target player", Targets.Player)
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.GRAVEYARD, Player.TargetPlayer),
                storeAs = "targetGraveyard",
            ),
            MoveCollectionEffect(
                from = "targetGraveyard",
                destination = CardDestination.ToZone(Zone.EXILE),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Howard Lyon"
        flavorText = "\"Better the dead depart utterly than be enslaved.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd3c34c9-2072-4ebb-93ef-34173015bfb8.jpg?1783939693"
    }
}
