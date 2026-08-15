package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Venom Connoisseur
 * {1}{G}
 * Creature — Human Druid
 * 2/2
 *
 * Alliance — Whenever another creature you control enters, this creature gains deathtouch
 * until end of turn. If this is the second time this ability has resolved this turn, all
 * creatures you control gain deathtouch until end of turn.
 *
 * The "second time this ability has resolved" clause uses the same pattern as Harvestrite
 * Host: increment the source's AbilityResolutionCountThisTurnComponent, then gate the
 * team-wide grant on [Conditions.SourceAbilityResolvedNTimes]. The team-wide grant is a
 * [Effects.ForEachInGroup] over AllCreaturesYouControl because GrantKeywordExecutor only
 * handles single targets.
 */
val VenomConnoisseur = card("Venom Connoisseur") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 2
    toughness = 2
    oracleText = "Alliance — Whenever another creature you control enters, this creature gains deathtouch until end of turn. If this is the second time this ability has resolved this turn, all creatures you control gain deathtouch until end of turn."

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
            .then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(2),
                    effect = Effects.ForEachInGroup(
                        GroupFilter.AllCreaturesYouControl,
                        Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
                    )
                )
            )
        description = "Alliance — Whenever another creature you control enters, this creature gains deathtouch until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Marta Nael"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04897d8c-ee05-45eb-80f7-76487dbcc449.jpg?1783923099"
    }
}
