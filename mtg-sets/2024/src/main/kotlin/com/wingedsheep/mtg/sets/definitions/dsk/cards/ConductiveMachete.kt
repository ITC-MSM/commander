package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Conductive Machete
 * {4}
 * Artifact — Equipment
 * When this Equipment enters, manifest dread, then attach this Equipment to that creature. (Look
 * at the top two cards of your library. Put one onto the battlefield face down as a 2/2 creature
 * and the other into your graveyard. Turn it face up any time for its mana cost if it's a
 * creature card.)
 * Equipped creature gets +2/+1.
 * Equip {4}
 *
 * Manifest dread (CR 701.62) stores the manifested creature under the pipeline collection
 * "manifestDreadManifested" (see [Patterns.Library.manifestDread]); the follow-up attach targets
 * that creature via [EffectTarget.PipelineTarget]. If the library is empty (no creature is
 * manifested), there is nothing to attach and the attach step is a no-op.
 */
val ConductiveMachete = card("Conductive Machete") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, manifest dread, then attach this Equipment to that " +
        "creature. (Look at the top two cards of your library. Put one onto the battlefield face " +
        "down as a 2/2 creature and the other into your graveyard. Turn it face up any time for " +
        "its mana cost if it's a creature card.)\n" +
        "Equipped creature gets +2/+1.\n" +
        "Equip {4}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.manifestDread(),
            Effects.AttachEquipment(EffectTarget.PipelineTarget("manifestDreadManifested"))
        )
    }

    staticAbility {
        ability = ModifyStats(+2, +1, Filters.EquippedCreature)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "Steven Russell Black"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1cf37c1a-096b-4306-97cf-bc4d4c47d4a1.jpg?1726286782"
    }
}
