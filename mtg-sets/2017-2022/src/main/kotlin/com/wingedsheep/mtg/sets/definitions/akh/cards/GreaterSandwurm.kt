package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Greater Sandwurm
 * {5}{G}{G}
 * Creature — Wurm
 * 7/7
 * This creature can't be blocked by creatures with power 2 or less.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The evasion is the standard [CantBeBlockedBy] over [GameObjectFilter.Creature.powerAtMost],
 * which reads projected power so pumped blockers stop qualifying.
 */
val GreaterSandwurm = card("Greater Sandwurm") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 7
    toughness = 7
    oracleText = "This creature can't be blocked by creatures with power 2 or less.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Steven Belledin"
        flavorText = "A sandwurm can lie in wait beneath the sands for years until the slightest tremor alerts it to the presence of prey."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6411d177-45fd-4193-8414-0f7e7846d2b9.jpg"
    }
}
