package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Merfolk Seastalkers
 * {3}{U}
 * Creature — Merfolk Scout
 * 2/3
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 * {2}{U}: Tap target creature without flying.
 *
 * "Without flying" is a targeting restriction, so it is checked against *projected* state both
 * when the ability is activated and again as it resolves.
 */
val MerfolkSeastalkers = card("Merfolk Seastalkers") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Scout"
    power = 2
    toughness = 3
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)\n" +
        "{2}{U}: Tap target creature without flying."

    keywords(Keyword.ISLANDWALK)

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val creature = target(
            "creature without flying",
            TargetCreature(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING)),
        )
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "55"
        artist = "Eric Deschamps"
        flavorText = "\"Do they seek knowledge or wealth? Are they bandits or benefactors? It depends on who is chanting the tale.\"\n—Nikou, Joraga bard"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fe5d069-45a0-46ea-b3ec-4e75f0531382.jpg"
    }
}
