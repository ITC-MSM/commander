package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * The Regalia
 * {4}
 * Legendary Artifact — Vehicle
 * 4/4
 * Haste
 * Whenever The Regalia attacks, reveal cards from the top of your library until you reveal a land
 *   card. Put that card onto the battlefield tapped and the rest on the bottom of your library in a
 *   random order.
 * Crew 1
 *
 * The attack trigger is a reveal-until-land pipeline: [GatherUntilMatchEffect] reveals from the top
 * until the first land (stops at one match), publishing everything seen into `allRevealed`.
 * [FilterCollectionEffect] then splits that into the land (`landCard`) and the cards revealed before
 * it (`rest`); the land is put onto the battlefield tapped and the rest go to the bottom of the
 * library in a random order. If the library has no land, no match is found — `landCard` is empty (no
 * permanent enters) and every revealed card goes to the bottom, matching the printed fallback.
 */
val TheRegalia = card("The Regalia") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Vehicle"
    oracleText = "Haste\n" +
        "Whenever The Regalia attacks, reveal cards from the top of your library until you reveal a " +
        "land card. Put that card onto the battlefield tapped and the rest on the bottom of your " +
        "library in a random order.\n" +
        "Crew 1"
    power = 4
    toughness = 4
    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            GatherUntilMatchEffect(
                filter = GameObjectFilter.Land,
                storeMatch = "matchLand",
                storeRevealed = "allRevealed"
            ),
            RevealCollectionEffect(from = "allRevealed"),
            FilterCollectionEffect(
                from = "allRevealed",
                filter = CollectionFilter.MatchesFilter(GameObjectFilter.Land),
                storeMatching = "landCard",
                storeNonMatching = "rest"
            ),
            MoveCollectionEffect(
                from = "landCard",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped)
            ),
            MoveCollectionEffect(
                from = "rest",
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random
            )
        )
    }

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "267"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dc420e79-c483-474f-97cd-e9c6a636c306.jpg?1748706782"
    }
}
