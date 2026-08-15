package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Zuko's Conviction
 * {B}
 * Instant
 * Kicker {4} (You may pay an additional {4} as you cast this spell.)
 * Return target creature card from your graveyard to your hand. If this spell was
 * kicked, instead put that card onto the battlefield tapped.
 *
 * The kicker simply swaps the destination of the single target: unkicked returns it to
 * hand, kicked puts that same card onto the battlefield tapped ("instead"), so the two
 * outcomes are the if/else branches of one [ConditionalEffect] gated on [WasKicked].
 */
val ZukosConviction = card("Zuko's Conviction") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Kicker {4} (You may pay an additional {4} as you cast this spell.)\n" +
        "Return target creature card from your graveyard to your hand. If this spell was kicked, instead put that card onto the battlefield tapped."

    keywordAbility(KeywordAbility.kicker("{4}"))

    spell {
        target = TargetObject(filter = TargetFilter.CreatureInYourGraveyard)
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = Effects.PutOntoBattlefield(EffectTarget.ContextTarget(0), tapped = true),
            elseEffect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "123"
        artist = "Kieran Yanner"
        flavorText = "\"I've always had to struggle and fight, and that's made me strong.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad8933d6-cdc7-4d60-a78e-b43ffecfb136.jpg?1764120849"
    }
}
