package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttack
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventActivatedAbilities
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Petrify
 * {1}{W}
 * Enchantment — Aura
 *
 * Enchant artifact or creature
 * Enchanted permanent can't attack or block, and its activated abilities can't be
 * activated.
 *
 * Pacifism's combat lock (CantAttack/CantBlock scoped to the attached permanent) plus
 * the Cursed Totem-style activation lock narrowed to just this Aura's host via
 * [PreventActivatedAbilities] with `attachedToBySource()`. `GroupFilter.attachedCreature()`
 * is actually a plain `Permanent` filter scoped to the attached object, so it correctly
 * covers an artifact host as well as a creature.
 */
val Petrify = card("Petrify") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant artifact or creature\n" +
        "Enchanted permanent can't attack or block, and its activated abilities can't " +
        "be activated."

    auraTarget = Targets.CreatureOrArtifact

    staticAbility {
        ability = CantAttack(filter = GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = CantBlock(filter = GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = PreventActivatedAbilities(
            filter = GameObjectFilter.Permanent.attachedToBySource(),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Samuel Araya"
        flavorText = "Those who break the laws of the Malamet become cold, silent " +
            "warnings to others who might try."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbc5f28f-6361-455f-ac82-260a70e59316.jpg?1782694588"
    }
}
