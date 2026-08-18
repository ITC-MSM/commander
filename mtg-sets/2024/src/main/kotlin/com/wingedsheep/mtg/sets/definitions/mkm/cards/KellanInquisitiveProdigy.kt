package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.PlayAdditionalLandsEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Kellan, Inquisitive Prodigy // Tail the Suspect — Murders at Karlov Manor #212
 *
 * Kellan's attack trigger checks who controlled the chosen artifact before destroying it. That
 * preserves the printed ruling that an indestructible artifact still draws a card for its
 * controller: destruction succeeding is not the condition. Tail the Suspect composes the existing
 * investigate and cumulative additional-land effects; the Adventure framework handles exile and
 * the later creature cast.
 */
val KellanInquisitiveProdigy = card("Kellan, Inquisitive Prodigy") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Human Faerie Detective"
    oracleText = "Flying, vigilance\n" +
        "Whenever Kellan attacks, destroy up to one target artifact. If you controlled that " +
        "permanent, draw a card."
    power = 3
    toughness = 4

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.Attacks
        val artifact = target(
            "up to one target artifact",
            TargetObject(filter = TargetFilter.Artifact, optional = true),
        )
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(GameObjectFilter.Artifact.youControl()),
            effect = Effects.Destroy(artifact).then(Effects.DrawCards(1)),
            elseEffect = Effects.Destroy(artifact),
        )
        description = "Whenever Kellan attacks, destroy up to one target artifact. If you " +
            "controlled that permanent, draw a card."
    }

    adventure("Tail the Suspect") {
        manaCost = "{G}{U}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Investigate. You may play an additional land this turn. (Then exile this " +
            "card. You may cast the creature later from exile.)"
        spell {
            effect = Effects.Investigate().then(PlayAdditionalLandsEffect(count = 1))
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "212"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c49690c7-c282-4eb4-8da3-5e0c46a80fc4.jpg?1783912845"

        ruling(
            "2024-02-02",
            "If the artifact you target with Kellan, Inquisitive Prodigy's triggered ability " +
                "isn't destroyed (perhaps because it's indestructible), you will still draw a " +
                "card as long as you control the artifact.",
        )
        ruling(
            "2024-02-02",
            "The effect of Tail the Suspect that allows you to play an additional land is " +
                "cumulative with similar effects.",
        )
    }
}
