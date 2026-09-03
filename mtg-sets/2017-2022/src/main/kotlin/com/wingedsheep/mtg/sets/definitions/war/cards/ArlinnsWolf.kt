package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Arlinn's Wolf — War of the Spark #151 (canonical printing)
 * {2}{G}
 * Creature — Wolf
 * 3 / 2
 * This creature can't be blocked by creatures with power 2 or less.
 *
 * The evasion is a printed [CantBeBlockedBy] static keyed on blocker power via
 * [GameObjectFilter.Creature.powerAtMost]. The ability's own `filter` defaults to the source
 * permanent, which is what "this creature" means here — the same shape as Stormkeld Vanguard.
 * The restriction is enforced at block declaration, so pumping a power-2 blocker after blocks are
 * declared doesn't undo the block.
 */
val ArlinnsWolf = card("Arlinn's Wolf") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    oracleText = "This creature can't be blocked by creatures with power 2 or less."
    power = 3
    toughness = 2

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Kimonas Theodossiou"
        flavorText = "\"If you don't speak wolf, allow me to translate: 'One step closer and I'll rip out your throat.'\"\n—Arlinn Kord"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/747223a5-c669-4d0e-a062-265eb47710cd.jpg"
    }
}
