package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.TransformPermanent

/**
 * Witness Protection
 * {U}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature loses all abilities and is a green and white Citizen creature with base
 * power and toughness 1/1 named Legitimate Businessperson. (It loses all other colors, card
 * types, creature types, and names.)
 *
 * Modeled as a stack of statics on the enchanted creature, mirroring Retro-Mutation /
 * Unable to Scream / Sugar Coat's "becomes a different thing entirely" shape:
 *  - [TransformPermanent] bundles the Layer 3 (TEXT) name overwrite, Layer 4 (TYPE) subtype
 *    replacement (keeps the CREATURE card type, replaces all subtypes with Citizen), and
 *    Layer 5 (COLOR) color overwrite (green/white) — `setName` is the new bit this card
 *    needed; see [com.wingedsheep.sdk.scripting.TransformPermanent.setName].
 *  - [SetBasePowerToughnessStatic] 1/1 (Layer 7b).
 *  - [LoseAllAbilities] (Layer 6).
 *
 * Note: "loses all other card types" does not touch supertypes — CR 205.4b: "Changing an
 * object's card types or subtypes won't change its supertypes" — so a Legendary creature hit
 * by this stays Legendary. And since every creature this enchants is renamed to the same
 * "Legitimate Businessperson" (CR 201.2a: same name = at least one name in common), two
 * Witness-Protected Legendary creatures under the same control trigger the legend rule against
 * each other (CR 704.5j), exactly as the rules imply.
 *
 * Earliest printing is Streets of New Capenna (2022); this is the canonical definition.
 * Foundations (2024) carries only a [com.wingedsheep.sdk.model.Printing] row.
 */
val WitnessProtection = card("Witness Protection") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature loses all abilities and is a green and " +
        "white Citizen creature with base power and toughness 1/1 named Legitimate " +
        "Businessperson. (It loses all other colors, card types, creature types, and names.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = TransformPermanent(
            setCardTypes = setOf("CREATURE"),
            setSubtypes = setOf("Citizen"),
            setColors = setOf(Color.GREEN, Color.WHITE),
            setName = "Legitimate Businessperson"
        )
    }

    staticAbility {
        ability = SetBasePowerToughnessStatic(1, 1)
    }

    staticAbility {
        ability = LoseAllAbilities()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2be6f2c-8ad0-402d-a7ca-9fe817e83b72.jpg?1782701623"
    }
}
