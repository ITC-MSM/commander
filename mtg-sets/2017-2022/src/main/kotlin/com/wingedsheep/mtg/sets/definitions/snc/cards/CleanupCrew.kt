package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Cleanup Crew
 * {4}{G}{G}
 * Creature — Human Citizen
 * 6 / 6
 * When this creature enters, choose one —
 * • Destroy target artifact.
 * • Destroy target enchantment.
 * • Exile target card from a graveyard.
 * • You gain 4 life.
 *
 * A four-mode "choose one" enters trigger via [ModalEffect.chooseOne] (Exhibition Magician's shape
 * with two more modes): each targeting mode carries its own requirement, and the life mode is the
 * lone [Mode.noTarget].
 */
val CleanupCrew = card("Cleanup Crew") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Citizen"
    oracleText = "When this creature enters, choose one —\n• Destroy target artifact.\n• Destroy target enchantment.\n• Exile target card from a graveyard.\n• You gain 4 life."
    power = 6
    toughness = 6

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                target = TargetObject(filter = TargetFilter.Artifact),
                description = "Destroy target artifact"
            ),
            Mode.withTarget(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                target = TargetObject(filter = TargetFilter.Enchantment),
                description = "Destroy target enchantment"
            ),
            Mode.withTarget(
                effect = Effects.Exile(EffectTarget.ContextTarget(0)),
                target = TargetObject(filter = TargetFilter.CardInGraveyard),
                description = "Exile target card from a graveyard"
            ),
            Mode.noTarget(
                Effects.GainLife(4),
                "You gain 4 life"
            )
        )
        description = "When this creature enters, choose one — Destroy target artifact; or destroy " +
            "target enchantment; or exile target card from a graveyard; or you gain 4 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Josu Hernaiz"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5354868b-c96b-4765-b0c8-8895093e4019.jpg?1783923105"
    }
}
