package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Take Down
 * {G}
 * Sorcery
 *
 * Choose one —
 * • Take Down deals 4 damage to target creature with flying.
 * • Take Down deals 1 damage to each creature with flying.
 *
 * A plain choose-one modal. The second mode's "each creature with flying" is untargeted and hits
 * every flier on the battlefield — the caster's own included — so it is the group sweep
 * [Patterns.Group.dealDamageToAll] over the same filter the first mode targets through.
 */
val TakeDown = card("Take Down") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Take Down deals 4 damage to target creature with flying.\n" +
        "• Take Down deals 1 damage to each creature with flying."

    spell {
        modal {
            mode("Take Down deals 4 damage to target creature with flying") {
                val t = target("target", TargetCreature(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING)))
                effect = Effects.DealDamage(4, t)
            }
            mode("Take Down deals 1 damage to each creature with flying") {
                effect = Patterns.Group.dealDamageToAll(
                    1,
                    GroupFilter.AllCreatures.withKeyword(Keyword.FLYING)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Izzy"
        flavorText = "The drake flew true. The arrow flew truer."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8e702db-8c73-4947-9c13-5dcb50f4efab.jpg?1783937172"
    }
}
