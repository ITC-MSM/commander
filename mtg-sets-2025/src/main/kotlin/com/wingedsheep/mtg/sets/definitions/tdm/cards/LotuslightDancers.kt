package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Lotuslight Dancers — Tarkir: Dragonstorm #204
 * {2}{B}{G}{U} · Creature — Zombie Bard · 3/6
 *
 * Lifelink
 * When this creature enters, search your library for a black card, a green card, and a blue card.
 * Put those cards into your graveyard, then shuffle.
 *
 * The ETB is modelled as three sequential single-card library searches — one per color filter —
 * each via the atomic [Patterns.Library.searchLibrary] gather→select→move pipeline to the graveyard.
 * Only the final search shuffles, matching the single "then shuffle" at the end of the oracle text.
 * Each search removes its found card from the library before the next runs, so a card cannot be
 * chosen twice; a multicolor card (e.g. black-green) can satisfy whichever single color search the
 * player picks it for.
 */
val LotuslightDancers = card("Lotuslight Dancers") {
    manaCost = "{2}{B}{G}{U}"
    colorIdentity = "BGU"
    typeLine = "Creature — Zombie Bard"
    power = 3
    toughness = 6
    oracleText = "Lifelink\n" +
        "When this creature enters, search your library for a black card, a green card, and a blue " +
        "card. Put those cards into your graveyard, then shuffle."

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withColor(Color.BLACK),
            count = 1,
            destination = SearchDestination.GRAVEYARD,
            shuffleAfter = false
        ).then(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any.withColor(Color.GREEN),
                count = 1,
                destination = SearchDestination.GRAVEYARD,
                shuffleAfter = false
            )
        ).then(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any.withColor(Color.BLUE),
                count = 1,
                destination = SearchDestination.GRAVEYARD,
                shuffleAfter = true
            )
        )
        description = "When this creature enters, search your library for a black card, a green card, " +
            "and a blue card. Put those cards into your graveyard, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "204"
        artist = "Jodie Muir"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82aa2593-4a79-46f1-a2bd-b71fb504d0ab.jpg?1743204801"
    }
}
