package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Ranger of Eos
 * {3}{W}
 * Creature — Human Soldier Ranger
 * 3 / 2
 * When this creature enters, you may search your library for up to two creature cards with mana value 1 or less, reveal them, put them into your hand, then shuffle.
 *
 * The whole printed search is one [Patterns.Library].`searchLibrary` call: the gather/select/move
 * pipeline, the trailing shuffle and the CR 701.23 "searched their library" event all come from its
 * defaults, with `count = 2` giving the "up to two" and `reveal = true` the reveal. The cap is a
 * filter predicate — `GameObjectFilter.Creature.manaValueAtMost(1)` — and `optional = true` lowers
 * the printed "you may" into a [com.wingedsheep.sdk.scripting.effects.Gate.MayDecide] gate around it.
 */
val RangerOfEos = card("Ranger of Eos") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier Ranger"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, you may search your library for up to two creature cards with mana value 1 or less, reveal them, put them into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.manaValueAtMost(1),
            count = 2,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Volkan Baǵa"
        flavorText = "At his side, humble beasts become weapons more deadly than sharpened steel."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a30ee26-5f78-4ac2-9105-1baa9ece8a21.jpg"
    }
}
