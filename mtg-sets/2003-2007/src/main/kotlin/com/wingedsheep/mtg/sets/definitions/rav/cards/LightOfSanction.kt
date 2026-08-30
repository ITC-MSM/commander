package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Light of Sanction
 * {1}{W}{W}
 * Enchantment
 *
 * Prevent all damage that would be dealt to creatures you control by sources you control.
 *
 * One [PreventDamage] replacement with both halves of the sentence filled in: recipients are
 * [RecipientFilter.CreatureYouControl] and sources are [SourceFilter.YouControl]. Both "you"s are
 * the enchantment's *current* controller, re-read per damage instance, so creatures that come under
 * your control later are covered and creatures that leave it stop being.
 *
 * [SourceFilter.YouControl] deliberately covers more than permanents — a burn spell on the stack and
 * an activated ability's source are checked by controller the same way — which is what makes this
 * the "point my own sweepers at my own board" card it was printed to be. Players are not creatures,
 * so it never protects you.
 */
val LightOfSanction = card("Light of Sanction") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Prevent all damage that would be dealt to creatures you control by sources you control."

    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.CreatureYouControl,
                source = SourceFilter.YouControl
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Michael Phillippi"
        flavorText = "The Legion looks after its own."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/daff382a-980e-4f0c-b26c-70c8a43c66f1.jpg?1783943697"
    }
}
