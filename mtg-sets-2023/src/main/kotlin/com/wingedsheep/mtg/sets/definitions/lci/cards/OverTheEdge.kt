package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Over the Edge — {1}{G}
 * Sorcery
 * Common — The Lost Caverns of Ixalan #205
 * Artist: Ryan Valle
 *
 * "Choose one —
 *  • Destroy target artifact or enchantment.
 *  • Target creature you control explores, then it explores again."
 *
 * A "Choose one —" modal sorcery (CR 700.2 / 601.2b). Each mode declares its own target, so
 * targeting legality is checked per chosen mode at cast time.
 *
 * Mode 0: Destroy a targeted artifact or enchantment ([Targets.ArtifactOrEnchantment] matches a
 *   permanent that is an artifact and/or an enchantment).
 *
 * Mode 1: A creature you control explores twice (CR 701.44). "Explores, then it explores again"
 *   = two sequential [Effects.Explore] calls on the same permanent, mirroring Defossilize. Each
 *   explore reveals the top library card; a land goes to hand, otherwise a +1/+1 counter is put
 *   on the creature and the player may put the revealed card into the graveyard. The target [c]
 *   is a stable [com.wingedsheep.sdk.scripting.targets.EffectTarget.BoundVariable] so both
 *   explores act on the same creature.
 */
val OverTheEdge = card("Over the Edge") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Destroy target artifact or enchantment.\n" +
        "• Target creature you control explores, then it explores again. " +
        "(Reveal the top card of your library. Put that card into your hand if it's a land. " +
        "Otherwise, put a +1/+1 counter on that creature, then put the card back or put it " +
        "into your graveyard. Then repeat this process.)"

    spell {
        modal {
            mode("Destroy target artifact or enchantment") {
                val artifactOrEnchantment = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
                effect = Effects.Destroy(artifactOrEnchantment)
            }
            mode("Target creature you control explores, then it explores again") {
                val creature = target("target creature you control", Targets.CreatureYouControl)
                effect = Effects.Explore(creature)
                    .then(Effects.Explore(creature))
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "205"
        artist = "Ryan Valle"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23dddddb-5409-4c28-bf32-e6473f2cc620.jpg?1782694445"
    }
}
