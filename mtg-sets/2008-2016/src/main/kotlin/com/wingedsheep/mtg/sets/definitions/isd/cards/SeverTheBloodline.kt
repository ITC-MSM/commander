package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.namedFromVariable
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource

/**
 * Sever the Bloodline
 * {3}{B}
 * Sorcery
 *
 * Exile target creature and all other creatures with the same name as that creature.
 * Flashback {5}{B}{B}
 *
 * Resolution pipeline mirrors Maelstrom Pulse's name-capture: gather the chosen target,
 * capture its card name, then gather every battlefield creature sharing that name (via
 * [GameObjectFilter.namedFromVariable]) and exile the whole group. The target shares its own
 * name, so it is exiled along with its copies.
 */
val SeverTheBloodline = card("Sever the Bloodline") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Exile target creature and all other creatures with the same name as that creature.\n" +
        "Flashback {5}{B}{B} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        target("target creature", Targets.Creature)
        effect = Effects.Pipeline {
            val chosen = gather(CardSource.ChosenTargets, name = "target")
            val chosenName = storeCardName(chosen, name = "name")
            val sameNamed = gather(
                GameObjectFilter.Creature.namedFromVariable(chosenName),
                name = "sameNamed"
            )
            exile(sameNamed)
        }
    }

    keywordAbility(KeywordAbility.flashback("{5}{B}{B}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "115"
        artist = "Clint Cearley"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c6da820-dfb9-4b61-aff8-56dfc9f4894e.jpg?1782714764"
    }
}
