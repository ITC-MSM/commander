package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Hellspur Brute
 * {4}{R}
 * Creature — Minotaur Mercenary
 * 5/4
 *
 * Affinity for outlaws (This spell costs {1} less to cast for each Assassin, Mercenary,
 * Pirate, Rogue, and/or Warlock you control.)
 * Trample
 *
 * "Affinity for outlaws" is a cost-reduction keyword: the spell costs {1} less per outlaw the
 * caster controls. Outlaws are creatures with any of the Assassin / Mercenary / Pirate / Rogue /
 * Warlock types ([Subtype.OUTLAW_TYPES], per the 2024-04-12 ruling). Modeled as a self-cast
 * [ModifySpellCost] whose [CostReductionSource.PermanentsYouControlMatching] counts permanents you
 * control matching the outlaw filter — the same general per-permanent reduction primitive Temur
 * Battlecrier uses. The spell is still on the stack while its cost is computed, so it never counts
 * itself.
 */
val HellspurBrute = card("Hellspur Brute") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Mercenary"
    power = 5
    toughness = 4
    oracleText = "Affinity for outlaws (This spell costs {1} less to cast for each Assassin, " +
        "Mercenary, Pirate, Rogue, and/or Warlock you control.)\nTrample"

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsYouControlMatching(
                    GameObjectFilter.Creature.withAnyOfSubtypes(Subtype.OUTLAW_TYPES),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Caio Monteiro"
        flavorText = "The last fool who called him \"cowboy\" earned a free ride to the bottom of Blacksnag Bog."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b99db5b-cd13-4e69-98b7-753e72c781f8.jpg?1712355767"

        ruling("2024-04-12", "A card, spell, or permanent is an outlaw if it has the Assassin, Mercenary, Pirate, Rogue, or Warlock creature type. It doesn't matter if it has more than one of those creature types; as long as it has at least one, it's an outlaw.")
    }
}
