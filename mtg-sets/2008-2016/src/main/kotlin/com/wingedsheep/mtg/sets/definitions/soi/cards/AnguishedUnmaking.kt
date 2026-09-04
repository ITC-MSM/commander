package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Anguished Unmaking (Shadows over Innistrad #242)
 * {1}{W}{B}
 * Instant
 *
 * Exile target nonland permanent. You lose 3 life.
 *
 * The life loss is not a cost and not conditional — it happens on resolution even if the exile
 * did nothing, so it is simply the second half of the composite.
 */
val AnguishedUnmaking = card("Anguished Unmaking") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Instant"
    oracleText = "Exile target nonland permanent. You lose 3 life."

    spell {
        val permanent = target("target", Targets.NonlandPermanent)
        effect = Effects.Exile(permanent) then Effects.LoseLife(3, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "242"
        artist = "Wesley Burt"
        flavorText = "Sorin had created Avacyn, so it was a cruelty beyond imagining, a pain beyond description, that it fell upon him to end her forever."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90ced4fa-6509-4f7a-9da7-efc70de6f90c.jpg?1783937714"
    }
}
