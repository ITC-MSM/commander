package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/** CR 603.3b: a controller, not detector iteration order, chooses their simultaneous triggers. */
class TriggeredAbilityOrderingTest : FunSpec({
    val doubleEtb = card("Double ETB") {
        manaCost = "{W}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(1)
        }
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(2)
        }
    }

    // This deliberately uses no attack event. The Artifact filter prevents the meta-trigger from
    // seeing itself, so the test proves generic ability-on-stack cascading rather than Firebender's
    // former combat-only workaround.
    val artifactWatcher = card("Artifact Trigger Watcher") {
        manaCost = "{2}"
        typeLine = "Artifact"
        triggeredAbility {
            trigger = Triggers.OtherCreatureEnters
            effect = Effects.GainLife(1)
            description = "When another creature enters, gain 1 life."
        }
    }
    val genericMetaWatcher = card("Generic Meta Watcher") {
        manaCost = "{2}"
        typeLine = "Enchantment"
        triggeredAbility {
            trigger = TriggerSpec(
                event = EventPattern.AbilityTriggeredEvent(
                    player = Player.You,
                    sourceFilter = GameObjectFilter.Artifact,
                ),
                binding = TriggerBinding.ANY,
            )
            effect = Effects.GainLife(2)
            description = "Whenever your artifact's triggered ability is put on the stack, gain 2 life."
        }
    }
    val baitCreature = card("Cascade Bait") {
        manaCost = "{W}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
    }
    val targetedArtifactWatcher = card("Targeted Artifact Trigger Watcher") {
        manaCost = "{2}"
        typeLine = "Artifact"
        triggeredAbility {
            trigger = Triggers.OtherCreatureEnters
            val target = target("target player", Targets.Player)
            effect = Effects.DealDamage(1, target)
            description = "When another creature enters, deal 1 damage to target player."
        }
    }

    test("controller explicitly chooses order for simultaneous triggers and chosen order becomes stack order") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + doubleEtb)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val player = driver.activePlayer!!
        val card = driver.putCardInHand(player, "Double ETB")
        driver.giveMana(player, Color.WHITE)

        driver.castSpell(player, card).isSuccess shouldBe true
        // Placement correctly pauses for the CR 603.3b order choice.
        driver.bothPass().isPaused shouldBe true

        val decision = driver.pendingDecision as? OrderTriggeredAbilitiesDecision
            ?: error("Expected CR 603.3b ordering decision, got ${driver.pendingDecision}")
        decision.playerId shouldBe player
        decision.abilities.map { it.description }.shouldContainExactly(
            Effects.GainLife(1).description,
            Effects.GainLife(2).description,
        )

        val reverseOrder = decision.abilities.reversed().map { it.id }
        driver.submitDecision(player, TriggeredAbilitiesOrderedResponse(decision.id, reverseOrder)).isSuccess shouldBe true

        val stackedEffects = driver.state.stack.map {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()?.effect
                ?: error("Expected triggered ability on stack")
        }
        stackedEffects.shouldContainExactly(Effects.GainLife(2), Effects.GainLife(1))
    }

    test("generic non-attack meta trigger forms a later placement wave above its cause") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(artifactWatcher, genericMetaWatcher, baitCreature))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Artifact Trigger Watcher")
        driver.putPermanentOnBattlefield(player, "Generic Meta Watcher")
        val bait = driver.putCardInHand(player, "Cascade Bait")
        driver.giveMana(player, Color.WHITE)

        driver.castSpell(player, bait).isSuccess shouldBe true
        driver.bothPass().isSuccess shouldBe true

        val stackedEffects = driver.state.stack.map {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()?.effect
                ?: error("Expected triggered ability on stack")
        }
        // Original wave first, then its generic reaction: the latter is on top and resolves first.
        stackedEffects.shouldContainExactly(Effects.GainLife(1), Effects.GainLife(2))
    }

    test("generic later wave is preserved when its causing trigger pauses for targets") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(targetedArtifactWatcher, genericMetaWatcher, baitCreature))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.putPermanentOnBattlefield(player, "Targeted Artifact Trigger Watcher")
        driver.putPermanentOnBattlefield(player, "Generic Meta Watcher")
        val bait = driver.putCardInHand(player, "Cascade Bait")
        driver.giveMana(player, Color.WHITE)

        driver.castSpell(player, bait).isSuccess shouldBe true
        driver.bothPass().isPaused shouldBe true
        (driver.pendingDecision is ChooseTargetsDecision) shouldBe true

        driver.submitTargetSelection(player, listOf(opponent)).isSuccess shouldBe true
        val stackedEffects = driver.state.stack.map {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()?.effect
                ?: error("Expected triggered ability on stack")
        }
        // The resumed original trigger is below the generic next-wave reaction.
        stackedEffects.size shouldBe 2
        stackedEffects.last() shouldBe Effects.GainLife(2)
    }

    test("ordering response does not duplicate its target-triggering placement event") {
        val valiantWatcher = card("Placement Valiant Watcher") {
            manaCost = "{2}"
            typeLine = "Creature — Human"
            power = 1
            toughness = 1
            triggeredAbility {
                trigger = Triggers.BecomesTarget
                effect = Effects.GainLife(1)
                description = "Whenever this becomes targeted, gain 1 life."
            }
        }
        val targetedOne = card("Ordered Target One") {
            manaCost = "{W}"
            typeLine = "Creature — Human"
            power = 1
            toughness = 1
            triggeredAbility {
                trigger = Triggers.OtherCreatureEnters
                val target = target("target creature", Targets.Creature)
                effect = Effects.AddCounters("+1/+1", 1, target)
            }
        }
        val targetedTwo = card("Ordered Target Two") {
            manaCost = "{W}"
            typeLine = "Creature — Human"
            power = 1
            toughness = 1
            triggeredAbility {
                trigger = Triggers.OtherCreatureEnters
                val target = target("target creature", Targets.Creature)
                effect = Effects.AddCounters("+1/+1", 1, target)
            }
        }
        val bait = card("Target Ordering Bait") {
            manaCost = "{W}"
            typeLine = "Creature — Human"
            power = 1
            toughness = 1
        }

        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(valiantWatcher, targetedOne, targetedTwo, bait))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val player = driver.activePlayer!!
        val watcher = driver.putPermanentOnBattlefield(player, "Placement Valiant Watcher")
        driver.putPermanentOnBattlefield(player, "Ordered Target One")
        driver.putPermanentOnBattlefield(player, "Ordered Target Two")
        val baitCard = driver.putCardInHand(player, "Target Ordering Bait")
        driver.giveMana(player, Color.WHITE)

        driver.castSpell(player, baitCard).isSuccess shouldBe true
        driver.bothPass().isPaused shouldBe true
        val order = driver.pendingDecision as? OrderTriggeredAbilitiesDecision
            ?: error("Expected ordering decision")
        driver.submitDecision(
            player,
            TriggeredAbilitiesOrderedResponse(order.id, order.abilities.map { it.id })
        ).isPaused shouldBe true
        val targets = driver.pendingDecision as? ChooseTargetsDecision
            ?: error("Expected first trigger target selection")
        driver.submitTargetSelection(player, listOf(watcher)).isPaused shouldBe true
        val secondTargets = driver.pendingDecision as? ChooseTargetsDecision
            ?: error("Expected second trigger target selection")
        driver.submitTargetSelection(player, listOf(watcher)).isSuccess shouldBe true

        // Both target events form one later placement wave.  If resumeOrder loses
        // TriggerProcessor ownership, SubmitDecisionHandler scans them again and this
        // produces four Valiant triggers instead of two.
        val valiantTriggers = driver.state.stack.mapNotNull { stackId ->
            driver.state.getEntity(stackId)?.get<TriggeredAbilityOnStackComponent>()
                ?.takeIf { it.sourceId == watcher }
        }
        valiantTriggers.size shouldBe 2
    }
})
