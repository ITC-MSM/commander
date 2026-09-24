package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Infinite Coursework — Reality Fracture #31
 * {2}{U} · Enchantment — Aura
 *
 * The Stop Cold / Flood the Engine shape plus an unprepare rider on the ETB. Being prepared is a
 * designation, not an ability, so "loses all abilities" alone wouldn't strip it — the one-shot
 * [Effects.Unprepare] on entry does.
 */
val InfiniteCoursework = card("Infinite Coursework") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, tap enchanted creature. It becomes unprepared.\n" +
        "Enchanted creature loses all abilities and doesn't untap during its controller's untap step."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Tap(EffectTarget.EnchantedCreature)
            .then(Effects.Unprepare(EffectTarget.EnchantedCreature))
        description = "When this Aura enters, tap enchanted creature. It becomes unprepared."
    }

    staticAbility {
        ability = LoseAllAbilities()
    }

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Randy Vargas"
        flavorText = "Theorem dreams soon became nightmares."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5988272-faaa-463d-a0a1-a8e96b946bad.jpg?1789385916"
        inBooster = false
    }
}
