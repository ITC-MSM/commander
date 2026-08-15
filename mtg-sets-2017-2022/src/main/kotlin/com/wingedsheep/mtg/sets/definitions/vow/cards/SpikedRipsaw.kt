package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spiked Ripsaw
 * {2}{G}
 * Artifact — Equipment
 *
 * Equipped creature gets +3/+3.
 * Whenever equipped creature attacks, you may sacrifice a Forest. If you do, that creature gains
 * trample until end of turn.
 * Equip {3}
 *
 * Standard Equipment shell: the +3/+3 is a [ModifyStats] static over [Filters.EquippedCreature].
 * The attack trigger lives on the Equipment itself (`Triggers.attacks(binding = ATTACHED)` =
 * "equipped creature attacks"), so "that creature" is [EffectTarget.EquippedCreature]. The body is
 * the Midgar / Highway Robbery optional-sacrifice idiom: [Effects.IfYouDo] gathers the controller's
 * Forests, lets them choose up to one to sacrifice, and only on a successful sacrifice
 * ([com.wingedsheep.sdk.scripting.effects.SuccessCriterion.Auto] inferring the zone move) grants
 * trample to the equipped creature until end of turn. Declining the "you may" sacrifices nothing
 * and grants nothing.
 */
val SpikedRipsaw = card("Spiked Ripsaw") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +3/+3.\n" +
        "Whenever equipped creature attacks, you may sacrifice a Forest. If you do, that creature " +
        "gains trample until end of turn.\n" +
        "Equip {3}"

    staticAbility {
        ability = ModifyStats(3, 3, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.attacks(binding = TriggerBinding.ATTACHED)
        effect = Effects.IfYouDo(
            action = Effects.Pipeline {
                val forests = gather(GameObjectFilter.Land.withSubtype("Forest"), player = Player.You)
                val chosen = chooseUpTo(
                    1,
                    from = forests,
                    useTargetingUI = true,
                    prompt = "Choose a Forest to sacrifice"
                )
                sacrifice(chosen)
            },
            ifYouDo = Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.EquippedCreature)
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/241e95ec-b630-4492-be1d-f66aa19889e5.jpg?1783924800"
    }
}
