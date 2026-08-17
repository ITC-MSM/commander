package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Insidious Roots — Murders at Karlov Manor #208
 * {B}{G} · Enchantment · Uncommon
 *
 * Creature tokens you control have "{T}: Add one mana of any color."
 * Whenever one or more creature cards leave your graveyard, create a 0/1 green Plant creature
 * token, then put a +1/+1 counter on each Plant you control.
 *
 * Two mana for an engine that turns graveyard recursion into a widening board *and* a mana base:
 * every Plant it makes is a Cryptolith Rite land, and every subsequent trigger grows all of them at
 * once. Note the counters go on **Plants**, not on all creature tokens — the Plant tokens it made
 * itself are the ones that compound.
 *
 * The first ability is [CryptolithRite]'s [GrantActivatedAbility] narrowed to `token()`; the
 * granted ability is `isManaAbility = true` with `TimingRule.ManaAbility` so the raw
 * [ActivatedAbility] constructor carries the classification the card-DSL builder's `manaAbility`
 * flag would set. The printed ruling about Insidious Roots granting *itself* the ability when it is
 * somehow a creature token falls out of the filter — it says "creature tokens you control", not
 * "other", so nothing excludes the source.
 *
 * The trigger is [Triggers.CardsLeaveYourGraveyard], the CR 603.2c batch shape [ChalkOutline] uses:
 * a mass reanimation, a flashback cast and a graveyard-exiling sweep each fire it exactly once no
 * matter how many creature cards moved or where they went — which is what the second printed ruling
 * says in as many words. The filter matches the *card*, so a creature card cast from the graveyard
 * counts on its way to the stack.
 *
 * The payoff is ordered, not simultaneous: the token is created first and then the counters land, so
 * the new Plant gets a counter too and enters play as a 1/2. [Effects.ForEachInGroup] snapshots the
 * group before iterating, and the snapshot is taken after the token exists.
 *
 * The Plant token takes its art from MKM's `tokenArt` layer, so no `imageUri` is baked in here.
 */
val InsidiousRoots = card("Insidious Roots") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment"
    oracleText = "Creature tokens you control have \"{T}: Add one mana of any color.\"\n" +
        "Whenever one or more creature cards leave your graveyard, create a 0/1 green Plant " +
        "creature token, then put a +1/+1 counter on each Plant you control."

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddManaOfChoice(),
                isManaAbility = true,
                timing = TimingRule.ManaAbility,
            ),
            filter = GroupFilter(GameObjectFilter.Creature.token().youControl()),
        )
    }

    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard(GameObjectFilter.Creature)
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 0,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Plant"),
            ),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.withSubtype("Plant").youControl()),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            ),
        )
        description = "Whenever one or more creature cards leave your graveyard, create a 0/1 " +
            "green Plant creature token, then put a +1/+1 counter on each Plant you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Jeremy Wilson"
        flavorText = "The roots of Vitu-Ghazi allowed Trostani to reach every crack and crevice " +
            "in the city."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0bb91a22-2040-4a37-85f8-5f22de8c5907.jpg?1783912846"

        ruling(
            "2024-02-02",
            "In the rare case where Insidious Roots is a creature token, it will grant itself the " +
                "ability \"{T}: Add one mana of any color.\""
        )
        ruling(
            "2024-02-02",
            "If multiple creature cards leave your graveyard at the same time, Insidious Roots's " +
                "last ability will trigger only once."
        )
    }
}
