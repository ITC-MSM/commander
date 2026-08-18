package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Hum of the Radix — Mirrodin #122 (canonical printing)
 * {2}{G}{G} · Enchantment
 *
 * Each artifact spell costs {1} more to cast for each artifact its controller controls.
 *
 * The Glowrider shape with a *dynamic* tax: [SpellCostTarget.AnyCaster] taxes every player's
 * artifact spells, and [CostModification.IncreaseGenericBy] scales the tax off the game state
 * instead of a printed number. It is the exact mirror of [CostModification.ReduceGenericBy] and
 * reads the same [CostReductionSource] vocabulary, so affinity's counter needs no twin here.
 *
 * "its controller controls" falls out of the shared evaluation rule rather than a new source: a
 * cost source is always evaluated against the *casting* player, which on the reduction side is
 * the same player as the source's controller and here is the taxed one. So an opponent casting
 * an artifact spell pays for the artifacts *they* control, not for the enchantment controller's
 * — the difference this card is entirely built on.
 *
 * The counted artifacts include the spell itself only once it has resolved, since a spell on the
 * stack is not a permanent; the tax is locked in when the total cost is determined (CR 601.2f).
 */
val HumOfTheRadix = card("Hum of the Radix") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Each artifact spell costs {1} more to cast for each artifact its controller controls."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Artifact),
            modification = CostModification.IncreaseGenericBy(CostReductionSource.ArtifactsYouControl),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "122"
        artist = "John Avon"
        flavorText = "The elves learned long ago that anything left here slowly vanishes. Now it is a sacred " +
            "site where the dead are laid to rest and where unnatural magic is erased forever."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/328f3afb-1a56-42a5-bd1e-3e704291972f.jpg?1783944533"
    }
}
