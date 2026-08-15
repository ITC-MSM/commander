package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Defossilize — {4}{B}
 * Sorcery
 * Uncommon — The Lost Caverns of Ixalan #103
 * Artist: Dibujante Nocturno
 *
 * "Return target creature card from your graveyard to the battlefield.
 *  That creature explores, then it explores again."
 *
 * Resolution (CR 608.2): all three effects execute sequentially as part of the spell resolving.
 *  1. The targeted creature card is moved from the controller's graveyard to the battlefield
 *     (not cast, so enters-when-cast conditions don't apply, and no mana was spent on it —
 *     relevant for Satoru, the Infiltrator and similar).
 *  2. That permanent explores once (CR 701.44): reveal top library card; if land → hand, else
 *     put a +1/+1 counter on the creature and the player may put the revealed card in the
 *     graveyard instead of leaving it on top.
 *  3. That permanent explores a second time with the same procedure.
 *
 * The target reference [t] is an [com.wingedsheep.sdk.scripting.targets.EffectTarget.BoundVariable]
 * whose entity ID is stable across zone changes, so it correctly identifies the same permanent
 * on the battlefield for both [Effects.Explore] calls after [Effects.PutOntoBattlefield] moves it.
 */
val Defossilize = card("Defossilize") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to the battlefield. " +
        "That creature explores, then it explores again. " +
        "(Reveal the top card of your library. Put that card into your hand if it's a land. " +
        "Otherwise, put a +1/+1 counter on that creature, then put the card back or put it " +
        "into your graveyard. Then repeat this process.)"

    spell {
        val t = target("target creature card from your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = Effects.PutOntoBattlefield(t)
            .then(Effects.Explore(t))
            .then(Effects.Explore(t))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "103"
        artist = "Dibujante Nocturno"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d9abaec-af72-4399-b162-5e62e7487242.jpg?1782694528"
    }
}
