package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Unassuming Sage
 * {1}{W}
 * Creature — Human Peasant Wizard
 * 2/2
 *
 * When this creature enters, you may pay {2}. If you do, create a Sorcerer Role token attached
 * to it. (Enchanted creature gets +1/+1 and has "Whenever this creature attacks, scry 1.")
 *
 * "Attached to it" points back at the Sage, not at a target — the trigger is untargeted, so
 * hexproof/shroud never enter into it and there's nothing to fizzle. [MayPayManaEffect] models
 * "you may pay {2}. If you do" as one step: declining, or being unable to produce the mana,
 * simply skips the Role. If the Sage has left the battlefield by the time the trigger resolves,
 * there's no legal host and the Role isn't created.
 */
val UnassumingSage = card("Unassuming Sage") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Peasant Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you may pay {2}. If you do, create a Sorcerer Role " +
        "token attached to it. (Enchanted creature gets +1/+1 and has \"Whenever this creature " +
        "attacks, scry 1.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = Effects.CreateRoleToken("Sorcerer Role", EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Michele Giorgi"
        flavorText = "\"The archmage Valya? Why yes, I do believe I've heard of her.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a66fbaeb-1624-43b3-83e2-a37ce7588a5a.jpg?1783915126"
    }
}
