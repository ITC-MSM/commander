package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

val ArchiveArbiter = card("Archive Arbiter") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Sphinx"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, choose one —\n" +
        "• Destroy target noncreature, nonland permanent.\n" +
        "• You gain 4 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                target = TargetPermanent(
                    filter = TargetFilter(GameObjectFilter.NonlandPermanent.notCreature())
                ),
                description = "Destroy target noncreature, nonland permanent."
            ),
            Mode.noTarget(
                effect = Effects.GainLife(4),
                description = "You gain 4 life."
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Valera Lutfullina"
        flavorText = "It ensures students leave the Folioplex with the correct notes, theories, and ponderings."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/024bce1e-a5f3-4292-bc17-d0355a5d65e1.jpg?1789614867"
        inBooster = false
    }
}
