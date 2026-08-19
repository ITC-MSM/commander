package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Briarhorn
 * {3}{G}
 * Creature — Elemental
 * 3/3
 * Flash
 * When this creature enters, target creature gets +3/+3 until end of turn.
 * Evoke {1}{G}
 *
 * Flash plus evoke is the combat trick: cast it for {1}{G} mid-combat, the trigger pumps, and the
 * body is sacrificed on the way in.
 */
val Briarhorn = card("Briarhorn") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "Flash\nWhen this creature enters, target creature gets +3/+3 until end of turn.\n" +
        "Evoke {1}{G} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    keywords(Keyword.FLASH)

    evoke = "{1}{G}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(3, 3, creature)
        description = "target creature gets +3/+3 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "199"
        artist = "Nils Hamm"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b734a0a8-aa21-4fc0-b20d-5fea172884f6.jpg?1783942868"
    }
}
