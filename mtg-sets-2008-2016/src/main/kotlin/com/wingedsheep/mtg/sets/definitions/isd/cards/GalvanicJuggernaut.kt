package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MustAttack
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Galvanic Juggernaut
 * {4}
 * Artifact Creature — Juggernaut
 * 5/5
 * This creature attacks each combat if able.
 * This creature doesn't untap during your untap step.
 * Whenever another creature dies, untap this creature.
 *
 * "Doesn't untap" is the [AbilityFlag.DOESNT_UNTAP] flag; the untap is driven by an "another
 * creature dies" trigger ([TriggerBinding.OTHER] on the creature-dies event), mirroring Goblin
 * Sharpshooter's untap loop.
 */
val GalvanicJuggernaut = card("Galvanic Juggernaut") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Juggernaut"
    power = 5
    toughness = 5
    oracleText = "This creature attacks each combat if able.\n" +
        "This creature doesn't untap during your untap step.\n" +
        "Whenever another creature dies, untap this creature."

    flags(AbilityFlag.DOESNT_UNTAP)

    staticAbility {
        ability = MustAttack()
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature,
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Lucas Graciano"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d14bc109-d5d5-4777-90e4-bef26d106571.jpg?1782714690"
    }
}
