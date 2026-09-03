package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Duskmantle Operative — War of the Spark #88 (canonical printing)
 * {1}{B}
 * Creature — Human Rogue
 * 2 / 2
 * This creature can't be blocked by creatures with power 4 or greater.
 *
 * The mirror of Arlinn's Wolf: a printed [CantBeBlockedBy] static, here keyed on
 * [GameObjectFilter.Creature.powerAtLeast] — "power 4 or greater" is the lower bound, not an upper
 * one. The ability's own `filter` defaults to the source permanent, which is what "this creature"
 * means, and the restriction is checked once at block declaration.
 */
val DuskmantleOperative = card("Duskmantle Operative") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    oracleText = "This creature can't be blocked by creatures with power 4 or greater."
    power = 2
    toughness = 2

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtLeast(4))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Anna Steinbauer"
        flavorText = "Gideon eyed the Dimir emissary warily. \"Could your agents slip inside Bolas's citadel?\" The figure met his gaze with icy resolve. \"We already have.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16eb5a6b-5e69-497c-a0c9-4165ad0f5d0b.jpg"
    }
}
