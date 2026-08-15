package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventActivatedAbilities

/**
 * Cursed Totem
 * {2}
 * Artifact
 * Activated abilities of creatures can't be activated.
 *
 * Rulings:
 * - Doesn't stop activating abilities of creature cards in zones other than the battlefield
 *   (cycling, unearth, etc.) — those aren't abilities of *creatures*.
 * - Stops mana abilities of creatures as well.
 * - Static and triggered abilities of creatures are unaffected.
 * - A Vehicle's Crew ability can still be activated; once the Vehicle becomes a creature, its
 *   activated abilities can't be activated.
 */
val CursedTotem = card("Cursed Totem") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Activated abilities of creatures can't be activated."

    staticAbility {
        ability = PreventActivatedAbilities(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "299"
        artist = "D. Alexander Gregory"
        flavorText = "Pass me from soul to soul / soldier to herder, herder to beast, beast to soil / until I am everywhere. / Then pass me those souls. —Totem inscription (translated)"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc99ee76-45b6-4f1d-b0b0-7da8775ca90c.jpg?1562721911"
    }
}
