package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Helvault
 * {3}
 * Legendary Artifact
 * {1}, {T}: Exile target creature you control.
 * {7}, {T}: Exile target creature you don't control.
 * When Helvault is put into a graveyard from the battlefield, return all cards exiled with it
 * to the battlefield under their owners' control.
 *
 * Both activated abilities exile their target linked to this permanent (accumulating in the
 * source's linked-exile pile). The leaves-to-graveyard trigger returns every card exiled with
 * Helvault at once under its owner's control — same linked-exile family as Oblivion Ring, but
 * the exile pile grows across repeated activations rather than holding a single card.
 */
val Helvault = card("Helvault") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "{1}, {T}: Exile target creature you control.\n" +
        "{7}, {T}: Exile target creature you don't control.\n" +
        "When Helvault is put into a graveyard from the battlefield, return all cards exiled " +
        "with it to the battlefield under their owners' control."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.ExileUntilLeaves(t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}"), Costs.Tap)
        val t = target("target", Targets.CreatureOpponentControls)
        effect = Effects.ExileUntilLeaves(t)
    }

    triggeredAbility {
        trigger = Triggers.PutIntoGraveyardFromBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "151"
        artist = "Jaime Jones"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16d2448c-1b2e-466a-a0ab-e20ba1de6bc9.jpg?1782714557"
    }
}
