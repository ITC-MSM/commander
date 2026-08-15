package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Safewright Cavalry
 * {3}{G}
 * Creature — Elf Warrior
 * 4/4
 *
 * This creature can't be blocked by more than one creature.
 * {5}: Target Elf you control gets +2/+2 until end of turn.
 */
val SafewrightCavalry = card("Safewright Cavalry") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 4
    toughness = 4
    oracleText = "This creature can't be blocked by more than one creature.\n{5}: Target Elf you control gets +2/+2 until end of turn."

    staticAbility {
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    activatedAbility {
        cost = Costs.Mana("{5}")
        val elf = target(
            "target Elf you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl.withSubtype(Subtype.ELF))
        )
        effect = Effects.ModifyStats(2, 2, elf)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Milivoj Ćeran"
        flavorText = "They had survived battles and foes together. For the elf, there was no greater beauty than her cervin steed."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7de412a-9731-4bfc-8fbc-c95988a3dd70.jpg?1767957270"
    }
}
