package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Demonmail Hauberk
 * {4}
 * Artifact — Equipment
 * Equipped creature gets +4/+2.
 * Equip—Sacrifice a creature.
 *
 * The equip cost is a non-mana cost ("Equip—Sacrifice a creature"), modeled as an equip-flagged
 * activated ability (sorcery-speed, equip-cost rules) whose cost sacrifices any creature — the
 * oracle says "a creature", not "another", so the equipped creature itself qualifies (cf.
 * Dissection Tools).
 */
val DemonmailHauberk = card("Demonmail Hauberk") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +4/+2.\n" +
        "Equip—Sacrifice a creature."

    staticAbility {
        ability = ModifyStats(4, 2, Filters.EquippedCreature)
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        isEquipAbility = true
        timing = TimingRule.SorcerySpeed
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.AttachEquipment(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Jason Felix"
        flavorText = "It comes off as easily as your own skin."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa33caa8-2a07-4f6c-a6c2-d21cf2d61193.jpg?1782714691"
    }
}
