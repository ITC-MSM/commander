package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Hearthcage Giant
 * {6}{R}{R}
 * Creature — Giant Warrior
 * 5/5
 * When this creature enters, create two 3/1 red Elemental Shaman creature tokens.
 * Sacrifice an Elemental: Target Giant creature gets +3/+1 until end of turn.
 *
 * Hearthcage Giant is itself a Giant, so it is a legal target for its own pump.
 */
val HearthcageGiant = card("Hearthcage Giant") {
    manaCost = "{6}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 5
    toughness = 5
    oracleText = "When this creature enters, create two 3/1 red Elemental Shaman creature tokens.\n" +
        "Sacrifice an Elemental: Target Giant creature gets +3/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 3,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Elemental", "Shaman"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/a/2/a280aee2-e15a-4625-b429-4032eae08a41.jpg?1783942839",
        )
        description = "create two 3/1 red Elemental Shaman creature tokens."
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.ELEMENTAL))
        val giant = target(
            "target Giant creature",
            TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.GIANT))
        )
        effect = Effects.ModifyStats(3, 1, giant)
        description = "Sacrifice an Elemental: Target Giant creature gets +3/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "The flamekin are mere kindling for his warmth."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb0d4d3f-d3ad-4c70-b9af-9bfcdd9efd1f.jpg?1783942874"
    }
}
