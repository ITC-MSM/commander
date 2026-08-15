package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Fiendish Panda
 * {2}{W}{B}
 * Creature — Bear Demon
 * 3/2
 *
 * Whenever you gain life, put a +1/+1 counter on this creature.
 * When this creature dies, return another target non-Bear creature card with mana value less than
 * or equal to this creature's power from your graveyard to the battlefield.
 *
 * The life-gain trigger fires per life-gain event ([Triggers.YouGainLife]) and adds a +1/+1
 * counter. The dies trigger reanimates a graveyard creature: the target is a creature card in
 * **your** graveyard, not a Bear ([notSubtype]), whose mana value is at most Fiendish Panda's
 * power — a dynamic cap read via [DynamicAmount.EntityProperty] on the source's power, which falls
 * back to the last-known power captured at death (Fiendish Panda is already in the graveyard when
 * the trigger resolves, CR 603.10). `.other()` enforces "another" so the Panda can't reanimate
 * itself even once its power has climbed to meet its own mana value.
 */
val FiendishPanda = card("Fiendish Panda") {
    manaCost = "{2}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Bear Demon"
    power = 3
    toughness = 2
    oracleText = "Whenever you gain life, put a +1/+1 counter on this creature.\n" +
        "When this creature dies, return another target non-Bear creature card with mana value " +
        "less than or equal to this creature's power from your graveyard to the battlefield."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you gain life, put a +1/+1 counter on this creature."
    }

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target(
            "another target non-Bear creature card with mana value less than or equal to " +
                "this creature's power from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature.ownedByYou()
                        .notSubtype(Subtype.BEAR)
                        .manaValueAtMostDynamic(
                            DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power)
                        ),
                    zone = Zone.GRAVEYARD,
                ).other()
            )
        )
        effect = Effects.PutOntoBattlefield(t)
        description = "When this creature dies, return another target non-Bear creature card with " +
            "mana value less than or equal to this creature's power from your graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e434d74-cad0-45f5-bc8d-f34aa5e1d879.jpg?1782689163"
    }
}
