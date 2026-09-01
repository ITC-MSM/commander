package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lobber Crew
 * {2}{R}
 * Creature — Goblin Warrior
 * 0/4
 *
 * Defender
 * {T}: This creature deals 1 damage to each opponent.
 * Whenever you cast a multicolored spell, untap this creature.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * The untapper is the guild payoff: [Triggers.youCastSpell] with a multicolored spell filter
 * already carries `TriggerBinding.ANY`, because the triggering object is the spell rather than
 * this permanent. The payoff still names the source, so it is [EffectTarget.Self].
 */
val LobberCrew = card("Lobber Crew") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    oracleText = "Defender\n" +
        "{T}: This creature deals 1 damage to each opponent.\n" +
        "Whenever you cast a multicolored spell, untap this creature."
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Multicolored)
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Greg Staples"
        flavorText = "It's easier to just aim at everything."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9d4aa15-a3c2-42a3-a87a-443e7dd20c04.jpg?1783940355"
    }
}
