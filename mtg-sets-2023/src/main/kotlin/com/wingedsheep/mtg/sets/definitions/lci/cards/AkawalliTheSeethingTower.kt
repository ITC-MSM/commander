package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Akawalli, the Seething Tower (LCI #220) — {1}{B}{G} Legendary Creature — Fungus (uncommon)
 * 3/3
 *
 * Descend 4 — As long as there are four or more permanent cards in your graveyard, Akawalli
 *   gets +2/+2 and has trample.
 * Descend 8 — As long as there are eight or more permanent cards in your graveyard, Akawalli
 *   gets an additional +2/+2 and can't be blocked by more than one creature.
 *
 * Implementation notes:
 * - Both descend clauses are "as long as" *static* buffs, so each is a self-scoped
 *   ([Filters.Self]) static gated by [Conditions.CardsInGraveyardMatchingAtLeast] with
 *   [GameObjectFilter.Permanent] (the Basking Capybara idiom). The `condition` field on the
 *   `staticAbility { }` block auto-wraps in a ConditionalStaticAbility.
 * - Descend 4 grants +2/+2 ([ModifyStats]) and trample ([GrantKeyword]); Descend 8 grants an
 *   *additional* +2/+2 (stacks with the descend-4 [ModifyStats] when ≥8 cards are present, since
 *   ≥8 implies ≥4) plus [CantBeBlockedByMoreThan] with `maxBlockers = 1`.
 */
val AkawalliTheSeethingTower = card("Akawalli, the Seething Tower") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Fungus"
    power = 3
    toughness = 3
    oracleText = "Descend 4 — As long as there are four or more permanent cards in your graveyard, " +
        "Akawalli gets +2/+2 and has trample.\n" +
        "Descend 8 — As long as there are eight or more permanent cards in your graveyard, Akawalli " +
        "gets an additional +2/+2 and can't be blocked by more than one creature."

    // Descend 4 — +2/+2 and trample.
    staticAbility {
        condition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        ability = ModifyStats(+2, +2, Filters.Self)
    }
    staticAbility {
        condition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.Self)
    }

    // Descend 8 — an additional +2/+2 and can't be blocked by more than one creature.
    staticAbility {
        condition = Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent)
        ability = ModifyStats(+2, +2, Filters.Self)
    }
    staticAbility {
        condition = Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent)
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3ee62dd1-97d0-4e5d-8937-26c9e51e9414.jpg?1782694434"
    }
}
