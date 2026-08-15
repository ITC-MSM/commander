package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bitter Chill
 * {1}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, tap enchanted creature.
 * Enchanted creature doesn't untap during its controller's untap step.
 * When this Aura is put into a graveyard from the battlefield, you may pay {1}. If you do,
 * scry 1, then draw a card.
 *
 * The lock half is Charmed Sleep's shape (tap on entry + [AbilityFlag.DOESNT_UNTAP] granted to the
 * enchanted creature). The refund half fires on *any* trip from battlefield to graveyard — the Aura
 * being destroyed, sacrificed, or falling off as a state-based action when its creature leaves —
 * which is exactly [Triggers.PutIntoGraveyardFromBattlefield].
 */
val BitterChill = card("Bitter Chill") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, tap enchanted creature.\n" +
        "Enchanted creature doesn't untap during its controller's untap step.\n" +
        "When this Aura is put into a graveyard from the battlefield, you may pay {1}. " +
        "If you do, scry 1, then draw a card."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Tap(EffectTarget.EnchantedCreature)
    }

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name)
    }

    triggeredAbility {
        trigger = Triggers.PutIntoGraveyardFromBattlefield
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Patterns.Library.scry(1).then(Effects.DrawCards(1))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "44"
        artist = "Julie Dillon"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/888e3c71-e21d-4e77-b1b6-09769f9cd3d6.jpg?1783915122"
    }
}
