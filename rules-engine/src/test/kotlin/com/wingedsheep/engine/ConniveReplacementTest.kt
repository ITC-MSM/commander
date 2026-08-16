package com.wingedsheep.engine

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.DrawFailedEvent
import com.wingedsheep.engine.core.PermanentConnivedEvent
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyKeywordAction
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.UUID

/**
 * The connive keyword action as a *primitive* (CR 701.50): a named, replaceable, observable action
 * rather than a bare draw/discard pipeline. Card-level coverage of the one printed card that uses
 * it lives in `LeaderSuperGeniusScenarioTest`; this file exercises the mechanism with synthetic
 * cards so the two do not have to move together.
 *
 * Three properties are under test:
 *
 *  1. **Replacement (CR 614).** A [ModifyKeywordAction] over [EventPattern.ConnivedEvent] inserts
 *     its prefix effect *before* the connive, whatever the connive's source is. The prefix here
 *     gains life rather than drawing, so it can never be confused with connive's own draw, and
 *     asserting on life *while the discard decision is still pending* proves the ordering.
 *  2. **Scoping and stacking.** The `appliesTo` filter resolves "you" to the *replacement source's*
 *     controller, and two sources each apply once (CR 614.5 stops one source applying twice).
 *  3. **Observation (CR 701.50f).** A completed connive emits [PermanentConnivedEvent] and fires
 *     `ConnivedEvent` triggers — including when the draw and the discard were both impossible,
 *     which is what "even if some or all of those actions were impossible" buys.
 *
 * Plus the negative that keeps the primitive honest: a connive-*shaped* looting pipeline that the
 * printed card never calls connive (`Effects.ConniveTargeting`, the Teo, Spirited Glider shape) is
 * neither replaced nor observed.
 */
class ConniveReplacementTest : FunSpec({

    val conniveAbilityId = AbilityId(UUID.randomUUID().toString())
    val lootAbilityId = AbilityId(UUID.randomUUID().toString())

    /** "{T}: This creature connives." — the real keyword action. */
    val ConniveCreature = CardDefinition(
        name = "Connive Creature",
        manaCost = ManaCost.parse("{2}{U}"),
        typeLine = TypeLine.creature(setOf(Subtype("Human"))),
        oracleText = "{T}: This creature connives.",
        creatureStats = CreatureStats(2, 2),
        script = CardScript.permanent(
            ActivatedAbility(
                id = conniveAbilityId,
                cost = AbilityCost.Tap,
                effect = Effects.Connive(target = EffectTarget.Self)
            )
        )
    )

    /**
     * The Teo, Spirited Glider shape: the same draw/discard/conditional-counter pipeline, but the
     * printed text never says "connive", so it is not the keyword action.
     */
    val LooterCreature = CardDefinition(
        name = "Looter Creature",
        manaCost = ManaCost.parse("{2}{U}"),
        typeLine = TypeLine.creature(setOf(Subtype("Human"))),
        oracleText = "{T}: Draw a card, then discard a card. When you discard a nonland card this " +
            "way, put a +1/+1 counter on target creature you control.",
        creatureStats = CreatureStats(2, 2),
        script = CardScript.permanent(
            ActivatedAbility(
                id = lootAbilityId,
                cost = AbilityCost.Tap,
                effect = Effects.ConniveTargeting(Targets.CreatureYouControl)
            )
        )
    )

    /** "If a creature you control would connive, instead you gain 3 life, then it connives." */
    val ConnivePrefixSource = card("Connive Prefix Source") {
        manaCost = "{2}{U}"
        typeLine = "Enchantment"
        oracleText = "If a creature you control would connive, instead you gain 3 life, then that " +
            "creature connives."
        replacementEffect(
            ModifyKeywordAction(
                prefixEffect = Effects.GainLife(3),
                appliesTo = EventPattern.ConnivedEvent(
                    filter = GameObjectFilter.Creature.youControl()
                ),
            )
        )
    }

    /** "Whenever a creature you control connives, you gain 7 life." */
    val ConniveWatcher = CardDefinition(
        name = "Connive Watcher",
        manaCost = ManaCost.parse("{1}{U}"),
        typeLine = TypeLine.enchantment(),
        oracleText = "Whenever a creature you control connives, you gain 7 life.",
        script = CardScript.permanent(
            triggeredAbilities = listOf(
                TriggeredAbility(
                    id = AbilityId(UUID.randomUUID().toString()),
                    trigger = Triggers.WheneverCreatureYouControlConnives.event,
                    binding = Triggers.WheneverCreatureYouControlConnives.binding,
                    effect = Effects.GainLife(7)
                )
            )
        )
    )

    val allCards = TestCards.all + listOf(
        ConniveCreature, LooterCreature, ConnivePrefixSource, ConniveWatcher
    )

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Forest" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun opponentOf(driver: GameTestDriver, player: EntityId): EntityId =
        driver.state.turnOrder.first { it != player }

    /**
     * Puts a creature with the given tap ability onto [player]'s battlefield, seeds a nonland on
     * top of their library and a discardable nonland in hand, then activates the ability and lets
     * it resolve up to the discard decision.
     */
    fun activate(
        driver: GameTestDriver,
        player: EntityId,
        creatureName: String,
        abilityId: AbilityId
    ): Pair<EntityId, EntityId> {
        driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val handCard = driver.putCardInHand(player, "Grizzly Bears")
        val creature = driver.putCreatureOnBattlefield(player, creatureName)
        driver.removeSummoningSickness(creature)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = creature, abilityId = abilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        return creature to handCard
    }

    /** Answers the pending discard decision with [card], then drains stack + decisions. */
    fun finish(driver: GameTestDriver, player: EntityId, card: EntityId) {
        val decision = driver.pendingDecision as SelectCardsDecision
        driver.submitDecision(
            player,
            CardsSelectedResponse(decisionId = decision.id, selectedCards = listOf(card))
        )
        var guard = 0
        while (guard++ < 30 && (driver.pendingDecision != null || driver.state.stack.isNotEmpty())) {
            if (driver.pendingDecision != null) driver.autoResolveDecision() else driver.bothPass()
        }
    }

    test("the prefix effect runs before the connive, and applies once per connive") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Connive Prefix Source")

        val (_, handCard) = activate(driver, player, "Connive Creature", conniveAbilityId)

        // The connive paused for its discard choice — and the prefix has ALREADY resolved, which is
        // the whole point of a replacement rather than a trigger: the extra life (here) and the
        // extra card (on the printed card) are in before the discard is chosen.
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        withClue("prefix must resolve before the connive's discard decision") {
            driver.getLifeTotal(player) shouldBe 23
        }

        finish(driver, player, handCard)

        // Applied exactly once: the re-issued connive carries replacementsApplied, so the same
        // source cannot prefix its own replacement (CR 614.5).
        driver.getLifeTotal(player) shouldBe 23
    }

    test("two replacement sources each apply their prefix once") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Connive Prefix Source")
        driver.putPermanentOnBattlefield(player, "Connive Prefix Source")

        val (_, handCard) = activate(driver, player, "Connive Creature", conniveAbilityId)
        finish(driver, player, handCard)

        driver.getLifeTotal(player) shouldBe 26
    }

    test("the filter resolves 'you' to the replacement source's controller, not the conniver's") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        val opponent = opponentOf(driver, player)
        // The opponent owns the "creature YOU control would connive" replacement; the creature that
        // connives belongs to the active player, so it must not fire for either of them.
        driver.putPermanentOnBattlefield(opponent, "Connive Prefix Source")

        val (_, handCard) = activate(driver, player, "Connive Creature", conniveAbilityId)
        finish(driver, player, handCard)

        driver.getLifeTotal(player) shouldBe 20
        driver.getLifeTotal(opponent) shouldBe 20
    }

    test("a completed connive emits the connived event and fires connive triggers") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Connive Watcher")

        val (creature, handCard) = activate(driver, player, "Connive Creature", conniveAbilityId)
        finish(driver, player, handCard)

        val connived = driver.events.filterIsInstance<PermanentConnivedEvent>()
        connived.size shouldBe 1
        connived.first().connivingPermanentId shouldBe creature
        driver.getLifeTotal(player) shouldBe 27
    }

    test("a connive whose draw and discard are both impossible still connives (CR 701.50f)") {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        // A 7-card deck goes entirely into the opening hand, so the library starts empty; binning
        // that hand leaves both zones empty. The draw then has nothing to draw and the discard
        // nothing to discard — the only way both halves of a connive can be impossible, since a
        // successful draw always refills the hand for the discard.
        driver.initMirrorMatch(deck = Deck.of("Plains" to 7), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.getHand(player).forEach { driver.moveToGraveyard(it) }
        driver.state.getLibrary(player).isEmpty() shouldBe true
        driver.getHand(player) shouldBe emptyList()

        val creature = driver.putCreatureOnBattlefield(player, "Connive Creature")
        driver.removeSummoningSickness(creature)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = creature, abilityId = conniveAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        withClue("the draw was impossible — empty library") {
            driver.events.filterIsInstance<DrawFailedEvent>().isEmpty() shouldBe false
        }
        withClue("the discard was impossible too — nothing to choose, so nothing is asked") {
            driver.pendingDecision shouldBe null
        }
        withClue("CR 701.50f: the permanent connives even so") {
            val connived = driver.events.filterIsInstance<PermanentConnivedEvent>()
            connived.size shouldBe 1
            connived.first().connivingPermanentId shouldBe creature
        }
    }

    test("connive-shaped looting that is not connive is neither replaced nor observed") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Connive Prefix Source")
        driver.putPermanentOnBattlefield(player, "Connive Watcher")

        val (_, handCard) = activate(driver, player, "Looter Creature", lootAbilityId)
        finish(driver, player, handCard)

        withClue("Effects.ConniveTargeting is the Teo looting shape, not the keyword action") {
            driver.events.filterIsInstance<PermanentConnivedEvent>() shouldBe emptyList()
            driver.getLifeTotal(player) shouldBe 20
        }
    }
})
