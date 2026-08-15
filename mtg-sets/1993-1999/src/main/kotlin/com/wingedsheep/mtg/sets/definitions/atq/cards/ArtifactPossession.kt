package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Artifact Possession
 * {2}{B}
 * Enchantment — Aura
 * Enchant artifact
 * Whenever enchanted artifact becomes tapped or a player activates an ability of enchanted
 * artifact without {T} in its activation cost, this Aura deals 2 damage to that artifact's
 * controller.
 *
 * The single-target ([TriggerBinding.ATTACHED]) member of the Antiquities "tap / activate an
 * artifact" punisher template (cf. Haunting Wind, Powerleech): both triggers fire only off the
 * enchanted artifact. Routed through `AttachmentTriggerDetector`, which exposes the enchanted
 * artifact as the triggering entity, so "that artifact's controller" resolves via
 * [EffectTarget.ControllerOfTriggeringEntity] for both halves.
 */
val ArtifactPossession = card("Artifact Possession") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant artifact\n" +
        "Whenever enchanted artifact becomes tapped or a player activates an ability of enchanted " +
        "artifact without {T} in its activation cost, this Aura deals 2 damage to that artifact's " +
        "controller."
    auraTarget = Targets.Artifact

    triggeredAbility {
        trigger = Triggers.becomesTapped(binding = TriggerBinding.ATTACHED)
        effect = Effects.DealDamage(2, EffectTarget.ControllerOfTriggeringEntity)
    }

    triggeredAbility {
        trigger = Triggers.activatesAbilityWithoutTap(
            player = Player.Each,
            binding = TriggerBinding.ATTACHED
        )
        effect = Effects.DealDamage(2, EffectTarget.ControllerOfTriggeringEntity)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Christopher Rush"
        flavorText = "Any black mage could coax a Thraxodemon to inhabit a magical device."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/587d6ac8-fad8-49e0-862e-636e06628ff9.jpg?1562913472"
    }
}
