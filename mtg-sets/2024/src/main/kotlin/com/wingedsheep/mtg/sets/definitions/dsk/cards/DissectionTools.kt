package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dissection Tools
 * {5}
 * Artifact — Equipment
 * When this Equipment enters, manifest dread, then attach this Equipment to that creature.
 * Equipped creature gets +2/+2 and has deathtouch and lifelink.
 * Equip—Sacrifice a creature.
 *
 * Manifest dread (CR 701.62) stores the manifested creature under the pipeline collection
 * "manifestDreadManifested" (see [Patterns.Library.manifestDread]); the follow-up attach targets
 * that creature via [EffectTarget.PipelineTarget]. If the library is empty (no creature is
 * manifested), there is nothing to attach and the attach step is a no-op.
 *
 * The equip cost is a non-mana cost ("Equip—Sacrifice a creature"), so it is modeled as an equip
 * activated ability (`isEquipAbility = true`) whose cost sacrifices any creature — including the
 * equipped one (the oracle says "a creature", not "another"). Sorcery-speed, like every equip.
 */
val DissectionTools = card("Dissection Tools") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, manifest dread, then attach this Equipment to that creature.\n" +
        "Equipped creature gets +2/+2 and has deathtouch and lifelink.\n" +
        "Equip—Sacrifice a creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.manifestDread(),
            Effects.AttachEquipment(EffectTarget.PipelineTarget("manifestDreadManifested"))
        )
    }

    staticAbility {
        ability = ModifyStats(2, 2, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        isEquipAbility = true
        timing = TimingRule.SorcerySpeed
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.AttachEquipment(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "245"
        artist = "Diana Franco"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/048bb2c6-91bd-4d6a-a070-c73d8277c264.jpg?1726301876"
    }
}
