package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Shang-Chi, Master of Kung Fu
 * {1}{G}
 * Legendary Creature — Human Warrior Hero
 * 2/2
 *
 * You may activate abilities of creatures you control as though those creatures had haste.
 * {T}: Add two mana of any one color. Spend this mana only to activate abilities of creature sources.
 *
 * The static is the Thousand-Year Elixir permission, and it is deliberately *not* a haste grant:
 * CR 302.6 gates a creature's `{T}`/`{Q}` abilities and its ability to attack on the same condition,
 * and this lifts only the first half. It is expressed as
 * [GrantKeyword]([AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY], creatures you control), so the
 * layer system carries it like any other granted keyword; only
 * `SummoningSicknessRules` — the shared `{T}`/`{Q}` gate — reads it, while combat keeps its own plain
 * haste check. The filter includes Shang-Chi itself ("creatures you control"), so its own mana ability
 * is live the turn it enters.
 *
 * The mana ability is `Effects.AddAnyColorMana(2, …)` — two mana of one chosen color — carrying
 * [ManaRestriction.CardTypeSpellsOrAbilitiesOnly](CREATURE) with `allowSpells = false`: the oracle
 * says "activate abilities of creature sources" and nothing about casting.
 */
val ShangChiMasterOfKungFu = card("Shang-Chi, Master of Kung Fu") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Warrior Hero"
    power = 2
    toughness = 2
    oracleText = "You may activate abilities of creatures you control as though those creatures " +
        "had haste.\n" +
        "{T}: Add two mana of any one color. Spend this mana only to activate abilities of " +
        "creature sources."

    staticAbility {
        ability = GrantKeyword(
            AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY.name,
            GroupFilter.AllCreaturesYouControl,
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            amount = 2,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.CREATURE,
                allowSpells = false,
                allowAbilities = true,
            )
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "187"
        artist = "Lee Woo-chul"
        flavorText = "\"Many underestimate me because I carry no weapon. But I am the weapon.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2edbb62f-ad45-4422-850b-68dcc18b4c73.jpg?1783902911"
    }
}
