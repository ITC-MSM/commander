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
 * Cursed Windbreaker
 * {2}{U}
 * Artifact — Equipment
 * When this Equipment enters, manifest dread, then attach this Equipment to that creature. (Look
 * at the top two cards of your library. Put one onto the battlefield face down as a 2/2 creature
 * and the other into your graveyard. Turn it face up any time for its mana cost if it's a
 * creature card.)
 * Equipped creature has flying.
 * Equip {3}
 *
 * Manifest dread (CR 701.62) stores the manifested creature under the pipeline collection
 * "manifestDreadManifested" (see [Patterns.Library.manifestDread]); the follow-up attach targets
 * that creature via [EffectTarget.PipelineTarget]. If the library is empty (no creature is
 * manifested), there is nothing to attach and the attach step is a no-op.
 */
val CursedWindbreaker = card("Cursed Windbreaker") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, manifest dread, then attach this Equipment to that " +
        "creature. (Look at the top two cards of your library. Put one onto the battlefield face " +
        "down as a 2/2 creature and the other into your graveyard. Turn it face up any time for " +
        "its mana cost if it's a creature card.)\n" +
        "Equipped creature has flying.\n" +
        "Equip {3}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.manifestDread(),
            Effects.AttachEquipment(EffectTarget.PipelineTarget("manifestDreadManifested"))
        )
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Nino Vecia"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f651e216-f9da-4696-8a1d-6d674e9044c0.jpg?1726286030"
    }
}
