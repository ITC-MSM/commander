package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.SpellCastEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Spider Manifestation
 * {1}{R/G}
 * Creature — Spider Avatar, 2/2
 * Reach
 * {T}: Add {R} or {G}.
 * Whenever you cast a spell with mana value 4 or greater, untap this creature.
 */
val SpiderManifestation = card("Spider Manifestation") {
    manaCost = "{1}{R/G}"
    colorIdentity = "RG"
    typeLine = "Creature — Spider Avatar"
    power = 2
    toughness = 2
    oracleText = "Reach\n{T}: Add {R} or {G}.\nWhenever you cast a spell with mana value 4 or greater, untap this creature."

    keywords(Keyword.REACH)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(ManaColorSet.Specific(setOf(Color.RED, Color.GREEN)))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    triggeredAbility {
        trigger = TriggerSpec(
            event = SpellCastEvent(spellFilter = GameObjectFilter.Any.manaValueAtLeast(4), player = Player.You),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Helge C. Balzer"
        flavorText = "Some spiders feed on *other* spiders."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99223677-b8a5-48f1-8009-e8475eada7db.jpg?1758215879"
    }
}
