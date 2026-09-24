package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Al-abara's Carpet
 * {5}
 * Artifact
 *
 * {5}, {T}: Prevent all damage that would be dealt to you this turn by attacking creatures without flying.
 *
 * The recipient is the controller alone: [Effects.PreventAllDamageToYouFrom] is the recipient-side
 * shield naming only "you", narrowed to damage from attacking creatures without flying. Damage those
 * creatures deal to anything else (a blocker, a planeswalker) is not prevented. Both halves are
 * re-read when damage would be dealt, so a creature that gains flying mid-combat is no longer covered.
 */
val AlabarasCarpet = card("Al-abara's Carpet") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{5}, {T}: Prevent all damage that would be dealt to you this turn by attacking creatures " +
        "without flying."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Effects.PreventAllDamageToYouFrom(
            GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING).attacking())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "271"
        artist = "Kaja Foglio"
        flavorText = "Al-abara simply laughed and lifted one finger, and the carpet carried her high out of our " +
            "reach."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d5aae6e-fe20-4363-9589-5a54bcbbb77e.jpg?1783948031"
    }
}
