package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Killer's Mask
 * {2}{B}
 * Artifact — Equipment
 * When this Equipment enters, manifest dread, then attach this Equipment to that creature. (Look at
 * the top two cards of your library. Put one onto the battlefield face down as a 2/2 creature and the
 * other into your graveyard. Turn it face up any time for its mana cost if it's a creature card.)
 * Equipped creature has menace.
 * Equip {2}
 *
 * Manifest dread (CR 701.62) stores the manifested creature under the pipeline collection
 * "manifestDreadManifested" (see [Patterns.Library.manifestDread]); the follow-up attach targets
 * that creature via [EffectTarget.PipelineTarget]. If the library is empty (no creature is
 * manifested), there is nothing to attach and the attach step is a no-op.
 */
val KillersMask = card("Killer's Mask") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, manifest dread, then attach this Equipment to that " +
        "creature. (Look at the top two cards of your library. Put one onto the battlefield face " +
        "down as a 2/2 creature and the other into your graveyard. Turn it face up any time for " +
        "its mana cost if it's a creature card.)\n" +
        "Equipped creature has menace.\n" +
        "Equip {2}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.manifestDread(),
            Effects.AttachEquipment(EffectTarget.PipelineTarget("manifestDreadManifested"))
        )
    }

    staticAbility {
        ability = GrantKeyword(Keyword.MENACE)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Wero Gallo"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1fc02ae-77b8-4e5e-94b4-22ecf7ae40ae.jpg?1726286237"
    }
}
