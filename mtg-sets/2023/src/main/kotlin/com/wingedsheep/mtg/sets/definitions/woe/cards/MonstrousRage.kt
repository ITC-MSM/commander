package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Monstrous Rage
 * {R}
 * Instant
 *
 * Target creature gets +2/+0 until end of turn. Create a Monster Role token attached to it.
 * (If you control another Role on it, put that one into the graveyard. Enchanted creature gets
 * +1/+1 and has trample.)
 *
 * The +2/+0 comes from the spell; the +1/+1 and trample come from the Monster Role token itself,
 * so the spell only pumps and creates the Role (Role replacement is handled by the executor).
 */
val MonstrousRage = card("Monstrous Rage") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+0 until end of turn. Create a Monster Role token attached to it. " +
        "(If you control another Role on it, put that one into the graveyard. Enchanted creature gets +1/+1 and has trample.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(+2, 0, t),
            Effects.CreateRoleToken("Monster Role", t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Borja Pindado"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eef5a0ae-5907-42c9-a097-3f973737e392.jpg?1783915091"
    }
}
