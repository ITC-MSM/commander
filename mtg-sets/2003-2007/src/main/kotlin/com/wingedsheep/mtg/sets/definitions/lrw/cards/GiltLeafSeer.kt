package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gilt-Leaf Seer
 * {2}{G}
 * Creature — Elf Shaman
 * 2/2
 * {G}, {T}: Look at the top two cards of your library, then put them back in any order.
 */
val GiltLeafSeer = card("Gilt-Leaf Seer") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 2
    toughness = 2
    oracleText = "{G}, {T}: Look at the top two cards of your library, then put them back in any order."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        effect = Patterns.Library.lookAtTopAndReorder(2)
        description = "{G}, {T}: Look at the top two cards of your library, then put them back in any order."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "215"
        artist = "Darrell Riche"
        flavorText = "Desmera blinded her seers so that her beauty would be the last image burned in their memories. The act only deepened their insight."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b4cc889-9bc1-48dd-b1d1-95daa73b8fbe.jpg?1783942864"
    }
}
