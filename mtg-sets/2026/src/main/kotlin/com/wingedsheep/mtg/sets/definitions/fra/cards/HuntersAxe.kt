package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hunter's Axe (Reality Fracture #108) — {G} Artifact — Equipment.
 *
 * Pirate Hat's shape: a +2/+0 pump on [Filters.EquippedCreature] plus an attack trigger granted to
 * the equipped creature, so the ability lives on the creature and "it" ([EffectTarget.Self]) is
 * that creature. "Your choice of trample or deathtouch" is a two-mode [ModalEffect] with no
 * targets, chosen as the trigger resolves (Manifold Mouse / Ezrim's spelling).
 */
val HuntersAxe = card("Hunter's Axe") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0 and has \"Whenever this creature attacks, it gains your " +
        "choice of trample or deathtouch until end of turn.\"\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(+2, 0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.attacks().event,
                binding = Triggers.attacks().binding,
                effect = ModalEffect.chooseOne(
                    Mode.noTarget(Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self), "Trample"),
                    Mode.noTarget(Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self), "Deathtouch"),
                ),
                descriptionOverride = "Whenever this creature attacks, it gains your choice of trample or " +
                    "deathtouch until end of turn."
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "Richard Kane Ferguson"
        flavorText = "One wielder a fierce steward of the wilds, the other a cruel bringer of death."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2cc5d0e-9643-4ee2-81de-fa2c3bd1ab09.jpg?1789556822"
        inBooster = false
    }
}
