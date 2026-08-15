package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Watery Grasp
 * {U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature doesn't untap during its controller's untap step.
 * Waterbend {5}: Enchanted creature's owner shuffles it into their library. (While paying a
 * waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)
 *
 * Implementation notes:
 *  - "Enchant creature" → [auraTarget] = [Targets.Creature].
 *  - "Doesn't untap during its controller's untap step" → static
 *    [GrantKeyword] of [AbilityFlag.DOESNT_UNTAP] on the enchanted creature.
 *  - "Waterbend {5}: ..." is an activated ability whose mana cost carries the waterbend
 *    alternative-cost flag ([com.wingedsheep.sdk.scripting.ActivatedAbility] `hasWaterbend`);
 *    the reminder text (tap artifacts/creatures to pay {1} each) is supplied by the flag.
 *    The effect shuffles the enchanted creature into its owner's library via
 *    [Effects.ShuffleIntoLibrary] on [EffectTarget.EnchantedCreature].
 */
val WateryGrasp = card("Watery Grasp") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature doesn't untap during its controller's untap step.\n" +
        "Waterbend {5}: Enchanted creature's owner shuffles it into their library. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. " +
        "Each one pays for {1}.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name)
    }

    // Waterbend {5}: Enchanted creature's owner shuffles it into their library.
    activatedAbility {
        cost = Costs.Mana("{5}")
        hasWaterbend = true
        effect = Effects.ShuffleIntoLibrary(EffectTarget.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Rose Benjamin"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26bcff57-428e-4f30-a153-a778fbfc437d.jpg?1764120563"
    }
}
