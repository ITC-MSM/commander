package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Orbweaver Kumo
 * {4}{G}{G}
 * Creature — Spirit
 * 3/4
 * Reach
 * Whenever you cast a Spirit or Arcane spell, this creature gains forestwalk until end of turn.
 *
 * The same "Whenever you cast a Spirit or Arcane spell" trigger the rest of the CHK Spirit cycle
 * carries — [Triggers.youCastSpell] over a homogeneous OR of the two subtype filters, binding
 * `ANY` — with the payoff pointed at the source rather than at a target.
 *
 * [EffectTarget.Self] is the right handle here, not `TriggeringEntity`: the entity bound by a
 * spell-cast trigger is the *spell*, and "this creature gains forestwalk" names the permanent
 * whose ability it is.
 */
val OrbweaverKumo = card("Orbweaver Kumo") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spirit"
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "Whenever you cast a Spirit or Arcane spell, this creature gains forestwalk until end of " +
        "turn. (It can't be blocked as long as defending player controls a Forest.)"
    power = 3
    toughness = 4

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        effect = Effects.GrantKeyword(Keyword.FORESTWALK, EffectTarget.Self)
        description = "Whenever you cast a Spirit or Arcane spell, this creature gains " +
            "forestwalk until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "231"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d8f0459-a839-4820-8f99-58da5851ff36.jpg?1783944285"
    }
}
