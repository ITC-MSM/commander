package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Rarity

/**
 * Reigning Victor — Tarkir: Dragonstorm #216
 * {2/R}{2/W}{2/B} · Creature — Orc Warrior · 3/3
 *
 * Mobilize 1 (Whenever this creature attacks, create a tapped and attacking 1/1 red Warrior
 * creature token. Sacrifice it at the beginning of the next end step.)
 * When this creature enters, target creature gets +1/+0 and gains indestructible until end of turn.
 *
 * `mobilize(1)` supplies the display keyword plus the attack-triggered tapped-and-attacking Warrior
 * token. The enters-the-battlefield ability targets any creature (the printed card says "target
 * creature", not "you control") and grants +1/+0 via [Effects.ModifyStats] plus indestructible via
 * [Effects.GrantKeyword]; both default to end-of-turn duration.
 */
val ReigningVictor = card("Reigning Victor") {
    manaCost = "{2/R}{2/W}{2/B}"
    colorIdentity = "RWB"
    typeLine = "Creature — Orc Warrior"
    power = 3
    toughness = 3
    oracleText = "Mobilize 1 (Whenever this creature attacks, create a tapped and attacking 1/1 red " +
        "Warrior creature token. Sacrifice it at the beginning of the next end step.)\n" +
        "When this creature enters, target creature gets +1/+0 and gains indestructible until end of turn. " +
        "(Damage and effects that say \"destroy\" don't destroy it.)"

    mobilize(1)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature to get +1/+0 and gain indestructible", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, creature)
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature))
        description = "When this creature enters, target creature gets +1/+0 and gains indestructible until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a394112a-032b-4047-887a-6522cf7b83d5.jpg?1743204853"
    }
}
