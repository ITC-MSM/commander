package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Tyrant's Scorn — War of the Spark #225 (canonical printing)
 * {U}{B}
 * Instant
 * Choose one —
 * • Destroy target creature with mana value 3 or less.
 * • Return target creature to its owner's hand.
 *
 * Each mode binds its own creature target because the two differ in what they will accept: the
 * destroy mode carries the mana-value cap, the bounce mode takes any creature. Modal targets are
 * chosen for the *chosen* mode only (CR 601.2b), so the cap never restricts the bounce.
 */
val TyrantsScorn = card("Tyrant's Scorn") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target creature with mana value 3 or less.\n" +
        "• Return target creature to its owner's hand."

    spell {
        modal {
            mode("Destroy target creature with mana value 3 or less.") {
                val creature = target("target", TargetCreature(filter = TargetFilter.Creature.manaValueAtMost(3)))
                effect = Effects.Destroy(creature)
            }
            mode("Return target creature to its owner's hand.") {
                val creature = target("target", Targets.Creature)
                effect = Effects.ReturnToHand(creature)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Svetlin Velinov"
        flavorText = "\"The hero with the magic sword slays the dragon? Not this time.\"\n—Nicol Bolas"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7e2708c-2824-4925-b529-d625deb77924.jpg"
    }
}
