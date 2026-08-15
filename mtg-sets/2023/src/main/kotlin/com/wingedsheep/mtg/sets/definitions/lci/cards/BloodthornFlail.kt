package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bloodthorn Flail (LCI #93) — {B} Artifact — Equipment (uncommon)
 *
 * Equipped creature gets +2/+1.
 * Equip—Pay {3} or discard a card.
 *
 * Implementation notes:
 * - The +2/+1 buff is a [ModifyStats] static scoped to [Filters.EquippedCreature].
 * - "Equip—Pay {3} or discard a card" is a single equip ability whose cost is a *choice*
 *   between {3} and discarding a card. The engine has no choice/"or" cost for activated
 *   abilities, so this is modeled as the faithful decomposition into two equip-flagged,
 *   sorcery-speed attach abilities — one costing {3} (via the [equipAbility] facade), one
 *   costing a discarded card. The player picks whichever equip option they can/want to pay,
 *   which is exactly the choice the printed single ability offers (only the presentation
 *   differs: two equip buttons instead of one with a cost prompt).
 */
val BloodthornFlail = card("Bloodthorn Flail") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+1.\n" +
        "Equip—Pay {3} or discard a card."

    // Equipped creature gets +2/+1.
    staticAbility {
        ability = ModifyStats(+2, +1, Filters.EquippedCreature)
    }

    // Equip—Pay {3}.
    equipAbility("{3}")

    // Equip—discard a card (the alternative half of "Pay {3} or discard a card").
    activatedAbility {
        isEquipAbility = true
        cost = Costs.DiscardCard
        timing = TimingRule.SorcerySpeed
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AttachEquipment(creature)
        description = "Equip—Discard a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "93"
        artist = "Igor Kieryluk"
        flavorText = "\"This weapon embodies both the strength and the weakness of the Legion of Dusk. They will " +
            "endure any pain to achieve their ends, but they suffer pointlessly just to prove their dedication.\"\n—Huatli"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db38babd-57e8-4e59-9701-11a0682baa77.jpg?1782694536"
    }
}
