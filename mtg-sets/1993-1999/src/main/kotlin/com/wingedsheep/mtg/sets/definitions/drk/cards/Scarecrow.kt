package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Scarecrow
 * {5}
 * Artifact Creature — Scarecrow
 * 2/2
 * {6}, {T}: Prevent all damage that would be dealt to you this turn by creatures with flying.
 *
 * The shield protects the controller, not Scarecrow itself, and it is not combat-only: an
 * activated ability of a flying creature that damages you is prevented too. The source filter is
 * re-read from projected state at damage time, so a creature that gains or loses flying after the
 * ability resolves is judged as it is when the damage happens.
 */
val Scarecrow = card("Scarecrow") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    power = 2
    toughness = 2
    oracleText = "{6}, {T}: Prevent all damage that would be dealt to you this turn by creatures with flying."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap)
        effect = Effects.PreventAllDamageToYouFrom(
            GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "Anson Maddocks"
        flavorText = "There was more malice in its button eyes than should have been possible in something that had never known life."
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93850e74-744c-4261-a84e-01eaced6e49a.jpg?1783947925"
    }
}
