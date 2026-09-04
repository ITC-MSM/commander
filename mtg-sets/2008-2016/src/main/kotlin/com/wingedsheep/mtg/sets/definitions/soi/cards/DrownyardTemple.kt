package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Drownyard Temple (Shadows over Innistrad #271)
 *
 * Land
 *
 * {T}: Add {C}.
 * {3}: Return this card from your graveyard to the battlefield tapped.
 *
 * Modeling notes:
 *  - The recursion is an activated ability that functions from the graveyard
 *    (`activateFromZone = Zone.GRAVEYARD`), and the return carries the graveyard guard plus the
 *    `tapped` axis — [Effects.PutOntoBattlefieldFromGraveyard] with `tapped = true`. Nothing
 *    re-checks `activateFromZone` at resolution, so the guard is what stops a Temple exiled from
 *    the graveyard in response coming back from exile.
 */
val DrownyardTemple = card("Drownyard Temple") {
    manaCost = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{3}: Return this card from your graveyard to the battlefield tapped."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.PutOntoBattlefieldFromGraveyard(EffectTarget.Self, tapped = true)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "271"
        artist = "John Avon"
        flavorText = "\"This is it! All the cryptoliths point here!\"\n—Jace Beleren"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5bb5657-18a1-455c-a87a-4e67971184ac.jpg?1783937699"
    }
}
