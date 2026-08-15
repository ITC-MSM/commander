package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter

/**
 * Karn's Temporal Sundering
 * {4}{U}{U}
 * Legendary Sorcery
 *
 * (You may cast a legendary sorcery only if you control a legendary creature or planeswalker.)
 * Target player takes an extra turn after this one. Return up to one target nonland permanent
 * to its owner's hand. Exile Karn's Temporal Sundering.
 */
val KarnsTemporalSundering = card("Karn's Temporal Sundering") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Sorcery"
    oracleText = "(You may cast a legendary sorcery only if you control a legendary creature or planeswalker.)\nTarget player takes an extra turn after this one. Return up to one target nonland permanent to its owner's hand. Exile Karn's Temporal Sundering."

    spell {
        castOnlyIf(Conditions.ControlLegendaryCreatureOrPlaneswalker)
        selfExile()

        val player = target("player", Targets.Player)
        val permanent = target("nonland permanent", TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent))

        effect = TakeExtraTurnEffect(target = player)
            .then(Effects.ReturnToHand(permanent))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "55"
        artist = "Noah Bradley"
        flavorText = "Centuries ago, a quest to harness time became a spiral into chaos."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a64c1a4-02ce-49d2-97f2-970b8c33672d.jpg?1562738165"
        ruling("2018-04-27", "If the target player or target nonland permanent is an illegal target as Karn's Temporal Sundering resolves, the other target is affected as normal and Karn's Temporal Sundering is exiled. If both targets are illegal, Karn's Temporal Sundering doesn't resolve and isn't exiled.")
        ruling("2018-04-27", "You can't cast a legendary sorcery unless you control a legendary creature or a legendary planeswalker. Once you begin to cast a legendary sorcery, losing control of your legendary creatures and planeswalkers won't affect that spell.")
    }
}
