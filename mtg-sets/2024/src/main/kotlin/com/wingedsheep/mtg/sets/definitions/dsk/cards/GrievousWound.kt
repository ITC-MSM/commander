package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.PreventLifeGain
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grievous Wound
 * {3}{B}{B}
 * Enchantment — Aura
 * Enchant player
 * Enchanted player can't gain life.
 * Whenever enchanted player is dealt damage, they lose half their life, rounded up.
 */
val GrievousWound = card("Grievous Wound") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant player\n" +
        "Enchanted player can't gain life.\n" +
        "Whenever enchanted player is dealt damage, they lose half their life, rounded up."

    auraTarget = Targets.Player

    // Enchanted player can't gain life.
    replacementEffect(PreventLifeGain(appliesTo = EventPattern.LifeGainEvent(player = Player.EnchantedPlayer)))

    // Whenever enchanted player is dealt damage, they lose half their life, rounded up.
    triggeredAbility {
        trigger = Triggers.takesDamage(binding = TriggerBinding.ATTACHED)
        effect = Effects.LoseHalfLife(
            roundUp = true,
            target = EffectTarget.PlayerRef(Player.EnchantedPlayer),
            lifePlayer = Player.EnchantedPlayer,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "102"
        artist = "Martina Fačková"
        flavorText = "Vicky was so tired. The pain began to fade. \"You were so brave,\" said her " +
            "grandmother's voice from somewhere far away. \"But it's time to rest now.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ed3dd5e-cd27-4b18-ad60-f5c4d9a811b9.jpg?1726286230"
    }
}
